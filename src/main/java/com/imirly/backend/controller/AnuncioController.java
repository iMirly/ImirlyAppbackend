package com.imirly.backend.controller;

import com.imirly.backend.dto.request.AnuncioStep1Request;
import com.imirly.backend.dto.request.AnuncioStep2Request;
import com.imirly.backend.dto.response.AnuncioDetailResponse;
import com.imirly.backend.dto.response.AnuncioResponse;
import com.imirly.backend.entity.Anuncio;
import com.imirly.backend.security.UserDetailsImpl;
import com.imirly.backend.service.AnuncioService;
import com.imirly.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final FavoriteService favoriteService;

    // ==================== CREAR ANUNCIO (2 PASOS) ====================

    @PostMapping("/step1")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioResponse> createStep1(
            @RequestBody AnuncioStep1Request request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(anuncioService.createStep1(userDetails.getId(), request));
    }

    @PostMapping("/step2")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioDetailResponse> completeStep2(
            @RequestBody AnuncioStep2Request request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(anuncioService.completeStep2(userDetails.getId(), request));
    }

    // ==================== PUBLICAR/DESPUBLICAR ====================

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

    // ==================== CRUD BÁSICO ====================

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(anuncioService.getById(id));
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioDetailResponse> getByIdForOwner(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(anuncioService.getByIdForOwner(id, userDetails.getId()));
    }

    @GetMapping("/mis-anuncios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AnuncioResponse>> getMisAnuncios(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(anuncioService.getMisAnuncios(userDetails.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioResponse> update(
            @PathVariable Long id,
            @RequestBody AnuncioStep1Request request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(anuncioService.update(id, userDetails.getId(), request));
    }

    // ==================== ACTUALIZAR METADATA (CORREGIDO) ====================

    @PutMapping("/{id}/metadata")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioDetailResponse> updateMetadata(
            @PathVariable Long id,
            @RequestBody AnuncioStep2Request request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(anuncioService.updateMetadata(id, userDetails.getId(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        anuncioService.delete(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // ==================== ANUNCIOS PÚBLICOS ====================

    @GetMapping("/public")
    public ResponseEntity<Page<AnuncioResponse>> getPublicos(Pageable pageable) {
        return ResponseEntity.ok(anuncioService.getAnunciosPublicos(pageable));
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<Page<AnuncioResponse>> getByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {

        return ResponseEntity.ok(anuncioService.getByCategory(categoryId, pageable));
    }

    @GetMapping("/public/subcategory/{subcategoryId}")
    public ResponseEntity<Page<AnuncioResponse>> getBySubcategory(
            @PathVariable Long subcategoryId,
            Pageable pageable) {

        return ResponseEntity.ok(anuncioService.getBySubcategory(subcategoryId, pageable));
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnuncioDetailResponse> getForEdit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(anuncioService.getByIdForEdit(id, userDetails.getId()));
    }

    // ==================== ANUNCIOS PÚBLICOS (EXCLUYENDO PROPIOS) ====================

    @GetMapping("/public/excluyendo-mios")
    public ResponseEntity<Page<AnuncioResponse>> getPublicExcluyendoMios(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        // Si userDetails es null, el usuario no está autenticado
        if (userDetails == null) {
            return ResponseEntity.ok(anuncioService.getAnunciosPublicos(pageable));
        }

        // Usa el método que implementamos en FavoriteService
        Page<AnuncioResponse> response = favoriteService.findPublicExcluyendoMios(
                userDetails.getId(), pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/excluyendo-mios/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AnuncioResponse>> getByCategoryExcluyendoMios(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        return ResponseEntity.ok(anuncioService.getByCategoryExcluyendoUsuario(categoryId, userDetails.getId(), pageable));
    }

    @GetMapping("/public/excluyendo-mios/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AnuncioResponse>> searchPublicosExcluyendoMios(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        return ResponseEntity.ok(anuncioService.searchAnunciosPublicos(query, categoryId, userDetails.getId(), pageable));
    }
}