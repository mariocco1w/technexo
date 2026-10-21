@echo off
REM ============================================================
REM  TechNexo - Detiene el backend (procesos Java en ejecucion)
REM ============================================================
echo Deteniendo procesos Java de TechNexo...
taskkill /F /IM java.exe >nul 2>&1
if %ERRORLEVEL%==0 (
    echo Backend detenido.
) else (
    echo No habia procesos Java en ejecucion.
)
