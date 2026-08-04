@echo off
setlocal
cd /d "%~dp0"

where py >nul 2>nul
if %errorlevel% equ 0 (
  py -3 scripts\launch.py
) else (
  python scripts\launch.py
)

if errorlevel 1 (
  echo.
  echo Start failed. Press any key to close this window...
  pause >nul
)
endlocal
