package com.technexo.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatPeticion(
        Long idConversacion,
        @NotBlank @Size(max = 2000) String mensaje,
        @Size(max = 150) String identificadorSesion
) {
}
