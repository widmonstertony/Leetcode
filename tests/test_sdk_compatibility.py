"""Exercise the real installed SDK without keys, external requests or model costs."""

import json

import httpx2
from openai import OpenAI

from leettutor.llm_client import ChatDelta, LocalLLMClient, ProviderSettings


def test_real_sdk_lists_models_and_parses_stream_without_network() -> None:
    seen = []

    def respond(request):
        seen.append(request)
        if request.url.path == "/v1/models":
            return httpx2.Response(200, json={
                "object": "list", "data": [{"id": "qa-model", "object": "model"}],
            })
        assert request.url.path == "/v1/chat/completions"
        body = json.loads(request.content)
        assert body["stream"] is True
        assert body["max_tokens"] == 32
        assert body["messages"] == [{"role": "user", "content": "hello"}]
        chunk = {
            "id": "qa", "object": "chat.completion.chunk", "created": 0,
            "model": "qa-model", "choices": [{"index": 0,
                "delta": {"content": "hello"}, "finish_reason": None}],
        }
        return httpx2.Response(200, headers={"content-type": "text/event-stream"},
            content=f"data: {json.dumps(chunk)}\n\ndata: [DONE]\n\n")

    transport = httpx2.MockTransport(respond)
    sdk = OpenAI(api_key="qa-not-a-real-key", base_url="https://sdk-qa.invalid/v1",
                 http_client=httpx2.Client(transport=transport), max_retries=0)
    try:
        adapter = LocalLLMClient(
            ProviderSettings("LM Studio", "https://sdk-qa.invalid/v1"),
            client_factory=lambda **kwargs: sdk,
        )
        assert adapter.list_models() == ["qa-model"]
        assert list(adapter.stream_chat(
            messages=[{"role": "user", "content": "hello"}], model="qa-model",
            temperature=0.2, top_p=0.9, max_tokens=32,
        )) == [ChatDelta("content", "hello")]
        assert len(seen) == 2
    finally:
        sdk.close()
