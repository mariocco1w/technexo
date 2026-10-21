$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"

function Api-Get($path) {
    (Invoke-WebRequest -Uri "$base$path" -UseBasicParsing -TimeoutSec 20).Content | ConvertFrom-Json
}
function Post-FormHtml($path, $body) {
    (Invoke-WebRequest -Uri "$base$path" -Method Post -Body $body -ContentType "application/x-www-form-urlencoded" -UseBasicParsing -TimeoutSec 20).Content
}

Write-Host "== A) Sobrevender por reserva (999 unidades) =="
$html = Post-FormHtml "/reservas/crear" @{ idProducto = 1; cantidad = 999; identificadorCliente = "WEB-AUTOTEST-OVER" }
$m = [regex]::Match($html, 'alert-danger[^>]*>\s*<span>(.*?)</span>', 'Singleline')
if ($m.Success) { Write-Host ("   Rechazado -> " + $m.Groups[1].Value.Trim()) } else { Write-Host "   (no se encontro alerta de error)"; }
$p = Api-Get "/api/productos/1"
Write-Host ("   disponibilidad intacta -> fisico={0} reservadas={1} disponible={2}" -f $p.stockFisico, $p.cantidadReservada, $p.stockDisponible)

Write-Host ""
Write-Host "== B) Reservar 3 y luego cancelar =="
Post-FormHtml "/reservas/crear" @{ idProducto = 1; cantidad = 3; identificadorCliente = "WEB-AUTOTEST-CANCEL" } | Out-Null
$reservas = Api-Get "/api/reservas"
$r = $reservas | Where-Object { $_.identificadorCliente -eq "WEB-AUTOTEST-CANCEL" } | Select-Object -First 1
$p = Api-Get "/api/productos/1"
Write-Host ("   Reserva id={0} estado={1}; disponible con reserva = {2}" -f $r.id, $r.estado, $p.stockDisponible)
Post-FormHtml "/reservas/$($r.id)/cancelar" @{} | Out-Null
$reservas = Api-Get "/api/reservas"
$r2 = $reservas | Where-Object { $_.id -eq $r.id } | Select-Object -First 1
$p = Api-Get "/api/productos/1"
Write-Host ("   Tras cancelar -> estado={0}, disponible={1}" -f $r2.estado, $p.stockDisponible)

Write-Host ""
Write-Host "== C) Sobrevender por pedido (999 unidades) =="
$html = Post-FormHtml "/pedidos/crear" @{
    nombreCliente = "Cliente Autotest Over"
    correoCliente = "over@technexo.local"
    telefonoCliente = "5555-0001"
    "items[0].idProducto" = 1
    "items[0].cantidad" = 999
}
$m = [regex]::Match($html, 'alert-danger[^>]*>\s*<span>(.*?)</span>', 'Singleline')
if ($m.Success) { Write-Host ("   Rechazado -> " + $m.Groups[1].Value.Trim()) } else { Write-Host "   (no se encontro alerta de error)" }
$p = Api-Get "/api/productos/1"
Write-Host ("   disponibilidad intacta -> fisico={0} reservadas={1} disponible={2}" -f $p.stockFisico, $p.cantidadReservada, $p.stockDisponible)
