#!/bin/zsh

cd -- "${0:A:h}" || exit 1

if command -v python3 >/dev/null 2>&1; then
  python3 scripts/launch.py
else
  echo "LeetTutor 需要 Python 3.10 或更高版本。"
  exit 1
fi

status=$?
if [ "$status" -ne 0 ]; then
  echo
  read "reply?启动失败。按回车键关闭窗口…"
fi
exit "$status"
