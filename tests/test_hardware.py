from unittest.mock import Mock, patch

from leettutor.hardware import (
    HardwareProfile,
    _detect_macos_gpu,
    _parse_windows_gpu_output,
    recommend_models,
    recommend_generation_defaults,
)


def test_small_machine_gets_small_model() -> None:
    profile = HardwareProfile("Windows", "AMD64", 8, "CPU")
    assert recommend_models(profile)[0].ollama_id == "qwen3.5:4b"


def test_apple_silicon_16gb_gets_balanced_8b_model() -> None:
    profile = HardwareProfile(
        "Darwin",
        "arm64",
        16,
        "Apple M2",
        apple_silicon=True,
        ollama_gpu_supported=True,
    )
    assert recommend_models(profile)[0].ollama_id == "qwen3.5:9b"


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
    recommendations = recommend_models(profile)
    assert recommendations[0].ollama_id == "qwen3.5:9b"
    assert recommendations[-1].ollama_id == "qwen3.6:27b"


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


def test_windows_gpu_parser_prefers_5600m_and_corrects_reported_vram() -> None:
    output = """
    Intel(R) UHD Graphics 630|1073741824
    AMD Radeon Pro 5600M|4293918720
    """
    assert _parse_windows_gpu_output(output) == ("AMD Radeon Pro 5600M", 8.0)


def test_windows_amd_profile_recommends_qwen35_and_qwen36_stretch() -> None:
    profile = HardwareProfile(
        "Windows",
        "AMD64",
        32,
        "Intel Core i9",
        gpu="AMD Radeon Pro 5600M",
        vram_gb=8,
        ollama_gpu_supported=True,
        ollama_vulkan_required=True,
    )
    assert [item.ollama_id for item in recommend_models(profile)] == [
        "qwen3.5:9b",
        "qwen3.5:4b",
        "qwen3.6:27b",
    ]
    assert "Vulkan" in profile.summary


def test_16gb_nvidia_prefers_qwen36_and_tunes_9b_for_interactive_use() -> None:
    profile = HardwareProfile(
        "Windows",
        "AMD64",
        32,
        "Intel Core i9",
        gpu="NVIDIA GeForce RTX 4090 Laptop GPU",
        vram_gb=16,
        ollama_gpu_supported=True,
    )

    assert [item.ollama_id for item in recommend_models(profile)] == [
        "qwen3.6:27b",
        "qwen3.5:9b",
        "qwen3.5:4b",
    ]

    defaults = recommend_generation_defaults(profile, "qwen3.5:9b")
    assert defaults.timeout_seconds == 300
    assert defaults.context_tokens == 8192
    assert defaults.keep_alive == "30m"
    assert defaults.algorithm_reasoning == "none"
    assert defaults.system_design_reasoning == "low"
    assert not defaults.partially_offloaded


def test_16gb_nvidia_marks_qwen36_27b_as_partially_offloaded() -> None:
    profile = HardwareProfile(
        "Windows",
        "AMD64",
        32,
        "Intel Core i9",
        gpu="NVIDIA GeForce RTX 4090 Laptop GPU",
        vram_gb=16,
        ollama_gpu_supported=True,
    )
    defaults = recommend_generation_defaults(profile, "qwen3.6:27b-q4_K_M")
    assert defaults.partially_offloaded
    assert defaults.timeout_seconds == 480
    assert defaults.context_tokens == 4096
