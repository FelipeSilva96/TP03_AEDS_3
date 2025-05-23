@echo off
setlocal

REM === Configurable section ===
set SRC_DIR=src
set BIN_DIR=bin
set MAIN_CLASS=Principal
set DADOS_DIR=dados

echo ==================================================
echo Removing 'dados' folder to ensure clean compilation
if exist %DADOS_DIR% (
    rmdir /S /Q %DADOS_DIR%
    echo Folder 'dados' removed.
) else (
    echo Folder 'dados' does not exist. Skipping removal.
)

echo Cleaning old class files...
if exist %BIN_DIR%\*.class del /Q %BIN_DIR%\*.class

echo Compiling Java source files...
if not exist %BIN_DIR% mkdir %BIN_DIR%
javac -d %BIN_DIR% %SRC_DIR%\*.java

if errorlevel 1 (
    echo Compilation failed. Exiting.
    exit /b 1
)

echo Running %MAIN_CLASS%...
java -cp %BIN_DIR% %MAIN_CLASS%

endlocal
pause
