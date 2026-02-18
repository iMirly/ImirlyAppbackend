package com.imirly.backend.service;

import com.imirly.backend.dto.response.AnuncioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    void addFavorite(Long userId, Long anuncioId);
    void removeFavorite(Long userId, Long anuncioId);
    Page<AnuncioResponse> getMisFavoritos(Long userId, Pageable pageable);
    boolean isFavorite(Long userId, Long anuncioId);
    Page<AnuncioResponse> findPublicExcluyendoMios(Long userId, Pageable pageable);
}