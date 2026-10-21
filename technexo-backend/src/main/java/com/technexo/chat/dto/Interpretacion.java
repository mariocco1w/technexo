package com.technexo.chat.dto;

public record Interpretacion(ConsultaFiltros filtros, boolean ambigua, String aclaracion) {
    public static Interpretacion concreta(ConsultaFiltros filtros) {
        return new Interpretacion(filtros, false, null);
    }

    public static Interpretacion ambigua(String aclaracion) {
        return new Interpretacion(null, true, aclaracion);
    }
}
