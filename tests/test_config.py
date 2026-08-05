import json
import stat
from pathlib import Path

from leettutor.config import AppConfig, load_config, save_config, save_secrets
from leettutor.providers import GEMINI_PROVIDER, OPENAI_PROVIDER


def test_config_round_trip(tmp_path: Path, monkeypatch) -> None:
    for name in (
        "LEETTUTOR_PROVIDER",
        "LEETTUTOR_MODEL",
        "LEETTUTOR_OLLAMA_URL",
        "LEETTUTOR_LM_STUDIO_URL",
        "LEETTUTOR_AMD_METAL_URL",
        "LEETTUTOR_OPENAI_URL",
        "LEETTUTOR_GEMINI_URL",
        "LEETTUTOR_API_KEY",
        "LEETTUTOR_OPENAI_API_KEY",
        "LEETTUTOR_GEMINI_API_KEY",
        "OPENAI_API_KEY",
        "GEMINI_API_KEY",
    ):
        monkeypatch.delenv(name, raising=False)

    path = tmp_path / "config.json"
    original = AppConfig(
        model="qwen-test",
        auto_tune=False,
        context_tokens=16384,
    )
    original.prompts["algorithm"] = "custom prompt"
    save_config(original, path)

    loaded = load_config(path)
    assert loaded.model == "qwen-test"
    assert loaded.prompts["algorithm"] == "custom prompt"
    assert loaded.temperatures["algorithm"] == 0.2
    assert loaded.reasoning_efforts["algorithm"] == "none"
    assert loaded.max_tokens["algorithm"] == 768
    assert not loaded.auto_tune
    assert loaded.context_tokens == 16384
    assert loaded.models["Ollama"] == "qwen-test"


def test_cloud_keys_are_stored_separately_with_private_permissions(
    tmp_path: Path, monkeypatch
) -> None:
    for name in (
        "LEETTUTOR_API_KEY",
        "LEETTUTOR_OPENAI_API_KEY",
        "LEETTUTOR_GEMINI_API_KEY",
        "OPENAI_API_KEY",
        "GEMINI_API_KEY",
    ):
        monkeypatch.delenv(name, raising=False)

    config_path = tmp_path / "config.json"
    secrets_path = tmp_path / "secrets.json"
    config = AppConfig(provider=OPENAI_PROVIDER)
    config.models[OPENAI_PROVIDER] = "gpt-5.6-terra"
    config.api_keys = {
        OPENAI_PROVIDER: "sk-openai-test",
        GEMINI_PROVIDER: "gemini-test",
    }

    save_config(config, config_path)
    save_secrets(config.api_keys, secrets_path)

    raw_config = json.loads(config_path.read_text(encoding="utf-8"))
    assert "api_keys" not in raw_config
    assert "sk-openai-test" not in config_path.read_text(encoding="utf-8")
    assert stat.S_IMODE(secrets_path.stat().st_mode) == 0o600

    loaded = load_config(config_path, secrets_path=secrets_path)
    assert loaded.api_key_for(OPENAI_PROVIDER) == "sk-openai-test"
    assert loaded.api_key_for(GEMINI_PROVIDER) == "gemini-test"
    assert loaded.models[OPENAI_PROVIDER] == "gpt-5.6-terra"


def test_invalid_numbers_fall_back_or_clamp() -> None:
    config = AppConfig.from_mapping(
        {"top_p": 8, "timeout_seconds": "bad", "temperatures": {"algorithm": -3}}
    )
    assert config.top_p == 1.0
    assert config.timeout_seconds == 120.0
    assert config.temperatures["algorithm"] == 0.0
