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
from leettutor.metal_runtime import AMD_METAL_API_KEY, AMD_METAL_PROVIDER
from leettutor.providers import GEMINI_PROVIDER, OPENAI_PROVIDER


@pytest.mark.parametrize(
    ("provider", "raw", "expected"),
    [
        ("Ollama", "http://localhost:11434", "http://localhost:11434/v1"),
        ("Ollama", "localhost:11434/", "http://localhost:11434/v1"),
        ("LM Studio", "http://localhost:1234/v1/", "http://localhost:1234/v1"),
        (AMD_METAL_PROVIDER, "http://127.0.0.1:11435", "http://127.0.0.1:11435/v1"),
        (OPENAI_PROVIDER, "https://api.openai.com/v1/", "https://api.openai.com/v1"),
        (
            GEMINI_PROVIDER,
            "https://generativelanguage.googleapis.com/v1beta/openai/",
            "https://generativelanguage.googleapis.com/v1beta/openai",
        ),
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


def test_cloud_provider_requires_its_own_api_key() -> None:
    with pytest.raises(LocalLLMError, match="网页会员登录不能代替 API Key"):
        LocalLLMClient(
            ProviderSettings(OPENAI_PROVIDER, "https://api.openai.com/v1"),
            client_factory=lambda **_kwargs: object(),
        )


@pytest.mark.parametrize(
    ("provider", "endpoint", "model", "expected_limit"),
    [
        (
            OPENAI_PROVIDER,
            "https://api.openai.com/v1",
            "gpt-5.6-terra",
            "max_completion_tokens",
        ),
        (
            GEMINI_PROVIDER,
            "https://generativelanguage.googleapis.com/v1beta/openai",
            "gemini-3.6-flash",
            "max_tokens",
        ),
    ],
)
def test_cloud_requests_use_provider_compatible_parameters(
    provider: str, endpoint: str, model: str, expected_limit: str
) -> None:
    class Value:
        def __init__(self, **values):
            self.__dict__.update(values)

    client_options: dict[str, object] = {}
    requests: list[dict[str, object]] = []

    class FakeClient:
        def __init__(self) -> None:
            self.models = Value(list=lambda: Value(data=[]))
            self.chat = Value(
                completions=Value(
                    create=lambda **kwargs: requests.append(kwargs) or iter([])
                )
            )

    def client_factory(**kwargs):
        client_options.update(kwargs)
        return FakeClient()

    client = LocalLLMClient(
        ProviderSettings(provider, endpoint, api_key="secret-test-key"),
        client_factory=client_factory,
    )
    list(
        client.stream_chat(
            messages=[{"role": "user", "content": "hello"}],
            model=model,
            temperature=0.2,
            top_p=0.9,
            reasoning_effort="low",
            max_tokens=1024,
        )
    )

    assert client_options["api_key"] == "secret-test-key"
    assert requests[0][expected_limit] == 1024
    assert requests[0]["reasoning_effort"] == "low"
    if provider == OPENAI_PROVIDER:
        assert "temperature" not in requests[0]
        assert "top_p" not in requests[0]
    else:
        assert requests[0]["temperature"] == 0.2
        assert requests[0]["top_p"] == 0.9


def test_amd_metal_forces_qwen_thinking_off() -> None:
    class Value:
        def __init__(self, **values):
            self.__dict__.update(values)

    requests: list[dict[str, object]] = []
    client_options: dict[str, object] = {}

    class FakeClient:
        def __init__(self) -> None:
            self.models = Value(list=lambda: Value(data=[]))
            self.chat = Value(
                completions=Value(
                    create=lambda **kwargs: requests.append(kwargs) or iter([])
                )
            )

    def client_factory(**kwargs):
        client_options.update(kwargs)
        return FakeClient()

    client = LocalLLMClient(
        ProviderSettings(AMD_METAL_PROVIDER, "http://127.0.0.1:11435/v1"),
        client_factory=client_factory,
    )
    list(
        client.stream_chat(
            messages=[{"role": "user", "content": "hello"}],
            model="qwen3.5:9b",
            temperature=0.2,
            top_p=0.9,
            reasoning_effort="high",
        )
    )

    assert client_options["api_key"] == AMD_METAL_API_KEY
    assert "reasoning_effort" not in requests[0]
    assert requests[0]["extra_body"] == {
        "chat_template_kwargs": {"enable_thinking": False}
    }


def test_stream_exposes_reasoning_as_status_event(monkeypatch) -> None:
    posted: dict[str, object] = {}
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

    def fake_post(*_args, **kwargs):
        posted.update(kwargs)
        return Response()

    monkeypatch.setattr("leettutor.llm_client.requests.post", fake_post)

    client = LocalLLMClient(
        ProviderSettings("Ollama", "http://localhost:11434", context_tokens=8192, keep_alive="30m"),
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

    payload = posted["json"]
    assert isinstance(payload, dict)
    assert payload["keep_alive"] == "30m"
    assert payload["options"]["num_ctx"] == 8192

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
