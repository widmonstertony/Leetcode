from pathlib import Path

from streamlit.testing.v1 import AppTest


def test_app_starts_without_contacting_a_model() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)
    assert not app.exception
    assert "导师给我下一题" in [button.label for button in app.button]
    assert "发送给小沐" in [button.label for button in app.button]
    assert "适合这台设备的模型" in [item.label for item in app.selectbox]


def test_streamlit_developer_shortcuts_are_disabled() -> None:
    project_root = Path(__file__).resolve().parents[1]
    config = (project_root / ".streamlit" / "config.toml").read_text(
        encoding="utf-8"
    )
    launcher = (project_root / "scripts" / "launch.py").read_text(encoding="utf-8")
    assert 'toolbarMode = "minimal"' in config
    assert "--client.toolbarMode=minimal" in launcher
