import json

import pytest

from leettutor.model_manager import (
    ModelDownloadError,
    ollama_native_url,
    pull_ollama_model,
)


def test_ollama_native_url_removes_openai_v1() -> None:
    assert (
        ollama_native_url("http://localhost:11434/v1", "/api/pull")
        == "http://localhost:11434/api/pull"
    )


def test_pull_parses_stream(monkeypatch) -> None:
    class Response:
        def __enter__(self):
            return self

        def __exit__(self, *_args):
            return None

        def raise_for_status(self):
            return None

        def iter_lines(self, decode_unicode: bool):
            assert decode_unicode
            return iter(
                [
                    json.dumps({"status": "pulling", "completed": 5, "total": 10}),
                    json.dumps({"status": "success"}),
                ]
            )

    monkeypatch.setattr("leettutor.model_manager.requests.post", lambda *_a, **_k: Response())
    updates = list(pull_ollama_model("localhost:11434", "test:1b"))
    assert updates[0].fraction == 0.5
    assert updates[-1].status == "success"


def test_empty_model_is_rejected() -> None:
    with pytest.raises(ModelDownloadError):
        list(pull_ollama_model("localhost:11434", ""))
