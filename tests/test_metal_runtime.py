import json
from pathlib import Path

from leettutor.metal_runtime import (
    AMD_METAL_API_KEY,
    AMD_METAL_ENDPOINT,
    build_server_command,
    is_intel_macos,
    resolve_ollama_model,
)


def test_intel_macos_detection() -> None:
    assert is_intel_macos(system="Darwin", machine="x86_64")
    assert not is_intel_macos(system="Darwin", machine="arm64")
    assert not is_intel_macos(system="Windows", machine="AMD64")


def test_resolve_ollama_model_from_manifest(tmp_path: Path) -> None:
    manifest = (
        tmp_path
        / "manifests"
        / "registry.ollama.ai"
        / "library"
        / "qwen3.5"
        / "9b"
    )
    manifest.parent.mkdir(parents=True)
    manifest.write_text(
        json.dumps(
            {
                "layers": [
                    {
                        "mediaType": "application/vnd.ollama.image.model",
                        "digest": "sha256:abc123",
                    }
                ]
            }
        ),
        encoding="utf-8",
    )
    blob = tmp_path / "blobs" / "sha256-abc123"
    blob.parent.mkdir()
    blob.write_bytes(b"GGUF")

    assert resolve_ollama_model("qwen3.5:9b", models_root=tmp_path) == blob.resolve()


def test_server_command_uses_private_vram_loading(tmp_path: Path) -> None:
    server = tmp_path / "llama-server"
    model = tmp_path / "model.gguf"
    command = build_server_command(server, model, endpoint=AMD_METAL_ENDPOINT)

    assert command[:3] == [str(server), "-m", str(model)]
    assert command[command.index("-lm") + 1] == "none"
    assert command[command.index("-dev") + 1] == "MTL0"
    assert command[command.index("--api-key") + 1] == AMD_METAL_API_KEY
    assert command[command.index("--reasoning") + 1] == "off"
