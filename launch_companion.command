#!/usr/bin/env bash

set -Eeuo pipefail

project_dir="$(cd "$(dirname "$0")" && pwd)"
cd "$project_dir"

if [[ -x .venv/bin/python ]]; then
  exec .venv/bin/python scripts/launch.py --hosted
fi

exec python3 scripts/launch.py --hosted
