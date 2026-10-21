package com.technexo.inventario.dto;

import java.time.LocalDateTime;

public record MovimientoDto(
        Long id,
        Integer idProducto,
        String sku,
        String producto,
        String tipoMovimiento,
        Integer cantidad,
        LocalDateTime fecha,
        String motivo,
        String referencia,
        String creadoPor
) {
}
