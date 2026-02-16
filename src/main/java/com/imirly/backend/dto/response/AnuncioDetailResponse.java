package com.imirly.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnuncioDetailResponse extends AnuncioResponse {
    private Map<String, Object> metadata;

    @Builder(builderMethodName = "detailBuilder")
    public AnuncioDetailResponse(Long id, String titulo, String descripcion,
                                 java.math.BigDecimal precio,
                                 com.imirly.backend.entity.enums.PriceType tipoPrecio,
                                 String ubicacion, String imagenPrincipalUrl,
                                 com.imirly.backend.entity.enums.AnuncioStatus status,
                                 String categoryNombre, String subcategoryNombre,
                                 Long propietarioId, String propietarioNombre,
                                 Integer favoritesCount, boolean isFavorite,
                                 java.time.LocalDateTime createdAt,
                                 java.time.LocalDateTime publishedAt,
                                 Map<String, Object> metadata) {
        super(id, titulo, descripcion, precio, tipoPrecio, ubicacion,
                imagenPrincipalUrl, status, categoryNombre, subcategoryNombre,
                propietarioId, propietarioNombre, favoritesCount, isFavorite,
                createdAt, publishedAt);
        this.metadata = metadata;
    }
}