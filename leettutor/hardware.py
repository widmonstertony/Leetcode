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
    ollama_vulkan_required: bool = False

    @property
    def summary(self) -> str:
        parts = [f"{self.system} {self.machine}", f"{self.memory_gb:.0f} GB RAM"]
        if self.gpu:
            gpu = self.gpu
            if self.vram_gb:
                gpu += f" · {self.vram_gb:.0f} GB VRAM"
            if self.ollama_vulkan_required:
                gpu += " · Ollama Vulkan（实验）"
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


@dataclass(frozen=True)
class GenerationDefaults:
    """Hardware- and model-aware generation settings for the local runtime."""

    algorithm_temperature: float
    system_design_temperature: float
    top_p: float
    timeout_seconds: float
    algorithm_reasoning: str
    system_design_reasoning: str
    algorithm_max_tokens: int
    system_design_max_tokens: int
    context_tokens: int
    keep_alive: str
    partially_offloaded: bool


MODEL_CATALOG: tuple[ModelRecommendation, ...] = (
    ModelRecommendation(
        "qwen3.5:4b",
        "Qwen 3.5 4B",
        3.4,
        6,
        "低显存快速导师，适合逐步提示和日常对话",
        "qwen3.5-4b",
    ),
    ModelRecommendation(
        "qwen3:8b",
        "Qwen 3 8B",
        5.2,
        8,
        "兼容性成熟的轻量算法与中文推理",
        "qwen3-8b",
    ),
    ModelRecommendation(
        "qwen3.5:9b",
        "Qwen 3.5 9B（首选）",
        6.6,
        12,
        "8 GB 显卡的最佳综合档：中文、算法、代码与响应速度平衡",
        "qwen3.5-9b",
    ),
    ModelRecommendation(
        "deepseek-r1:8b",
        "DeepSeek R1 8B",
        5.2,
        12,
        "困难题的长推理备选；需要开启思考并提高输出额度",
        "deepseek-r1-0528-qwen3-8b",
    ),
    ModelRecommendation(
        "qwen3.6:27b",
        "Qwen 3.6 27B（慢速进阶）",
        17,
        28,
        "质量更高，但 8 GB 显卡只能部分卸载，适合不赶时间的深度 Review",
        "qwen3.6-27b",
    ),
    ModelRecommendation(
        "qwen3.6:35b",
        "Qwen 3.6 35B-A3B（大内存）",
        24,
        32,
        "MoE 进阶模型；32 GB 机器余量很小，不建议作为常驻导师",
        "qwen3.6-35b-a3b",
    ),
)

_MODELS_BY_ID = {model.ollama_id: model for model in MODEL_CATALOG}


def detect_hardware() -> HardwareProfile:
    system = platform.system() or "Unknown"
    machine = platform.machine() or "Unknown"
    cpu = platform.processor() or _run_first_line(["sysctl", "-n", "machdep.cpu.brand_string"])
    memory_gb = _total_memory_bytes() / (1024**3)
    apple_silicon = system == "Darwin" and machine.lower() in {"arm64", "aarch64"}
    gpu, vram = _detect_nvidia_gpu()
    ollama_gpu_supported = bool(gpu)
    ollama_vulkan_required = False
    if apple_silicon and not gpu:
        gpu = "Apple Silicon（统一内存）"
        ollama_gpu_supported = True
    elif system == "Windows" and not gpu:
        gpu, vram = _detect_windows_gpu()
        gpu_name = gpu.casefold()
        if "amd" in gpu_name or "radeon" in gpu_name or "intel" in gpu_name:
            # Ollama exposes additional Windows GPU support through its Vulkan
            # backend. It is experimental, so keep that fact visible in the UI.
            ollama_gpu_supported = True
            ollama_vulkan_required = True
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
        ollama_vulkan_required=ollama_vulkan_required,
    )


def recommend_models(profile: HardwareProfile) -> list[ModelRecommendation]:
    """Return a responsive default, a faster option, and a quality stretch."""

    accelerated_memory = (
        profile.memory_gb
        if profile.apple_silicon
        else ((profile.vram_gb or 0) if profile.ollama_gpu_supported else 0)
    )
    if profile.memory_gb >= 28 and accelerated_memory >= 15:
        model_ids = ["qwen3.6:27b", "qwen3.5:9b", "qwen3.5:4b"]
    elif profile.memory_gb >= 12 and (
        accelerated_memory >= 7 or (not profile.ollama_gpu_supported and profile.memory_gb >= 16)
    ):
        model_ids = ["qwen3.5:9b", "qwen3.5:4b"]
    else:
        model_ids = ["qwen3.5:4b", "qwen3:8b"]

    if profile.memory_gb >= 28 and "qwen3.6:27b" not in model_ids:
        model_ids.append("qwen3.6:27b")
    elif profile.memory_gb < 28:
        model_ids.append("deepseek-r1:8b")
    return [_MODELS_BY_ID[model_id] for model_id in model_ids]


def recommend_generation_defaults(
    profile: HardwareProfile, model: str
) -> GenerationDefaults:
    """Choose safe interactive defaults without overriding manual mode."""

    accelerated_memory = (
        profile.memory_gb
        if profile.apple_silicon
        else ((profile.vram_gb or 0) if profile.ollama_gpu_supported else 0)
    )
    estimated_model_gb = _estimated_model_memory_gb(model)
    model_parameters = _model_parameters_b(model)
    usable_accelerated_memory = max(0.0, accelerated_memory - 1.5)
    partially_offloaded = (
        not profile.ollama_gpu_supported
        or estimated_model_gb > usable_accelerated_memory
    )

    if model_parameters >= 35:
        timeout_seconds = 600.0 if partially_offloaded else 420.0
    elif model_parameters >= 27:
        timeout_seconds = 480.0 if partially_offloaded else 360.0
    elif model_parameters >= 8:
        timeout_seconds = 420.0 if partially_offloaded else 300.0
    else:
        timeout_seconds = 240.0 if partially_offloaded else 180.0

    roomy_gpu = accelerated_memory >= 12
    return GenerationDefaults(
        algorithm_temperature=0.2,
        system_design_temperature=0.4,
        top_p=0.9,
        timeout_seconds=timeout_seconds,
        algorithm_reasoning="none",
        system_design_reasoning=(
            "low" if roomy_gpu and model_parameters <= 27 else "none"
        ),
        algorithm_max_tokens=1024 if roomy_gpu else 768,
        system_design_max_tokens=(
            2048 if roomy_gpu and model_parameters < 27 else 1536
        ),
        context_tokens=8192 if roomy_gpu and not partially_offloaded else 4096,
        keep_alive="30m" if roomy_gpu else "10m",
        partially_offloaded=partially_offloaded,
    )


def _model_parameters_b(model: str) -> float:
    matches = re.findall(r"(\d+(?:\.\d+)?)b", model.casefold())
    return float(matches[0]) if matches else 0.0


def _estimated_model_memory_gb(model: str) -> float:
    folded = model.casefold()
    parameters = _model_parameters_b(folded)
    if parameters >= 35:
        return 24.0
    if parameters >= 27:
        return 17.0
    if parameters >= 14:
        return 9.3
    if parameters >= 9:
        return 11.0 if "q8" in folded else 6.6
    if parameters >= 8:
        return 5.2
    if parameters >= 4:
        return 3.4
    return max(1.0, parameters * 0.7)


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


def _detect_windows_gpu() -> tuple[str, float | None]:
    """Return the most useful Windows display adapter and its reported VRAM."""

    shell = shutil.which("powershell") or shutil.which("pwsh")
    if not shell:
        return "", None
    scripts = (
        (
            "Get-CimInstance Win32_VideoController -ErrorAction SilentlyContinue | "
            'ForEach-Object { "$($_.Name)|$($_.AdapterRAM)" }'
        ),
        (
            "Get-ItemProperty -Path "
            "'HKLM:\\SYSTEM\\CurrentControlSet\\Control\\Video\\*\\0000' "
            "-ErrorAction SilentlyContinue | Where-Object DriverDesc | "
            'ForEach-Object { "$($_.DriverDesc)|" }'
        ),
    )
    for script in scripts:
        try:
            result = subprocess.run(
                [shell, "-NoProfile", "-NonInteractive", "-Command", script],
                capture_output=True,
                text=True,
                timeout=8,
                check=False,
            )
        except (OSError, subprocess.TimeoutExpired):
            continue
        detected = _parse_windows_gpu_output(result.stdout)
        if detected[0]:
            return detected
    return "", None


def _parse_windows_gpu_output(output: str) -> tuple[str, float | None]:
    devices: list[tuple[str, float | None]] = []
    known_vram = {"radeon pro 5600m": 8.0}
    for raw_line in output.splitlines():
        if "|" not in raw_line:
            continue
        raw_name, raw_bytes = raw_line.rsplit("|", 1)
        name = raw_name.strip()
        if not name:
            continue
        vram: float | None = None
        try:
            vram = int(raw_bytes.strip()) / (1024**3)
        except ValueError:
            pass
        folded = name.casefold()
        for marker, expected_vram in known_vram.items():
            if marker in folded:
                vram = expected_vram
                break
        devices.append((name, vram))
    if not devices:
        return "", None
    discrete = [
        device
        for device in devices
        if any(marker in device[0].casefold() for marker in ("nvidia", "amd", "radeon"))
    ]
    return max(discrete or devices, key=lambda device: device[1] or 0.0)


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


def _run_first_line(command: list[str]) -> str:
    try:
        result = subprocess.run(
            command, capture_output=True, text=True, timeout=3, check=False
        )
        return result.stdout.splitlines()[0].strip() if result.stdout else ""
    except (OSError, subprocess.TimeoutExpired):
        return ""
