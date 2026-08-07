#!/usr/bin/env python3
"""Loopback-only bridge between the hosted LeetTutor demo and a local model.

The public site never receives prompts, code, model names, or responses. This
process binds to 127.0.0.1, accepts only the portfolio/local development
origins, and forwards two OpenAI-compatible endpoints to a loopback provider.
"""

from __future__ import annotations

import argparse
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
import re
from typing import Final
from urllib import error as urllib_error
from urllib import parse as urllib_parse
from urllib import request as urllib_request


MAX_BODY_BYTES: Final = 512 * 1024
ALLOWED_PATHS: Final = {"/v1/models", "/v1/chat/completions"}
LOCAL_ORIGIN = re.compile(r"^https?://(?:localhost|127\.0\.0\.1)(?::\d+)?$")


def validate_upstream(value: str) -> str:
    parsed = urllib_parse.urlparse(value.rstrip("/"))
    if parsed.scheme != "http" or parsed.hostname not in {"localhost", "127.0.0.1", "::1"}:
        raise argparse.ArgumentTypeError("upstream must be an http:// loopback URL")
    if parsed.username or parsed.password or parsed.query or parsed.fragment:
        raise argparse.ArgumentTypeError("upstream cannot include credentials, query, or fragment")
    return value.rstrip("/")


def join_upstream(base: str, path: str) -> str:
    """Avoid duplicating /v1 for LM Studio-style configured base URLs."""
    return base + (path[3:] if base.endswith("/v1") and path.startswith("/v1/") else path)


class BridgeServer(ThreadingHTTPServer):
    upstream: str
    allowed_origins: frozenset[str]


class BridgeHandler(BaseHTTPRequestHandler):
    server: BridgeServer
    protocol_version = "HTTP/1.1"

    def log_message(self, _format: str, *_args: object) -> None:
        return

    def _origin_allowed(self) -> bool:
        origin = self.headers.get("Origin", "")
        return not origin or origin in self.server.allowed_origins or bool(LOCAL_ORIGIN.fullmatch(origin))

    def _headers(self, length: int, content_type: str, status: HTTPStatus) -> None:
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(length))
        self.send_header("Cache-Control", "no-store")
        self.send_header("X-Content-Type-Options", "nosniff")
        origin = self.headers.get("Origin", "")
        if origin and self._origin_allowed():
            self.send_header("Access-Control-Allow-Origin", origin)
            self.send_header("Vary", "Origin")
            self.send_header("Access-Control-Allow-Private-Network", "true")
        self.end_headers()

    def _json(self, payload: object, status: HTTPStatus = HTTPStatus.OK) -> None:
        body = json.dumps(payload, ensure_ascii=False, separators=(",", ":")).encode()
        self._headers(len(body), "application/json; charset=utf-8", status)
        self.wfile.write(body)

    def do_OPTIONS(self) -> None:  # noqa: N802
        if not self._origin_allowed():
            self._json({"error": "Origin is not allowed."}, HTTPStatus.FORBIDDEN)
            return
        self.send_response(HTTPStatus.NO_CONTENT)
        origin = self.headers.get("Origin", "")
        if origin:
            self.send_header("Access-Control-Allow-Origin", origin)
            self.send_header("Vary", "Origin")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.send_header("Access-Control-Allow-Private-Network", "true")
        self.send_header("Access-Control-Max-Age", "600")
        self.send_header("Content-Length", "0")
        self.end_headers()

    def do_GET(self) -> None:  # noqa: N802
        if not self._origin_allowed():
            self._json({"error": "Origin is not allowed."}, HTTPStatus.FORBIDDEN)
            return
        path = urllib_parse.urlparse(self.path).path
        if path == "/healthz":
            self._json({"ok": True, "privacy": "loopback-only", "upstream": self.server.upstream})
            return
        if path != "/v1/models":
            self._json({"error": "Not found."}, HTTPStatus.NOT_FOUND)
            return
        self._forward("GET", path, None)

    def do_POST(self) -> None:  # noqa: N802
        if not self._origin_allowed():
            self._json({"error": "Origin is not allowed."}, HTTPStatus.FORBIDDEN)
            return
        path = urllib_parse.urlparse(self.path).path
        if path != "/v1/chat/completions":
            self._json({"error": "Not found."}, HTTPStatus.NOT_FOUND)
            return
        content_type = self.headers.get("Content-Type", "").split(";", 1)[0].casefold()
        length = int(self.headers.get("Content-Length", "0"))
        if content_type != "application/json" or not 0 < length <= MAX_BODY_BYTES:
            self._json({"error": "A bounded JSON body is required."}, HTTPStatus.BAD_REQUEST)
            return
        body = self.rfile.read(length)
        try:
            payload = json.loads(body)
        except (UnicodeDecodeError, json.JSONDecodeError):
            self._json({"error": "Invalid JSON."}, HTTPStatus.BAD_REQUEST)
            return
        if not isinstance(payload, dict) or payload.get("stream") is True:
            self._json({"error": "Non-streaming OpenAI-compatible requests are required."}, HTTPStatus.BAD_REQUEST)
            return
        self._forward("POST", path, json.dumps(payload).encode())

    def _forward(self, method: str, path: str, body: bytes | None) -> None:
        if path not in ALLOWED_PATHS:
            self._json({"error": "Endpoint is not allowed."}, HTTPStatus.FORBIDDEN)
            return
        request = urllib_request.Request(
            join_upstream(self.server.upstream, path),
            data=body,
            method=method,
            headers={"Accept": "application/json", "Content-Type": "application/json"},
        )
        try:
            with urllib_request.urlopen(request, timeout=180) as response:
                result = response.read(MAX_BODY_BYTES + 1)
                if len(result) > MAX_BODY_BYTES:
                    raise ValueError("Local model response exceeded the bridge limit.")
                self._headers(len(result), response.headers.get_content_type() or "application/json", HTTPStatus.OK)
                self.wfile.write(result)
        except urllib_error.HTTPError as exc:
            detail = exc.read(MAX_BODY_BYTES)
            self._headers(len(detail), "application/json; charset=utf-8", HTTPStatus(exc.code))
            self.wfile.write(detail)
        except (OSError, TimeoutError, ValueError) as exc:
            self._json({"error": f"Local model unavailable: {exc}"}, HTTPStatus.BAD_GATEWAY)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="LeetTutor loopback model bridge")
    parser.add_argument("--port", type=int, default=8766)
    parser.add_argument("--upstream", type=validate_upstream, default="http://127.0.0.1:11434")
    parser.add_argument(
        "--allow-origin",
        action="append",
        default=["https://tonytan.me", "https://www.tonytan.me"],
        help="Additional exact HTTPS origin. May be repeated.",
    )
    return parser


def main() -> int:
    args = build_parser().parse_args()
    if not 1024 <= args.port <= 65535:
        raise SystemExit("--port must be between 1024 and 65535")
    server = BridgeServer(("127.0.0.1", args.port), BridgeHandler)
    server.upstream = args.upstream
    server.allowed_origins = frozenset(args.allow_origin)
    print(f"LeetTutor bridge: http://127.0.0.1:{args.port} -> {args.upstream}")
    print("Prompts and responses stay between this browser and your computer.")
    try:
        server.serve_forever(poll_interval=0.25)
    except KeyboardInterrupt:
        pass
    finally:
        server.server_close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
