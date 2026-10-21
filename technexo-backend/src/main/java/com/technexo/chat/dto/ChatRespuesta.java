package com.technexo.chat.dto;

import com.technexo.catalogo.dto.ProductoDto;

import java.util.List;

public record ChatRespuesta(
        Long idConversacion,
        String respuesta,
        boolean ambigua,
        List<ProductoDto> productos
) {
}
