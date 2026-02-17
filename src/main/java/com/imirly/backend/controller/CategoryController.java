package com.imirly.backend.controller;

import com.imirly.backend.dto.response.CategoryResponse;
import com.imirly.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Endpoint para inicializar categorías (solo ejecutar una vez)
    @PostMapping("/seed")
    public ResponseEntity<?> seedCategories() {
        categoryService.seedCategories();
        return ResponseEntity.ok().body("Categorías inicializadas correctamente");
    }
}