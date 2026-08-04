import pytest
import httpx
from openai import APIConnectionError

from leettutor.llm_client import (
    ChatDelta,
    LocalLLMClient,
    LocalLLMError,
    ProviderSettings,
    normalize_base_url,
)


@pytest.mark.parametrize(
    ("provider", "raw", "expected"),
    [
        ("Ollama", "http://localhost:11434", "http://localhost:11434/v1"),
        ("Ollama", "localhost:11434/", "http://localhost:11434/v1"),
        ("LM Studio", "http://localhost:1234/v1/", "http://localhost:1234/v1"),
    ],
)
def test_normalize_base_url(provider: str, raw: str, expected: str) -> None:
    assert normalize_base_url(provider, raw) == expected


def test_list_and_stream_with_openai_compatible_shape() -> None:
    class Value:
        def __init__(self, **values):
            self.__dict__.update(values)

    class FakeClient:
        def __init__(self) -> None:
            self.models = Value(list=lambda: Value(data=[Value(id="z"), Value(id="a")]))
            chunks = [Value(choices=[Value(delta=Value(content="hi"))])]
            self.chat = Value(
                completions=Value(create=lambda **_kwargs: iter(chunks))
            )

    client = LocalLLMClient(
        ProviderSettings("LM Studio", "http://localhost:1234/v1"),
        client_factory=lambda **_kwargs: FakeClient(),
    )
    assert client.list_models() == ["a", "z"]
    assert list(
        client.stream_chat(
            messages=[{"role": "user", "content": "hello"}],
            model="test",
            temperature=0.2,
            top_p=0.9,
        )
    ) == [ChatDelta("content", "hi")]


def test_lm_studio_receives_qwen_thinking_controls() -> None:
    class Value:
        def __init__(self, **values):
            self.__dict__.update(values)

    requests: list[dict[str, object]] = []

    class FakeClient:
        def __init__(self) -> None:
            self.models = Value(list=lambda: Value(data=[]))
            self.chat = Value(
                completions=Value(
                    create=lambda **kwargs: requests.append(kwargs) or iter([])
                )
            )

    client = LocalLLMClient(
        ProviderSettings("LM Studio", "http://localhost:1234/v1"),
        client_factory=lambda **_kwargs: FakeClient(),
    )
    list(
        client.stream_chat(
            messages=[{"role": "user", "content": "hello"}],
            model="qwen3.5:9b",
            temperature=0.6,
            top_p=0.95,
            reasoning_effort="medium",
        )
    )
    assert requests[0]["reasoning_effort"] == "medium"
    assert requests[0]["extra_body"] == {
        "chat_template_kwargs": {"enable_thinking": True}
    }


def test_stream_exposes_reasoning_as_status_event(monkeypatch) -> None:
    class Value:
        def __init__(self, **values):
            self.__dict__.update(values)

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
                    '{"message":{"thinking":"checking","content":""}}',
                    '{"message":{"thinking":"","content":"answer"},"done":true}',
                ]
            )

    monkeypatch.setattr(
        "leettutor.llm_client.requests.post", lambda *_args, **_kwargs: Response()
    )

    client = LocalLLMClient(
        ProviderSettings("Ollama", "http://localhost:11434"),
        client_factory=lambda **_kwargs: Value(),
    )
    assert list(
        client.stream_chat(
            messages=[{"role": "user", "content": "hello"}],
            model="test",
            temperature=0.2,
            top_p=0.9,
        )
    ) == [ChatDelta("thinking", "checking"), ChatDelta("content", "answer")]


def test_connection_error_has_ollama_start_hint() -> None:
    class FailingClient:
        class Models:
            @staticmethod
            def list():
                request = httpx.Request("GET", "http://localhost:11434/v1/models")
                raise APIConnectionError(request=request)

        models = Models()

    client = LocalLLMClient(
        ProviderSettings("Ollama", "http://localhost:11434"),
        client_factory=lambda **_kwargs: FailingClient(),
    )
    with pytest.raises(LocalLLMError, match="ollama serve"):
        client.list_models()
