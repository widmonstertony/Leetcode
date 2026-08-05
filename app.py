"""Streamlit entry point for LeetTutor-Local."""

from __future__ import annotations

import atexit
import base64
import html
import json
import re
import shutil
import subprocess
import time
from dataclasses import replace
from pathlib import Path
from typing import Any, Literal

import streamlit as st
import streamlit.components.v1 as components
from code_editor import code_editor

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
from leettutor.metal_runtime import (
    AMD_METAL_MODEL,
    AMD_METAL_PROVIDER,
    MetalRuntimeError,
    ensure_metal_runtime,
    inspect_metal_setup,
    install_metal_runtime,
    open_xcode_tools_installer,
)
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
from leettutor.system_design_curriculum import (
    SYSTEM_DESIGN_TRACKS,
    SystemDesignCase,
    choose_next_system_design_case,
    get_system_design_case,
)
from leettutor.visuals import (
    algorithm_pattern_mermaid,
    system_design_pattern_mermaid,
)


PROJECT_ROOT = Path(__file__).resolve().parent
PROGRESS_STORE = ProgressStore(PROJECT_ROOT / "study_progress.json")
INSTALLER_DIRECTORY = PROJECT_ROOT / ".leettutor" / "installers"
JARVIS_ASSET = PROJECT_ROOT / "assets" / "jarvis-ai-core.png"
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


def _jarvis_data_url() -> str:
    encoded = base64.b64encode(JARVIS_ASSET.read_bytes()).decode("ascii")
    return f"data:image/png;base64,{encoded}"


def _jarvis_identity_html(*, compact: bool = False) -> str:
    size_class = "jarvis-avatar compact" if compact else "jarvis-avatar"
    return (
        '<div class="jarvis-identity">'
        f'<img class="{size_class}" src="{_jarvis_data_url()}" alt="JARVIS AI core">'
        '<div><div class="jarvis-name">JARVIS</div>'
        '<div class="jarvis-role">AI INTERVIEW COPILOT</div></div></div>'
    )


def _ui(zh: str, en: str) -> str:
    """Return a UI string without coupling widget values to the language."""

    return en if st.session_state.get("ui_language", "zh") == "en" else zh


def _problem_title(problem: Problem) -> str:
    """Use the selected statement language for curriculum titles too."""

    if st.session_state.get("problem_language", "zh") == "en":
        return problem.title
    return problem.title_cn


def _request_problem_translation() -> None:
    st.session_state.problem_translation_requested = True


def _set_workspace_option(key: str, value: object) -> None:
    st.session_state[key] = value


def _set_training_mode(mode_label: str) -> None:
    """Switch modes from a real full-area button instead of a hidden radio."""

    st.session_state.mode_label = mode_label


def _set_code_language(language: str) -> None:
    st.session_state.code_language = language


def _restore_workspace_layout() -> None:
    st.session_state.show_problem_pane = True
    st.session_state.show_code_pane = True
    st.session_state.show_mentor_pane = True
    st.session_state.mentor_layout_mode = "悬浮"


def _queue_floating_mentor_action(mode: str, action: str) -> None:
    """Capture the submitted draft, then clear the composer before the rerun."""

    st.session_state[f"{mode}_floating_mentor_action"] = {
        "action": action,
        "question": str(
            st.session_state.get("floating_mentor_question", "")
        ).strip(),
    }
    st.session_state.floating_mentor_question = ""


def install_mentor_client_controller() -> None:
    """Install persistent drag and conversation behavior for the floating mentor."""

    components.html(
        """
        <script>
        (() => {
          const win = window.parent;
          const doc = win.document;
          const storageKey = "leettutor-floating-mentor-position-v2";
          const sizeStorageKey = "leettutor-floating-mentor-size-v5";
          const controllerVersion = 19;
          const cornerMargin = 18;
          const snapDistance = 132;

          // Replace an older observer during Streamlit hot reloads so UI fixes
          // take effect without requiring the user to close the whole app.
          if (win.__leettutorMentorControllerVersion !== controllerVersion) {
            win.__leettutorMentorDragObserver?.disconnect();
            delete win.__leettutorMentorDragObserver;
            doc.getElementById("leettutor-mentor-resize-overlay")?.remove();
            const openPopover = doc.querySelector(".st-key-floating_composer")
              ?.closest('[data-testid="stPopoverBody"]');
            openPopover?.__leettutorResizeObserver?.disconnect();
            if (openPopover) {
              delete openPopover.__leettutorResizeObserver;
              delete openPopover.__leettutorConversationBound;
            }
            win.__leettutorMentorControllerVersion = controllerVersion;
          }

          const clamp = (root, left, top) => {
            const width = root.offsetWidth || 180;
            const height = root.offsetHeight || 64;
            return {
              left: Math.max(8, Math.min(left, win.innerWidth - width - 8)),
              top: Math.max(8, Math.min(top, win.innerHeight - height - 8)),
            };
          };

          const cornerPoints = (root) => {
            const width = root.offsetWidth || 180;
            const height = root.offsetHeight || 64;
            const right = Math.max(cornerMargin, win.innerWidth - width - cornerMargin);
            const bottom = Math.max(cornerMargin, win.innerHeight - height - cornerMargin);
            return {
              "top-left": {left: cornerMargin, top: cornerMargin},
              "top-right": {left: right, top: cornerMargin},
              "bottom-left": {left: cornerMargin, top: bottom},
              "bottom-right": {left: right, top: bottom},
            };
          };

          const applyPosition = (root, point) => {
            root.style.right = "auto";
            root.style.bottom = "auto";
            root.style.left = `${point.left}px`;
            root.style.top = `${point.top}px`;
          };

          const nearestCorner = (root, left, top) => {
            const width = root.offsetWidth || 180;
            const height = root.offsetHeight || 64;
            const centerX = left + width / 2;
            const centerY = top + height / 2;
            let nearest = null;
            for (const [anchor, point] of Object.entries(cornerPoints(root))) {
              const distance = Math.hypot(
                centerX - (point.left + width / 2),
                centerY - (point.top + height / 2),
              );
              if (!nearest || distance < nearest.distance) {
                nearest = {...point, anchor, distance};
              }
            }
            return nearest && nearest.distance <= snapDistance ? nearest : null;
          };

          const load = () => {
            try {
              return JSON.parse(win.localStorage.getItem(storageKey) || "null");
            } catch (_) {
              return null;
            }
          };

          const save = (root, anchor = null) => {
            try {
              const rect = root.getBoundingClientRect();
              win.localStorage.setItem(storageKey, JSON.stringify({
                left: rect.left,
                top: rect.top,
                anchor,
              }));
            } catch (_) {}
          };

          const restore = (root) => {
            try {
              const saved = load();
              if (!saved) return;
              if (saved.anchor && cornerPoints(root)[saved.anchor]) {
                applyPosition(root, cornerPoints(root)[saved.anchor]);
                return;
              }
              if (!Number.isFinite(saved.left) || !Number.isFinite(saved.top)) return;
              applyPosition(root, clamp(root, saved.left, saved.top));
            } catch (_) {}
          };

          const syncResizeOverlay = (popover, overlay) => {
            const rect = popover.getBoundingClientRect();
            const isDialogChild = overlay.parentElement === popover;
            overlay.style.left = isDialogChild
              ? `${popover.scrollLeft}px`
              : `${rect.left}px`;
            overlay.style.top = isDialogChild
              ? `${popover.scrollTop}px`
              : `${rect.top}px`;
            overlay.style.width = `${rect.width}px`;
            overlay.style.height = `${rect.height}px`;
          };

          const lockPopoverToViewport = (popover) => {
            const rect = popover.getBoundingClientRect();
            const width = Math.min(rect.width, win.innerWidth - 16);
            const height = Math.min(rect.height, win.innerHeight - 16);
            const left = Math.max(8, Math.min(rect.left, win.innerWidth - width - 8));
            const top = Math.max(8, Math.min(rect.top, win.innerHeight - height - 8));
            popover.classList.add("mentor-resize-locked");
            for (const [property, value] of Object.entries({
              position: "fixed",
              inset: "auto",
              left: `${left}px`,
              top: `${top}px`,
              width: `${width}px`,
              height: `${height}px`,
              margin: "0",
              transform: "none",
              translate: "none",
              scale: "1",
              opacity: "1",
            })) {
              popover.style.setProperty(property, value, "important");
            }
          };

          const installResizeOverlay = (popover) => {
            doc.getElementById("leettutor-mentor-resize-overlay")?.remove();
            const overlay = doc.createElement("div");
            overlay.id = "leettutor-mentor-resize-overlay";
            overlay.className = "mentor-resize-overlay";

            const bindCorner = (corner) => {
              const handle = doc.createElement("div");
              handle.className = `mentor-resize-handle mentor-resize-${corner}`;
              handle.dataset.corner = corner;
              handle.title = "拖动调整 JARVIS 窗口大小";
              overlay.appendChild(handle);
              handle.addEventListener("pointerdown", (event) => {
                if (event.button !== 0) return;
                event.preventDefault();
                event.stopPropagation();
                const startRect = popover.getBoundingClientRect();
                const startX = event.clientX;
                const startY = event.clientY;
                const minWidth = Math.min(340, win.innerWidth - 16);
                const minHeight = Math.min(360, win.innerHeight - 16);
                const maxWidth = win.innerWidth - 16;
                const maxHeight = win.innerHeight - 16;
                let didResize = false;

                // Streamlit's popover engine normally owns transform/translate.
                // Lock the live window to viewport coordinates before resizing so
                // the pointer and the selected corner always move one-to-one.
                popover.classList.add("mentor-resize-locked");
                for (const [property, value] of Object.entries({
                  position: "fixed",
                  inset: "auto",
                  left: `${startRect.left}px`,
                  top: `${startRect.top}px`,
                  width: `${startRect.width}px`,
                  height: `${startRect.height}px`,
                  margin: "0",
                  transform: "none",
                  translate: "none",
                  scale: "1",
                  opacity: "1",
                  animation: "none",
                })) {
                  popover.style.setProperty(property, value, "important");
                }

                const moveResize = (moveEvent) => {
                  if (moveEvent.pointerId !== event.pointerId) return;
                  didResize = true;
                  popover.__leettutorUserResized = true;
                  const dx = moveEvent.clientX - startX;
                  const dy = moveEvent.clientY - startY;
                  const fromLeft = corner.includes("left");
                  const fromTop = corner.includes("top");
                  const width = Math.max(
                    minWidth,
                    Math.min(maxWidth, startRect.width + (fromLeft ? -dx : dx)),
                  );
                  const height = Math.max(
                    minHeight,
                    Math.min(maxHeight, startRect.height + (fromTop ? -dy : dy)),
                  );
                  const left = Math.max(
                    8,
                    Math.min(
                      fromLeft ? startRect.right - width : startRect.left,
                      win.innerWidth - width - 8,
                    ),
                  );
                  const top = Math.max(
                    8,
                    Math.min(
                      fromTop ? startRect.bottom - height : startRect.top,
                      win.innerHeight - height - 8,
                    ),
                  );
                  popover.style.setProperty("left", `${left}px`, "important");
                  popover.style.setProperty("top", `${top}px`, "important");
                  popover.style.setProperty("width", `${width}px`, "important");
                  popover.style.setProperty("height", `${height}px`, "important");
                  syncResizeOverlay(popover, overlay);
                };
                const finishResize = (finishEvent) => {
                  if (finishEvent.pointerId !== event.pointerId) return;
                  finishEvent.preventDefault();
                  finishEvent.stopImmediatePropagation();
                  doc.body.classList.remove("mentor-window-resizing");
                  win.removeEventListener("pointermove", moveResize, true);
                  win.removeEventListener("pointerup", finishResize, true);
                  win.removeEventListener("pointercancel", finishResize, true);
                  if (didResize) {
                    const swallowResizeClick = (clickEvent) => {
                      clickEvent.preventDefault();
                      clickEvent.stopImmediatePropagation();
                    };
                    win.addEventListener("click", swallowResizeClick, {capture: true, once: true});
                    win.setTimeout(
                      () => win.removeEventListener("click", swallowResizeClick, true),
                      160,
                    );
                  }
                };

                doc.body.classList.add("mentor-window-resizing");
                win.addEventListener("pointermove", moveResize, true);
                win.addEventListener("pointerup", finishResize, true);
                win.addEventListener("pointercancel", finishResize, true);
                try { handle.setPointerCapture(event.pointerId); } catch (_) {}
              });
            };
            for (const corner of ["top-left", "top-right", "bottom-left", "bottom-right"]) {
              bindCorner(corner);
            }
            // Keep resize handles inside the dialog's DOM ownership boundary.
            // Otherwise Streamlit treats pointer-up as an outside click and
            // closes the popover immediately after a resize.
            popover.appendChild(overlay);
            syncResizeOverlay(popover, overlay);
            return overlay;
          };

          const bindProductMark = () => {
            const mark = doc.querySelector(".leettutor-product-mark");
            if (!mark || mark.__leettutorSidebarBound) return;
            mark.__leettutorSidebarBound = true;
            mark.addEventListener("click", () => {
              const nativeToggle = doc.querySelector(
                '[data-testid="stExpandSidebarButton"], '
                + '[data-testid="stSidebarCollapseButton"] button',
              );
              nativeToggle?.click();
            });
          };

          const bindConversation = () => {
            const composer = doc.querySelector(".st-key-floating_composer");
            const popover = composer?.closest('[data-testid="stPopoverBody"]');
            if (!popover) {
              doc.getElementById("leettutor-mentor-resize-overlay")?.remove();
              return;
            }
            if (popover.__leettutorConversationBound) {
              let overlay = doc.getElementById("leettutor-mentor-resize-overlay");
              if (!overlay || overlay.parentElement !== popover) {
                overlay = installResizeOverlay(popover);
              }
              syncResizeOverlay(popover, overlay);
              return;
            }
            popover.__leettutorConversationBound = true;
            const scrollTarget = popover.querySelector(".st-key-floating_transcript");
            try {
              const savedSize = JSON.parse(
                win.localStorage.getItem(sizeStorageKey) || "null",
              );
              if (savedSize && Number.isFinite(savedSize.width)) {
                popover.style.width = `${Math.max(
                  Math.min(340, win.innerWidth - 16),
                  Math.min(savedSize.width, win.innerWidth - 16),
                )}px`;
              }
              if (savedSize && Number.isFinite(savedSize.height)) {
                popover.style.height = `${Math.max(
                  Math.min(360, win.innerHeight - 16),
                  Math.min(savedSize.height, win.innerHeight - 16),
                )}px`;
              }
            } catch (_) {}

            const trigger = doc.querySelector(".st-key-floating_mentor button");
            const triggerRect = trigger?.getBoundingClientRect();
            popover.style.scale = "1";
            const popoverRect = popover.getBoundingClientRect();
            popover.style.removeProperty("scale");
            if (triggerRect) {
              popover.style.setProperty(
                "--mentor-origin-x",
                `${triggerRect.left + triggerRect.width / 2 - popoverRect.left}px`,
              );
              popover.style.setProperty(
                "--mentor-origin-y",
                `${triggerRect.top + triggerRect.height / 2 - popoverRect.top}px`,
              );
            }

            const overlay = installResizeOverlay(popover);
            popover.addEventListener("scroll", () => {
              const liveOverlay = doc.getElementById("leettutor-mentor-resize-overlay");
              if (liveOverlay?.parentElement === popover) {
                syncResizeOverlay(popover, liveOverlay);
              }
            }, {passive: true});
            const settlePopover = () => {
              if (!popover.isConnected || !overlay.isConnected) return;
              lockPopoverToViewport(popover);
              syncResizeOverlay(popover, overlay);
            };
            popover.addEventListener(
              "animationend",
              settlePopover,
              {once: true},
            );
            win.setTimeout(settlePopover, 340);
            if (win.ResizeObserver) {
              const resizeObserver = new win.ResizeObserver(() => {
                const width = popover.offsetWidth;
                const height = popover.offsetHeight;
                syncResizeOverlay(popover, overlay);
                if (
                  width < 1
                  || height < 1
                  || !popover.__leettutorUserResized
                ) return;
                try {
                  win.localStorage.setItem(sizeStorageKey, JSON.stringify({width, height}));
                } catch (_) {}
              });
              resizeObserver.observe(popover);
              popover.__leettutorResizeObserver = resizeObserver;
            }

            win.requestAnimationFrame(() => {
              popover.classList.add("mentor-popover-ready");
              syncResizeOverlay(popover, overlay);
              const mentorBusy = doc.querySelector(".st-key-floating_mentor")
                ?.classList.contains("mentor-busy");
              if (!mentorBusy) {
                popover.scrollTop = 0;
                scrollTarget?.scrollTo({top: scrollTarget.scrollHeight, behavior: "auto"});
              }
            });
          };

          const bind = () => {
            bindProductMark();
            bindConversation();
            const root = doc.querySelector(".st-key-floating_mentor");
            const button = root?.querySelector("button");
            if (!root || !button) return;

            // Streamlit can replace the trigger while a response is streaming.
            // Reconcile the newest state on every DOM mutation so an old busy
            // class cannot survive after the model has completed.
            const mentorState = win.__leettutorMentorClientState;
            if (mentorState?.state === "done" || mentorState?.state === "error") {
              root.classList.remove("mentor-busy");
              button.removeAttribute("data-mentor-label");
              button.removeAttribute("aria-busy");
              if (
                mentorState.state === "done"
                && button.getAttribute("aria-expanded") !== "true"
              ) {
                root.classList.add("mentor-has-update");
              }
            } else if (mentorState?.state) {
              root.classList.add("mentor-busy");
              root.classList.remove("mentor-has-update");
              button.setAttribute("data-mentor-label", mentorState.label || "JARVIS");
              button.setAttribute("aria-busy", "true");
            }

            if (button.__leettutorMentorDragBound) return;
            button.__leettutorMentorDragBound = true;
            restore(root);

            let gesture = null;
            let suppressClick = false;
            const move = (event) => {
              if (!gesture || event.pointerId !== gesture.pointerId) return;
              const dx = event.clientX - gesture.startX;
              const dy = event.clientY - gesture.startY;
              if (!gesture.moved && Math.hypot(dx, dy) < 6) return;
              gesture.moved = true;
              event.preventDefault();
              const point = clamp(root, gesture.left + dx, gesture.top + dy);
              root.classList.add("mentor-dragging");
              applyPosition(root, point);
            };
            const finish = (event) => {
              if (!gesture || event.pointerId !== gesture.pointerId) return;
              if (gesture.moved) {
                suppressClick = true;
                const rect = root.getBoundingClientRect();
                const snapped = nearestCorner(root, rect.left, rect.top);
                root.classList.remove("mentor-dragging");
                if (snapped) {
                  root.classList.add("mentor-snapping");
                  applyPosition(root, snapped);
                  save(root, snapped.anchor);
                  setTimeout(() => root.classList.remove("mentor-snapping"), 280);
                } else {
                  save(root);
                }
                setTimeout(() => { suppressClick = false; }, 120);
              }
              root.classList.remove("mentor-dragging");
              gesture = null;
              win.removeEventListener("pointermove", move, true);
              win.removeEventListener("pointerup", finish, true);
              win.removeEventListener("pointercancel", finish, true);
            };
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
              // Window-level listeners keep a long diagonal drag alive after the
              // pointer leaves the compact mentor button. Pointer capture remains
              // as a best-effort optimization for browsers that support it fully.
              win.addEventListener("pointermove", move, true);
              win.addEventListener("pointerup", finish, true);
              win.addEventListener("pointercancel", finish, true);
              try { button.setPointerCapture(event.pointerId); } catch (_) {}
            });
            button.addEventListener("click", (event) => {
              if (suppressClick) {
                event.preventDefault();
                event.stopImmediatePropagation();
                return;
              }
              win.setTimeout(() => {
                if (button.getAttribute("aria-expanded") === "true") {
                  root.classList.remove("mentor-has-update");
                }
              }, 0);
            }, true);
          };

          const start = () => {
            const target = doc.body || doc.documentElement;
            if (!target) {
              win.setTimeout(start, 50);
              return;
            }
            if (!win.__leettutorMentorDragObserver) {
              win.__leettutorMentorDragObserver = new win.MutationObserver(bind);
              win.__leettutorMentorDragObserver.observe(target, {childList: true, subtree: true});
              win.addEventListener("resize", () => {
              const root = doc.querySelector(".st-key-floating_mentor");
              if (!root) return;
              const saved = load();
              if (saved?.anchor && cornerPoints(root)[saved.anchor]) {
                applyPosition(root, cornerPoints(root)[saved.anchor]);
                save(root, saved.anchor);
              } else if (root.style.left) {
                const rect = root.getBoundingClientRect();
                applyPosition(root, clamp(root, rect.left, rect.top));
                save(root);
              }
              });
            }
            bind();
          };
          start();
        })();
        </script>
        """,
        height=0,
        width=0,
    )


def install_workspace_split_controller() -> None:
    """Install the draggable divider used by the default two-pane workspace."""

    components.html(
        """
        <script>
        (() => {
          const win = window.parent;
          const doc = win.document;
          const storageKey = "leettutor-workspace-split-v1";
          const defaultRatio = 0.45;
          const minRatio = 0.26;
          const maxRatio = 0.74;

          const clampRatio = (value) => Math.max(minRatio, Math.min(maxRatio, value));
          const readRatio = () => {
            const value = Number.parseFloat(win.localStorage.getItem(storageKey) || "");
            return Number.isFinite(value) ? clampRatio(value) : defaultRatio;
          };
          const saveRatio = (ratio) => {
            try { win.localStorage.setItem(storageKey, String(clampRatio(ratio))); }
            catch (_) {}
          };

          const findLayout = () => {
            if (win.innerWidth < 901) return null;
            const problem = doc.querySelector(".st-key-problem_pane");
            const code = doc.querySelector(".st-key-code_pane");
            const problemColumn = problem?.closest('[data-testid="stColumn"]');
            const codeColumn = code?.closest('[data-testid="stColumn"]');
            const horizontal = problemColumn?.closest('[data-testid="stHorizontalBlock"]');
            if (!problemColumn || !codeColumn || !horizontal) return null;
            if (codeColumn.closest('[data-testid="stHorizontalBlock"]') !== horizontal) return null;
            const columns = Array.from(horizontal.children).filter(
              (child) => child.matches?.('[data-testid="stColumn"]'),
            );
            if (columns.length !== 2) return null;
            return {horizontal, problemColumn, codeColumn};
          };

          const clearColumnOverrides = () => {
            for (const selector of [".st-key-problem_pane", ".st-key-code_pane"]) {
              const column = doc.querySelector(selector)?.closest('[data-testid="stColumn"]');
              if (!column) continue;
              for (const property of ["flex", "width", "max-width", "min-width"]) {
                column.style.removeProperty(property);
              }
            }
          };

          const applyRatio = (layout, ratio) => {
            const value = clampRatio(ratio);
            const style = win.getComputedStyle(layout.horizontal);
            const gap = Number.parseFloat(style.columnGap || style.gap || "16") || 16;
            // Streamlit columns use flex-wrap. clientWidth is integer-rounded and can
            // exceed the real CSS-pixel width by a fraction, which is enough to wrap
            // the right pane onto a new row. Measure the fractional rect and keep a
            // one-pixel safety allowance so both panes always remain side by side.
            const available = Math.max(
              320,
              layout.horizontal.getBoundingClientRect().width - gap - 1,
            );
            const problemWidth = available * value;
            const codeWidth = available - problemWidth;
            for (const [column, width] of [
              [layout.problemColumn, problemWidth],
              [layout.codeColumn, codeWidth],
            ]) {
              column.style.flex = `0 0 ${width}px`;
              column.style.width = `${width}px`;
              column.style.maxWidth = `${width}px`;
              column.style.minWidth = "0";
            }
            const handle = layout.horizontal.querySelector(".leettutor-split-handle");
            if (handle) {
              const parentRect = layout.horizontal.getBoundingClientRect();
              const leftRect = layout.problemColumn.getBoundingClientRect();
              const rightRect = layout.codeColumn.getBoundingClientRect();
              const center = (leftRect.right + rightRect.left) / 2 - parentRect.left;
              handle.style.left = `${center}px`;
              handle.dataset.ratio = String(value);
              handle.setAttribute("aria-valuenow", String(Math.round(value * 100)));
            }
            return value;
          };

          const removeOrphanHandles = (keep = null) => {
            for (const handle of doc.querySelectorAll(".leettutor-split-handle")) {
              if (handle !== keep) handle.remove();
            }
          };

          const bind = () => {
            const layout = findLayout();
            if (!layout) {
              // A third docked mentor column (or a hidden pane) means this is no
              // longer the resizable two-pane layout. Remove the pixel widths left
              // by the splitter so Streamlit can size every visible column again.
              clearColumnOverrides();
              removeOrphanHandles();
              return;
            }
            layout.horizontal.style.position = "relative";
            let handle = layout.horizontal.querySelector(".leettutor-split-handle");
            if (!handle) {
              handle = doc.createElement("div");
              handle.className = "leettutor-split-handle";
              handle.setAttribute("role", "separator");
              handle.setAttribute("aria-label", "调整题目和代码宽度");
              handle.setAttribute("aria-orientation", "vertical");
              handle.setAttribute("aria-valuemin", String(minRatio * 100));
              handle.setAttribute("aria-valuemax", String(maxRatio * 100));
              handle.tabIndex = 0;
              handle.title = "拖动调整题目和代码宽度；双击恢复默认";
              handle.innerHTML = '<span aria-hidden="true"></span>';
              layout.horizontal.appendChild(handle);
            }

            // Streamlit may reconcile an imperatively inserted DOM node between
            // reruns. Keep element binding separate from creation so an existing
            // but newly reconciled handle always regains its interactions.
            if (!handle.__leettutorSplitBound) {
              handle.__leettutorSplitBound = true;
              let gesture = null;
              handle.addEventListener("pointerdown", (event) => {
                if (event.button !== 0) return;
                const current = findLayout();
                if (!current) return;
                const rect = current.horizontal.getBoundingClientRect();
                const style = win.getComputedStyle(current.horizontal);
                const gap = Number.parseFloat(style.columnGap || style.gap || "16") || 16;
                gesture = {pointerId: event.pointerId, layout: current, rect, gap};
                handle.classList.add("is-dragging");
                doc.body.classList.add("leettutor-split-resizing");
                try { handle.setPointerCapture(event.pointerId); } catch (_) {}
                event.preventDefault();
              });
              handle.addEventListener("pointermove", (event) => {
                if (!gesture || event.pointerId !== gesture.pointerId) return;
                const available = Math.max(
                  320,
                  gesture.rect.width - gesture.gap - 1,
                );
                const leftWidth = event.clientX - gesture.rect.left - gesture.gap / 2;
                applyRatio(gesture.layout, leftWidth / available);
                event.preventDefault();
              });
              const finish = (event) => {
                if (!gesture || event.pointerId !== gesture.pointerId) return;
                saveRatio(Number.parseFloat(handle.dataset.ratio || String(defaultRatio)));
                handle.classList.remove("is-dragging");
                doc.body.classList.remove("leettutor-split-resizing");
                gesture = null;
              };
              handle.addEventListener("pointerup", finish);
              handle.addEventListener("pointercancel", finish);
              handle.addEventListener("dblclick", () => {
                const current = findLayout();
                if (!current) return;
                applyRatio(current, defaultRatio);
                saveRatio(defaultRatio);
              });
              handle.addEventListener("keydown", (event) => {
                if (!["ArrowLeft", "ArrowRight", "Home"].includes(event.key)) return;
                const current = findLayout();
                if (!current) return;
                const existing = Number.parseFloat(handle.dataset.ratio || String(readRatio()));
                const next = event.key === "Home"
                  ? defaultRatio
                  : existing + (event.key === "ArrowLeft" ? -0.02 : 0.02);
                const applied = applyRatio(current, next);
                saveRatio(applied);
                event.preventDefault();
              });
            }
            removeOrphanHandles(handle);
            applyRatio(layout, readRatio());
          };

          const start = () => {
            const target = doc.body || doc.documentElement;
            if (!target) {
              win.setTimeout(start, 50);
              return;
            }
            if (!win.__leettutorSplitObserver) {
              let frame = 0;
              win.__leettutorSplitObserver = new win.MutationObserver(() => {
                win.cancelAnimationFrame(frame);
                frame = win.requestAnimationFrame(bind);
              });
              win.__leettutorSplitObserver.observe(target, {childList: true, subtree: true});
              win.addEventListener("resize", bind);
            }
            bind();
          };
          start();
        })();
        </script>
        """,
        height=0,
        width=0,
    )


def install_code_editor_controller() -> None:
    """Give the native editor IDE-like indentation and run shortcuts."""

    components.html(
        r"""
        <script>
        (() => {
          const win = window.parent;
          const doc = win.document;
          const indent = "    ";

          const setEditorValue = (textarea, value, start, end = start) => {
            const scrollTop = textarea.scrollTop;
            const setter = Object.getOwnPropertyDescriptor(
              win.HTMLTextAreaElement.prototype,
              "value",
            )?.set;
            if (setter) setter.call(textarea, value);
            else textarea.value = value;
            textarea.dispatchEvent(new win.InputEvent("input", {
              bubbles: true,
              inputType: "insertText",
            }));
            textarea.focus({preventScroll: true});
            textarea.setSelectionRange(start, end);
            textarea.scrollTop = scrollTop;
          };

          const indentSelection = (textarea, reverse) => {
            const value = textarea.value;
            const start = textarea.selectionStart;
            const end = textarea.selectionEnd;
            const lineStart = value.lastIndexOf("\n", Math.max(0, start - 1)) + 1;

            if (start === end && !reverse) {
              const column = start - lineStart;
              const spaces = indent.length - (column % indent.length);
              const insertion = " ".repeat(spaces);
              setEditorValue(
                textarea,
                value.slice(0, start) + insertion + value.slice(end),
                start + spaces,
              );
              return;
            }

            if (start === end && reverse) {
              const leading = value.slice(lineStart).match(/^[ \t]*/)?.[0] || "";
              const remove = leading.startsWith("\t")
                ? 1
                : Math.min(indent.length, leading.length);
              if (!remove) return;
              setEditorValue(
                textarea,
                value.slice(0, lineStart) + value.slice(lineStart + remove),
                Math.max(lineStart, start - remove),
              );
              return;
            }

            const adjustedEnd = end > lineStart && value[end - 1] === "\n" ? end - 1 : end;
            let blockEnd = value.indexOf("\n", adjustedEnd);
            if (blockEnd < 0) blockEnd = value.length;
            const lines = value.slice(lineStart, blockEnd).split("\n");
            const replacement = lines.map((line) => {
              if (!reverse) return indent + line;
              if (line.startsWith("\t")) return line.slice(1);
              return line.slice(Math.min(indent.length, line.match(/^ */)?.[0].length || 0));
            }).join("\n");
            setEditorValue(
              textarea,
              value.slice(0, lineStart) + replacement + value.slice(blockEnd),
              lineStart,
              lineStart + replacement.length,
            );
          };

          const insertNewline = (textarea) => {
            const value = textarea.value;
            const start = textarea.selectionStart;
            const end = textarea.selectionEnd;
            const lineStart = value.lastIndexOf("\n", Math.max(0, start - 1)) + 1;
            const beforeCursor = value.slice(lineStart, start);
            const leading = beforeCursor.match(/^[ \t]*/)?.[0] || "";
            const trimmed = beforeCursor.trimEnd();
            const extra = /[:{[(]$/.test(trimmed) ? indent : "";
            const insertion = "\n" + leading + extra;
            setEditorValue(
              textarea,
              value.slice(0, start) + insertion + value.slice(end),
              start + insertion.length,
            );
          };

          const bind = () => {
            const textarea = doc.querySelector(".st-key-code_editor textarea");
            if (!textarea || textarea.__leettutorEditorBound) return;
            textarea.__leettutorEditorBound = true;
            textarea.setAttribute("spellcheck", "false");
            textarea.addEventListener("keydown", (event) => {
              if (event.isComposing) return;
              if (event.key === "Tab") {
                event.preventDefault();
                event.stopPropagation();
                indentSelection(textarea, event.shiftKey);
                return;
              }
              if (event.key === "Enter" && (event.metaKey || event.ctrlKey)) {
                event.preventDefault();
                const selector = event.shiftKey
                  ? ".st-key-analyze_code button"
                  : ".st-key-run_code button";
                doc.querySelector(selector)?.click();
                return;
              }
              if (event.key === "Enter" && !event.altKey) {
                event.preventDefault();
                insertNewline(textarea);
              }
            });
          };

          // Streamlit may replace the textarea during a rerun. A light polling
          // binder is more reliable here than observing nodes across the sandboxed
          // component iframe boundary, and it does no work after a node is bound.
          if (!win.__leettutorEditorBindTimer) {
            win.__leettutorEditorBindTimer = win.setInterval(bind, 250);
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
    surface: Literal["main", "floating", "workspace"] = "main",
    state_mount: Any | None = None,
) -> None:
    """Reflect tutor activity on the floating button, browser title, and scroll."""

    labels = {
        "loading": "JARVIS 加载中…",
        "thinking": "JARVIS 思考中…",
        "answering": "JARVIS 回答中…",
        "done": "JARVIS",
        "error": "JARVIS 调用失败",
    }
    state_json = json.dumps(state)
    label_json = json.dumps(labels[state], ensure_ascii=False)
    anchor_json = json.dumps(anchor_id)
    surface_json = json.dumps(surface)
    client_script = f"""
        <script>
        (() => {{
          const win = window.parent;
          const doc = win.document;
          const state = {state_json};
          const label = {label_json};
          const anchorId = {anchor_json};
          const surface = {surface_json};
          win.__leettutorMentorClientState = {{state, label, updatedAt: Date.now()}};

          const syncMentorButton = () => {{
            const root = doc.querySelector(".st-key-floating_mentor");
            const button = root?.querySelector("button");
            if (!root || !button) return;
            if (state === "done" || state === "error") {{
              root.classList.remove("mentor-busy");
              button.removeAttribute("data-mentor-label");
              button.removeAttribute("aria-busy");
              if (state === "done" && button.getAttribute("aria-expanded") !== "true") {{
                root.classList.add("mentor-has-update");
              }}
            }} else {{
              root.classList.add("mentor-busy");
              root.classList.remove("mentor-has-update");
              button.setAttribute("data-mentor-label", label);
              button.setAttribute("aria-busy", "true");
            }}
          }};
          syncMentorButton();

          if (state === "done" || state === "error") {{
            // A Streamlit delta may replace the button immediately after this
            // component runs. Repeat terminal cleanup across that replacement
            // window; the persistent controller also reconciles future nodes.
            for (const delay of [50, 180, 480, 1000, 1800]) {{
              win.setTimeout(syncMentorButton, delay);
            }}
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
          }}

          const bindScrollTracking = (attempt = 0) => {{
            const target = anchorId ? doc.getElementById(anchorId) : null;
            if (!target) {{
              if (anchorId && attempt < 8) {{
                win.setTimeout(() => bindScrollTracking(attempt + 1), 50);
              }}
              return;
            }}
            const floatingTranscript = target.closest(".st-key-floating_transcript");
            const scrollRoot = surface === "floating"
              ? floatingTranscript?.querySelector(
                  '[data-testid="stVerticalBlockBorderWrapper"]',
                ) || target.closest('[data-testid="stPopoverBody"]')
              : surface === "workspace"
                ? target.closest('[data-testid="stVerticalBlockBorderWrapper"]')
                : null;
            if (scrollRoot && scrollRoot.__leettutorAnchorId !== anchorId) {{
              scrollRoot.__leettutorAnchorId = anchorId;
              scrollRoot.__leettutorAutoFollow = true;
            }}
            if (scrollRoot && !scrollRoot.__leettutorFollowBound) {{
              scrollRoot.__leettutorFollowBound = true;
              scrollRoot.addEventListener("scroll", () => {{
                if (Date.now() < (scrollRoot.__leettutorProgrammaticUntil || 0)) return;
                const distanceFromBottom = scrollRoot.scrollHeight
                  - scrollRoot.scrollTop
                  - scrollRoot.clientHeight;
                scrollRoot.__leettutorAutoFollow = distanceFromBottom <= 72;
              }}, {{passive: true}});
            }}
            const scroll = (behavior = "smooth", force = false) => {{
              if (scrollRoot) {{
                if (!force && scrollRoot.__leettutorAutoFollow === false) return;
                scrollRoot.__leettutorProgrammaticUntil = Date.now() + 120;
                scrollRoot.scrollTo({{
                  top: scrollRoot.scrollHeight,
                  behavior,
                }});
                return;
              }}
              target.scrollIntoView({{behavior, block: "center"}});
            }};
            win.setTimeout(() => scroll("smooth", state === "loading"), 30);
            if (surface === "floating" && (state === "done" || state === "error")) {{
              win.setTimeout(() => {{
                scroll("auto");
                const composer = doc.querySelector(".st-key-floating_composer textarea");
                composer?.focus({{preventScroll: true}});
              }}, 240);
            }}
          }};
          bindScrollTracking();
        }})();
        </script>
        """
    if state_mount is not None:
        state_mount.empty()
        with state_mount.container():
            components.html(client_script, height=0, width=0)
    else:
        components.html(client_script, height=0, width=0)


def configure_page() -> None:
    st.set_page_config(
        page_title="LeetTutor-Local",
        page_icon="🧠",
        layout="wide",
        initial_sidebar_state="collapsed",
    )
    styles = """
        <style>
        [data-testid="stHeader"] {
            top: 0.55rem !important;
            height: 0 !important;
            min-height: 0 !important;
            background: transparent;
            pointer-events: none;
        }
        [data-testid="stHeader"] button,
        [data-testid="stHeader"] [role="button"] {
            pointer-events: auto;
            width: 44px !important;
            min-width: 44px !important;
            height: 44px !important;
            min-height: 44px !important;
            padding: 0 !important;
            border: 1px solid color-mix(in srgb, var(--text-color) 18%, transparent) !important;
            border-radius: 11px !important;
            background: color-mix(in srgb, var(--background-color) 86%, transparent) !important;
            color: color-mix(in srgb, var(--text-color) 76%, transparent) !important;
            box-shadow: 0 7px 20px rgba(20, 47, 72, 0.09) !important;
            backdrop-filter: blur(14px);
        }
        [data-testid="stHeader"] button:hover,
        [data-testid="stHeader"] [role="button"]:hover {
            border-color: rgba(47, 185, 229, 0.52) !important;
            color: #23b7e2 !important;
            background: color-mix(in srgb, var(--secondary-background-color) 92%, transparent) !important;
        }
        [data-testid="stSidebarCollapseButton"] button,
        [data-testid="stExpandSidebarButton"],
        [data-testid="stMainMenu"] button {
            border-color: transparent !important;
            border-radius: 9px !important;
            background: transparent !important;
            color: color-mix(in srgb, var(--text-color) 78%, #35c4ec 22%) !important;
            box-shadow: none !important;
        }
        /* Repeating the attribute intentionally matches Streamlit's two-part
           header selector specificity, so the compact control cannot fall back to
           the native 44px size and drift off the shared center line. */
        [data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"] {
            position: fixed !important;
            top: 0.5rem !important;
            left: max(0.65rem, calc((100vw - 1780px) / 2 + 0.65rem)) !important;
            right: auto !important;
            z-index: 2;
            display: flex !important;
            width: 42px !important;
            min-width: 42px !important;
            height: 42px !important;
            min-height: 42px !important;
            align-items: center !important;
            justify-content: center !important;
            padding: 0 !important;
            border: 0 !important;
            border-radius: 50% !important;
            background: transparent !important;
            color: transparent !important;
            box-shadow: none !important;
            backdrop-filter: none;
        }
        [data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"]:hover {
            background: rgba(34, 184, 228, 0.08) !important;
            transform: scale(1.04);
        }
        [data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"]::after {
            content: "";
            position: absolute;
            top: 8px;
            right: -8px;
            width: 1px;
            height: 26px;
            background: color-mix(in srgb, var(--text-color) 13%, transparent);
        }
        [data-testid="stSidebarCollapseButton"] button {
            position: relative !important;
            width: 42px !important;
            min-width: 42px !important;
            height: 42px !important;
            min-height: 42px !important;
            padding: 0 !important;
            border: 0 !important;
            border-radius: 50% !important;
            background: transparent !important;
            box-shadow: none !important;
        }
        [data-testid="stMainMenu"] button {
            position: fixed !important;
            top: 0.82rem !important;
            right: max(0.85rem, calc((100vw - 1780px) / 2 + 0.85rem)) !important;
            z-index: 2;
            width: 32px !important;
            min-width: 32px !important;
            height: 32px !important;
            min-height: 32px !important;
            border: 0 !important;
            background: transparent !important;
        }
        [data-testid="stSidebarCollapseButton"] button > * {
            position: absolute !important;
            width: 1px !important;
            height: 1px !important;
            padding: 0 !important;
            margin: -1px !important;
            overflow: hidden !important;
            clip: rect(0, 0, 0, 0) !important;
            white-space: nowrap !important;
            border: 0 !important;
        }
        [data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"] > * {
            position: absolute !important;
            width: 1px !important;
            height: 1px !important;
            padding: 0 !important;
            margin: -1px !important;
            overflow: hidden !important;
            clip: rect(0, 0, 0, 0) !important;
            white-space: nowrap !important;
            border: 0 !important;
        }
        [data-testid="stSidebarCollapseButton"] button,
        [data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"],
        [data-testid="stMainMenu"] button {
            position: relative;
        }
        [data-testid="stSidebarCollapseButton"] button::before,
        [data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"]::before {
            content: "";
            position: absolute;
            left: 50%;
            top: 50%;
            box-sizing: border-box;
            width: 36px;
            height: 36px;
            border: 1px solid rgba(61, 201, 244, 0.34);
            border-radius: 50%;
            background: url("__JARVIS_DATA_URL__") center / cover no-repeat;
            box-shadow: 0 0 14px rgba(31, 178, 230, 0.22);
            transform: translate(-50%, -50%);
        }
        /* The product mark in the app bar is the single sidebar affordance.
           Keep Streamlit's native toggles available to the controller without
           exposing a second JARVIS avatar or a detached header control. */
        [data-testid="stSidebarCollapseButton"] button,
        [data-testid="stExpandSidebarButton"][data-testid="stExpandSidebarButton"] {
            position: fixed !important;
            left: 0 !important;
            top: 0 !important;
            width: 1px !important;
            min-width: 1px !important;
            height: 1px !important;
            min-height: 1px !important;
            padding: 0 !important;
            opacity: 0 !important;
            pointer-events: none !important;
            overflow: hidden !important;
        }
        [data-testid="stMainMenu"] button svg {
            display: none;
        }
        [data-testid="stMainMenu"] button::before {
            content: "";
            position: absolute;
            left: 50%;
            top: 50%;
            box-sizing: border-box;
            width: 18px;
            height: 18px;
            border: 1.7px solid currentColor;
            border-radius: 50%;
            background: linear-gradient(90deg, currentColor 50%, transparent 50%);
            transform: translate(-50%, -50%) rotate(-35deg);
        }
        .block-container {
            width: min(100%, 1780px);
            max-width: 1780px;
            padding: 0 clamp(0.75rem, 1.5vw, 1.5rem) 3rem;
        }
        /* Controller iframes must stay alive so their scripts execute. Remove
           them from layout without display:none, which prevents a fresh page
           from installing drag, resize, sidebar, and popover behavior. */
        .block-container > [data-testid="stVerticalBlock"]
        > [data-testid="stElementContainer"]:has(iframe) {
            position: absolute !important;
            width: 1px !important;
            height: 1px !important;
            min-height: 0 !important;
            margin: 0 !important;
            padding: 0 !important;
            overflow: hidden !important;
            opacity: 0 !important;
            pointer-events: none !important;
        }
        .block-container > [data-testid="stVerticalBlock"]
        > [data-testid="stElementContainer"]:has(style) {
            display: none;
        }
        /* The native sidebar and theme controls now overlay the first row.
           Reserve horizontal room for them, but no vertical blank strip. */
        .block-container [data-testid="stLayoutWrapper"]:has(h2) {
            padding-left: 2.5rem;
            padding-right: 2.5rem;
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
        .st-key-app_header {
            position: relative;
            /* Share the exact left edge used by the mission and workspace
               panels. The brand mark is the page's visual origin. */
            padding: 0.35rem 0;
            /* Streamlit already contributes a 1rem sibling gap. Pull back all
               but 4px so the product bar does not leave a blank lower strip. */
            margin-bottom: -0.75rem;
            border-bottom: 1px solid color-mix(in srgb, var(--text-color) 12%, transparent);
            background: linear-gradient(
                180deg,
                color-mix(in srgb, var(--secondary-background-color) 30%, transparent),
                transparent
            );
        }
        .st-key-app_header > [data-testid="stVerticalBlockBorderWrapper"] {
            box-sizing: border-box;
            overflow: visible;
            border: 0;
            border-radius: 0;
            background: transparent;
            box-shadow: none;
        }
        .st-key-app_header > [data-testid="stVerticalBlockBorderWrapper"]
        > [data-testid="stVerticalBlock"] {
            min-height: 60px;
            justify-content: center;
            padding: 0;
        }
        .st-key-app_header [data-testid="stVerticalBlock"] {
            gap: 0.45rem;
        }
        .st-key-app_header [data-testid="stHorizontalBlock"] {
            align-items: center;
        }
        .st-key-app_header > [data-testid="stLayoutWrapper"]
        > [data-testid="stHorizontalBlock"] {
            display: grid !important;
            grid-template-columns: minmax(0, 1fr) 18rem minmax(0, 1fr) !important;
            column-gap: 1rem !important;
            width: 100% !important;
        }
        .st-key-app_header > [data-testid="stLayoutWrapper"]
        > [data-testid="stHorizontalBlock"] > [data-testid="stColumn"] {
            width: 100% !important;
            min-width: 0 !important;
            flex: none !important;
        }
        .st-key-app_header > [data-testid="stLayoutWrapper"]
        > [data-testid="stHorizontalBlock"] > [data-testid="stColumn"]:first-child {
            height: 44px !important;
        }
        .st-key-app_header > [data-testid="stLayoutWrapper"]
        > [data-testid="stHorizontalBlock"] > [data-testid="stColumn"]:last-child {
            justify-self: end;
            width: min(15rem, 100%) !important;
            margin-right: 2.5rem;
        }
        .st-key-app_header button {
            width: 100%;
            min-height: 44px;
            height: 44px;
            border-radius: 11px;
        }
        .st-key-app_header [data-testid="stPopover"] {
            width: 100%;
        }
        .brand-shell {
            display: flex;
            align-items: center;
            height: 44px;
            min-width: 0;
            gap: 0.65rem;
        }
        .st-key-app_header button.leettutor-product-mark {
            display: inline-grid !important;
            place-items: center !important;
            flex: 0 0 44px !important;
            width: 44px !important;
            min-width: 44px !important;
            height: 44px !important;
            min-height: 44px !important;
            margin: 0 !important;
            padding: 0 !important;
            border: 1px solid rgba(84, 211, 249, 0.52) !important;
            border-radius: 13px !important;
            background: linear-gradient(145deg, #087fb3, #20b9df) !important;
            color: #fff !important;
            box-shadow: 0 8px 22px rgba(13, 139, 190, 0.22),
                        inset 0 1px 0 rgba(255, 255, 255, 0.34) !important;
            font: 850 0.76rem/1 ui-sans-serif, system-ui, sans-serif !important;
            letter-spacing: -0.02em !important;
            cursor: pointer;
            transition: transform 150ms ease, box-shadow 150ms ease !important;
        }
        .st-key-app_header button.leettutor-product-mark:hover {
            transform: translateY(-1px);
            box-shadow: 0 11px 26px rgba(13, 139, 190, 0.28),
                        inset 0 1px 0 rgba(255, 255, 255, 0.38) !important;
        }
        .st-key-app_header button.leettutor-product-mark:focus-visible {
            outline: 3px solid rgba(35, 184, 228, 0.26) !important;
            outline-offset: 3px;
        }
        .brand-copy {
            display: grid;
            grid-template-rows: 9px 20px 12px;
            align-content: center;
            row-gap: 1px;
            height: 44px;
            min-width: 0;
        }
        .brand-eyebrow {
            color: #268fc8;
            font-size: 0.56rem;
            font-weight: 800;
            letter-spacing: 0.16em;
            line-height: 9px;
            text-transform: uppercase;
        }
        .brand-title-row {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            height: 20px;
            margin: 0;
            white-space: nowrap;
        }
        .brand-title {
            font-size: 1.25rem;
            font-weight: 820;
            letter-spacing: -0.035em;
            line-height: 20px;
        }
        .brand-maker {
            color: color-mix(in srgb, var(--text-color) 58%, transparent);
            font-size: 0.64rem;
            font-weight: 720;
            letter-spacing: 0.06em;
            line-height: 12px;
            text-transform: uppercase;
        }
        .runtime-strip {
            display: flex;
            align-items: center;
            min-width: 0;
            gap: 0.45rem;
            color: color-mix(in srgb, var(--text-color) 55%, transparent);
            font-size: 0.6rem;
            line-height: 12px;
            margin: 0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .runtime-dot {
            width: 7px;
            height: 7px;
            flex: 0 0 7px;
            border-radius: 50%;
            background: #29c98b;
            box-shadow: 0 0 10px rgba(41, 201, 139, 0.55);
        }
        .visual-map-intro {
            display: flex;
            align-items: center;
            gap: 0.7rem;
            margin: 0.1rem 0 0.65rem;
            color: color-mix(in srgb, var(--text-color) 68%, transparent);
            font-size: 0.78rem;
            line-height: 1.35;
        }
        .visual-map-intro strong {
            color: var(--text-color);
            font-size: 0.86rem;
        }
        .visual-map-orbit {
            display: inline-grid;
            place-items: center;
            width: 34px;
            height: 34px;
            flex: 0 0 34px;
            border: 1px solid rgba(36, 184, 228, 0.42);
            border-radius: 50%;
            color: #22b8e4;
            background: rgba(34, 184, 228, 0.09);
            box-shadow: 0 0 18px rgba(34, 184, 228, 0.12);
        }
        body:has(.st-key-system_command_dock) .block-container {
            padding-bottom: 7.5rem;
        }
        .st-key-system_command_dock {
            position: fixed;
            left: 50%;
            bottom: max(0.75rem, env(safe-area-inset-bottom));
            z-index: 999900;
            box-sizing: border-box;
            width: min(820px, calc(100vw - 8rem));
            padding: 0.55rem 0.65rem 0.6rem;
            border: 1px solid color-mix(in srgb, var(--text-color) 16%, transparent);
            border-radius: 16px;
            background: color-mix(in srgb, var(--background-color) 88%, transparent);
            box-shadow: 0 18px 48px rgba(19, 48, 73, 0.18),
                        inset 0 1px 0 rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(22px) saturate(1.15);
            transform: translateX(-50%);
        }
        body:has([data-testid="stSidebar"][aria-expanded="true"])
        .st-key-system_command_dock {
            left: calc(50% + 165px);
            width: min(820px, calc(100vw - 380px));
        }
        .st-key-system_command_dock > [data-testid="stVerticalBlockBorderWrapper"] {
            border: 0;
            background: transparent;
        }
        .st-key-system_command_dock [data-testid="stVerticalBlock"] {
            gap: 0.35rem;
        }
        .st-key-system_command_dock [data-testid="stForm"] {
            padding: 0;
            border: 0;
        }
        .st-key-system_command_dock [data-testid="stHorizontalBlock"] {
            align-items: center;
            gap: 0.5rem;
        }
        .system-command-label {
            display: flex;
            align-items: center;
            gap: 0.45rem;
            margin: 0;
            color: color-mix(in srgb, var(--text-color) 68%, transparent);
            font-size: 0.7rem;
            font-weight: 780;
            letter-spacing: 0.08em;
            line-height: 1;
            text-transform: uppercase;
            white-space: nowrap;
        }
        .system-command-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #2ac98a;
            box-shadow: 0 0 11px rgba(42, 201, 138, 0.58);
        }
        .st-key-system_command_dock button,
        .st-key-system_command_dock [data-testid="stTextInputRootElement"] {
            min-height: 44px;
            border-radius: 11px;
        }
        .st-key-system_live_panel {
            position: sticky;
            top: 0.75rem;
            box-sizing: border-box;
            height: max(340px, min(640px, calc(100vh - 14rem))) !important;
            margin-top: 0;
            overflow-y: auto;
            padding: 0.85rem;
            border: 1px solid color-mix(in srgb, #25b8e5 30%, transparent);
            border-radius: 16px;
            border-color: color-mix(in srgb, #25b8e5 30%, transparent);
            background: linear-gradient(
                145deg,
                color-mix(in srgb, var(--secondary-background-color) 62%, transparent),
                color-mix(in srgb, var(--background-color) 94%, transparent)
            );
            box-shadow: 0 12px 34px rgba(18, 87, 119, 0.08);
        }
        [data-testid="stLayoutWrapper"]:has(> .st-key-system_live_panel) {
            height: max(340px, min(640px, calc(100vh - 14rem))) !important;
            min-height: 340px;
        }
        .st-key-system_live_panel [data-testid="stVerticalBlock"] {
            gap: 0.6rem;
        }
        .system-live-heading {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 1rem;
            margin: 0;
        }
        .system-live-identity {
            display: flex;
            align-items: center;
            gap: 0.58rem;
            min-width: 0;
        }
        .system-live-orb {
            width: 28px;
            height: 28px;
            flex: 0 0 28px;
            border: 1px solid rgba(43, 190, 232, 0.45);
            border-radius: 50%;
            background: radial-gradient(circle, #36c8ee 0 13%, #086a9c 15% 36%, #061d31 38% 100%);
            box-shadow: 0 0 14px rgba(43, 190, 232, 0.3);
        }
        .system-live-title {
            font-size: 0.82rem;
            font-weight: 820;
            letter-spacing: 0.06em;
            line-height: 1.1;
        }
        .system-live-subtitle {
            color: color-mix(in srgb, var(--text-color) 55%, transparent);
            font-size: 0.7rem;
            line-height: 1.2;
        }
        .system-live-status {
            display: block;
            flex: 0 0 auto;
            padding: 0.28rem 0.55rem;
            border: 1px solid color-mix(in srgb, var(--text-color) 12%, transparent);
            border-radius: 999px;
            color: color-mix(in srgb, var(--text-color) 60%, transparent);
            font-size: 0.66rem;
            font-weight: 720;
            text-align: center;
            white-space: nowrap;
        }
        .system-latest-question {
            margin: 0.1rem 0 0;
            color: color-mix(in srgb, var(--text-color) 58%, transparent);
            font-size: 0.74rem;
        }
        @media (max-width: 760px) {
            .st-key-system_command_dock,
            body:has([data-testid="stSidebar"][aria-expanded="true"])
            .st-key-system_command_dock {
                left: 0.6rem;
                right: 0.6rem;
                width: auto;
                transform: none;
            }
            .system-command-label {display: none;}
            .st-key-system_live_panel {
                position: relative;
                top: auto;
                height: auto !important;
                min-height: 340px;
            }
            [data-testid="stLayoutWrapper"]:has(> .st-key-system_live_panel) {
                height: auto !important;
                min-height: 340px;
            }
        }
        .st-key-app_mode [data-testid="stHorizontalBlock"] {
            gap: 0.45rem;
        }
        .st-key-app_mode button {
            cursor: pointer;
            font-weight: 720;
        }
        [data-testid="stBaseButton-primary"],
        button[kind="primary"] {
            background: linear-gradient(135deg, #168fc7, #20b9df) !important;
            border-color: rgba(65, 205, 245, 0.72) !important;
            box-shadow: 0 6px 18px rgba(17, 143, 192, 0.17);
        }
        [data-testid="stBaseButton-primary"]:hover,
        button[kind="primary"]:hover {
            background: linear-gradient(135deg, #117eaf, #16a9d1) !important;
            border-color: rgba(116, 225, 255, 0.9) !important;
        }
        .st-key-system_mission_control > [data-testid="stVerticalBlockBorderWrapper"] {
            border-color: rgba(47, 190, 239, 0.24);
            background:
                linear-gradient(90deg, rgba(31, 190, 237, 0.08) 0 3px, transparent 3px),
                radial-gradient(circle at 100% 0, rgba(41, 179, 238, 0.08), transparent 26%);
            box-shadow: 0 10px 30px rgba(18, 107, 149, 0.05);
        }
        .st-key-algorithm_mission_control [data-testid="stHorizontalBlock"],
        .st-key-system_mission_control [data-testid="stHorizontalBlock"] {
            align-items: center;
        }
        .st-key-algorithm_mission_control button,
        .st-key-system_mission_control button {
            min-height: 44px;
            border-radius: 11px;
        }
        .jarvis-identity {
            display: flex;
            align-items: center;
            gap: 0.72rem;
            margin: 0.1rem 0 0.65rem;
        }
        .jarvis-avatar {
            width: 52px;
            height: 52px;
            border-radius: 50%;
            object-fit: cover;
            box-shadow: 0 0 0 1px rgba(62, 204, 255, 0.45),
                        0 0 22px rgba(34, 175, 255, 0.30);
        }
        .jarvis-avatar.compact {width: 42px; height: 42px;}
        .jarvis-name {
            font-size: 1.08rem;
            font-weight: 850;
            letter-spacing: 0.13em;
        }
        .jarvis-role {
            color: #268fc8;
            font-size: 0.63rem;
            font-weight: 800;
            letter-spacing: 0.12em;
            margin-top: 0.12rem;
        }
        .status-line {
            color: color-mix(in srgb, var(--text-color) 66%, transparent);
            font-size: 0.9rem;
            margin-top: -0.5rem;
        }
        .stChatMessage {border-radius: 12px;}
        [data-testid="stChatMessage"]:has(.mentor-response-anchor)
        [data-testid="stElementContainer"]:has(.mentor-response-anchor) {
            position: absolute !important;
            width: 1px !important;
            height: 1px !important;
            min-height: 0 !important;
            margin: 0 !important;
            padding: 0 !important;
            overflow: hidden !important;
        }
        [data-testid="stChatMessage"]:has(.mentor-response-anchor)
        [data-testid="stVerticalBlock"] {
            gap: 0;
        }
        textarea {font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;}
        .st-key-code_editor textarea {
            min-height: 400px;
            resize: vertical;
            tab-size: 4;
            white-space: pre;
            overflow-wrap: normal;
            overflow-x: auto;
            font-size: 0.9rem;
            line-height: 1.65;
            caret-color: var(--primary-color);
            background: var(--secondary-background-color);
            border-color: color-mix(in srgb, var(--text-color) 20%, transparent);
        }
        .st-key-code_editor textarea:focus {
            background: var(--background-color);
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(91, 115, 242, 0.12);
        }
        .code-editor-shortcuts {
            margin: -0.2rem 0 0.55rem;
            color: color-mix(in srgb, var(--text-color) 60%, transparent);
            font-size: 0.76rem;
        }
        .st-key-problem_pane [data-testid="stVerticalBlockBorderWrapper"],
        .st-key-code_pane [data-testid="stVerticalBlockBorderWrapper"],
        .st-key-mentor_pane [data-testid="stVerticalBlockBorderWrapper"] {
            border-color: color-mix(in srgb, var(--text-color) 18%, transparent);
            box-shadow: 0 8px 24px rgba(40, 53, 82, 0.06);
        }
        .st-key-problem_pane,
        .st-key-code_pane {
            padding: 0.65rem 0.9rem 0.9rem !important;
        }
        .st-key-problem_pane > [data-testid="stVerticalBlockBorderWrapper"],
        .st-key-code_pane > [data-testid="stVerticalBlockBorderWrapper"],
        .st-key-mentor_pane > [data-testid="stVerticalBlockBorderWrapper"] {
            overflow: hidden;
        }
        .st-key-problem_header [data-testid="stHorizontalBlock"],
        .st-key-code_header [data-testid="stHorizontalBlock"] {
            min-height: 52px;
            align-items: center;
        }
        .st-key-mentor_header [data-testid="stHorizontalBlock"] {
            min-height: 60px;
            align-items: center;
        }
        .st-key-problem_header [data-testid="stVerticalBlock"],
        .st-key-code_header [data-testid="stVerticalBlock"],
        .st-key-mentor_header [data-testid="stVerticalBlock"] {
            gap: 0;
        }
        .workspace-panel-heading {
            display: flex;
            min-height: 52px;
            flex-direction: column;
            justify-content: center;
        }
        .workspace-panel-heading .workspace-kicker {
            margin: 0 0 0.36rem;
            line-height: 1;
        }
        .workspace-panel-heading h3 {
            margin: 0 !important;
            padding: 0 !important;
            font-size: 1.72rem !important;
            line-height: 1 !important;
        }
        .st-key-problem_header button,
        .st-key-code_header button,
        .st-key-mentor_header button {
            width: 100%;
            min-height: 42px;
            height: 42px;
            border-radius: 10px;
            white-space: nowrap;
        }
        .st-key-hide_problem_pane button,
        .st-key-hide_code_pane button,
        .st-key-hide_mentor_pane button,
        .st-key-float_mentor_pane button {
            width: 42px !important;
            min-width: 42px !important;
            padding: 0 !important;
            margin-left: auto;
            border-radius: 50% !important;
            position: relative !important;
        }
        .st-key-hide_problem_pane button p,
        .st-key-hide_code_pane button p,
        .st-key-hide_mentor_pane button p,
        .st-key-float_mentor_pane button p {
            position: absolute !important;
            width: 1px !important;
            height: 1px !important;
            padding: 0 !important;
            margin: -1px !important;
            overflow: hidden !important;
            clip: rect(0, 0, 0, 0) !important;
            white-space: nowrap !important;
            border: 0 !important;
        }
        .st-key-hide_problem_pane button [data-testid="stIconMaterial"],
        .st-key-hide_code_pane button [data-testid="stIconMaterial"],
        .st-key-hide_mentor_pane button [data-testid="stIconMaterial"],
        .st-key-float_mentor_pane button [data-testid="stIconMaterial"] {
            position: absolute !important;
            left: 50% !important;
            top: 50% !important;
            width: 20px !important;
            height: 20px !important;
            margin: 0 !important;
            display: grid !important;
            place-items: center !important;
            line-height: 20px !important;
            transform: translate(-50%, -50%) !important;
        }
        .st-key-code_language_switch [data-testid="stHorizontalBlock"] {
            min-height: 0;
            gap: 0.35rem;
        }
        .st-key-code_language_switch button {
            min-height: 38px;
            height: 38px;
            padding-left: 0.45rem;
            padding-right: 0.45rem;
        }
        .st-key-mentor_header .jarvis-identity {
            min-height: 60px;
            margin: 0;
        }
        .st-key-mentor_history [data-testid="stVerticalBlockBorderWrapper"] {
            background: linear-gradient(
                180deg,
                var(--background-color) 0%,
                var(--secondary-background-color) 100%
            );
        }
        @media (min-width: 901px) {
            .st-key-mentor_pane {
                position: sticky;
                top: 0.75rem;
                align-self: flex-start;
            }
        }
        .workspace-kicker {
            color: color-mix(in srgb, var(--text-color) 66%, transparent);
            font-size: 0.78rem;
            font-weight: 700;
            letter-spacing: 0.08em;
            margin-bottom: -0.35rem;
            text-transform: uppercase;
        }
        .workspace-title {margin: 0 0 0.25rem 0;}

        .leettutor-split-handle {
            position: absolute;
            top: 0;
            bottom: 0;
            width: 18px;
            transform: translateX(-50%);
            cursor: col-resize;
            touch-action: none;
            z-index: 50;
            outline: none;
        }
        .leettutor-split-handle > span {
            position: absolute;
            left: 50%;
            top: 0.65rem;
            bottom: 0.65rem;
            width: 3px;
            transform: translateX(-50%);
            border-radius: 999px;
            background: color-mix(in srgb, var(--text-color) 20%, transparent);
            transition: width 140ms ease, background 140ms ease, box-shadow 140ms ease;
        }
        .leettutor-split-handle:hover > span,
        .leettutor-split-handle:focus-visible > span,
        .leettutor-split-handle.is-dragging > span {
            width: 5px;
            background: var(--primary-color);
            box-shadow: 0 0 0 4px rgba(91, 115, 242, 0.12);
        }
        body.leettutor-split-resizing,
        body.leettutor-split-resizing * {
            cursor: col-resize !important;
            user-select: none !important;
        }

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
        .st-key-floating_mentor.mentor-snapping {
            transition: left 240ms cubic-bezier(0.22, 1, 0.36, 1),
                        top 240ms cubic-bezier(0.22, 1, 0.36, 1);
        }
        .st-key-floating_mentor.mentor-snapping button {
            animation: mentor-snap 240ms ease-out;
        }
        .st-key-floating_mentor button {
            position: relative;
            display: flex;
            width: 72px !important;
            min-width: 72px !important;
            height: 72px !important;
            min-height: 72px !important;
            align-items: center;
            justify-content: center;
            padding: 0 !important;
            overflow: visible;
            border: 1px solid rgba(62, 203, 245, 0.42);
            border-radius: 50%;
            background:
                radial-gradient(circle, rgba(32, 180, 226, 0.16), transparent 62%),
                color-mix(in srgb, var(--background-color) 90%, transparent);
            box-shadow:
                0 15px 34px rgba(13, 110, 153, 0.24),
                inset 0 0 20px rgba(47, 195, 239, 0.08);
            animation: jarvis-float 3.8s ease-in-out infinite;
        }
        .st-key-floating_mentor button:hover {
            border-color: #32c3ec;
            transform: translateY(-3px) scale(1.045);
            box-shadow: 0 18px 42px rgba(23, 158, 203, 0.33);
            animation-play-state: paused;
        }
        .st-key-floating_mentor button[aria-expanded="true"] {
            border-color: rgba(56, 199, 239, 0.72) !important;
            box-shadow: 0 10px 34px rgba(21, 148, 198, 0.25) !important;
            animation: none;
        }
        .st-key-floating_mentor button svg,
        .st-key-floating_mentor button [data-testid="stIconMaterial"] {
            display: none !important;
        }
        .st-key-floating_mentor button p {
            position: absolute !important;
            width: 1px !important;
            height: 1px !important;
            padding: 0 !important;
            margin: -1px !important;
            overflow: hidden !important;
            clip: rect(0, 0, 0, 0) !important;
            white-space: nowrap !important;
            border: 0 !important;
        }
        .st-key-floating_mentor:not(.mentor-busy) button::before {
            content: "";
            position: absolute;
            left: 50%;
            top: 50%;
            width: 52px;
            height: 52px;
            border-radius: 50%;
            background: url("__JARVIS_DATA_URL__") center / cover no-repeat;
            box-shadow: 0 0 0 1px rgba(64, 204, 255, 0.48),
                        0 0 20px rgba(38, 179, 255, 0.40);
            transform: translate(-50%, -50%);
        }
        .st-key-floating_mentor:not(.mentor-busy)
        button[aria-expanded="true"]::before {
            content: "×";
            display: grid;
            place-items: center;
            background: color-mix(in srgb, var(--background-color) 90%, #159ac8 10%);
            color: color-mix(in srgb, var(--text-color) 76%, #22b8e4 24%);
            font: 300 2rem/1 ui-sans-serif, system-ui, sans-serif;
            box-shadow: 0 0 0 1px rgba(64, 204, 255, 0.36),
                        0 8px 22px rgba(22, 139, 181, 0.18);
        }
        .st-key-floating_mentor button[aria-expanded="true"]::after {
            animation: none;
            opacity: 0;
        }
        .st-key-floating_mentor button::after {
            content: "";
            position: absolute;
            inset: -7px;
            pointer-events: none;
            border: 1px solid rgba(62, 201, 244, 0.28);
            border-radius: 50%;
            animation: jarvis-ring 2.6s ease-out infinite;
        }
        .st-key-floating_mentor.mentor-busy button {
            border-color: rgba(56, 199, 239, 0.68);
            background: linear-gradient(
                135deg,
                var(--background-color) 0%,
                var(--secondary-background-color) 100%
            );
            animation: mentor-pulse 1.4s ease-in-out infinite;
        }
        .st-key-floating_mentor.mentor-has-update::after {
            content: "";
            position: absolute;
            right: 1px;
            top: 1px;
            width: 13px;
            height: 13px;
            pointer-events: none;
            border: 2px solid var(--background-color);
            border-radius: 50%;
            background: #2ac98a;
            box-shadow: 0 0 0 4px rgba(42, 201, 138, 0.15),
                        0 0 16px rgba(42, 201, 138, 0.52);
            animation: mentor-update-pulse 1.8s ease-in-out infinite;
        }
        .st-key-floating_mentor.mentor-busy button::before {
            content: "";
            position: absolute;
            left: 50%;
            top: 50%;
            width: 30px;
            height: 30px;
            border: 3px solid rgba(56, 199, 239, 0.24);
            border-top-color: #32c3ec;
            border-radius: 999px;
            animation: mentor-spin 0.85s linear infinite;
        }
        @keyframes mentor-spin {
            from {transform: translate(-50%, -50%) rotate(0deg);}
            to {transform: translate(-50%, -50%) rotate(360deg);}
        }
        @keyframes jarvis-float {
            0%, 100% {transform: translateY(0);}
            50% {transform: translateY(-5px);}
        }
        @keyframes jarvis-ring {
            0% {transform: scale(0.88); opacity: 0;}
            28% {opacity: 0.62;}
            100% {transform: scale(1.12); opacity: 0;}
        }
        @keyframes mentor-snap {
            0% {transform: scale(1);}
            55% {transform: scale(1.055);}
            100% {transform: scale(1);}
        }
        @keyframes mentor-pulse {
            0%, 100% {box-shadow: 0 10px 30px rgba(56, 76, 160, 0.18);}
            50% {box-shadow: 0 12px 38px rgba(76, 96, 230, 0.38);}
        }
        @keyframes mentor-update-pulse {
            0%, 100% {transform: scale(0.9); opacity: 0.82;}
            50% {transform: scale(1.12); opacity: 1;}
        }
        [data-testid="stPopoverBody"] {
            width: min(390px, calc(100vw - 2rem));
            max-height: min(760px, calc(100vh - 3rem));
            overflow-y: auto;
            scrollbar-gutter: stable;
            border-radius: 18px;
        }
        [data-testid="stPopoverBody"]:has(.st-key-floating_composer) {
            --mentor-origin-x: 100%;
            --mentor-origin-y: 100%;
            width: min(420px, calc(100vw - 1rem));
            min-width: min(340px, calc(100vw - 1rem));
            min-height: min(380px, calc(100vh - 1rem));
            max-width: calc(100vw - 1rem);
            max-height: calc(100vh - 1rem);
            overflow: auto;
            resize: none;
            overscroll-behavior: contain;
            transform-origin: var(--mentor-origin-x) var(--mentor-origin-y);
            /* Safe visible fallback. The controller adds the entrance class on
               the next frame, but a delayed/blocked controller must never make
               the tutor window permanently transparent and unclickable. */
            opacity: 1;
            scale: 1;
        }
        [data-testid="stPopoverBody"]:has(.st-key-floating_composer).mentor-popover-ready {
            animation: mentor-popover-enter 280ms cubic-bezier(0.16, 1, 0.3, 1) both;
        }
        [data-testid="stPopoverBody"]:has(.st-key-floating_composer).mentor-resize-locked {
            transform: none !important;
        }
        [data-testid="stPopoverBody"]:has(.st-key-floating_composer)
        > [data-testid="stVerticalBlock"],
        [data-testid="stPopoverBody"]:has(.st-key-floating_composer)
        > div > [data-testid="stVerticalBlock"] {
            min-height: 100%;
        }
        .st-key-floating_transcript {
            flex: 1 1 auto;
            min-height: 96px;
            overflow-y: auto;
            overscroll-behavior: contain;
        }
        .st-key-floating_transcript > [data-testid="stVerticalBlockBorderWrapper"] {
            height: 100% !important;
            overflow-y: auto;
            border-color: color-mix(in srgb, var(--text-color) 10%, transparent);
            background: color-mix(in srgb, var(--secondary-background-color) 48%, transparent);
            scrollbar-gutter: stable;
        }
        .st-key-floating_composer {
            position: sticky;
            bottom: -1rem;
            z-index: 20;
            margin-top: 0.35rem;
            padding: 0.65rem 0 1rem;
            border-top: 1px solid color-mix(in srgb, var(--text-color) 12%, transparent);
            background: color-mix(in srgb, var(--background-color) 94%, transparent);
            box-shadow: 0 -12px 24px color-mix(in srgb, var(--background-color) 85%, transparent);
            backdrop-filter: blur(14px);
        }
        .st-key-floating_composer [data-testid="stTextArea"] textarea {
            min-height: 82px;
            outline: none !important;
        }
        .st-key-floating_composer [data-baseweb="textarea"] {
            border-color: color-mix(in srgb, var(--text-color) 20%, transparent) !important;
            box-shadow: none !important;
        }
        .st-key-floating_composer [data-baseweb="textarea"]:focus-within {
            border-color: #22b8e4 !important;
            box-shadow: 0 0 0 1px #22b8e4 !important;
        }
        .st-key-floating_composer [data-testid="stTextAreaRootElement"] {
            border: 1px solid color-mix(in srgb, var(--text-color) 20%, transparent) !important;
            box-shadow: none !important;
        }
        .st-key-floating_composer [data-testid="stTextAreaRootElement"]:focus-within {
            border-color: #22b8e4 !important;
            box-shadow: 0 0 0 2px rgba(34, 184, 228, 0.18) !important;
        }
        .st-key-floating_composer textarea[aria-invalid="true"] {
            caret-color: #22b8e4 !important;
        }
        .st-key-floating_composer [data-testid="stHorizontalBlock"] {
            gap: 0.45rem;
        }
        .st-key-floating_composer [data-testid="stFormSubmitButton"]
        button[kind="primaryFormSubmit"] {
            border-color: rgba(45, 190, 231, 0.7) !important;
            background: linear-gradient(135deg, #168fbe, #25b9dd) !important;
            color: #fff !important;
            box-shadow: 0 8px 20px rgba(15, 144, 190, 0.2) !important;
        }
        .mentor-resize-overlay {
            position: fixed;
            z-index: 2147483000;
            pointer-events: none;
        }
        .mentor-resize-handle {
            position: absolute;
            z-index: 1000;
            width: 36px;
            height: 36px;
            touch-action: none;
            pointer-events: auto;
            opacity: 0.62;
            transition: opacity 140ms ease;
        }
        .mentor-resize-handle::before {
            content: "";
            position: absolute;
            width: 8px;
            height: 8px;
            border-color: #24b9e4;
            border-style: solid;
        }
        .mentor-resize-handle:hover {opacity: 1;}
        .mentor-resize-top-left {top: 0; left: 0; cursor: nwse-resize;}
        .mentor-resize-top-right {top: 0; right: 0; cursor: nesw-resize;}
        .mentor-resize-bottom-left {bottom: 0; left: 0; cursor: nesw-resize;}
        .mentor-resize-bottom-right {right: 0; bottom: 0; cursor: nwse-resize;}
        .mentor-resize-top-left::before {
            top: 4px; left: 4px; border-width: 2px 0 0 2px;
        }
        .mentor-resize-top-right::before {
            top: 4px; right: 4px; border-width: 2px 2px 0 0;
        }
        .mentor-resize-bottom-left::before {
            bottom: 4px; left: 4px; border-width: 0 0 2px 2px;
        }
        .mentor-resize-bottom-right::before {
            right: 4px; bottom: 4px; border-width: 0 2px 2px 0;
        }
        body.mentor-window-resizing,
        body.mentor-window-resizing * {
            user-select: none !important;
        }
        body.mentor-window-resizing [data-testid="stPopoverBody"]:has(.st-key-floating_composer) {
            will-change: left, top, width, height;
            transition: none !important;
        }
        @keyframes mentor-popover-enter {
            from {opacity: 0; scale: 0.14; filter: blur(3px);}
            62% {opacity: 1;}
            to {opacity: 1; scale: 1; filter: blur(0);}
        }
        @media (prefers-reduced-motion: reduce) {
            [data-testid="stPopoverBody"]:has(.st-key-floating_composer) {
                animation: none;
                opacity: 1;
                scale: 1;
            }
        }
        @media (max-width: 700px) {
            .block-container {padding-left: 0.5rem; padding-right: 0.5rem;}
            .st-key-app_header {padding-left: 0.35rem; padding-right: 3.25rem;}
            .block-container [data-testid="stLayoutWrapper"]:has(h2) {
                padding-left: 2.25rem;
                padding-right: 2.25rem;
            }
            .st-key-floating_mentor {
                right: 0.75rem;
                bottom: 0.75rem;
            }
            .st-key-floating_mentor button {
                min-height: 54px;
                padding: 0.55rem 0.8rem;
            }
        }
        @media (max-height: 760px) {
            .st-key-floating_transcript > [data-testid="stVerticalBlockBorderWrapper"] {
                height: 210px !important;
            }
        }
        </style>
        """
    st.markdown(
        styles.replace("__JARVIS_DATA_URL__", _jarvis_data_url()),
        unsafe_allow_html=True,
    )
    install_mentor_client_controller()
    install_workspace_split_controller()


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
        st.session_state.ui_language = "zh"
        st.session_state.problem_language = "zh"
        st.session_state.problem_translation_requested = False
        st.session_state.provider = config.provider
        st.session_state.endpoint_ollama = config.endpoints["Ollama"]
        st.session_state.endpoint_lm_studio = config.endpoints["LM Studio"]
        st.session_state.endpoint_amd_metal = config.endpoints[AMD_METAL_PROVIDER]
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
        st.session_state.available_models = {
            provider: [] for provider in config.endpoints
        }
        st.session_state.algorithm_messages = []
        st.session_state.system_design_messages = []
        st.session_state.system_design_selected_id = ""
        st.session_state.system_design_track = "auto"
        st.session_state.system_design_difficulty = "progressive"
        st.session_state.system_design_attempted = []
        st.session_state.system_requirement = ""
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
        st.session_state.code_editor_seed = st.session_state.code_editor
        st.session_state.code_editor_revision = 0
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
    config.endpoints.setdefault(
        AMD_METAL_PROVIDER, defaults.endpoints[AMD_METAL_PROVIDER]
    )
    st.session_state.setdefault(
        "endpoint_amd_metal", config.endpoints[AMD_METAL_PROVIDER]
    )
    st.session_state.setdefault("available_models", {})
    st.session_state.available_models.setdefault(AMD_METAL_PROVIDER, [])
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
    st.session_state.setdefault("system_design_selected_id", "")
    st.session_state.setdefault("system_design_track", "auto")
    st.session_state.setdefault("system_design_difficulty", "progressive")
    st.session_state.setdefault("system_design_attempted", [])
    st.session_state.setdefault("system_requirement", "")
    st.session_state.setdefault("ui_language", "zh")
    st.session_state.setdefault("problem_language", "zh")
    st.session_state.setdefault("problem_translation_requested", False)
    st.session_state.setdefault(
        "code_editor_seed", st.session_state.get("code_editor", "")
    )
    st.session_state.setdefault("code_editor_revision", 0)
    st.session_state.setdefault("show_problem_pane", True)
    st.session_state.setdefault("show_code_pane", True)
    st.session_state.setdefault("show_mentor_pane", True)
    st.session_state.setdefault("mentor_layout_mode", "悬浮")
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


def _retain_metal_runtime(handle: Any) -> None:
    """Keep an in-app started server alive and close it with Streamlit."""

    previous = st.session_state.get("metal_runtime_handle")
    if previous is not None and previous is not handle and previous.managed:
        previous.stop()
    st.session_state.metal_runtime_handle = handle
    if handle.managed:
        atexit.register(handle.stop)


def render_amd_metal_setup(
    *, endpoint: str, config: AppConfig, profile: HardwareProfile
) -> None:
    """Offer a complete, recoverable setup path for Intel Mac Radeon users."""

    status = inspect_metal_setup(
        project_root=PROJECT_ROOT,
        endpoint=endpoint,
        gpu_name=profile.gpu,
        vram_gb=profile.vram_gb,
    )
    st.selectbox(
        _ui("适合这台设备的模型", "Recommended model for this device"),
        [
            _ui(
                "Qwen 3.5 9B（Radeon 5600M 实测档）",
                "Qwen 3.5 9B (tested on Radeon 5600M)",
            )
        ],
        key="recommended_model_amd_metal",
        disabled=True,
    )
    if status.endpoint_running:
        st.success(
            _ui(
                f"Radeon 私有显存推理正在运行：`{AMD_METAL_MODEL}` · 本机实测约 20 token/s。",
                f"Radeon private-VRAM inference is running: `{AMD_METAL_MODEL}` · about 20 token/s measured here.",
            )
        )
    elif status.verified_5600m:
        st.info(
            _ui(
                "已识别 Radeon Pro 5600M 8 GB：这是项目持续实测的配置。按下面的状态卡补齐缺项即可。",
                "Radeon Pro 5600M 8 GB detected: this is the project's continuously tested configuration. Complete any missing step below.",
            )
        )
    elif status.hardware_compatible:
        st.warning(
            _ui(
                "这台 Intel Mac 有 8 GB 独立 Radeon，硬件条件满足，但尚未在本项目持续实测；安装流程仍可试用。",
                "This Intel Mac has an 8 GB discrete Radeon. It meets the hardware budget but is not continuously tested by this project.",
            )
        )
    else:
        st.error(
            _ui(
                "自动安装要求 Intel macOS 和至少 8 GB 独立 Radeon。Apple Silicon 应使用官方 Ollama Metal；4 GB Radeon 请改用 CPU 小模型。",
                "Automatic setup requires Intel macOS and a discrete Radeon with at least 8 GB. Apple Silicon should use official Ollama Metal; use a small CPU model on 4 GB Radeons.",
            )
        )

    checks = [
        (
            status.hardware_compatible,
            _ui("Intel Mac + 8 GB 独立 Radeon", "Intel Mac + 8 GB discrete Radeon"),
        ),
        (status.xcode_tools, _ui("Apple 编译工具", "Apple build tools")),
        (bool(status.cmake_path), "CMake"),
        (bool(status.server_path), _ui("定制 llama-server", "patched llama-server")),
        (bool(status.model_path), AMD_METAL_MODEL),
        (status.endpoint_running, _ui("本地 GPU 服务", "local GPU endpoint")),
    ]
    st.markdown(
        "\n".join(
            f"{'✅' if ready else '○'} **{index}. {label}**"
            for index, (ready, label) in enumerate(checks, start=1)
        )
    )

    if not status.xcode_tools and status.intel_macos:
        if st.button(
            _ui("打开 Apple 编译工具安装器", "Open Apple build-tools installer"),
            key="install_xcode_tools_for_metal",
            use_container_width=True,
        ):
            try:
                open_xcode_tools_installer()
            except MetalRuntimeError as exc:
                st.error(str(exc))
            else:
                st.success(
                    _ui(
                        "系统安装器已打开。完成后点击下面的“重新自检”。",
                        "The system installer is open. When it finishes, run the check again below.",
                    )
                )

    ollama_endpoint = config.endpoints.get("Ollama", "http://localhost:11434")
    if status.hardware_compatible and status.model_path is None:
        st.divider()
        st.caption(
            _ui(
                "模型只下载一次。AMD 服务直接读取 Ollama 的本地 GGUF，不会再复制一份。",
                "The model is downloaded once. The AMD service reads Ollama's local GGUF without making another copy.",
            )
        )
        ollama_ready = render_ollama_setup(ollama_endpoint)
        if st.button(
            _ui(f"下载 {AMD_METAL_MODEL}", f"Download {AMD_METAL_MODEL}"),
            key="download_amd_metal_model",
            type="primary",
            use_container_width=True,
            disabled=not ollama_ready,
        ):
            progress_bar = st.progress(
                0.0, text=_ui("正在连接 Ollama…", "Connecting to Ollama…")
            )
            try:
                for update in pull_ollama_model(ollama_endpoint, AMD_METAL_MODEL):
                    detail = update.status
                    if update.total:
                        detail += (
                            f" · {update.completed / (1024**3):.1f}/"
                            f"{update.total / (1024**3):.1f} GB"
                        )
                    progress_bar.progress(update.fraction, text=detail)
            except ModelDownloadError as exc:
                progress_bar.empty()
                st.error(str(exc))
            else:
                progress_bar.progress(
                    1.0, text=_ui("模型下载完成", "Model download complete")
                )
                st.success(_ui("模型已就绪。", "The model is ready."))
                status = inspect_metal_setup(
                    project_root=PROJECT_ROOT,
                    endpoint=endpoint,
                    gpu_name=profile.gpu,
                    vram_gb=profile.vram_gb,
                )

    if status.hardware_compatible and not status.endpoint_running:
        st.divider()
        install_label = (
            _ui(
                "修复 / 重新编译 AMD Metal 后端",
                "Repair / rebuild AMD Metal backend",
            )
            if status.server_path
            else _ui("安装 AMD Metal 实验后端", "Install experimental AMD Metal backend")
        )
        if st.button(
            install_label,
            key="install_amd_metal_backend",
            type="primary" if not status.server_path else "secondary",
            use_container_width=True,
            disabled=not status.build_ready,
        ):
            progress_bar = st.progress(
                0.0, text=_ui("准备安装…", "Preparing setup…")
            )
            detail_slot = st.empty()
            try:
                for update in install_metal_runtime(
                    project_root=PROJECT_ROOT,
                    gpu_name=profile.gpu,
                    vram_gb=profile.vram_gb,
                ):
                    progress_bar.progress(update.progress, text=update.phase)
                    if update.detail:
                        detail_slot.code(update.detail, language="text")
            except MetalRuntimeError as exc:
                st.error(str(exc))
            else:
                progress_bar.progress(
                    1.0, text=_ui("后端已编译", "Backend compiled")
                )
                detail_slot.empty()
                status = inspect_metal_setup(
                    project_root=PROJECT_ROOT,
                    endpoint=endpoint,
                    gpu_name=profile.gpu,
                    vram_gb=profile.vram_gb,
                )
                st.success(
                    _ui(
                        "运行时安装完成；以后双击 run.command 会自动启动它。",
                        "Runtime installed; run.command will start it automatically from now on.",
                    )
                )

        can_start = bool(status.server_path and status.model_path)
        if st.button(
            _ui("立即启动并验证 GPU 服务", "Start and verify GPU endpoint now"),
            key="start_amd_metal_backend",
            use_container_width=True,
            disabled=not can_start,
        ):
            with st.status(
                _ui("正在把模型载入 Radeon 显存…", "Loading the model into Radeon VRAM…"),
                expanded=True,
            ) as service_status:
                try:
                    handle = ensure_metal_runtime(
                        project_root=PROJECT_ROOT,
                        endpoint=endpoint,
                        model=AMD_METAL_MODEL,
                    )
                except MetalRuntimeError as exc:
                    service_status.update(label=str(exc), state="error")
                else:
                    _retain_metal_runtime(handle)
                    service_status.update(
                        label=_ui("GPU 服务验证通过", "GPU endpoint verified"),
                        state="complete",
                        expanded=False,
                    )
                    st.success(
                        _ui("现在可以直接使用 JARVIS。", "JARVIS is ready to use.")
                    )

    refresh_col, guide_col = st.columns(2)
    if refresh_col.button(
        _ui("重新自检", "Run checks again"),
        key="refresh_amd_metal_setup",
        use_container_width=True,
    ):
        st.session_state.hardware_profile = detect_hardware()
        st.rerun()
    guide_col.link_button(
        _ui("GitHub 完整指南", "Full GitHub guide"),
        "https://github.com/widmonstertony/Leetcode/blob/master/docs/INTEL_AMD_MACBOOK.md",
        use_container_width=True,
    )

    with st.expander(_ui("兼容范围与工作原理", "Compatibility and how it works")):
        st.markdown(
            _ui(
                """
- **持续验证：** 16-inch Intel MacBook Pro（MacBookPro16,4）、Radeon Pro 5600M 8 GB。
- **可实验：** 其他 Intel Mac + 8 GB 独立 Radeon；速度和稳定性不保证相同。
- **不适用：** Apple Silicon、Windows/Boot Camp、4 GB Radeon。
- 安装器固定下载 `llama.cpp b10240`，应用仓库内的 Qwen 3.5 / Ollama GGUF 兼容补丁，再用 Apple Metal 编译；不会修改系统驱动或 Ollama 文件。
- 模型使用 `MTL0`、私有 Metal buffer、4096 context 和单并发，服务只监听 `127.0.0.1`。
""",
                """
- **Continuously tested:** 16-inch Intel MacBook Pro (MacBookPro16,4) with Radeon Pro 5600M 8 GB.
- **Experimental:** other Intel Macs with an 8 GB discrete Radeon; speed and stability may differ.
- **Not applicable:** Apple Silicon, Windows/Boot Camp, or 4 GB Radeons.
- Setup pins `llama.cpp b10240`, applies the repository's Qwen 3.5 / Ollama-GGUF compatibility patch, then builds with Apple Metal. It does not modify drivers or Ollama files.
- The endpoint uses `MTL0`, private Metal buffers, a 4096-token context, one concurrent request, and listens only on `127.0.0.1`.
""",
            )
        )
        st.code(
            "python3 scripts/setup_intel_amd_metal.py\n"
            "python3 scripts/setup_intel_amd_metal.py --install\n"
            "python3 scripts/setup_intel_amd_metal.py --start",
            language="bash",
        )

    with st.expander(_ui("复制诊断信息", "Copy diagnostics")):
        st.code("\n".join(status.report_lines()), language="text")
        if status.log_path.is_file():
            st.caption(_ui("最近启动日志", "Recent startup log"))
            try:
                log_tail = status.log_path.read_text(
                    encoding="utf-8", errors="replace"
                )[-3000:]
            except OSError as exc:
                st.warning(str(exc))
            else:
                st.code(log_tail, language="text")


def render_model_center(
    *, provider: str, endpoint: str, config: AppConfig
) -> None:
    """Show hardware-aware recommendations and provider-specific installation."""

    if "hardware_profile" not in st.session_state:
        st.session_state.hardware_profile = detect_hardware()
    profile: HardwareProfile = st.session_state.hardware_profile
    recommendations = recommend_models(profile)

    with st.expander(
        _ui("硬件检测与模型安装", "Hardware detection and model setup"),
        expanded=not bool(config.model),
    ):
        st.markdown("**" + _ui("检测到的设备", "Detected hardware") + "**")
        st.caption(profile.summary)
        st.caption(
            _ui(
                "推荐基于模型体积和保守内存预算估算；上下文越长，额外内存越多。",
                "Recommendations use model size and a conservative memory budget; longer contexts need more memory.",
            )
        )
        if provider == AMD_METAL_PROVIDER:
            render_amd_metal_setup(endpoint=endpoint, config=config, profile=profile)
            return
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
            _ui("适合这台设备的模型", "Recommended model for this device"),
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
                _ui("查看模型页面", "View model page"),
                f"https://ollama.com/library/{recommendation.ollama_id.split(':')[0]}",
                use_container_width=True,
            )
            if download_col.button(
                _ui("一键下载", "Download"),
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
        st.header(_ui("本地模型", "Local model"))
        provider = st.selectbox(
            "API Provider", list(config.endpoints), key="provider"
        )
        endpoint_key = {
            "Ollama": "endpoint_ollama",
            "LM Studio": "endpoint_lm_studio",
            AMD_METAL_PROVIDER: "endpoint_amd_metal",
        }[provider]
        endpoint = st.text_input("API Endpoint", key=endpoint_key)

        settings = provider_settings(provider, endpoint, config)
        render_model_center(
            provider=provider, endpoint=endpoint, config=config
        )
        if st.button(_ui("检测服务并刷新模型", "Check service and refresh models"), use_container_width=True):
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
        manual_option = _ui("手动输入…", "Enter manually…")
        options = [manual_option, *models]
        choice_key = f"model_choice_{provider.lower().replace(' ', '_')}"
        if st.session_state.get(choice_key) not in options:
            st.session_state[choice_key] = manual_option
        selected_model = st.selectbox(
            _ui("已检测到的模型", "Detected models"), options, key=choice_key
        )
        if selected_model == manual_option:
            model = st.text_input(
                "Model Name",
                key="model_manual",
                placeholder=_ui("例如 qwen3.5:9b", "For example qwen3.5:9b"),
            ).strip()
        else:
            model = selected_model
            st.caption(_ui("当前模型：", "Current model: ") + f"`{model}`")

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

        generation_profile = (
            replace(profile, ollama_gpu_supported=True)
            if provider == AMD_METAL_PROVIDER
            else profile
        )
        generation = recommend_generation_defaults(generation_profile, model)
        auto_tune = st.toggle(
            _ui("根据硬件和模型自动调优", "Auto-tune for hardware and model"),
            key="auto_tune",
            help=_ui(
                "按显存、内存和模型体积自动设置超时、上下文、思考强度与输出额度。",
                "Set timeout, context, reasoning, and output limits from VRAM, RAM, and model size.",
            ),
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
                "none"
                if provider == AMD_METAL_PROVIDER
                else generation.system_design_reasoning
            )
            st.session_state.max_tokens_algorithm = (
                generation.algorithm_max_tokens
            )
            st.session_state.max_tokens_system_design = (
                generation.system_design_max_tokens
            )
            if provider == AMD_METAL_PROVIDER:
                offload_label = _ui(
                    "Radeon 私有显存完整加载", "Fully loaded in Radeon private VRAM"
                )
            else:
                offload_label = (
                    _ui("CPU + GPU 混合卸载", "CPU + GPU partial offload")
                    if generation.partially_offloaded
                    else _ui("优先完整 GPU 加载", "Prefer full GPU loading")
                )
            st.caption(
                f"{_ui('自动档', 'Auto')}: {offload_label} · Timeout "
                f"{generation.timeout_seconds:g}s · {_ui('上下文', 'context')} "
                f"{generation.context_tokens} · {_ui('模型驻留', 'keep-alive')} "
                f"{generation.keep_alive}"
            )

        st.divider()
        st.subheader(_ui("生成参数", "Generation settings"))
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
            _ui("Timeout（秒）", "Timeout (seconds)"),
            min_value=5.0,
            max_value=600.0,
            step=5.0,
            key="timeout_seconds",
            disabled=auto_tune,
        )
        st.number_input(
            _ui("上下文 Tokens", "Context tokens"),
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
            _ui("深度思考", "Reasoning effort"),
            list(REASONING_LABELS),
            format_func=lambda value: (
                REASONING_LABELS[value]
                if st.session_state.get("ui_language", "zh") == "zh"
                else {
                    "none": "Off (recommended, fastest)",
                    "low": "Low",
                    "medium": "Medium",
                    "high": "High (may take much longer)",
                }[value]
            ),
            key=reasoning_key,
            help="DeepSeek/Qwen 的 thinking 可能先运行数分钟；算法引导默认关闭。",
            disabled=auto_tune,
        )
        if provider == AMD_METAL_PROVIDER:
            st.caption(
                _ui(
                    "AMD Metal 实验后端固定关闭深度思考，优先保证直接返回答案。",
                    "The experimental AMD Metal backend keeps long reasoning off so it can return a final answer promptly.",
                )
            )
        max_tokens_key = (
            "max_tokens_algorithm"
            if mode == "algorithm"
            else "max_tokens_system_design"
        )
        max_tokens = int(
            st.number_input(
                _ui("最大输出 Tokens", "Maximum output tokens"),
                min_value=64,
                max_value=4096,
                step=64,
                key=max_tokens_key,
                disabled=auto_tune,
            )
        )
        if reasoning_effort != "none" and max_tokens < 1024:
            st.warning("已开启深度思考；建议把最大输出 Tokens 提高到至少 1024。")

        with st.expander(_ui("角色 Prompt（可编辑）", "Role prompts (editable)")):
            st.text_area(_ui("算法面试官", "Algorithm interviewer"), height=260, key="prompt_algorithm")
            st.text_area(_ui("系统架构师", "System architect"), height=260, key="prompt_system_design")

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
        if left.button(_ui("保存设置", "Save settings"), use_container_width=True):
            try:
                save_config(config)
            except ConfigError as exc:
                st.error(str(exc))
            else:
                st.success("已保存到本地 config.json。")
        if right.button(_ui("清空对话", "Clear chat"), use_container_width=True):
            st.session_state[f"{mode}_messages"] = []
            st.rerun()

        if st.button(_ui("在 VS Code 中打开仓库", "Open repository in VS Code"), use_container_width=True):
            open_in_vscode()

        st.caption(
            _ui(
                "只有点击运行时才会在受限子进程执行编辑器代码；AI 对话只发送到你配置的 API Endpoint。",
                "Editor code runs in a restricted subprocess only when you click Run; AI chat is sent only to your configured API endpoint.",
            )
        )

    return (
        settings,
        model,
        float(temperature),
        float(top_p),
        reasoning_effort,
        max_tokens,
    )


def _render_mermaid_chart(diagram: str) -> None:
    """Render a vector diagram, with a readable fallback on older Streamlit."""

    if hasattr(st, "mermaid_chart"):
        st.mermaid_chart(diagram, width="stretch")
    else:
        st.code(diagram, language="mermaid")


def render_visual_learning_map(
    *,
    mode: Literal["algorithm", "system_design"],
    diagram: str,
    focus: str,
) -> bool:
    """Show an instant visual scaffold and optionally ask JARVIS to redraw it."""

    with st.expander(
        _ui("视觉地图 · 先看结构再写答案", "Visual map · See the structure first"),
        expanded=False,
    ):
        st.markdown(
            '<div class="visual-map-intro"><span class="visual-map-orbit">◈</span>'
            '<div><strong>'
            + _ui("当前心智模型", "Current mental model")
            + "</strong><br><span>"
            + html.escape(focus)
            + "</span></div></div>",
            unsafe_allow_html=True,
        )
        _render_mermaid_chart(diagram)
        st.caption(
            _ui(
                "这是可缩放的矢量图，不是完整答案。JARVIS 可以根据你的代码或方案把它重画成当前状态。",
                "This is a scalable vector diagram, not a full answer. JARVIS can redraw it from your current code or design.",
            )
        )
        return st.button(
            _ui("让 JARVIS 按当前进度重画", "Ask JARVIS to redraw my current state"),
            key=f"{mode}_redraw_visual",
            icon=":material/auto_awesome:",
            use_container_width=True,
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
        else:
            _render_mermaid_chart(segment.content)


def render_history(history: list[HistoryItem], *, render_mermaid: bool) -> None:
    for item in history:
        with st.chat_message(item["role"]):
            visible = item.get("display", item["content"])
            if item["role"] == "assistant":
                render_assistant_content(visible, render_mermaid=render_mermaid)
            else:
                st.markdown(visible)


def _build_api_messages(
    history: list[HistoryItem], system_prompt: str, *, limit: int = 12
) -> list[dict[str, str]]:
    """Keep only the newest workspace snapshot while preserving real dialogue."""

    recent_history = history[-limit:]
    messages = [{"role": "system", "content": system_prompt}]
    for index, item in enumerate(recent_history):
        content = item["content"]
        if item["role"] == "user" and index < len(recent_history) - 1:
            content = item.get("display", content)
        messages.append({"role": item["role"], "content": content})
    return messages


def _tutor_output_limit(*, mode: str, display: str, configured: int) -> int:
    """Keep coaching turns short while leaving explicit deep asks untouched."""

    if mode == "algorithm" and "求最优解代码" not in display:
        return min(configured, 256)
    if mode == "system_design" and any(
        marker in display
        for marker in ("下一步", "Next step", "视觉", "Visual")
    ):
        return min(configured, 512)
    return configured


def _format_coaching_turn(content: str) -> str:
    """Render the one-hint/one-question contract as two scannable dialogue beats."""

    formatted = re.sub(
        r"^\s*(?:\*\*)?提示[：:]\s*(?:\*\*)?",
        "**提示：** ",
        content.strip(),
        count=1,
    )
    return re.sub(
        r"\s*(?:\*\*)?轮到你[：:]\s*(?:\*\*)?",
        "\n\n**轮到你：** ",
        formatted,
        count=1,
    )


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
    surface: Literal["main", "floating", "workspace"] = "main",
    mirror: dict[str, Any] | None = None,
) -> bool:
    history: list[HistoryItem] = st.session_state[f"{mode}_messages"]
    history.append({"role": "user", "content": content, "display": display})
    anchor_id = f"mentor-response-{surface}-{mode}-{len(history)}"

    with st.chat_message("user"):
        st.markdown(display)
    with st.chat_message("assistant"):
        mirror_activity = mirror.get("activity") if mirror else None
        mirror_output = mirror.get("output") if mirror else None
        mirror_phase = mirror.get("phase") if mirror else None
        state_mount = mirror.get("state_mount") if mirror else None

        def update_mirror_phase(zh: str, en: str) -> None:
            if mirror_phase is not None:
                mirror_phase.markdown(
                    '<span class="system-live-status">'
                    + _ui(zh, en)
                    + "</span>",
                    unsafe_allow_html=True,
                )

        st.markdown(
            f'<span id="{html.escape(anchor_id, quote=True)}" '
            'class="mentor-response-anchor"></span>',
            unsafe_allow_html=True,
        )
        set_mentor_client_state(
            "loading",
            anchor_id=anchor_id,
            surface=surface,
            state_mount=state_mount,
        )
        activity = st.status(
            f"正在连接 {settings.provider} 并加载 {model or '模型'}…首次加载可能需要几十秒。",
            expanded=False,
        )
        placeholder = st.empty()
        if mirror_activity is not None:
            mirror_activity.update(
                label=f"正在连接 {settings.provider} 并加载 {model or '模型'}…",
                state="running",
            )
        update_mirror_phase("连接中", "Connecting")
        complete = ""
        thinking_chars = 0
        answer_started = False
        thinking_started = False
        last_activity_update = time.monotonic()
        try:
            with st.spinner("JARVIS 正在加载模型并思考…", show_time=True):
                client = LocalLLMClient(settings)
                api_messages = _build_api_messages(
                    history, config.prompts[mode]
                )
                output_limit = _tutor_output_limit(
                    mode=mode,
                    display=display,
                    configured=max_tokens,
                )
                for delta in client.stream_chat(
                    messages=api_messages,
                    model=model,
                    temperature=temperature,
                    top_p=top_p,
                    reasoning_effort=reasoning_effort,
                    max_tokens=output_limit,
                ):
                    if delta.kind == "thinking":
                        thinking_chars += len(delta.content)
                        if not thinking_started:
                            set_mentor_client_state(
                                "thinking",
                                anchor_id=anchor_id,
                                surface=surface,
                                state_mount=state_mount,
                            )
                            activity.update(label="模型正在思考…", state="running")
                            if mirror_activity is not None:
                                mirror_activity.update(
                                    label="JARVIS 正在思考…", state="running"
                                )
                            update_mirror_phase("思考中", "Thinking")
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
                            set_mentor_client_state(
                                "answering",
                                anchor_id=anchor_id,
                                surface=surface,
                                state_mount=state_mount,
                            )
                            activity.update(label="模型正在回答…", state="running")
                            if mirror_activity is not None:
                                mirror_activity.update(
                                    label="JARVIS 正在回答…", state="running"
                                )
                            update_mirror_phase("生成中", "Answering")
                            answer_started = True
                        placeholder.markdown(complete + "▌")
                        if mirror_output is not None:
                            mirror_output.markdown(complete + "▌")
        except LocalLLMError as exc:
            placeholder.empty()
            activity.update(label="模型调用失败", state="error")
            if mirror_activity is not None:
                mirror_activity.update(label="模型调用失败", state="error")
            if mirror_output is not None:
                mirror_output.error(str(exc))
            update_mirror_phase("调用失败", "Failed")
            set_mentor_client_state(
                "error",
                anchor_id=anchor_id,
                surface=surface,
                state_mount=state_mount,
            )
            st.error(str(exc))
            st.caption("你的问题已经保留，可以修正侧边栏配置后再次发送。")
            return False

        placeholder.empty()
        if not complete.strip():
            activity.update(label="没有收到最终答案", state="error")
            if mirror_activity is not None:
                mirror_activity.update(label="没有收到最终答案", state="error")
            update_mirror_phase("没有回答", "No answer")
            set_mentor_client_state(
                "error",
                anchor_id=anchor_id,
                surface=surface,
                state_mount=state_mount,
            )
            if thinking_chars:
                st.warning(
                    "模型把本次输出额度都用在了思考阶段。请把“深度思考”设为关闭，"
                    "或提高最大输出 Tokens。"
                )
            else:
                st.warning("模型连接已结束，但没有返回文本。请检查模型日志或换一个模型。")
            return False
        activity.update(label="回答完成", state="complete")
        if mirror_activity is not None:
            mirror_activity.update(label="JARVIS 已更新当前回合", state="complete")
        update_mirror_phase("已完成", "Complete")
        is_short_coaching_turn = (
            mode == "algorithm" and "求最优解代码" not in display
        )
        visible_complete = (
            _format_coaching_turn(complete)
            if is_short_coaching_turn
            else complete
        )
        # Algorithm visual explanations use the same safe Mermaid renderer as
        # system-design diagrams; normal replies remain plain Markdown.
        render_assistant_content(visible_complete, render_mermaid=True)
        if mirror_output is not None:
            mirror_output.empty()
            with mirror_output.container():
                render_assistant_content(visible_complete, render_mermaid=True)
        history.append(
            {
                "role": "assistant",
                "content": complete,
                "display": visible_complete,
            }
        )
        set_mentor_client_state(
            "done",
            anchor_id=anchor_id,
            surface=surface,
            state_mount=state_mount,
        )
        return True


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
    pending: tuple[str, str] | None = None,
    mirror: dict[str, Any] | None = None,
) -> None:
    """Render a fixed tutor character backed by the existing conversation."""

    history: list[HistoryItem] = st.session_state[f"{mode}_messages"]
    queued_action_key = f"{mode}_floating_mentor_action"
    transcript_is_active = bool(
        history or pending or st.session_state.get(queued_action_key)
    )
    transcript_height = 270 if transcript_is_active else 104
    with st.container(key="floating_mentor"):
        with st.popover(
            "JARVIS",
            help=_ui(
                "随时询问当前训练；JARVIS 能看到编辑器和最近运行结果。",
                "Ask about the current mission anytime; JARVIS can see your editor and latest run.",
            ),
        ):
            st.markdown(_jarvis_identity_html(), unsafe_allow_html=True)
            if mode == "algorithm":
                dock_col, hide_col = st.columns(2)
                dock_col.button(
                    _ui("停靠到右栏", "Dock to right pane"),
                    key="floating_dock_mentor",
                    use_container_width=True,
                    on_click=_set_workspace_option,
                    args=("mentor_layout_mode", "停靠"),
                )
                hide_col.button(
                    _ui("隐藏导师", "Hide tutor"),
                    key="floating_hide_mentor",
                    use_container_width=True,
                    on_click=_set_workspace_option,
                    args=("show_mentor_pane", False),
                )
                problem = st.session_state.get("algorithm_problem", "当前题目")
                run_status = st.session_state.get("code_run_result", {}).get(
                    "summary", "还没有运行"
                )
                st.caption(
                    _ui("正在陪练：", "Coaching: ")
                    + f"{problem or _ui('尚未选题', 'No problem selected')} · {run_status}"
                )
                st.caption(
                    _ui(
                        "我能看到题面、代码和运行结果；每次只推进一个问题。",
                        "I can see the statement, code, and run result; one question at a time.",
                    )
                )
            else:
                requirement = st.session_state.get("system_requirement", "")
                st.caption(
                    _ui("当前任务：", "Current mission: ")
                    + (requirement or _ui("尚未分配", "Not assigned"))
                )
                st.caption(
                    _ui(
                        "每次只追问一个容量、可靠性或架构取舍问题。",
                        "One capacity, reliability, or architecture tradeoff at a time.",
                    )
                )

            transcript_box = st.container(
                height=transcript_height,
                border=True,
                key="floating_transcript",
            )
            with transcript_box:
                if history:
                    render_history(history[-6:], render_mermaid=True)
                else:
                    st.caption(
                        _ui(
                            "对话会留在这里。直接在下方回答或追问。",
                            "The conversation stays here. Reply or ask below.",
                        )
                    )

            with st.container(key="floating_composer"):
                with st.form(
                    key=f"floating_mentor_form_{mode}",
                    clear_on_submit=True,
                    border=False,
                ):
                    question = st.text_area(
                        _ui("继续回复 JARVIS", "Reply to JARVIS"),
                        key="floating_mentor_question",
                        height=82,
                        placeholder=(
                            _ui(
                                "写下你的判断，或继续追问……",
                                "Share your reasoning or ask a follow-up…",
                            )
                            if mode == "algorithm"
                            else _ui(
                                "回答当前追问，或继续讨论取舍……",
                                "Answer the current question or discuss the tradeoff…",
                            )
                        ),
                    )
                    send_clicked = st.form_submit_button(
                        _ui("发送给 JARVIS", "Send to JARVIS"),
                        type="primary",
                        use_container_width=True,
                        on_click=_queue_floating_mentor_action,
                        args=(mode, "send"),
                    )
                    stuck_col, next_col = st.columns(2)
                    stuck_clicked = stuck_col.form_submit_button(
                        _ui("我卡住了", "I'm stuck"),
                        use_container_width=True,
                        on_click=_queue_floating_mentor_action,
                        args=(mode, "stuck"),
                    )
                    next_clicked = next_col.form_submit_button(
                        _ui("只提示下一步", "Hint the next step"),
                        use_container_width=True,
                        on_click=_queue_floating_mentor_action,
                        args=(mode, "next"),
                    )

            queued_action = st.session_state.pop(
                queued_action_key,
                None,
            )
            if queued_action:
                question = str(queued_action.get("question", ""))
                action = str(queued_action.get("action", ""))
                send_clicked = action == "send"
                stuck_clicked = action == "stuck"
                next_clicked = action == "next"

            requested_question = ""
            trigger = "悬浮导师对话"
            if send_clicked:
                requested_question = question.strip() or _ui(
                    "请根据当前现场继续引导我。",
                    "Continue coaching me from the current workspace state.",
                )
            elif stuck_clicked:
                requested_question = (
                    question.strip()
                    or (
                        _ui(
                            "我卡住了。先判断我已经做到哪里，只指出一个最关键的问题。",
                            "I'm stuck. First assess how far I got, then point out only the single most important issue.",
                        )
                        if mode == "algorithm"
                        else _ui(
                            "我卡在当前架构决策。只指出一个最关键的遗漏，并问我一个问题。",
                            "I'm stuck on this architecture decision. Point out one critical omission and ask one question.",
                        )
                    )
                )
                trigger = "悬浮导师：我卡住了"
            elif next_clicked:
                requested_question = (
                    question.strip()
                    or (
                        _ui(
                            "不要给完整解法，只根据现有进度提示下一步并问我一个问题。",
                            "Do not give the full solution. Hint only the next step, then ask me one question.",
                        )
                        if mode == "algorithm"
                        else _ui(
                            "不要展开完整架构；只推进下一个最关键的设计决策。",
                            "Do not expand the full architecture; advance only the next critical decision.",
                        )
                    )
                )
                trigger = "悬浮导师：下一步提示"

            requested = pending
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
                    requested = (
                        content,
                        _ui("向 JARVIS 提问：", "Ask JARVIS: ") + requested_question,
                    )

            if requested:
                if not model:
                    st.warning("先在左侧选择或填写本地模型，然后再和我对话。")
                else:
                    with transcript_box:
                        submit_to_tutor(
                            mode=mode,
                            content=requested[0],
                            display=requested[1],
                            settings=settings,
                            model=model,
                            temperature=temperature,
                            top_p=top_p,
                            reasoning_effort=reasoning_effort,
                            max_tokens=max_tokens,
                            config=config,
                            surface="floating",
                            mirror=mirror,
                        )


def _run_result_text(result: dict[str, object] | None = None) -> str:
    value = result if result is not None else st.session_state.get("code_run_result", {})
    if not value:
        return "尚未运行"
    return json.dumps(value, ensure_ascii=False, indent=2, default=str)


def _workspace_request(question: str, *, trigger: str) -> str:
    imported = st.session_state.get("leetcode_problem", {})
    request = build_workspace_help_request(
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
    if st.session_state.get("ui_language", "zh") == "en":
        request += (
            "\nReply in English. Keep this coaching turn to one short hint and "
            "exactly one question.\n"
        )
    return request


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


def _apply_imported_problem(
    imported_problem: ImportedProblem, *, replace_code: bool = True
) -> None:
    """Load a public LeetCode problem into the shared mentor/IDE workspace."""

    imported = imported_problem.to_dict()
    title = f"{imported_problem.frontend_id}. {imported_problem.title}"
    st.session_state.leetcode_problem = imported
    st.session_state.leetcode_import_error = ""
    st.session_state.algorithm_problem = title
    if replace_code:
        st.session_state.code_language = "Python"
        st.session_state.code_editor = imported_problem.starter_code
        st.session_state.code_editor_seed = imported_problem.starter_code
        st.session_state.code_editor_revision += 1
        st.session_state.solution_method = imported_problem.method_name
        if imported_problem.sample_cases:
            st.session_state.solution_test_cases = imported_problem.sample_cases
        st.session_state.code_run_result = {}
    st.session_state.save_filename_python = (
        f"{imported_problem.frontend_id}.{imported_problem.slug}.py"
    )


def _import_problem_into_workspace(reference: str) -> ImportedProblem:
    imported_problem = fetch_problem(
        reference,
        locale=st.session_state.get("problem_language", "zh"),
        timeout_seconds=12,
    )
    _apply_imported_problem(imported_problem)
    return imported_problem


def _refresh_imported_problem_translation() -> None:
    """Refresh only title/statement metadata, never the user's current code."""

    if not st.session_state.pop("problem_translation_requested", False):
        return
    imported = st.session_state.get("leetcode_problem", {})
    if not imported:
        return
    reference = str(imported.get("slug") or imported.get("url") or "")
    if not reference:
        return
    try:
        translated = fetch_problem(
            reference,
            locale=st.session_state.get("problem_language", "zh"),
            timeout_seconds=12,
        )
    except LeetCodeImportError as exc:
        st.session_state.leetcode_import_error = str(exc)
    else:
        _apply_imported_problem(translated, replace_code=False)


def _workspace_panel_heading(kicker: str, title: str) -> str:
    """Return one compact heading block so every pane shares a baseline."""

    return (
        '<div class="workspace-panel-heading">'
        f'<p class="workspace-kicker">{html.escape(kicker)}</p>'
        f'<h3>{html.escape(title)}</h3></div>'
    )


def render_problem_pane() -> None:
    """Keep the complete problem visible beside the editor."""

    with st.container(height=760, border=True, key="problem_pane"):
        with st.container(key="problem_header"):
            header_col, hide_col = st.columns(
                [5, 0.52], vertical_alignment="center"
            )
            header_col.markdown(
                _workspace_panel_heading("Problem", _ui("题目", "Problem")),
                unsafe_allow_html=True,
            )
            hide_col.button(
                _ui("隐藏题目", "Hide problem"),
                icon=":material/visibility_off:",
                key="hide_problem_pane",
                help=_ui("隐藏题目；可从顶部布局菜单恢复", "Hide problem; restore it from Layout"),
                on_click=_set_workspace_option,
                args=("show_problem_pane", False),
            )

        imported = st.session_state.get("leetcode_problem", {})
        selected = (
            get_problem(st.session_state.selected_problem_id)
            if st.session_state.selected_problem_id
            else None
        )
        if imported:
            loaded_locale = str(imported.get("content_locale") or "en")
            wanted_locale = st.session_state.get("problem_language", "zh")
            if loaded_locale != wanted_locale:
                st.warning(
                    _ui(
                        "当前还是另一种语言的题面；刷新只会更新题面，不会覆盖代码。",
                        "This statement is in the other language. Refreshing it will not overwrite your code.",
                    )
                )
                if st.button(
                    _ui("刷新题面语言", "Refresh statement language"),
                    key="refresh_problem_translation",
                    use_container_width=True,
                ):
                    st.session_state.problem_translation_requested = True
                    st.rerun()
            badge = (
                "Premium"
                if imported.get("paid_only")
                else imported.get("difficulty", "")
            )
            st.markdown(
                f"#### {imported.get('frontend_id')}. {imported.get('title')} · {badge}"
            )
            topics = imported.get("topics") or []
            if topics:
                st.caption(" · ".join(str(topic) for topic in topics))
            st.link_button(
                _ui("在 LeetCode 打开 / 提交", "Open / Submit on LeetCode"),
                str(imported.get("url")),
                use_container_width=True,
            )
            st.divider()
            st.markdown(
                str(imported.get("statement", _ui("（题面为空）", "(Empty statement)")))
            )
        elif selected:
            st.markdown(
                f"#### {selected.id}. {_problem_title(selected)} · {selected.difficulty}"
            )
            if st.session_state.get("ui_language", "zh") == "zh":
                st.write(f"**本轮目标：** {selected.focus}")
                st.write(f"**先想清楚：** {selected.invariant_prompt}")
            st.info(
                _ui(
                    "完整题面尚未载入。载入后会一直显示在这里，不再藏在弹窗里。",
                    "The full statement is not loaded yet. Load it once and it stays beside your editor.",
                )
            )
            if st.button(
                _ui(
                    "载入完整题面和 Python 模板",
                    "Load full statement and Python template",
                ),
                type="primary",
                use_container_width=True,
                key="problem_pane_import_selected",
            ):
                with st.spinner(_ui("正在从 LeetCode 载入题面…", "Loading from LeetCode…")):
                    try:
                        _import_problem_into_workspace(selected.url)
                    except LeetCodeImportError as exc:
                        st.session_state.leetcode_import_error = str(exc)
                        st.error(str(exc))
                    else:
                        st.rerun()
        else:
            st.info(
                _ui(
                    "点击上方“JARVIS 给我下一题”，或在下面粘贴 LeetCode 链接。",
                    "Ask JARVIS for a problem, or paste a LeetCode link below.",
                )
            )

        with st.expander(
            _ui("换题 / 手动导入", "Change problem / Import manually"),
            expanded=not bool(imported or selected),
        ):
            st.text_input(
                _ui("LeetCode 题目链接或 slug", "LeetCode URL or slug"),
                key="leetcode_reference",
                placeholder="https://leetcode.com/problems/binary-search/",
            )
            if st.button(
                _ui("导入到工作台", "Import into workspace"),
                type="primary",
                use_container_width=True,
                key="problem_pane_import_manual",
            ):
                with st.spinner(
                    _ui(
                        "正在读取公开题面与 Python 模板…",
                        "Loading the public statement and Python template…",
                    )
                ):
                    try:
                        _import_problem_into_workspace(
                            st.session_state.leetcode_reference
                        )
                    except LeetCodeImportError as exc:
                        st.session_state.leetcode_import_error = str(exc)
                        st.error(str(exc))
                    else:
                        st.rerun()


def render_code_pane() -> tuple[str, str] | None:
    """Render a focused code-and-run pane and return an optional tutor request."""

    store = SolutionStore(PROJECT_ROOT)
    pending: tuple[str, str] | None = None
    with st.container(height=760, border=True, key="code_pane"):
        with st.container(key="code_header"):
            header_col, language_col, hide_col = st.columns(
                [2.6, 1.6, 0.5], vertical_alignment="center"
            )
            header_col.markdown(
                _workspace_panel_heading("Solution", _ui("代码", "Code")),
                unsafe_allow_html=True,
            )
            language = st.session_state.get("code_language", "Python")
            with language_col:
                with st.container(key="code_language_switch"):
                    python_col, java_col = st.columns(2, gap="small")
                    python_col.button(
                        "Python",
                        key="select_python_language",
                        type="primary" if language == "Python" else "secondary",
                        use_container_width=True,
                        on_click=_set_code_language,
                        args=("Python",),
                    )
                    java_col.button(
                        "Java",
                        key="select_java_language",
                        type="primary" if language == "Java" else "secondary",
                        use_container_width=True,
                        on_click=_set_code_language,
                        args=("Java",),
                    )
            hide_col.button(
                _ui("隐藏代码", "Hide code"),
                icon=":material/visibility_off:",
                key="hide_code_pane",
                help=_ui("隐藏代码；可从顶部布局菜单恢复", "Hide code; restore it from Layout"),
                on_click=_set_workspace_option,
                args=("show_code_pane", False),
            )

        with st.expander(_ui("载入已有题解", "Load an existing solution"), expanded=False):
            files = store.list_files(language)
            new_file_option = _ui("（新建）", "(New file)")
            file_options = [new_file_option, *files]
            file_key = f"existing_solution_{language.lower()}"
            if st.session_state.get(file_key) not in file_options:
                st.session_state[file_key] = new_file_option
            existing_col, load_col = st.columns([4, 1])
            existing = existing_col.selectbox(
                _ui("已有题解", "Existing solutions"),
                file_options,
                key=file_key,
                label_visibility="collapsed",
            )
            if load_col.button(
                _ui("载入", "Load"),
                disabled=existing == new_file_option,
                use_container_width=True,
                key=f"load_solution_{language.lower()}",
            ):
                try:
                    loaded_code = store.load(language, existing)
                except SolutionError as exc:
                    st.error(str(exc))
                else:
                    st.session_state.code_editor = loaded_code
                    st.session_state.code_editor_seed = loaded_code
                    st.session_state.code_editor_revision += 1
                    st.session_state[f"save_filename_{language.lower()}"] = existing
                    st.session_state.code_run_result = {}
                    st.rerun()

        editor_response = code_editor(
            st.session_state.code_editor_seed,
            lang=language.lower(),
            theme="default",
            shortcuts="vscode",
            height=26,
            allow_reset=True,
            response_mode=["debounce", "blur"],
            options={
                "fontSize": 14,
                "tabSize": 4,
                "useSoftTabs": True,
                "navigateWithinSoftTabs": True,
                "showLineNumbers": True,
                "showGutter": True,
                "displayIndentGuides": True,
                "showPrintMargin": False,
                "wrap": False,
                "enableBasicAutocompletion": True,
                "enableLiveAutocompletion": False,
                "enableSnippets": True,
                "useWorker": False,
            },
            editor_props={"$blockScrolling": True},
            key=(
                f"solution_editor_{language.lower()}_"
                f"{st.session_state.code_editor_revision}"
            ),
        )
        returned_code = editor_response.get("text")
        if editor_response.get("type") and isinstance(returned_code, str):
            st.session_state.code_editor = returned_code
        code = st.session_state.code_editor
        st.markdown(
            '<div class="code-editor-shortcuts">'
            + _ui(
                'Tab 缩进 · Shift+Tab 反缩进 · 自动缩进 · 行号与语法高亮 · ⌘/Ctrl+F 查找',
                'Tab indent · Shift+Tab outdent · auto-indent · line numbers · ⌘/Ctrl+F search',
            )
            + "</div>",
            unsafe_allow_html=True,
        )

        with st.expander(_ui("测试用例与运行设置", "Tests and run settings"), expanded=True):
            method_col, timeout_col = st.columns([2.2, 1])
            method_col.text_input(
                _ui("Solution 方法名", "Solution method"),
                key="solution_method",
                placeholder=_ui("留空时自动识别", "Auto-detect when empty"),
            )
            timeout_col.number_input(
                _ui("超时（秒）", "Timeout (seconds)"),
                min_value=0.5,
                max_value=10.0,
                step=0.5,
                key="code_timeout_seconds",
            )
            st.text_area(
                _ui("测试用例（JSON）", "Test cases (JSON)"),
                height=105,
                key="solution_test_cases",
                help='每项格式：{"args": [...], "expected": ...}',
            )

        run_col, analyze_col = st.columns(2)
        run_clicked = run_col.button(
            _ui("▶ 运行", "▶ Run"),
            key="run_code",
            type="primary",
            use_container_width=True,
            disabled=language != "Python",
        )
        analyze_clicked = analyze_col.button(
            _ui("运行并问导师", "Run and ask tutor"),
            key="analyze_code",
            use_container_width=True,
            disabled=language != "Python",
        )
        if language != "Python":
            st.info(
                _ui(
                    "Java 可编辑、保存和 Review；当前内置运行器先支持 Python。",
                    "You can edit, save, and review Java; the built-in runner currently supports Python.",
                )
            )
        if run_clicked or analyze_clicked:
            with st.spinner(_ui("正在受限子进程中运行…", "Running in a restricted subprocess…")):
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
                question = "分析最近运行结果，只指出最关键的问题并追问我。"
                pending = (
                    _workspace_request(question, trigger="运行后自动求助"),
                    "我运行了当前代码，请根据结果继续引导。",
                )

        result = st.session_state.get("code_run_result", {})
        if result:
            _render_run_result(result)

        suffix = SolutionStore.LANGUAGE_SUFFIXES[language]
        filename_key = f"save_filename_{language.lower()}"
        st.session_state.setdefault(filename_key, f"0.problem-name{suffix}")
        with st.expander(_ui("保存到仓库", "Save to repository"), expanded=False):
            st.text_input(_ui("保存文件名", "Filename"), key=filename_key)
            save_col, overwrite_col = st.columns([2, 1])
            overwrite = overwrite_col.checkbox(
                _ui("允许覆盖", "Allow overwrite"), key=f"overwrite_{language}"
            )
            if save_col.button(
                _ui("保存当前代码", "Save current code"),
                use_container_width=True,
                key=f"save_{language}",
            ):
                try:
                    path = store.save(
                        language,
                        st.session_state[filename_key].strip(),
                        code,
                        overwrite=overwrite,
                    )
                except SolutionError as exc:
                    st.error(str(exc))
                else:
                    st.success(f"已保存：{path.relative_to(PROJECT_ROOT)}")

    return pending


def render_workspace_mentor(
    *,
    pending: tuple[str, str] | None,
    settings: ProviderSettings,
    model: str,
    temperature: float,
    top_p: float,
    reasoning_effort: str,
    max_tokens: int,
    config: AppConfig,
) -> None:
    """Render one always-visible conversation surface next to the code."""

    history: list[HistoryItem] = st.session_state.algorithm_messages
    with st.container(height=650, border=True, key="mentor_pane"):
        with st.container(key="mentor_header"):
            header_col, float_col, hide_col = st.columns(
                [3.5, 0.52, 0.52], vertical_alignment="center"
            )
            with header_col:
                st.markdown(_jarvis_identity_html(compact=True), unsafe_allow_html=True)
            float_col.button(
                _ui("切换为悬浮导师", "Switch to floating tutor"),
                icon=":material/open_in_new:",
                key="float_mentor_pane",
                help=_ui("切换为悬浮导师", "Switch to floating tutor"),
                on_click=_set_workspace_option,
                args=("mentor_layout_mode", "悬浮"),
            )
            hide_col.button(
                _ui("隐藏导师", "Hide tutor"),
                icon=":material/visibility_off:",
                key="hide_mentor_pane",
                help=_ui("隐藏导师；可从顶部布局菜单恢复", "Hide tutor; restore it from Layout"),
                on_click=_set_workspace_option,
                args=("show_mentor_pane", False),
            )
        current_problem = st.session_state.get("algorithm_problem", "")
        st.caption(
            _ui("正在看：", "Watching: ")
            + (current_problem or _ui("尚未选题", "No problem selected"))
        )
        st.caption(
            _ui(
                "每个回合：1 个短提示 + 1 个问题，等你回答后再继续。",
                "Each turn: one short hint and one question. I wait for your answer before continuing.",
            )
        )

        history_box = st.container(height=340, border=True, key="mentor_history")
        with history_box:
            if history:
                render_history(history, render_mermaid=True)
            else:
                st.info(
                    _ui(
                        "我会一直待在这里，并读取左侧题面、中间代码、测试和最近运行结果。",
                        "I stay here and read the problem, your code, tests, and latest run.",
                    )
                )
                st.markdown(
                    _ui(
                        "先告诉我你的思路，或者直接写代码；卡住时我只提示下一步。",
                        "Tell me your approach or start coding. If you get stuck, I will hint only the next step.",
                    )
                )

        with st.form("mentor_workspace_form", clear_on_submit=True, border=False):
            question = st.text_area(
                _ui("继续问 JARVIS", "Continue with JARVIS"),
                height=82,
                placeholder=_ui(
                    "继续追问；不用回到页面上方，也不用再贴代码",
                    "Keep asking here; there is no need to paste your code again",
                ),
                label_visibility="collapsed",
            )
            send_clicked = st.form_submit_button(
                _ui("发送给 JARVIS", "Send to JARVIS"),
                type="primary",
                use_container_width=True,
            )
            stuck_col, next_col, review_col = st.columns(3)
            stuck_clicked = stuck_col.form_submit_button(
                _ui("我卡住了", "I'm stuck"), use_container_width=True
            )
            next_clicked = next_col.form_submit_button(
                _ui("下一步", "Next step"), use_container_width=True
            )
            review_clicked = review_col.form_submit_button(
                "Review", use_container_width=True
            )

        requested: tuple[str, str] | None = pending
        stripped_question = question.strip()
        if send_clicked:
            text = stripped_question or "请根据当前代码现场继续问我一个问题。"
            requested = (
                _workspace_request(text, trigger="导师工作台连续对话"),
                text,
            )
        elif stuck_clicked:
            text = stripped_question or (
                "我卡住了。判断我已经做到哪里，只指出一个最关键的问题。"
            )
            requested = (
                _workspace_request(text, trigger="用户在代码现场卡住"),
                text,
            )
        elif next_clicked:
            text = stripped_question or (
                "不要给完整解法；根据现有代码只提示下一步，再问我一个问题。"
            )
            requested = (
                _workspace_request(text, trigger="继续导师对练"),
                text,
            )
        elif review_clicked:
            code = st.session_state.get("code_editor", "")
            if not code.strip():
                st.warning("先在中间写一点代码，我才能 Review。")
            else:
                requested = (
                    build_code_review_request(
                        problem=current_problem,
                        language=st.session_state.get("code_language", "Python"),
                        code=code,
                        notes=(stripped_question + "\n最近运行现场：\n" + _run_result_text()),
                    ),
                    stripped_question or "请 Review 当前代码，但先别直接给完整答案。",
                )

        if requested:
            if not model:
                st.warning("先在左侧模型设置中选择或填写模型。")
            else:
                with history_box:
                    submit_to_tutor(
                        mode="algorithm",
                        content=requested[0],
                        display=requested[1],
                        settings=settings,
                        model=model,
                        temperature=temperature,
                        top_p=top_p,
                        reasoning_effort=reasoning_effort,
                        max_tokens=max_tokens,
                        config=config,
                        surface="workspace",
                    )


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
    st.session_state.algorithm_problem = f"{selected.id}. {_problem_title(selected)}"
    st.session_state.code_language = "Python"
    st.session_state.save_filename_python = f"{selected.id}.{selected.slug}.py"
    st.session_state.algorithm_notes = f"导师训练目标：{selected.focus}"
    return selected


def render_mentor_panel() -> tuple[str, str] | None:
    """Render compact curriculum controls above the three-pane workspace."""

    pending: tuple[str, str] | None = None
    progress = st.session_state.study_progress
    summary = progress_summary(progress)
    problem = (
        get_problem(st.session_state.selected_problem_id)
        if st.session_state.selected_problem_id
        else None
    )

    with st.container(border=True, key="algorithm_mission_control"):
        summary_col, guide_col, next_col, settings_col = st.columns(
            [3.8, 1, 1, 1], vertical_alignment="center"
        )
        with summary_col:
            if problem:
                st.markdown(
                    f"**{_ui('导师训练', 'Tutor session')} · {problem.id}. "
                    f"{_problem_title(problem)} · {problem.difficulty}**"
                )
                if st.session_state.get("ui_language", "zh") == "zh":
                    focus_prefix = problem.focus + "　·　"
                else:
                    focus_prefix = ""
                st.caption(
                    focus_prefix
                    + _ui("已练", "Attempted")
                    + f" {summary['attempted']}/{summary['total']}　·　"
                    + _ui("掌握", "Mastered")
                    + f" {summary['mastered']}　·　"
                    + _ui("复习", "Review")
                    + f" {summary['review']}"
                )
            else:
                st.markdown(
                    "**"
                    + _ui("导师训练 · 还没有选择题目", "Tutor session · No problem selected")
                    + "**"
                )
                st.caption(
                    _ui(
                        "JARVIS 会从二分、栈、优先队列和 DP 中安排起点。",
                        "JARVIS will choose a starting point from binary search, stacks, priority queues, and DP.",
                    )
                )

        start_guide = guide_col.button(
            _ui("开始引导", "Start coaching"),
            use_container_width=True,
            disabled=problem is None,
        )
        if next_col.button(
            _ui("JARVIS 给我下一题", "JARVIS, next problem"),
            type="primary",
            use_container_width=True,
        ):
            try:
                selected = _select_next_mentor_problem()
            except ValueError as exc:
                st.error(str(exc))
            else:
                with st.spinner(
                    _ui(
                        "导师正在载入完整题面和 Python 模板…",
                        "Loading the full statement and Python template…",
                    )
                ):
                    try:
                        _import_problem_into_workspace(selected.url)
                    except LeetCodeImportError as exc:
                        st.session_state.leetcode_import_error = str(exc)
                st.rerun()

        with settings_col.popover(_ui("训练设置", "Training settings"), use_container_width=True):
            st.selectbox(
                _ui("训练路线", "Training track"),
                ["自动补弱", *TOPIC_ORDER],
                format_func=lambda value: {
                    "自动补弱": _ui("自动补弱", "Auto-focus weaknesses"),
                    "二分": _ui("二分", "Binary search"),
                    "栈": _ui("栈", "Stack"),
                    "优先队列": _ui("优先队列", "Priority queue"),
                    "DP": "DP",
                }[value],
                key="mentor_track",
            )
            st.selectbox(
                _ui("难度", "Difficulty"),
                ["循序渐进", "Easy", "Medium", "Hard"],
                format_func=lambda value: (
                    _ui("循序渐进", "Progressive")
                    if value == "循序渐进"
                    else value
                ),
                key="mentor_difficulty",
            )
            if problem:
                leetcode_host = (
                    "leetcode.cn"
                    if st.session_state.get("problem_language", "zh") == "zh"
                    else "leetcode.com"
                )
                problem_url = f"https://{leetcode_host}/problems/{problem.slug}/"
                st.link_button(
                    _ui("打开 LeetCode", "Open LeetCode"),
                    problem_url,
                    use_container_width=True,
                )
                mastered_col, review_col = st.columns(2)
                if mastered_col.button(_ui("标记掌握", "Mark mastered"), use_container_width=True):
                    _save_problem_status(problem, "mastered")
                    st.toast("已记录为掌握。")
                if review_col.button(_ui("稍后复习", "Review later"), use_container_width=True):
                    _save_problem_status(problem, "review")
                    st.toast("已加入复习队列。")

        if problem:
            redraw_visual = render_visual_learning_map(
                mode="algorithm",
                diagram=algorithm_pattern_mermaid(
                    problem.topic,
                    language=st.session_state.get("ui_language", "zh"),
                ),
                focus=(
                    problem.focus
                    if st.session_state.get("ui_language", "zh") == "zh"
                    else f"{problem.topic} pattern · {problem.title}"
                ),
            )
            if redraw_visual:
                question = _ui(
                    "请根据当前题面、我的代码和运行结果，用一个 3～8 节点的 Mermaid 图画出当前状态变化。不要给完整解法；图后只问我一个问题。",
                    "Using the current statement, code, and run result, draw the current state transition as one 3–8 node Mermaid diagram. Do not give the full solution; ask exactly one question after the diagram.",
                )
                pending = (
                    _workspace_request(question, trigger="视觉解释"),
                    _ui("视觉解释：请按我的当前进度重画", "Visual: redraw my current state"),
                )

        if problem and st.session_state.get("leetcode_import_error"):
            st.warning(
                "自动导入题面失败："
                + st.session_state.leetcode_import_error
                + " 可在左侧题目栏重试。"
            )

        if problem and start_guide:
            attempt = int(progress.get(str(problem.id), {}).get("attempts", 1))
            imported = st.session_state.get("leetcode_problem", {})
            imported_matches = imported.get("slug") == problem.slug
            if not imported_matches:
                with st.spinner(
                    _ui(
                        "正在载入完整题面和 Python 模板…",
                        "Loading the full statement and Python template…",
                    )
                ):
                    try:
                        _import_problem_into_workspace(problem.url)
                    except LeetCodeImportError as exc:
                        st.session_state.leetcode_import_error = str(exc)
            opening = build_tutor_opening(
                problem_id=problem.id,
                title_cn=_problem_title(problem),
                difficulty=problem.difficulty,
                topic=problem.topic,
                focus=problem.focus,
                invariant_prompt=problem.invariant_prompt,
                attempt=attempt,
                language=st.session_state.get("ui_language", "zh"),
            )
            history: list[HistoryItem] = st.session_state.algorithm_messages
            history.append(
                {
                    "role": "user",
                    "content": _ui("开始导师带练：", "Start tutor session: ")
                    + f"{problem.id}. {_problem_title(problem)}",
                    "display": _ui("开始导师带练：", "Start tutor session: ")
                    + f"**{problem.id}. {_problem_title(problem)}**",
                }
            )
            history.append({"role": "assistant", "content": opening})
            st.rerun()

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
    pending = render_mentor_panel()
    docked_mentor = (
        st.session_state.show_mentor_pane
        and st.session_state.mentor_layout_mode == "停靠"
    )
    pane_specs: list[tuple[str, float]] = []
    if st.session_state.show_problem_pane:
        pane_specs.append(("problem", 0.95))
    if st.session_state.show_code_pane:
        pane_specs.append(("code", 1.15))
    if docked_mentor:
        pane_specs.append(("mentor", 0.9))

    if pane_specs:
        columns = st.columns(
            [weight for _, weight in pane_specs],
            gap="small",
            vertical_alignment="top",
        )
        for (pane, _), column in zip(pane_specs, columns, strict=True):
            with column:
                if pane == "problem":
                    render_problem_pane()
                elif pane == "code":
                    code_request = render_code_pane()
                    if code_request:
                        pending = code_request
                else:
                    render_workspace_mentor(
                        pending=pending,
                        settings=settings,
                        model=model,
                        temperature=temperature,
                        top_p=top_p,
                        reasoning_effort=reasoning_effort,
                        max_tokens=max_tokens,
                        config=config,
                    )
    elif not st.session_state.show_mentor_pane:
        st.info(
            _ui(
                "三个工作面板都已隐藏。使用右上角“布局”随时恢复。",
                "All three workspace panes are hidden. Use Layout in the top-right to restore them.",
            )
        )

    if (
        st.session_state.show_mentor_pane
        and st.session_state.mentor_layout_mode == "悬浮"
    ):
        render_floating_mentor(
            mode="algorithm",
            model=model,
            settings=settings,
            temperature=temperature,
            top_p=top_p,
            reasoning_effort=reasoning_effort,
            max_tokens=max_tokens,
            config=config,
            pending=pending,
        )
    elif pending and not docked_mentor:
        st.warning("导师目前已隐藏；运行结果已保留，恢复导师后可以继续分析。")


def _system_case_text(case: SystemDesignCase, field: str) -> str:
    return case.localized(field, st.session_state.get("ui_language", "zh"))


def _select_next_system_design_mission() -> SystemDesignCase:
    selected = choose_next_system_design_case(
        current_id=st.session_state.system_design_selected_id,
        track=st.session_state.system_design_track,
        difficulty=st.session_state.system_design_difficulty,
    )
    st.session_state.system_design_selected_id = selected.id
    st.session_state.system_requirement = _system_case_text(
        selected, "requirement"
    )
    attempted = list(st.session_state.system_design_attempted)
    if selected.id not in attempted:
        attempted.append(selected.id)
    st.session_state.system_design_attempted = attempted

    opening = (
        "**MISSION BRIEF**  \n"
        + _system_case_text(selected, "requirement")
        + "\n\n**"
        + _ui("第一关：", "CHECKPOINT 01: ")
        + "** "
        + _system_case_text(selected, "first_question")
    )
    history: list[HistoryItem] = st.session_state.system_design_messages
    history.append(
        {
            "role": "user",
            "content": f"JARVIS assigned mission {selected.id}: "
            + _system_case_text(selected, "title"),
            "display": _ui("JARVIS 分配任务：", "JARVIS assigned: ")
            + f"**{selected.id} · {_system_case_text(selected, 'title')}**",
        }
    )
    history.append({"role": "assistant", "content": opening})
    return selected


def render_system_design_mission_control() -> tuple[
    SystemDesignCase | None, tuple[str, str] | None
]:
    case = get_system_design_case(st.session_state.system_design_selected_id)
    pending: tuple[str, str] | None = None
    attempted = len(st.session_state.system_design_attempted)

    with st.container(border=True, key="system_mission_control"):
        brief_col, start_col, next_col, settings_col = st.columns(
            [3.7, 1, 1.15, 0.95], vertical_alignment="center"
        )
        with brief_col:
            st.markdown(
                '<p class="workspace-kicker">JARVIS MISSION CONTROL</p>',
                unsafe_allow_html=True,
            )
            if case:
                st.markdown(
                    f"**{case.id} · {_system_case_text(case, 'title')} · "
                    f"{case.difficulty}**"
                )
                st.caption(
                    _system_case_text(case, "focus")
                    + f"　·　{_ui('已训练', 'Attempted')} {attempted}"
                )
            else:
                st.markdown(
                    "**"
                    + _ui(
                        "等待 JARVIS 分配第一项架构任务",
                        "Awaiting the first architecture mission",
                    )
                    + "**"
                )
                st.caption(
                    _ui(
                        "任务覆盖扩展性、可靠性、实时系统、数据平台与事务。",
                        "Missions cover scaling, reliability, realtime systems, data, and transactions.",
                    )
                )

        start_clicked = start_col.button(
            _ui("开始压力测试", "Start stress test"),
            use_container_width=True,
            disabled=not bool(st.session_state.system_requirement.strip()),
        )
        if next_col.button(
            _ui("JARVIS 分配任务", "JARVIS, assign mission"),
            type="primary",
            use_container_width=True,
        ):
            try:
                _select_next_system_design_mission()
            except ValueError as exc:
                st.error(str(exc))
            else:
                st.rerun()

        with settings_col.popover(
            _ui("任务设置", "Mission settings"), use_container_width=True
        ):
            track_labels = {
                "auto": _ui("自动轮换", "Auto rotation"),
                "scaling": _ui("扩展性", "Scaling"),
                "reliability": _ui("可靠性", "Reliability"),
                "realtime": _ui("实时系统", "Realtime"),
                "data": _ui("数据平台", "Data"),
                "transactions": _ui("事务", "Transactions"),
            }
            st.selectbox(
                _ui("任务路线", "Mission track"),
                ["auto", *SYSTEM_DESIGN_TRACKS],
                format_func=track_labels.get,
                key="system_design_track",
            )
            st.selectbox(
                _ui("难度", "Difficulty"),
                ["progressive", "Easy", "Medium", "Hard"],
                format_func=lambda value: (
                    _ui("循序渐进", "Progressive")
                    if value == "progressive"
                    else value
                ),
                key="system_design_difficulty",
            )

        if case:
            st.divider()
            st.markdown(
                '<p class="workspace-kicker">MISSION BRIEF</p>',
                unsafe_allow_html=True,
            )
            st.markdown("### " + _system_case_text(case, "title"))
            st.write(_system_case_text(case, "requirement"))
            st.info(
                "**"
                + _ui("当前检查点：", "CURRENT CHECKPOINT: ")
                + "** "
                + _system_case_text(case, "first_question")
            )
            redraw_visual = render_visual_learning_map(
                mode="system_design",
                diagram=system_design_pattern_mermaid(
                    case.track,
                    language=st.session_state.get("ui_language", "zh"),
                ),
                focus=_system_case_text(case, "focus"),
            )
            if redraw_visual:
                pending = (
                    _ui(
                        "请根据我目前已经确认的需求、估算和取舍，重画一张 3～8 节点的 Mermaid 架构图。"
                        "只画已确认的组件与一个待定边界，不要展开完整方案；图后只问我一个问题。",
                        "Redraw the requirements, estimates, and trade-offs I have confirmed as one 3–8 node Mermaid architecture diagram. Show only confirmed components and one open boundary; do not expand the full design, and ask exactly one question after the diagram.",
                    ),
                    _ui(
                        "视觉解释：按当前方案更新架构图",
                        "Visual: update the architecture from my current design",
                    ),
                )

        with st.expander(
            _ui("自定义或修改任务", "Customize the mission"),
            expanded=False,
        ):
            st.text_area(
                _ui("设计需求", "Design requirement"),
                key="system_requirement",
                height=110,
                placeholder=_ui(
                    "也可以输入你自己的系统设计题目",
                    "You can also enter your own system-design prompt",
                ),
            )

        if start_clicked:
            requirement = st.session_state.system_requirement.strip()
            if not requirement:
                st.error(
                    _ui(
                        "当前任务没有设计需求。",
                        "The current mission has no requirement.",
                    )
                )
            else:
                pending = (
                    build_system_design_request(requirement),
                    _ui("开始架构压力测试：", "Start architecture stress test: ")
                    + f"**{_system_case_text(case, 'title') if case else requirement}**",
                )
    return case, pending


def _system_design_next_step_request(requirement: str) -> tuple[str, str]:
    return (
        _ui(
            "只推进系统设计面试的下一个关键决策。不要复述需求，不要给完整架构；"
            "先用 1～2 句最小提示，再用 3～6 个节点的 Mermaid 图标出当前已确认范围和一个待定边界，"
            "最后只问我一个问题。\n\n当前需求：",
            "Advance only the next critical system-design decision. Do not restate the requirement or provide a full architecture. Give a 1–2 sentence hint, then a 3–6 node Mermaid diagram showing the confirmed scope and one open boundary, and finish with exactly one question.\n\nCurrent requirement: ",
        )
        + requirement,
        _ui("只提示下一步", "Next step only"),
    )


def render_system_design_command_dock(
    case: SystemDesignCase | None,
) -> tuple[str, str] | None:
    """Keep the only system-design composer reachable at the viewport bottom."""

    requirement = st.session_state.system_requirement.strip()
    with st.container(key="system_command_dock"):
        st.markdown(
            '<p class="system-command-label"><span class="system-command-dot"></span>'
            + _ui(
                "JARVIS 指令栏 · 完整对话在右下角",
                "JARVIS command bar · full conversation at bottom-right",
            )
            + "</p>",
            unsafe_allow_html=True,
        )
        with st.form(
            "system_design_command_form",
            clear_on_submit=True,
            border=False,
        ):
            input_col, send_col, next_col = st.columns(
                [4.8, 0.9, 1.2], vertical_alignment="center"
            )
            prompt = input_col.text_input(
                _ui("继续问 JARVIS", "Continue with JARVIS"),
                key="system_design_command_input",
                label_visibility="collapsed",
                placeholder=(
                    _ui(
                        "回答当前检查点，或追问一个架构取舍……",
                        "Answer the checkpoint or ask about one trade-off…",
                    )
                    if requirement
                    else _ui(
                        "先让 JARVIS 分配任务，或填写自定义需求",
                        "Assign a mission or enter a custom requirement first",
                    )
                ),
            )
            send_clicked = send_col.form_submit_button(
                _ui("发送", "Send"),
                use_container_width=True,
                disabled=not bool(requirement),
            )
            next_clicked = next_col.form_submit_button(
                _ui("只提示下一步", "Next step"),
                type="primary",
                use_container_width=True,
                disabled=not bool(requirement),
            )

    if next_clicked:
        return _system_design_next_step_request(requirement)
    if send_clicked:
        cleaned = prompt.strip()
        if not cleaned:
            return _system_design_next_step_request(requirement)
        return (
            _ui(
                "这是系统设计导师对练。请结合已有对话与当前任务，一次只推进一个关键判断。\n\n当前需求：",
                "This is a system-design coaching turn. Use the existing conversation and mission, and advance only one critical judgment.\n\nCurrent requirement: ",
            )
            + requirement
            + _ui("\n\n我的输入：", "\n\nMy input: ")
            + cleaned,
            cleaned,
        )
    return None


def _latest_system_design_exchange() -> tuple[str, str] | None:
    history: list[HistoryItem] = st.session_state.system_design_messages
    assistant_index = next(
        (
            index
            for index in range(len(history) - 1, -1, -1)
            if history[index]["role"] == "assistant"
        ),
        -1,
    )
    if assistant_index < 0:
        return None
    answer = history[assistant_index].get(
        "display", history[assistant_index]["content"]
    )
    if history[assistant_index]["content"].lstrip().startswith("**MISSION BRIEF**"):
        return None
    question = ""
    for index in range(assistant_index - 1, -1, -1):
        if history[index]["role"] == "user":
            question = history[index].get("display", history[index]["content"])
            break
    return question, answer


def render_system_design_live_panel(
    pending: tuple[str, str] | None,
) -> dict[str, Any] | None:
    """Render one live/latest turn on the page while the popover owns history."""

    latest = _latest_system_design_exchange()
    state_label = (
        _ui("实时生成", "Generating")
        if pending
        else _ui("最新回合", "Latest turn")
        if latest
        else _ui("等待开始", "Ready")
    )
    with st.container(border=True, key="system_live_panel"):
        identity_col, phase_col = st.columns([5.4, 1], vertical_alignment="center")
        identity_col.markdown(
            '<div class="system-live-heading"><div class="system-live-identity">'
            '<span class="system-live-orb"></span><div>'
            '<div class="system-live-title">JARVIS LIVE</div>'
            '<div class="system-live-subtitle">'
            + _ui(
                "主界面只显示当前回合；浮窗保存完整历史",
                "The page shows this turn; the popover keeps full history",
            )
            + "</div></div></div></div>",
            unsafe_allow_html=True,
        )
        phase = phase_col.empty()
        phase.markdown(
            '<span class="system-live-status">' + state_label + "</span>",
            unsafe_allow_html=True,
        )

        if pending:
            st.markdown(
                '<p class="system-latest-question">'
                + _ui("你：", "You: ")
                + html.escape(pending[1])
                + "</p>",
                unsafe_allow_html=True,
            )
            activity = st.status(
                _ui("已交给 JARVIS，正在准备当前回合…", "JARVIS is preparing this turn…"),
                expanded=False,
            )
            output = st.empty()
            state_mount = st.empty()
            return {
                "activity": activity,
                "output": output,
                "phase": phase,
                "state_mount": state_mount,
            }

        if latest:
            question, answer = latest
            if question:
                st.markdown(
                    '<p class="system-latest-question">'
                    + _ui("你：", "You: ")
                    + html.escape(question)
                    + "</p>",
                    unsafe_allow_html=True,
                )
            render_assistant_content(answer, render_mermaid=True)
        else:
            st.caption(
                _ui(
                    "回答当前检查点，或使用底部“只提示下一步”；状态和新回复会直接出现在这里。",
                    "Answer the checkpoint or use Next step below; progress and the new reply will appear here.",
                )
            )
    return None


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
    mission_col, live_col = st.columns(
        [1.28, 0.92], gap="medium", vertical_alignment="top"
    )
    with mission_col:
        case, pending = render_system_design_mission_control()
    command = render_system_design_command_dock(case)
    if command:
        pending = command

    # Main page: one live/latest turn. Floating window: complete transcript.
    with live_col:
        mirror = render_system_design_live_panel(pending)
    render_floating_mentor(
        mode="system_design",
        model=model,
        settings=settings,
        temperature=temperature,
        top_p=top_p,
        reasoning_effort=reasoning_effort,
        max_tokens=max_tokens,
        config=config,
        pending=pending,
        mirror=mirror,
    )


def render_workspace_layout_controls() -> None:
    """Keep restoration controls available even when every pane is hidden."""

    with st.popover(_ui("布局", "Layout"), use_container_width=True):
        st.markdown("**" + _ui("显示面板", "Visible panes") + "**")
        st.toggle(_ui("题目", "Problem"), key="show_problem_pane")
        st.toggle(_ui("代码", "Code"), key="show_code_pane")
        st.toggle(_ui("导师", "Tutor"), key="show_mentor_pane")
        st.radio(
            _ui("导师形态", "Tutor style"),
            ["停靠", "悬浮"],
            format_func=lambda value: (
                _ui("停靠", "Docked") if value == "停靠" else _ui("悬浮", "Floating")
            ),
            horizontal=True,
            key="mentor_layout_mode",
            disabled=not st.session_state.show_mentor_pane,
        )
        st.button(
            _ui("恢复默认布局", "Restore default layout"),
            use_container_width=True,
            on_click=_restore_workspace_layout,
        )


def render_language_controls() -> None:
    with st.popover(_ui("语言", "Language"), use_container_width=True):
        st.selectbox(
            _ui("界面语言", "Interface language"),
            ["zh", "en"],
            format_func=lambda value: "中文" if value == "zh" else "English",
            key="ui_language",
        )
        st.selectbox(
            _ui("LeetCode 题面", "LeetCode statement"),
            ["zh", "en"],
            format_func=lambda value: (
                _ui("中文（中国站）", "Chinese (LeetCode CN)")
                if value == "zh"
                else _ui("英文（国际站）", "English (LeetCode.com)")
            ),
            key="problem_language",
            on_change=_request_problem_translation,
        )
        active_theme = getattr(st.context.theme, "type", None) or "light"
        st.markdown("**" + _ui("显示主题", "Display theme") + "**")
        st.caption(
            _ui(
                "使用顶部控制组的明暗图标选择 System / Light / Dark；IDE 会同步。当前：",
                "Use the appearance control in the top utility group to choose System, Light, or Dark. The IDE follows it. Current: ",
            )
            + _ui("深色" if active_theme == "dark" else "浅色", active_theme)
        )


def render_app_header(
    *,
    mode: Literal["algorithm", "system_design"],
    settings: ProviderSettings,
    model: str,
) -> None:
    """Render one cohesive product bar instead of unrelated top-row widgets."""

    with st.container(key="app_header"):
        brand_col, mode_col, actions_col = st.columns(
            [1.45, 0.72, 0.58], vertical_alignment="center"
        )
        brand_col.markdown(
            '<div class="brand-shell">'
            '<button class="leettutor-product-mark" type="button" '
            'aria-label="打开或收起设置侧栏" title="设置与本地模型">LT</button>'
            '<div class="brand-copy"><div class="brand-eyebrow">JARVIS LEARNING SYSTEM</div>'
            '<div class="brand-title-row"><span class="brand-title">LeetTutor</span>'
            '<span class="brand-maker">Made by Tony</span></div>'
            '<div class="runtime-strip"><span class="runtime-dot"></span>'
            f'<span>{html.escape(settings.provider)} · '
            f'{html.escape(model or _ui("未选择模型", "No model"))} · '
            f'{html.escape(settings.endpoint)}</span></div></div></div>',
            unsafe_allow_html=True,
        )

        with mode_col.container(key="app_mode"):
            algorithm_col, system_col = st.columns(2, gap="small")
            algorithm_col.button(
                _ui("算法", "Algorithms"),
                key="switch_to_algorithm",
                type="primary" if mode == "algorithm" else "secondary",
                use_container_width=True,
                on_click=_set_training_mode,
                args=("算法刷题",),
            )
            system_col.button(
                _ui("系统设计", "System design"),
                key="switch_to_system_design",
                type="primary" if mode == "system_design" else "secondary",
                use_container_width=True,
                on_click=_set_training_mode,
                args=("系统设计",),
            )

        if mode == "algorithm":
            language_col, layout_col = actions_col.columns(2)
            with language_col:
                render_language_controls()
            with layout_col:
                render_workspace_layout_controls()
        else:
            with actions_col:
                render_language_controls()


def main() -> None:
    configure_page()
    config = initialize_state()

    if st.session_state.config_load_error:
        st.warning(
            f"配置文件没有成功载入，当前使用默认值。{st.session_state.config_load_error}"
        )
    if st.session_state.progress_load_error:
        st.warning(
            f"学习进度没有成功载入，本次从空进度开始。{st.session_state.progress_load_error}"
        )

    selected_mode = st.session_state.get("mode_label", "算法刷题")
    mode: Literal["algorithm", "system_design"] = MODE_LABELS[selected_mode]  # type: ignore[assignment]
    (
        settings,
        model,
        temperature,
        top_p,
        reasoning_effort,
        max_tokens,
    ) = render_sidebar(config, mode)
    render_app_header(mode=mode, settings=settings, model=model)
    _refresh_imported_problem_translation()

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
