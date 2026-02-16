package com.imirly.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Pattern(regexp = "^[0-9]{9}$", message = "Teléfono inválido (9 dígitos)")
    private String telefono;

    private LocalDate fechaNacimiento;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccionCalle;

    @NotBlank(message = "La ciudad es obligatoria")
    private String direccionCiudad;

    @Pattern(regexp = "^[0-9]{5}$", message = "Código postal inválido")
    private String direccionCodigoPostal;

    private String fotoPerfilUrl;
}