package com.imirly.backend.entity.enums;

public enum AnuncioStatus {
    BORRADOR,        // Paso 1 completado, falta paso 2
    PUBLICADO,       // Activo y visible
    DESPUBLICADO,    // Inactivo (oculto pero no borrado)
    ELIMINADO        // Soft delete
}