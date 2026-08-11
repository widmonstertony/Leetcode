"use strict";

const $ = (selector, root = document) => root.querySelector(selector);
const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
const STORAGE = "leettutor-web-v2";
const RECOMMENDED_MODEL = "qwen3.5:9b";
const PROVIDERS = {
  ollama: { endpoint: "http://127.0.0.1:11434", model: RECOMMENDED_MODEL },
  lmstudio: { endpoint: "http://127.0.0.1:1234", model: "local-model" },
  amdmetal: { endpoint: "http://127.0.0.1:11435", model: "qwen3.5-9b" },
  openai: { endpoint: "https://api.openai.com", model: "gpt-5-mini", key: true },
  gemini: { endpoint: "https://generativelanguage.googleapis.com", model: "gemini-2.5-flash", key: true },
};

const copy = {
  en: {
    skip: "Skip to workspace", modelService: "Model service", apiKeyNote: "Kept only in this tab and never sent to tonytan.me.", hardwareSetup: "Hardware detection and model installation", openOllama: "Open Ollama", setupGuide: "Setup guide", refreshModels: "Check service and refresh models", detectedModels: "Detected models", manualModel: "Enter manually…", autoTune: "Auto-tune for hardware and model", generationSettings: "Generation settings", timeoutSeconds: "Timeout (seconds)", contextTokens: "Context tokens", reasoningEffort: "Reasoning effort", maxOutput: "Maximum output tokens", rolePrompts: "Role prompts (editable)", algorithmInterviewer: "Algorithm interviewer", systemArchitect: "System architect", saveSettings: "Save settings", clearChat: "Clear chat", openVscode: "Open repository in VS Code", localPrivacy: "Code runs only when requested. Local-provider prompts travel directly from this browser to the configured loopback endpoint.",
    checkingModel: "Checking local model…", algorithms: "Algorithms", systemDesign: "System design", language: "Language", interfaceLanguage: "Interface language", statementLanguage: "LeetCode statement", layout: "Layout", visiblePanes: "Visible panes", problem: "Problem", code: "Code", tutor: "Tutor", tutorStyle: "Tutor style", docked: "Docked", floating: "Floating", restoreLayout: "Restore default layout", startCoaching: "Start coaching", nextProblem: "JARVIS, next problem", trainingSettings: "Training settings", trainingTrack: "Training track", difficulty: "Difficulty", progressive: "Progressive", openLeetcode: "Open LeetCode", markMastered: "Mark mastered", reviewLater: "Review later", visualMap: "Visual map · See the structure first", mentalModel: "Current mental model", visualMapHelp: "This is a scalable scaffold, not the full answer. JARVIS can redraw it from your current work.", redrawJarvis: "Ask JARVIS to redraw my current state", hideProblem: "Hide problem", roundGoal: "Round goal:", thinkFirst: "Think first:", statementMissing: "The full statement is not loaded yet. Load it once and it stays beside your editor.", loadStatement: "Load full statement and Python template", changeProblem: "Change problem / Import manually", problemReference: "LeetCode URL or slug", importWorkspace: "Import into workspace", hideCode: "Hide code", loadSolution: "Load an existing solution", newFile: "(New file)", load: "Load", upload: "Upload", editorLabel: "Solution editor", editorHelp: "Tab indent · Shift+Tab outdent · auto-indent · line numbers and syntax highlighting · ⌘/Ctrl+F search", testsAndRun: "Tests and run settings", methodName: "Solution method", testCases: "Test cases (JSON)", run: "▶ Run", runAndAsk: "Run and ask tutor", saveRepository: "Save to repository", filename: "Filename", saveCode: "Save current code", allowOverwrite: "Allow overwrite", mentorRole: "AI INTERVIEW COPILOT", watching: "Watching:", turnRule: "Each turn: one short hint and one question. I wait for your answer before continuing.", sendJarvis: "Send to JARVIS", stuck: "I'm stuck", nextStep: "Next step", allHidden: "All three workspace panes are hidden. Use Layout to restore them.",
    startStress: "Start stress test", assignMission: "JARVIS, assign mission", missionSettings: "Mission settings", missionTrack: "Mission track", autoRotation: "Auto rotation", scaling: "Scaling", reliability: "Reliability", realtime: "Realtime", dataPlatform: "Data platform", transactions: "Transactions", currentCheckpoint: "Current checkpoint:", redrawArchitecture: "Ask JARVIS to redraw my architecture", customMission: "Customize the mission", designRequirement: "Design requirement", liveSubtitle: "The page shows this turn; the floating window keeps full history", ready: "Ready", liveEmpty: "Answer the current checkpoint or use Next step below; progress and the new reply will appear here.", commandDock: "JARVIS command bar · full conversation at bottom-right", send: "Send", nextStepOnly: "Next step only", replyJarvis: "Reply to JARVIS", dockRight: "Dock to right pane", hideTutor: "Hide tutor",
    localModel: "LOCAL MODEL", connectOllama: "Connect Ollama", continue: "Continue to workspace", retry: "Check again", launchExplanation: "Your browser may ask permission to open Ollama. This site cannot start software without your confirmation.", missingExplanation: "If Ollama did not open, install it once, open it, and return here. LeetTutor itself requires no installation.", downloadOllama: "Download Ollama ↗", installedRetry: "I installed/opened it — check again", originExplanation: "Ollama is running but has not allowed tonytan.me. Run this command, quit Ollama completely, and reopen it.", copy: "Copy", permissionExplanation: "Allow Local network access for tonytan.me in browser site settings, refresh, and check again.", noModelsExplanation: "Ollama is ready but no local model was found.", modelToDownload: "Model to download", downloadModel: "Download model", privacyTitle: "Privacy and requirements", privacyBody: "Prompts, code, model names, and answers go directly between this browser and the selected provider. Local-provider traffic is not relayed through tonytan.me.",
    apiKeyPlaceholder: "Session-only API key", autoDetect: "Auto detect", customRequirementPlaceholder: "Describe a custom system-design requirement…", mentorWorkspacePlaceholder: "Explain your reasoning or ask for one hint…", messagePlaceholder: "Reply with your reasoning…", systemCommandPlaceholder: "Answer this checkpoint or ask JARVIS…",
  },
  zh: {
    skip: "跳到训练工作区", modelService: "模型服务", apiKeyNote: "仅保存在当前标签页，不会发送到 tonytan.me。", hardwareSetup: "硬件检测与模型安装", openOllama: "打开 Ollama", setupGuide: "配置指南", refreshModels: "检查服务并刷新模型", detectedModels: "检测到的模型", manualModel: "手动输入…", autoTune: "根据硬件和模型自动调优", generationSettings: "生成参数", timeoutSeconds: "超时（秒）", contextTokens: "上下文长度", reasoningEffort: "推理强度", maxOutput: "最大输出长度", rolePrompts: "角色提示词（可编辑）", algorithmInterviewer: "算法面试官", systemArchitect: "系统架构师", saveSettings: "保存设置", clearChat: "清空对话", openVscode: "在 VS Code 中打开仓库", localPrivacy: "代码只在你点击运行后执行。本地模型的提示词由浏览器直接发送到你配置的本机端口。",
    checkingModel: "正在检查本地模型…", algorithms: "算法", systemDesign: "系统设计", language: "语言", interfaceLanguage: "界面语言", statementLanguage: "LeetCode 题面", layout: "布局", visiblePanes: "显示面板", problem: "题目", code: "代码", tutor: "导师", tutorStyle: "导师样式", docked: "停靠", floating: "悬浮", restoreLayout: "恢复默认布局", startCoaching: "开始辅导", nextProblem: "JARVIS，下一题", trainingSettings: "训练设置", trainingTrack: "训练方向", difficulty: "难度", progressive: "循序渐进", openLeetcode: "打开 LeetCode", markMastered: "标记已掌握", reviewLater: "稍后复习", visualMap: "视觉地图 · 先看清结构", mentalModel: "当前心智模型", visualMapHelp: "这是可扩展的思考支架，不是完整答案。JARVIS 可以根据你当前的工作重新绘制。", redrawJarvis: "让 JARVIS 重画当前状态", hideProblem: "隐藏题目", roundGoal: "本轮目标：", thinkFirst: "先想清：", statementMissing: "完整题面尚未载入。载入一次后会一直显示在编辑器旁。", loadStatement: "载入完整题面和 Python 模板", changeProblem: "更换题目 / 手动导入", problemReference: "LeetCode URL 或 slug", importWorkspace: "导入工作区", hideCode: "隐藏代码", loadSolution: "载入已有解法", newFile: "（新文件）", load: "载入", upload: "上传", editorLabel: "解法编辑器", editorHelp: "Tab 缩进 · Shift+Tab 反缩进 · 自动缩进 · 行号与语法高亮 · ⌘/Ctrl+F 搜索", testsAndRun: "测试与运行设置", methodName: "Solution 方法", testCases: "测试用例（JSON）", run: "▶ 运行", runAndAsk: "运行并询问导师", saveRepository: "保存到仓库", filename: "文件名", saveCode: "保存当前代码", allowOverwrite: "允许覆盖", mentorRole: "AI 面试副驾驶", watching: "正在观察：", turnRule: "每轮只给一个短提示和一个问题；等你回答后再继续。", sendJarvis: "发送给 JARVIS", stuck: "我卡住了", nextStep: "下一步", allHidden: "三个工作面板都已隐藏，请从“布局”中恢复。",
    startStress: "开始压力测试", assignMission: "JARVIS，分配任务", missionSettings: "任务设置", missionTrack: "任务方向", autoRotation: "自动轮换", scaling: "扩展性", reliability: "可靠性", realtime: "实时系统", dataPlatform: "数据平台", transactions: "事务", currentCheckpoint: "当前检查点：", redrawArchitecture: "让 JARVIS 重画我的架构", customMission: "自定义任务", designRequirement: "设计需求", liveSubtitle: "页面展示本轮；右下角悬浮窗保留完整对话", ready: "就绪", liveEmpty: "回答当前检查点，或点击下方“仅下一步”；进度和新回复会显示在这里。", commandDock: "JARVIS 指令栏 · 完整对话在右下角", send: "发送", nextStepOnly: "仅下一步", replyJarvis: "回复 JARVIS", dockRight: "停靠到右侧", hideTutor: "隐藏导师",
    localModel: "本地模型", connectOllama: "连接 Ollama", continue: "继续进入工作区", retry: "再次检查", launchExplanation: "浏览器可能询问是否打开 Ollama；未经你确认，网站不能启动本机软件。", missingExplanation: "如果 Ollama 没有打开，请安装一次、启动后回到这里；LeetTutor 本身无需安装。", downloadOllama: "下载 Ollama ↗", installedRetry: "已安装/打开——再次检查", originExplanation: "Ollama 正在运行，但尚未允许 tonytan.me。运行此命令，彻底退出 Ollama 后重新打开。", copy: "复制", permissionExplanation: "在浏览器网站设置中允许 tonytan.me 访问本地网络，刷新后再检查。", noModelsExplanation: "Ollama 已就绪，但未发现本地模型。", modelToDownload: "要下载的模型", downloadModel: "下载模型", privacyTitle: "隐私与要求", privacyBody: "提示词、代码、模型名和回答直接在浏览器与所选服务之间传输；本地模型流量不经过 tonytan.me。",
    apiKeyPlaceholder: "仅当前会话使用的 API Key", autoDetect: "自动检测", customRequirementPlaceholder: "描述一个自定义系统设计需求…", mentorWorkspacePlaceholder: "说明你的思路，或只问一个提示…", messagePlaceholder: "回复你的推理过程…", systemCommandPlaceholder: "回答当前检查点，或询问 JARVIS…",
  },
};

const state = {
  catalog: { problems: [], system_design: [], prompts: {} }, locale: "en", statementLocale: "en", theme: "system", mode: "algorithm",
  currentProblem: null, currentSystem: null, language: "python", progress: {}, settings: {}, models: [], runtime: { status: "checking", message: "" },
  lastRun: null, histories: { algorithm: [], system: [] }, layout: { problem: true, code: true, mentor: true, mentorStyle: "floating", mobilePane: "code", problemWidth: 43 },
};

function t(key, vars = {}) {
  let value = copy[state.locale]?.[key] || copy.en[key] || key;
  for (const [name, replacement] of Object.entries(vars)) value = value.replaceAll(`{${name}}`, String(replacement));
  return value;
}

function readJson(key, fallback) { try { return JSON.parse(localStorage.getItem(`${STORAGE}:${key}`) || "null") ?? fallback; } catch (_) { return fallback; } }
function writeJson(key, value) { localStorage.setItem(`${STORAGE}:${key}`, JSON.stringify(value)); }
function escapeHtml(value) { return String(value ?? "").replace(/[&<>"']/g, (ch) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[ch])); }
function localized(item, field) { return state.locale === "zh" ? (item?.[`${field}_cn`] || item?.[field] || "") : (item?.[field] || item?.[`${field}_cn`] || ""); }
function problemCopy(item, field) { return state.locale === "zh" ? (item?.[field] || "") : (item?.[`${field}_en`] || item?.[field] || ""); }
function showToast(message) { const node = $("#toast"); node.textContent = message; node.hidden = false; clearTimeout(showToast.timer); showToast.timer = setTimeout(() => { node.hidden = true; }, 3000); }

function defaultSettings() {
  const provider = "ollama";
  return { provider, endpoint: PROVIDERS[provider].endpoint, model: PROVIDERS[provider].model, autoTune: true, temperature: .2, topP: .9, timeout: 180, context: 8192, reasoning: "none", maxOutput: 768, algorithmPrompt: "", systemPrompt: "" };
}

function loadState() {
  const preferred = readJson("preferences", {});
  const browserChinese = (navigator.languages || [navigator.language]).some((language) => language.toLowerCase().startsWith("zh"));
  state.locale = preferred.locale === "system" || !preferred.locale ? (browserChinese ? "zh" : "en") : preferred.locale;
  state.statementLocale = preferred.statementLocale || (browserChinese ? "zh" : "en");
  state.theme = preferred.theme || "system";
  state.progress = readJson("progress", {});
  state.settings = { ...defaultSettings(), ...readJson("settings", {}) };
  state.layout = { ...state.layout, ...readJson("layout", {}) };
  state.histories = { ...state.histories, ...readJson("histories", {}) };
}

function savePreferences() {
  writeJson("preferences", { locale: $("#language").value, statementLocale: state.statementLocale, theme: state.theme });
}

function applyPreferences() {
  const dark = state.theme === "dark" || (state.theme === "system" && matchMedia("(prefers-color-scheme: dark)").matches);
  document.documentElement.dataset.theme = dark ? "dark" : "light";
  document.documentElement.lang = state.locale === "zh" ? "zh-CN" : "en";
  $("#theme-toggle span").textContent = state.theme === "system" ? "◐" : state.theme === "dark" ? "☾" : "☀";
  $("#theme-toggle").title = state.theme === "system" ? (state.locale === "zh" ? "外观：跟随系统" : "Appearance: system") : `${state.locale === "zh" ? "外观" : "Appearance"}: ${state.theme}`;
  $$('[data-copy]').forEach((node) => { node.textContent = t(node.dataset.copy); });
  $$('[data-copy-title]').forEach((node) => { node.title = t(node.dataset.copyTitle); node.setAttribute("aria-label", t(node.dataset.copyTitle)); });
  $$('[data-placeholder]').forEach((node) => { node.placeholder = t(node.dataset.placeholder); });
  renderAll();
}

async function loadCatalog() {
  const response = await fetch("./catalog.json", { cache: "no-store" });
  if (!response.ok) throw new Error(`Catalog HTTP ${response.status}`);
  state.catalog = await response.json();
  if (!state.settings.algorithmPrompt) state.settings.algorithmPrompt = state.catalog.prompts?.algorithm || "";
  if (!state.settings.systemPrompt) state.settings.systemPrompt = state.catalog.prompts?.system_design || "";
  state.currentProblem = state.catalog.problems.find((item) => item.id === Number(readJson("current-problem", 33))) || state.catalog.problems[0];
  state.currentSystem = state.catalog.system_design.find((item) => item.id === readJson("current-system", "SD-01")) || state.catalog.system_design[0];
  populateSettings();
}

function populateSettings() {
  const settings = state.settings;
  $("#provider-select").value = settings.provider;
  $("#endpoint-input").value = settings.endpoint;
  $("#manual-model").value = settings.model;
  $("#auto-tune").checked = settings.autoTune;
  $("#temperature").value = settings.temperature; $("#temperature-output").value = Number(settings.temperature).toFixed(2);
  $("#top-p").value = settings.topP; $("#top-p-output").value = Number(settings.topP).toFixed(2);
  $("#model-timeout").value = settings.timeout; $("#context-tokens").value = settings.context;
  $("#reasoning-effort").value = settings.reasoning; $("#max-output").value = settings.maxOutput;
  $("#algorithm-prompt").value = settings.algorithmPrompt; $("#system-prompt").value = settings.systemPrompt;
  renderProviderFields();
}

function renderProviderFields() {
  const provider = $("#provider-select").value;
  $("#api-key-field").hidden = !PROVIDERS[provider].key;
  $("#runtime-setup").hidden = provider !== "ollama";
  const hardware = [navigator.hardwareConcurrency ? `${navigator.hardwareConcurrency} CPU threads` : "CPU unknown", navigator.deviceMemory ? `${navigator.deviceMemory} GB browser memory hint` : "browser memory hidden"].join(" · ");
  $("#device-summary").textContent = hardware;
  $("#auto-summary").textContent = $("#auto-tune").checked ? (state.locale === "zh" ? `自动：${$("#context-tokens").value} 上下文，${$("#max-output").value} 输出` : `Auto: ${$("#context-tokens").value} context, ${$("#max-output").value} output`) : "";
}

function saveSettingsFromDrawer() {
  state.settings = {
    provider: $("#provider-select").value, endpoint: $("#endpoint-input").value.replace(/\/$/, ""), model: $("#model-select").value || $("#manual-model").value.trim(),
    autoTune: $("#auto-tune").checked, temperature: Number($("#temperature").value), topP: Number($("#top-p").value), timeout: Number($("#model-timeout").value),
    context: Number($("#context-tokens").value), reasoning: $("#reasoning-effort").value, maxOutput: Number($("#max-output").value), algorithmPrompt: $("#algorithm-prompt").value, systemPrompt: $("#system-prompt").value,
  };
  writeJson("settings", state.settings);
  showToast(state.locale === "zh" ? "模型设置已保存" : "Model settings saved");
  probeProvider();
}

function openDrawer(open = true) {
  $("#settings-drawer").classList.toggle("is-open", open); $("#settings-drawer").setAttribute("aria-hidden", String(!open)); $("#drawer-backdrop").hidden = !open;
}

function providerUrl(path) { return `${state.settings.endpoint.replace(/\/$/, "")}${path}`; }
async function timedFetch(url, options = {}, timeout = 7000) {
  const controller = new AbortController(); const timer = setTimeout(() => controller.abort(), timeout);
  const loopback = /^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?(?:\/|$)/i.test(url);
  try { return await fetch(url, { ...(loopback ? { targetAddressSpace: "loopback" } : {}), ...options, signal: controller.signal }); }
  finally { clearTimeout(timer); }
}

function runtimeCopy(status, detail = "") {
  const zh = state.locale === "zh";
  const table = {
    checking: [zh ? "正在检查模型服务" : "Checking model service", zh ? "正在检测本机端口和可用模型。" : "Detecting the configured endpoint and available models."],
    ready: [zh ? "模型服务已就绪" : "Model service is ready", detail || (zh ? "可以开始 JARVIS 对练。" : "JARVIS coaching is available.")],
    "no-models": [zh ? "服务可用，但没有模型" : "Service ready, no models", zh ? "先下载或载入一个模型。" : "Download or load a model first."],
    origin: [zh ? "本地服务拒绝了网页来源" : "Local service blocked this website", zh ? "需要允许 tonytan.me 访问该本机端口。" : "Allow tonytan.me in the local service origin list."],
    permission: [zh ? "浏览器阻止了本地网络访问" : "Local network permission blocked", zh ? "请在网站权限中允许本地网络。" : "Allow Local network access in site permissions."],
    offline: [zh ? "未连接到模型服务" : "Model service not reached", zh ? "请先启动本地服务；Ollama 缺失时可从配置指南安装。" : "Start the local service first; install Ollama from the guide if missing."],
    error: [zh ? "模型服务出错" : "Model service error", detail],
  };
  return table[status] || table.error;
}

function setRuntime(status, detail = "") {
  state.runtime = { status, message: detail };
  const [title, body] = runtimeCopy(status, detail);
  $("#runtime-label").textContent = status === "ready" ? `${state.settings.provider} · ${state.settings.model} · ${state.settings.provider === "ollama" ? "local only" : "ready"}` : title;
  $("#drawer-runtime-title").textContent = title; $("#drawer-runtime-body").textContent = body;
  $("#runtime-dot").dataset.status = status; $("#drawer-runtime-dot").dataset.status = status;
}

function classifyFetchError(error) {
  const text = `${error?.name || ""} ${error?.message || ""}`.toLowerCase();
  if (text.includes("private network") || text.includes("local network") || text.includes("permission")) return "permission";
  if (text.includes("cors") || text.includes("origin")) return "origin";
  return "offline";
}

async function probeProvider({ openGuide = false } = {}) {
  saveSettingsSilently(); setRuntime("checking");
  try {
    const provider = state.settings.provider;
    let models = [];
    if (provider === "ollama") {
      const response = await timedFetch(providerUrl("/api/tags"));
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const payload = await response.json(); models = (payload.models || []).map((item) => item.name || item.model).filter(Boolean);
    } else if (provider === "gemini") {
      const key = $("#api-key-input").value.trim(); if (!key) throw new Error("API key required");
      const response = await timedFetch(`${providerUrl("/v1beta/models")}?key=${encodeURIComponent(key)}`);
      if (!response.ok) throw new Error(`HTTP ${response.status}`); const payload = await response.json(); models = (payload.models || []).filter((item) => item.supportedGenerationMethods?.includes("generateContent")).map((item) => item.name.replace("models/", ""));
    } else {
      const headers = {}; if (PROVIDERS[provider].key) { const key = $("#api-key-input").value.trim(); if (!key) throw new Error("API key required"); headers.Authorization = `Bearer ${key}`; }
      const response = await timedFetch(providerUrl("/v1/models"), { headers }); if (!response.ok) throw new Error(`HTTP ${response.status}`); const payload = await response.json(); models = (payload.data || []).map((item) => item.id).filter(Boolean);
    }
    state.models = models; populateModelSelect();
    if (models.length && !models.includes(state.settings.model)) state.settings.model = models[0];
    setRuntime(models.length ? "ready" : "no-models", models.length ? `${models.length} model(s) detected` : "");
    if (openGuide && state.settings.provider === "ollama") showConnectionDialog(models.length ? "ready" : "no-models");
  } catch (error) {
    let status = classifyFetchError(error);
    if (state.settings.provider === "ollama" && status === "offline") {
      try {
        await timedFetch(providerUrl("/api/tags"), { mode: "no-cors" }, 2500);
        status = "origin";
      } catch (diagnosticError) {
        status = classifyFetchError(diagnosticError);
      }
    }
    setRuntime(status, error.message);
    if (openGuide && state.settings.provider === "ollama") showConnectionDialog(status);
  }
}

function saveSettingsSilently() {
  state.settings.provider = $("#provider-select").value; state.settings.endpoint = $("#endpoint-input").value.replace(/\/$/, ""); state.settings.model = $("#model-select").value || $("#manual-model").value.trim() || PROVIDERS[state.settings.provider].model;
}

function populateModelSelect() {
  const select = $("#model-select"); select.replaceChildren();
  const manual = document.createElement("option"); manual.value = ""; manual.textContent = t("manualModel"); select.append(manual);
  state.models.forEach((model) => { const option = document.createElement("option"); option.value = model; option.textContent = model; select.append(option); });
  if (state.models.includes(state.settings.model)) select.value = state.settings.model;
  $("#manual-model-field").hidden = Boolean(select.value);
}

function showConnectionDialog(status = state.runtime.status) {
  const dialog = $("#connection-dialog");
  ["ready", "launch", "install", "origin", "permission", "model"].forEach((name) => { const node = $(`#${name}-actions`); if (node) node.hidden = true; });
  const mapping = { ready: "ready", "no-models": "model", origin: "origin", permission: "permission", offline: "launch", error: "launch", checking: "launch" };
  const section = mapping[status] || "launch"; $(`#${section}-actions`).hidden = false;
  const [title, body] = runtimeCopy(status); $("#diagnostic-title").textContent = title; $("#diagnostic-body").textContent = body; $("#diagnostic-icon").dataset.status = status === "ready" ? "ready" : ["offline", "origin", "permission", "error"].includes(status) ? "error" : "checking";
  if (!dialog.open) dialog.showModal();
}

async function openAndWaitForOllama() {
  const link = document.createElement("a");
  link.href = "ollama://";
  link.hidden = true;
  document.body.append(link);
  link.click();
  link.remove();
  setRuntime("checking");
  for (let attempt = 0; attempt < 14; attempt += 1) { await new Promise((resolve) => setTimeout(resolve, attempt < 4 ? 800 : 1300)); await probeProvider(); if (["ready", "no-models", "origin", "permission"].includes(state.runtime.status)) { showConnectionDialog(); return; } }
  showConnectionDialog("offline"); $("#launch-actions").hidden = true; $("#install-actions").hidden = false;
}

function platformOriginCommand() {
  const platform = (navigator.userAgentData?.platform || navigator.platform || "").toLowerCase();
  if (platform.includes("win")) return "[Environment]::SetEnvironmentVariable('OLLAMA_ORIGINS','https://tonytan.me','User')";
  if (platform.includes("linux")) return "OLLAMA_ORIGINS=https://tonytan.me ollama serve";
  return 'launchctl setenv OLLAMA_ORIGINS "https://tonytan.me"';
}

async function pullModel() {
  const name = $("#pull-model-name").value.trim() || RECOMMENDED_MODEL; const progress = $("#pull-progress"); const status = $("#pull-status");
  progress.hidden = false; progress.removeAttribute("value"); status.textContent = state.locale === "zh" ? "正在开始下载…" : "Starting download…";
  try {
    const response = await timedFetch(providerUrl("/api/pull"), { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ model: name, stream: true }) }, 30 * 60 * 1000);
    if (!response.ok || !response.body) throw new Error(`HTTP ${response.status}`);
    const reader = response.body.getReader(); const decoder = new TextDecoder(); let buffer = "";
    while (true) { const { value, done } = await reader.read(); if (done) break; buffer += decoder.decode(value, { stream: true }); const lines = buffer.split("\n"); buffer = lines.pop() || ""; for (const line of lines) { if (!line.trim()) continue; const update = JSON.parse(line); if (update.total) { progress.value = Math.round(((update.completed || 0) / update.total) * 100); status.textContent = `${update.status || "Downloading"} ${progress.value}%`; } else status.textContent = update.status || status.textContent; if (update.error) throw new Error(update.error); } }
    state.settings.model = name; $("#manual-model").value = name; await probeProvider({ openGuide: true });
  } catch (error) { progress.hidden = true; status.textContent = error.message; }
}

function progressSummary() {
  const values = Object.values(state.progress); return { mastered: values.filter((x) => x.status === "mastered").length, review: values.filter((x) => x.status === "review").length, attempted: values.filter((x) => Number(x.attempts) > 0).length, total: state.catalog.problems.length };
}

function chooseNextProblem(excludeId = state.currentProblem?.id) {
  const track = $("#topic-select").value || "auto"; const difficulty = $("#difficulty-select").value || "progressive";
  let candidates = state.catalog.problems.filter((item) => item.id !== excludeId);
  if (track !== "auto") candidates = candidates.filter((item) => item.topic === track);
  if (["Easy", "Medium", "Hard"].includes(difficulty)) candidates = candidates.filter((item) => item.difficulty === difficulty);
  if (!candidates.length) return state.currentProblem;
  const topicScores = Object.fromEntries([...new Set(state.catalog.problems.map((item) => item.topic))].map((topic) => { const items = state.catalog.problems.filter((item) => item.topic === topic); return [topic, items.filter((item) => state.progress[item.id]?.status === "mastered").length / items.length]; }));
  const rank = { review: 0, new: 1, in_progress: 2, mastered: 3 };
  return candidates.sort((a, b) => {
    const pa = state.progress[a.id] || {}, pb = state.progress[b.id] || {};
    const sa = [track === "auto" ? topicScores[a.topic] : 0, rank[pa.status || "new"], pa.attempts || 0, state.catalog.problems.indexOf(a)];
    const sb = [track === "auto" ? topicScores[b.topic] : 0, rank[pb.status || "new"], pb.attempts || 0, state.catalog.problems.indexOf(b)];
    for (let index = 0; index < sa.length; index += 1) if (sa[index] !== sb[index]) return sa[index] - sb[index];
    return 0;
  })[0];
}

function updateProgress(status) {
  const id = state.currentProblem.id; const current = state.progress[id] || { attempts: 0 };
  state.progress[id] = { ...current, status, attempts: current.attempts + (status === "in_progress" ? 1 : 0), topic: state.currentProblem.topic, updatedAt: new Date().toISOString() };
  writeJson("progress", state.progress); renderAlgorithm();
}

function defaultSource(problem, language = state.language) {
  if (language === "java") return `class Solution {\n    public int solve(int[] nums) {\n        // Define the invariant first.\n        return 0;\n    }\n}\n`;
  if (problem?.id === 704) return "class Solution:\n    def search(self, nums, target):\n        left, right = 0, len(nums) - 1\n        while left <= right:\n            mid = left + (right - left) // 2\n            if nums[mid] == target:\n                return mid\n            if nums[mid] < target:\n                left = mid + 1\n            else:\n                right = mid - 1\n        return -1\n";
  return "class Solution:\n    def solve(self, *args):\n        # Define the invariant, then implement the solution.\n        pass\n";
}

function defaultTests(problem) {
  if (problem?.id === 704) return '[\n  {"args":[[-1,0,3,5,9,12],9],"expected":4},\n  {"args":[[-1,0,3,5,9,12],2],"expected":-1}\n]';
  return '[\n  {"args":[],"expected":null}\n]';
}
function draftKey() { return `draft:${state.currentProblem?.id}:${state.language}`; }

function populateTrackSelect() {
  const select = $("#topic-select"); const prior = select.value || "auto"; select.replaceChildren();
  const auto = document.createElement("option"); auto.value = "auto"; auto.textContent = state.locale === "zh" ? "自动补弱" : "Auto · weakest track"; select.append(auto);
  const topicEnglish = { "二分": "Binary search", "栈": "Stack", "优先队列": "Priority queue", "DP": "Dynamic programming" };
  [...new Set(state.catalog.problems.map((item) => item.topic))].forEach((topic) => { const option = document.createElement("option"); option.value = topic; option.textContent = state.locale === "zh" ? topic : (topicEnglish[topic] || topic); select.append(option); });
  select.value = [...select.options].some((option) => option.value === prior) ? prior : "auto";
}

function renderAlgorithm(resetEditor = false) {
  const item = state.currentProblem; if (!item) return;
  populateTrackSelect(); const summary = progressSummary(); const entry = state.progress[item.id] || { attempts: 0, status: "new" };
  $("#algorithm-session-title").textContent = `${state.locale === "zh" ? "导师训练" : "Tutor session"} · ${item.id}. ${localized(item, "title")} · ${item.difficulty}`;
  $("#algorithm-session-summary").textContent = state.locale === "zh" ? `已掌握 ${summary.mastered}/${summary.total} · 待复习 ${summary.review} · 已练习 ${summary.attempted} · 当前第 ${Math.max(1, entry.attempts)} 次` : `Mastered ${summary.mastered}/${summary.total} · Review ${summary.review} · Attempted ${summary.attempted} · Attempt ${Math.max(1, entry.attempts)}`;
  $("#problem-title").textContent = `${item.id}. ${localized(item, "title")} · ${item.difficulty}`;
  $("#problem-focus").textContent = problemCopy(item, "focus"); $("#objective").textContent = problemCopy(item, "invariant_prompt"); $("#problem-link").href = item.url;
  $("#algorithm-map-focus").textContent = problemCopy(item, "focus"); renderAlgorithmDiagram(item);
  $("#method-name").value = $("#method-name").value || (item.id === 704 ? "search" : "solve");
  $("#save-filename").value = `${item.id}.${item.slug}.${state.language === "python" ? "py" : "java"}`;
  if (resetEditor || !$("#code-editor").value) { $("#code-editor").value = localStorage.getItem(`${STORAGE}:${draftKey()}`) || defaultSource(item); $("#test-cases").value = defaultTests(item); updateEditor(); }
  $("#mentor-context").textContent = `${item.id}. ${localized(item, "title")} · ${item.difficulty}`; $("#docked-watching").textContent = $("#mentor-context").textContent;
  renderSavedSolutions(); renderMessages(); updateLayout();
}

function renderAlgorithmDiagram(item) {
  const patterns = {
    "二分": ["Search range", "mid", "Keep one half", "Converge"], "栈": ["Input", "Stack invariant", "Resolve top", "Answer"],
    "优先队列": ["Stream", "Heap boundary", "Evict", "Top-K"], "DP": ["State", "Choice", "Transition", "Base case"],
  };
  renderFlow($("#algorithm-diagram"), patterns[item.topic] || ["Input", "Invariant", "Decision", "Output"]);
}

function renderFlow(root, labels) {
  root.replaceChildren(); labels.forEach((label, index) => { const node = document.createElement("span"); node.className = "diagram-node"; node.textContent = label; root.append(node); if (index < labels.length - 1) { const arrow = document.createElement("i"); arrow.textContent = "→"; root.append(arrow); } });
}

function chooseNextSystem() {
  let candidates = state.catalog.system_design; const track = $("#system-track").value; const difficulty = $("#system-difficulty").value;
  if (track !== "auto") candidates = candidates.filter((item) => item.track === track); if (["Easy", "Medium", "Hard"].includes(difficulty)) candidates = candidates.filter((item) => item.difficulty === difficulty);
  if (!candidates.length) return state.currentSystem; const index = candidates.findIndex((item) => item.id === state.currentSystem?.id); return candidates[(index + 1) % candidates.length];
}

function renderSystem() {
  const item = state.currentSystem; if (!item) return;
  $("#system-mission-title").textContent = state.locale === "zh" ? "系统设计面试训练" : "System design interview training";
  $("#system-mission-summary").textContent = `${item.id} · ${localized(item, "title")} · ${item.difficulty}`;
  $("#system-case-title").textContent = `${item.id} · ${localized(item, "title")} · ${item.difficulty}`; $("#system-requirement").textContent = localized(item, "requirement"); $("#system-checkpoint").textContent = localized(item, "first_question"); $("#system-map-focus").textContent = localized(item, "focus");
  const diagram = { scaling: ["Client", "Edge", "Service", "Cache / DB"], reliability: ["Client", "Limiter", "Counter", "Fallback"], realtime: ["Client", "Gateway", "Message log", "Recipients"], data: ["Producers", "Ingest", "Storage", "Query"], transactions: ["Client", "API", "Ledger", "Reconcile"] };
  renderFlow($("#system-diagram"), diagram[item.track] || ["Client", "API", "Data", "Failure boundary"]);
  $("#mentor-context").textContent = `${item.id} · ${localized(item, "title")} · ${item.difficulty}`; renderMessages();
}

function renderAll() { if (!state.catalog.problems.length) return; renderAlgorithm(); renderSystem(); renderMode(); renderLayoutControls(); }

function renderMode() {
  $("#algorithm-mode").hidden = state.mode !== "algorithm"; $("#system-mode").hidden = state.mode !== "system";
  $$('.mode-switch [data-mode]').forEach((button) => button.classList.toggle("is-active", button.dataset.mode === state.mode));
  $("#dock-mentor").hidden = state.mode === "system"; $("#layout-popover").hidden = state.mode === "system";
  $("#mentor-context").textContent = state.mode === "algorithm" ? `${state.currentProblem.id}. ${localized(state.currentProblem, "title")}` : `${state.currentSystem.id} · ${localized(state.currentSystem, "title")}`;
  renderMessages();
}

function renderLayoutControls() {
  $("#show-problem").checked = state.layout.problem; $("#show-code").checked = state.layout.code; $("#show-mentor").checked = state.layout.mentor;
  const radio = $(`input[name="mentor-style"][value="${state.layout.mentorStyle}"]`); if (radio) radio.checked = true;
  document.documentElement.style.setProperty("--problem-width", `${state.layout.problemWidth}%`);
}

function updateLayout() {
  const docked = state.layout.mentor && state.layout.mentorStyle === "docked";
  $("#problem-pane").hidden = !state.layout.problem || (innerWidth <= 760 && state.layout.mobilePane !== "problem");
  $("#code-pane").hidden = !state.layout.code || (innerWidth <= 760 && state.layout.mobilePane !== "code");
  $("#docked-mentor").hidden = !docked || (innerWidth <= 760 && state.layout.mobilePane !== "jarvis");
  $("#splitter").hidden = !state.layout.problem || !state.layout.code || innerWidth <= 760;
  $("#jarvis-launcher").hidden = !state.layout.mentor || docked;
  if (!state.layout.mentor) $("#jarvis-panel").hidden = true;
  $("#all-panes-hidden").hidden = state.layout.problem || state.layout.code || state.layout.mentor;
  $("#algorithm-workspace").dataset.columns = [state.layout.problem ? "problem" : "", state.layout.code ? "code" : "", docked ? "mentor" : ""].filter(Boolean).join("-");
  $$('#mobile-nav [data-mobile-pane]').forEach((button) => button.classList.toggle("is-active", button.dataset.mobilePane === state.layout.mobilePane));
  writeJson("layout", state.layout);
}

function switchMode(mode) { state.mode = mode; renderMode(); if (innerWidth <= 760 && mode === "algorithm") updateLayout(); }

function openingForProblem() {
  const item = state.currentProblem; const attempts = state.progress[item.id]?.attempts || 1;
  return state.locale === "zh" ? `提示：这道题用来训练“${problemCopy(item, "focus")}”。当前是${attempts <= 1 ? "第一次接触" : `第 ${attempts} 次练习`}，我们一次只过一关，先不写代码。\n\n轮到你：用自己的话说清 LeetCode ${item.id}（${item.title_cn}）的输入、输出，并给一个最小样例，好吗？` : `Hint: This problem trains one core invariant: ${problemCopy(item, "focus")}. We will clear one checkpoint at a time and hold off on code.\n\nYour turn: In your own words, what are the input and output of LeetCode ${item.id} (${item.title})? Give one minimal example too.`;
}

function beginCoaching() { updateProgress("in_progress"); if (!state.histories.algorithm.length) appendMessage("assistant", openingForProblem(), "algorithm"); openMentor(); }
function assignNextProblem() { state.currentProblem = chooseNextProblem(); writeJson("current-problem", state.currentProblem.id); $("#method-name").value = ""; $("#code-editor").value = ""; state.histories.algorithm = []; writeJson("histories", state.histories); renderAlgorithm(true); beginCoaching(); }

function beginSystemStress() {
  $("#system-brief").hidden = false; const item = state.currentSystem; const opening = state.locale === "zh" ? `我们先锁定需求边界，不选组件。\n\n轮到你：${item.first_question_cn}` : `We will lock the requirement boundary before choosing components.\n\nYour turn: ${item.first_question}`;
  if (!state.histories.system.length) appendMessage("assistant", opening, "system"); $("#system-latest-question").hidden = false; $("#system-latest-question").textContent = localized(item, "first_question"); $("#system-live-content").textContent = opening; openMentor();
}

function assignNextSystem() { state.currentSystem = chooseNextSystem(); writeJson("current-system", state.currentSystem.id); state.histories.system = []; writeJson("histories", state.histories); $("#system-brief").hidden = false; renderSystem(); beginSystemStress(); }

function editorHighlight(code) {
  let html = escapeHtml(code); const placeholders = [];
  html = html.replace(/(&quot;{3}[\s\S]*?&quot;{3}|&#39;{3}[\s\S]*?&#39;{3}|&quot;(?:\\.|[^"\\])*&quot;|&#39;(?:\\.|[^'\\])*&#39;|#[^\n]*)/g, (match) => {
    const tokenClass = match.startsWith("#") ? "tok-comment" : "tok-string";
    const index = placeholders.push(`<span class="${tokenClass}">${match}</span>`) - 1;
    return `\uE000${String.fromCharCode(0xE100 + index)}\uE001`;
  });
  html = html.replace(/\b(class|def|return|if|else|elif|for|while|in|import|from|as|try|except|finally|with|lambda|pass|break|continue|True|False|None|public|private|static|void|int|new)\b/g, '<span class="tok-keyword">$1</span>').replace(/\b(\d+(?:\.\d+)?)\b/g, '<span class="tok-number">$1</span>');
  return html.replace(/\uE000([\uE100-\uE1FF])\uE001/g, (_, marker) => placeholders[marker.charCodeAt(0) - 0xE100]);
}

function updateEditor() {
  const field = $("#code-editor"); const lines = field.value.split("\n").length; $("#editor-lines").textContent = Array.from({ length: lines }, (_, index) => index + 1).join("\n"); $("#editor-highlight").innerHTML = `${editorHighlight(field.value)}\n`;
  localStorage.setItem(`${STORAGE}:${draftKey()}`, field.value); syncEditorScroll();
}
function syncEditorScroll() { $("#editor-highlight").scrollTop = $("#code-editor").scrollTop; $("#editor-highlight").scrollLeft = $("#code-editor").scrollLeft; $("#editor-lines").scrollTop = $("#code-editor").scrollTop; }

let pythonWorker = null; let pythonReady = null; const pendingRuns = new Map();
function resetPythonWorker() { pythonWorker?.terminate(); pythonWorker = null; pythonReady = null; pendingRuns.clear(); }
function ensurePythonWorker() {
  if (pythonReady) return pythonReady;
  pythonReady = new Promise((resolve, reject) => { pythonWorker = new Worker("./python-worker.js"); pythonWorker.addEventListener("message", (event) => { if (event.data.type === "ready") return resolve(); if (event.data.type === "init-error") { resetPythonWorker(); return reject(new Error(event.data.error)); } const pending = pendingRuns.get(event.data.id); if (!pending) return; clearTimeout(pending.timer); pendingRuns.delete(event.data.id); event.data.type === "result" ? pending.resolve(event.data.result) : pending.reject(new Error(event.data.error)); }); pythonWorker.addEventListener("error", (event) => { resetPythonWorker(); reject(new Error(event.message)); }); });
  return pythonReady;
}

async function executePython() {
  const output = $("#run-result"); output.hidden = false;
  if (state.language !== "python") { output.textContent = state.locale === "zh" ? "浏览器运行器目前支持 Python；Java 仍可编辑和保存。" : "The browser runner currently supports Python; Java can still be edited and saved."; return null; }
  output.textContent = state.locale === "zh" ? "正在加载安全的浏览器 Python 运行器…" : "Loading the isolated browser Python runner…";
  try {
    await ensurePythonWorker(); const id = crypto.randomUUID(); const timeout = Number($("#run-timeout").value || 3) * 1000; output.textContent = state.locale === "zh" ? "正在运行测试…" : "Running tests…";
    const result = await new Promise((resolve, reject) => { const timer = setTimeout(() => { pendingRuns.delete(id); resetPythonWorker(); reject(new Error(state.locale === "zh" ? "运行超时，已重置隔离运行器。" : "Run timed out; isolated runner reset.")); }, timeout); pendingRuns.set(id, { resolve, reject, timer }); pythonWorker.postMessage({ id, source: $("#code-editor").value, methodName: $("#method-name").value, testCases: $("#test-cases").value }); });
    state.lastRun = result; output.textContent = [`${result.passed === result.total ? "✓" : "✗"} ${state.locale === "zh" ? "通过" : "passed"} ${result.passed}/${result.total}`, ...(result.cases || []).map((item, index) => `#${index + 1} ${item.passed ? "✓" : "✗"} actual=${JSON.stringify(item.actual)} expected=${JSON.stringify(item.expected)}`), result.stdout ? `stdout:\n${result.stdout}` : "", result.error || ""].filter(Boolean).join("\n");
    if (result.total) updateProgress(result.passed === result.total ? "mastered" : "review"); return result;
  } catch (error) { output.textContent = `${state.locale === "zh" ? "运行失败" : "Run failed"}: ${error.message}`; return null; }
}

function savedSolutions() { return readJson("solutions", []); }
function renderSavedSolutions() { const select = $("#saved-solution-select"); const current = select.value; select.replaceChildren(); const blank = document.createElement("option"); blank.value = ""; blank.textContent = t("newFile"); select.append(blank); savedSolutions().forEach((item, index) => { const option = document.createElement("option"); option.value = String(index); option.textContent = `${item.filename} · ${new Date(item.savedAt).toLocaleDateString()}`; select.append(option); }); select.value = [...select.options].some((option) => option.value === current) ? current : ""; }
async function saveSolution() {
  const filename = $("#save-filename").value.trim() || `${state.currentProblem.id}.${state.language === "python" ? "py" : "java"}`; const solutions = savedSolutions(); const existing = solutions.findIndex((item) => item.filename === filename);
  if (existing >= 0 && !$("#allow-overwrite").checked) { showToast(state.locale === "zh" ? "文件已存在；勾选允许覆盖后再保存。" : "File exists; enable overwrite to save."); return; }
  const record = { filename, source: $("#code-editor").value, language: state.language, problemId: state.currentProblem.id, savedAt: new Date().toISOString() }; if (existing >= 0) solutions[existing] = record; else solutions.unshift(record); writeJson("solutions", solutions.slice(0, 40)); renderSavedSolutions();
  try {
    if (window.showSaveFilePicker) { const handle = await window.showSaveFilePicker({ suggestedName: filename, types: [{ description: "Source code", accept: { "text/plain": [`.${filename.split(".").pop()}`] } }] }); const writable = await handle.createWritable(); await writable.write(record.source); await writable.close(); }
    else { const link = document.createElement("a"); link.href = URL.createObjectURL(new Blob([record.source], { type: "text/plain" })); link.download = filename; link.click(); setTimeout(() => URL.revokeObjectURL(link.href), 1000); }
    showToast(state.locale === "zh" ? "已保存到浏览器并导出文件" : "Saved in browser and exported");
  } catch (error) { if (error.name !== "AbortError") showToast(error.message); else showToast(state.locale === "zh" ? "代码已保存在浏览器中" : "Code saved in browser"); }
}

async function loadStatement() {
  const item = state.currentProblem; const button = $("#load-statement"); button.disabled = true; button.textContent = state.locale === "zh" ? "正在从 LeetCode 载入…" : "Loading from LeetCode…";
  try {
    const host = state.statementLocale === "zh" ? "https://leetcode.cn/graphql/" : "https://leetcode.com/graphql/";
    const query = `query questionData($titleSlug: String!) { question(titleSlug: $titleSlug) { content translatedContent codeSnippets { lang langSlug code } } }`;
    const response = await timedFetch(host, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ query, variables: { titleSlug: item.slug } }) }, 12000);
    if (!response.ok) throw new Error(`HTTP ${response.status}`); const question = (await response.json()).data?.question; if (!question) throw new Error("No statement returned");
    const raw = state.statementLocale === "zh" ? (question.translatedContent || question.content) : question.content; const parsed = new DOMParser().parseFromString(raw || "", "text/html"); parsed.querySelectorAll("script,style,iframe,object,embed,form").forEach((node) => node.remove()); parsed.querySelectorAll("*").forEach((node) => [...node.attributes].forEach((attribute) => { if (attribute.name.startsWith("on") || ["style", "srcset"].includes(attribute.name)) node.removeAttribute(attribute.name); if (["href", "src"].includes(attribute.name) && !/^(https?:|data:image\/)/i.test(attribute.value)) node.removeAttribute(attribute.name); }));
    $("#problem-statement").replaceChildren(...parsed.body.childNodes); $("#problem-statement").hidden = false; $("#statement-status").hidden = true;
    const snippet = question.codeSnippets?.find((entry) => entry.langSlug === state.language); if (snippet?.code && !localStorage.getItem(`${STORAGE}:${draftKey()}`)) { $("#code-editor").value = snippet.code; updateEditor(); }
    button.hidden = true;
  } catch (error) {
    $("#statement-status").hidden = false; $("#statement-status").innerHTML = `${escapeHtml(state.locale === "zh" ? "浏览器被 LeetCode 的跨域策略阻止，无法直接导入题面。" : "LeetCode's cross-origin policy blocked direct import in this browser.")} <a href="${escapeHtml(item.url)}" target="_blank" rel="noopener noreferrer">${escapeHtml(t("openLeetcode"))} ↗</a>`;
    showToast(error.message);
  } finally { button.disabled = false; button.textContent = t("loadStatement"); }
}

function importProblem() {
  const value = $("#problem-reference").value.trim(); const slug = value.replace(/\?.*$/, "").replace(/\/$/, "").split("/").pop(); const item = state.catalog.problems.find((problem) => problem.slug === slug || String(problem.id) === value);
  if (!item) { showToast(state.locale === "zh" ? "课程目录中没有匹配的题目。" : "No matching problem in the curriculum."); return; }
  state.currentProblem = item; writeJson("current-problem", item.id); $("#code-editor").value = ""; renderAlgorithm(true); $("#statement-status").hidden = false; $("#problem-statement").hidden = true; $("#load-statement").hidden = false;
}

function renderRichMessage(root, text) {
  root.replaceChildren(); const pieces = String(text).split(/```mermaid\s*([\s\S]*?)```/i);
  pieces.forEach((piece, index) => { if (index % 2 === 0) { if (!piece.trim()) return; const p = document.createElement("p"); p.textContent = piece.trim(); root.append(p); } else { const visual = document.createElement("div"); visual.className = "mermaid-lite"; const edges = [...piece.matchAll(/([\w\u4e00-\u9fff]+)(?:\[[^\]]*\])?\s*--+>?\s*([\w\u4e00-\u9fff]+)(?:\[[^\]]*\])?/g)].map((match) => [match[1], match[2]]); const labels = edges.length ? [...new Set(edges.flat())] : piece.split("\n").filter(Boolean).slice(0, 8); renderFlow(visual, labels); root.append(visual); } });
}

function renderMessages() {
  const history = state.histories[state.mode] || []; [$("#messages"), $("#docked-messages")].forEach((root) => { root.replaceChildren(); history.forEach((entry) => { const article = document.createElement("article"); article.className = `message ${entry.role}`; const badge = document.createElement("span"); badge.textContent = entry.role === "assistant" ? "J" : "YOU"; const body = document.createElement("div"); body.className = "message-body"; renderRichMessage(body, entry.content); article.append(badge, body); root.append(article); }); root.scrollTop = root.scrollHeight; });
  if (state.mode === "system" && history.length) { const latest = [...history].reverse().find((entry) => entry.role === "assistant"); if (latest) renderRichMessage($("#system-live-content"), latest.content); }
}

function appendMessage(role, content, mode = state.mode) { state.histories[mode].push({ role, content, createdAt: Date.now() }); writeJson("histories", state.histories); renderMessages(); }

function workspaceRequest(question, trigger = "User request") {
  if (state.mode === "system") {
    const requirement = $("#custom-requirement").value.trim() || localized(state.currentSystem, "requirement"); return `This is an interactive system-design interview.\nRequirement: ${requirement}\nCurrent checkpoint: ${localized(state.currentSystem, "first_question")}\nTrigger: ${trigger}\nStudent: ${question}\nAdvance exactly one decision, include one small fenced Mermaid diagram, then ask exactly one question.`;
  }
  return `This is a coaching turn grounded in the current browser workspace. Do not provide a full replacement solution unless the student explicitly asks for 求最优解代码.\nTrigger: ${trigger}\nQuestion: ${question}\nProblem: ${state.currentProblem.id}. ${localized(state.currentProblem, "title")}\nFocus: ${problemCopy(state.currentProblem, "focus")}\nLanguage: ${state.language}\nCurrent code:\n\`\`\`${state.language}\n${$("#code-editor").value.slice(0, 12000)}\n\`\`\`\nTests: ${$("#test-cases").value.slice(0, 4000)}\nLast run: ${JSON.stringify(state.lastRun)}\nGive one short hint and exactly one question.`;
}

async function callModel(messages) {
  const settings = state.settings; const provider = settings.provider; const model = settings.model;
  if (provider === "ollama") {
    const response = await timedFetch(providerUrl("/api/chat"), { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ model, stream: false, think: settings.reasoning !== "none", options: { temperature: settings.temperature, top_p: settings.topP, num_ctx: settings.context, num_predict: settings.maxOutput }, messages }) }, settings.timeout * 1000);
    if (!response.ok) throw new Error(`HTTP ${response.status}`); return (await response.json()).message?.content || "";
  }
  if (provider === "gemini") {
    const key = $("#api-key-input").value.trim(); const system = messages.find((entry) => entry.role === "system")?.content || ""; const contents = messages.filter((entry) => entry.role !== "system").map((entry) => ({ role: entry.role === "assistant" ? "model" : "user", parts: [{ text: entry.content }] }));
    const response = await timedFetch(`${providerUrl(`/v1beta/models/${encodeURIComponent(model)}:generateContent`)}?key=${encodeURIComponent(key)}`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ systemInstruction: { parts: [{ text: system }] }, contents, generationConfig: { temperature: settings.temperature, topP: settings.topP, maxOutputTokens: settings.maxOutput } }) }, settings.timeout * 1000); if (!response.ok) throw new Error(`HTTP ${response.status}`); return (await response.json()).candidates?.[0]?.content?.parts?.map((part) => part.text).join("") || "";
  }
  const headers = { "Content-Type": "application/json" }; if (PROVIDERS[provider].key) headers.Authorization = `Bearer ${$("#api-key-input").value.trim()}`;
  const response = await timedFetch(providerUrl("/v1/chat/completions"), { method: "POST", headers, body: JSON.stringify({ model, messages, temperature: settings.temperature, top_p: settings.topP, max_tokens: settings.maxOutput }) }, settings.timeout * 1000); if (!response.ok) throw new Error(`HTTP ${response.status}`); return (await response.json()).choices?.[0]?.message?.content || "";
}

async function askJarvis(question, trigger = "User request") {
  const text = question.trim(); if (!text) { showToast(state.locale === "zh" ? "先写下你的思路或问题。" : "Write your reasoning or question first."); return; }
  if (state.runtime.status !== "ready") { appendMessage("assistant", state.locale === "zh" ? "本地模型尚未连接。代码仍可在浏览器运行；JARVIS 需要先在左上角模型中心连接服务。" : "The model is not connected. Browser code practice still works; connect a model from the LT model center for JARVIS."); if (state.settings.provider === "ollama") showConnectionDialog(); else openDrawer(true); return; }
  appendMessage("user", text); const pending = { role: "assistant", content: state.locale === "zh" ? "JARVIS 正在分析当前现场…" : "JARVIS is analyzing the current workspace…", createdAt: Date.now() }; state.histories[state.mode].push(pending); renderMessages();
  try {
    const system = state.mode === "algorithm" ? state.settings.algorithmPrompt : state.settings.systemPrompt; const prior = state.histories[state.mode].slice(-12, -1).map((entry) => ({ role: entry.role, content: entry.content })); const answer = await callModel([{ role: "system", content: `${system}\nUse ${state.locale === "zh" ? "Chinese" : "English"} for this turn.` }, ...prior, { role: "user", content: workspaceRequest(text, trigger) }]); pending.content = answer || (state.locale === "zh" ? "模型返回了空响应。" : "The model returned an empty response."); setRuntime("ready");
  } catch (error) { pending.content = `${state.locale === "zh" ? "模型请求失败" : "Model request failed"}: ${error.message}`; setRuntime("error", error.message); }
  writeJson("histories", state.histories); renderMessages();
}

function openMentor() { if (state.layout.mentorStyle === "docked" && state.mode === "algorithm") { state.layout.mentor = true; updateLayout(); return; } $("#jarvis-panel").hidden = false; $("#jarvis-launcher").setAttribute("aria-expanded", "true"); }
function quickAction(action) { if (action === "hide") { state.layout.mentor = false; updateLayout(); return; } const prompts = { stuck: state.locale === "zh" ? "我卡住了。只给我一个最小提示，再问一个问题。" : "I'm stuck. Give one minimal hint, then ask one question.", next: state.locale === "zh" ? "仅推进下一步，不要复述前文。" : "Advance only the next step without repeating prior context.", review: state.locale === "zh" ? "Review 当前工作，只指出最关键的一个问题。" : "Review the current work and identify only the single most important issue." }; askJarvis(prompts[action] || action, action); }

function setupSplitter() {
  const splitter = $("#splitter"); splitter.addEventListener("pointerdown", (event) => { splitter.setPointerCapture(event.pointerId); document.body.classList.add("is-resizing"); const move = (moveEvent) => { const bounds = $("#algorithm-workspace").getBoundingClientRect(); state.layout.problemWidth = Math.min(67, Math.max(28, ((moveEvent.clientX - bounds.left) / bounds.width) * 100)); document.documentElement.style.setProperty("--problem-width", `${state.layout.problemWidth}%`); }; const stop = () => { document.body.classList.remove("is-resizing"); writeJson("layout", state.layout); splitter.removeEventListener("pointermove", move); splitter.removeEventListener("pointerup", stop); }; splitter.addEventListener("pointermove", move); splitter.addEventListener("pointerup", stop); });
}

function setupMentorDrag() {
  const panel = $("#jarvis-panel"), handle = $("#mentor-drag-handle"); const stored = readJson("mentor-position", null); if (stored && innerWidth > 760) Object.assign(panel.style, { left: `${stored.left}px`, top: `${stored.top}px`, right: "auto", bottom: "auto" });
  handle.addEventListener("pointerdown", (event) => { if (event.target.closest("button") || innerWidth <= 760) return; const rect = panel.getBoundingClientRect(), offsetX = event.clientX - rect.left, offsetY = event.clientY - rect.top; handle.setPointerCapture(event.pointerId); const move = (moveEvent) => { panel.style.left = `${Math.max(8, Math.min(moveEvent.clientX - offsetX, innerWidth - panel.offsetWidth - 8))}px`; panel.style.top = `${Math.max(8, Math.min(moveEvent.clientY - offsetY, innerHeight - panel.offsetHeight - 8))}px`; panel.style.right = "auto"; panel.style.bottom = "auto"; }; const stop = () => { const now = panel.getBoundingClientRect(); writeJson("mentor-position", { left: now.left, top: now.top }); handle.removeEventListener("pointermove", move); handle.removeEventListener("pointerup", stop); }; handle.addEventListener("pointermove", move); handle.addEventListener("pointerup", stop); });
}

function bindEvents() {
  $("#product-mark").addEventListener("click", () => openDrawer(!$("#settings-drawer").classList.contains("is-open"))); $("#close-drawer").addEventListener("click", () => openDrawer(false)); $("#drawer-backdrop").addEventListener("click", () => openDrawer(false));
  $("#provider-select").addEventListener("change", (event) => { const config = PROVIDERS[event.target.value]; $("#endpoint-input").value = config.endpoint; $("#manual-model").value = config.model; renderProviderFields(); });
  $("#model-select").addEventListener("change", () => { $("#manual-model-field").hidden = Boolean($("#model-select").value); }); $("#refresh-models").addEventListener("click", () => probeProvider({ openGuide: false })); $("#save-settings").addEventListener("click", saveSettingsFromDrawer);
  $("#temperature").addEventListener("input", () => { $("#temperature-output").value = Number($("#temperature").value).toFixed(2); }); $("#top-p").addEventListener("input", () => { $("#top-p-output").value = Number($("#top-p").value).toFixed(2); }); $("#auto-tune").addEventListener("change", renderProviderFields);
  $("#clear-chat").addEventListener("click", () => { state.histories[state.mode] = []; writeJson("histories", state.histories); renderMessages(); }); $("#open-vscode").addEventListener("click", () => { location.href = "vscode://vscode.git/clone?url=https://github.com/widmonstertony/Leetcode.git"; });
  $("#runtime-guide").addEventListener("click", () => showConnectionDialog()); $("#open-runtime").addEventListener("click", openAndWaitForOllama);
  $$('.mode-switch [data-mode]').forEach((button) => button.addEventListener("click", () => switchMode(button.dataset.mode)));
  $("#language").addEventListener("change", (event) => { const browserChinese = navigator.language.toLowerCase().startsWith("zh"); state.locale = event.target.value === "system" ? (browserChinese ? "zh" : "en") : event.target.value; savePreferences(); applyPreferences(); }); $("#statement-language").addEventListener("change", (event) => { state.statementLocale = event.target.value; savePreferences(); });
  $("#theme-toggle").addEventListener("click", () => { state.theme = state.theme === "system" ? "light" : state.theme === "light" ? "dark" : "system"; savePreferences(); applyPreferences(); }); matchMedia("(prefers-color-scheme: dark)").addEventListener("change", () => { if (state.theme === "system") applyPreferences(); });
  $("#start-coaching").addEventListener("click", beginCoaching); $("#next-problem").addEventListener("click", assignNextProblem); $("#mark-mastered").addEventListener("click", () => updateProgress("mastered")); $("#mark-review").addEventListener("click", () => updateProgress("review"));
  $("#load-statement").addEventListener("click", loadStatement); $("#import-problem").addEventListener("click", importProblem); $("#redraw-algorithm").addEventListener("click", () => askJarvis(state.locale === "zh" ? "视觉解释：根据当前代码重画不变量。" : "Visual explanation: redraw the invariant from my current code.", "visual map"));
  $("#hide-problem").addEventListener("click", () => { state.layout.problem = false; updateLayout(); }); $("#hide-code").addEventListener("click", () => { state.layout.code = false; updateLayout(); }); $("#hide-docked-mentor").addEventListener("click", () => { state.layout.mentor = false; updateLayout(); }); $("#float-mentor").addEventListener("click", () => { state.layout.mentorStyle = "floating"; updateLayout(); openMentor(); });
  ["problem", "code", "mentor"].forEach((name) => $(`#show-${name}`).addEventListener("change", (event) => { state.layout[name] = event.target.checked; updateLayout(); })); $$('input[name="mentor-style"]').forEach((radio) => radio.addEventListener("change", () => { state.layout.mentorStyle = radio.value; updateLayout(); })); $("#restore-layout").addEventListener("click", () => { state.layout = { ...state.layout, problem: true, code: true, mentor: true, mentorStyle: "floating", problemWidth: 43 }; renderLayoutControls(); updateLayout(); });
  $$('.language-switch [data-language]').forEach((button) => button.addEventListener("click", () => { state.language = button.dataset.language; $$('.language-switch [data-language]').forEach((item) => item.classList.toggle("is-active", item === button)); $("#code-editor").value = localStorage.getItem(`${STORAGE}:${draftKey()}`) || defaultSource(state.currentProblem); renderAlgorithm(); updateEditor(); }));
  $("#code-editor").addEventListener("input", updateEditor); $("#code-editor").addEventListener("scroll", syncEditorScroll); $("#code-editor").addEventListener("keydown", (event) => { if (event.key !== "Tab") return; event.preventDefault(); const field = event.currentTarget; const start = field.selectionStart, end = field.selectionEnd; if (!event.shiftKey) { field.setRangeText("    ", start, end, "end"); } else { const lineStart = field.value.lastIndexOf("\n", start - 1) + 1; const remove = Math.min(4, (field.value.slice(lineStart).match(/^ +/) || [""])[0].length); field.setRangeText("", lineStart, lineStart + remove, "end"); } updateEditor(); });
  $("#run-code").addEventListener("click", executePython); $("#run-review").addEventListener("click", async () => { await executePython(); await askJarvis(state.locale === "zh" ? "结合当前代码和运行结果，只提示下一步。" : "Use the current code and run result; hint only the next step.", "run and review"); }); $("#save-solution").addEventListener("click", saveSolution);
  $("#load-solution").addEventListener("click", () => { const item = savedSolutions()[Number($("#saved-solution-select").value)]; if (!item) return; state.language = item.language; $("#code-editor").value = item.source; $$('.language-switch [data-language]').forEach((button) => button.classList.toggle("is-active", button.dataset.language === state.language)); updateEditor(); }); $("#upload-solution").addEventListener("click", () => $("#solution-file").click()); $("#solution-file").addEventListener("change", async (event) => { const file = event.target.files?.[0]; if (!file) return; state.language = file.name.endsWith(".java") ? "java" : "python"; $("#code-editor").value = await file.text(); $("#save-filename").value = file.name; $$('.language-switch [data-language]').forEach((button) => button.classList.toggle("is-active", button.dataset.language === state.language)); updateEditor(); });
  $("#start-stress").addEventListener("click", beginSystemStress); $("#next-system-mission").addEventListener("click", assignNextSystem); $("#redraw-system").addEventListener("click", () => askJarvis(state.locale === "zh" ? "根据目前确认的设计，重画当前架构图。" : "Redraw the architecture using only confirmed decisions.", "visual map"));
  $("#system-command-form").addEventListener("submit", async (event) => { event.preventDefault(); const field = $("#system-command"); const value = field.value; field.value = ""; await askJarvis(value, "command dock"); }); $("#system-next-step").addEventListener("click", () => quickAction("next"));
  $("#jarvis-launcher").addEventListener("click", openMentor); $("#hide-mentor").addEventListener("click", () => { $("#jarvis-panel").hidden = true; $("#jarvis-launcher").setAttribute("aria-expanded", "false"); }); $("#dock-mentor").addEventListener("click", () => { state.layout.mentorStyle = "docked"; state.layout.mentor = true; $("#jarvis-panel").hidden = true; updateLayout(); });
  [["#chat-form", "#message"], ["#docked-chat-form", "#docked-message"]].forEach(([formSelector, fieldSelector]) => $(formSelector).addEventListener("submit", async (event) => { event.preventDefault(); const field = $(fieldSelector); const value = field.value; field.value = ""; await askJarvis(value); })); $$('[data-action]').forEach((button) => button.addEventListener("click", () => quickAction(button.dataset.action)));
  $$('#mobile-nav [data-mobile-pane]').forEach((button) => button.addEventListener("click", () => { state.layout.mobilePane = button.dataset.mobilePane; if (state.layout.mobilePane === "jarvis" && state.layout.mentorStyle === "floating") openMentor(); updateLayout(); }));
  $$('.dialog-close').forEach((button) => button.addEventListener("click", () => button.closest("dialog").close())); $("#enter-workspace").addEventListener("click", () => $("#connection-dialog").close()); ["#retry-connection", "#installed-retry", "#origin-retry", "#permission-retry"].forEach((selector) => $(selector).addEventListener("click", () => probeProvider({ openGuide: true }))); $("#open-ollama").addEventListener("click", openAndWaitForOllama); $("#pull-model").addEventListener("click", pullModel); $("#copy-origin-command").addEventListener("click", async () => { await navigator.clipboard.writeText($("#origin-command").textContent); showToast(state.locale === "zh" ? "命令已复制" : "Command copied"); });
  addEventListener("resize", updateLayout); setupSplitter(); setupMentorDrag();
}

async function initialize() {
  loadState(); bindEvents(); $("#language").value = readJson("preferences", {}).locale || "system"; $("#statement-language").value = state.statementLocale; $("#origin-command").textContent = platformOriginCommand();
  try { await loadCatalog(); } catch (error) { showToast(error.message); return; }
  applyPreferences(); renderLayoutControls(); updateLayout(); updateEditor(); probeProvider();
}

document.addEventListener("DOMContentLoaded", initialize);
