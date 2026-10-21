package com.technexo.chat.dto;

import java.util.List;

public record ConversacionDto(Long id, String estado, List<MensajeDto> mensajes) {
}
