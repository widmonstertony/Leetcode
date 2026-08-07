from __future__ import annotations

import argparse
from pathlib import Path
import sys
import unittest


ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))

from browser_bridge import join_upstream, validate_upstream  # noqa: E402


class BrowserBridgeValidationTests(unittest.TestCase):
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


if __name__ == "__main__":
    unittest.main()
