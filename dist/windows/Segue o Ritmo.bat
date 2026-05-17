@echo off
setlocal

:: ─── Segue o Ritmo — Windows launcher ────────────────────────────────────────
:: Double-click this file to start the game.
:: Requires Java 17+ installed. Download from https://adoptium.net/

set "JAR=%~dp0simon-says.jar"

:: Check that the JAR exists next to this .bat
if not exist "%JAR%" (
    echo.
    echo  ERRO: Ficheiro simon-says.jar nao encontrado.
    echo  Coloca este ficheiro na mesma pasta que simon-says.jar
    echo.
    pause
    exit /b 1
)

:: Check Java is available
where java >nul 2>&1
if errorlevel 1 (
    echo.
    echo  ERRO: Java nao encontrado.
    echo  Instala o Java 17 em: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

:: Launch — start detached so the console window closes immediately
start "" javaw -Xmx512m -Dfile.encoding=UTF-8 -jar "%JAR%"
endlocal
