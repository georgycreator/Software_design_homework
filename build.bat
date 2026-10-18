@echo off
setlocal
cd /d "%~dp0"
if exist target\classes rmdir /s /q target\classes
mkdir target\classes
rem -sourcepath сам находит нужные классы; @argfile не используем: javac
rem воспринимает обратные слэши в кавычках как escape-символы (ломает пути Windows).
javac -encoding UTF-8 -d target\classes -sourcepath src\main\java src\main\java\ru\vyshkat\app\Main.java
exit /b %errorlevel%
