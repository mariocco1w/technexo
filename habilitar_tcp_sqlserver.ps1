# =========================================================
# Habilita TCP/IP en la instancia SQLEXPRESS (puerto fijo 1433)
# Debe ejecutarse como Administrador.
# =========================================================

$ErrorActionPreference = "Stop"

$instance = "MSSQL17.SQLEXPRESS"
$tcp = "HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\$instance\MSSQLServer\SuperSocketNetLib\Tcp"

Write-Host "Habilitando TCP/IP para $instance ..."
Set-ItemProperty -Path $tcp -Name "Enabled" -Value 1 -Type DWord
Set-ItemProperty -Path "$tcp\IPAll" -Name "TcpDynamicPorts" -Value ""
Set-ItemProperty -Path "$tcp\IPAll" -Name "TcpPort" -Value "1433"

Write-Host "Reiniciando servicio MSSQL`$SQLEXPRESS ..."
Restart-Service -Name "MSSQL`$SQLEXPRESS" -Force

Write-Host "Esperando a que SQL Server acepte conexiones ..."
Start-Sleep -Seconds 8

try {
    sqlcmd -S "localhost,1433" -U technexo_app -P "TechNexo#2026" -d TechNexoDB -Q "SELECT 1 AS ok" -W | Out-Null
    Write-Host "RESULTADO: TCP/IP habilitado y conexion verificada en localhost,1433"
} catch {
    Write-Host "RESULTADO: TCP habilitado, pero la prueba de conexion fallo. Verifique el firewall."
}
