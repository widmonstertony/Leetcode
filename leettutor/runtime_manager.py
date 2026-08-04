"""Install, launch, and inspect the local Ollama runtime."""

from __future__ import annotations

import os
import platform
import shutil
import subprocess
from dataclasses import dataclass
from pathlib import Path
from typing import Iterator
from urllib.parse import urlsplit

import requests

from .model_manager import ModelDownloadError, ollama_native_url


class RuntimeSetupError(RuntimeError):
    """An actionable Ollama runtime setup failure."""


@dataclass(frozen=True)
class InstallerSpec:
    url: str
    filename: str
    instructions: str


@dataclass(frozen=True)
class InstallerProgress:
    downloaded: int
    total: int
    path: Path | None = None

    @property
    def fraction(self) -> float:
        if self.total <= 0:
            return 0.0
        return min(max(self.downloaded / self.total, 0.0), 1.0)


@dataclass(frozen=True)
class OllamaRuntimeStatus:
    local_endpoint: bool
    installed: bool
    running: bool
    version: str = ""
    executable: Path | None = None


def installer_for_system(system: str | None = None) -> InstallerSpec | None:
    current = system or platform.system()
    if current == "Darwin":
        return InstallerSpec(
            url="https://ollama.com/download/Ollama.dmg",
            filename="Ollama.dmg",
            instructions="打开 DMG 后把 Ollama 拖入 Applications，再启动一次 Ollama。",
        )
    if current == "Windows":
        return InstallerSpec(
            url="https://ollama.com/download/OllamaSetup.exe",
            filename="OllamaSetup.exe",
            instructions="完成官方安装向导；Ollama 随后会在后台运行。",
        )
    return None


def is_local_endpoint(endpoint: str) -> bool:
    value = endpoint.strip()
    if "://" not in value:
        value = f"http://{value}"
    hostname = urlsplit(value).hostname
    return hostname in {"localhost", "127.0.0.1", "::1"}


def inspect_ollama_runtime(endpoint: str) -> OllamaRuntimeStatus:
    """Inspect both the configured API and common local install locations."""

    local = is_local_endpoint(endpoint)
    executable = _find_ollama_executable() if local else None
    installed = executable is not None
    try:
        url = ollama_native_url(endpoint, "/api/version")
        response = requests.get(url, timeout=1.5)
        response.raise_for_status()
        payload = response.json()
        return OllamaRuntimeStatus(
            local_endpoint=local,
            installed=installed or local,
            running=True,
            version=str(payload.get("version", "")),
            executable=executable,
        )
    except (requests.RequestException, ValueError, ModelDownloadError):
        return OllamaRuntimeStatus(
            local_endpoint=local,
            installed=installed,
            running=False,
            executable=executable,
        )


def download_official_installer(
    spec: InstallerSpec, destination: Path
) -> Iterator[InstallerProgress]:
    """Download only a fixed official installer URL to an app-owned directory."""

    destination.mkdir(parents=True, exist_ok=True)
    target = destination / spec.filename
    if target.exists() and target.stat().st_size > 0:
        size = target.stat().st_size
        yield InstallerProgress(size, size, target)
        return

    partial = target.with_suffix(target.suffix + ".part")
    try:
        with requests.get(spec.url, stream=True, timeout=(10, 1800)) as response:
            response.raise_for_status()
            total = int(response.headers.get("content-length", 0) or 0)
            downloaded = 0
            with partial.open("wb") as output:
                for chunk in response.iter_content(chunk_size=1024 * 1024):
                    if not chunk:
                        continue
                    output.write(chunk)
                    downloaded += len(chunk)
                    yield InstallerProgress(downloaded, total)
            partial.replace(target)
            yield InstallerProgress(downloaded, total or downloaded, target)
    except requests.ConnectionError as exc:
        partial.unlink(missing_ok=True)
        raise RuntimeSetupError("无法从 Ollama 官方网站下载安装包。") from exc
    except requests.Timeout as exc:
        partial.unlink(missing_ok=True)
        raise RuntimeSetupError("Ollama 安装包下载超时，请重试。") from exc
    except requests.HTTPError as exc:
        partial.unlink(missing_ok=True)
        status = exc.response.status_code if exc.response is not None else "未知"
        raise RuntimeSetupError(f"官方安装包下载返回 HTTP {status}。") from exc
    except OSError as exc:
        partial.unlink(missing_ok=True)
        raise RuntimeSetupError(f"无法保存 Ollama 安装包：{exc}") from exc


def open_installer(path: Path) -> None:
    """Hand the signed installer to the operating system for user approval."""

    try:
        if platform.system() == "Darwin":
            subprocess.Popen(["open", str(path)])  # noqa: S603
        elif platform.system() == "Windows":
            os.startfile(path)  # type: ignore[attr-defined]
        else:
            raise RuntimeSetupError("当前系统暂不支持自动打开安装器。")
    except OSError as exc:
        raise RuntimeSetupError(f"无法打开 Ollama 安装器：{exc}") from exc


def start_ollama(status: OllamaRuntimeStatus) -> None:
    """Start an existing Ollama installation without invoking a shell."""

    if status.running:
        return
    try:
        if platform.system() == "Darwin":
            app_paths = [
                Path("/Applications/Ollama.app"),
                Path.home() / "Applications" / "Ollama.app",
            ]
            app = next((path for path in app_paths if path.exists()), None)
            if app:
                subprocess.Popen(["open", str(app)])  # noqa: S603
                return
        if status.executable:
            kwargs: dict[str, object] = {
                "stdout": subprocess.DEVNULL,
                "stderr": subprocess.DEVNULL,
            }
            if platform.system() == "Windows":
                kwargs["creationflags"] = (
                    subprocess.CREATE_NEW_PROCESS_GROUP
                    | subprocess.DETACHED_PROCESS
                )
            else:
                kwargs["start_new_session"] = True
            subprocess.Popen([str(status.executable), "serve"], **kwargs)  # noqa: S603
            return
    except OSError as exc:
        raise RuntimeSetupError(f"无法启动 Ollama：{exc}") from exc
    raise RuntimeSetupError("没有找到 Ollama 可执行文件，请先完成安装向导。")


def _find_ollama_executable() -> Path | None:
    command = shutil.which("ollama")
    if command:
        return Path(command)

    candidates: list[Path] = []
    if platform.system() == "Darwin":
        candidates.extend(
            [
                Path("/Applications/Ollama.app/Contents/Resources/ollama"),
                Path.home()
                / "Applications"
                / "Ollama.app"
                / "Contents"
                / "Resources"
                / "ollama",
            ]
        )
    elif platform.system() == "Windows":
        local_app_data = os.environ.get("LOCALAPPDATA")
        if local_app_data:
            candidates.append(Path(local_app_data) / "Programs" / "Ollama" / "ollama.exe")
    return next((path for path in candidates if path.exists()), None)
