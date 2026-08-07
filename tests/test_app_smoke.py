from pathlib import Path

from streamlit.testing.v1 import AppTest

from app import _build_api_messages, _format_coaching_turn, _tutor_output_limit


def test_app_starts_without_contacting_a_model() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)
    assert not app.exception
    assert "JARVIS 给我下一题" in [button.label for button in app.button]
    assert "发送给 JARVIS" in [button.label for button in app.button]
    assert "适合这台设备的模型" in [item.label for item in app.selectbox]

    assert "根据硬件和模型自动调优" in [item.label for item in app.toggle]
    assert "上下文 Tokens" in [item.label for item in app.number_input]



def test_chat_submission_is_preserved_without_a_selected_model() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)
    model = next(item for item in app.text_input if item.label == "Model Name")
    model.set_value("").run(timeout=20)
    question = next(item for item in app.text_area if item.label == "继续回复 JARVIS")
    send = next(item for item in app.button if item.label == "发送给 JARVIS")
    question.set_value("先提示我下一步")
    send.click().run(timeout=20)

    assert not app.exception
    assert any(
        "先在左侧选择或填写本地模型" in warning.value
        for warning in app.warning
    )
    assert any("先提示我下一步" in item.value for item in app.markdown)
    cleared_question = next(
        item for item in app.text_area if item.label == "继续回复 JARVIS"
    )
    assert cleared_question.value == ""


def test_streamlit_developer_shortcuts_are_disabled() -> None:
    project_root = Path(__file__).resolve().parents[1]
    config = (project_root / ".streamlit" / "config.toml").read_text(
        encoding="utf-8"
    )
    launcher = (project_root / "scripts" / "launch.py").read_text(encoding="utf-8")
    assert 'toolbarMode = "viewer"' in config
    assert "--client.toolbarMode=viewer" in launcher


def test_lan_launcher_is_explicit_and_password_protected() -> None:
    project_root = Path(__file__).resolve().parents[1]
    launcher = (project_root / "scripts" / "launch.py").read_text(encoding="utf-8")
    run_lan = project_root / "run-lan.command"
    run_lan_windows = project_root / "run-lan.bat"

    assert run_lan.is_file()
    assert run_lan_windows.is_file()
    assert run_lan.stat().st_mode & 0o111
    assert "scripts/launch.py --lan" in run_lan.read_text(encoding="utf-8")
    assert "scripts\\launch.py --lan" in run_lan_windows.read_text(encoding="utf-8")
    assert 'server_address = "0.0.0.0"' in launcher
    assert 'environment["LEETTUTOR_ACCESS_CODE"]' in launcher
    assert 'environment.setdefault(\n            "LEETTUTOR_LAN_TRUST_SECRET"' in launcher
    assert "首次验证后，可在该浏览器记住此主机 30 天" in launcher
    assert "请勿把端口映射到公网" in launcher


def test_hosted_launcher_runs_the_original_app_without_opening_another_tab() -> None:
    project_root = Path(__file__).resolve().parents[1]
    launcher = (project_root / "scripts" / "launch.py").read_text(encoding="utf-8")
    companion_macos = (project_root / "launch_companion.command").read_text(
        encoding="utf-8"
    )
    companion_windows = (project_root / "launch_companion.bat").read_text(
        encoding="utf-8"
    )

    assert '"--hosted"' in launcher
    assert 'server_address = "127.0.0.1" if args.hosted else "localhost"' in launcher
    assert "--server.headless={'true' if args.hosted else 'false'}" in launcher
    assert "scripts/launch.py --hosted" in companion_macos
    assert "scripts\\launch.py --hosted" in companion_windows


def test_lan_access_gate_runs_before_the_workspace(monkeypatch) -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    monkeypatch.setenv("LEETTUTOR_ACCESS_CODE", "12345678")
    monkeypatch.setenv("LEETTUTOR_LAN_MODE", "1")
    monkeypatch.setenv("LEETTUTOR_LAN_URL", "http://192.168.1.25:8501")
    monkeypatch.setenv(
        "LEETTUTOR_LAN_TRUST_SECRET",
        "host-secret-that-is-long-enough-for-smoke-tests",
    )

    app = AppTest.from_file(str(app_path)).run(timeout=20)
    assert not app.exception
    assert "进入 LeetTutor" in [button.label for button in app.button]
    assert "JARVIS 给我下一题" not in [button.label for button in app.button]
    assert "在此浏览器记住这台主机 30 天" in [
        checkbox.label for checkbox in app.checkbox
    ]

    code = next(item for item in app.text_input if item.label == "本次访问码")
    code.set_value("12345678")
    next(item for item in app.button if item.label == "进入 LeetTutor").click().run(
        timeout=20
    )
    assert not app.exception
    assert "JARVIS 给我下一题" in [button.label for button in app.button]


def test_lan_trusted_browser_controller_uses_a_signed_cookie_not_the_code() -> None:
    source = (
        Path(__file__).resolve().parents[1] / "app.py"
    ).read_text(encoding="utf-8")

    assert "const cookieName = __COOKIE_NAME__" in source
    assert "SameSite=Strict" in source
    assert "Max-Age=${maxAge}" in source
    assert "st.context.cookies.get(TRUSTED_DEVICE_COOKIE_NAME" in source
    assert "create_trusted_device_token(trust_secret)" in source
    assert "document.cookie = expected" not in source


def test_cloud_provider_has_safe_key_and_model_controls() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)

    provider = next(item for item in app.selectbox if item.label == "API Provider")
    provider.set_value("OpenAI API").run(timeout=20)

    assert not app.exception
    api_key = next(item for item in app.text_input if item.label == "API Key")
    assert api_key.value == ""
    model = next(item for item in app.text_input if item.label == "Model Name")
    assert model.value == "gpt-5.6-terra"
    rendered = " ".join(item.value for item in app.info)
    assert "ChatGPT Plus/Pro" in rendered


def test_ui_and_problem_statement_languages_can_switch_to_english() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)

    ui_language = next(
        item for item in app.selectbox if item.label == "界面语言"
    )
    ui_language.set_value("en").run(timeout=20)

    assert not app.exception
    assert "JARVIS, next problem" in [
        button.label for button in app.button
    ]
    assert "LeetCode statement" in [item.label for item in app.selectbox]
    assert "choose System, Light, or Dark" in " ".join(
        item.value for item in app.caption
    )


def test_theme_follows_system_and_top_spacing_is_compact() -> None:
    project_root = Path(__file__).resolve().parents[1]
    config = (project_root / ".streamlit" / "config.toml").read_text(
        encoding="utf-8"
    )
    source = (project_root / "app.py").read_text(encoding="utf-8")

    assert "[theme]" not in config
    assert 'height: 0 !important' in source
    assert 'pointer-events: none' in source
    assert 'padding: 0 clamp(' in source
    assert ':has(iframe)' in source
    assert ':has(iframe),' not in source
    assert 'Controller iframes must stay alive' in source
    assert 'position: absolute !important' in source
    assert 'theme="default"' in source


def test_settings_sidebar_keeps_native_resize_and_remembers_width() -> None:
    source = (
        Path(__file__).resolve().parents[1] / "app.py"
    ).read_text(encoding="utf-8")

    assert 'const storageKey = "leettutor-sidebar-width-v1"' in source
    assert "win.__leettutorSidebarResizeObserver = new win.ResizeObserver" in source
    assert 'sidebar.style.width = `${saved}px`' in source
    assert '"--leettutor-sidebar-width"' in source
    assert '"--leettutor-sidebar-half-width"' in source
    assert "width: 330px !important" not in source
    assert "min-width: 280px;" in source
    assert "max-width: min(520px, 55vw);" in source


def test_header_branding_and_jarvis_asset_are_present() -> None:
    project_root = Path(__file__).resolve().parents[1]
    app = AppTest.from_file(str(project_root / "app.py")).run(timeout=20)
    source = (project_root / "app.py").read_text(encoding="utf-8")

    markdown = " ".join(item.value for item in app.markdown)
    assert "Made by Tony" in markdown
    assert "JARVIS LEARNING SYSTEM" in markdown
    assert (project_root / "assets" / "jarvis-ai-core.png").is_file()
    assert 'with st.container(key="app_header")' in source
    assert '@keyframes jarvis-float' in source
    assert 'background: linear-gradient(90deg, currentColor 50%, transparent 50%)' in source
    assert '[data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"]' in source
    assert 'class="leettutor-product-mark"' in source
    assert "bindProductMark" in source
    assert 'opacity: 0 !important' in source
    assert 'grid-template-columns: minmax(0, 1fr) 18rem minmax(0, 1fr)' in source
    assert 'width: min(15rem, 100%) !important' in source
    assert 'height: 44px !important' in source
    assert 'margin-bottom: -0.75rem' in source
    assert 'padding: 0.35rem 0;' in source
    assert 'min-height: 52px' in source
    assert "f'<img class=\"brand-core\"" not in source
    assert "隐藏题目" in [button.label for button in app.button]
    assert "隐藏代码" in [button.label for button in app.button]


def test_system_design_mode_can_assign_a_jarvis_mission() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)

    next(item for item in app.button if item.label == "系统设计").click().run(
        timeout=20
    )
    assert "JARVIS 分配任务" in [button.label for button in app.button]
    assert "只提示下一步" in [button.label for button in app.button]
    source = app_path.read_text(encoding="utf-8")
    assert 'key="system_command_dock"' in source
    assert '"system_design_command_form"' in source
    assert 'key="system_live_panel"' in source
    assert 'mirror=mirror' in source
    assert 'state_mount=state_mount' in source
    assert 'update_mirror_phase("已完成", "Complete")' in source
    assert 'mission_col, live_col = st.columns(' in source
    assert 'height: max(340px, min(640px, calc(100vh - 14rem)))' in source

    next(item for item in app.button if item.label == "JARVIS 分配任务").click().run(
        timeout=20
    )
    rendered = " ".join(item.value for item in app.markdown)
    assert "SD-01" in rendered
    assert "全球短链接系统" in rendered
    assert any("先不要选组件" in item.value for item in app.info)
    assert "让 JARVIS 按当前进度重画" in [button.label for button in app.button]
    assert not app.chat_input
    assert "JARVIS 指令栏" in " ".join(item.value for item in app.markdown)
    assert "JARVIS LIVE" in " ".join(item.value for item in app.markdown)


def test_floating_mentor_keeps_response_and_scroll_inside_popover() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    source = app_path.read_text(encoding="utf-8")

    assert 'surface="floating"' in source
    assert 'const controllerVersion = 20' in source
    assert '__leettutorMentorClientState' in source
    assert 'for (const delay of [50, 180, 480, 1000, 1800])' in source
    assert 'mentor-has-update' in source
    assert "scrollRoot.scrollTo" in source
    assert 'key="floating_composer"' in source
    assert 'key="floating_transcript"' in source
    assert "with transcript_box:" in source
    assert 'clear_on_submit=True' in source
    assert 'mentor-resize-top-left' in source
    assert 'mentor-resize-top-right' in source
    assert 'mentor-resize-bottom-left' in source
    assert 'mentor-resize-bottom-right' in source
    assert 'mentor-resize-overlay' in source
    assert 'popover.appendChild(overlay)' in source
    assert 'finishEvent.stopImmediatePropagation()' in source
    assert 'swallowResizeClick' in source
    assert 'resize: none' in source
    assert 'overflow: auto' in source
    assert '@keyframes mentor-popover-enter' in source
    assert '--mentor-origin-x' in source
    assert 'sizeStorageKey' in source
    assert 'height=transcript_height' in source
    assert 'min-height: min(380px' in source
    assert 'position: "fixed"' in source
    assert 'z-index: 2147483000' in source
    assert 'mentor-resize-locked' in source
    assert 'popover.scrollTop = 0' in source
    assert ':has(.mentor-response-anchor)' in source
    assert '[data-testid="stTextAreaRootElement"]:focus-within' in source
    assert 'gap: 0;' in source
    assert 'a delayed/blocked controller must never make' in source
    assert 'new win.MutationObserver(() => scroll("auto"))' not in source
    assert "composer?.focus(" in source
    assert "preventScroll: true" in source
    assert "floating_pending" not in source


def test_phone_workspace_targets_iphone_13_pro_and_iphone_air() -> None:
    project_root = Path(__file__).resolve().parents[1]
    source = (project_root / "app.py").read_text(encoding="utf-8")

    assert "iPhone 13 Pro = 390x844 CSS px" in source
    assert "iPhone Air" in source and "420x912 CSS px" in source
    assert 'id = "leettutor-mobile-nav"' in source
    assert 'data-leettutor-mobile-pane="problem"' in source
    assert 'data-leettutor-mobile-pane="code"' in source
    assert 'data-leettutor-mobile-pane="mission"' in source
    assert 'data-leettutor-mobile-pane="live"' in source
    assert 'data-pane="jarvis"' in source
    assert "env(safe-area-inset-bottom)" in source
    assert "win.visualViewport?.height" in source
    assert 'popover.classList.add("mentor-mobile-sheet"' in source
    assert "if (shouldScroll && changed)" in source
    assert "grid-template-columns: repeat(2, minmax(0, 1fr)) !important" in source
    assert ".st-key-app_mode [data-testid=\"stColumn\"]" in source
    assert "overflow-x: hidden" in source


def test_algorithm_workspace_has_one_persistent_tutor_surface() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)

    headings = [item.value for item in app.markdown]
    assert any("题目" in value for value in headings)
    assert any("代码" in value for value in headings)
    assert any("JARVIS" in value for value in headings)
    assert [item.label for item in app.text_area].count("继续回复 JARVIS") == 1
    assert [item.label for item in app.text_area].count("继续问 JARVIS") == 0
    assert not app.chat_input


def test_only_latest_user_turn_keeps_full_workspace_snapshot() -> None:
    history = [
        {"role": "user", "content": "旧题面和旧代码", "display": "第一问"},
        {"role": "assistant", "content": "第一次回答"},
        {"role": "user", "content": "最新题面和最新代码", "display": "第二问"},
    ]

    assert _build_api_messages(history, "system") == [
        {"role": "system", "content": "system"},
        {"role": "user", "content": "第一问"},
        {"role": "assistant", "content": "第一次回答"},
        {"role": "user", "content": "最新题面和最新代码"},
    ]


def test_each_workspace_pane_can_hide_and_restore() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)

    for label in ("题目", "代码", "导师"):
        next(item for item in app.toggle if item.label == label).set_value(False).run(
            timeout=20
        )

    toggles = {item.label: item for item in app.toggle}
    assert not toggles["题目"].value
    assert not toggles["代码"].value
    assert not toggles["导师"].value
    assert any("三个工作面板都已隐藏" in item.value for item in app.info)

    next(item for item in app.button if item.label == "恢复默认布局").click().run(
        timeout=20
    )
    toggles = {item.label: item for item in app.toggle}
    assert toggles["题目"].value
    assert toggles["代码"].value
    assert toggles["导师"].value
    mentor_mode = next(item for item in app.radio if item.label == "导师形态")
    assert mentor_mode.value == "悬浮"


def test_mentor_switches_between_docked_and_floating_surfaces() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    app = AppTest.from_file(str(app_path)).run(timeout=20)

    mentor_mode = next(item for item in app.radio if item.label == "导师形态")
    assert mentor_mode.value == "悬浮"
    assert "停靠到右栏" in [item.label for item in app.button]
    assert "↗" not in [item.label for item in app.button]
    assert [item.label for item in app.button].count("发送给 JARVIS") == 1

    next(item for item in app.button if item.label == "停靠到右栏").click().run(
        timeout=20
    )
    mentor_mode = next(item for item in app.radio if item.label == "导师形态")
    assert mentor_mode.value == "停靠"
    assert "切换为悬浮导师" in [item.label for item in app.button]
    assert "停靠到右栏" not in [item.label for item in app.button]


def test_workspace_splitter_and_corner_magnet_are_installed() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    source = app_path.read_text(encoding="utf-8")

    assert "leettutor-workspace-split-v1" in source
    assert "__leettutorSplitControllerVersion" in source
    assert "__leettutorSplitResizeObserver = new win.ResizeObserver" in source
    assert 'layout.horizontal.style.width = "100%"' in source
    assert "调整题目和代码宽度" in source
    assert "ArrowLeft" in source and "ArrowRight" in source
    assert "leettutor-floating-mentor-position-v2" in source
    assert '"top-left"' in source and '"bottom-right"' in source
    assert "snapDistance" in source


def test_code_editor_uses_ace_with_ide_features() -> None:
    app_path = Path(__file__).resolve().parents[1] / "app.py"
    source = app_path.read_text(encoding="utf-8")

    assert "from code_editor import code_editor" in source
    assert 'shortcuts="vscode"' in source
    assert '"tabSize": 4' in source
    assert '"useSoftTabs": True' in source
    assert '"showLineNumbers": True' in source
    assert 'response_mode=["debounce", "blur"]' in source


def test_algorithm_coaching_turns_have_a_small_output_budget() -> None:
    assert _tutor_output_limit(
        mode="algorithm", display="下一步", configured=768
    ) == 256
    assert _tutor_output_limit(
        mode="algorithm", display="求最优解代码", configured=768
    ) == 768
    assert _tutor_output_limit(
        mode="system_design", display="下一步", configured=1536
    ) == 512


def test_short_coaching_turn_is_rendered_as_two_dialogue_beats() -> None:
    formatted = _format_coaching_turn(
        "提示：先比较 nums[mid] 和 nums[right]。轮到你：哪一侧还能保留最小值？"
    )

    assert formatted.startswith("**提示：**")
    assert "\n\n**轮到你：**" in formatted
