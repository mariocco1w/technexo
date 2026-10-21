$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"

function Test-Page($name, $url) {
    try {
        $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 20
        $body = $r.Content
        $isError = ($body -match "<title>Error</title>") -or ($body -match "alert-danger") -and ($body -match "Error</h4>")
        $flag = if ($isError) { "ERROR-PAGE" } else { "OK" }
        Write-Host ("{0,-28} {1,4}  len={2,-6} {3}" -f $name, $r.StatusCode, $body.Length, $flag)
        if ($isError) {
            $m = [regex]::Match($body, "<p>(.*?)</p>", "Singleline")
            if ($m.Success) { Write-Host ("    -> " + $m.Groups[1].Value.Substring(0, [Math]::Min(300, $m.Groups[1].Value.Length))) }
        }
    } catch {
        Write-Host ("{0,-28} FAIL  {1}" -f $name, $_.Exception.Message)
    }
}

Test-Page "GET /"            "$base/"
Test-Page "GET /catalogo"    "$base/catalogo"
Test-Page "GET /buscar?q=sw" "$base/buscar?q=switch"
Test-Page "GET /producto/1"  "$base/producto/1"
Test-Page "GET /reservas"    "$base/reservas"
Test-Page "GET /pedidos"     "$base/pedidos"
Test-Page "GET /inventario"  "$base/inventario"
Test-Page "GET /checkout"    "$base/checkout"

Write-Host ""
Write-Host "=== API JSON ==="
function Test-Api($name, $url) {
    try {
        $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 20
        Write-Host ("{0,-32} {1,4}  len={2}" -f $name, $r.StatusCode, $r.Content.Length)
        Write-Host ("    " + $r.Content.Substring(0, [Math]::Min(200, $r.Content.Length)))
    } catch {
        Write-Host ("{0,-32} FAIL  {1}" -f $name, $_.Exception.Message)
    }
}
Test-Api "GET /api/productos"        "$base/api/productos"
Test-Api "GET /api/productos/1"      "$base/api/productos/1"
Test-Api "GET /api/productos/1/atr"  "$base/api/productos/1/atributos"
Test-Api "GET /api/categorias"       "$base/api/categorias"
Test-Api "GET /api/inventario/mov"   "$base/api/inventario/movimientos"
