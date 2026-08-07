const copy = {
  en: {
    privacy: 'LOCAL MODEL · NO SERVER RELAY', language: 'Language', theme: 'Theme', problemLabel: 'ALGORITHM PRACTICE', problemTitle: 'Two Sum', easy: 'EASY',
    problemBody: 'Given an integer array nums and a target, return the indices of the two numbers whose sum equals the target.', example: 'Example', objective: 'This round',
    objectiveBody: 'Explain what information the hash map stores before writing the final loop.', trace: 'Complexity trace', runTrace: 'Run source example', editorLabel: 'PYTHON WORKSPACE',
    localDraft: 'DRAFT STAYS LOCAL', editorAria: 'Python solution', review: 'Ask JARVIS to review', mentorLabel: 'LOCAL AI MENTOR',
    opening: 'Before coding, what value would let you know that the current number completes a pair you have already seen?', replyLabel: 'Your reasoning', replyPlaceholder: 'Explain your next step…', send: 'Send to local JARVIS',
    connectionEyebrow: 'PRIVATE CONNECTION', connectionTitle: 'Connect this page to the model on your computer.', connectionBody: 'The bridge listens only on 127.0.0.1 and forwards directly to Ollama or LM Studio. EC2 never sees your code or conversation.',
    bridgeUrl: 'Bridge URL', model: 'Model', test: 'Test local connection', notConnected: 'Not connected yet.', setup: 'One-minute setup', setup1: 'Start Ollama (or LM Studio local server) and load a model.', setup3: 'Return here and choose “Test local connection.”', source: 'Get the source and bridge ↗',
    connected: 'Connected to {count} local model(s).', failed: 'Local bridge unavailable. Start it on this computer, then retry.', thinking: 'JARVIS is thinking on your computer…', empty: 'Explain your reasoning or ask a question first.',
    requestFailed: 'The local model did not answer. Check the bridge terminal and selected model.', traceSteps: ['Read 2; need 7; store 2 → 0', 'Read 7; need 2; found index 0', 'Return [0, 1] without scanning the rest'], traceIdle: 'Ready', traceDone: '3 lookups · O(n) time · O(n) space',
  },
  zh: {
    privacy: '本机模型 · 不经过服务器', language: '语言', theme: '主题', problemLabel: '算法训练', problemTitle: '两数之和', easy: '简单',
    problemBody: '给定整数数组 nums 和目标值，返回两个相加等于目标值的数字下标。', example: '示例', objective: '本轮目标', objectiveBody: '先说清楚哈希表保存什么信息，再补完整循环。',
    trace: '复杂度跟踪', runTrace: '运行源码示例', editorLabel: 'PYTHON 工作区', localDraft: '草稿仅留本机', editorAria: 'Python 解题代码', review: '让 JARVIS Review', mentorLabel: '本机 AI 导师',
    opening: '先不急着写代码：当前数字需要哪个值，才能与之前见过的数字组成目标和？', replyLabel: '你的推理', replyPlaceholder: '解释下一步怎么做…', send: '发送给本机 JARVIS',
    connectionEyebrow: '隐私连接', connectionTitle: '把这个网页连接到你电脑上的模型。', connectionBody: '桥接器只监听 127.0.0.1，并直接转发给 Ollama 或 LM Studio；EC2 看不到你的代码和对话。',
    bridgeUrl: '桥接地址', model: '模型', test: '测试本机连接', notConnected: '尚未连接。', setup: '一分钟设置', setup1: '启动 Ollama（或 LM Studio 本地服务）并加载模型。', setup3: '回到这里，点击“测试本机连接”。', source: '获取源码和桥接器 ↗',
    connected: '已连接到 {count} 个本机模型。', failed: '无法连接本机桥接器。请先在这台电脑启动它。', thinking: 'JARVIS 正在你的电脑上思考…', empty: '请先写下推理或问题。', requestFailed: '本机模型没有返回；请检查桥接器终端和模型名。',
    traceSteps: ['读取 2；需要 7；保存 2 → 0', '读取 7；需要 2；找到下标 0', '返回 [0, 1]，无需继续扫描'], traceIdle: '就绪', traceDone: '3 次查找 · 时间 O(n) · 空间 O(n)',
  },
};

const state = { locale: 'en', connected: false };
const $ = (selector) => document.querySelector(selector);

function detectedLocale() {
  return navigator.languages?.some((value) => value.toLowerCase().startsWith('zh')) ? 'zh' : 'en';
}

function applyPreferences() {
  const language = localStorage.getItem('leettutor-demo-language') || 'system';
  const theme = localStorage.getItem('leettutor-demo-theme') || 'system';
  state.locale = language === 'system' ? detectedLocale() : language;
  const resolvedTheme = theme === 'system' ? (matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light') : theme;
  document.documentElement.lang = state.locale === 'zh' ? 'zh-CN' : 'en';
  document.documentElement.dataset.theme = resolvedTheme;
  $('#language').value = language;
  $('#theme').value = theme;
  document.querySelectorAll('[data-copy]').forEach((node) => { node.textContent = copy[state.locale][node.dataset.copy]; });
  document.querySelectorAll('[data-placeholder]').forEach((node) => { node.placeholder = copy[state.locale][node.dataset.placeholder]; });
  if (!state.connected) $('#connection-status').textContent = copy[state.locale].notConnected;
}

function bridgeUrl(path) {
  const url = new URL($('#bridge-url').value.trim());
  if (url.protocol !== 'http:' || !['127.0.0.1', 'localhost', '[::1]'].includes(url.hostname)) {
    throw new Error('The model bridge must run on this computer.');
  }
  url.pathname = `${url.pathname.replace(/\/+$/, '')}${path}`;
  url.search = '';
  url.hash = '';
  return url.href;
}

async function testConnection() {
  const status = $('#connection-status');
  status.textContent = '…';
  try {
    const response = await fetch(bridgeUrl('/v1/models'));
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const payload = await response.json();
    const models = Array.isArray(payload.data) ? payload.data : Array.isArray(payload.models) ? payload.models : [];
    if (models[0]?.id || models[0]?.name) $('#model').value = models[0].id || models[0].name;
    state.connected = true;
    $('#connection-dot').classList.add('is-live');
    status.textContent = copy[state.locale].connected.replace('{count}', String(models.length));
  } catch (_error) {
    state.connected = false;
    $('#connection-dot').classList.remove('is-live');
    status.textContent = copy[state.locale].failed;
  }
}

function appendMessage(role, message) {
  const article = document.createElement('article');
  article.className = `message ${role}`;
  const badge = document.createElement('span');
  badge.textContent = role === 'assistant' ? 'J' : 'YOU';
  const paragraph = document.createElement('p');
  paragraph.textContent = message;
  article.append(badge, paragraph);
  $('#messages').append(article);
  article.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  return article;
}

async function askJarvis(message) {
  if (!message.trim()) return;
  appendMessage('user', message.trim());
  const pending = appendMessage('assistant', copy[state.locale].thinking);
  try {
    const response = await fetch(bridgeUrl('/v1/chat/completions'), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        model: $('#model').value.trim(), stream: false, temperature: 0.2,
        messages: [
          { role: 'system', content: `You are JARVIS, a concise Socratic algorithm mentor. Reply in ${state.locale === 'zh' ? 'Chinese' : 'English'}. Give one short hint and ask exactly one next question. Never provide the complete solution unless explicitly requested.` },
          { role: 'user', content: `Problem: Two Sum.\nCurrent code:\n${$('#code').value}\n\nStudent: ${message.trim()}` },
        ],
      }),
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const payload = await response.json();
    pending.querySelector('p').textContent = payload.choices?.[0]?.message?.content || copy[state.locale].requestFailed;
    state.connected = true;
    $('#connection-dot').classList.add('is-live');
  } catch (_error) {
    pending.querySelector('p').textContent = copy[state.locale].requestFailed;
  }
}

function runTrace() {
  const target = $('#trace-steps');
  target.replaceChildren();
  $('#trace-status').textContent = copy[state.locale].traceIdle;
  copy[state.locale].traceSteps.forEach((step, index) => {
    setTimeout(() => {
      const item = document.createElement('p');
      const number = document.createElement('span');
      number.textContent = String(index + 1);
      item.append(number, document.createTextNode(step));
      target.append(item);
      if (index === copy[state.locale].traceSteps.length - 1) $('#trace-status').textContent = copy[state.locale].traceDone;
    }, index * 420);
  });
}

$('#language').addEventListener('change', (event) => { localStorage.setItem('leettutor-demo-language', event.target.value); applyPreferences(); });
$('#theme').addEventListener('change', (event) => { localStorage.setItem('leettutor-demo-theme', event.target.value); applyPreferences(); });
matchMedia('(prefers-color-scheme: dark)').addEventListener('change', applyPreferences);
$('#test-connection').addEventListener('click', testConnection);
$('#run-trace').addEventListener('click', runTrace);
$('#review-code').addEventListener('click', () => askJarvis(state.locale === 'zh' ? '请 Review 当前代码，只给一个提示并问我一个问题。' : 'Review my current code. Give one hint and ask one question.'));
$('#chat-form').addEventListener('submit', (event) => {
  event.preventDefault();
  const field = $('#message');
  if (!field.value.trim()) {
    field.setCustomValidity(copy[state.locale].empty);
    field.reportValidity();
    field.setCustomValidity('');
    return;
  }
  askJarvis(field.value);
  field.value = '';
});
applyPreferences();
