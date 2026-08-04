"""Local JSON/.env configuration for LeetTutor-Local."""

from __future__ import annotations

import json
import os
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Any, Mapping

from dotenv import load_dotenv

from .prompts import ALGORITHM_SYSTEM_PROMPT, SYSTEM_DESIGN_SYSTEM_PROMPT


PROJECT_ROOT = Path(__file__).resolve().parents[1]
DEFAULT_CONFIG_PATH = PROJECT_ROOT / "config.json"
DEFAULT_ENV_PATH = PROJECT_ROOT / ".env"


class ConfigError(RuntimeError):
    """Raised when a local configuration file cannot be read or written."""


@dataclass
class AppConfig:
    """All user-editable settings persisted by the app."""

    provider: str = "Ollama"
    endpoints: dict[str, str] = field(
        default_factory=lambda: {
            "Ollama": "http://localhost:11434",
            "LM Studio": "http://localhost:1234/v1",
        }
    )
    model: str = ""
    temperatures: dict[str, float] = field(
        default_factory=lambda: {"algorithm": 0.2, "system_design": 0.5}
    )
    top_p: float = 0.9
    timeout_seconds: float = 120.0
    reasoning_efforts: dict[str, str] = field(
        default_factory=lambda: {"algorithm": "none", "system_design": "low"}
    )
    max_tokens: dict[str, int] = field(
        default_factory=lambda: {"algorithm": 768, "system_design": 1536}
    )
    api_key: str = ""
    prompts: dict[str, str] = field(
        default_factory=lambda: {
            "algorithm": ALGORITHM_SYSTEM_PROMPT,
            "system_design": SYSTEM_DESIGN_SYSTEM_PROMPT,
        }
    )

    def to_dict(self) -> dict[str, Any]:
        return asdict(self)

    @classmethod
    def from_mapping(cls, raw: Mapping[str, Any]) -> "AppConfig":
        defaults = cls()

        provider = str(raw.get("provider", defaults.provider))
        if provider not in defaults.endpoints:
            provider = defaults.provider

        raw_endpoints = raw.get("endpoints", {})
        endpoints = defaults.endpoints.copy()
        if isinstance(raw_endpoints, Mapping):
            for name in endpoints:
                value = raw_endpoints.get(name)
                if isinstance(value, str) and value.strip():
                    endpoints[name] = value.strip()

        raw_temperatures = raw.get("temperatures", {})
        temperatures = defaults.temperatures.copy()
        if isinstance(raw_temperatures, Mapping):
            for mode in temperatures:
                temperatures[mode] = _bounded_float(
                    raw_temperatures.get(mode),
                    default=temperatures[mode],
                    minimum=0.0,
                    maximum=2.0,
                )

        raw_prompts = raw.get("prompts", {})
        prompts = defaults.prompts.copy()
        if isinstance(raw_prompts, Mapping):
            for mode in prompts:
                value = raw_prompts.get(mode)
                if isinstance(value, str) and value.strip():
                    prompts[mode] = value.strip()

        raw_reasoning = raw.get("reasoning_efforts", {})
        reasoning_efforts = defaults.reasoning_efforts.copy()
        if isinstance(raw_reasoning, Mapping):
            for mode in reasoning_efforts:
                value = str(raw_reasoning.get(mode, reasoning_efforts[mode]))
                if value in {"none", "low", "medium", "high"}:
                    reasoning_efforts[mode] = value

        raw_max_tokens = raw.get("max_tokens", {})
        max_tokens = defaults.max_tokens.copy()
        if isinstance(raw_max_tokens, Mapping):
            for mode in max_tokens:
                max_tokens[mode] = int(
                    _bounded_float(
                        raw_max_tokens.get(mode),
                        default=float(max_tokens[mode]),
                        minimum=64,
                        maximum=4096,
                    )
                )

        return cls(
            provider=provider,
            endpoints=endpoints,
            model=str(raw.get("model", "")).strip(),
            temperatures=temperatures,
            top_p=_bounded_float(
                raw.get("top_p"), default=defaults.top_p, minimum=0.0, maximum=1.0
            ),
            timeout_seconds=_bounded_float(
                raw.get("timeout_seconds"),
                default=defaults.timeout_seconds,
                minimum=5.0,
                maximum=600.0,
            ),
            reasoning_efforts=reasoning_efforts,
            max_tokens=max_tokens,
            api_key=str(raw.get("api_key", "")).strip(),
            prompts=prompts,
        )


def _bounded_float(
    value: object, *, default: float, minimum: float, maximum: float
) -> float:
    try:
        number = float(value)
    except (TypeError, ValueError):
        return default
    return min(max(number, minimum), maximum)


def load_config(path: Path = DEFAULT_CONFIG_PATH) -> AppConfig:
    """Load JSON settings, then apply optional environment overrides."""

    load_dotenv(DEFAULT_ENV_PATH, override=False)
    raw: Mapping[str, Any] = {}
    if path.exists():
        try:
            loaded = json.loads(path.read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError) as exc:
            raise ConfigError(f"无法读取 {path.name}：{exc}") from exc
        if not isinstance(loaded, Mapping):
            raise ConfigError(f"{path.name} 的顶层必须是 JSON 对象。")
        raw = loaded

    config = AppConfig.from_mapping(raw)
    config.provider = os.getenv("LEETTUTOR_PROVIDER", config.provider)
    if config.provider not in config.endpoints:
        config.provider = "Ollama"
    config.endpoints["Ollama"] = os.getenv(
        "LEETTUTOR_OLLAMA_URL", config.endpoints["Ollama"]
    )
    config.endpoints["LM Studio"] = os.getenv(
        "LEETTUTOR_LM_STUDIO_URL", config.endpoints["LM Studio"]
    )
    config.model = os.getenv("LEETTUTOR_MODEL", config.model)
    config.api_key = os.getenv("LEETTUTOR_API_KEY", config.api_key)
    return config


def save_config(config: AppConfig, path: Path = DEFAULT_CONFIG_PATH) -> None:
    """Atomically persist settings so a partial write cannot corrupt them."""

    path.parent.mkdir(parents=True, exist_ok=True)
    temporary_path = path.with_suffix(path.suffix + ".tmp")
    try:
        temporary_path.write_text(
            json.dumps(config.to_dict(), ensure_ascii=False, indent=2) + "\n",
            encoding="utf-8",
        )
        temporary_path.replace(path)
    except OSError as exc:
        raise ConfigError(f"无法保存 {path.name}：{exc}") from exc
