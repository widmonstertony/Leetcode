"use strict";

const LOCAL_APP_URL = "http://127.0.0.1:8501/?embed=true";
const $ = (selector) => document.querySelector(selector);

const copy = {
  en: {
    language: "Language", theme: "Theme", eyebrow: "ORIGINAL APP · LOCAL EXECUTION",
    title: "The original LeetTutor, in this tab.",
    intro: "No replacement dashboard. The hosted page opens the repository's real Streamlit product, including the floating JARVIS, original workspace, progress, editor, and model settings.",
    privacy: "The app and model run only on this computer. tonytan.me does not receive code, prompts, progress, or answers.",
    step1Title: "Start your model", step1Body: "Open Ollama, LM Studio, or the configured AMD Metal runtime.",
    step2Title: "Start the original app", step2Body: "Double-click launch_companion.command, or run:",
    step3Title: "Connect here", step3Body: "Allow local-network access when Chrome asks. The original app then fills this tab.",
    connect: "Open original LeetTutor", fallback: "Open the original app directly ↗",
    waiting: "Waiting for the original app on this computer.", opening: "Opening the original LeetTutor…",
    troubleshoot: "Chrome already blocked it?",
    permissionHelp: "In the address bar, open Site controls → Site settings → Local network access → Allow, refresh, and connect again.",
  },
  zh: {
    language: "语言", theme: "主题", eyebrow: "原版应用 · 本机执行",
    title: "原版 LeetTutor，直接在这个标签页使用。",
    intro: "不再使用另做的 Dashboard。网页会打开仓库中真正的 Streamlit 产品，保留漂浮 JARVIS、原工作区、学习进度、编辑器和模型设置。",
    privacy: "应用与模型只在这台电脑运行；代码、Prompt、进度和回答都不会发送给 tonytan.me。",
    step1Title: "启动模型", step1Body: "打开 Ollama、LM Studio，或已经配置的 AMD Metal Runtime。",
    step2Title: "启动原版应用", step2Body: "双击 launch_companion.command，或运行：",
    step3Title: "在这里连接", step3Body: "Chrome 询问时允许本地网络访问，随后原版应用会铺满这个标签页。",
    connect: "打开原版 LeetTutor", fallback: "直接打开原版应用 ↗",
    waiting: "正在等待这台电脑上的原版应用。", opening: "正在打开原版 LeetTutor…",
    troubleshoot: "Chrome 之前已经拒绝？",
    permissionHelp: "点击地址栏左侧的网站控制 → 网站设置 → 本地网络访问 → 允许，然后刷新并重新连接。",
  },
};

function detectedLocale() {
  return navigator.languages?.some((value) => value.toLowerCase().startsWith("zh")) ? "zh" : "en";
}

function locale() {
  const preference = localStorage.getItem("leettutor-language") || "system";
  return preference === "system" ? detectedLocale() : preference;
}

function applyPreferences() {
  const language = localStorage.getItem("leettutor-language") || "system";
  const theme = localStorage.getItem("leettutor-theme") || "system";
  const resolvedTheme = theme === "system"
    ? (matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light")
    : theme;
  document.documentElement.lang = locale() === "zh" ? "zh-CN" : "en";
  document.documentElement.dataset.theme = resolvedTheme;
  $("#language").value = language;
  $("#theme").value = theme;
  document.querySelectorAll("[data-copy]").forEach((node) => {
    node.textContent = copy[locale()][node.dataset.copy] || copy.en[node.dataset.copy] || node.dataset.copy;
  });
}

function connectOriginal() {
  const button = $("#connect-original");
  button.disabled = true;
  $("#connection-status").textContent = copy[locale()].opening;
  document.body.classList.add("is-connecting");
  const frame = $("#original-app");
  frame.src = LOCAL_APP_URL;
}

function revealOriginal() {
  const frame = $("#original-app");
  if (!frame.src.startsWith("http://127.0.0.1:8501/")) return;
  $("#connection-view").hidden = true;
  $("#app-view").hidden = false;
  document.body.classList.remove("is-connecting");
  document.body.classList.add("is-connected");
  document.title = "LeetTutor — JARVIS Learning System";
}

document.addEventListener("DOMContentLoaded", () => {
  applyPreferences();
  $("#language").addEventListener("change", (event) => {
    localStorage.setItem("leettutor-language", event.target.value);
    applyPreferences();
  });
  $("#theme").addEventListener("change", (event) => {
    localStorage.setItem("leettutor-theme", event.target.value);
    applyPreferences();
  });
  $("#connect-original").addEventListener("click", connectOriginal);
  $("#original-app").addEventListener("load", revealOriginal);
});
