"""Cross-platform one-click launcher for LeetTutor-Local."""

from __future__ import annotations

import argparse
import hashlib
import os
import socket
import shutil
import subprocess
import sys
import venv
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parents[1]
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from leettutor.metal_runtime import (
    AMD_METAL_ENDPOINT,
    AMD_METAL_MODEL,
    MetalRuntimeError,
    MetalRuntimeHandle,
    ensure_metal_runtime,
    is_intel_macos,
)
from leettutor.lan import (
    build_lan_url,
    find_lan_ipv4,
    generate_access_code,
    load_or_create_trust_secret,
)


VENV_DIRECTORY = PROJECT_ROOT / ".venv"
REQUIREMENTS = PROJECT_ROOT / "requirements.txt"
DEPENDENCY_MARKER = VENV_DIRECTORY / ".leettutor-requirements.sha256"


def virtualenv_python() -> Path:
    if sys.platform == "win32":
        return VENV_DIRECTORY / "Scripts" / "python.exe"
    return VENV_DIRECTORY / "bin" / "python"


def requirements_digest() -> str:
    return hashlib.sha256(REQUIREMENTS.read_bytes()).hexdigest()


def prepare_environment(*, skip_install: bool) -> Path:
    python = virtualenv_python()
    if not python.exists():
        print("[LeetTutor] 首次运行：正在创建 .venv …")
        venv.EnvBuilder(with_pip=True).create(VENV_DIRECTORY)

    if skip_install:
        return python

    expected = requirements_digest()
    installed = (
        DEPENDENCY_MARKER.read_text(encoding="utf-8").strip()
        if DEPENDENCY_MARKER.exists()
        else ""
    )
    if installed != expected:
        print("[LeetTutor] 正在安装或更新依赖 …")
        subprocess.run(
            [
                str(python),
                "-m",
                "pip",
                "install",
                "--disable-pip-version-check",
                "-r",
                str(REQUIREMENTS),
            ],
            cwd=PROJECT_ROOT,
            check=True,
        )
        DEPENDENCY_MARKER.write_text(expected + "\n", encoding="utf-8")
    return python


def maybe_open_vscode() -> None:
    command = shutil.which("code")
    if command:
        subprocess.Popen([command, str(PROJECT_ROOT)])  # noqa: S603
    else:
        print("[LeetTutor] 未找到 VS Code 的 code 命令，跳过编辑器启动。")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Launch LeetTutor-Local")
    parser.add_argument(
        "--skip-install",
        action="store_true",
        help="Skip dependency installation (intended for development only).",
    )
    parser.add_argument(
        "--vscode", action="store_true", help="Also open the repository in VS Code."
    )
    parser.add_argument(
        "--lan",
        action="store_true",
        help="Allow password-protected access from other devices on this LAN.",
    )
    parser.add_argument(
        "--port", type=int, default=8501, help="Streamlit port (default: 8501)."
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    if not 1024 <= args.port <= 65535:
        print("[LeetTutor] 端口必须在 1024 到 65535 之间。", file=sys.stderr)
        return 2
    try:
        python = prepare_environment(skip_install=args.skip_install)
    except (OSError, subprocess.CalledProcessError) as exc:
        print(f"[LeetTutor] 环境准备失败：{exc}", file=sys.stderr)
        raise

    if args.vscode:
        maybe_open_vscode()

    metal_runtime: MetalRuntimeHandle | None = None
    if is_intel_macos():
        print("[LeetTutor] 正在检测 Radeon 5600M Metal 加速后端 …")
        try:
            metal_runtime = ensure_metal_runtime(project_root=PROJECT_ROOT)
        except MetalRuntimeError as exc:
            print(f"[LeetTutor] Metal 后端未启用：{exc}")
            print("[LeetTutor] 应用仍会启动，可在侧边栏改用 Ollama CPU。")
        else:
            owner = "已自动启动" if metal_runtime.managed else "已经运行"
            print(f"[LeetTutor] Qwen3.5 9B GPU 服务{owner}：{AMD_METAL_ENDPOINT}")

    environment = os.environ.copy()
    server_address = "localhost"
    if args.lan:
        server_address = "0.0.0.0"
        lan_host = find_lan_ipv4() or f"{socket.gethostname()}.local"
        lan_url = build_lan_url(lan_host, args.port)
        access_code = environment.get("LEETTUTOR_ACCESS_CODE", "").strip()
        if not access_code:
            access_code = generate_access_code()
        environment["LEETTUTOR_LAN_MODE"] = "1"
        environment["LEETTUTOR_LAN_URL"] = lan_url
        environment["LEETTUTOR_ACCESS_CODE"] = access_code
        environment.setdefault(
            "LEETTUTOR_LAN_TRUST_SECRET",
            load_or_create_trust_secret(
                PROJECT_ROOT / ".leettutor" / "lan-trust-secret"
            ),
        )
        print(f"[LeetTutor] 手机访问地址：{lan_url}")
        print(f"[LeetTutor] 本次访问码：{access_code}")
        print("[LeetTutor] 首次验证后，可在该浏览器记住此主机 30 天。")
        print("[LeetTutor] 手机与主机必须在同一 Wi-Fi；请勿把端口映射到公网。")

    print("[LeetTutor] 正在启动；浏览器会自动打开。按 Ctrl+C 停止。")
    command = [
        str(python),
        "-m",
        "streamlit",
        "run",
        str(PROJECT_ROOT / "app.py"),
        f"--server.address={server_address}",
        f"--server.port={args.port}",
        "--server.headless=false",
        "--client.toolbarMode=viewer",
    ]
    if metal_runtime is not None:
        environment.setdefault("LEETTUTOR_AMD_METAL_URL", AMD_METAL_ENDPOINT)
        environment.setdefault("LEETTUTOR_MODEL", AMD_METAL_MODEL)
    try:
        return subprocess.run(
            command,
            cwd=PROJECT_ROOT,
            env=environment,
            check=False,
        ).returncode
    except KeyboardInterrupt:
        return 0
    except OSError as exc:
        print(f"[LeetTutor] 启动失败：{exc}", file=sys.stderr)
        raise
    finally:
        if metal_runtime is not None and metal_runtime.managed:
            print("[LeetTutor] 正在关闭 AMD Metal 模型服务 …")
            metal_runtime.stop()


if __name__ == "__main__":
    raise SystemExit(main())
