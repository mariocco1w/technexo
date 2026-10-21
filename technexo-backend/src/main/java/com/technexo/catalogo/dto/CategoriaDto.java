package com.technexo.catalogo.dto;

public record CategoriaDto(
        Integer id,
        String nombre,
        Integer idCategoriaPadre,
        String estado
) {
}
