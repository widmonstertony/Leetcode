"""Manage the experimental Intel Mac AMD Metal llama.cpp runtime."""

from __future__ import annotations

import json
import os
import platform
import subprocess
import time
from dataclasses import dataclass
from pathlib import Path
from typing import IO, Mapping
from urllib.error import HTTPError, URLError
from urllib.parse import urlsplit
from urllib.request import Request, urlopen


AMD_METAL_PROVIDER = "AMD Metal（Intel Mac）"
AMD_METAL_MODEL = "qwen3.5:9b"
AMD_METAL_ENDPOINT = "http://127.0.0.1:11435/v1"
AMD_METAL_API_KEY = "leettutor-local"
PROJECT_ROOT = Path(__file__).resolve().parents[1]


class MetalRuntimeError(RuntimeError):
    """Raised when the local experimental Metal runtime cannot start."""


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


def find_llama_server(
    *, project_root: Path = PROJECT_ROOT, environment: Mapping[str, str] | None = None
) -> Path | None:
    env = environment or os.environ
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


def resolve_ollama_model(
    model: str = AMD_METAL_MODEL,
    *,
    models_root: Path | None = None,
    environment: Mapping[str, str] | None = None,
) -> Path | None:
    env = environment or os.environ
    configured = env.get("LEETTUTOR_METAL_MODEL_PATH", "").strip()
    if configured:
        candidate = Path(configured).expanduser()
        return candidate.resolve() if candidate.is_file() else None

    root = models_root or Path(env.get("OLLAMA_MODELS", Path.home() / ".ollama" / "models"))
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
) -> list[str]:
    parsed = urlsplit(endpoint)
    if parsed.scheme != "http" or parsed.hostname not in {"localhost", "127.0.0.1"}:
        raise MetalRuntimeError("AMD Metal Endpoint 必须是本机 http 地址。")
    port = parsed.port or 11435
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
        "999",
        "-fit",
        "off",
        "-lm",
        "none",
        "-c",
        "4096",
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
    environment: Mapping[str, str] | None = None,
    startup_timeout: float = 60.0,
) -> MetalRuntimeHandle:
    if endpoint_ready(endpoint):
        return MetalRuntimeHandle(endpoint=endpoint)
    if not is_intel_macos():
        raise MetalRuntimeError("AMD Metal 实验后端只适用于 Intel macOS。")

    env = dict(environment or os.environ)
    server = find_llama_server(project_root=project_root, environment=env)
    if server is None:
        raise MetalRuntimeError(
            "没有找到已编译的 llama-server；预期位置是仓库同级的 "
            "llama.cpp-metal/build-metal/bin/llama-server。"
        )
    model_path = resolve_ollama_model(model, environment=env)
    if model_path is None:
        raise MetalRuntimeError(f"没有找到 {model}；请先运行 ollama pull {model}。")

    runtime_dir = project_root / ".leettutor"
    runtime_dir.mkdir(parents=True, exist_ok=True)
    log_path = runtime_dir / "amd-metal-server.log"
    log_file = log_path.open("ab", buffering=0)
    command = build_server_command(server, model_path, endpoint=endpoint, model=model)
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
