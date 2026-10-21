package com.technexo.catalogo.dto;

import java.math.BigDecimal;

public record ProductoDto(
        Integer id,
        String sku,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String moneda,
        String estado,
        String marca,
        String categoria,
        Integer stockFisico,
        Integer cantidadReservada,
        Integer stockDisponible
) {
}
