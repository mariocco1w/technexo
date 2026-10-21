package com.technexo.chat.dto;

import java.math.BigDecimal;
import java.util.List;

public record ConsultaFiltros(
        String texto,
        String categoria,
        String marca,
        BigDecimal precioMin,
        BigDecimal precioMax,
        boolean soloDisponibles,
        List<FiltroAtributo> atributos
) {
    public boolean tieneCriterio() {
        return (texto != null && !texto.isBlank())
                || (categoria != null && !categoria.isBlank())
                || (marca != null && !marca.isBlank())
                || precioMin != null
                || precioMax != null
                || soloDisponibles
                || (atributos != null && !atributos.isEmpty());
    }
}
