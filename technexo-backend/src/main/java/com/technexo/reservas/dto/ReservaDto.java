package com.technexo.reservas.dto;

import java.time.LocalDateTime;

public record ReservaDto(
        Long id,
        Integer idProducto,
        String sku,
        String producto,
        Integer cantidad,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaExpiracion,
        String estado,
        String identificadorCliente
) {
}
