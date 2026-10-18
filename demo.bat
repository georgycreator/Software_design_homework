@echo off
chcp 65001 > nul
setlocal
cd /d "%~dp0"

if not exist examples\demo-input.txt (
    echo Не найден файл examples\demo-input.txt
    exit /b 1
)

echo === ВышКат: повторяемая демонстрация ===
echo Ввод берётся из examples\demo-input.txt; те же действия доступны вручную через run.bat.
echo.

call build.bat < nul
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

rem Перенаправление ввода применяется только к java, а не к вызову батника.
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dvyshkat.echoInput=true -cp target\classes ru.vyshkat.app.Main < "examples\demo-input.txt"
exit /b %errorlevel%
