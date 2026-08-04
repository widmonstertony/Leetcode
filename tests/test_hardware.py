from unittest.mock import Mock, patch

from leettutor.hardware import (
    HardwareProfile,
    _detect_macos_gpu,
    recommend_models,
)


def test_small_machine_gets_small_model() -> None:
    profile = HardwareProfile("Windows", "AMD64", 8, "CPU")
    assert recommend_models(profile)[0].ollama_id == "deepseek-r1:1.5b"


def test_apple_silicon_16gb_gets_balanced_8b_model() -> None:
    profile = HardwareProfile(
        "Darwin",
        "arm64",
        16,
        "Apple M2",
        apple_silicon=True,
        ollama_gpu_supported=True,
    )
    assert recommend_models(profile)[0].ollama_id == "deepseek-r1:8b"


def test_intel_mac_flags_lm_studio_as_unsupported() -> None:
    profile = HardwareProfile("Darwin", "x86_64", 32, "Intel")
    assert not profile.lm_studio_supported


def test_cpu_only_32gb_prefers_responsive_8b_model() -> None:
    profile = HardwareProfile(
        "Darwin",
        "x86_64",
        32,
        "Intel",
        gpu="AMD Radeon Pro 5600M",
        vram_gb=8,
        ollama_gpu_supported=False,
    )
    assert recommend_models(profile)[0].ollama_id == "deepseek-r1:8b"


def test_macos_gpu_parser_prefers_discrete_amd_gpu() -> None:
    output = """
    Chipset Model: Intel UHD Graphics 630
    VRAM (Dynamic, Max): 1536 MB
    Chipset Model: AMD Radeon Pro 5600M
    VRAM (Total): 8 GB
    """
    completed = Mock(stdout=output)
    with patch("leettutor.hardware.subprocess.run", return_value=completed):
        assert _detect_macos_gpu() == ("AMD Radeon Pro 5600M", 8.0)
