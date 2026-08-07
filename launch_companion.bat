@echo off
setlocal
cd /d "%~dp0"
if exist ".venv\Scripts\python.exe" (
  ".venv\Scripts\python.exe" scripts\browser_bridge.py
) else (
  py -3 scripts\browser_bridge.py
)
endlocal
