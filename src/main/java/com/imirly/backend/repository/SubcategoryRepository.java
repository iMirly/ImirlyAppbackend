package com.imirly.backend.repository;

import com.imirly.backend.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    List<Subcategory> findByCategoryId(Long categoryId);
}
