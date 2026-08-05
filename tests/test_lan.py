from leettutor.lan import (
    TRUSTED_DEVICE_TTL_SECONDS,
    access_code_matches,
    build_lan_url,
    create_trusted_device_token,
    generate_access_code,
    load_or_create_trust_secret,
    qr_png_data_url,
    trusted_device_token_matches,
)


def test_access_code_is_easy_to_type_and_constant_time_comparable() -> None:
    code = generate_access_code()
    assert len(code) == 8
    assert code.isdigit()
    assert access_code_matches(code, f" {code[:4]} {code[4:]} ")
    assert not access_code_matches(code, "00000000" if code != "00000000" else "1")


def test_lan_url_and_qr_are_generated_in_memory() -> None:
    url = build_lan_url("192.168.1.25", 8501)
    assert url == "http://192.168.1.25:8501"
    assert qr_png_data_url(url).startswith("data:image/png;base64,")


def test_trusted_browser_token_survives_host_restarts_but_expires() -> None:
    secret = "host-secret-that-is-long-enough-for-tests"
    token = create_trusted_device_token(secret, now=1_000_000)

    assert trusted_device_token_matches(secret, token, now=1_000_001)
    assert not trusted_device_token_matches("another-host-secret", token, now=1_000_001)
    assert not trusted_device_token_matches(
        secret,
        token + "tampered",
        now=1_000_001,
    )
    assert not trusted_device_token_matches(
        secret,
        token,
        now=1_000_000 + TRUSTED_DEVICE_TTL_SECONDS + 1,
    )


def test_host_trust_secret_is_persistent_and_owner_only(tmp_path) -> None:
    path = tmp_path / ".leettutor" / "lan-trust-secret"
    first = load_or_create_trust_secret(path)
    second = load_or_create_trust_secret(path)

    assert first == second
    assert len(first) >= 32
    assert path.stat().st_mode & 0o777 == 0o600
