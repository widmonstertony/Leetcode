"""Streamlit entry point for LeetTutor-Local."""

from __future__ import annotations

import html
import json
import shutil
import subprocess
import time
from pathlib import Path
from typing import Literal

import streamlit as st
import streamlit.components.v1 as components

from leettutor.code_runner import (
    CodeValidationError,
    RunResult,
    run_python_solution,
)
from leettutor.config import AppConfig, ConfigError, load_config, save_config
from leettutor.curriculum import (
    TOPIC_ORDER,
    Problem,
    ProgressError,
    ProgressStore,
    choose_next_problem,
    get_problem,
    progress_summary,
)
from leettutor.hardware import (
    GenerationDefaults,
    HardwareProfile,
    detect_hardware,
    recommend_generation_defaults,
    recommend_models,
)
from leettutor.leetcode_client import (
    ImportedProblem,
    LeetCodeImportError,
    fetch_problem,
)
from leettutor.llm_client import LocalLLMClient, LocalLLMError, ProviderSettings
from leettutor.mermaid import split_mermaid_blocks
from leettutor.model_manager import ModelDownloadError, pull_ollama_model
from leettutor.prompts import (
    build_code_review_request,
    build_system_design_request,
    build_tutor_opening,
    build_workspace_help_request,
)
from leettutor.runtime_manager import (
    OllamaRuntimeStatus,
    RuntimeSetupError,
    download_official_installer,
    inspect_ollama_runtime,
    installer_for_system,
    open_installer,
    start_ollama,
)
from leettutor.solutions import SolutionError, SolutionStore


PROJECT_ROOT = Path(__file__).resolve().parent
PROGRESS_STORE = ProgressStore(PROJECT_ROOT / "study_progress.json")
INSTALLER_DIRECTORY = PROJECT_ROOT / ".leettutor" / "installers"
MODE_LABELS = {
    "算法刷题": "algorithm",
    "系统设计": "system_design",
}
HistoryItem = dict[str, str]
REASONING_LABELS = {
    "none": "关闭（推荐，响应最快）",
    "low": "低",
    "medium": "中",
    "high": "高（可能等待很久）",
}


def install_mentor_client_controller() -> None:
    """Install persistent drag behavior for the native floating popover button."""

    components.html(
        """
        <script>
        (() => {
          const win = window.parent;
          const doc = win.document;
          const storageKey = "leettutor-floating-mentor-position-v1";

          const clamp = (root, left, top) => {
            const width = root.offsetWidth || 180;
            const height = root.offsetHeight || 64;
            return {
              left: Math.max(8, Math.min(left, win.innerWidth - width - 8)),
              top: Math.max(8, Math.min(top, win.innerHeight - height - 8)),
            };
          };

          const save = (root) => {
            try {
              const rect = root.getBoundingClientRect();
              win.localStorage.setItem(storageKey, JSON.stringify({
                left: rect.left,
                top: rect.top,
              }));
            } catch (_) {}
          };

          const restore = (root) => {
            try {
              const saved = JSON.parse(win.localStorage.getItem(storageKey) || "null");
              if (!saved || !Number.isFinite(saved.left) || !Number.isFinite(saved.top)) return;
              const point = clamp(root, saved.left, saved.top);
              root.style.right = "auto";
              root.style.bottom = "auto";
              root.style.left = `${point.left}px`;
              root.style.top = `${point.top}px`;
            } catch (_) {}
          };

          const bind = () => {
            const root = doc.querySelector(".st-key-floating_mentor");
            const button = root?.querySelector("button");
            if (!root || !button || button.dataset.mentorDragBound === "1") return;
            button.dataset.mentorDragBound = "1";
            restore(root);

            let gesture = null;
            let suppressClick = false;
            button.addEventListener("pointerdown", (event) => {
              if (event.button !== 0) return;
              const rect = root.getBoundingClientRect();
              gesture = {
                pointerId: event.pointerId,
                startX: event.clientX,
                startY: event.clientY,
                left: rect.left,
                top: rect.top,
                moved: false,
              };
              try { button.setPointerCapture(event.pointerId); } catch (_) {}
            });
            button.addEventListener("pointermove", (event) => {
              if (!gesture || event.pointerId !== gesture.pointerId) return;
              const dx = event.clientX - gesture.startX;
              const dy = event.clientY - gesture.startY;
              if (!gesture.moved && Math.hypot(dx, dy) < 6) return;
              gesture.moved = true;
              event.preventDefault();
              const point = clamp(root, gesture.left + dx, gesture.top + dy);
              root.classList.add("mentor-dragging");
              root.style.right = "auto";
              root.style.bottom = "auto";
              root.style.left = `${point.left}px`;
              root.style.top = `${point.top}px`;
            });
            const finish = (event) => {
              if (!gesture || event.pointerId !== gesture.pointerId) return;
              if (gesture.moved) {
                suppressClick = true;
                save(root);
                setTimeout(() => { suppressClick = false; }, 80);
              }
              root.classList.remove("mentor-dragging");
              gesture = null;
            };
            button.addEventListener("pointerup", finish);
            button.addEventListener("pointercancel", finish);
            button.addEventListener("click", (event) => {
              if (!suppressClick) return;
              event.preventDefault();
              event.stopImmediatePropagation();
            }, true);
          };

          if (!win.__leettutorMentorDragObserver) {
            win.__leettutorMentorDragObserver = new win.MutationObserver(bind);
            win.__leettutorMentorDragObserver.observe(doc.body, {childList: true, subtree: true});
            win.addEventListener("resize", () => {
              const root = doc.querySelector(".st-key-floating_mentor");
              if (!root || !root.style.left) return;
              const rect = root.getBoundingClientRect();
              const point = clamp(root, rect.left, rect.top);
              root.style.left = `${point.left}px`;
              root.style.top = `${point.top}px`;
              save(root);
            });
          }
          bind();
        })();
        </script>
        """,
        height=0,
        width=0,
    )


def set_mentor_client_state(
    state: Literal["loading", "thinking", "answering", "done", "error"],
    *,
    anchor_id: str = "",
    surface: Literal["main", "floating"] = "main",
) -> None:
    """Reflect tutor activity on the floating button, browser title, and scroll."""

    labels = {
        "loading": "小沐加载中…",
        "thinking": "小沐思考中…",
        "answering": "小沐回答中…",
        "done": "小沐导师",
        "error": "小沐调用失败",
    }
    state_json = json.dumps(state)
    label_json = json.dumps(labels[state], ensure_ascii=False)
    anchor_json = json.dumps(anchor_id)
    surface_json = json.dumps(surface)
    components.html(
        f"""
        <script>
        (() => {{
          const win = window.parent;
          const doc = win.document;
          const state = {state_json};
          const label = {label_json};
          const anchorId = {anchor_json};
          const surface = {surface_json};
          const root = doc.querySelector(".st-key-floating_mentor");
          const button = root?.querySelector("button");
          if (state === "done" || state === "error") {{
            root?.classList.remove("mentor-busy");
            button?.removeAttribute("data-mentor-label");
            button?.removeAttribute("aria-busy");
            if (win.__leettutorMentorOriginalTitle) {{
              doc.title = win.__leettutorMentorOriginalTitle;
              delete win.__leettutorMentorOriginalTitle;
            }}
            win.__leettutorMentorScrollObserver?.disconnect();
            delete win.__leettutorMentorScrollObserver;
          }} else {{
            if (!win.__leettutorMentorOriginalTitle) {{
              win.__leettutorMentorOriginalTitle = doc.title;
            }}
            doc.title = `⏳ ${{label}} · LeetTutor`;
            root?.classList.add("mentor-busy");
            button?.setAttribute("data-mentor-label", label);
            button?.setAttribute("aria-busy", "true");
          }}

          const target = anchorId ? doc.getElementById(anchorId) : null;
          const scrollRoot = surface === "floating"
            ? target?.closest('[data-testid="stPopoverBody"]')
            : null;
          const scroll = (behavior = "smooth") => {{
            if (scrollRoot) {{
              scrollRoot.scrollTo({{
                top: scrollRoot.scrollHeight,
                behavior,
              }});
              return;
            }}
            target?.scrollIntoView({{behavior, block: "center"}});
          }};
          if (target) {{
            setTimeout(() => scroll(), 30);
            setTimeout(() => scroll(), 180);
            if (state !== "done" && state !== "error") {{
              win.__leettutorMentorScrollObserver?.disconnect();
              const observer = new win.MutationObserver(() => scroll("auto"));
              const observationRoot = target.closest('[data-testid="stChatMessage"]')
                || target.parentElement
                || target;
              observer.observe(observationRoot, {{
                childList: true,
                subtree: true,
                characterData: true,
              }});
              win.__leettutorMentorScrollObserver = observer;
            }}
          }}
        }})();
        </script>
        """,
        height=0,
        width=0,
    )


def configure_page() -> None:
    st.set_page_config(
        page_title="LeetTutor-Local",
        page_icon="🧠",
        layout="wide",
        initial_sidebar_state="expanded",
    )
    st.markdown(
        """
        <style>
        .block-container {
            width: min(100%, 1480px);
            max-width: 1480px;
            padding: 2rem clamp(1rem, 3vw, 3rem) 6rem;
        }
        [data-testid="stSidebar"][aria-expanded="true"] {
            width: 330px !important;
            min-width: 330px !important;
            flex-basis: 330px !important;
        }
        [data-testid="stSidebar"][aria-expanded="false"] {
            width: 0 !important;
            min-width: 0 !important;
            flex-basis: 0 !important;
            margin-left: 0 !important;
        }
        [data-testid="stAppViewContainer"],
        [data-testid="stMain"] {
            width: 100%;
            min-width: 0;
        }
        .status-line {color: #667085; font-size: 0.9rem; margin-top: -0.5rem;}
        .stChatMessage {border-radius: 12px;}
        textarea {font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;}

        /* Native Streamlit popover, visually presented as a floating mentor. */
        .st-key-floating_mentor {
            position: fixed;
            right: max(1.25rem, env(safe-area-inset-right));
            bottom: max(1.25rem, env(safe-area-inset-bottom));
            width: auto !important;
            z-index: 999990;
            filter: drop-shadow(0 12px 24px rgba(38, 51, 77, 0.20));
        }
        .st-key-floating_mentor button {cursor: grab; touch-action: none;}
        .st-key-floating_mentor.mentor-dragging,
        .st-key-floating_mentor.mentor-dragging button {
            cursor: grabbing !important;
            transition: none !important;
            user-select: none;
        }
        .st-key-floating_mentor button {
            min-height: 62px;
            padding: 0.65rem 1.05rem 0.65rem 0.8rem;
            border: 1px solid rgba(89, 106, 255, 0.24);
            border-radius: 999px;
            background: linear-gradient(135deg, #ffffff 0%, #f2f4ff 100%);
            color: #283056;
            font-weight: 700;
            box-shadow: 0 10px 30px rgba(56, 76, 160, 0.18);
        }
        .st-key-floating_mentor button:hover {
            border-color: #6c7cff;
            transform: translateY(-2px);
            box-shadow: 0 14px 34px rgba(56, 76, 160, 0.26);
        }
        .st-key-floating_mentor button p {
            font-size: 1rem;
        }
        .st-key-floating_mentor.mentor-busy button {
            border-color: rgba(108, 124, 255, 0.68);
            background: linear-gradient(135deg, #f7f8ff 0%, #e8ecff 100%);
            animation: mentor-pulse 1.4s ease-in-out infinite;
        }
        .st-key-floating_mentor.mentor-busy button p {display: none;}
        .st-key-floating_mentor.mentor-busy button::before {
            content: "";
            width: 17px;
            height: 17px;
            flex: 0 0 17px;
            border: 2px solid rgba(76, 92, 190, 0.24);
            border-top-color: #596aff;
            border-radius: 999px;
            animation: mentor-spin 0.85s linear infinite;
        }
        .st-key-floating_mentor.mentor-busy button::after {
            content: attr(data-mentor-label);
            color: #313a73;
            font-size: 0.95rem;
            font-weight: 750;
            white-space: nowrap;
        }
        @keyframes mentor-spin {to {transform: rotate(360deg);}}
        @keyframes mentor-pulse {
            0%, 100% {box-shadow: 0 10px 30px rgba(56, 76, 160, 0.18);}
            50% {box-shadow: 0 12px 38px rgba(76, 96, 230, 0.38);}
        }
        [data-testid="stPopoverBody"] {
            width: min(390px, calc(100vw - 2rem));
            max-height: min(680px, calc(100vh - 5rem));
            overflow-y: auto;
            border-radius: 18px;
        }
        @media (max-width: 700px) {
            .block-container {padding-left: 1rem; padding-right: 1rem;}
            .st-key-floating_mentor {
                right: 0.75rem;
                bottom: 0.75rem;
            }
            .st-key-floating_mentor button {
                min-height: 54px;
                padding: 0.55rem 0.8rem;
            }
        }
        </style>
        """,
        unsafe_allow_html=True,
    )
    install_mentor_client_controller()


def initialize_state() -> AppConfig:
    if "app_config" not in st.session_state:
        try:
            config = load_config()
            st.session_state.config_load_error = ""
        except ConfigError as exc:
            config = AppConfig()
            st.session_state.config_load_error = str(exc)
        st.session_state.app_config = config
        st.session_state.mode_label = "算法刷题"
        st.session_state.provider = config.provider
        st.session_state.endpoint_ollama = config.endpoints["Ollama"]
        st.session_state.endpoint_lm_studio = config.endpoints["LM Studio"]
        st.session_state.model_manual = config.model
        st.session_state.temperature_algorithm = config.temperatures["algorithm"]
        st.session_state.temperature_system_design = config.temperatures[
            "system_design"
        ]
        st.session_state.top_p = config.top_p
        st.session_state.timeout_seconds = config.timeout_seconds
        st.session_state.auto_tune = config.auto_tune
        st.session_state.context_tokens = config.context_tokens
        st.session_state.reasoning_algorithm = config.reasoning_efforts["algorithm"]
        st.session_state.reasoning_system_design = config.reasoning_efforts[
            "system_design"
        ]
        st.session_state.max_tokens_algorithm = config.max_tokens["algorithm"]
        st.session_state.max_tokens_system_design = config.max_tokens[
            "system_design"
        ]
        st.session_state.prompt_algorithm = config.prompts["algorithm"]
        st.session_state.prompt_system_design = config.prompts["system_design"]
        st.session_state.available_models = {"Ollama": [], "LM Studio": []}
        st.session_state.algorithm_messages = []
        st.session_state.system_design_messages = []
        try:
            progress = PROGRESS_STORE.load()
            st.session_state.progress_load_error = ""
        except ProgressError as exc:
            progress = {}
            st.session_state.progress_load_error = str(exc)
        st.session_state.study_progress = progress
        active = sorted(
            (
                item
                for item in progress.items()
                if item[1].get("status") == "in_progress"
            ),
            key=lambda item: str(item[1].get("updated_at", "")),
            reverse=True,
        )
        st.session_state.selected_problem_id = active[0][0] if active else ""
        active_problem = (
            get_problem(st.session_state.selected_problem_id)
            if st.session_state.selected_problem_id
            else None
        )
        st.session_state.algorithm_problem = (
            active_problem.label if active_problem else ""
        )
        st.session_state.mentor_track = "自动补弱"
        st.session_state.mentor_difficulty = "循序渐进"
        st.session_state.code_editor = (
            "# Pattern:\n# Invariant:\n# Mistake:\n\n"
            "class Solution:\n    pass\n"
        )
        st.session_state.leetcode_reference = (
            active_problem.url if active_problem else ""
        )
        st.session_state.leetcode_problem = {}
        st.session_state.leetcode_import_error = ""
        st.session_state.solution_method = ""
        st.session_state.solution_test_cases = (
            '[\n  {"args": [[-1, 0, 3, 5, 9, 12], 9], "expected": 4}\n]'
        )
        st.session_state.code_run_result = {}
        st.session_state.workspace_question = ""
        st.session_state.code_timeout_seconds = 3.0
        st.session_state.floating_mentor_question = ""

    config = st.session_state.app_config
    # Streamlit preserves session objects across code hot reloads. Rebuild an
    # older AppConfig instance when new fields are introduced during development.
    if any(
        not hasattr(config, field)
        for field in ("reasoning_efforts", "max_tokens", "auto_tune", "context_tokens")
    ):
        try:
            raw_config = config.to_dict()
        except (AttributeError, TypeError):
            raw_config = {}
        config = AppConfig.from_mapping(raw_config)
        st.session_state.app_config = config

    defaults = AppConfig()
    st.session_state.setdefault("auto_tune", config.auto_tune)
    st.session_state.setdefault("context_tokens", config.context_tokens)
    st.session_state.setdefault(
        "reasoning_algorithm", config.reasoning_efforts["algorithm"]
    )
    st.session_state.setdefault(
        "reasoning_system_design", config.reasoning_efforts["system_design"]
    )
    st.session_state.setdefault("max_tokens_algorithm", config.max_tokens["algorithm"])
    st.session_state.setdefault(
        "max_tokens_system_design", config.max_tokens["system_design"]
    )
    st.session_state.setdefault("leetcode_reference", "")
    st.session_state.setdefault("leetcode_problem", {})
    st.session_state.setdefault("leetcode_import_error", "")
    st.session_state.setdefault("solution_method", "")
    st.session_state.setdefault(
        "solution_test_cases",
        '[\n  {"args": [[-1, 0, 3, 5, 9, 12], 9], "expected": 4}\n]',
    )
    st.session_state.setdefault("code_run_result", {})
    st.session_state.setdefault("workspace_question", "")
    st.session_state.setdefault("code_timeout_seconds", 3.0)
    st.session_state.setdefault("floating_mentor_question", "")
    for mode in ("algorithm", "system_design"):
        if config.reasoning_efforts.get(mode) not in REASONING_LABELS:
            config.reasoning_efforts[mode] = defaults.reasoning_efforts[mode]
    return config


def provider_settings(
    provider: str,
    endpoint: str,
    config: AppConfig,
    generation: GenerationDefaults | None = None,
) -> ProviderSettings:
    return ProviderSettings(
        provider=provider,
        endpoint=endpoint,
        api_key=config.api_key,
        timeout_seconds=float(st.session_state.timeout_seconds),
        context_tokens=int(st.session_state.context_tokens),
        keep_alive=generation.keep_alive if generation else "10m",
    )


def open_in_vscode() -> None:
    command = shutil.which("code")
    if not command:
        st.sidebar.error(
            "没有找到 VS Code 的 `code` 命令。可在 VS Code 命令面板运行 "
            "“Shell Command: Install 'code' command in PATH”。"
        )
        return
    try:
        subprocess.Popen([command, str(PROJECT_ROOT)])  # noqa: S603
    except OSError as exc:
        st.sidebar.error(f"无法打开 VS Code：{exc}")
    else:
        st.sidebar.success("已请求 VS Code 打开当前仓库。")


def _refresh_ollama_status(endpoint: str) -> OllamaRuntimeStatus:
    status = inspect_ollama_runtime(endpoint)
    st.session_state.ollama_runtime_status = status
    st.session_state.ollama_runtime_endpoint = endpoint
    return status


def render_ollama_setup(endpoint: str, *, enable_vulkan: bool = False) -> bool:
    """First-run setup from official installer through a running local API."""

    if (
        "ollama_runtime_status" not in st.session_state
        or st.session_state.get("ollama_runtime_endpoint") != endpoint
    ):
        _refresh_ollama_status(endpoint)
    status: OllamaRuntimeStatus = st.session_state.ollama_runtime_status

    st.markdown("**Ollama 运行时**")
    if status.running:
        version = f" v{status.version}" if status.version else ""
        st.success(f"Ollama{version} 正在运行。")
    elif not status.local_endpoint:
        st.warning("当前是远程 Endpoint；请在远程机器上安装并启动 Ollama。")
    elif status.installed:
        st.warning("已找到 Ollama，但本地服务尚未启动。")
    else:
        st.error("这台机器尚未安装 Ollama。可以直接在这里完成官方安装。")

    refresh_col, action_col = st.columns(2)
    if refresh_col.button(
        "重新检测",
        key="refresh_ollama_runtime",
        use_container_width=True,
    ):
        status = _refresh_ollama_status(endpoint)
        if status.running:
            st.success("Ollama 已连接。")
        elif status.installed:
            st.info("检测到安装，但服务仍未启动。")
        else:
            st.info("仍未检测到本地 Ollama。")

    if status.local_endpoint and status.installed and not status.running:
        if action_col.button(
            "启动 Ollama",
            key="start_ollama_runtime",
            type="primary",
            use_container_width=True,
        ):
            try:
                start_ollama(status, enable_vulkan=enable_vulkan)
            except RuntimeSetupError as exc:
                st.error(str(exc))
            else:
                with st.spinner("正在等待 Ollama API 启动…"):
                    for _ in range(24):
                        time.sleep(0.5)
                        status = _refresh_ollama_status(endpoint)
                        if status.running:
                            break
                if status.running:
                    st.success("Ollama 已启动，现在可以下载模型。")
                else:
                    st.warning("应用已经发出启动请求，但 API 尚未就绪；请稍后重新检测。")

    if status.local_endpoint and not status.installed:
        spec = installer_for_system()
        if spec is None:
            action_col.link_button(
                "打开官方安装页",
                "https://ollama.com/download",
                use_container_width=True,
            )
        elif action_col.button(
            "安装 Ollama",
            key="install_ollama_runtime",
            type="primary",
            use_container_width=True,
        ):
            progress_bar = st.progress(0.0, text="正在从 Ollama 官方网站下载安装器…")
            installer_path: Path | None = None
            try:
                for update in download_official_installer(
                    spec, INSTALLER_DIRECTORY
                ):
                    installer_path = update.path or installer_path
                    if update.total:
                        downloaded_mb = update.downloaded / (1024**2)
                        total_mb = update.total / (1024**2)
                        progress_bar.progress(
                            update.fraction,
                            text=f"下载安装器 · {downloaded_mb:.0f}/{total_mb:.0f} MB",
                        )
                if installer_path is None:
                    raise RuntimeSetupError("安装器下载结束，但没有生成文件。")
                open_installer(installer_path)
            except RuntimeSetupError as exc:
                progress_bar.empty()
                st.error(str(exc))
            else:
                progress_bar.progress(1.0, text="安装器已打开")
                st.success(spec.instructions)
                st.caption("完成系统安装向导后，回到这里点击“重新检测”。")

    return status.running


def render_model_center(
    *, provider: str, endpoint: str, config: AppConfig
) -> None:
    """Show hardware-aware recommendations and provider-specific installation."""

    if "hardware_profile" not in st.session_state:
        st.session_state.hardware_profile = detect_hardware()
    profile: HardwareProfile = st.session_state.hardware_profile
    recommendations = recommend_models(profile)

    with st.expander("硬件检测与模型安装", expanded=not bool(config.model)):
        st.markdown("**检测到的设备**")
        st.caption(profile.summary)
        st.caption("推荐基于模型体积和保守内存预算估算；上下文越长，额外内存越多。")
        if profile.gpu and not profile.ollama_gpu_supported:
            st.warning(
                f"检测到 {profile.gpu}"
                + (f"（{profile.vram_gb:g} GB VRAM）" if profile.vram_gb else "")
                + "，但官方 Ollama 在 Intel macOS 上不会使用这块 AMD GPU；"
                "当前推理仍走 CPU，这部分显存不会计入模型预算。"
            )
        elif profile.ollama_vulkan_required:
            st.info(
                "检测到 Windows AMD/Intel 显卡。应用从这里启动 Ollama 时会自动设置 "
                "`OLLAMA_VULKAN=1` 使用实验性 Vulkan 加速；如果 Ollama 已在后台运行，"
                "请先完全退出它，再回到这里启动。"
            )

        ollama_ready = False
        if provider == "Ollama":
            ollama_ready = render_ollama_setup(
                endpoint, enable_vulkan=profile.ollama_vulkan_required
            )
            st.divider()
        elif not profile.lm_studio_supported:
            st.warning("LM Studio 官方目前不支持 Intel Mac；这台设备建议改用 Ollama。")

        labels = [recommendation.label for recommendation in recommendations]
        selected_label = st.selectbox(
            "适合这台设备的模型",
            labels,
            key=f"recommended_model_{provider.lower().replace(' ', '_')}",
        )
        recommendation = recommendations[labels.index(selected_label)]
        st.write(f"**用途：** {recommendation.purpose}")
        st.caption(
            f"建议至少约 {recommendation.minimum_memory_gb:g} GB 可用内存；"
            f"模型文件约 {recommendation.download_gb:g} GB。"
        )

        if provider == "Ollama":
            st.code(f"ollama pull {recommendation.ollama_id}", language="bash")
            link_col, download_col = st.columns(2)
            link_col.link_button(
                "查看模型页面",
                f"https://ollama.com/library/{recommendation.ollama_id.split(':')[0]}",
                use_container_width=True,
            )
            if download_col.button(
                "一键下载",
                key="download_recommended_ollama_model",
                type="primary",
                use_container_width=True,
                disabled=not ollama_ready,
            ):
                progress_bar = st.progress(0.0, text="正在连接 Ollama…")
                status_slot = st.empty()
                try:
                    final_status = ""
                    for update in pull_ollama_model(
                        endpoint, recommendation.ollama_id
                    ):
                        final_status = update.status
                        if update.total:
                            downloaded = update.completed / (1024**3)
                            total = update.total / (1024**3)
                            progress_bar.progress(
                                update.fraction,
                                text=f"{update.status} · {downloaded:.1f}/{total:.1f} GB",
                            )
                        else:
                            status_slot.caption(update.status)
                except ModelDownloadError as exc:
                    progress_bar.empty()
                    status_slot.empty()
                    st.error(str(exc))
                else:
                    progress_bar.progress(1.0, text="下载完成")
                    status_slot.success(
                        f"{recommendation.ollama_id} 已就绪（{final_status}）。"
                    )
                    st.session_state.model_manual = recommendation.ollama_id
                    choice_key = "model_choice_ollama"
                    st.session_state[choice_key] = "手动输入…"
                    models = st.session_state.available_models["Ollama"]
                    if recommendation.ollama_id not in models:
                        models.append(recommendation.ollama_id)
            if not ollama_ready:
                st.caption("先在上方完成 Ollama 安装并启动服务，模型下载按钮才会启用。")
        else:
            st.code(f"lms get {recommendation.lm_search}", language="bash")
            st.info(
                "LM Studio 用户可在应用内 Discover 搜索上面的关键词并选择 Q4；"
                "也可以运行 `lms get`。下载完成后在 Developer 页面 Load 并启动 Server。"
            )
            first, second = st.columns(2)
            first.link_button(
                "下载 LM Studio",
                "https://lmstudio.ai/download",
                use_container_width=True,
            )
            second.link_button(
                "官方模型下载说明",
                "https://lmstudio.ai/docs/cli/local-models/get",
                use_container_width=True,
            )


def render_sidebar(
    config: AppConfig, mode: str
) -> tuple[ProviderSettings, str, float, float, str, int]:
    with st.sidebar:
        st.header("本地模型")
        provider = st.selectbox(
            "API Provider", ["Ollama", "LM Studio"], key="provider"
        )
        endpoint_key = (
            "endpoint_ollama" if provider == "Ollama" else "endpoint_lm_studio"
        )
        endpoint = st.text_input("API Endpoint", key=endpoint_key)

        settings = provider_settings(provider, endpoint, config)
        render_model_center(
            provider=provider, endpoint=endpoint, config=config
        )
        if st.button("检测服务并刷新模型", use_container_width=True):
            try:
                models = LocalLLMClient(settings).list_models()
            except LocalLLMError as exc:
                st.error(str(exc))
            else:
                st.session_state.available_models[provider] = models
                if models:
                    st.success(f"服务正常，发现 {len(models)} 个模型。")
                else:
                    st.warning("服务可访问，但没有返回模型；请确认已经下载并加载模型。")

        models = st.session_state.available_models.get(provider, [])
        manual_option = "手动输入…"
        options = [manual_option, *models]
        choice_key = f"model_choice_{provider.lower().replace(' ', '_')}"
        if st.session_state.get(choice_key) not in options:
            st.session_state[choice_key] = manual_option
        selected_model = st.selectbox("已检测到的模型", options, key=choice_key)
        if selected_model == manual_option:
            model = st.text_input(
                "Model Name",
                key="model_manual",
                placeholder="例如 qwen3.5:9b",
            ).strip()
        else:
            model = selected_model
            st.caption(f"当前模型：`{model}`")

        profile: HardwareProfile = st.session_state.hardware_profile
        slow_cpu_model = any(
            marker in model.casefold()
            for marker in ("14b", "27b", "30b", "32b", "70b")
        )
        if (
            provider == "Ollama"
            and model
            and not profile.ollama_gpu_supported
            and slow_cpu_model
        ):
            st.warning(
                "当前是 CPU-only 推理，这个模型会明显偏慢。导师对练建议改用 "
                "`qwen3.5:9b`；大模型可以留给不赶时间的深度 Review。"
            )
        if (
            model
            and "qwen3.6" in model.casefold()
            and (profile.vram_gb or 0) <= 8
        ):
            st.warning(
                "Qwen 3.6 27B 的 Q4 文件约 17 GB，无法完整放进 8 GB 显存；"
                "它会使用 CPU + GPU 混合推理。可运行，但实时导师体验通常不如 "
                "Qwen 3.5 9B。"
            )

        generation = recommend_generation_defaults(profile, model)
        auto_tune = st.toggle(
            "根据硬件和模型自动调优",
            key="auto_tune",
            help="按显存、内存和模型体积自动设置超时、上下文、思考强度与输出额度。",
        )
        if auto_tune:
            st.session_state.temperature_algorithm = (
                generation.algorithm_temperature
            )
            st.session_state.temperature_system_design = (
                generation.system_design_temperature
            )
            st.session_state.top_p = generation.top_p
            st.session_state.timeout_seconds = generation.timeout_seconds
            st.session_state.context_tokens = generation.context_tokens
            st.session_state.reasoning_algorithm = generation.algorithm_reasoning
            st.session_state.reasoning_system_design = (
                generation.system_design_reasoning
            )
            st.session_state.max_tokens_algorithm = (
                generation.algorithm_max_tokens
            )
            st.session_state.max_tokens_system_design = (
                generation.system_design_max_tokens
            )
            offload_label = (
                "CPU + GPU 混合卸载"
                if generation.partially_offloaded
                else "优先完整 GPU 加载"
            )
            st.caption(
                f"自动档：{offload_label} · Timeout "
                f"{generation.timeout_seconds:g}s · 上下文 "
                f"{generation.context_tokens} · 模型驻留 {generation.keep_alive}"
            )

        st.divider()
        st.subheader("生成参数")
        temperature_key = (
            "temperature_algorithm"
            if mode == "algorithm"
            else "temperature_system_design"
        )
        temperature = st.slider(
            "Temperature",
            min_value=0.0,
            max_value=2.0,
            step=0.05,
            key=temperature_key,
            disabled=auto_tune,
            help="算法默认 0.2；系统设计默认 0.5。",
        )
        top_p = st.slider(
            "Top P",
            min_value=0.0,
            max_value=1.0,
            step=0.05,
            key="top_p",
            disabled=auto_tune,
        )
        st.number_input(
            "Timeout（秒）",
            min_value=5.0,
            max_value=600.0,
            step=5.0,
            key="timeout_seconds",
            disabled=auto_tune,
        )
        st.number_input(
            "上下文 Tokens",
            min_value=2048,
            max_value=65536,
            step=1024,
            key="context_tokens",
            disabled=auto_tune,
        )
        reasoning_key = (
            "reasoning_algorithm"
            if mode == "algorithm"
            else "reasoning_system_design"
        )
        reasoning_effort = st.selectbox(
            "深度思考",
            list(REASONING_LABELS),
            format_func=REASONING_LABELS.get,
            key=reasoning_key,
            help="DeepSeek/Qwen 的 thinking 可能先运行数分钟；算法引导默认关闭。",
            disabled=auto_tune,
        )
        max_tokens_key = (
            "max_tokens_algorithm"
            if mode == "algorithm"
            else "max_tokens_system_design"
        )
        max_tokens = int(
            st.number_input(
                "最大输出 Tokens",
                min_value=64,
                max_value=4096,
                step=64,
                key=max_tokens_key,
                disabled=auto_tune,
            )
        )
        if reasoning_effort != "none" and max_tokens < 1024:
            st.warning("已开启深度思考；建议把最大输出 Tokens 提高到至少 1024。")

        with st.expander("角色 Prompt（可编辑）"):
            st.text_area("算法面试官", height=260, key="prompt_algorithm")
            st.text_area("系统架构师", height=260, key="prompt_system_design")

        # Keep runtime configuration current even before it is persisted.
        config.provider = provider
        config.endpoints[provider] = endpoint
        config.model = model
        config.auto_tune = bool(auto_tune)
        config.context_tokens = int(st.session_state.context_tokens)
        config.temperatures["algorithm"] = float(
            st.session_state.temperature_algorithm
        )
        config.temperatures["system_design"] = float(
            st.session_state.temperature_system_design
        )
        config.top_p = float(top_p)
        config.timeout_seconds = float(st.session_state.timeout_seconds)
        config.reasoning_efforts["algorithm"] = st.session_state.reasoning_algorithm
        config.reasoning_efforts["system_design"] = (
            st.session_state.reasoning_system_design
        )
        config.max_tokens["algorithm"] = int(st.session_state.max_tokens_algorithm)
        config.max_tokens["system_design"] = int(
            st.session_state.max_tokens_system_design
        )
        config.prompts["algorithm"] = st.session_state.prompt_algorithm
        config.prompts["system_design"] = st.session_state.prompt_system_design

        settings = provider_settings(
            provider,
            endpoint,
            config,
            generation=generation,
        )
        left, right = st.columns(2)
        if left.button("保存设置", use_container_width=True):
            try:
                save_config(config)
            except ConfigError as exc:
                st.error(str(exc))
            else:
                st.success("已保存到本地 config.json。")
        if right.button("清空对话", use_container_width=True):
            st.session_state[f"{mode}_messages"] = []
            st.rerun()

        if st.button("在 VS Code 中打开仓库", use_container_width=True):
            open_in_vscode()

        st.caption(
            "只有点击运行时才会在受限子进程执行编辑器代码；AI 对话只发送到你配置的 API Endpoint。"
        )

    return (
        settings,
        model,
        float(temperature),
        float(top_p),
        reasoning_effort,
        max_tokens,
    )


def render_assistant_content(content: str, *, render_mermaid: bool) -> None:
    if not render_mermaid:
        st.markdown(content)
        return

    segments = split_mermaid_blocks(content)
    if not segments:
        st.markdown(content)
        return
    for segment in segments:
        if segment.kind == "markdown":
            st.markdown(segment.content)
        elif hasattr(st, "mermaid_chart"):
            st.mermaid_chart(segment.content, width="stretch")
        else:  # Keeps the answer readable if an older Streamlit is used accidentally.
            st.code(segment.content, language="mermaid")


def render_history(history: list[HistoryItem], *, render_mermaid: bool) -> None:
    for item in history:
        with st.chat_message(item["role"]):
            visible = item.get("display", item["content"])
            if item["role"] == "assistant":
                render_assistant_content(visible, render_mermaid=render_mermaid)
            else:
                st.markdown(visible)


def submit_to_tutor(
    *,
    mode: str,
    content: str,
    display: str,
    settings: ProviderSettings,
    model: str,
    temperature: float,
    top_p: float,
    reasoning_effort: str,
    max_tokens: int,
    config: AppConfig,
    surface: Literal["main", "floating"] = "main",
) -> None:
    history: list[HistoryItem] = st.session_state[f"{mode}_messages"]
    history.append({"role": "user", "content": content, "display": display})
    anchor_id = f"mentor-response-{surface}-{mode}-{len(history)}"

    with st.chat_message("user"):
        st.markdown(display)
    with st.chat_message("assistant"):
        st.markdown(
            f'<span id="{html.escape(anchor_id, quote=True)}" '
            'class="mentor-response-anchor"></span>',
            unsafe_allow_html=True,
        )
        set_mentor_client_state("loading", anchor_id=anchor_id, surface=surface)
        activity = st.status(
            f"正在连接 {settings.provider} 并加载 {model or '模型'}…首次加载可能需要几十秒。",
            expanded=False,
        )
        placeholder = st.empty()
        complete = ""
        thinking_chars = 0
        answer_started = False
        thinking_started = False
        last_activity_update = time.monotonic()
        try:
            with st.spinner("小沐正在加载模型并思考…", show_time=True):
                client = LocalLLMClient(settings)
                api_messages = [
                    {"role": "system", "content": config.prompts[mode]},
                    *[
                        {"role": item["role"], "content": item["content"]}
                        for item in history[-24:]
                    ],
                ]
                for delta in client.stream_chat(
                    messages=api_messages,
                    model=model,
                    temperature=temperature,
                    top_p=top_p,
                    reasoning_effort=reasoning_effort,
                    max_tokens=max_tokens,
                ):
                    if delta.kind == "thinking":
                        thinking_chars += len(delta.content)
                        if not thinking_started:
                            set_mentor_client_state("thinking", anchor_id=anchor_id, surface=surface)
                            activity.update(label="模型正在思考…", state="running")
                            thinking_started = True
                        now = time.monotonic()
                        if now - last_activity_update >= 1.0:
                            activity.update(
                                label=(
                                    "模型正在思考…已产生约 "
                                    f"{thinking_chars} 个思考字符"
                                ),
                                state="running",
                            )
                            last_activity_update = now
                    else:
                        complete += delta.content
                        if not answer_started:
                            set_mentor_client_state("answering", anchor_id=anchor_id, surface=surface)
                            activity.update(label="模型正在回答…", state="running")
                            answer_started = True
                        placeholder.markdown(complete + "▌")
        except LocalLLMError as exc:
            placeholder.empty()
            activity.update(label="模型调用失败", state="error")
            set_mentor_client_state("error", anchor_id=anchor_id, surface=surface)
            st.error(str(exc))
            st.caption("你的问题已经保留，可以修正侧边栏配置后再次发送。")
            return

        placeholder.empty()
        if not complete.strip():
            activity.update(label="没有收到最终答案", state="error")
            set_mentor_client_state("error", anchor_id=anchor_id, surface=surface)
            if thinking_chars:
                st.warning(
                    "模型把本次输出额度都用在了思考阶段。请把“深度思考”设为关闭，"
                    "或提高最大输出 Tokens。"
                )
            else:
                st.warning("模型连接已结束，但没有返回文本。请检查模型日志或换一个模型。")
            return
        activity.update(label="回答完成", state="complete")
        render_assistant_content(
            complete, render_mermaid=(mode == "system_design")
        )
        history.append({"role": "assistant", "content": complete})
        set_mentor_client_state("done", anchor_id=anchor_id, surface=surface)


def render_floating_mentor(
    *,
    mode: Literal["algorithm", "system_design"],
    model: str,
    settings: ProviderSettings,
    temperature: float,
    top_p: float,
    reasoning_effort: str,
    max_tokens: int,
    config: AppConfig,
) -> None:
    """Render a fixed tutor character backed by the existing conversation."""

    history: list[HistoryItem] = st.session_state[f"{mode}_messages"]
    with st.container(key="floating_mentor"):
        with st.popover(
            "👩🏻‍🏫 小沐导师",
            help="随时问当前题目；她能看到编辑器和最近运行结果。",
        ):
            st.markdown("### 👩🏻‍🏫 小沐导师")
            if mode == "algorithm":
                problem = st.session_state.get("algorithm_problem", "当前题目")
                run_status = st.session_state.get("code_run_result", {}).get(
                    "summary", "还没有运行"
                )
                st.caption(f"正在陪练：{problem or '尚未选题'} · {run_status}")
                st.info("我能看到你此刻的题面、代码、测试和最近运行结果。")
            else:
                requirement = st.session_state.get("system_requirement", "")
                st.caption(f"当前设计：{requirement or '尚未填写需求'}")
                st.info("我会继续追问容量、可靠性和架构取舍。")

            if history:
                st.markdown("**最近对话**")
                for item in history[-4:]:
                    speaker = "你" if item["role"] == "user" else "小沐"
                    visible = item.get("display", item["content"]).strip()
                    if len(visible) > 700:
                        visible = visible[:700] + "…"
                    with st.container(border=True):
                        st.caption(speaker)
                        st.markdown(visible)

            question = st.text_area(
                "直接问小沐",
                key="floating_mentor_question",
                height=110,
                placeholder="例如：我这里为什么会死循环？先别给答案。",
            )
            send_clicked = st.button(
                "发送给小沐",
                key=f"floating_send_{mode}",
                type="primary",
                use_container_width=True,
            )
            stuck_col, next_col = st.columns(2)
            stuck_clicked = stuck_col.button(
                "我卡住了",
                key=f"floating_stuck_{mode}",
                use_container_width=True,
            )
            next_clicked = next_col.button(
                "只提示下一步",
                key=f"floating_next_{mode}",
                use_container_width=True,
            )

            requested_question = ""
            trigger = "悬浮导师对话"
            if send_clicked:
                requested_question = question.strip() or "请根据当前现场继续引导我。"
            elif stuck_clicked:
                requested_question = (
                    question.strip()
                    or "我卡住了。先判断我已经做到哪里，只指出一个最关键的问题。"
                )
                trigger = "悬浮导师：我卡住了"
            elif next_clicked:
                requested_question = (
                    question.strip()
                    or "不要给完整解法，只根据现有进度提示下一步并问我一个问题。"
                )
                trigger = "悬浮导师：下一步提示"

            if requested_question:
                if not model:
                    st.warning("先在左侧选择或填写本地模型，然后再和我对话。")
                else:
                    if mode == "algorithm":
                        content = _workspace_request(
                            requested_question,
                            trigger=trigger,
                        )
                    else:
                        requirement = st.session_state.get(
                            "system_requirement", "（尚未填写）"
                        )
                        content = (
                            "这是悬浮架构导师对话。请延续现有面试记录，一次只推进一个关键点。\n\n"
                            f"当前需求：{requirement}\n我的问题：{requested_question}"
                        )
                    submit_to_tutor(
                        mode=mode,
                        content=content,
                        display=f"向小沐提问：{requested_question}",
                        settings=settings,
                        model=model,
                        temperature=temperature,
                        top_p=top_p,
                        reasoning_effort=reasoning_effort,
                        max_tokens=max_tokens,
                        config=config,
                        surface="floating",
                    )


def _run_result_text(result: dict[str, object] | None = None) -> str:
    value = result if result is not None else st.session_state.get("code_run_result", {})
    if not value:
        return "尚未运行"
    return json.dumps(value, ensure_ascii=False, indent=2, default=str)


def _workspace_request(question: str, *, trigger: str) -> str:
    imported = st.session_state.get("leetcode_problem", {})
    return build_workspace_help_request(
        problem=st.session_state.get("algorithm_problem", ""),
        statement=str(imported.get("statement", "")),
        language=st.session_state.get("code_language", "Python"),
        code=st.session_state.get("code_editor", ""),
        method_name=st.session_state.get("solution_method", ""),
        test_cases=st.session_state.get("solution_test_cases", ""),
        run_result=_run_result_text(),
        question=question,
        trigger=trigger,
    )


def _render_run_result(result: dict[str, object]) -> None:
    status = str(result.get("status", "error"))
    summary = str(result.get("summary", ""))
    duration = float(result.get("duration_seconds", 0.0) or 0.0)
    message = f"{summary} · {duration:.3f}s"
    if status == "passed":
        st.success(message)
    elif status == "failed":
        st.error(message)
    elif status == "timeout":
        st.warning(message)
    else:
        st.error(message)

    cases = result.get("cases", [])
    if isinstance(cases, list) and cases:
        st.dataframe(cases, use_container_width=True, hide_index=True)
    if result.get("stdout"):
        with st.expander("程序打印（stdout）", expanded=status != "passed"):
            st.code(str(result["stdout"]), language="text")
    if result.get("stderr"):
        with st.expander("异常现场（traceback）", expanded=True):
            st.code(str(result["stderr"]), language="text")


def _apply_imported_problem(imported_problem: ImportedProblem) -> None:
    """Load a public LeetCode problem into the shared mentor/IDE workspace."""

    imported = imported_problem.to_dict()
    title = f"{imported_problem.frontend_id}. {imported_problem.title}"
    st.session_state.leetcode_problem = imported
    st.session_state.leetcode_import_error = ""
    st.session_state.algorithm_problem = title
    st.session_state.code_language = "Python"
    st.session_state.code_editor = imported_problem.starter_code
    st.session_state.solution_method = imported_problem.method_name
    if imported_problem.sample_cases:
        st.session_state.solution_test_cases = imported_problem.sample_cases
    st.session_state.save_filename_python = (
        f"{imported_problem.frontend_id}.{imported_problem.slug}.py"
    )
    st.session_state.code_run_result = {}


def _import_problem_into_workspace(reference: str) -> ImportedProblem:
    imported_problem = fetch_problem(reference, timeout_seconds=12)
    _apply_imported_problem(imported_problem)
    return imported_problem


def render_solution_workbench() -> tuple[str, str] | None:
    """Render the in-browser IDE and return an optional tutor request."""

    store = SolutionStore(PROJECT_ROOT)
    pending: tuple[str, str] | None = None
    st.markdown("### 刷题 IDE")
    st.caption("导入题目 → 写代码 → 本地跑样例 → 把当前现场交给导师。")

    with st.container(border=True):
        reference_col, import_col = st.columns([4, 1])
        reference_col.text_input(
            "LeetCode 题目链接或 slug",
            key="leetcode_reference",
            placeholder="https://leetcode.com/problems/binary-search/",
        )
        import_clicked = import_col.button(
            "导入题目", type="primary", use_container_width=True
        )
        if import_clicked:
            with st.spinner("正在从 LeetCode 读取公开题面与 Python 模板…"):
                try:
                    imported_problem = _import_problem_into_workspace(
                        st.session_state.leetcode_reference
                    )
                except LeetCodeImportError as exc:
                    st.session_state.leetcode_import_error = str(exc)
                    st.error(str(exc))
                else:
                    title = f"{imported_problem.frontend_id}. {imported_problem.title}"
                    st.success(f"已导入 {title}。样例参数已转换为可运行 JSON。")

        imported = st.session_state.get("leetcode_problem", {})
        if imported:
            badge = "Premium" if imported.get("paid_only") else imported.get("difficulty", "")
            st.markdown(f"#### {imported.get('frontend_id')}. {imported.get('title')} · {badge}")
            topics = imported.get("topics") or []
            if topics:
                st.caption(" · ".join(str(topic) for topic in topics))
            open_col, statement_col = st.columns(2)
            open_col.link_button(
                "在 LeetCode 打开 / 提交",
                str(imported.get("url")),
                use_container_width=True,
            )
            with statement_col.popover("查看完整题面", use_container_width=True):
                st.markdown(str(imported.get("statement", "（题面为空）")))

        meta_col, language_col = st.columns([3, 1])
        problem = meta_col.text_input(
            "当前题目",
            key="algorithm_problem",
            placeholder="例如 34. Find First and Last Position",
        )
        language = language_col.selectbox(
            "语言", ["Python", "Java"], key="code_language"
        )

        with st.expander("载入仓库中的已有题解", expanded=False):
            files = store.list_files(language)
            file_options = ["（新建）", *files]
            file_key = f"existing_solution_{language.lower()}"
            if st.session_state.get(file_key) not in file_options:
                st.session_state[file_key] = "（新建）"
            existing_col, load_col = st.columns([4, 1])
            existing = existing_col.selectbox(
                "已有题解", file_options, key=file_key, label_visibility="collapsed"
            )
            if load_col.button(
                "载入",
                disabled=existing == "（新建）",
                use_container_width=True,
            ):
                try:
                    st.session_state.code_editor = store.load(language, existing)
                except SolutionError as exc:
                    st.error(str(exc))
                else:
                    st.session_state[f"save_filename_{language.lower()}"] = existing
                    st.session_state.code_run_result = {}
                    st.success(f"已载入 {existing}。")

        code = st.text_area(
            "代码编辑器",
            height=500,
            key="code_editor",
            help="点击运行后才会启动受限子进程；不会自动提交到 LeetCode。",
        )

        method_col, timeout_col = st.columns([3, 1])
        method_col.text_input(
            "Solution 方法名",
            key="solution_method",
            placeholder="留空时自动识别唯一的公开方法",
        )
        timeout_col.number_input(
            "超时（秒）",
            min_value=0.5,
            max_value=10.0,
            step=0.5,
            key="code_timeout_seconds",
        )
        st.text_area(
            "测试用例（JSON）",
            height=180,
            key="solution_test_cases",
            help=(
                '每项格式：{"args": [...], "kwargs": {}, "expected": ...}。'
                "没有 expected 时只展示实际输出。"
            ),
        )

        run_col, analyze_col = st.columns(2)
        run_clicked = run_col.button(
            "▶ 运行代码",
            type="primary",
            use_container_width=True,
            disabled=language != "Python",
        )
        analyze_clicked = analyze_col.button(
            "▶ 运行并让导师分析",
            use_container_width=True,
            disabled=language != "Python",
        )
        if language != "Python":
            st.info("当前内置运行器先支持 Python；Java 仍可编辑、保存和交给导师 Review。")
        if run_clicked or analyze_clicked:
            with st.spinner("正在受限子进程中运行…"):
                try:
                    run_result: RunResult = run_python_solution(
                        source=code,
                        method_name=st.session_state.solution_method,
                        test_cases=st.session_state.solution_test_cases,
                        timeout_seconds=st.session_state.code_timeout_seconds,
                    )
                except CodeValidationError as exc:
                    run_result = RunResult(status="error", summary=str(exc))
            st.session_state.code_run_result = run_result.to_dict()
            if analyze_clicked:
                question = (
                    "请分析最近一次运行现场，只指出一个最关键的问题并追问我。"
                )
                pending = (
                    _workspace_request(question, trigger="运行后自动求助"),
                    f"运行了 **{problem or '当前题目'}**，请导师根据结果继续引导。",
                )

        result = st.session_state.get("code_run_result", {})
        if result:
            _render_run_result(result)

        st.text_input(
            "我卡在哪里（可选）",
            key="workspace_question",
            placeholder="例如：为什么重复元素时这里会越界？",
        )
        stuck_col, continue_col, review_col = st.columns(3)
        if stuck_col.button("我卡住了，提示我", use_container_width=True):
            pending = (
                _workspace_request(
                    st.session_state.workspace_question,
                    trigger="用户在代码现场卡住",
                ),
                f"我在 **{problem or '当前题目'}** 卡住了，请看当前代码和运行结果。",
            )
        if continue_col.button("根据现有代码继续引导", use_container_width=True):
            pending = (
                _workspace_request(
                    st.session_state.workspace_question
                    or "请判断我现在完成到哪一步，并只给下一步追问。",
                    trigger="继续导师对练",
                ),
                f"请根据 **{problem or '当前题目'}** 的现有代码继续引导。",
            )
        if review_col.button("代码 Review", use_container_width=True):
            if not code.strip():
                st.error("请先写代码。")
            else:
                request = build_code_review_request(
                    problem=problem,
                    language=language,
                    code=code,
                    notes=(
                        st.session_state.workspace_question
                        + "\n最近运行现场：\n"
                        + _run_result_text()
                    ),
                )
                pending = (
                    request,
                    f"请 Review 我的 **{language}** 代码：{problem or '未命名题目'}",
                )

        suffix = SolutionStore.LANGUAGE_SUFFIXES[language]
        filename_key = f"save_filename_{language.lower()}"
        if filename_key not in st.session_state:
            st.session_state[filename_key] = f"0.problem-name{suffix}"
        with st.expander("保存到仓库", expanded=False):
            name_col, overwrite_col, save_col = st.columns([3, 1, 1])
            filename = name_col.text_input("保存文件名", key=filename_key)
            overwrite = overwrite_col.checkbox(
                "允许覆盖", key=f"overwrite_{language}"
            )
            if save_col.button("保存", use_container_width=True):
                try:
                    path = store.save(
                        language, filename.strip(), code, overwrite=overwrite
                    )
                except SolutionError as exc:
                    st.error(str(exc))
                else:
                    st.success(f"已保存：{path.relative_to(PROJECT_ROOT)}")

        st.caption(
            "安全说明：运行器会限制时间、内存并阻止常见文件/网络/子进程操作，"
            "但它不是用于执行陌生代码的强安全沙箱。只运行你自己编写的代码。"
        )

    return pending


def _save_problem_status(problem: Problem, status: str) -> None:
    try:
        st.session_state.study_progress = PROGRESS_STORE.update(problem, status)
    except ProgressError as exc:
        st.error(str(exc))


def _select_next_mentor_problem() -> Problem:
    current = st.session_state.selected_problem_id
    selected = choose_next_problem(
        st.session_state.study_progress,
        track=st.session_state.mentor_track,
        difficulty=st.session_state.mentor_difficulty,
        exclude_id=int(current) if current else None,
    )
    _save_problem_status(selected, "in_progress")
    st.session_state.selected_problem_id = str(selected.id)
    st.session_state.leetcode_reference = selected.url
    st.session_state.leetcode_problem = {}
    st.session_state.algorithm_problem = selected.label
    st.session_state.code_language = "Python"
    st.session_state.save_filename_python = f"{selected.id}.{selected.slug}.py"
    st.session_state.algorithm_notes = f"导师训练目标：{selected.focus}"
    return selected


def render_mentor_panel() -> tuple[str, str] | None:
    """Render the guided curriculum and return an optional first tutor turn."""

    pending: tuple[str, str] | None = None
    progress = st.session_state.study_progress
    summary = progress_summary(progress)

    with st.container(border=True):
        st.markdown("### 导师模式")
        st.write("系统负责安排下一题；你只需要讲思路、回答追问、写代码。")
        filter_col, difficulty_col, action_col = st.columns([1.2, 1.2, 1])
        filter_col.selectbox(
            "训练路线", ["自动补弱", *TOPIC_ORDER], key="mentor_track"
        )
        difficulty_col.selectbox(
            "难度", ["循序渐进", "Easy", "Medium", "Hard"], key="mentor_difficulty"
        )
        if action_col.button(
            "导师给我下一题", type="primary", use_container_width=True
        ):
            try:
                selected = _select_next_mentor_problem()
            except ValueError as exc:
                st.error(str(exc))
            else:
                with st.spinner("导师正在载入完整题面和 Python 模板…"):
                    try:
                        _import_problem_into_workspace(selected.url)
                    except LeetCodeImportError as exc:
                        st.session_state.leetcode_import_error = str(exc)
                st.rerun()

        st.caption(
            f"已练 {summary['attempted']}/{summary['total']} · "
            f"已掌握 {summary['mastered']} · 待复习 {summary['review']}"
        )

        problem = (
            get_problem(st.session_state.selected_problem_id)
            if st.session_state.selected_problem_id
            else None
        )
        if problem is None:
            st.info("点击“导师给我下一题”，系统会从二分、栈、优先队列和 DP 中安排起点。")
            return None

        difficulty_colors = {"Easy": "green", "Medium": "orange", "Hard": "red"}
        st.markdown(
            f"#### {problem.id}. {problem.title_cn} · :{difficulty_colors[problem.difficulty]}[{problem.difficulty}]"
        )
        st.write(f"**本轮训练目标：** {problem.focus}")
        st.write(f"**开场自问：** {problem.invariant_prompt}")
        attempt = int(progress.get(str(problem.id), {}).get("attempts", 1))

        imported = st.session_state.get("leetcode_problem", {})
        imported_matches = imported.get("slug") == problem.slug
        if st.session_state.get("leetcode_import_error"):
            st.warning(
                "自动导入题面失败："
                + st.session_state.leetcode_import_error
                + " 你仍可打开原题，或在下方重试导入。"
            )
        if imported_matches:
            with st.expander("📖 完整题目内容", expanded=True):
                st.markdown(str(imported.get("statement", "（题面为空）")))
                topics = imported.get("topics") or []
                if topics:
                    st.caption("Topics：" + " · ".join(str(topic) for topic in topics))
        else:
            st.info("点击“开始导师引导”时会自动载入完整题面、Python 模板和样例参数。")

        open_col, guide_col = st.columns(2)
        open_col.link_button(
            "打开 LeetCode 题目", problem.url, use_container_width=True
        )
        if guide_col.button("开始导师引导", use_container_width=True):
            if not imported_matches:
                with st.spinner("正在载入完整题面和 Python 模板…"):
                    try:
                        _import_problem_into_workspace(problem.url)
                    except LeetCodeImportError as exc:
                        st.session_state.leetcode_import_error = str(exc)
            opening = build_tutor_opening(
                problem_id=problem.id,
                title_cn=problem.title_cn,
                difficulty=problem.difficulty,
                topic=problem.topic,
                focus=problem.focus,
                invariant_prompt=problem.invariant_prompt,
                attempt=attempt,
            )
            history: list[HistoryItem] = st.session_state.algorithm_messages
            history.append(
                {
                    "role": "user",
                    "content": f"开始导师带练：{problem.id}. {problem.title_cn}",
                    "display": f"开始导师带练：**{problem.id}. {problem.title_cn}**",
                }
            )
            history.append({"role": "assistant", "content": opening})
            st.rerun()

        mastered_col, review_col = st.columns(2)
        if mastered_col.button("这题已掌握", use_container_width=True):
            _save_problem_status(problem, "mastered")
            st.success("已记录为掌握。下一题会根据新的薄弱分布选择。")
        if review_col.button("这题需要复习", use_container_width=True):
            _save_problem_status(problem, "review")
            st.warning("已加入复习队列，之后会优先再次出现。")

    return pending


def render_algorithm_mode(
    *,
    settings: ProviderSettings,
    model: str,
    temperature: float,
    top_p: float,
    reasoning_effort: str,
    max_tokens: int,
    config: AppConfig,
) -> None:
    st.subheader("Algorithm Mode")
    st.write("导师安排训练节奏；你可以在当前页面导题、写代码、运行，并把现场交给导师。")
    pending = render_mentor_panel()
    review_request = render_solution_workbench()
    if review_request:
        pending = review_request

    render_floating_mentor(
        mode="algorithm",
        model=model,
        settings=settings,
        temperature=temperature,
        top_p=top_p,
        reasoning_effort=reasoning_effort,
        max_tokens=max_tokens,
        config=config,
    )

    st.markdown("#### 导师对练记录")
    history: list[HistoryItem] = st.session_state.algorithm_messages
    render_history(history, render_mermaid=False)
    chat_prompt = st.chat_input(
        "直接问当前代码；题面、编辑器、测试和最近运行结果会一起交给导师",
        key="algorithm_chat_input",
    )
    if chat_prompt:
        pending = (
            _workspace_request(chat_prompt, trigger="代码工作区对话"),
            chat_prompt,
        )

    if pending:
        submit_to_tutor(
            mode="algorithm",
            content=pending[0],
            display=pending[1],
            settings=settings,
            model=model,
            temperature=temperature,
            top_p=top_p,
            reasoning_effort=reasoning_effort,
            max_tokens=max_tokens,
            config=config,
        )


def render_system_design_mode(
    *,
    settings: ProviderSettings,
    model: str,
    temperature: float,
    top_p: float,
    reasoning_effort: str,
    max_tokens: int,
    config: AppConfig,
) -> None:
    st.subheader("System Design Mode")
    st.write("从容量估算开始，逐步承受 SPOF、缓存和高并发压力测试。")

    requirement = st.text_area(
        "设计需求",
        key="system_requirement",
        height=120,
        placeholder="例如：设计一个支持 1 亿用户的短链接系统",
    )
    pending: tuple[str, str] | None = None
    if st.button("开始架构面试", type="primary"):
        if not requirement.strip():
            st.error("请先填写一个系统设计需求。")
        else:
            pending = (
                build_system_design_request(requirement),
                f"开始系统设计：**{requirement.strip()}**",
            )

    render_floating_mentor(
        mode="system_design",
        model=model,
        settings=settings,
        temperature=temperature,
        top_p=top_p,
        reasoning_effort=reasoning_effort,
        max_tokens=max_tokens,
        config=config,
    )

    st.markdown("#### 架构对练")
    history: list[HistoryItem] = st.session_state.system_design_messages
    render_history(history, render_mermaid=True)
    chat_prompt = st.chat_input(
        "回答容量问题、解释取舍，或要求继续压力测试",
        key="system_design_chat_input",
    )
    if chat_prompt:
        pending = (chat_prompt, chat_prompt)

    if pending:
        submit_to_tutor(
            mode="system_design",
            content=pending[0],
            display=pending[1],
            settings=settings,
            model=model,
            temperature=temperature,
            top_p=top_p,
            reasoning_effort=reasoning_effort,
            max_tokens=max_tokens,
            config=config,
        )


def main() -> None:
    configure_page()
    config = initialize_state()

    st.title("LeetTutor-Local")
    st.caption("本地大模型驱动的算法面试与系统设计训练场")
    if st.session_state.config_load_error:
        st.warning(
            f"配置文件没有成功载入，当前使用默认值。{st.session_state.config_load_error}"
        )
    if st.session_state.progress_load_error:
        st.warning(
            f"学习进度没有成功载入，本次从空进度开始。{st.session_state.progress_load_error}"
        )

    st.radio(
        "学习模式",
        list(MODE_LABELS),
        horizontal=True,
        key="mode_label",
        label_visibility="collapsed",
    )
    mode: Literal["algorithm", "system_design"] = MODE_LABELS[
        st.session_state.mode_label
    ]  # type: ignore[assignment]
    (
        settings,
        model,
        temperature,
        top_p,
        reasoning_effort,
        max_tokens,
    ) = render_sidebar(config, mode)
    st.markdown(
        f'<p class="status-line">{html.escape(settings.provider)} · '
        f'{html.escape(model or "尚未选择模型")} · 请求目标 '
        f'{html.escape(settings.endpoint)}</p>',
        unsafe_allow_html=True,
    )

    if mode == "algorithm":
        render_algorithm_mode(
            settings=settings,
            model=model,
            temperature=temperature,
            top_p=top_p,
            reasoning_effort=reasoning_effort,
            max_tokens=max_tokens,
            config=config,
        )
    else:
        render_system_design_mode(
            settings=settings,
            model=model,
            temperature=temperature,
            top_p=top_p,
            reasoning_effort=reasoning_effort,
            max_tokens=max_tokens,
            config=config,
        )


if __name__ == "__main__":
    main()
