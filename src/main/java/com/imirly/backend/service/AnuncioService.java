package com.imirly.backend.service;

import com.imirly.backend.dto.request.AnuncioStep1Request;
import com.imirly.backend.dto.request.AnuncioStep2Request;
import com.imirly.backend.dto.response.AnuncioDetailResponse;
import com.imirly.backend.dto.response.AnuncioResponse;
import com.imirly.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnuncioService {
    // Paso 1: Crear anuncio genérico
    AnuncioResponse createStep1(Long userId, AnuncioStep1Request request);

    // Paso 2: Completar con metadatos específicos
    AnuncioDetailResponse completeStep2(Long userId, AnuncioStep2Request request);

    // Publicar/Despublicar
    AnuncioResponse publicar(Long anuncioId, Long userId);
    AnuncioResponse despublicar(Long anuncioId, Long userId);

    // CRUD
    AnuncioDetailResponse getById(Long id);
    AnuncioDetailResponse getByIdForOwner(Long id, Long userId);
    List<AnuncioResponse> getMisAnuncios(Long userId);
    Page<AnuncioResponse> getAnunciosPublicos(Pageable pageable);
    Page<AnuncioResponse> getByCategory(Long categoryId, Pageable pageable);
    Page<AnuncioResponse> getBySubcategory(Long subcategoryId, Pageable pageable);

    // Actualizar (solo si es BORRADOR o DESPUBLICADO)
    AnuncioResponse update(Long anuncioId, Long userId, AnuncioStep1Request request);
    AnuncioDetailResponse updateMetadata(Long anuncioId, Long userId, AnuncioStep2Request request);

    // Eliminar (soft delete)
    void delete(Long anuncioId, Long userId);
}