"""OpenAI-compatible adapter for local model servers."""

from __future__ import annotations

import json
from dataclasses import dataclass
from typing import Any, Callable, Iterator, Literal, Sequence
from urllib.parse import urlsplit, urlunsplit

import requests
from openai import (
    APIConnectionError,
    APIStatusError,
    APITimeoutError,
    AuthenticationError,
    BadRequestError,
    OpenAI,
)

from .metal_runtime import AMD_METAL_API_KEY, AMD_METAL_PROVIDER
from .providers import GEMINI_PROVIDER, OPENAI_PROVIDER, is_cloud_provider


class LocalLLMError(RuntimeError):
    """A local-model failure with an actionable message for the UI."""


@dataclass(frozen=True)
class ProviderSettings:
    provider: str
    endpoint: str
    api_key: str = ""
    timeout_seconds: float = 120.0
    context_tokens: int = 4096
    keep_alive: str = "10m"


@dataclass(frozen=True)
class ChatDelta:
    kind: Literal["thinking", "content"]
    content: str


def normalize_base_url(provider: str, endpoint: str) -> str:
    """Normalize a provider URL to its OpenAI-compatible `/v1` base."""

    value = endpoint.strip()
    if not value:
        raise ValueError("API Endpoint 不能为空。")
    if "://" not in value:
        value = f"http://{value}"

    parsed = urlsplit(value)
    if parsed.scheme not in {"http", "https"} or not parsed.netloc:
        raise ValueError("API Endpoint 必须是有效的 http(s) 地址。")

    path = parsed.path.rstrip("/")
    if provider == GEMINI_PROVIDER:
        if not path.endswith("/openai"):
            raise ValueError("Gemini API Endpoint 必须以 /openai 结尾。")
    elif not path.endswith("/v1"):
        path = f"{path}/v1" if path else "/v1"
    return urlunsplit((parsed.scheme, parsed.netloc, path, "", ""))


class LocalLLMClient:
    """Small testable wrapper around the official OpenAI Python client."""

    def __init__(
        self,
        settings: ProviderSettings,
        *,
        client_factory: Callable[..., Any] = OpenAI,
    ) -> None:
        self.settings = settings
        try:
            base_url = normalize_base_url(settings.provider, settings.endpoint)
        except ValueError as exc:
            raise LocalLLMError(str(exc)) from exc

        if is_cloud_provider(settings.provider) and not settings.api_key.strip():
            raise LocalLLMError(
                f"请先为 {settings.provider} 填写 API Key。网页会员登录不能代替 API Key。"
            )

        if settings.provider == "Ollama":
            default_key = "ollama"
        elif settings.provider == AMD_METAL_PROVIDER:
            default_key = AMD_METAL_API_KEY
        elif settings.provider == "LM Studio":
            default_key = "lm-studio"
        else:
            default_key = settings.api_key
        self._client = client_factory(
            base_url=base_url,
            api_key=settings.api_key or default_key,
            timeout=settings.timeout_seconds,
            max_retries=0,
        )

    def list_models(self) -> list[str]:
        """Return model IDs exposed by the selected local endpoint."""

        try:
            response = self._client.models.list()
            return sorted(
                {
                    str(item.id)
                    for item in getattr(response, "data", [])
                    if getattr(item, "id", None)
                },
                key=str.casefold,
            )
        except Exception as exc:  # SDK has several transport/status subclasses.
            raise self._friendly_error(exc) from exc

    def stream_chat(
        self,
        *,
        messages: Sequence[dict[str, str]],
        model: str,
        temperature: float,
        top_p: float,
        reasoning_effort: str = "none",
        max_tokens: int = 512,
    ) -> Iterator[ChatDelta]:
        """Yield text deltas from a local chat-completions stream."""

        if not model.strip():
            raise LocalLLMError("请先在侧边栏选择或输入模型名称。")

        if self.settings.provider == "Ollama":
            yield from self._stream_ollama_native(
                messages=messages,
                model=model,
                temperature=temperature,
                top_p=top_p,
                reasoning_effort=reasoning_effort,
                max_tokens=max_tokens,
            )
            return

        try:
            request: dict[str, Any] = {
                "model": model.strip(),
                "messages": list(messages),
                "stream": True,
            }
            folded_model = model.casefold()
            if self.settings.provider == OPENAI_PROVIDER:
                request["max_completion_tokens"] = max_tokens
                if not folded_model.startswith("gpt-5"):
                    request["temperature"] = temperature
                    request["top_p"] = top_p
            else:
                request["max_tokens"] = max_tokens
                request["temperature"] = temperature
                request["top_p"] = top_p
            if reasoning_effort != "none" and self.settings.provider != AMD_METAL_PROVIDER:
                # Hosted and recent LM Studio OpenAI-compatible endpoints
                # accept the standard reasoning-effort field.
                request["reasoning_effort"] = reasoning_effort
            if "qwen3" in folded_model and self.settings.provider in {
                "LM Studio",
                AMD_METAL_PROVIDER,
            }:
                # Qwen 3.x chat templates expose a boolean thinking switch.
                # Passing it alongside reasoning_effort keeps both recent and
                # older llama.cpp-based LM Studio runtimes compatible.
                request["extra_body"] = {
                    "chat_template_kwargs": {
                        "enable_thinking": reasoning_effort != "none"
                        and self.settings.provider != AMD_METAL_PROVIDER
                    }
                }
            stream = self._client.chat.completions.create(
                **request,
            )
            for chunk in stream:
                choices = getattr(chunk, "choices", None) or []
                if not choices:
                    continue
                delta = choices[0].delta
                thinking = (
                    getattr(delta, "reasoning_content", None)
                    or getattr(delta, "reasoning", None)
                    or getattr(delta, "thinking", None)
                )
                if thinking:
                    yield ChatDelta("thinking", str(thinking))
                content = getattr(delta, "content", None)
                if content:
                    yield ChatDelta("content", str(content))
        except Exception as exc:  # Includes errors raised during stream iteration.
            raise self._friendly_error(exc) from exc

    def _stream_ollama_native(
        self,
        *,
        messages: Sequence[dict[str, str]],
        model: str,
        temperature: float,
        top_p: float,
        reasoning_effort: str,
        max_tokens: int,
    ) -> Iterator[ChatDelta]:
        """Use Ollama native chat so `think: false` is actually honored.

        Ollama's OpenAI-compatible endpoint accepts reasoning controls, but
        some DeepSeek versions still spend the entire token budget on hidden
        reasoning. The native endpoint provides the reliable `think` switch
        and separate `message.thinking` stream documented by Ollama.
        """

        parsed = urlsplit(normalize_base_url("Ollama", self.settings.endpoint))
        path = parsed.path.removesuffix("/v1") + "/api/chat"
        url = urlunsplit((parsed.scheme, parsed.netloc, path, "", ""))
        if model.casefold().startswith("gpt-oss") and reasoning_effort != "none":
            think: bool | str = reasoning_effort
        else:
            think = reasoning_effort != "none"

        payload = {
            "model": model.strip(),
            "messages": list(messages),
            "stream": True,
            "think": think,
            "keep_alive": self.settings.keep_alive,
            "options": {
                "temperature": temperature,
                "top_p": top_p,
                "num_predict": max_tokens,
                "num_ctx": self.settings.context_tokens,
            },
        }
        try:
            with requests.post(
                url,
                json=payload,
                stream=True,
                timeout=(5, self.settings.timeout_seconds),
            ) as response:
                response.raise_for_status()
                for line in response.iter_lines(decode_unicode=True):
                    if not line:
                        continue
                    try:
                        event = json.loads(line)
                    except json.JSONDecodeError as exc:
                        raise LocalLLMError("Ollama 返回了无法解析的流式数据。") from exc
                    if event.get("error"):
                        raise LocalLLMError(f"Ollama 调用失败：{event['error']}")
                    message = event.get("message", {})
                    thinking = message.get("thinking")
                    if thinking:
                        yield ChatDelta("thinking", str(thinking))
                    content = message.get("content")
                    if content:
                        yield ChatDelta("content", str(content))
        except requests.ConnectionError as exc:
            raise LocalLLMError(
                "无法连接到 Ollama。请在“硬件检测与模型安装”中启动服务。"
            ) from exc
        except requests.Timeout as exc:
            raise LocalLLMError(
                "Ollama 在等待下一个输出片段时超过 "
                f"{self.settings.timeout_seconds:g} 秒。模型冷启动或长题面预填充仍在进行；"
                "请保持“根据硬件和模型自动调优”开启，或手动提高 Timeout。"
            ) from exc
        except requests.HTTPError as exc:
            status = exc.response.status_code if exc.response is not None else "未知"
            detail = ""
            if exc.response is not None:
                try:
                    detail = str(exc.response.json().get("error", ""))
                except (ValueError, AttributeError):
                    detail = ""
            suffix = f"：{detail}" if detail else ""
            raise LocalLLMError(f"Ollama 返回 HTTP {status}{suffix}") from exc
        except requests.RequestException as exc:
            raise LocalLLMError(f"Ollama 调用失败：{exc}") from exc

    def _friendly_error(self, exc: Exception) -> LocalLLMError:
        provider = self.settings.provider
        # APITimeoutError inherits from APIConnectionError in the OpenAI SDK,
        # so the more specific case must be checked first.
        if isinstance(exc, APITimeoutError):
            return LocalLLMError(
                f"{provider} 响应超时。模型可能仍在加载；可稍后重试或调高 Timeout。"
            )
        if isinstance(exc, APIConnectionError):
            if provider == "Ollama":
                hint = "请先启动 Ollama（可在终端运行 `ollama serve`），再确认地址和端口。"
            elif provider == AMD_METAL_PROVIDER:
                hint = "请用 run.command 启动应用，并查看 .leettutor/amd-metal-server.log。"
            elif provider == OPENAI_PROVIDER:
                hint = "请检查主机的互联网连接，以及 OpenAI Endpoint 是否保持官方地址。"
            elif provider == GEMINI_PROVIDER:
                hint = "请检查主机的互联网连接，以及 Gemini Endpoint 是否保持官方地址。"
            else:
                hint = "请在 LM Studio 的 Developer / Local Server 页面加载模型并启动服务器。"
            return LocalLLMError(f"无法连接到 {provider}。{hint}")
        if isinstance(exc, AuthenticationError):
            if is_cloud_provider(provider):
                return LocalLLMError(
                    f"{provider} 拒绝了 API Key。请重新复制密钥，并检查对应 API 项目的额度或账单状态。"
                )
            return LocalLLMError("本地端点拒绝了 API Key，请检查侧边栏或 .env 设置。")
        if isinstance(exc, BadRequestError):
            if is_cloud_provider(provider):
                return LocalLLMError(
                    f"{provider} 拒绝了请求。请检查模型名称，以及账号是否有权使用该模型。"
                )
            return LocalLLMError(
                "本地模型拒绝了请求。请检查模型名称，以及它是否支持 Chat Completions。"
            )
        if isinstance(exc, APIStatusError):
            return LocalLLMError(
                f"{provider} 返回 HTTP {exc.status_code}。请检查 Endpoint、模型名称、额度和服务状态。"
            )
        if isinstance(exc, (ConnectionError, OSError)):
            return LocalLLMError(f"连接本地模型时出错：{exc}")
        return LocalLLMError(f"{provider} 调用失败：{exc}")
