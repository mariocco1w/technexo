package com.technexo.chat.dto;

import java.util.List;

public record CatalogoReferencia(
        List<String> categorias,
        List<String> marcas,
        List<AtributoConocido> atributos
) {
}
