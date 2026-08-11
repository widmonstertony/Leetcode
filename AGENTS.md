# LeetTutor maintainer and AI guide

LeetTutor has one product experience with two maintained implementations:

- the original Streamlit application in `app.py`, including code execution,
  progress, local/cloud providers, and the floating JARVIS;
- trusted-LAN host mode for that same application;
- the browser-hosted application in `web-demo`, which preserves the original
  training controls, split workspace, floating/docked JARVIS, system-design
  mode, and provider settings. It requires no local Python or Streamlit process.

Treat `app.py` as the product reference. A hosted UX change must be checked
against the Streamlit behavior, translated in Chinese and English, and usable
in system/light/dark appearance. Keep floating JARVIS, the problem/code split,
model selection, progress, and mobile behavior aligned even though browser and
Streamlit implementations use different rendering runtimes.

## Safety invariants

The public portfolio server must never proxy model traffic. The browser UI may
call only the explicitly configured provider directly: reviewed loopback ports
for Ollama, LM Studio, and AMD Metal, or the official OpenAI/Gemini HTTPS API.
Caddy must keep an exact `connect-src` allowlist without exposing local ports
publicly. Cloud API keys are session-only and must never enter local storage.
Hosted Python execution stays in a bounded Web Worker via Pyodide, and browser
drafts/progress stay in local storage. The legacy browser bridge must retain its
loopback bind, reviewed origins, bounded API allowlists, and loopback-only model upstreams. The
Streamlit app must continue to use `leettutor.code_runner` and `SolutionStore`.
Do not store prompts, code, API keys, responses, model files, or progress on EC2.

Host mode is for a trusted private LAN and is not an Internet deployment.
Preserve its access-code/device-token boundary and the warnings in
`docs/HOST_MODE.md`.

## Change and release flow

Run `python -m pytest`, `python -m compileall -q app.py leettutor scripts tests`,
`python scripts/export_web_catalog.py`, and Node syntax checks for both browser
JavaScript files. The committed catalog must match the Python curriculum.
Branch from protected `master`, open a PR,
and merge after `LeetTutor quality and demo deployment` passes. A merge packages
only `web-demo`, then the repository-scoped runner atomically deploys it to
`https://tonytan.me/leetcode/`. No Python service, user solution, progress,
prompt, response, or local model is installed on EC2.

Shared Caddy, server accounts, sudo rules, and rollback policy live in the
`Personal-Website/ops` repository. Never commit `config.json`, `.env`,
`.leettutor/`, API keys, model files, PEM files, runner tokens, or user progress.
