package com.imirly.backend.service;

import com.imirly.backend.dto.response.CategoryResponse;
import com.imirly.backend.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    Category getCategoryById(Long id);
    void seedCategories(); // Para inicializar datos
}