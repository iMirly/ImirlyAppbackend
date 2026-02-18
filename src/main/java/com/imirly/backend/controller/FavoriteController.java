package com.imirly.backend.controller;

import com.imirly.backend.dto.response.AnuncioResponse;
import com.imirly.backend.security.UserDetailsImpl;
import com.imirly.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{anuncioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long anuncioId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        favoriteService.addFavorite(userDetails.getId(), anuncioId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{anuncioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long anuncioId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        favoriteService.removeFavorite(userDetails.getId(), anuncioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mis-favoritos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<AnuncioResponse>> getMisFavoritos(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        return ResponseEntity.ok(favoriteService.getMisFavoritos(userDetails.getId(), pageable));
    }

    @GetMapping("/check/{anuncioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Long anuncioId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(favoriteService.isFavorite(userDetails.getId(), anuncioId));
    }
}