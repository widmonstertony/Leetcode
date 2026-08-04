"""Model-management helpers for local providers."""

from __future__ import annotations

import json
from dataclasses import dataclass
from typing import Iterator
from urllib.parse import urlsplit, urlunsplit

import requests


class ModelDownloadError(RuntimeError):
    """An actionable model-download failure."""


@dataclass(frozen=True)
class PullProgress:
    status: str
    completed: int = 0
    total: int = 0

    @property
    def fraction(self) -> float:
        if self.total <= 0:
            return 0.0
        return min(max(self.completed / self.total, 0.0), 1.0)


def ollama_native_url(endpoint: str, path: str) -> str:
    value = endpoint.strip()
    if "://" not in value:
        value = f"http://{value}"
    parsed = urlsplit(value)
    if parsed.scheme not in {"http", "https"} or not parsed.netloc:
        raise ModelDownloadError("Ollama Endpoint 不是有效的 http(s) 地址。")
    base_path = parsed.path.rstrip("/")
    if base_path.endswith("/v1"):
        base_path = base_path[:-3]
    native_path = f"{base_path}/{path.lstrip('/')}"
    return urlunsplit((parsed.scheme, parsed.netloc, native_path, "", ""))


def pull_ollama_model(endpoint: str, model: str) -> Iterator[PullProgress]:
    """Download through Ollama's streaming `/api/pull` endpoint."""

    if not model.strip():
        raise ModelDownloadError("模型名称不能为空。")
    url = ollama_native_url(endpoint, "/api/pull")
    try:
        with requests.post(
            url,
            json={"model": model.strip(), "stream": True},
            stream=True,
            timeout=(5, 3600),
        ) as response:
            response.raise_for_status()
            saw_update = False
            for line in response.iter_lines(decode_unicode=True):
                if not line:
                    continue
                saw_update = True
                try:
                    payload = json.loads(line)
                except json.JSONDecodeError as exc:
                    raise ModelDownloadError("Ollama 返回了无法解析的下载进度。") from exc
                if payload.get("error"):
                    raise ModelDownloadError(f"Ollama 下载失败：{payload['error']}")
                yield PullProgress(
                    status=str(payload.get("status", "下载中")),
                    completed=int(payload.get("completed", 0) or 0),
                    total=int(payload.get("total", 0) or 0),
                )
            if not saw_update:
                raise ModelDownloadError("Ollama 没有返回下载进度，请检查服务日志。")
    except requests.ConnectionError as exc:
        raise ModelDownloadError(
            "无法连接 Ollama。请先安装并启动 Ollama，再重试下载。"
        ) from exc
    except requests.Timeout as exc:
        raise ModelDownloadError("模型下载超时；已下载的分层通常可以在重试时复用。") from exc
    except requests.HTTPError as exc:
        status = exc.response.status_code if exc.response is not None else "未知"
        raise ModelDownloadError(f"Ollama 下载接口返回 HTTP {status}。") from exc
    except requests.RequestException as exc:
        raise ModelDownloadError(f"模型下载失败：{exc}") from exc
