package com.technexo.chat.consulta;

import java.math.BigDecimal;
import java.util.List;

public record FichaFiltro(
        String sku,
        String nombre,
        String descripcion,
        String marca,
        String categoria,
        BigDecimal precio,
        int stockDisponible,
        List<AtributoValor> atributos
) {
}
