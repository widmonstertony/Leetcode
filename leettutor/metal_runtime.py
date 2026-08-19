"""Manage the experimental Intel Mac AMD Metal llama.cpp runtime."""

from __future__ import annotations

import json
import os
import platform
import shutil
import signal
import subprocess
import sys
import time
from dataclasses import dataclass
from pathlib import Path
from typing import IO, Iterator, Mapping, Sequence
from urllib.error import HTTPError, URLError
from urllib.parse import urlsplit
from urllib.request import Request, urlopen


AMD_METAL_PROVIDER = "AMD Metal（Intel Mac）"
AMD_METAL_MODEL = "qwen3.5:9b"
AMD_METAL_ENDPOINT = "http://127.0.0.1:11435/v1"
AMD_METAL_API_KEY = "leettutor-local"
PROJECT_ROOT = Path(__file__).resolve().parents[1]
LLAMA_CPP_REPOSITORY = "https://github.com/ggml-org/llama.cpp.git"
LLAMA_CPP_TAG = "b10240"
LLAMA_CPP_COMMIT = "0b14b87d7c20cb753b94b96854dd7b45306fc696"
METAL_PATCH_NAME = "llama.cpp-b10240-qwen35-ollama.patch"


class MetalRuntimeError(RuntimeError):
    """Raised when the local experimental Metal runtime cannot start."""


@dataclass(frozen=True)
class MetalLaunchPlan:
    """A conservative GPU/CPU split for one GGUF on an Intel Radeon Mac."""

    gpu_layers: int
    context_tokens: int
    partial_offload: bool
    model_gb: float


def _model_size_gb(path: Path) -> float:
    try:
        return path.stat().st_size / (1024**3)
    except OSError:
        return 0.0


def metal_launch_plan(model_path: Path, *, vram_gb: float = 8.0) -> MetalLaunchPlan:
    """Keep models larger than VRAM from dying during an all-GPU allocation.

    The original experimental runner was deliberately tuned for the 6.6 GB 9B
    model and used ``-ngl 999``. A 17 GB 27B GGUF cannot use that plan on an
    8 GB Radeon.  Keep enough VRAM for Metal and KV cache, offload a bounded
    number of transformer layers, and leave the remaining weights in host RAM.
    """

    model_gb = _model_size_gb(model_path)
    # A full model also needs Metal scratch buffers and a KV cache.  72% leaves
    # enough headroom on the 5600M for the 4096-token interactive 9B setting.
    if not model_gb or model_gb <= vram_gb * 0.72:
        return MetalLaunchPlan(999, 4096, False, model_gb)

    # Qwen 27B Q4 is about 17 GB.  16 layers uses roughly 4–5 GB of VRAM,
    # leaving safe room for Metal allocations on an 8 GB card.  Larger cards
    # scale the cap mildly, but this remains a quality/slow-review mode.
    gpu_layers = max(8, min(40, int(vram_gb * 2)))
    return MetalLaunchPlan(gpu_layers, 2048, True, model_gb)


def _total_memory_gb() -> float:
    try:
        return os.sysconf("SC_PAGE_SIZE") * os.sysconf("SC_PHYS_PAGES") / (1024**3)
    except (AttributeError, OSError, ValueError):
        raw = _command_output(["sysctl", "-n", "hw.memsize"])
        return int(raw) / (1024**3) if raw.isdigit() else 0.0


@dataclass(frozen=True)
class MetalInstallUpdate:
    """One visible step emitted by the reproducible runtime installer."""

    progress: float
    phase: str
    detail: str = ""


@dataclass(frozen=True)
class MetalSetupStatus:
    """Read-only readiness report used by the in-app repair guide."""

    system: str
    machine: str
    gpu_name: str
    vram_gb: float | None
    model_identifier: str
    xcode_tools: bool
    git_path: Path | None
    cmake_path: Path | None
    patch_path: Path
    server_path: Path | None
    model_path: Path | None
    model: str
    endpoint_running: bool
    log_path: Path

    @property
    def intel_macos(self) -> bool:
        return is_intel_macos(system=self.system, machine=self.machine)

    @property
    def discrete_amd(self) -> bool:
        folded = self.gpu_name.casefold()
        return "amd" in folded or "radeon" in folded

    @property
    def verified_5600m(self) -> bool:
        return "radeon pro 5600m" in self.gpu_name.casefold() and (
            self.vram_gb or 0
        ) >= 7.5

    @property
    def hardware_compatible(self) -> bool:
        return self.intel_macos and self.discrete_amd and (self.vram_gb or 0) >= 7.5

    @property
    def build_ready(self) -> bool:
        return all(
            (
                self.hardware_compatible,
                self.xcode_tools,
                self.git_path,
                self.cmake_path,
                self.patch_path.is_file(),
            )
        )

    @property
    def runtime_ready(self) -> bool:
        return self.server_path is not None and self.model_path is not None

    def report_lines(self) -> list[str]:
        """Return a copyable report without usernames or home-directory paths."""

        gpu = self.gpu_name or "not detected"
        if self.vram_gb:
            gpu += f" ({self.vram_gb:g} GB VRAM)"
        return [
            f"macOS Intel: {'yes' if self.intel_macos else 'no'}",
            f"Mac model: {self.model_identifier or 'unknown'}",
            f"GPU: {gpu}",
            f"Xcode Command Line Tools: {'ready' if self.xcode_tools else 'missing'}",
            f"Git: {'ready' if self.git_path else 'missing'}",
            f"CMake: {'ready' if self.cmake_path else 'missing'}",
            f"Patched llama-server: {'ready' if self.server_path else 'missing'}",
            f"{self.model}: {'ready' if self.model_path else 'missing'}",
            f"Local endpoint: {'running' if self.endpoint_running else 'stopped'}",
        ]


@dataclass
class MetalRuntimeHandle:
    """A running endpoint, optionally owned by the current launcher."""

    endpoint: str
    process: subprocess.Popen[bytes] | None = None
    log_file: IO[bytes] | None = None
    log_path: Path | None = None

    @property
    def managed(self) -> bool:
        return self.process is not None

    def stop(self) -> None:
        if self.process is not None and self.process.poll() is None:
            self.process.terminate()
            try:
                self.process.wait(timeout=8)
            except subprocess.TimeoutExpired:
                self.process.kill()
                self.process.wait(timeout=3)
        if self.log_file is not None:
            self.log_file.close()


def is_intel_macos(
    *, system: str | None = None, machine: str | None = None
) -> bool:
    current_system = system or platform.system()
    current_machine = (machine or platform.machine()).casefold()
    return current_system == "Darwin" and current_machine in {"x86_64", "amd64"}


def _find_executable(name: str) -> Path | None:
    configured = shutil.which(name)
    if configured:
        return Path(configured).resolve()
    sibling = Path(sys.executable).resolve().with_name(name)
    return sibling if sibling.is_file() and os.access(sibling, os.X_OK) else None


def _command_output(command: Sequence[str], *, timeout: float = 5.0) -> str:
    try:
        result = subprocess.run(
            list(command),
            capture_output=True,
            text=True,
            timeout=timeout,
            check=False,
        )
    except (OSError, subprocess.TimeoutExpired):
        return ""
    return result.stdout.strip()


def inspect_metal_setup(
    *,
    project_root: Path = PROJECT_ROOT,
    endpoint: str = AMD_METAL_ENDPOINT,
    model: str = AMD_METAL_MODEL,
    gpu_name: str = "",
    vram_gb: float | None = None,
    system: str | None = None,
    machine: str | None = None,
    environment: Mapping[str, str] | None = None,
) -> MetalSetupStatus:
    """Inspect every prerequisite without changing the user's machine."""

    current_system = system or platform.system()
    current_machine = machine or platform.machine()
    xcode_path = _command_output(["/usr/bin/xcode-select", "-p"])
    model_identifier = _command_output(["/usr/sbin/sysctl", "-n", "hw.model"])
    return MetalSetupStatus(
        system=current_system,
        machine=current_machine,
        gpu_name=gpu_name,
        vram_gb=vram_gb,
        model_identifier=model_identifier,
        xcode_tools=bool(xcode_path),
        git_path=_find_executable("git"),
        cmake_path=_find_executable("cmake"),
        patch_path=project_root / "patches" / METAL_PATCH_NAME,
        server_path=find_llama_server(
            project_root=project_root, environment=environment
        ),
        model_path=resolve_ollama_model(model, environment=environment),
        model=model,
        endpoint_running=endpoint_ready(endpoint),
        log_path=project_root / ".leettutor" / "amd-metal-server.log",
    )


def metal_build_commands(source: Path, cmake: Path) -> tuple[list[str], list[str]]:
    """Return the pinned configure/build commands shown in the guide and tests."""

    build = source / "build-metal"
    configure = [
        str(cmake),
        "-S",
        str(source),
        "-B",
        str(build),
        "-DCMAKE_BUILD_TYPE=Release",
        "-DGGML_METAL=ON",
        "-DGGML_ACCELERATE=ON",
        "-DLLAMA_BUILD_SERVER=ON",
    ]
    jobs = str(max(2, min(os.cpu_count() or 2, 8)))
    compile_server = [
        str(cmake),
        "--build",
        str(build),
        "--config",
        "Release",
        "--target",
        "llama-server",
        "-j",
        jobs,
    ]
    return configure, compile_server


def install_metal_runtime(
    *,
    project_root: Path = PROJECT_ROOT,
    gpu_name: str = "",
    vram_gb: float | None = None,
) -> Iterator[MetalInstallUpdate]:
    """Clone, patch, and compile the tested llama.cpp revision inside the app."""

    status = inspect_metal_setup(
        project_root=project_root, gpu_name=gpu_name, vram_gb=vram_gb
    )
    if not status.hardware_compatible:
        raise MetalRuntimeError(
            "自动安装仅对 Intel macOS + 8 GB 独立 Radeon 开放；"
            "Radeon Pro 5600M 8 GB 是已验证配置。"
        )
    if not status.xcode_tools:
        raise MetalRuntimeError("缺少 Apple Command Line Tools；请先在 App 内打开安装器。")
    if status.git_path is None:
        raise MetalRuntimeError("缺少 Git；安装 Apple Command Line Tools 后再试。")
    if status.cmake_path is None:
        raise MetalRuntimeError(
            "缺少 CMake；请重新运行 run.command，让 LeetTutor 安装完整依赖。"
        )
    if not status.patch_path.is_file():
        raise MetalRuntimeError(f"缺少兼容补丁：{status.patch_path.name}")

    source = project_root / ".leettutor" / "llama.cpp-metal"
    if (source / ".git").is_dir():
        existing_revision = _command_output(
            [str(status.git_path), "-C", str(source), "rev-parse", "HEAD"]
        )
        if existing_revision != LLAMA_CPP_COMMIT:
            backup = source.with_name(f"{source.name}.backup-{int(time.time())}")
            source.rename(backup)
            yield MetalInstallUpdate(
                0.05, "保留其他版本的源码", backup.name
            )
    if not (source / ".git").is_dir():
        if source.exists():
            backup = source.with_name(f"{source.name}.backup-{int(time.time())}")
            source.rename(backup)
            yield MetalInstallUpdate(0.05, "保留旧目录", backup.name)
        source.parent.mkdir(parents=True, exist_ok=True)
        yield MetalInstallUpdate(0.08, "下载固定版本", f"llama.cpp {LLAMA_CPP_TAG}")
        yield from _stream_install_command(
            [
                str(status.git_path),
                "clone",
                "--filter=blob:none",
                "--depth",
                "1",
                "--branch",
                LLAMA_CPP_TAG,
                "--single-branch",
                LLAMA_CPP_REPOSITORY,
                str(source),
            ],
            cwd=project_root,
            progress=0.2,
            phase="下载 llama.cpp",
        )

    revision = _command_output(
        [str(status.git_path), "-C", str(source), "rev-parse", "HEAD"]
    )
    if revision != LLAMA_CPP_COMMIT:
        raise MetalRuntimeError(
            f"运行时源码不是已验证提交 {LLAMA_CPP_COMMIT[:8]}。"
            "请在 App 中再次点击安装；现有目录会先保留为 backup。"
        )

    forward_check = subprocess.run(
        [
            str(status.git_path),
            "-C",
            str(source),
            "apply",
            "--check",
            str(status.patch_path),
        ],
        capture_output=True,
        text=True,
        check=False,
    )
    if forward_check.returncode == 0:
        yield MetalInstallUpdate(0.32, "应用兼容补丁", status.patch_path.name)
        _run_checked(
            [
                str(status.git_path),
                "-C",
                str(source),
                "apply",
                str(status.patch_path),
            ],
            error_prefix="补丁应用失败",
        )
    else:
        reverse_check = subprocess.run(
            [
                str(status.git_path),
                "-C",
                str(source),
                "apply",
                "--reverse",
                "--check",
                str(status.patch_path),
            ],
            capture_output=True,
            text=True,
            check=False,
        )
        if reverse_check.returncode != 0:
            detail = forward_check.stderr.strip().splitlines()[-1:]
            raise MetalRuntimeError(
                "兼容补丁与本地源码不匹配。" + (f" {detail[0]}" if detail else "")
            )
        yield MetalInstallUpdate(0.32, "兼容补丁已存在")

    configure, compile_server = metal_build_commands(source, status.cmake_path)
    yield MetalInstallUpdate(0.4, "配置 Metal Release 构建")
    yield from _stream_install_command(
        configure,
        cwd=project_root,
        progress=0.52,
        phase="配置构建",
    )
    yield MetalInstallUpdate(0.58, "编译 llama-server", "首次通常需要数分钟")
    yield from _stream_install_command(
        compile_server,
        cwd=project_root,
        progress=0.9,
        phase="编译 Metal 后端",
    )

    server = source / "build-metal" / "bin" / "llama-server"
    if not server.is_file() or not os.access(server, os.X_OK):
        raise MetalRuntimeError("编译结束，但没有生成可执行的 llama-server。")
    version = _command_output([str(server), "--version"], timeout=10).splitlines()
    detail = version[0] if version else server.name
    yield MetalInstallUpdate(1.0, "AMD Metal 后端安装完成", detail)


def open_xcode_tools_installer() -> None:
    """Open Apple's signed Command Line Tools installer dialog."""

    try:
        result = subprocess.run(
            ["/usr/bin/xcode-select", "--install"],
            capture_output=True,
            text=True,
            timeout=15,
            check=False,
        )
    except (OSError, subprocess.TimeoutExpired) as exc:
        raise MetalRuntimeError(f"无法打开 Apple 安装器：{exc}") from exc
    combined = " ".join((result.stdout, result.stderr)).casefold()
    if result.returncode != 0 and "already installed" not in combined:
        raise MetalRuntimeError(
            (result.stderr or result.stdout or "Apple 安装器没有启动。").strip()
        )


def _run_checked(command: Sequence[str], *, error_prefix: str) -> None:
    try:
        result = subprocess.run(
            list(command), capture_output=True, text=True, timeout=120, check=False
        )
    except (OSError, subprocess.TimeoutExpired) as exc:
        raise MetalRuntimeError(f"{error_prefix}：{exc}") from exc
    if result.returncode != 0:
        detail = (result.stderr or result.stdout).strip().splitlines()[-1:]
        raise MetalRuntimeError(
            error_prefix + (f"：{detail[0]}" if detail else "。")
        )


def _stream_install_command(
    command: Sequence[str],
    *,
    cwd: Path,
    progress: float,
    phase: str,
) -> Iterator[MetalInstallUpdate]:
    try:
        process = subprocess.Popen(
            list(command),
            cwd=cwd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            bufsize=1,
        )
    except OSError as exc:
        raise MetalRuntimeError(f"{phase}无法启动：{exc}") from exc
    recent: list[str] = []
    assert process.stdout is not None
    for raw_line in process.stdout:
        line = raw_line.strip()
        if line:
            recent.append(line)
            recent = recent[-8:]
            yield MetalInstallUpdate(progress, phase, line[-240:])
    return_code = process.wait()
    if return_code != 0:
        detail = recent[-1] if recent else f"exit {return_code}"
        raise MetalRuntimeError(f"{phase}失败：{detail}")


def find_llama_server(
    *, project_root: Path = PROJECT_ROOT, environment: Mapping[str, str] | None = None
) -> Path | None:
    env = os.environ if environment is None else environment
    candidates: list[Path] = []
    configured = env.get("LEETTUTOR_METAL_SERVER", "").strip()
    if configured:
        candidates.append(Path(configured).expanduser())
    candidates.extend(
        [
            project_root.parent
            / "llama.cpp-metal"
            / "build-metal"
            / "bin"
            / "llama-server",
            project_root
            / ".leettutor"
            / "llama.cpp-metal"
            / "build-metal"
            / "bin"
            / "llama-server",
        ]
    )
    return next(
        (path.resolve() for path in candidates if path.is_file() and os.access(path, os.X_OK)),
        None,
    )


def _ollama_models_root(
    *, models_root: Path | None = None, environment: Mapping[str, str] | None = None
) -> Path:
    env = os.environ if environment is None else environment
    return models_root or Path(
        env.get("OLLAMA_MODELS", Path.home() / ".ollama" / "models")
    )


def list_ollama_models(
    *,
    models_root: Path | None = None,
    environment: Mapping[str, str] | None = None,
) -> list[str]:
    """List GGUF-backed Ollama models even when the Ollama API is stopped."""

    root = _ollama_models_root(models_root=models_root, environment=environment)
    manifests = root / "manifests" / "registry.ollama.ai" / "library"
    try:
        entries = sorted(path for path in manifests.rglob("*") if path.is_file())
    except OSError:
        return []
    models: list[str] = []
    for manifest in entries:
        try:
            relative = manifest.relative_to(manifests)
        except ValueError:
            continue
        if len(relative.parts) < 2:
            continue
        name = "/".join(relative.parts[:-1])
        tag = relative.parts[-1]
        if name and tag:
            models.append(f"{name}:{tag}")
    return sorted(set(models), key=str.casefold)


def selected_metal_model(
    *,
    project_root: Path = PROJECT_ROOT,
    environment: Mapping[str, str] | None = None,
) -> str:
    """Read the selected AMD model without importing Streamlit configuration."""

    env = os.environ if environment is None else environment
    explicit = env.get("LEETTUTOR_METAL_MODEL", "").strip()
    if explicit:
        return explicit
    try:
        raw = json.loads((project_root / "config.json").read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return AMD_METAL_MODEL
    models = raw.get("models", {})
    if isinstance(models, Mapping):
        value = models.get(AMD_METAL_PROVIDER)
        if isinstance(value, str) and value.strip():
            return value.strip()
    return AMD_METAL_MODEL


def resolve_ollama_model(
    model: str = AMD_METAL_MODEL,
    *,
    models_root: Path | None = None,
    environment: Mapping[str, str] | None = None,
) -> Path | None:
    env = os.environ if environment is None else environment
    configured = env.get("LEETTUTOR_METAL_MODEL_PATH", "").strip()
    if configured:
        candidate = Path(configured).expanduser()
        return candidate.resolve() if candidate.is_file() else None

    root = _ollama_models_root(models_root=models_root, environment=env)
    name, separator, tag = model.rpartition(":")
    if not separator:
        name, tag = model, "latest"
    if not name or "/" in name or not tag:
        return None

    manifest_path = root / "manifests" / "registry.ollama.ai" / "library" / name / tag
    try:
        manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return None

    for layer in manifest.get("layers", []):
        if layer.get("mediaType") != "application/vnd.ollama.image.model":
            continue
        digest = str(layer.get("digest", ""))
        if not digest.startswith("sha256:"):
            continue
        blob = root / "blobs" / digest.replace(":", "-", 1)
        if blob.is_file():
            return blob.resolve()
    return None


def stop_metal_runtime(endpoint: str = AMD_METAL_ENDPOINT) -> None:
    """Stop the loopback llama-server currently listening on the AMD endpoint."""

    parsed = urlsplit(endpoint)
    if parsed.scheme != "http" or parsed.hostname not in {"localhost", "127.0.0.1"}:
        raise MetalRuntimeError("只能重启本机 AMD Metal Endpoint。")
    port = parsed.port or 11435
    try:
        result = subprocess.run(
            ["lsof", "-nP", f"-iTCP:{port}", "-sTCP:LISTEN", "-t"],
            capture_output=True,
            text=True,
            timeout=3,
            check=False,
        )
    except (OSError, subprocess.TimeoutExpired) as exc:
        raise MetalRuntimeError(f"无法定位 AMD Metal 服务：{exc}") from exc
    pids = {int(value) for value in result.stdout.split() if value.isdigit()}
    for pid in pids:
        try:
            os.kill(pid, signal.SIGTERM)
        except ProcessLookupError:
            continue
        except PermissionError as exc:
            raise MetalRuntimeError("没有权限停止当前 AMD Metal 服务。") from exc
    deadline = time.monotonic() + 5
    while pids and time.monotonic() < deadline:
        alive = set()
        for pid in pids:
            try:
                os.kill(pid, 0)
            except ProcessLookupError:
                continue
            alive.add(pid)
        pids = alive
        if pids:
            time.sleep(0.1)


def endpoint_ready(endpoint: str = AMD_METAL_ENDPOINT, *, timeout: float = 1.0) -> bool:
    request = Request(
        endpoint.rstrip("/") + "/models",
        headers={"Authorization": f"Bearer {AMD_METAL_API_KEY}"},
    )
    try:
        with urlopen(request, timeout=timeout) as response:
            return 200 <= response.status < 300
    except (HTTPError, URLError, OSError, ValueError):
        return False


def build_server_command(
    server: Path,
    model_path: Path,
    *,
    endpoint: str = AMD_METAL_ENDPOINT,
    model: str = AMD_METAL_MODEL,
    vram_gb: float = 8.0,
) -> list[str]:
    parsed = urlsplit(endpoint)
    if parsed.scheme != "http" or parsed.hostname not in {"localhost", "127.0.0.1"}:
        raise MetalRuntimeError("AMD Metal Endpoint 必须是本机 http 地址。")
    port = parsed.port or 11435
    plan = metal_launch_plan(model_path, vram_gb=vram_gb)
    return [
        str(server),
        "-m",
        str(model_path),
        "--alias",
        model,
        "--host",
        "127.0.0.1",
        "--port",
        str(port),
        "--api-key",
        AMD_METAL_API_KEY,
        "-dev",
        "MTL0",
        "-ngl",
        str(plan.gpu_layers),
        "-fit",
        "on" if plan.partial_offload else "off",
        "-lm",
        "none",
        "-c",
        str(plan.context_tokens),
        "-b",
        "64",
        "-ub",
        "16",
        "-fa",
        "off",
        "--parallel",
        "1",
        "--jinja",
        "--reasoning",
        "off",
        "--reasoning-budget",
        "0",
        "--reasoning-format",
        "deepseek",
    ]


def ensure_metal_runtime(
    *,
    project_root: Path = PROJECT_ROOT,
    endpoint: str = AMD_METAL_ENDPOINT,
    model: str = AMD_METAL_MODEL,
    vram_gb: float = 8.0,
    environment: Mapping[str, str] | None = None,
    startup_timeout: float = 60.0,
) -> MetalRuntimeHandle:
    if endpoint_ready(endpoint):
        return MetalRuntimeHandle(endpoint=endpoint)
    if not is_intel_macos():
        raise MetalRuntimeError("AMD Metal 实验后端只适用于 Intel macOS。")

    env = dict(os.environ if environment is None else environment)
    server = find_llama_server(project_root=project_root, environment=env)
    if server is None:
        raise MetalRuntimeError(
            "没有找到已编译的 llama-server；预期位置是仓库同级的 "
            "llama.cpp-metal/build-metal/bin/llama-server。"
        )
    model_path = resolve_ollama_model(model, environment=env)
    if model_path is None:
        raise MetalRuntimeError(f"没有找到 {model}；请先运行 ollama pull {model}。")

    plan = metal_launch_plan(model_path, vram_gb=vram_gb)
    if plan.partial_offload:
        host_memory = _total_memory_gb()
        # The file itself is mapped into RAM in addition to macOS, Metal and
        # KV allocations. Refuse a launch that is likely to invoke the OOM
        # killer instead of leaving the machine unresponsive.
        minimum_memory = plan.model_gb + 8.0
        if host_memory and host_memory < minimum_memory:
            raise MetalRuntimeError(
                f"{model} 约 {plan.model_gb:.1f} GB；部分 GPU 卸载至少需要约 "
                f"{minimum_memory:.0f} GB RAM，这台机器检测到 {host_memory:.0f} GB。"
            )

    runtime_dir = project_root / ".leettutor"
    runtime_dir.mkdir(parents=True, exist_ok=True)
    log_path = runtime_dir / "amd-metal-server.log"
    log_file = log_path.open("ab", buffering=0)
    command = build_server_command(
        server, model_path, endpoint=endpoint, model=model, vram_gb=vram_gb
    )
    child_env = env.copy()
    child_env["GGML_METAL_CONCURRENCY_DISABLE"] = "1"
    try:
        process = subprocess.Popen(
            command,
            cwd=project_root,
            env=child_env,
            stdout=log_file,
            stderr=subprocess.STDOUT,
        )
    except OSError as exc:
        log_file.close()
        raise MetalRuntimeError(f"无法启动 AMD Metal 服务：{exc}") from exc

    handle = MetalRuntimeHandle(endpoint, process, log_file, log_path)
    deadline = time.monotonic() + startup_timeout
    while time.monotonic() < deadline:
        if endpoint_ready(endpoint):
            return handle
        if process.poll() is not None:
            break
        time.sleep(0.25)

    handle.stop()
    detail = _log_tail(log_path)
    suffix = f" 最近日志：{detail}" if detail else ""
    raise MetalRuntimeError(f"AMD Metal 服务未能在 {startup_timeout:g} 秒内启动。{suffix}")


def _log_tail(path: Path, *, limit: int = 1200) -> str:
    try:
        content = path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return ""
    return " ".join(content[-limit:].split())
