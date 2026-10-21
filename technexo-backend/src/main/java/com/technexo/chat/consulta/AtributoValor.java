package com.technexo.chat.consulta;

import java.math.BigDecimal;

public record AtributoValor(
        String nombre,
        String tipoDato,
        String texto,
        BigDecimal numero,
        Boolean booleano
) {
}
