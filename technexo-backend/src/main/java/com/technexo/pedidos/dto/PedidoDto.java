package com.technexo.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDto(
        Long id,
        String estado,
        LocalDateTime fecha,
        BigDecimal subtotal,
        BigDecimal total,
        String moneda,
        String nombreCliente,
        List<ItemDto> items
) {
    public record ItemDto(
            Integer idProducto,
            String sku,
            String producto,
            Integer cantidad,
            BigDecimal precioUnitarioHistorico,
            BigDecimal importe
    ) {
    }
}
