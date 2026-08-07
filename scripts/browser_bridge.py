#!/usr/bin/env python3
"""Loopback-only companion between the hosted LeetTutor UI and local source.

The public site never receives prompts, code, model names, or responses. This
process binds to 127.0.0.1, accepts only the portfolio/local development
origins, exposes a bounded source-backed app API, and forwards the two required
OpenAI-compatible model endpoints to a loopback provider.
"""

from __future__ import annotations

import argparse
from dataclasses import asdict
from http import HTTPStatus
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
from pathlib import Path
import re
import sys
import threading
from typing import Final
from urllib import error as urllib_error
from urllib import parse as urllib_parse
from urllib import request as urllib_request


PROJECT_ROOT = Path(__file__).resolve().parents[1]
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from leettutor.code_runner import CodeValidationError, run_python_solution  # noqa: E402
from leettutor.curriculum import (  # noqa: E402
    PROBLEMS,
    ProgressError,
    ProgressStore,
    get_problem,
)
from leettutor.leetcode_client import LeetCodeImportError, fetch_problem  # noqa: E402
from leettutor.solutions import SolutionError, SolutionStore  # noqa: E402
from leettutor.system_design_curriculum import SYSTEM_DESIGN_CASES  # noqa: E402


MAX_BODY_BYTES: Final = 512 * 1024
MODEL_PATHS: Final = {"/v1/models", "/v1/chat/completions"}
APP_POST_PATHS: Final = {
    "/api/code/run",
    "/api/problems/import",
    "/api/progress",
    "/api/solutions/load",
    "/api/solutions/save",
}
LOCAL_ORIGIN = re.compile(r"^https?://(?:localhost|127\.0\.0\.1)(?::\d+)?$")
STATIC_PATHS: Final = {
    "/": ("index.html", "text/html; charset=utf-8"),
    "/index.html": ("index.html", "text/html; charset=utf-8"),
    "/app.js": ("app.js", "text/javascript; charset=utf-8"),
    "/styles.css": ("styles.css", "text/css; charset=utf-8"),
}


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


def ollama_chat_payload(payload: dict[str, object]) -> dict[str, object]:
    """Use Ollama's native no-thinking switch for prompt, bounded tutor turns."""
    try:
        requested_tokens = int(payload.get("max_tokens") or 512)
    except (TypeError, ValueError):
        requested_tokens = 512
    try:
        temperature = float(payload.get("temperature") or 0.2)
        top_p = float(payload.get("top_p") or 0.9)
    except (TypeError, ValueError):
        temperature, top_p = 0.2, 0.9
    return {
        "model": str(payload.get("model") or ""),
        "messages": payload.get("messages") or [],
        "stream": False,
        "think": False,
        "keep_alive": "5m",
        "options": {
            "temperature": max(0.0, min(temperature, 2.0)),
            "top_p": max(0.0, min(top_p, 1.0)),
            "num_predict": max(64, min(requested_tokens, 1024)),
            "num_ctx": 8192,
        },
    }


class BridgeServer(ThreadingHTTPServer):
    upstream: str
    allowed_origins: frozenset[str]
    project_root: Path
    progress_path: Path


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
        self.send_header("Referrer-Policy", "no-referrer")
        self.send_header(
            "Content-Security-Policy",
            "default-src 'self'; style-src 'self'; script-src 'self'; "
            "connect-src 'self'; img-src 'self' data:; frame-ancestors 'none'; "
            "base-uri 'none'; form-action 'self'",
        )
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
        if path in STATIC_PATHS:
            filename, content_type = STATIC_PATHS[path]
            try:
                body = (self.server.project_root / "web-demo" / filename).read_bytes()
            except OSError:
                self._json({"error": "Local UI asset unavailable."}, HTTPStatus.INTERNAL_SERVER_ERROR)
                return
            self._headers(len(body), content_type, HTTPStatus.OK)
            self.wfile.write(body)
            return
        if path == "/healthz":
            self._json({
                "ok": True,
                "privacy": "loopback-only",
                "upstream": self.server.upstream,
                "app": "LeetTutor local companion",
            })
            return
        if path == "/api/catalog":
            self._json({
                "ok": True,
                "problems": [
                    {**asdict(problem), "url": problem.url}
                    for problem in PROBLEMS
                ],
                "system_design": [asdict(item) for item in SYSTEM_DESIGN_CASES],
            })
            return
        if path == "/api/progress":
            try:
                self.server.progress_path.parent.mkdir(parents=True, exist_ok=True)
                progress = ProgressStore(self.server.progress_path).load()
            except (ProgressError, OSError) as exc:
                self._json({"ok": False, "error": str(exc)}, HTTPStatus.BAD_REQUEST)
                return
            self._json({"ok": True, "progress": progress})
            return
        if path == "/api/solutions":
            query = urllib_parse.parse_qs(urllib_parse.urlparse(self.path).query)
            language = str((query.get("language") or ["Python"])[0])
            try:
                files = SolutionStore(self.server.project_root).list_files(language)
            except (SolutionError, OSError) as exc:
                self._json({"ok": False, "error": str(exc)}, HTTPStatus.BAD_REQUEST)
                return
            self._json({"ok": True, "language": language, "files": files})
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
        if path not in MODEL_PATHS | APP_POST_PATHS:
            self._json({"error": "Not found."}, HTTPStatus.NOT_FOUND)
            return
        content_type = self.headers.get("Content-Type", "").split(";", 1)[0].casefold()
        try:
            length = int(self.headers.get("Content-Length", "0"))
        except ValueError:
            self._json({"error": "Invalid Content-Length."}, HTTPStatus.BAD_REQUEST)
            return
        if content_type != "application/json" or not 0 < length <= MAX_BODY_BYTES:
            self._json({"error": "A bounded JSON body is required."}, HTTPStatus.BAD_REQUEST)
            return
        body = self.rfile.read(length)
        try:
            payload = json.loads(body)
        except (UnicodeDecodeError, json.JSONDecodeError):
            self._json({"error": "Invalid JSON."}, HTTPStatus.BAD_REQUEST)
            return
        if not isinstance(payload, dict):
            self._json({"error": "JSON body must be an object."}, HTTPStatus.BAD_REQUEST)
            return
        if path in APP_POST_PATHS:
            self._handle_app_post(path, payload)
            return
        if payload.get("stream") is True:
            self._json({"error": "Non-streaming OpenAI-compatible requests are required."}, HTTPStatus.BAD_REQUEST)
            return
        if path == "/v1/chat/completions" and not self.server.upstream.endswith("/v1"):
            self._forward_ollama_chat(payload)
            return
        self._forward("POST", path, json.dumps(payload).encode())

    def _handle_app_post(self, path: str, payload: dict[str, object]) -> None:
        try:
            if path == "/api/code/run":
                result = run_python_solution(
                    source=str(payload.get("source") or ""),
                    method_name=str(payload.get("method_name") or ""),
                    test_cases=str(payload.get("test_cases") or ""),
                    timeout_seconds=float(payload.get("timeout_seconds") or 3.0),
                )
                self._json({"ok": True, "result": result.to_dict()})
                return
            if path == "/api/problems/import":
                problem = fetch_problem(
                    str(payload.get("reference") or ""),
                    locale=str(payload.get("locale") or "en"),
                    timeout_seconds=12,
                )
                self._json({"ok": True, "problem": problem.to_dict()})
                return
            if path == "/api/progress":
                problem = get_problem(str(payload.get("problem_id") or "0"))
                status = str(payload.get("status") or "")
                if problem is None or status not in {"in_progress", "review", "mastered"}:
                    raise ValueError("A curated problem and valid progress status are required.")
                self.server.progress_path.parent.mkdir(parents=True, exist_ok=True)
                progress = ProgressStore(self.server.progress_path).update(problem, status)
                self._json({"ok": True, "progress": progress})
                return
            store = SolutionStore(self.server.project_root)
            language = str(payload.get("language") or "Python")
            filename = str(payload.get("filename") or "")
            if path == "/api/solutions/load":
                self._json({
                    "ok": True,
                    "language": language,
                    "filename": filename,
                    "content": store.load(language, filename),
                })
                return
            if path == "/api/solutions/save":
                saved = store.save(
                    language,
                    filename,
                    str(payload.get("content") or ""),
                    overwrite=bool(payload.get("overwrite")),
                )
                self._json({"ok": True, "path": str(saved.relative_to(self.server.project_root))})
                return
            raise ValueError("Unsupported app endpoint.")
        except (CodeValidationError, LeetCodeImportError, ProgressError, SolutionError, ValueError, OSError) as exc:
            self._json({"ok": False, "error": str(exc)}, HTTPStatus.BAD_REQUEST)

    def _forward(self, method: str, path: str, body: bytes | None) -> None:
        if path not in MODEL_PATHS:
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

    def _forward_ollama_chat(self, payload: dict[str, object]) -> None:
        request = urllib_request.Request(
            self.server.upstream + "/api/chat",
            data=json.dumps(ollama_chat_payload(payload)).encode(),
            method="POST",
            headers={"Accept": "application/json", "Content-Type": "application/json"},
        )
        try:
            with urllib_request.urlopen(request, timeout=180) as response:
                raw = response.read(MAX_BODY_BYTES + 1)
            if len(raw) > MAX_BODY_BYTES:
                raise ValueError("Local model response exceeded the bridge limit.")
            native = json.loads(raw)
            content = str((native.get("message") or {}).get("content") or "")
            self._json({
                "id": "leettutor-local",
                "object": "chat.completion",
                "model": str(native.get("model") or payload.get("model") or ""),
                "choices": [{
                    "index": 0,
                    "message": {"role": "assistant", "content": content},
                    "finish_reason": str(native.get("done_reason") or "stop"),
                }],
            })
        except urllib_error.HTTPError as exc:
            detail = exc.read(MAX_BODY_BYTES)
            self._headers(len(detail), "application/json; charset=utf-8", HTTPStatus(exc.code))
            self.wfile.write(detail)
        except (OSError, TimeoutError, ValueError, json.JSONDecodeError) as exc:
            self._json({"error": f"Local model unavailable: {exc}"}, HTTPStatus.BAD_GATEWAY)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="LeetTutor loopback model bridge")
    parser.add_argument("--port", type=int, default=8766)
    parser.add_argument("--upstream", type=validate_upstream, default="http://127.0.0.1:11434")
    parser.add_argument("--open-browser", action="store_true", help="Also open the loopback fallback UI")
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
    server.project_root = PROJECT_ROOT
    server.progress_path = Path.home() / ".leettutor" / "progress.json"
    print(f"LeetTutor bridge: http://127.0.0.1:{args.port} -> {args.upstream}")
    print("Prompts and responses stay between this browser and your computer.")
    if args.open_browser:
        import webbrowser

        threading.Timer(0.6, lambda: webbrowser.open(f"http://127.0.0.1:{args.port}/")).start()
    try:
        server.serve_forever(poll_interval=0.25)
    except KeyboardInterrupt:
        pass
    finally:
        server.server_close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
