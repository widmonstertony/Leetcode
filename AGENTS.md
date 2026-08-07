# LeetTutor maintainer and AI guide

LeetTutor has one product UI with three supported access paths:

- the original Streamlit application in `app.py`, including code execution,
  progress, local/cloud providers, and the floating JARVIS;
- trusted-LAN host mode for that same application;
- the hosted shell in `web-demo`, which embeds the unchanged original UI from
  `127.0.0.1:8501` after the visitor starts `scripts/launch.py --hosted`.

Do not build or ship a second LeetTutor workspace in `web-demo`. It is only the
bilingual connection shell for the original app. UX changes belong in `app.py`
so local, hosted, and LAN access cannot drift apart again.

## Safety invariants

The public portfolio server must never proxy the Streamlit app or model traffic.
Hosted mode must bind the original app only to `127.0.0.1`; the Caddy boundary
may frame that exact loopback origin but must not expose it publicly. The legacy
browser bridge must retain its loopback bind, reviewed origins, bounded API
allowlists, and loopback-only model upstreams. Code execution must continue to
use `leettutor.code_runner`; solution access must continue to use
`SolutionStore`. Do not store prompts, code, API keys, or responses on EC2.

Host mode is for a trusted private LAN and is not an Internet deployment.
Preserve its access-code/device-token boundary and the warnings in
`docs/HOST_MODE.md`.

## Change and release flow

Run `python -m pytest`, `python -m compileall -q app.py leettutor scripts tests`,
and `node --check web-demo/app.js`. Branch from protected `master`, open a PR,
and merge after `LeetTutor quality and demo deployment` passes. A merge packages
only `web-demo`, then the repository-scoped runner atomically deploys it to
`https://tonytan.me/leetcode/`. The shell contains no replacement workspace.
The Python process, original Streamlit UI, user solutions, progress, and local
model are never installed on EC2.

Shared Caddy, server accounts, sudo rules, and rollback policy live in the
`Personal-Website/ops` repository. Never commit `config.json`, `.env`,
`.leettutor/`, API keys, model files, PEM files, runner tokens, or user progress.
