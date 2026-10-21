package com.technexo.inventario.dto;

public record InventarioDto(
        Integer idProducto,
        String sku,
        String nombre,
        Integer stockActual,
        Integer stockMinimo,
        String ubicacion
) {
}
