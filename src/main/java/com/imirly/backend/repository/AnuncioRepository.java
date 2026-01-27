package com.imirly.backend.repository;

import com.imirly.backend.entity.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    List<Anuncio> findBySubcategoryId(Long subcategoryId);

    List<Anuncio> findBySubcategoryCategoryId(Long categoryId);

    List<Anuncio> findBySubcategoryIdAndActivoTrue(Long subcategoryId);

    // Método para encontrar anuncios con precio no nulo
    List<Anuncio> findBySubcategoryIdAndPriceIsNotNull(Long subcategoryId);

    // Método para debug: ver todos los anuncios con sus precios
    @Query("SELECT a.id, a.titulo, a.price FROM Anuncio a WHERE a.subcategory.id = :subcategoryId")
    List<Object[]> findPreciosBySubcategoryId(@Param("subcategoryId") Long subcategoryId);
}