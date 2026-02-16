package com.imirly.backend.repository;

import com.imirly.backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Favorite> findByUserIdAndAnuncioId(Long userId, Long anuncioId);
    boolean existsByUserIdAndAnuncioId(Long userId, Long anuncioId);
    void deleteByUserIdAndAnuncioId(Long userId, Long anuncioId);
}