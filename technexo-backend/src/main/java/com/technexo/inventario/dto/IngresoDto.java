package com.technexo.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record IngresoDto(
        @NotNull Integer idProducto,
        @NotNull @Min(1) Integer cantidad,
        String motivo,
        String referencia
) {
}
