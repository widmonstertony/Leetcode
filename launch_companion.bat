@echo off
setlocal
cd /d "%~dp0"
if exist ".venv\Scripts\python.exe" (
  ".venv\Scripts\python.exe" scripts\launch.py --hosted
) else (
  py -3 scripts\launch.py --hosted
)
endlocal
