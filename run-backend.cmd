@echo off
REM ============================================================
REM  TechNexo - Arranque del backend + frontend (Spring Boot 3)
REM  Modulos: catalogo, reservas, pedidos, inventario
REM  Uso: doble clic o ejecutar  run-backend.cmd
REM ============================================================
setlocal enableextensions

REM --- JDK 21 (Red Hat OpenJDK). Ajuste esta ruta si es necesario. ---
set "JAVA_HOME=C:\Program Files\RedHat\java-21-openjdk-21.0.6.0.7-1"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] No se encontro un JDK valido en:
    echo         %JAVA_HOME%
    echo         Edite la variable JAVA_HOME de este script.
    exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0technexo-backend"

echo ============================================================
echo  TechNexo se iniciara en   http://localhost:8080
echo  Base de datos: TechNexoDB  (localhost,1433)
echo  Detener con Ctrl+C o ejecutando stop-backend.cmd
echo ============================================================
echo.

call mvnw.cmd spring-boot:run
set "EXITCODE=%ERRORLEVEL%"

endlocal & exit /b %EXITCODE%
