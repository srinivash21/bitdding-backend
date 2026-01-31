@echo off
setlocal

echo.
echo === BID BACKEND ===
echo Working dir: %~dp0
echo.

cd /d "%~dp0" || exit /b 1

REM Use the repo-local Maven to avoid requiring mvn on PATH
set "MAVEN_CMD=%~dp0.tools\apache-maven-3.9.12\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
  echo ERROR: Repo-local Maven not found at:
  echo   %MAVEN_CMD%
  echo.
  echo Fix: ensure the .tools folder is included in the zip.
  exit /b 1
)

echo Starting Spring Boot on http://localhost:8080 ...
echo Press Ctrl+C to stop.
echo.

call "%MAVEN_CMD%" spring-boot:run
