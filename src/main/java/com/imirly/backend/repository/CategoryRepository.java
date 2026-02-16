package com.imirly.backend.repository;

import com.imirly.backend.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @EntityGraph(attributePaths = {"subcategories"})
    List<Category> findAllByOrderByNombreAsc();

    Optional<Category> findByCodigo(String codigo);
}