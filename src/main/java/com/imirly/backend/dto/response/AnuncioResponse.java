package com.imirly.backend.dto.response;

import com.imirly.backend.entity.enums.AnuncioStatus;
import com.imirly.backend.entity.enums.PriceType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AnuncioResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private PriceType tipoPrecio;
    private String ubicacion;
    private String imagenPrincipalUrl;
    private AnuncioStatus status;
    private String categoryNombre;
    private String subcategoryNombre;
    private Long propietarioId;
    private String propietarioNombre;
    private Integer favoritesCount;
    private boolean isFavorite; // Para el usuario logueado
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}