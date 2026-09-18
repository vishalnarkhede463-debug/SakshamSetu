@echo off
setlocal
cd /d "%~dp0"

echo ================================================================
echo   GovSkill Connect - Compiling Java Backend
echo ================================================================

if not exist "backend\src" (
  echo [ERROR] backend\src folder not found.
  pause
  exit /b 1
)

where javac >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Java JDK not found. Install a JDK and add javac to PATH.
  pause
  exit /b 1
)

if not exist "backend\bin" mkdir "backend\bin"

echo [1/2] Cleaning old compiled classes...
del /q /s "backend\bin\*.class" >nul 2>&1

echo [2/2] Compiling backend...
dir /b /s "backend\src\*.java" > "backend\sources.txt"
javac -encoding UTF-8 -d "backend\bin" @"backend\sources.txt"
set ERR=%ERRORLEVEL%
del /q "backend\sources.txt" >nul 2>&1

if not "%ERR%"=="0" (
  echo.
  echo [ERROR] Backend compilation failed.
  pause
  exit /b %ERR%
)

echo.
echo [OK] Backend compiled successfully.
echo.
exit /b 0
