#!/bin/zsh

set -e
SCRIPT_DIR="${0:A:h}"
cd "$SCRIPT_DIR"

echo "[LeetTutor] 正在启动局域网主机模式…"
python3 scripts/launch.py --lan
