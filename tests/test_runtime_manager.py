from pathlib import Path
from unittest.mock import Mock, patch

from leettutor.runtime_manager import (
    OllamaRuntimeStatus,
    download_official_installer,
    installer_for_system,
    is_local_endpoint,
    start_ollama,
)


def test_official_installer_urls_are_platform_specific() -> None:
    mac = installer_for_system("Darwin")
    windows = installer_for_system("Windows")
    assert mac is not None and mac.url == "https://ollama.com/download/Ollama.dmg"
    assert windows is not None and windows.url.endswith("OllamaSetup.exe")
    assert installer_for_system("Plan9") is None


def test_local_endpoint_detection() -> None:
    assert is_local_endpoint("http://localhost:11434/v1")
    assert is_local_endpoint("127.0.0.1:11434")
    assert not is_local_endpoint("http://model-server.internal:11434")


def test_installer_download_is_atomic(tmp_path: Path, monkeypatch) -> None:
    class Response:
        headers = {"content-length": "6"}

        def __enter__(self):
            return self

        def __exit__(self, *_args):
            return None

        def raise_for_status(self):
            return None

        def iter_content(self, chunk_size: int):
            assert chunk_size == 1024 * 1024
            return iter([b"abc", b"def"])

    monkeypatch.setattr("leettutor.runtime_manager.requests.get", lambda *_a, **_k: Response())
    spec = installer_for_system("Darwin")
    assert spec is not None
    updates = list(download_official_installer(spec, tmp_path))
    assert updates[-1].path == tmp_path / "Ollama.dmg"
    assert updates[-1].fraction == 1.0
    assert (tmp_path / "Ollama.dmg").read_bytes() == b"abcdef"


def test_windows_ollama_start_can_enable_vulkan(tmp_path: Path) -> None:
    executable = tmp_path / "ollama.exe"
    executable.write_bytes(b"")
    status = OllamaRuntimeStatus(True, True, False, executable=executable)
    popen = Mock()
    with patch("leettutor.runtime_manager.platform.system", return_value="Windows"), patch(
        "leettutor.runtime_manager.subprocess.Popen", popen
    ):
        start_ollama(status, enable_vulkan=True)

    kwargs = popen.call_args.kwargs
    assert kwargs["env"]["OLLAMA_VULKAN"] == "1"
