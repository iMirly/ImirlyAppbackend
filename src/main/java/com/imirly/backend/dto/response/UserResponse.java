package com.imirly.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String direccionCalle;
    private String direccionCiudad;
    private String direccionCodigoPostal;
    private String fotoPerfilUrl;
    private boolean datosCompletos;
    private LocalDateTime createdAt;
}