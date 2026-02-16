package com.imirly.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String imagen;
    private List<SubcategoryResponse> subcategories;
}