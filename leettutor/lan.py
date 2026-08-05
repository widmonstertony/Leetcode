"""Helpers for safe same-Wi-Fi access to the Streamlit app."""

from __future__ import annotations

import base64
import hashlib
import hmac
import ipaddress
import os
import secrets
import socket
import time
from io import BytesIO
from pathlib import Path


TRUSTED_DEVICE_TTL_SECONDS = 30 * 24 * 60 * 60
TRUSTED_DEVICE_COOKIE_NAME = "leettutor_host_trust_v1"


def generate_access_code() -> str:
    """Return an easy-to-type code with enough entropy for a trusted LAN."""

    return f"{secrets.randbelow(100_000_000):08d}"


def access_code_matches(expected: str, supplied: str) -> bool:
    normalized = "".join(supplied.split())
    return bool(expected) and hmac.compare_digest(expected, normalized)


def load_or_create_trust_secret(path: Path) -> str:
    """Return the host's private signing secret, creating it owner-only."""

    path.parent.mkdir(parents=True, exist_ok=True)
    try:
        existing = path.read_text(encoding="utf-8").strip()
    except FileNotFoundError:
        existing = ""
    if len(existing) >= 32:
        return existing

    generated = secrets.token_urlsafe(32)
    flags = os.O_WRONLY | os.O_CREAT | os.O_EXCL
    try:
        descriptor = os.open(path, flags, 0o600)
    except FileExistsError:
        # Another launcher may have won the race. Keep its valid secret; repair
        # a truncated file only when there is no usable value to preserve.
        existing = path.read_text(encoding="utf-8").strip()
        if len(existing) >= 32:
            return existing
        path.write_text(generated + "\n", encoding="utf-8")
    else:
        with os.fdopen(descriptor, "w", encoding="utf-8") as handle:
            handle.write(generated + "\n")
    try:
        path.chmod(0o600)
    except OSError:
        pass
    return generated


def create_trusted_device_token(secret: str, *, now: int | None = None) -> str:
    """Issue an opaque browser token signed by this LeetTutor host."""

    issued_at = int(time.time() if now is None else now)
    payload = f"v1.{issued_at}.{secrets.token_urlsafe(18)}"
    signature = hmac.new(
        secret.encode("utf-8"), payload.encode("utf-8"), hashlib.sha256
    ).digest()
    encoded_signature = base64.urlsafe_b64encode(signature).decode("ascii").rstrip("=")
    return f"{payload}.{encoded_signature}"


def trusted_device_token_matches(
    secret: str,
    token: str,
    *,
    now: int | None = None,
    ttl_seconds: int = TRUSTED_DEVICE_TTL_SECONDS,
) -> bool:
    """Validate a signed browser token and its rolling trust window."""

    if not secret or not token or ttl_seconds <= 0:
        return False
    parts = token.split(".")
    if len(parts) != 4 or parts[0] != "v1" or not parts[1].isdigit():
        return False
    issued_at = int(parts[1])
    current_time = int(time.time() if now is None else now)
    if issued_at > current_time + 300 or current_time - issued_at > ttl_seconds:
        return False
    payload = ".".join(parts[:3])
    signature = hmac.new(
        secret.encode("utf-8"), payload.encode("utf-8"), hashlib.sha256
    ).digest()
    expected = base64.urlsafe_b64encode(signature).decode("ascii").rstrip("=")
    return hmac.compare_digest(expected, parts[3])


def find_lan_ipv4() -> str | None:
    """Best-effort discovery of the IPv4 address used by the default route."""

    candidates: list[str] = []
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        sock.connect(("192.0.2.1", 80))
        candidates.append(sock.getsockname()[0])
    except OSError:
        pass
    finally:
        sock.close()

    try:
        for info in socket.getaddrinfo(socket.gethostname(), None, socket.AF_INET):
            candidates.append(info[4][0])
    except OSError:
        pass

    for raw in candidates:
        try:
            address = ipaddress.ip_address(raw)
        except ValueError:
            continue
        if address.version == 4 and not address.is_loopback and not address.is_link_local:
            return str(address)
    return None


def build_lan_url(host: str, port: int = 8501) -> str:
    return f"http://{host}:{port}"


def qr_png_data_url(value: str) -> str:
    """Render a compact QR code in memory for the desktop setup panel."""

    import qrcode

    image = qrcode.make(value)
    buffer = BytesIO()
    image.save(buffer, format="PNG")
    encoded = base64.b64encode(buffer.getvalue()).decode("ascii")
    return f"data:image/png;base64,{encoded}"
