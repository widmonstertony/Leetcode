"""Provider names, endpoints, and safe defaults shared across the app."""

from __future__ import annotations

import re

from .metal_runtime import AMD_METAL_ENDPOINT, AMD_METAL_PROVIDER


OPENAI_PROVIDER = "OpenAI API"
GEMINI_PROVIDER = "Gemini API"


def default_endpoints() -> dict[str, str]:
    return {
        "Ollama": "http://localhost:11434",
        "LM Studio": "http://localhost:1234/v1",
        AMD_METAL_PROVIDER: AMD_METAL_ENDPOINT,
        OPENAI_PROVIDER: "https://api.openai.com/v1",
        GEMINI_PROVIDER: "https://generativelanguage.googleapis.com/v1beta/openai",
    }


def default_models() -> dict[str, str]:
    return {
        "Ollama": "",
        "LM Studio": "",
        AMD_METAL_PROVIDER: "qwen3.5:9b",
        OPENAI_PROVIDER: "gpt-5.6-terra",
        GEMINI_PROVIDER: "gemini-3.6-flash",
    }


def is_cloud_provider(provider: str) -> bool:
    return provider in {OPENAI_PROVIDER, GEMINI_PROVIDER}


def provider_state_slug(provider: str) -> str:
    slug = re.sub(r"[^a-z0-9]+", "_", provider.casefold()).strip("_")
    return slug or "provider"
