package com.technexo.chat.consulta;

import com.technexo.chat.dto.ConsultaFiltros;
import com.technexo.chat.dto.FiltroAtributo;
import com.technexo.chat.dto.Operador;

import java.math.BigDecimal;

public final class FiltroProductos {

    private FiltroProductos() {
    }

    public static boolean cumple(FichaFiltro ficha, ConsultaFiltros filtros) {
        if (filtros.texto() != null && !contieneTexto(ficha, filtros.texto())) {
            return false;
        }
        if (filtros.categoria() != null && !igual(ficha.categoria(), filtros.categoria())) {
            return false;
        }
        if (filtros.marca() != null && !igual(ficha.marca(), filtros.marca())) {
            return false;
        }
        if (filtros.precioMin() != null && (ficha.precio() == null || ficha.precio().compareTo(filtros.precioMin()) < 0)) {
            return false;
        }
        if (filtros.precioMax() != null && (ficha.precio() == null || ficha.precio().compareTo(filtros.precioMax()) > 0)) {
            return false;
        }
        if (filtros.soloDisponibles() && ficha.stockDisponible() <= 0) {
            return false;
        }
        if (filtros.atributos() == null) {
            return true;
        }
        for (FiltroAtributo filtro : filtros.atributos()) {
            if (!cumpleAtributo(ficha, filtro)) {
                return false;
            }
        }
        return true;
    }

    private static boolean contieneTexto(FichaFiltro ficha, String texto) {
        String bolsa = TextoUtil.normalizar(String.join(" ",
                nulo(ficha.nombre()), nulo(ficha.sku()), nulo(ficha.descripcion()),
                nulo(ficha.marca()), nulo(ficha.categoria())));
        return bolsa.contains(TextoUtil.normalizar(texto));
    }

    private static boolean cumpleAtributo(FichaFiltro ficha, FiltroAtributo filtro) {
        if (ficha.atributos() == null) {
            return false;
        }
        for (AtributoValor valor : ficha.atributos()) {
            if (igual(valor.nombre(), filtro.nombre()) && coincide(valor, filtro)) {
                return true;
            }
        }
        return false;
    }

    private static boolean coincide(AtributoValor valor, FiltroAtributo filtro) {
        return switch (filtro.tipoDato()) {
            case "BOOLEANO" -> filtro.booleano().equals(valor.booleano());
            case "NUMERO" -> valor.numero() != null && compara(valor.numero(), filtro.operador(), filtro.numero());
            case "TEXTO" -> TextoUtil.normalizar(valor.texto()).equals(TextoUtil.normalizar(filtro.texto()));
            case "FECHA" -> false;
            default -> false;
        };
    }

    private static boolean compara(BigDecimal valor, Operador operador, BigDecimal esperado) {
        int comparacion = valor.compareTo(esperado);
        return switch (operador) {
            case EQ -> comparacion == 0;
            case LT -> comparacion < 0;
            case GT -> comparacion > 0;
            case LTE -> comparacion <= 0;
            case GTE -> comparacion >= 0;
        };
    }

    private static boolean igual(String izquierda, String derecha) {
        return TextoUtil.normalizar(izquierda).equals(TextoUtil.normalizar(derecha));
    }

    private static String nulo(String valor) {
        return valor == null ? "" : valor;
    }
}
