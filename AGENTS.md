# LeetTutor maintainer and AI guide

LeetTutor has two supported surfaces:

- the full local Streamlit application, including code execution, progress,
  local/cloud providers, and trusted-LAN host mode;
- the static portfolio workspace in `web-demo`, which can talk to a model only
  through `scripts/browser_bridge.py` on the visitor's own loopback interface.

## Safety invariants

The public portfolio server must never proxy model traffic. The bridge must
bind only to `127.0.0.1`, allow only reviewed origins, expose only `/v1/models`
and `/v1/chat/completions`, accept bounded non-streaming JSON, and forward only
to another loopback URL. Do not store prompts, code, API keys, or responses in
the hosted site. Do not add a public bind flag to the bridge.

Host mode is for a trusted private LAN and is not an Internet deployment.
Preserve its access-code/device-token boundary and the warnings in
`docs/HOST_MODE.md`.

## Change and release flow

Run `python -m pytest`, `python -m compileall -q app.py leettutor scripts tests`,
and `node --check web-demo/app.js`. Branch from protected `master`, open a PR,
and merge after `LeetTutor quality and demo deployment` passes. A merge packages
only `web-demo`, then the repository-scoped runner atomically deploys it to
`https://tonytan.me/leetcode/`. The full Streamlit app and local model are never
installed on EC2.

Shared Caddy, server accounts, sudo rules, and rollback policy live in the
`Personal-Website/ops` repository. Never commit `config.json`, `.env`,
`.leettutor/`, API keys, model files, PEM files, runner tokens, or user progress.
