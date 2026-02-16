package com.imirly.backend.repository;

import com.imirly.backend.entity.Anuncio;
import com.imirly.backend.entity.enums.AnuncioStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    // Anuncios de un usuario (todos los estados excepto ELIMINADO)
    @Query("SELECT a FROM Anuncio a WHERE a.propietario.id = :userId AND a.status != 'ELIMINADO' ORDER BY a.createdAt DESC")
    List<Anuncio> findByPropietarioId(@Param("userId") Long userId);

    // Anuncios públicos activos
    @Query("SELECT a FROM Anuncio a WHERE a.status = 'PUBLICADO' ORDER BY a.publishedAt DESC")
    Page<Anuncio> findAllPublicados(Pageable pageable);

    // Anuncios por categoría
    @Query("SELECT a FROM Anuncio a WHERE a.status = 'PUBLICADO' AND a.category.id = :categoryId")
    Page<Anuncio> findByCategoryIdAndPublicado(@Param("categoryId") Long categoryId, Pageable pageable);

    // Anuncios por subcategoría
    @Query("SELECT a FROM Anuncio a WHERE a.status = 'PUBLICADO' AND a.subcategory.id = :subcategoryId")
    Page<Anuncio> findBySubcategoryIdAndPublicado(@Param("subcategoryId") Long subcategoryId, Pageable pageable);

    // Buscar por ID y propietario (para verificar ownership)
    Optional<Anuncio> findByIdAndPropietarioId(Long id, Long propietarioId);

    // Contar favoritos
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.anuncio.id = :anuncioId")
    Long countFavoritesByAnuncioId(@Param("anuncioId") Long anuncioId);
}