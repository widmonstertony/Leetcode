"use strict";

const copy = {
  en: {
    privacy: "REAL LOCAL APP · NO SERVER RELAY", language: "Language", theme: "Theme", practiceLabel: "SOURCE CURRICULUM",
    practiceMode: "Practice mode", mission: "Mission", openOriginal: "Open original problem ↗", objective: "This round",
    importProblem: "Import any LeetCode problem from this computer", problemUrl: "LeetCode URL or slug", import: "Import statement & starter",
    realRunner: "Real constrained Python runner", methodName: "Solution method", testCases: "JSON test cases", runCode: "Run on this computer",
    editorLabel: "LOCAL SOURCE WORKSPACE", localDraft: "DRAFT STAYS LOCAL", editorAria: "Python solution or system design notes",
    saveSource: "Save to cloned repository", review: "Ask JARVIS to review", mentorLabel: "LOCAL AI MENTOR",
    opening: "Start the local companion, choose a source mission, and I will coach against your real code and test output.",
    replyLabel: "Your reasoning", replyPlaceholder: "Explain your next step…", send: "Send to local JARVIS",
    connectionEyebrow: "LOCAL COMPANION", connectionTitle: "Connect the hosted UI to the source on this computer.",
    connectionBody: "The companion imports LeetTutor’s real curriculum, code runner and problem client, and forwards model requests only to loopback Ollama or LM Studio. EC2 never receives code, prompts or responses.",
    bridgeUrl: "Fixed loopback address", model: "Local model", test: "Connect local app", notConnected: "Local companion is not connected.",
    setup: "Start the real local app", setup1: "Clone or update the Leetcode repository, then start Ollama or LM Studio.", setup3: "Return here and choose “Connect local app.”", source: "Get source and companion ↗",
    connected: "Connected: {problems} algorithm missions, {systems} system-design missions, {models} local model(s).",
    failed: "Local companion unavailable. Run python3 scripts/browser_bridge.py in this repository, then retry.",
    loading: "Loading from this computer…", imported: "Imported {title} through the local source client.", importFailed: "Could not import this problem locally.",
    ready: "READY", running: "RUNNING", passed: "PASSED", failedRun: "FAILED", saved: "Saved as {path} on this computer.", saveFailed: "Could not save; an existing solution is never overwritten automatically.",
    thinking: "JARVIS is thinking on your computer…", coldStart: "The local model is still loading on this computer; the first answer can take longer.", requestFailed: "The local model did not answer. Check the companion terminal and selected model.",
    empty: "Explain your reasoning or ask a question first.", progress: "Progress: {status} · {attempts} attempt(s)", newProblem: "Progress: new",
    algorithmMode: "Algorithms", systemMode: "System design", notesLanguage: "Architecture notes · stored locally", codeLanguage: "Python 3 · local constrained process",
  },
  zh: {
    privacy: "真实本机应用 · 不经过服务器", language: "语言", theme: "主题", practiceLabel: "源码课程",
    practiceMode: "训练模式", mission: "任务", openOriginal: "打开 LeetCode 原题 ↗", objective: "本轮目标",
    importProblem: "通过本机源码导入任意 LeetCode 题目", problemUrl: "LeetCode 链接或 slug", import: "导入题面与 Starter",
    realRunner: "真实受限 Python 运行器", methodName: "Solution 方法名", testCases: "JSON 测试用例", runCode: "在这台电脑运行",
    editorLabel: "本机源码工作区", localDraft: "草稿仅留本机", editorAria: "Python 题解或系统设计笔记",
    saveSource: "保存到本机克隆仓库", review: "让 JARVIS Review", mentorLabel: "本机 AI 导师",
    opening: "启动本机 companion 并选择源码任务后，我会结合你的真实代码和测试结果进行辅导。",
    replyLabel: "你的推理", replyPlaceholder: "解释下一步怎么做…", send: "发送给本机 JARVIS",
    connectionEyebrow: "本机 COMPANION", connectionTitle: "把托管界面连接到这台电脑上的真实源码。",
    connectionBody: "Companion 直接导入 LeetTutor 的真实题库、代码运行器和题目客户端，并且只把模型请求转发给回环地址上的 Ollama 或 LM Studio；EC2 收不到代码、Prompt 或回答。",
    bridgeUrl: "固定回环地址", model: "本机模型", test: "连接本机应用", notConnected: "尚未连接本机 companion。",
    setup: "启动真实本机应用", setup1: "克隆或更新 Leetcode 仓库，然后启动 Ollama 或 LM Studio。", setup3: "回到这里点击“连接本机应用”。", source: "获取源码与 companion ↗",
    connected: "已连接：{problems} 个算法任务、{systems} 个系统设计任务、{models} 个本机模型。",
    failed: "无法连接本机 companion。请在仓库运行 python3 scripts/browser_bridge.py 后重试。",
    loading: "正在从这台电脑加载…", imported: "已通过本机源码客户端导入 {title}。", importFailed: "无法在本机导入这道题。",
    ready: "就绪", running: "运行中", passed: "通过", failedRun: "未通过", saved: "已保存到这台电脑：{path}", saveFailed: "保存失败；现有题解绝不会被自动覆盖。",
    thinking: "JARVIS 正在你的电脑上思考…", coldStart: "本机模型仍在加载；第一次回答可能更慢。", requestFailed: "本机模型没有返回；请检查 companion 终端和所选模型。",
    empty: "请先解释推理或提出问题。", progress: "进度：{status} · 尝试 {attempts} 次", newProblem: "进度：新题",
    algorithmMode: "算法刷题", systemMode: "系统设计", notesLanguage: "架构笔记 · 本机保存", codeLanguage: "Python 3 · 本机受限进程",
  },
};

const state = {
  locale: "en", connected: false, problems: [], systems: [], progress: {}, current: null,
  imported: false, lastRun: null,
};
const $ = (selector) => document.querySelector(selector);

function t(key, values = {}) {
  let value = copy[state.locale][key] || copy.en[key] || key;
  Object.entries(values).forEach(([name, item]) => { value = value.replace(`{${name}}`, String(item)); });
  return value;
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
  document.querySelectorAll("[data-copy]").forEach((node) => { node.textContent = t(node.dataset.copy); });
  document.querySelectorAll("[data-placeholder]").forEach((node) => { node.placeholder = t(node.dataset.placeholder); });
  const algorithmOption = $("#practice-mode option[value='algorithm']");
  const systemOption = $("#practice-mode option[value='system']");
  algorithmOption.textContent = t("algorithmMode");
  systemOption.textContent = t("systemMode");
  if (!state.connected) $("#connection-status").textContent = t("notConnected");
  if (state.current) renderCurrent(false);
}

function bridgeUrl(path) {
  const url = new URL($("#bridge-url").value.trim());
  if (url.protocol !== "http:" || !["127.0.0.1", "localhost", "[::1]"].includes(url.hostname)) {
    throw new Error("The companion must run on this computer.");
  }
  url.pathname = `${url.pathname.replace(/\/+$/, "")}${path}`;
  url.search = "";
  url.hash = "";
  return url.href;
}

async function localApi(path, options = {}) {
  const headers = { Accept: "application/json", ...(options.headers || {}) };
  if (options.body) headers["Content-Type"] = "application/json";
  const response = await fetch(bridgeUrl(path), { ...options, headers, cache: "no-store" });
  let payload;
  try { payload = await response.json(); } catch { payload = { ok: false, error: `HTTP ${response.status}` }; }
  if (!response.ok || payload.ok === false) throw new Error(payload.error || `HTTP ${response.status}`);
  return payload;
}

function setConnected(connected) {
  state.connected = connected;
  $("#connection-dot").classList.toggle("is-live", connected);
  document.body.classList.toggle("is-connected", connected);
}

async function testConnection() {
  const status = $("#connection-status");
  status.textContent = t("loading");
  try {
    const [catalog, progressPayload] = await Promise.all([
      localApi("/api/catalog"), localApi("/api/progress"),
    ]);
    state.problems = catalog.problems || [];
    state.systems = catalog.system_design || [];
    state.progress = progressPayload.progress || {};
    let modelPayload = { data: [] };
    try { modelPayload = await localApi("/v1/models"); } catch (_error) { /* Code practice still works without a loaded model. */ }
    const models = Array.isArray(modelPayload.data) ? modelPayload.data : Array.isArray(modelPayload.models) ? modelPayload.models : [];
    const select = $("#model");
    const previous = localStorage.getItem("leettutor-model") || "";
    select.replaceChildren();
    models.forEach((item) => {
      const value = item.id || item.name;
      if (!value) return;
      const option = document.createElement("option");
      option.value = value; option.textContent = value; select.append(option);
    });
    if (previous && [...select.options].some((option) => option.value === previous)) select.value = previous;
    setConnected(true);
    status.textContent = t("connected", { problems: state.problems.length, systems: state.systems.length, models: models.length });
    populateMissions();
  } catch (_error) {
    setConnected(false);
    status.textContent = t("failed");
  }
}

function currentKey() {
  if (!state.current) return "none";
  const mode = $("#practice-mode").value;
  return `${mode}:${state.current.id || state.current.frontend_id || state.current.slug}`;
}

function draftKey() { return `leettutor-draft:${currentKey()}`; }

function defaultAlgorithmSource(problem) {
  if (Number(problem.id || problem.frontend_id) === 704 || problem.slug === "binary-search") {
    return `class Solution:\n    def search(self, nums, target):\n        left, right = 0, len(nums) - 1\n        while left <= right:\n            mid = left + (right - left) // 2\n            if nums[mid] == target:\n                return mid\n            # Preserve the binary-search invariant here.\n        return -1`;
  }
  return problem.starter_code || "class Solution:\n    pass\n";
}

function defaultTests(problem) {
  if (problem.sample_cases) return problem.sample_cases;
  if (Number(problem.id || problem.frontend_id) === 704 || problem.slug === "binary-search") {
    return '[\n  {"args":[[-1,0,3,5,9,12],9],"expected":4},\n  {"args":[[-1,0,3,5,9,12],2],"expected":-1}\n]';
  }
  return '[\n  {"args":[],"expected":null}\n]';
}

function safeSlug(value) {
  return String(value || "solution").toLowerCase().replace(/[^a-z0-9.-]+/g, "-").replace(/^-+|-+$/g, "") || "solution";
}

function renderProgress() {
  if (!state.current || $("#practice-mode").value !== "algorithm") {
    $("#progress-status").textContent = "";
    return;
  }
  const id = String(state.current.id || state.current.frontend_id || "");
  const entry = state.progress[id];
  $("#progress-status").textContent = entry
    ? t("progress", { status: entry.status, attempts: entry.attempts || 0 })
    : t("newProblem");
}

function renderCurrent(resetEditor = true) {
  const problem = state.current;
  if (!problem) return;
  const mode = $("#practice-mode").value;
  const algorithm = mode === "algorithm";
  const title = algorithm
    ? `${problem.id || problem.frontend_id || ""}. ${state.locale === "zh" ? (problem.title_cn || problem.title) : problem.title}`
    : `${problem.id} · ${state.locale === "zh" ? problem.title_cn : problem.title}`;
  $("#problem-title").textContent = title;
  $("#difficulty").textContent = problem.difficulty || "LOCAL";
  $("#problem-statement").textContent = algorithm
    ? (problem.statement || problem.focus || "")
    : (state.locale === "zh" ? problem.requirement_cn : problem.requirement);
  $("#objective").textContent = algorithm
    ? (problem.invariant_prompt || (problem.hints || [])[0] || "")
    : (state.locale === "zh" ? problem.first_question_cn : problem.first_question);
  $("#problem-link").hidden = !algorithm;
  $("#problem-link").href = algorithm ? (problem.url || `https://leetcode.com/problems/${problem.slug}/`) : "#";
  $("#runner-card").hidden = !algorithm;
  $(".import-card").hidden = !algorithm;
  $("#editor-language").textContent = algorithm ? t("codeLanguage") : t("notesLanguage");
  const filename = algorithm
    ? `${problem.id || problem.frontend_id || "0"}.${safeSlug(problem.slug || problem.title)}.py`
    : `${problem.id}.architecture-notes.md`;
  $("#editor-filename").textContent = filename;
  if (algorithm) {
    $("#method-name").value = problem.method_name || (problem.slug === "binary-search" ? "search" : "");
    $("#test-cases").value = defaultTests(problem);
  }
  if (resetEditor) {
    const saved = localStorage.getItem(draftKey());
    $("#code").value = saved || (algorithm
      ? defaultAlgorithmSource(problem)
      : `# ${title}\n\n## Requirements and estimates\n\n## API and data model\n\n## Architecture\n\n## Reliability and trade-offs\n`);
  }
  state.lastRun = null;
  $("#run-result").textContent = "";
  $("#run-status").textContent = t("ready");
  renderProgress();
}

function populateMissions() {
  const mode = $("#practice-mode").value;
  const items = mode === "algorithm" ? state.problems : state.systems;
  const select = $("#problem-select");
  const previous = state.current?.id || "";
  select.replaceChildren();
  items.forEach((item) => {
    const option = document.createElement("option");
    option.value = String(item.id);
    const title = state.locale === "zh" ? (item.title_cn || item.title) : item.title;
    option.textContent = `${item.id} · ${title}`;
    select.append(option);
  });
  state.current = items.find((item) => String(item.id) === String(previous)) || items[0] || null;
  if (state.current) { select.value = String(state.current.id); renderCurrent(); }
}

async function importProblem() {
  const status = $("#import-status");
  status.textContent = t("loading");
  try {
    const payload = await localApi("/api/problems/import", {
      method: "POST",
      body: JSON.stringify({ reference: $("#problem-reference").value, locale: state.locale }),
    });
    state.current = payload.problem;
    state.imported = true;
    renderCurrent();
    status.textContent = t("imported", { title: payload.problem.title });
  } catch (error) {
    status.textContent = `${t("importFailed")} ${error.message}`;
  }
}

async function updateProgress(status) {
  if (!state.current?.id || state.imported) return;
  try {
    const payload = await localApi("/api/progress", {
      method: "POST", body: JSON.stringify({ problem_id: state.current.id, status }),
    });
    state.progress = payload.progress || state.progress;
    renderProgress();
  } catch (_error) { /* Progress never blocks code execution. */ }
}

async function runCode() {
  if (!state.connected) return testConnection();
  $("#run-status").textContent = t("running");
  $("#run-result").textContent = t("loading");
  await updateProgress("in_progress");
  try {
    const payload = await localApi("/api/code/run", {
      method: "POST",
      body: JSON.stringify({
        source: $("#code").value, method_name: $("#method-name").value,
        test_cases: $("#test-cases").value, timeout_seconds: 3,
      }),
    });
    state.lastRun = payload.result;
    const result = payload.result;
    $("#run-status").textContent = result.status === "passed" ? t("passed") : t("failedRun");
    $("#run-result").textContent = [
      result.summary,
      ...(result.cases || []).map((item) => `#${item.case} ${item.passed ? "✓" : "✗"} actual=${JSON.stringify(item.actual)} expected=${JSON.stringify(item.expected)}`),
      result.stdout || "", result.stderr || "",
    ].filter(Boolean).join("\n");
    if (result.status === "passed") await updateProgress("mastered");
  } catch (error) {
    $("#run-status").textContent = t("failedRun");
    $("#run-result").textContent = error.message;
  }
}

async function saveSource() {
  if ($("#practice-mode").value === "system") {
    localStorage.setItem(draftKey(), $("#code").value);
    $("#progress-status").textContent = t("saved", { path: "browser local storage" });
    return;
  }
  const filename = $("#editor-filename").textContent;
  try {
    const payload = await localApi("/api/solutions/save", {
      method: "POST",
      body: JSON.stringify({ language: "Python", filename, content: $("#code").value, overwrite: false }),
    });
    $("#progress-status").textContent = t("saved", { path: payload.path });
  } catch (error) {
    $("#progress-status").textContent = `${t("saveFailed")} ${error.message}`;
  }
}

function appendMessage(role, message) {
  const article = document.createElement("article"); article.className = `message ${role}`;
  const badge = document.createElement("span"); badge.textContent = role === "assistant" ? "J" : "YOU";
  const paragraph = document.createElement("p"); paragraph.textContent = message;
  article.append(badge, paragraph); $("#messages").append(article);
  article.scrollIntoView({ behavior: "smooth", block: "nearest" });
  return article;
}

async function askJarvis(message) {
  if (!message.trim()) return;
  if (!state.connected || !$("#model").value) {
    appendMessage("assistant", t("failed"));
    return;
  }
  appendMessage("user", message.trim());
  const pending = appendMessage("assistant", t("thinking"));
  const coldStartTimer = window.setTimeout(() => {
    pending.querySelector("p").textContent = t("coldStart");
  }, 12_000);
  const mode = $("#practice-mode").value;
  try {
    const response = await fetch(bridgeUrl("/v1/chat/completions"), {
      method: "POST", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        model: $("#model").value, stream: false, temperature: 0.2, top_p: 0.9, max_tokens: 512,
        messages: [
          { role: "system", content: `You are JARVIS, a concise Socratic ${mode === "algorithm" ? "algorithm" : "system-design"} mentor. Reply in ${state.locale === "zh" ? "Chinese" : "English"}. Inspect the supplied work and test result. Give one specific observation, one short hint, and exactly one next question. Do not invent execution results.` },
          { role: "user", content: `Mission: ${$("#problem-title").textContent}\nRequirement: ${$("#problem-statement").textContent}\nObjective: ${$("#objective").textContent}\nCurrent work:\n${$("#code").value}\nLast local run: ${JSON.stringify(state.lastRun)}\n\nStudent: ${message.trim()}` },
        ],
      }),
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const payload = await response.json();
    window.clearTimeout(coldStartTimer);
    pending.querySelector("p").textContent = payload.choices?.[0]?.message?.content || t("requestFailed");
  } catch (_error) {
    window.clearTimeout(coldStartTimer);
    pending.querySelector("p").textContent = t("requestFailed");
  }
}

$("#language").addEventListener("change", (event) => { localStorage.setItem("leettutor-language", event.target.value); applyPreferences(); populateMissions(); });
$("#theme").addEventListener("change", (event) => { localStorage.setItem("leettutor-theme", event.target.value); applyPreferences(); });
matchMedia("(prefers-color-scheme: dark)").addEventListener("change", applyPreferences);
$("#model").addEventListener("change", (event) => localStorage.setItem("leettutor-model", event.target.value));
$("#test-connection").addEventListener("click", testConnection);
$("#practice-mode").addEventListener("change", () => { state.imported = false; state.current = null; populateMissions(); });
$("#problem-select").addEventListener("change", (event) => {
  const items = $("#practice-mode").value === "algorithm" ? state.problems : state.systems;
  state.current = items.find((item) => String(item.id) === event.target.value) || null;
  state.imported = false; renderCurrent();
});
$("#import-problem").addEventListener("click", importProblem);
$("#run-code").addEventListener("click", runCode);
$("#save-source").addEventListener("click", saveSource);
$("#review-code").addEventListener("click", () => askJarvis(state.locale === "zh" ? "请根据当前真实代码和测试结果 Review，只提示下一步。" : "Review the current real code and test result, then hint only the next step."));
$("#code").addEventListener("input", () => { if (state.current) localStorage.setItem(draftKey(), $("#code").value); });
$("#chat-form").addEventListener("submit", (event) => {
  event.preventDefault(); const field = $("#message");
  if (!field.value.trim()) { field.setCustomValidity(t("empty")); field.reportValidity(); field.setCustomValidity(""); return; }
  askJarvis(field.value); field.value = "";
});

applyPreferences();
testConnection();
