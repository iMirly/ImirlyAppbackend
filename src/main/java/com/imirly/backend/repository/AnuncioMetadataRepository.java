package com.imirly.backend.repository;

import com.imirly.backend.entity.AnuncioMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnuncioMetadataRepository extends JpaRepository<AnuncioMetadata, Long> {
    Optional<AnuncioMetadata> findByAnuncioId(Long anuncioId);
}