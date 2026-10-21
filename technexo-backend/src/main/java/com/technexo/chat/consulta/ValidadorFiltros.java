package com.technexo.chat.consulta;

import com.technexo.chat.dto.AtributoConocido;
import com.technexo.chat.dto.CatalogoReferencia;
import com.technexo.chat.dto.ConsultaFiltros;
import com.technexo.chat.dto.FiltroAtributo;
import com.technexo.chat.dto.Operador;

import java.math.BigDecimal;
import java.util.Set;

public final class ValidadorFiltros {

    private static final Set<String> TIPOS = Set.of("TEXTO", "NUMERO", "BOOLEANO", "FECHA");

    private ValidadorFiltros() {
    }

    public static void validar(ConsultaFiltros filtros, CatalogoReferencia catalogo) {
        if (filtros == null || !filtros.tieneCriterio()) {
            throw new IllegalArgumentException("La consulta no tiene filtros permitidos");
        }
        if (filtros.precioMin() != null && filtros.precioMin().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio mínimo no es válido");
        }
        if (filtros.precioMax() != null && filtros.precioMax().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio máximo no es válido");
        }
        if (filtros.categoria() != null && !contiene(catalogo.categorias(), filtros.categoria())) {
            throw new IllegalArgumentException("La categoría no existe en el catálogo");
        }
        if (filtros.marca() != null && !contiene(catalogo.marcas(), filtros.marca())) {
            throw new IllegalArgumentException("La marca no existe en el catálogo");
        }
        if (filtros.atributos() == null) {
            return;
        }
        for (FiltroAtributo filtro : filtros.atributos()) {
            validarAtributo(filtro, catalogo);
        }
    }

    private static void validarAtributo(FiltroAtributo filtro, CatalogoReferencia catalogo) {
        AtributoConocido conocido = null;
        if (catalogo.atributos() != null) {
            for (AtributoConocido candidato : catalogo.atributos()) {
                if (candidato.nombre().equals(filtro.nombre())) {
                    conocido = candidato;
                    break;
                }
            }
        }
        if (conocido == null || !TIPOS.contains(filtro.tipoDato()) || !filtro.tipoDato().equals(conocido.tipoDato())) {
            throw new IllegalArgumentException("El atributo no está permitido: " + filtro.nombre());
        }
        if (filtro.operador() == null) {
            throw new IllegalArgumentException("Falta el operador del atributo " + filtro.nombre());
        }
        switch (filtro.tipoDato()) {
            case "BOOLEANO" -> {
                if (filtro.operador() != Operador.EQ || filtro.booleano() == null) {
                    throw new IllegalArgumentException("El atributo booleano " + filtro.nombre() + " solo admite igualdad");
                }
            }
            case "NUMERO" -> {
                if (filtro.numero() == null) {
                    throw new IllegalArgumentException("Falta el valor numérico de " + filtro.nombre());
                }
            }
            case "TEXTO" -> {
                if (filtro.operador() != Operador.EQ || filtro.texto() == null || filtro.texto().isBlank()) {
                    throw new IllegalArgumentException("Falta el valor de texto de " + filtro.nombre());
                }
            }
            case "FECHA" -> throw new IllegalArgumentException("El filtro por fecha no está habilitado");
            default -> throw new IllegalArgumentException("Tipo de atributo no permitido");
        }
    }

    private static boolean contiene(java.util.List<String> valores, String buscado) {
        if (valores == null) {
            return false;
        }
        for (String valor : valores) {
            if (valor.equalsIgnoreCase(buscado)) {
                return true;
            }
        }
        return false;
    }
}
