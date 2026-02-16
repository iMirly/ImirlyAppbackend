package com.imirly.backend.dto.request;

import com.imirly.backend.entity.enums.PriceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnuncioStep1Request {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;

    @NotNull(message = "El tipo de precio es obligatorio")
    private PriceType tipoPrecio;

    private String ubicacion;

    private String imagenPrincipalUrl;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "La subcategoría es obligatoria")
    private Long subcategoryId;
}