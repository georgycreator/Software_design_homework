@echo off
chcp 65001 > nul
setlocal
cd /d "%~dp0"

call build.bat < nul
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp target\classes ru.vyshkat.app.Main
exit /b %errorlevel%
