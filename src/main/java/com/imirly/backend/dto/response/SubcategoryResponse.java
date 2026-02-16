package com.imirly.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubcategoryResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String imagen;
}