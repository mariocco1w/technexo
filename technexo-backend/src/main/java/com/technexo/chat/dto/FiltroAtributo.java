package com.technexo.chat.dto;

import java.math.BigDecimal;

public record FiltroAtributo(
        String nombre,
        String tipoDato,
        Operador operador,
        String texto,
        BigDecimal numero,
        Boolean booleano
) {
}
