$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"

function Api-Get($path) {
    (Invoke-WebRequest -Uri "$base$path" -UseBasicParsing -TimeoutSec 20).Content | ConvertFrom-Json
}
function Post-Form($path, $body) {
    Invoke-WebRequest -Uri "$base$path" -Method Post -Body $body -ContentType "application/x-www-form-urlencoded" -UseBasicParsing -TimeoutSec 20 -MaximumRedirection 5 | Out-Null
}

$p = Api-Get "/api/productos/1"
Write-Host ("ANTES  -> stock_fisico={0} reservadas={1} disponible={2}" -f $p.stockFisico, $p.cantidadReservada, $p.stockDisponible)

Write-Host ""
Write-Host "== 1) Crear reserva (2 unidades) =="
Post-Form "/reservas/crear" @{ idProducto = 1; cantidad = 2; identificadorCliente = "WEB-AUTOTEST" }
$reservas = Api-Get "/api/reservas"
$r = $reservas | Where-Object { $_.identificadorCliente -eq "WEB-AUTOTEST" -and $_.estado -eq "PENDIENTE" } | Select-Object -First 1
Write-Host ("Reserva creada -> id={0} estado={1} cantidad={2}" -f $r.id, $r.estado, $r.cantidad)
$p = Api-Get "/api/productos/1"
Write-Host ("Tras reservar -> stock_fisico={0} reservadas={1} disponible={2}" -f $p.stockFisico, $p.cantidadReservada, $p.stockDisponible)

Write-Host ""
Write-Host "== 2) Confirmar reserva =="
Post-Form "/reservas/$($r.id)/confirmar" @{}
$reservas = Api-Get "/api/reservas"
$r2 = $reservas | Where-Object { $_.id -eq $r.id } | Select-Object -First 1
Write-Host ("Reserva -> id={0} estado={1}" -f $r2.id, $r2.estado)
$p = Api-Get "/api/productos/1"
Write-Host ("Tras confirmar -> stock_fisico={0} reservadas={1} disponible={2}" -f $p.stockFisico, $p.cantidadReservada, $p.stockDisponible)

Write-Host ""
Write-Host "== 3) Crear pedido PAGADO (1 unidad) =="
Post-Form "/pedidos/crear" @{
    nombreCliente = "Cliente Autotest"
    correoCliente = "autotest@technexo.local"
    telefonoCliente = "5555-0000"
    "items[0].idProducto" = 1
    "items[0].cantidad" = 1
}
$pedidos = Api-Get "/api/pedidos"
$pe = $pedidos | Select-Object -First 1
Write-Host ("Pedido -> id={0} estado={1} total={2} {3}" -f $pe.id, $pe.estado, $pe.total, $pe.moneda)
foreach ($i in $pe.items) {
    Write-Host ("   item: {0} x{1} precio_historico={2} importe={3}" -f $i.sku, $i.cantidad, $i.precioUnitarioHistorico, $i.importe)
}
$p = Api-Get "/api/productos/1"
Write-Host ("Tras pedido -> stock_fisico={0} reservadas={1} disponible={2}" -f $p.stockFisico, $p.cantidadReservada, $p.stockDisponible)

Write-Host ""
Write-Host "== 4) Registrar ingreso (ENTRADA, +5 unidades) =="
Post-Form "/inventario/entrada" @{ idProducto = 1; cantidad = 5; motivo = "Autotest reposicion"; referencia = "TEST-ING-001" }
$p = Api-Get "/api/productos/1"
Write-Host ("Tras ingreso -> stock_fisico={0} reservadas={1} disponible={2}" -f $p.stockFisico, $p.cantidadReservada, $p.stockDisponible)

Write-Host ""
Write-Host "== 5) Movimientos de inventario =="
$movs = Api-Get "/api/inventario/movimientos"
foreach ($m in $movs) {
    Write-Host ("   [{0}] {1} {2} x{3} ref={4} motivo={5}" -f $m.id, $m.tipoMovimiento, $m.sku, $m.cantidad, $m.referencia, $m.motivo)
}

Write-Host ""
Write-Host "== 6) Render de paginas con datos =="
foreach ($u in @("/reservas", "/pedidos", "/inventario")) {
    $resp = Invoke-WebRequest -Uri "$base$u" -UseBasicParsing -TimeoutSec 20
    $hasErr = $resp.Content -match "<title>Error</title>"
    Write-Host ("   {0,-12} status={1} len={2} error={3}" -f $u, $resp.StatusCode, $resp.Content.Length, $hasErr)
}
