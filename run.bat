@echo off
setlocal
cd /d "%~dp0"

where py >nul 2>nul
if not errorlevel 1 (
  py -3 -c "import sys; sys.exit(0 if sys.version_info >= (3, 10) else 1)" >nul 2>nul
  if not errorlevel 1 (
    py -3 scripts\launch.py
    goto :finished
  )

  echo [LeetTutor] ERROR: The Python launcher was found, but Python 3.10 or newer is unavailable.
  echo [LeetTutor] Installed Python runtimes:
  py -0p
  goto :failed
)

where python >nul 2>nul
if errorlevel 1 goto :python_not_found

python -c "import sys; sys.exit(0 if sys.version_info >= (3, 10) else 1)" >nul 2>nul
if errorlevel 1 (
  echo [LeetTutor] ERROR: The "python" command is unavailable, is a Windows Store alias, or is older than Python 3.10.
  echo [LeetTutor] Resolved command:
  where python
  python --version
  goto :failed
)

python scripts\launch.py

:finished
set "LEETTUTOR_EXIT_CODE=%errorlevel%"
if "%LEETTUTOR_EXIT_CODE%"=="0" goto :end

echo [LeetTutor] ERROR: Launcher exited with code %LEETTUTOR_EXIT_CODE%.
goto :failed_with_code

:python_not_found
echo [LeetTutor] ERROR: Python 3.10 or newer was not found on PATH.
echo [LeetTutor] Install it with: winget install -e --id Python.Python.3.14

:failed
set "LEETTUTOR_EXIT_CODE=1"

:failed_with_code
echo.
echo Press any key to close this window...
pause >nul

:end
endlocal & exit /b %LEETTUTOR_EXIT_CODE%
