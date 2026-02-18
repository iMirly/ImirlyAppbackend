package com.imirly.backend.repository;

import com.imirly.backend.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Cambiado a Page para paginación correcta
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Favorite> findByUserIdAndAnuncioId(Long userId, Long anuncioId);

    boolean existsByUserIdAndAnuncioId(Long userId, Long anuncioId);

    void deleteByUserIdAndAnuncioId(Long userId, Long anuncioId);

    // FALTABA ESTE MÉTODO
    long countByAnuncioId(Long anuncioId);
}