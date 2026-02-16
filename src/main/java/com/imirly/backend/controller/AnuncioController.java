package com.imirly.backend.controller;

import com.imirly.backend.dto.request.AnuncioStep1Request;
import com.imirly.backend.dto.request.AnuncioStep2Request;
import com.imirly.backend.dto.response.AnuncioDetailResponse;
import com.imirly.backend.dto.response.AnuncioResponse;
import com.imirly.backend.security.UserDetailsImpl;
import com.imirly.backend.service.AnuncioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final AnuncioService anuncioService;

    // ==================== PASO 1: Crear anuncio genérico ====================
    @PostMapping("/step1")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioResponse> createStep1(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AnuncioStep1Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(anuncioService.createStep1(userDetails.getId(), request));
    }

    // ==================== PASO 2: Completar metadatos ====================
    @PostMapping("/step2")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioDetailResponse> completeStep2(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AnuncioStep2Request request) {
        return ResponseEntity.ok(anuncioService.completeStep2(userDetails.getId(), request));
    }

    // ==================== PUBLICAR / DESPUBLICAR ====================
    @PostMapping("/{id}/publicar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioResponse> publicar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(anuncioService.publicar(id, userDetails.getId()));
    }

    @PostMapping("/{id}/despublicar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioResponse> despublicar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(anuncioService.despublicar(id, userDetails.getId()));
    }

    // ==================== MIS ANUNCIOS ====================
    @GetMapping("/mis-anuncios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AnuncioResponse>> getMisAnuncios(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(anuncioService.getMisAnuncios(userDetails.getId()));
    }

    // ==================== ANUNCIOS PÚBLICOS ====================
    @GetMapping("/public")
    public ResponseEntity<Page<AnuncioResponse>> getPublicos(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(anuncioService.getAnunciosPublicos(pageable));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<AnuncioDetailResponse> getPublicoById(@PathVariable Long id) {
        return ResponseEntity.ok(anuncioService.getById(id));
    }

    // ==================== FILTROS ====================
    @GetMapping("/public/categoria/{categoryId}")
    public ResponseEntity<Page<AnuncioResponse>> getByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(anuncioService.getByCategory(categoryId, pageable));
    }

    @GetMapping("/public/subcategoria/{subcategoryId}")
    public ResponseEntity<Page<AnuncioResponse>> getBySubcategory(
            @PathVariable Long subcategoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(anuncioService.getBySubcategory(subcategoryId, pageable));
    }

    // ==================== DETALLE PROPIETARIO ====================
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioDetailResponse> getByIdForOwner(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(anuncioService.getByIdForOwner(id, userDetails.getId()));
    }

    // ==================== ACTUALIZAR ====================
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioResponse> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AnuncioStep1Request request) {
        return ResponseEntity.ok(anuncioService.update(id, userDetails.getId(), request));
    }

    @PutMapping("/{id}/metadata")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioDetailResponse> updateMetadata(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AnuncioStep2Request request) {
        return ResponseEntity.ok(anuncioService.updateMetadata(id, userDetails.getId(), request));
    }

    // ==================== ELIMINAR ====================
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        anuncioService.delete(id, userDetails.getId());
        return ResponseEntity.ok().body("Anuncio eliminado correctamente");
    }
}