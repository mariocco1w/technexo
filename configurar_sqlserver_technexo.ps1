# =========================================================
# TechNexo - Configuracion de SQL Server para el backend
# Requiere ejecutarse como Administrador.
#
# Acciones:
#   1. Habilita TCP/IP en SQLEXPRESS con puerto fijo 1433.
#   2. Habilita autenticacion mixta (Windows + SQL).
#   3. Reinicia el servicio MSSQL$SQLEXPRESS.
#   4. Verifica la conexion del login de aplicacion.
#
# No modifica datos ni estructura de la base de datos.
# =========================================================

$ErrorActionPreference = "Stop"

$instance  = "MSSQL17.SQLEXPRESS"
$regBase   = "HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\$instance\MSSQLServer"
$tcp       = "$regBase\SuperSocketNetLib\Tcp"
$svc       = "MSSQL`$SQLEXPRESS"
$dbLogin   = "technexo_app"
$dbPass    = "TechNexo#2026"
$database  = "TechNexoDB"

Write-Host "== 1. Configurando TCP/IP (puerto 1433) =="
Set-ItemProperty -Path $tcp -Name "Enabled" -Value 1 -Type DWord
Set-ItemProperty -Path "$tcp\IPAll" -Name "TcpDynamicPorts" -Value ""
Set-ItemProperty -Path "$tcp\IPAll" -Name "TcpPort" -Value "1433"

Write-Host "== 2. Habilitando autenticacion mixta (LoginMode = 2) =="
Set-ItemProperty -Path $regBase -Name "LoginMode" -Value 2 -Type DWord

Write-Host "== 3. Reiniciando servicio $svc =="
Restart-Service -Name $svc -Force
Start-Sleep -Seconds 10

Write-Host "== 4. Verificando conexion por TCP =="
$check = sqlcmd -S "localhost,1433" -U $dbLogin -P $dbPass -d $database -Q "SELECT SUSER_SNAME() AS login_ok, DB_NAME() AS db_ok" -h -1 -W
Write-Host $check
Write-Host "Configuracion finalizada. Si aparece 'login_ok' y '$database', el backend puede conectarse."
