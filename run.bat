 @echo off
setlocal

REM === Configurable section ===
set MAIN_CLASS=Principal
set BIN_DIR=bin

echo Running %MAIN_CLASS%...
java -cp %BIN_DIR% %MAIN_CLASS%

endlocal
pause