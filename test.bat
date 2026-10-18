@echo off
chcp 65001 > nul
setlocal
cd /d "%~dp0"
mvn clean verify
exit /b %errorlevel%
