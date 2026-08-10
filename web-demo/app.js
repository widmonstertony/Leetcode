"use strict";

const OLLAMA_BASE = "http://127.0.0.1:11434";
const RECOMMENDED_MODEL = "qwen3.5:9b";
const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => [...document.querySelectorAll(selector)];

const copy = {
  en: {
    skip: "Skip to workspace", checkingModel: "Checking local Ollama…", algorithms: "Algorithms", systemDesign: "System design", hideProblem: "Hide problem",
    connection: "Connection", settings: "Settings", fixConnection: "Fix connection", problem: "Problem", code: "Code",
    track: "Track", mission: "Mission", objective: "Think first", openProblem: "Open on LeetCode ↗", newMission: "New mission",
    markReview: "Mark for review", markMastered: "Mark mastered", savedLocally: "Saved in this browser", editorLabel: "Solution editor",
    editorHelp: "Tab indent · Shift+Tab outdent · ⌘/Ctrl+F search", testsAndRun: "Tests and run settings",
    methodName: "Solution method", timeout: "Timeout", testCases: "JSON test cases", run: "▶ Run in browser",
    runAndAsk: "Run and ask JARVIS", mentorRole: "AI interview copilot", mentorOpening: "Connect your local Ollama model and I will coach against the code and results visible in this browser.",
    yourReasoning: "Your reasoning", messagePlaceholder: "Explain your next step…", resetPosition: "Reset position", send: "Send",
    localModel: "LOCAL MODEL", connectOllama: "Connect Ollama", model: "Local model", continue: "Continue to workspace",
    openOllama: "Open Ollama", retry: "Check again", launchExplanation: "Your browser may ask permission to open the Ollama app. This site cannot start software without your confirmation.",
    missingExplanation: "If Ollama did not open, it may not be installed. Install it once, open it, then return here. LeetTutor itself requires no installation.",
    downloadOllama: "Download Ollama ↗", installedRetry: "I installed/opened it — check again", originExplanation: "Ollama is running, but it has not allowed tonytan.me. Run this one-time command, quit Ollama completely, and reopen it.",
    copy: "Copy", permissionExplanation: "Allow Local network access for tonytan.me in the browser site settings, refresh this page, and check again.",
    noModelsExplanation: "Ollama is ready but no local model was found. Download the recommended tutor model directly into Ollama.",
    modelToDownload: "Model to download", downloadModel: "Download model", privacyTitle: "Privacy and requirements",
    privacyBody: "Prompts, code, model names, and answers go directly between this browser and Ollama on 127.0.0.1. They are not relayed through tonytan.me.",
    preferences: "PREFERENCES", languageAppearance: "Language and appearance", language: "Interface language", theme: "Appearance",
    checkingTitle: "Checking this computer", checkingBody: "Looking for Ollama on the loopback-only API.",
    readyTitle: "Local Ollama is ready", readyBody: "Connected directly to {count} model(s) on this computer.",
    offlineTitle: "Ollama was not reached", offlineBody: "It may be closed or not installed. Try opening it first; if no app opens, use the installation guide.",
    launchingTitle: "Waiting for Ollama", launchingBody: "Approve the browser prompt if it appears. LeetTutor will connect automatically when the local API is ready.",
    missingTitle: "Ollama may be missing", missingBody: "No local API appeared after the launch attempt. Install Ollama, open it, and check again.",
    permissionTitle: "Browser permission is blocked", permissionBody: "tonytan.me cannot inspect localhost until Local network access is allowed for this site.",
    originTitle: "Ollama is running but blocked this website", originBody: "The local API responded, but browser access from tonytan.me is not in OLLAMA_ORIGINS.",
    noModelsTitle: "Ollama has no local models", noModelsBody: "The app is running. Download one model before asking JARVIS.",
    modelReady: "Ollama · {model} · local only", modelUnavailable: "Local Ollama unavailable", modelMissing: "Ollama ready · model required",
    connectionNeeded: "Connect a local model", connectionNeededBody: "Code practice works in the browser; JARVIS needs Ollama on this computer.",
    noModelBanner: "Download a local model", noModelBannerBody: "Ollama is connected but its model list is empty.",
    copied: "Command copied", pullStarting: "Starting local model download…", pullProgress: "Downloading {percent}% · {status}", pullComplete: "Model downloaded. Refreshing the local model list…", pullFailed: "Model download failed: {error}",
    loadingCatalog: "Loading the curriculum…", catalogFailed: "The curriculum could not be loaded.",
    progressReview: "Marked for review", progressMastered: "Mastered", progressNew: "New mission",
    pythonOnly: "The in-browser runner currently supports Python. Java remains editable and locally saved.",
    runnerLoading: "Loading the private in-browser Python runtime for the first run…", runnerRunning: "Running in an isolated browser worker…", runnerTimeout: "Execution timed out and the worker was reset.",
    runnerFailed: "Browser runner failed: {error}", testsPassed: "{passed}/{total} tests passed", jarvisNeedsModel: "Open the connection panel and choose a local Ollama model first.",
    jarvisThinking: "JARVIS is thinking on this computer…", jarvisFailed: "The local model did not answer: {error}", emptyMessage: "Explain your reasoning or ask a question first.",
    topicsAll: "All topics", systemTrack: "System design cases", genericFocus: "Load the original problem statement on LeetCode, define the invariant, then implement and test your solution.",
  },
  zh: {
    skip: "跳到工作区", checkingModel: "正在检测本机 Ollama…", algorithms: "算法", systemDesign: "系统设计", hideProblem: "隐藏题目",
    connection: "连接", settings: "设置", fixConnection: "修复连接", problem: "题目", code: "代码",
    track: "专题", mission: "任务", objective: "先想清楚", openProblem: "在 LeetCode 打开 ↗", newMission: "新任务",
    markReview: "标记复习", markMastered: "标记掌握", savedLocally: "已保存在这个浏览器", editorLabel: "题解编辑器",
    editorHelp: "Tab 缩进 · Shift+Tab 反缩进 · ⌘/Ctrl+F 查找", testsAndRun: "测试用例与运行设置",
    methodName: "Solution 方法名", timeout: "超时", testCases: "JSON 测试用例", run: "▶ 在浏览器运行",
    runAndAsk: "运行并问 JARVIS", mentorRole: "AI 面试副驾", mentorOpening: "连接本机 Ollama 后，我会结合这个浏览器里的代码和运行结果进行辅导。",
    yourReasoning: "你的推理", messagePlaceholder: "解释下一步怎么做…", resetPosition: "复位位置", send: "发送",
    localModel: "本机模型", connectOllama: "连接 Ollama", model: "本机模型", continue: "进入工作区",
    openOllama: "打开 Ollama", retry: "重新检测", launchExplanation: "浏览器可能询问是否打开 Ollama；未经你的确认，网站不能启动本机软件。",
    missingExplanation: "如果 Ollama 没有打开，它可能尚未安装。只需安装一次并打开，然后回到这里；LeetTutor 本身无需安装。",
    downloadOllama: "下载 Ollama ↗", installedRetry: "我已安装/打开，重新检测", originExplanation: "Ollama 正在运行，但尚未允许 tonytan.me。执行下面的一次性命令，完全退出 Ollama 后重新打开。",
    copy: "复制", permissionExplanation: "请在浏览器的网站设置里允许 tonytan.me 的“本地网络访问”，刷新页面后重新检测。",
    noModelsExplanation: "Ollama 已就绪，但没有找到本机模型。可以直接把推荐导师模型下载进 Ollama。",
    modelToDownload: "要下载的模型", downloadModel: "下载模型", privacyTitle: "隐私与要求",
    privacyBody: "Prompt、代码、模型名称和回答只在这个浏览器与 127.0.0.1 上的 Ollama 之间传输，不经过 tonytan.me 中转。",
    preferences: "偏好设置", languageAppearance: "语言与外观", language: "界面语言", theme: "显示主题",
    checkingTitle: "正在检测这台电脑", checkingBody: "正在查找只监听回环地址的 Ollama API。",
    readyTitle: "本机 Ollama 已就绪", readyBody: "已直接连接这台电脑上的 {count} 个模型。",
    offlineTitle: "没有连接到 Ollama", offlineBody: "它可能尚未启动或尚未安装。请先尝试打开；如果没有应用响应，再按安装说明操作。",
    launchingTitle: "正在等待 Ollama", launchingBody: "如果浏览器弹出询问，请允许打开。Ollama API 就绪后会自动连接。",
    missingTitle: "可能尚未安装 Ollama", missingBody: "尝试启动后仍未出现本机 API。请安装并打开 Ollama，然后重新检测。",
    permissionTitle: "浏览器权限被阻止", permissionBody: "允许 tonytan.me 的“本地网络访问”之前，网页不能检测 localhost。",
    originTitle: "Ollama 已运行，但拒绝了这个网站", originBody: "本机 API 有响应，但 OLLAMA_ORIGINS 还没有允许 tonytan.me。",
    noModelsTitle: "Ollama 中没有本机模型", noModelsBody: "应用已经运行；向 JARVIS 提问前需要先下载一个模型。",
    modelReady: "Ollama · {model} · 仅限本机", modelUnavailable: "本机 Ollama 不可用", modelMissing: "Ollama 已就绪 · 需要模型",
    connectionNeeded: "连接本机模型", connectionNeededBody: "代码练习可直接在浏览器运行；JARVIS 需要这台电脑上的 Ollama。",
    noModelBanner: "下载一个本机模型", noModelBannerBody: "Ollama 已连接，但模型列表为空。",
    copied: "命令已复制", pullStarting: "正在开始下载本机模型…", pullProgress: "正在下载 {percent}% · {status}", pullComplete: "模型下载完成，正在刷新列表…", pullFailed: "模型下载失败：{error}",
    loadingCatalog: "正在载入课程…", catalogFailed: "无法载入课程数据。",
    progressReview: "已标记复习", progressMastered: "已掌握", progressNew: "新任务",
    pythonOnly: "浏览器运行器目前支持 Python；Java 仍可编辑并保存在本机浏览器。",
    runnerLoading: "首次运行正在载入浏览器内的私有 Python Runtime…", runnerRunning: "正在隔离的浏览器 Worker 中运行…", runnerTimeout: "执行超时，运行 Worker 已重置。",
    runnerFailed: "浏览器运行失败：{error}", testsPassed: "通过 {passed}/{total} 个测试", jarvisNeedsModel: "请先打开连接面板并选择一个本机 Ollama 模型。",
    jarvisThinking: "JARVIS 正在这台电脑上思考…", jarvisFailed: "本机模型没有回答：{error}", emptyMessage: "请先解释推理或提出问题。",
    topicsAll: "全部专题", systemTrack: "系统设计案例", genericFocus: "先在 LeetCode 载入原题，定义不变量，再实现并测试题解。",
  },
};

const topicNames = {
  "二分": { en: "Binary search", zh: "二分" }, "栈": { en: "Stack", zh: "栈" }, "堆": { en: "Heap", zh: "堆" },
  "动态规划": { en: "Dynamic programming", zh: "动态规划" }, "区间动态规划": { en: "Interval DP", zh: "区间动态规划" },
};

const state = {
  locale: "en", mode: "algorithm", catalog: { problems: [], system_design: [] }, current: null,
  ollamaStatus: "checking", models: [], selectedModel: "", lastRun: null, launchAttempts: 0,
};

function t(key, values = {}) {
  let result = copy[state.locale]?.[key] || copy.en[key] || key;
  Object.entries(values).forEach(([name, value]) => { result = result.replaceAll(`{${name}}`, String(value)); });
  return result;
}

function detectedLocale() {
  return navigator.languages?.some((value) => value.toLowerCase().startsWith("zh")) ? "zh" : "en";
}

function applyPreferences() {
  const language = localStorage.getItem("leettutor-language") || "system";
  const theme = localStorage.getItem("leettutor-theme") || "system";
  state.locale = language === "system" ? detectedLocale() : language;
  const resolvedTheme = theme === "system" ? (matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light") : theme;
  document.documentElement.lang = state.locale === "zh" ? "zh-CN" : "en";
  document.documentElement.dataset.theme = resolvedTheme;
  $("#language").value = language;
  $("#theme").value = theme;
  $$('[data-copy]').forEach((node) => { node.textContent = t(node.dataset.copy); });
  $$('[data-placeholder]').forEach((node) => { node.placeholder = t(node.dataset.placeholder); });
  $$('[data-copy-title]').forEach((node) => { node.title = t(node.dataset.copyTitle); });
  renderCatalogControls(false);
  renderCurrent(false);
  renderRuntime();
  renderDiagnostic();
}

function timeoutSignal(milliseconds) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), milliseconds);
  return { signal: controller.signal, cancel: () => clearTimeout(timer) };
}

async function loopbackFetch(path, options = {}, timeoutMs = 6_000) {
  const timeout = timeoutSignal(timeoutMs);
  const headers = { Accept: "application/json", ...(options.headers || {}) };
  try {
    return await fetch(`${OLLAMA_BASE}${path}`, {
      ...options,
      headers,
      cache: "no-store",
      signal: timeout.signal,
      targetAddressSpace: "loopback",
    });
  } finally {
    timeout.cancel();
  }
}

async function loopbackPermission() {
  if (!navigator.permissions?.query) return "unknown";
  for (const name of ["loopback-network", "local-network-access"]) {
    try {
      const result = await navigator.permissions.query({ name });
      if (result.state === "denied") return "denied";
      if (result.state === "granted") return "granted";
    } catch (_error) { /* Browser does not expose this permission name yet. */ }
  }
  return "unknown";
}

async function probeOllama() {
  try {
    const response = await loopbackFetch("/api/tags");
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const payload = await response.json();
    return { status: "ready", models: Array.isArray(payload.models) ? payload.models : [] };
  } catch (corsOrOfflineError) {
    const permission = await loopbackPermission();
    if (permission === "denied") return { status: "permission", error: corsOrOfflineError };
    try {
      const response = await loopbackFetch("/api/version", { mode: "no-cors" }, 3_000);
      if (response.type === "opaque" || response.ok) return { status: "origin", error: corsOrOfflineError };
    } catch (_offlineError) { /* No readable or opaque loopback response. */ }
    return { status: "offline", error: corsOrOfflineError };
  }
}

function clearDiagnosticSections() {
  for (const selector of ["#ready-actions", "#launch-actions", "#install-actions", "#origin-actions", "#permission-actions", "#model-actions"]) {
    $(selector).hidden = true;
  }
}

function setDiagnostic(status, { models = [] } = {}) {
  state.ollamaStatus = status;
  if (status === "ready" || status === "no-models") {
    state.models = models;
    populateModels();
  }
  renderRuntime();
  renderDiagnostic();
}

function renderRuntime() {
  document.body.classList.toggle("ollama-ready", state.ollamaStatus === "ready");
  document.body.classList.toggle("ollama-error", ["offline", "permission", "origin", "missing"].includes(state.ollamaStatus));
  const label = $("#runtime-label");
  const banner = $("#connection-banner");
  if (!label || !banner) return;
  if (state.ollamaStatus === "ready") {
    label.textContent = t("modelReady", { model: state.selectedModel || state.models[0]?.name || "Ollama" });
    banner.hidden = true;
  } else if (state.ollamaStatus === "no-models") {
    label.textContent = t("modelMissing");
    $("#banner-title").textContent = t("noModelBanner");
    $("#banner-body").textContent = t("noModelBannerBody");
    banner.hidden = false;
  } else if (state.ollamaStatus === "checking" || state.ollamaStatus === "launching") {
    label.textContent = t("checkingModel");
    banner.hidden = true;
  } else {
    label.textContent = t("modelUnavailable");
    $("#banner-title").textContent = t("connectionNeeded");
    $("#banner-body").textContent = t("connectionNeededBody");
    banner.hidden = false;
  }
}

function renderDiagnostic() {
  const title = $("#diagnostic-title");
  const body = $("#diagnostic-body");
  const icon = $("#diagnostic-icon");
  if (!title || !body || !icon) return;
  clearDiagnosticSections();
  icon.dataset.status = "checking";
  const status = state.ollamaStatus;
  if (status === "checking") {
    title.textContent = t("checkingTitle"); body.textContent = t("checkingBody");
  } else if (status === "launching") {
    title.textContent = t("launchingTitle"); body.textContent = t("launchingBody"); $("#launch-actions").hidden = false;
  } else if (status === "ready") {
    icon.dataset.status = "ready"; title.textContent = t("readyTitle"); body.textContent = t("readyBody", { count: state.models.length }); $("#ready-actions").hidden = false;
  } else if (status === "no-models") {
    title.textContent = t("noModelsTitle"); body.textContent = t("noModelsBody"); $("#model-actions").hidden = false;
  } else if (status === "permission") {
    icon.dataset.status = "error"; title.textContent = t("permissionTitle"); body.textContent = t("permissionBody"); $("#permission-actions").hidden = false;
  } else if (status === "origin") {
    icon.dataset.status = "error"; title.textContent = t("originTitle"); body.textContent = t("originBody"); $("#origin-actions").hidden = false;
  } else if (status === "missing") {
    icon.dataset.status = "error"; title.textContent = t("missingTitle"); body.textContent = t("missingBody"); $("#install-actions").hidden = false; $("#launch-actions").hidden = false;
  } else {
    icon.dataset.status = "error"; title.textContent = t("offlineTitle"); body.textContent = t("offlineBody"); $("#launch-actions").hidden = false; if (state.launchAttempts > 0) $("#install-actions").hidden = false;
  }
}

function showConnectionDialog() {
  const dialog = $("#connection-dialog");
  if (!dialog.open) dialog.showModal();
}

async function diagnoseOllama({ showDialog = false } = {}) {
  setDiagnostic("checking");
  if (showDialog) showConnectionDialog();
  const result = await probeOllama();
  if (result.status === "ready") {
    setDiagnostic(result.models.length ? "ready" : "no-models", { models: result.models });
    if (!result.models.length) showConnectionDialog();
    else if (!showDialog && $("#connection-dialog").open) $("#connection-dialog").close();
  } else {
    setDiagnostic(result.status);
    showConnectionDialog();
  }
  return result;
}

function populateModels() {
  const select = $("#model-select");
  const preferred = localStorage.getItem("leettutor-model") || state.selectedModel;
  select.replaceChildren();
  for (const item of state.models) {
    const name = item.name || item.model;
    if (!name) continue;
    const option = document.createElement("option");
    option.value = name; option.textContent = name; select.append(option);
  }
  if (preferred && [...select.options].some((option) => option.value === preferred)) select.value = preferred;
  state.selectedModel = select.value || "";
  if (state.selectedModel) localStorage.setItem("leettutor-model", state.selectedModel);
}

function triggerOllamaDeepLink() {
  const link = document.createElement("a");
  link.href = "ollama://";
  link.hidden = true;
  document.body.append(link);
  link.click();
  link.remove();
}

async function openAndWaitForOllama() {
  state.launchAttempts += 1;
  setDiagnostic("launching");
  triggerOllamaDeepLink();
  for (let attempt = 0; attempt < 18; attempt += 1) {
    await new Promise((resolve) => setTimeout(resolve, attempt < 5 ? 900 : 1_400));
    const result = await probeOllama();
    if (result.status === "ready") {
      setDiagnostic(result.models.length ? "ready" : "no-models", { models: result.models });
      return;
    }
    if (["permission", "origin"].includes(result.status)) { setDiagnostic(result.status); return; }
  }
  setDiagnostic("missing");
}

function platformOriginCommand() {
  const platform = (navigator.userAgentData?.platform || navigator.platform || "").toLowerCase();
  if (platform.includes("win")) return "[Environment]::SetEnvironmentVariable('OLLAMA_ORIGINS','https://tonytan.me','User')";
  if (platform.includes("linux")) return "OLLAMA_ORIGINS=https://tonytan.me ollama serve";
  return 'launchctl setenv OLLAMA_ORIGINS "https://tonytan.me"';
}

async function pullModel() {
  const name = $("#pull-model-name").value.trim() || RECOMMENDED_MODEL;
  const progress = $("#pull-progress");
  const status = $("#pull-status");
  progress.hidden = false; progress.removeAttribute("value"); status.textContent = t("pullStarting");
  try {
    const response = await loopbackFetch("/api/pull", {
      method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ model: name, stream: true }),
    }, 30 * 60 * 1_000);
    if (!response.ok || !response.body) throw new Error(`HTTP ${response.status}`);
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = "";
    while (true) {
      const { value, done } = await reader.read();
      if (done) break;
      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split("\n"); buffer = lines.pop() || "";
      for (const line of lines) {
        if (!line.trim()) continue;
        const update = JSON.parse(line);
        const percent = update.total ? Math.round((update.completed || 0) / update.total * 100) : 0;
        if (update.total) { progress.value = percent; status.textContent = t("pullProgress", { percent, status: update.status || "" }); }
        else status.textContent = update.status || t("pullStarting");
        if (update.error) throw new Error(update.error);
      }
    }
    progress.value = 100; status.textContent = t("pullComplete"); localStorage.setItem("leettutor-model", name);
    await diagnoseOllama({ showDialog: true });
  } catch (error) {
    progress.hidden = true; status.textContent = t("pullFailed", { error: error.message });
  }
}

function progressKey() { return `leettutor-progress:${state.mode}:${state.current?.id || "none"}`; }
function draftKey(language = selectedLanguage()) { return `leettutor-draft:${state.mode}:${state.current?.id || "none"}:${language}`; }
function selectedLanguage() { return $(".language-switch button.is-active")?.dataset.language || "python"; }

function defaultSource(item, language = selectedLanguage()) {
  if (state.mode === "system") return `# ${state.locale === "zh" ? item.title_cn : item.title}\n\n## Requirements and estimates\n\n## API and data model\n\n## Architecture\n\n## Reliability and trade-offs\n`;
  if (language === "java") return `class Solution {\n    public int solve(int[] nums) {\n        // Define the invariant first.\n        return 0;\n    }\n}\n`;
  if (Number(item.id) === 704) return "class Solution:\n    def search(self, nums, target):\n        left, right = 0, len(nums) - 1\n        while left <= right:\n            mid = left + (right - left) // 2\n            if nums[mid] == target:\n                return mid\n            if nums[mid] < target:\n                left = mid + 1\n            else:\n                right = mid - 1\n        return -1\n";
  return "class Solution:\n    def solve(self, *args):\n        # Define the invariant, then implement the solution.\n        pass\n";
}

function defaultTests(item) {
  if (Number(item?.id) === 704) return '[\n  {"args":[[-1,0,3,5,9,12],9],"expected":4},\n  {"args":[[-1,0,3,5,9,12],2],"expected":-1}\n]';
  return '[\n  {"args":[],"expected":null}\n]';
}

function renderCatalogControls(resetCurrent = true) {
  if (!$("#topic-select") || !state.catalog.problems.length) return;
  const topics = state.mode === "algorithm" ? [...new Set(state.catalog.problems.map((item) => item.topic))] : ["system"];
  const topicSelect = $("#topic-select");
  const priorTopic = topicSelect.value;
  topicSelect.replaceChildren();
  const allOption = document.createElement("option"); allOption.value = "all"; allOption.textContent = state.mode === "algorithm" ? t("topicsAll") : t("systemTrack"); topicSelect.append(allOption);
  if (state.mode === "algorithm") for (const topic of topics) { const option = document.createElement("option"); option.value = topic; option.textContent = topicNames[topic]?.[state.locale] || topic; topicSelect.append(option); }
  if (priorTopic && [...topicSelect.options].some((option) => option.value === priorTopic)) topicSelect.value = priorTopic;
  const items = state.mode === "algorithm" ? state.catalog.problems : state.catalog.system_design;
  const filtered = state.mode === "algorithm" && topicSelect.value !== "all" ? items.filter((item) => item.topic === topicSelect.value) : items;
  const select = $("#problem-select");
  const priorId = state.current?.id;
  select.replaceChildren();
  for (const item of filtered) { const option = document.createElement("option"); option.value = String(item.id); option.textContent = `${item.id} · ${state.locale === "zh" ? (item.title_cn || item.title) : item.title}`; select.append(option); }
  if (!resetCurrent && priorId && filtered.some((item) => String(item.id) === String(priorId))) select.value = String(priorId);
  state.current = filtered.find((item) => String(item.id) === select.value) || filtered[0] || null;
}

function renderCurrent(resetEditor = true) {
  const item = state.current;
  if (!item || !$("#problem-title")) return;
  const algorithm = state.mode === "algorithm";
  $("#problem-number").textContent = algorithm ? `LEETCODE ${item.id}` : item.id;
  $("#problem-title").textContent = state.locale === "zh" ? (item.title_cn || item.title) : item.title;
  $("#difficulty").textContent = item.difficulty || "";
  $("#difficulty").dataset.level = String(item.difficulty || "").toLowerCase();
  $("#problem-focus").textContent = algorithm ? (item.focus || t("genericFocus")) : (state.locale === "zh" ? item.requirement_cn : item.requirement);
  $("#objective").textContent = algorithm ? (item.invariant_prompt || t("genericFocus")) : (state.locale === "zh" ? item.first_question_cn : item.first_question);
  $("#problem-link").hidden = !algorithm; $("#problem-link").href = algorithm ? item.url : "#";
  $("#editor-filename").textContent = algorithm ? `${item.id}.${item.slug}.${selectedLanguage() === "python" ? "py" : "java"}` : `${item.id}.architecture-notes.md`;
  $("#method-name").value = Number(item.id) === 704 ? "search" : "solve";
  $("#test-cases").value = defaultTests(item);
  $(".test-settings").hidden = !algorithm;
  $("#run-code").hidden = !algorithm; $("#run-review").textContent = algorithm ? t("runAndAsk") : t("send");
  if (resetEditor) $("#code-editor").value = localStorage.getItem(draftKey()) || defaultSource(item);
  const progress = localStorage.getItem(progressKey()) || "new";
  $("#progress-label").textContent = progress === "mastered" ? t("progressMastered") : progress === "review" ? t("progressReview") : t("progressNew");
  if (resetEditor) { state.lastRun = null; $("#run-result").textContent = ""; }
}

async function loadCatalog() {
  try {
    const response = await fetch("./catalog.json", { cache: "no-store" });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    state.catalog = await response.json();
    renderCatalogControls(); renderCurrent();
  } catch (error) {
    showToast(`${t("catalogFailed")} ${error.message}`);
  }
}

let pythonWorker = null;
let pythonReady = null;
let resolvePythonReady = null;
let rejectPythonReady = null;
const pendingRuns = new Map();

function resetPythonWorker() {
  pythonWorker?.terminate(); pythonWorker = null; pythonReady = null; pendingRuns.clear();
}

function ensurePythonWorker() {
  if (pythonReady) return pythonReady;
  pythonReady = new Promise((resolve, reject) => { resolvePythonReady = resolve; rejectPythonReady = reject; });
  pythonWorker = new Worker("./python-worker.js");
  pythonWorker.addEventListener("message", (event) => {
    if (event.data.type === "ready") { resolvePythonReady?.(); return; }
    if (event.data.type === "init-error") { rejectPythonReady?.(new Error(event.data.error)); resetPythonWorker(); return; }
    const pending = pendingRuns.get(event.data.id);
    if (!pending) return;
    clearTimeout(pending.timer); pendingRuns.delete(event.data.id);
    if (event.data.type === "result") pending.resolve(event.data.result); else pending.reject(new Error(event.data.error));
  });
  pythonWorker.addEventListener("error", (event) => { rejectPythonReady?.(new Error(event.message)); resetPythonWorker(); });
  return pythonReady;
}

async function executePython() {
  const output = $("#run-result");
  if (selectedLanguage() !== "python") { output.textContent = t("pythonOnly"); return null; }
  output.textContent = t("runnerLoading");
  try {
    await ensurePythonWorker();
    output.textContent = t("runnerRunning");
    const id = crypto.randomUUID();
    const timeoutMs = Number($("#run-timeout").value || 4) * 1000;
    const result = await new Promise((resolve, reject) => {
      const timer = setTimeout(() => { pendingRuns.delete(id); resetPythonWorker(); reject(new Error(t("runnerTimeout"))); }, timeoutMs);
      pendingRuns.set(id, { resolve, reject, timer });
      pythonWorker.postMessage({ id, source: $("#code-editor").value, methodName: $("#method-name").value, testCases: $("#test-cases").value });
    });
    state.lastRun = result;
    output.textContent = [t("testsPassed", { passed: result.passed, total: result.total }), ...(result.cases || []).map((item, index) => `#${index + 1} ${item.passed ? "✓" : "✗"} actual=${JSON.stringify(item.actual)} expected=${JSON.stringify(item.expected)}`), result.stdout, result.error].filter(Boolean).join("\n");
    localStorage.setItem(progressKey(), result.passed === result.total ? "mastered" : "review"); renderCurrent(false);
    return result;
  } catch (error) {
    output.textContent = error.message === t("runnerTimeout") ? error.message : t("runnerFailed", { error: error.message });
    return null;
  }
}

function appendMessage(role, message) {
  const article = document.createElement("article"); article.className = `message ${role}`;
  const badge = document.createElement("span"); badge.textContent = role === "assistant" ? "J" : "YOU";
  const paragraph = document.createElement("p"); paragraph.textContent = message;
  article.append(badge, paragraph); $("#messages").append(article); article.scrollIntoView({ behavior: "smooth", block: "nearest" });
  return paragraph;
}

async function askJarvis(message) {
  if (!message.trim()) { showToast(t("emptyMessage")); return; }
  if (state.ollamaStatus !== "ready" || !state.selectedModel) { appendMessage("assistant", t("jarvisNeedsModel")); showConnectionDialog(); return; }
  appendMessage("user", message.trim());
  const pending = appendMessage("assistant", t("jarvisThinking"));
  try {
    const response = await loopbackFetch("/api/chat", {
      method: "POST", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        model: state.selectedModel, stream: false, think: false,
        options: { temperature: 0.2, top_p: 0.9, num_predict: 700, num_ctx: 8192 },
        messages: [
          { role: "system", content: `You are JARVIS, a concise Socratic interview tutor. Reply in ${state.locale === "zh" ? "Chinese" : "English"}. Give one specific observation, one short hint, and exactly one next question. Never invent execution results.` },
          { role: "user", content: `Mission: ${$("#problem-title").textContent}\nContext: ${$("#problem-focus").textContent}\nObjective: ${$("#objective").textContent}\nCurrent work:\n${$("#code-editor").value}\nLast browser run: ${JSON.stringify(state.lastRun)}\n\nStudent: ${message.trim()}` },
        ],
      }),
    }, 180_000);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const payload = await response.json(); pending.textContent = payload.message?.content || t("jarvisFailed", { error: "empty response" });
  } catch (error) { pending.textContent = t("jarvisFailed", { error: error.message }); }
}

function showToast(message) {
  const toast = $("#toast"); toast.textContent = message; toast.hidden = false;
  clearTimeout(showToast.timer); showToast.timer = setTimeout(() => { toast.hidden = true; }, 2_800);
}

function setupSplitter() {
  const splitter = $("#splitter");
  splitter.addEventListener("pointerdown", (event) => {
    splitter.setPointerCapture(event.pointerId); document.body.classList.add("is-resizing");
    const move = (moveEvent) => { const bounds = $("#workspace").getBoundingClientRect(); const percent = Math.min(67, Math.max(28, (moveEvent.clientX - bounds.left) / bounds.width * 100)); document.documentElement.style.setProperty("--problem-width", `${percent}%`); localStorage.setItem("leettutor-problem-width", String(percent)); };
    const stop = () => { document.body.classList.remove("is-resizing"); splitter.removeEventListener("pointermove", move); splitter.removeEventListener("pointerup", stop); };
    splitter.addEventListener("pointermove", move); splitter.addEventListener("pointerup", stop);
  });
  const saved = Number(localStorage.getItem("leettutor-problem-width")); if (saved >= 28 && saved <= 67) document.documentElement.style.setProperty("--problem-width", `${saved}%`);
}

function setupMentorDrag() {
  const panel = $("#jarvis-panel"); const handle = $("#mentor-drag-handle");
  const restore = () => { try { const point = JSON.parse(localStorage.getItem("leettutor-mentor-position") || "null"); if (point && innerWidth > 620) { panel.style.left = `${Math.max(7, Math.min(point.left, innerWidth - panel.offsetWidth - 7))}px`; panel.style.top = `${Math.max(7, Math.min(point.top, innerHeight - panel.offsetHeight - 7))}px`; panel.style.right = "auto"; panel.style.bottom = "auto"; } } catch (_error) {} };
  handle.addEventListener("pointerdown", (event) => {
    if (event.target.closest("button")) return;
    const rect = panel.getBoundingClientRect(); const offsetX = event.clientX - rect.left; const offsetY = event.clientY - rect.top; handle.setPointerCapture(event.pointerId);
    const move = (moveEvent) => { const left = Math.max(7, Math.min(moveEvent.clientX - offsetX, innerWidth - panel.offsetWidth - 7)); const top = Math.max(7, Math.min(moveEvent.clientY - offsetY, innerHeight - panel.offsetHeight - 7)); panel.style.left = `${left}px`; panel.style.top = `${top}px`; panel.style.right = "auto"; panel.style.bottom = "auto"; };
    const stop = () => { const current = panel.getBoundingClientRect(); localStorage.setItem("leettutor-mentor-position", JSON.stringify({ left: current.left, top: current.top })); handle.removeEventListener("pointermove", move); handle.removeEventListener("pointerup", stop); };
    handle.addEventListener("pointermove", move); handle.addEventListener("pointerup", stop);
  });
  $("#dock-mentor").addEventListener("click", () => { localStorage.removeItem("leettutor-mentor-position"); panel.removeAttribute("style"); });
  addEventListener("resize", restore); restore();
}

function bindEvents() {
  $("#language").addEventListener("change", (event) => { localStorage.setItem("leettutor-language", event.target.value); applyPreferences(); });
  $("#theme").addEventListener("change", (event) => { localStorage.setItem("leettutor-theme", event.target.value); applyPreferences(); });
  matchMedia("(prefers-color-scheme: dark)").addEventListener("change", applyPreferences);
  $("#settings-button").addEventListener("click", () => $("#settings-dialog").showModal());
  $("#connection-button").addEventListener("click", () => diagnoseOllama({ showDialog: true }));
  $("#banner-action").addEventListener("click", showConnectionDialog);
  for (const selector of ["#retry-connection", "#installed-retry", "#origin-retry", "#permission-retry"]) $(selector).addEventListener("click", () => diagnoseOllama({ showDialog: true }));
  $("#open-ollama").addEventListener("click", openAndWaitForOllama);
  $("#pull-model").addEventListener("click", pullModel);
  $("#model-select").addEventListener("change", (event) => { state.selectedModel = event.target.value; localStorage.setItem("leettutor-model", state.selectedModel); renderRuntime(); });
  $("#enter-workspace").addEventListener("click", () => $("#connection-dialog").close());
  $("#origin-command").textContent = platformOriginCommand();
  $("#copy-origin-command").addEventListener("click", async () => { await navigator.clipboard.writeText($("#origin-command").textContent); showToast(t("copied")); });
  $$(".mode-switch button").forEach((button) => button.addEventListener("click", () => { state.mode = button.dataset.mode; $$(".mode-switch button").forEach((item) => item.classList.toggle("is-active", item === button)); renderCatalogControls(); renderCurrent(); }));
  $("#topic-select").addEventListener("change", () => { renderCatalogControls(); renderCurrent(); });
  $("#problem-select").addEventListener("change", (event) => { const items = state.mode === "algorithm" ? state.catalog.problems : state.catalog.system_design; state.current = items.find((item) => String(item.id) === event.target.value) || null; renderCurrent(); });
  $$(".language-switch button").forEach((button) => button.addEventListener("click", () => { $$(".language-switch button").forEach((item) => item.classList.toggle("is-active", item === button)); renderCurrent(); }));
  $("#code-editor").addEventListener("input", () => { if (!state.current) return; localStorage.setItem(draftKey(), $("#code-editor").value); $("#draft-status").textContent = t("savedLocally"); });
  $("#code-editor").addEventListener("keydown", (event) => { if (event.key !== "Tab") return; event.preventDefault(); const field = event.currentTarget; const before = field.value.slice(0, field.selectionStart); const after = field.value.slice(field.selectionEnd); const insert = event.shiftKey ? "" : "    "; field.value = before + insert + after; field.selectionStart = field.selectionEnd = before.length + insert.length; field.dispatchEvent(new Event("input")); });
  $("#mark-review").addEventListener("click", () => { localStorage.setItem(progressKey(), "review"); renderCurrent(false); });
  $("#mark-mastered").addEventListener("click", () => { localStorage.setItem(progressKey(), "mastered"); renderCurrent(false); });
  $("#run-code").addEventListener("click", executePython);
  $("#run-review").addEventListener("click", async () => { if (state.mode === "algorithm") await executePython(); await askJarvis(state.locale === "zh" ? "请结合当前代码和运行结果，只提示下一步。" : "Review the current code and run result, then hint only the next step."); });
  $("#jarvis-launcher").addEventListener("click", () => { const panel = $("#jarvis-panel"); panel.hidden = !panel.hidden; $("#jarvis-launcher").setAttribute("aria-expanded", String(!panel.hidden)); });
  $("#hide-mentor").addEventListener("click", () => { $("#jarvis-panel").hidden = true; $("#jarvis-launcher").setAttribute("aria-expanded", "false"); });
  $("#chat-form").addEventListener("submit", async (event) => { event.preventDefault(); const field = $("#message"); const value = field.value; field.value = ""; await askJarvis(value); });
  setupSplitter(); setupMentorDrag();
}

async function initialize() {
  applyPreferences(); bindEvents();
  await Promise.all([loadCatalog(), diagnoseOllama()]);
}

document.addEventListener("DOMContentLoaded", initialize);
