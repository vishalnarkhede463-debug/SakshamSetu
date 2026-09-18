@echo off
setlocal
cd /d "%~dp0"

echo ================================================================
echo   GovSkill Connect - Full Project Launcher
echo ================================================================
echo.

rem 1) Start MySQL if XAMPP is installed.
set "MYSQL_STARTED=0"
if exist "%ProgramFiles%\XAMPP\xampp-control.exe" (
  echo [1/3] Starting XAMPP control panel...
  start "XAMPP" "%ProgramFiles%\XAMPP\xampp-control.exe"
  set "MYSQL_STARTED=1"
) else if exist "%ProgramFiles(x86)%\XAMPP\xampp-control.exe" (
  echo [1/3] Starting XAMPP control panel...
  start "XAMPP" "%ProgramFiles(x86)%\XAMPP\xampp-control.exe"
  set "MYSQL_STARTED=1"
) else if exist "C:\xampp\xampp-control.exe" (
  echo [1/3] Starting XAMPP control panel...
  start "XAMPP" "C:\xampp\xampp-control.exe"
  set "MYSQL_STARTED=1"
) else (
  echo [1/3] XAMPP not found - backend will use its built-in seeded in-memory database.
)

rem 2) Compile backend.
echo [2/3] Compiling backend...
call "%~dp0compile.bat"
if errorlevel 1 exit /b 1

rem 3) Start Java server and open the portal.
echo [3/3] Starting Java backend + TEE security layer...
echo.
echo Portal:    http://localhost:8080/
echo TEE:       http://localhost:8080/api/security/tee-status
echo.
echo Keep this window open while using the project.
echo Press Ctrl+C here to stop the server.
echo.

start "GovSkill Connect - Browser" cmd /c "timeout /t 2 /nobreak >nul & start "" "http://localhost:8080/"

java -cp "backend\bin" com.govskill.Main

if errorlevel 1 (
  echo.
  echo [ERROR] Server stopped or could not start.
  echo Check whether Java is installed and port 8080 is free.
  pause
)
