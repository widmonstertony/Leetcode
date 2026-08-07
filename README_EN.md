# LeetTutor — JARVIS Learning System

[中文说明](README.md)

LeetTutor is a local-first AI learning workspace for LeetCode and system design. Its JARVIS mentor chooses the next exercise from your weak areas, reads the live problem/code/test context, and teaches through short Socratic prompts instead of dumping an answer. You can run Python solutions in the browser, use either local or cloud models, and continue on a phone through a trusted home-network host.

## Hosted entry + original app/model on your computer

[tonytan.me/leetcode/](https://tonytan.me/leetcode/) no longer maintains a separate simplified workspace. After the local app starts, the site loads the repository's original `app.py` in the current tab. The floating JARVIS, split workspace, Ace editor, progress, model settings, and mobile navigation therefore share the same Streamlit source as the directly launched product.

```bash
./launch_companion.command
```

The browser entry remains `https://tonytan.me/leetcode/`. Start Ollama (or LM Studio), run `python3 scripts/launch.py --hosted`, return to the site, and choose “Open original LeetTutor.” Hosted mode binds the original Streamlit app only to `127.0.0.1:8501` and does not open a second local tab. Code, tests, progress, prompts, model names, and responses never pass through EC2. If Chrome local-network permission is unavailable, open `http://127.0.0.1:8501/` as the direct fallback.

`scripts/browser_bridge.py` remains available for compatibility and API diagnostics, but it no longer provides the primary product UI.

## Highlights

- Mentor-led Algorithm practice for binary search, stacks, heaps, DP, and more.
- A browser IDE with Ace editing, Python execution, test cases, solution persistence, and code review.
- JARVIS answers in small turns: one useful hint and one next question by default. It provides a complete implementation only when you explicitly ask for the optimal solution code.
- System-design coaching that begins with capacity estimates and then pressure-tests SPOFs, cache failure modes, concurrency, and consistency. Mermaid diagrams render in the app.
- Local providers: Ollama, LM Studio, and the experimental AMD Metal backend for supported Intel Macs with Radeon GPUs.
- Cloud providers: OpenAI API and Gemini API.
- Desktop, mobile, and trusted LAN-host workflows. The UI supports system/light/dark themes and Chinese/English LeetCode descriptions.

## Quick start

Install Python 3.10 or newer. Ollama and models can be installed later from the LeetTutor settings panel.

- macOS: double-click `run.command`.
- Windows: double-click `run.bat`.
- Terminal: `python3 scripts/launch.py`.
- Start and open the repository in VS Code: `python3 scripts/launch.py --vscode`.

The first run creates `.venv`, installs dependencies, and opens the browser. Later launches only reinstall packages when `requirements.txt` changes.

For the best everyday workflow, write and run solutions in the browser IDE. Open VS Code when you need debugger support, larger refactors, or Git operations.

## Host mode: continue on your phone

Run the app on any computer that stays on your trusted local network, then open it from a phone or tablet connected to the same Wi-Fi.

- macOS: double-click `run-lan.command`.
- Windows: double-click `run-lan.bat`.
- Terminal: `python3 scripts/launch.py --lan`.

The launcher prints a LAN URL, an eight-character access code, and a QR code. Scan the QR code and enter the code once. The browser can remember a host-signed device credential for 30 days, so routine visits do not require entering the code again.

Progress and saved solutions live on the host, so you can resume on another device. Unsaved editor drafts and in-progress chats are browser-session specific; save a solution before switching devices.

The mobile interface is optimized for iPhone 13 Pro and iPhone Air-sized screens. Algorithm mode uses fixed Problem / Code / JARVIS navigation; System Design mode uses Task / Workspace / JARVIS. Do not port-forward this service or expose it to the public internet. See [Host Mode Guide](docs/HOST_MODE.md) for setup and troubleshooting.

## Training modes

### Algorithm mode

- **JARVIS gives me the next problem** automatically selects a problem from your study path and imports the description, Python template, method name, and sample arguments.
- Every round starts with a specific objective and an immediate diagnostic question. The course engine can show the first question before the model responds.
- The mentor evaluates time and space complexity, points out edge cases, and advances one decision at a time.
- Paste a public `leetcode.com/problems/...` or `leetcode.cn/problems/...` link to import the problem and starter code.
- Run a `class Solution` implementation in a constrained child process with a default three-second timeout. It is designed for your own practice code, not as a security sandbox for untrusted code.
- Current problem, code, tests, and the latest execution result are automatically included for Review, “I’m stuck,” and follow-up coaching.
- Drag the divider between Problem and Code to resize the workspace; the ratio is remembered. Double-click it to restore the default.

### System Design mode

- **JARVIS assigns a task** rotates through scalability, reliability, real-time systems, data platforms, and transactional consistency.
- Start from DAU, QPS, peak traffic, and read/write ratio.
- JARVIS then challenges the proposal with at least three pressure tests, including SPOFs, caching, and high-concurrency or consistency failure modes.
- Mermaid blocks returned by the model render as architecture diagrams.

## Model providers

| Provider | Default endpoint | Notes |
| --- | --- | --- |
| Ollama | `http://localhost:11434` | Install a model, then run `ollama serve` when needed. |
| LM Studio | `http://localhost:1234/v1` | Load a model and start its Local Server. |
| AMD Metal (Intel Mac) | `http://127.0.0.1:11435/v1` | Experimental backend for the supported Radeon configuration. |
| OpenAI API | `https://api.openai.com/v1` | Requires an OpenAI Platform API key and API billing. |
| Gemini API | `https://generativelanguage.googleapis.com/v1beta/openai` | Requires a Google AI Studio API key. |

ChatGPT Plus/Pro and Gemini web subscriptions are not API credits. Configure the relevant API key from the sidebar; keys are saved to the Git-ignored `.leettutor/secrets.json` with restrictive permissions and are never displayed on LAN clients.

### Intel Mac + AMD Radeon experimental backend

Standard Ollama on Intel macOS normally runs on CPU. For the validated 16-inch Intel MacBook Pro (`MacBookPro16,4`) with Radeon Pro 5600M 8 GB, the app offers an experimental AMD Metal Provider. The in-app installer checks hardware and build tools, downloads the pinned `llama.cpp` source, applies the included Qwen compatibility patch, builds locally, and serves the supported GGUF through an OpenAI-compatible local endpoint.

This backend is experimental and intended for compatible 8 GB Intel Radeon Macs. It does not apply to Apple Silicon, Boot Camp, or 4 GB Radeon models. Read [Intel MacBook Pro + AMD Radeon Guide](docs/INTEL_AMD_MACBOOK.md) before installing.

## Configuration

Use **Save settings** in the sidebar to write local `config.json`; `.env` can override configuration values:

```bash
cp .env.example .env
```

Supported variables include `LEETTUTOR_PROVIDER`, `LEETTUTOR_MODEL`, `LEETTUTOR_OLLAMA_URL`, `LEETTUTOR_LM_STUDIO_URL`, `LEETTUTOR_AMD_METAL_URL`, `LEETTUTOR_OPENAI_API_KEY`, `LEETTUTOR_GEMINI_API_KEY`, and `LEETTUTOR_API_KEY`. Standard `OPENAI_API_KEY` and `GEMINI_API_KEY` are also recognized.

## Repository layout

```text
Leetcode/
├── app.py                 # Streamlit entry point
├── assets/                # Original JARVIS visual assets
├── leettutor/             # Mentor, importer, runner, and model adapters
├── scripts/               # Cross-platform launcher and AMD setup utility
├── run.command / .bat     # One-click local launchers
├── run-lan.command / .bat # Trusted LAN-host launchers
├── tests/                 # Tests that do not require a local model
├── python/                # Current Python practice solutions
├── java/                  # Existing and future Java solutions
├── sql/                   # SQL solutions
└── docs/                  # Playbooks, guides, and study notes
```

## Study materials

- [Python practice playbook](docs/PYTHON_PLAYBOOK.md) — binary search, stacks, heaps, DP, and the learning path.
- [Python solutions](python/)
- [Java solutions](java/) — maintained as a live Java track, not an archive.
- [BST notes](docs/BST.md)

The personal binary-search convention used in the playbook is:

```python
while left < right:
    mid = left + (right - left) // 2
    if mid_may_be_answer(mid):
        right = mid
    else:
        left = mid + 1
# inspect left at the end; verify it if an answer may not exist
```

## Development

```bash
python3 -m venv .venv
.venv/bin/python -m pip install -r requirements-dev.txt
.venv/bin/python -m pytest
.venv/bin/python -m streamlit run app.py
```

On Windows, replace `.venv/bin/python` with `.venv\\Scripts\\python.exe`.
