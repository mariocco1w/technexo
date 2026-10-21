package com.technexo.chat.dto;

import java.time.LocalDateTime;

public record MensajeDto(Long id, String rol, String contenido, LocalDateTime fecha) {
}
