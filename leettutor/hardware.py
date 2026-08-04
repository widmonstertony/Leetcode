"""Best-effort, read-only hardware detection and model-size recommendations."""

from __future__ import annotations

import ctypes
import os
import platform
import re
import shutil
import subprocess
from dataclasses import dataclass


@dataclass(frozen=True)
class HardwareProfile:
    system: str
    machine: str
    memory_gb: float
    cpu: str
    gpu: str = ""
    vram_gb: float | None = None
    apple_silicon: bool = False
    ollama_gpu_supported: bool = False

    @property
    def summary(self) -> str:
        parts = [f"{self.system} {self.machine}", f"{self.memory_gb:.0f} GB RAM"]
        if self.gpu:
            gpu = self.gpu
            if self.vram_gb:
                gpu += f" · {self.vram_gb:.0f} GB VRAM"
            parts.append(gpu)
        return " · ".join(parts)

    @property
    def lm_studio_supported(self) -> bool:
        return not (self.system == "Darwin" and not self.apple_silicon)


@dataclass(frozen=True)
class ModelRecommendation:
    ollama_id: str
    display_name: str
    download_gb: float
    minimum_memory_gb: float
    purpose: str
    lm_search: str

    @property
    def label(self) -> str:
        return f"{self.display_name}（约 {self.download_gb:g} GB）"


MODEL_CATALOG: tuple[ModelRecommendation, ...] = (
    ModelRecommendation("deepseek-r1:1.5b", "DeepSeek R1 1.5B", 1.1, 4, "低内存入门、提示与复杂度分析", "deepseek-r1-distill-qwen-1.5b"),
    ModelRecommendation("deepseek-r1:8b", "DeepSeek R1 8B", 5.2, 8, "刷题导师的速度/质量平衡", "deepseek-r1-0528-qwen3-8b"),
    ModelRecommendation("deepseek-r1:14b", "DeepSeek R1 14B", 9.0, 16, "更稳定的推理和边界分析", "deepseek-r1-distill-qwen-14b"),
    ModelRecommendation("qwen3-coder:30b", "Qwen3 Coder 30B", 19, 28, "代码 Review 与仓库级理解", "qwen3-coder-30b-a3b-instruct"),
    ModelRecommendation("qwen3.6:27b", "Qwen3.6 27B", 17, 28, "算法与系统设计综合能力", "qwen3.6-27b"),
    ModelRecommendation("deepseek-r1:32b", "DeepSeek R1 32B", 20, 32, "高质量复杂推理", "deepseek-r1-distill-qwen-32b"),
    ModelRecommendation("deepseek-r1:70b", "DeepSeek R1 70B", 43, 60, "大内存设备的高质量推理", "deepseek-r1-distill-llama-70b"),
)


def detect_hardware() -> HardwareProfile:
    system = platform.system() or "Unknown"
    machine = platform.machine() or "Unknown"
    cpu = platform.processor() or _run_first_line(["sysctl", "-n", "machdep.cpu.brand_string"])
    memory_gb = _total_memory_bytes() / (1024**3)
    apple_silicon = system == "Darwin" and machine.lower() in {"arm64", "aarch64"}
    gpu, vram = _detect_nvidia_gpu()
    ollama_gpu_supported = bool(gpu)
    if apple_silicon and not gpu:
        gpu = "Apple Silicon（统一内存）"
        ollama_gpu_supported = True
    elif system == "Darwin" and not gpu:
        # Intel Macs can contain capable AMD discrete GPUs with dedicated VRAM,
        # but Ollama's official macOS acceleration path targets Apple Silicon.
        # Detect and show the hardware without treating that VRAM as usable by
        # the current Ollama runtime.
        gpu, vram = _detect_macos_gpu()
    return HardwareProfile(
        system=system,
        machine=machine,
        memory_gb=memory_gb,
        cpu=cpu or "Unknown CPU",
        gpu=gpu,
        vram_gb=vram,
        apple_silicon=apple_silicon,
        ollama_gpu_supported=ollama_gpu_supported,
    )


def recommend_models(profile: HardwareProfile) -> list[ModelRecommendation]:
    """Return balanced, faster, and stretch options for the detected memory."""

    if profile.apple_silicon:
        budget = profile.memory_gb * 0.75
    elif profile.ollama_gpu_supported and profile.vram_gb:
        # Allows modest CPU offload without claiming all system RAM is available.
        budget = max(profile.vram_gb, profile.memory_gb * 0.55)
    else:
        # CPU-only inference becomes frustrating before memory is exhausted.
        # Keep the balanced recommendation at 8B and expose larger models as
        # stretch options instead.
        budget = min(profile.memory_gb * 0.55, 12.0)

    fitting = [model for model in MODEL_CATALOG if model.minimum_memory_gb <= budget]
    primary = fitting[-1] if fitting else MODEL_CATALOG[0]
    primary_index = MODEL_CATALOG.index(primary)
    indexes = [primary_index]
    if primary_index > 0:
        indexes.append(primary_index - 1)
    if primary_index + 1 < len(MODEL_CATALOG):
        indexes.append(primary_index + 1)
    return [MODEL_CATALOG[index] for index in indexes]


def _total_memory_bytes() -> int:
    if platform.system() == "Windows":
        class MemoryStatus(ctypes.Structure):
            _fields_ = [
                ("length", ctypes.c_ulong),
                ("memory_load", ctypes.c_ulong),
                ("total_physical", ctypes.c_ulonglong),
                ("available_physical", ctypes.c_ulonglong),
                ("total_page_file", ctypes.c_ulonglong),
                ("available_page_file", ctypes.c_ulonglong),
                ("total_virtual", ctypes.c_ulonglong),
                ("available_virtual", ctypes.c_ulonglong),
                ("available_extended_virtual", ctypes.c_ulonglong),
            ]

        status = MemoryStatus()
        status.length = ctypes.sizeof(MemoryStatus)
        if ctypes.windll.kernel32.GlobalMemoryStatusEx(ctypes.byref(status)):  # type: ignore[attr-defined]
            return int(status.total_physical)

    try:
        return int(os.sysconf("SC_PAGE_SIZE") * os.sysconf("SC_PHYS_PAGES"))
    except (AttributeError, OSError, ValueError):
        raw = _run_first_line(["sysctl", "-n", "hw.memsize"])
        return int(raw) if raw.isdigit() else 8 * 1024**3


def _detect_nvidia_gpu() -> tuple[str, float | None]:
    command = shutil.which("nvidia-smi")
    if not command:
        return "", None


def _detect_macos_gpu() -> tuple[str, float | None]:
    """Return the most useful GPU reported by ``system_profiler`` on macOS."""

    try:
        result = subprocess.run(
            ["system_profiler", "SPDisplaysDataType"],
            capture_output=True,
            text=True,
            timeout=8,
            check=False,
        )
    except (OSError, subprocess.TimeoutExpired):
        return "", None

    devices: list[tuple[str, float | None]] = []
    current_name = ""
    current_vram: float | None = None
    for raw_line in result.stdout.splitlines():
        line = raw_line.strip()
        if line.startswith("Chipset Model:"):
            if current_name:
                devices.append((current_name, current_vram))
            current_name = line.split(":", 1)[1].strip()
            current_vram = None
        elif current_name and line.startswith("VRAM"):
            match = re.search(r":\s*([\d.]+)\s*(GB|MB)", line, re.IGNORECASE)
            if match:
                value = float(match.group(1))
                current_vram = value if match.group(2).upper() == "GB" else value / 1024
    if current_name:
        devices.append((current_name, current_vram))
    if not devices:
        return "", None

    # Prefer a discrete AMD GPU, otherwise pick the device with the most VRAM.
    amd_devices = [device for device in devices if "AMD" in device[0].upper()]
    candidates = amd_devices or devices
    return max(candidates, key=lambda device: device[1] or 0.0)
    try:
        result = subprocess.run(
            [
                command,
                "--query-gpu=name,memory.total",
                "--format=csv,noheader,nounits",
            ],
            capture_output=True,
            text=True,
            timeout=4,
            check=False,
        )
        first = result.stdout.splitlines()[0]
        name, memory_mb = (part.strip() for part in first.rsplit(",", 1))
        return name, float(memory_mb) / 1024
    except (OSError, ValueError, IndexError, subprocess.TimeoutExpired):
        return "", None


def _run_first_line(command: list[str]) -> str:
    try:
        result = subprocess.run(
            command, capture_output=True, text=True, timeout=3, check=False
        )
        return result.stdout.splitlines()[0].strip() if result.stdout else ""
    except (OSError, subprocess.TimeoutExpired):
        return ""
