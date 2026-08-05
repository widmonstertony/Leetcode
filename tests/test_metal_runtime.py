import json
from pathlib import Path

from leettutor.metal_runtime import (
    AMD_METAL_API_KEY,
    AMD_METAL_ENDPOINT,
    LLAMA_CPP_COMMIT,
    LLAMA_CPP_TAG,
    METAL_PATCH_NAME,
    build_server_command,
    inspect_metal_setup,
    is_intel_macos,
    metal_build_commands,
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


def test_setup_status_recognizes_verified_5600m(tmp_path: Path) -> None:
    server = tmp_path / "llama-server"
    server.write_bytes(b"binary")
    server.chmod(0o755)
    model = tmp_path / "qwen.gguf"
    model.write_bytes(b"GGUF")

    status = inspect_metal_setup(
        project_root=tmp_path,
        endpoint="http://127.0.0.1:1/v1",
        gpu_name="AMD Radeon Pro 5600M",
        vram_gb=8.0,
        system="Darwin",
        machine="x86_64",
        environment={
            "LEETTUTOR_METAL_SERVER": str(server),
            "LEETTUTOR_METAL_MODEL_PATH": str(model),
        },
    )

    assert status.verified_5600m
    assert status.hardware_compatible
    assert status.runtime_ready
    assert "GPU: AMD Radeon Pro 5600M (8 GB VRAM)" in status.report_lines()


def test_four_gb_radeon_is_not_auto_install_target(tmp_path: Path) -> None:
    status = inspect_metal_setup(
        project_root=tmp_path,
        endpoint="http://127.0.0.1:1/v1",
        gpu_name="AMD Radeon Pro 5500M",
        vram_gb=4.0,
        system="Darwin",
        machine="x86_64",
        environment={},
    )

    assert status.intel_macos
    assert status.discrete_amd
    assert not status.hardware_compatible


def test_build_commands_are_pinned_to_metal_server_target(tmp_path: Path) -> None:
    source = tmp_path / "llama.cpp-metal"
    configure, compile_server = metal_build_commands(source, Path("/opt/cmake"))

    assert "-DGGML_METAL=ON" in configure
    assert "-DGGML_ACCELERATE=ON" in configure
    assert compile_server[compile_server.index("--target") + 1] == "llama-server"
    assert LLAMA_CPP_TAG == "b10240"
    assert LLAMA_CPP_COMMIT.startswith("0b14b87")


def test_pinned_qwen_patch_is_packaged() -> None:
    patch = Path(__file__).resolve().parents[1] / "patches" / METAL_PATCH_NAME
    content = patch.read_text(encoding="utf-8")

    assert "src/llama-model.cpp" in content
    assert "src/models/qwen35.cpp" in content
    assert "arch == LLM_ARCH_QWEN35" in content
