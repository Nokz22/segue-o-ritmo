@echo off
:: ─── Build a self-contained Windows .exe with jpackage ────────────────────────
:: Run this script on a Windows machine that has JDK 17+ installed.
:: Output: dist\windows-app\Segue o Ritmo\Segue o Ritmo.exe
::
:: Prerequisite (for --type exe): WiX Toolset 3.x installed
::   https://github.com/wixtoolset/wix3/releases
:: If WiX is not available, change --type exe  to  --type app-image

setlocal
set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."
set "DIST_DIR=%PROJECT_DIR%\dist\windows-app"

cd /d "%PROJECT_DIR%"

:: Build the fat JAR first
call mvn package -q
if errorlevel 1 (
    echo BUILD FAILED — run  mvn package  manually and check errors.
    pause & exit /b 1
)

:: Create the native installer
jpackage ^
  --type exe ^
  --input "%PROJECT_DIR%\target" ^
  --main-jar simon-says.jar ^
  --main-class com.blip.simonSays.Main ^
  --name "Segue o Ritmo" ^
  --app-version "1.0.0" ^
  --vendor "Blip" ^
  --description "Jogo Simon Says para utilizadores com dificuldades cognitivas" ^
  --java-options "-Xmx512m -Dfile.encoding=UTF-8" ^
  --win-shortcut ^
  --win-menu ^
  --win-menu-group "Blip" ^
  --win-dir-chooser ^
  --dest "%DIST_DIR%"

if errorlevel 1 (
    echo.
    echo jpackage falhou.  Se nao tens o WiX Toolset instalado, edita
    echo este script e muda  --type exe  para  --type app-image
    pause & exit /b 1
)

echo.
echo  Instalador criado em: %DIST_DIR%
echo  Copia o ficheiro .exe para as maquinas de destino e executa.
pause
