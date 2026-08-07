from __future__ import annotations

import argparse
import json
from pathlib import Path
import sys
import tempfile
import threading
import unittest
from urllib import error as urllib_error
from urllib import request as urllib_request


ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))

from browser_bridge import (  # noqa: E402
    BridgeHandler,
    BridgeServer,
    join_upstream,
    ollama_chat_payload,
    validate_upstream,
)


class BrowserBridgeValidationTests(unittest.TestCase):
    def test_hosted_ui_declares_the_companion_as_loopback(self) -> None:
        app_source = (ROOT / "web-demo" / "app.js").read_text(encoding="utf-8")
        self.assertIn('targetAddressSpace: "loopback"', app_source)
        self.assertNotIn('targetAddressSpace: "local"', app_source)

    def test_accepts_loopback_model_servers(self) -> None:
        self.assertEqual(validate_upstream("http://127.0.0.1:11434/"), "http://127.0.0.1:11434")
        self.assertEqual(validate_upstream("http://localhost:1234/v1"), "http://localhost:1234/v1")

    def test_rejects_remote_or_credentialed_upstreams(self) -> None:
        for value in (
            "https://api.openai.com/v1",
            "http://192.168.1.10:11434",
            "http://user:secret@127.0.0.1:11434",
        ):
            with self.subTest(value=value), self.assertRaises(argparse.ArgumentTypeError):
                validate_upstream(value)

    def test_rejects_https_even_on_loopback(self) -> None:
        with self.assertRaises(argparse.ArgumentTypeError):
            validate_upstream("https://127.0.0.1:11434")

    def test_preserves_openai_compatible_base_paths(self) -> None:
        self.assertEqual(
            join_upstream("http://localhost:1234/v1", "/v1/chat/completions"),
            "http://localhost:1234/v1/chat/completions",
        )

    def test_native_ollama_tutor_turn_disables_unbounded_thinking(self) -> None:
        payload = ollama_chat_payload(
            {"model": "qwen3.5:9b", "messages": [{"role": "user", "content": "hint"}], "max_tokens": 5000}
        )
        self.assertFalse(payload["think"])
        self.assertFalse(payload["stream"])
        self.assertEqual(payload["options"]["num_predict"], 1024)


class BrowserBridgeAppTests(unittest.TestCase):
    def test_catalog_and_real_code_runner_are_available_to_first_party_ui(self) -> None:
        with tempfile.TemporaryDirectory() as temporary:
            server = BridgeServer(("127.0.0.1", 0), BridgeHandler)
            server.upstream = "http://127.0.0.1:9"
            server.allowed_origins = frozenset({"https://tonytan.me"})
            server.project_root = ROOT
            server.progress_path = Path(temporary) / "progress.json"
            thread = threading.Thread(target=server.serve_forever, daemon=True)
            thread.start()
            base_url = f"http://127.0.0.1:{server.server_port}"
            try:
                request = urllib_request.Request(
                    base_url + "/api/catalog",
                    headers={"Origin": "https://tonytan.me"},
                )
                with urllib_request.urlopen(request, timeout=3) as response:
                    payload = json.load(response)
                    self.assertTrue(payload["problems"])
                    self.assertTrue(payload["system_design"])
                    self.assertEqual(
                        response.headers["Access-Control-Allow-Origin"],
                        "https://tonytan.me",
                    )

                body = json.dumps(
                    {
                        "source": "class Solution:\n    def add(self, a, b):\n        return a + b\n",
                        "method_name": "add",
                        "test_cases": '[{"args":[2,3],"expected":5}]',
                    }
                ).encode()
                request = urllib_request.Request(
                    base_url + "/api/code/run",
                    data=body,
                    method="POST",
                    headers={
                        "Origin": "https://tonytan.me",
                        "Content-Type": "application/json",
                    },
                )
                with urllib_request.urlopen(request, timeout=5) as response:
                    payload = json.load(response)
                    self.assertEqual(payload["result"]["status"], "passed")
                    self.assertEqual(payload["result"]["summary"], "1/1 个测试通过")

                request = urllib_request.Request(
                    base_url + "/api/catalog",
                    headers={"Origin": "https://attacker.example"},
                )
                with self.assertRaises(urllib_error.HTTPError) as forbidden:
                    urllib_request.urlopen(request, timeout=3)
                self.assertEqual(forbidden.exception.code, 403)
            finally:
                server.shutdown()
                server.server_close()
                thread.join(timeout=3)


if __name__ == "__main__":
    unittest.main()
