package com.imirly.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class AnuncioStep2Request {
    @NotNull(message = "El ID del anuncio es obligatorio")
    private Long anuncioId;

    // Campos específicos de la subcategoría como Map flexible
    // Ejemplo: {"disponibilidad": ["Lun", "Mar"], "servicioDomicilio": true, "anosExperiencia": 5}
    @NotNull(message = "Los metadatos son obligatorios")
    private Map<String, Object> metadata;
}