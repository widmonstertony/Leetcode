from pathlib import Path

from streamlit.testing.v1 import AppTest


def test_app_starts_without_contacting_a_model() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)
    assert not app.exception
    assert "导师给我下一题" in [button.label for button in app.button]
    assert "发送给小沐" in [button.label for button in app.button]
    assert "适合这台设备的模型" in [item.label for item in app.selectbox]

    assert "根据硬件和模型自动调优" in [item.label for item in app.toggle]
    assert "上下文 Tokens" in [item.label for item in app.number_input]



def test_chat_submission_renders_activity_ui_without_a_selected_model() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)
    app.chat_input[0].set_value("先提示我下一步").run(timeout=20)

    assert not app.exception
    assert any("模型调用失败" in status.label for status in app.status)
    assert any("先提示我下一步" in item.value for item in app.markdown)


def test_streamlit_developer_shortcuts_are_disabled() -> None:
    project_root = Path(__file__).resolve().parents[1]
    config = (project_root / ".streamlit" / "config.toml").read_text(
        encoding="utf-8"
    )
    launcher = (project_root / "scripts" / "launch.py").read_text(encoding="utf-8")
    assert 'toolbarMode = "minimal"' in config
    assert "--client.toolbarMode=minimal" in launcher


def test_floating_mentor_keeps_response_and_scroll_inside_popover() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    source = app_path.read_text(encoding="utf-8")

    assert 'surface="floating"' in source
    assert "scrollRoot.scrollTo" in source
    assert "floating_pending" not in source
