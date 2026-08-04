from pathlib import Path

from leettutor.config import AppConfig, load_config, save_config


def test_config_round_trip(tmp_path: Path, monkeypatch) -> None:
    for name in (
        "LEETTUTOR_PROVIDER",
        "LEETTUTOR_MODEL",
        "LEETTUTOR_OLLAMA_URL",
        "LEETTUTOR_LM_STUDIO_URL",
        "LEETTUTOR_API_KEY",
    ):
        monkeypatch.delenv(name, raising=False)

    path = tmp_path / "config.json"
    original = AppConfig(model="qwen-test")
    original.prompts["algorithm"] = "custom prompt"
    save_config(original, path)

    loaded = load_config(path)
    assert loaded.model == "qwen-test"
    assert loaded.prompts["algorithm"] == "custom prompt"
    assert loaded.temperatures["algorithm"] == 0.2
    assert loaded.reasoning_efforts["algorithm"] == "none"
    assert loaded.max_tokens["algorithm"] == 768


def test_invalid_numbers_fall_back_or_clamp() -> None:
    config = AppConfig.from_mapping(
        {"top_p": 8, "timeout_seconds": "bad", "temperatures": {"algorithm": -3}}
    )
    assert config.top_p == 1.0
    assert config.timeout_seconds == 120.0
    assert config.temperatures["algorithm"] == 0.0
