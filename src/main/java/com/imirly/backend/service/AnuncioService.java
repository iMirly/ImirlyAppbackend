package com.imirly.backend.service;

import com.imirly.backend.dto.request.AnuncioStep1Request;
import com.imirly.backend.dto.request.AnuncioStep2Request;
import com.imirly.backend.dto.response.AnuncioDetailResponse;
import com.imirly.backend.dto.response.AnuncioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AnuncioService {

    // Paso 1: Crear anuncio genérico
    AnuncioResponse createStep1(Long userId, AnuncioStep1Request request);

    // Paso 2: Completar con metadatos específicos
    AnuncioDetailResponse completeStep2(Long userId, AnuncioStep2Request request);

    // Publicar/Despublicar
    AnuncioResponse publicar(Long anuncioId, Long userId);
    AnuncioResponse despublicar(Long anuncioId, Long userId);

    // CRUD - Lectura
    AnuncioDetailResponse getById(Long id);
    AnuncioDetailResponse getByIdForOwner(Long id, Long userId);
    List<AnuncioResponse> getMisAnuncios(Long userId);
    Page<AnuncioResponse> getAnunciosPublicos(Pageable pageable);
    Page<AnuncioResponse> getByCategory(Long categoryId, Pageable pageable);
    Page<AnuncioResponse> getBySubcategory(Long subcategoryId, Pageable pageable);


    Page<AnuncioResponse> getAnunciosPublicosExcluyendoUsuario(Long userId, Pageable pageable);
    Page<AnuncioResponse> getByCategoryExcluyendoUsuario(Long categoryId, Long userId, Pageable pageable);
    Page<AnuncioResponse> searchAnunciosPublicos(String query, Long categoryId, Long userId, Pageable pageable);


    // CRUD - Actualizar
    AnuncioResponse update(Long anuncioId, Long userId, AnuncioStep1Request request);

    // Actualizar metadatos (PASO 2 en edición) - SOLO ESTE, elimina el otro
    AnuncioDetailResponse updateMetadata(Long anuncioId, Long userId, AnuncioStep2Request request);

    // CRUD - Eliminar (soft delete)
    void delete(Long anuncioId, Long userId);

    // Para editar - carga anuncio con todos los datos
    AnuncioDetailResponse getByIdForEdit(Long id, Long userId);
}