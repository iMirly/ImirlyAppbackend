package com.imirly.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imirly.backend.dto.request.AnuncioStep1Request;
import com.imirly.backend.dto.request.AnuncioStep2Request;
import com.imirly.backend.dto.response.AnuncioDetailResponse;
import com.imirly.backend.dto.response.AnuncioResponse;
import com.imirly.backend.entity.*;
import com.imirly.backend.entity.enums.AnuncioStatus;
import com.imirly.backend.exception.BusinessException;
import com.imirly.backend.exception.ResourceNotFoundException;
import com.imirly.backend.repository.*;
import com.imirly.backend.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnuncioServiceImpl implements AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final AnuncioMetadataRepository metadataRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AnuncioResponse createStep1(Long userId, AnuncioStep1Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar que el usuario tiene datos completos
        if (!user.puedePublicarAnuncio()) {
            throw new BusinessException("Debes completar tus datos personales antes de publicar un anuncio");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Subcategory subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada"));

        // Verificar que la subcategoría pertenece a la categoría
        if (!subcategory.getCategory().getId().equals(category.getId())) {
            throw new BusinessException("La subcategoría no pertenece a la categoría seleccionada");
        }

        Anuncio anuncio = Anuncio.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .tipoPrecio(request.getTipoPrecio())
                .ubicacion(request.getUbicacion())
                .imagenPrincipalUrl(request.getImagenPrincipalUrl())
                .status(AnuncioStatus.BORRADOR)
                .propietario(user)
                .category(category)
                .subcategory(subcategory)
                .build();

        Anuncio saved = anuncioRepository.save(anuncio);
        return mapToResponse(saved, false);
    }

    @Override
    @Transactional
    public AnuncioDetailResponse completeStep2(Long userId, AnuncioStep2Request request) {
        Anuncio anuncio = anuncioRepository.findById(request.getAnuncioId())
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        // Verificar ownership
        if (!anuncio.getPropietario().getId().equals(userId)) {
            throw new BusinessException("No tienes permiso para editar este anuncio");
        }

        // Solo se puede completar si está en BORRADOR
        if (anuncio.getStatus() != AnuncioStatus.BORRADOR) {
            throw new BusinessException("El anuncio ya ha sido completado o publicado");
        }

        // Convertir metadata a JSON
        String metadataJson;
        try {
            metadataJson = objectMapper.writeValueAsString(request.getMetadata());
        } catch (JsonProcessingException e) {
            throw new BusinessException("Error al procesar los metadatos");
        }

        // Extraer campos comunes para indexación (opcional)
        Boolean servicioDomicilio = extractBoolean(request.getMetadata(), "servicioDomicilio");
        Integer anosExperiencia = extractInteger(request.getMetadata(), "anosExperiencia");
        Boolean disponibilidadUrgente = extractBoolean(request.getMetadata(), "disponibilidadUrgente");

        AnuncioMetadata metadata = AnuncioMetadata.builder()
                .anuncio(anuncio)
                .metadataJson(metadataJson)
                .servicioDomicilio(servicioDomicilio)
                .anosExperiencia(anosExperiencia)
                .disponibilidadUrgente(disponibilidadUrgente)
                .build();

        metadataRepository.save(metadata);
        anuncio.setMetadata(metadata);

        // Auto-publicar o dejar en borrador según preferencia
        // Por ahora lo dejamos en BORRADOR hasta que llamen a publicar()

        return mapToDetailResponse(anuncio, false);
    }

    @Override
    @Transactional
    public AnuncioResponse publicar(Long anuncioId, Long userId) {
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        if (!anuncio.getPropietario().getId().equals(userId)) {
            throw new BusinessException("No tienes permiso para publicar este anuncio");
        }

        // Verificar que tiene metadata (paso 2 completado)
        if (anuncio.getMetadata() == null) {
            throw new BusinessException("Debes completar los datos específicos del servicio antes de publicar");
        }

        anuncio.publicar();
        Anuncio saved = anuncioRepository.save(anuncio);
        return mapToResponse(saved, false);
    }

    @Override
    @Transactional
    public AnuncioResponse despublicar(Long anuncioId, Long userId) {
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        if (!anuncio.getPropietario().getId().equals(userId)) {
            throw new BusinessException("No tienes permiso para despublicar este anuncio");
        }

        anuncio.despublicar();
        Anuncio saved = anuncioRepository.save(anuncio);
        return mapToResponse(saved, false);
    }

    @Override
    @Transactional(readOnly = true)
    public AnuncioDetailResponse getById(Long id) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        if (anuncio.getStatus() != AnuncioStatus.PUBLICADO) {
            throw new ResourceNotFoundException("Anuncio no encontrado");
        }

        return mapToDetailResponse(anuncio, false);
    }

    @Override
    @Transactional(readOnly = true)
    public AnuncioDetailResponse getByIdForOwner(Long id, Long userId) {
        Anuncio anuncio = anuncioRepository.findByIdAndPropietarioId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        return mapToDetailResponse(anuncio, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnuncioResponse> getMisAnuncios(Long userId) {
        List<Anuncio> anuncios = anuncioRepository.findByPropietarioId(userId);
        return anuncios.stream()
                .map(a -> mapToResponse(a, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnuncioResponse> getAnunciosPublicos(Pageable pageable) {
        return anuncioRepository.findAllPublicados(pageable)
                .map(a -> mapToResponse(a, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnuncioResponse> getByCategory(Long categoryId, Pageable pageable) {
        return anuncioRepository.findByCategoryIdAndPublicado(categoryId, pageable)
                .map(a -> mapToResponse(a, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnuncioResponse> getBySubcategory(Long subcategoryId, Pageable pageable) {
        return anuncioRepository.findBySubcategoryIdAndPublicado(subcategoryId, pageable)
                .map(a -> mapToResponse(a, false));
    }

    @Override
    @Transactional
    public AnuncioResponse update(Long anuncioId, Long userId, AnuncioStep1Request request) {
        Anuncio anuncio = anuncioRepository.findByIdAndPropietarioId(anuncioId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        if (!anuncio.isEditable()) {
            throw new BusinessException("No se puede editar un anuncio publicado. Despublícalo primero.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Subcategory subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada"));

        anuncio.setTitulo(request.getTitulo());
        anuncio.setDescripcion(request.getDescripcion());
        anuncio.setPrecio(request.getPrecio());
        anuncio.setTipoPrecio(request.getTipoPrecio());
        anuncio.setUbicacion(request.getUbicacion());
        anuncio.setImagenPrincipalUrl(request.getImagenPrincipalUrl());
        anuncio.setCategory(category);
        anuncio.setSubcategory(subcategory);

        Anuncio saved = anuncioRepository.save(anuncio);
        return mapToResponse(saved, false);
    }

    @Override
    @Transactional
    public AnuncioDetailResponse updateMetadata(Long anuncioId, Long userId, AnuncioStep2Request request) {
        Anuncio anuncio = anuncioRepository.findByIdAndPropietarioId(anuncioId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        if (!anuncio.isEditable()) {
            throw new BusinessException("No se puede editar un anuncio publicado");
        }

        AnuncioMetadata metadata = anuncio.getMetadata();
        if (metadata == null) {
            metadata = new AnuncioMetadata();
            metadata.setAnuncio(anuncio);
        }

        try {
            metadata.setMetadataJson(objectMapper.writeValueAsString(request.getMetadata()));
        } catch (JsonProcessingException e) {
            throw new BusinessException("Error al procesar los metadatos");
        }

        metadata.setServicioDomicilio(extractBoolean(request.getMetadata(), "servicioDomicilio"));
        metadata.setAnosExperiencia(extractInteger(request.getMetadata(), "anosExperiencia"));
        metadata.setDisponibilidadUrgente(extractBoolean(request.getMetadata(), "disponibilidadUrgente"));

        metadataRepository.save(metadata);
        anuncio.setMetadata(metadata);

        return mapToDetailResponse(anuncio, false);
    }

    @Override
    @Transactional
    public void delete(Long anuncioId, Long userId) {
        Anuncio anuncio = anuncioRepository.findByIdAndPropietarioId(anuncioId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Anuncio no encontrado"));

        anuncio.eliminar();
        anuncioRepository.save(anuncio);
    }

    // Mappers
    private AnuncioResponse mapToResponse(Anuncio anuncio, boolean isFavorite) {
        return AnuncioResponse.builder()
                .id(anuncio.getId())
                .titulo(anuncio.getTitulo())
                .descripcion(anuncio.getDescripcion())
                .precio(anuncio.getPrecio())
                .tipoPrecio(anuncio.getTipoPrecio())
                .ubicacion(anuncio.getUbicacion())
                .imagenPrincipalUrl(anuncio.getImagenPrincipalUrl())
                .status(anuncio.getStatus())
                .categoryNombre(anuncio.getCategory().getNombre())
                .subcategoryNombre(anuncio.getSubcategory().getNombre())
                .propietarioId(anuncio.getPropietario().getId())
                .propietarioNombre(anuncio.getPropietario().getNombre())
                .favoritesCount(anuncio.getFavoritesCount())
                .isFavorite(isFavorite)
                .createdAt(anuncio.getCreatedAt())
                .publishedAt(anuncio.getPublishedAt())
                .build();
    }

    private AnuncioDetailResponse mapToDetailResponse(Anuncio anuncio, boolean isFavorite) {
        Map<String, Object> metadataMap = null;
        if (anuncio.getMetadata() != null && anuncio.getMetadata().getMetadataJson() != null) {
            try {
                metadataMap = objectMapper.readValue(anuncio.getMetadata().getMetadataJson(), Map.class);
            } catch (JsonProcessingException e) {
                log.error("Error parsing metadata", e);
            }
        }

        return AnuncioDetailResponse.detailBuilder()
                .id(anuncio.getId())
                .titulo(anuncio.getTitulo())
                .descripcion(anuncio.getDescripcion())
                .precio(anuncio.getPrecio())
                .tipoPrecio(anuncio.getTipoPrecio())
                .ubicacion(anuncio.getUbicacion())
                .imagenPrincipalUrl(anuncio.getImagenPrincipalUrl())
                .status(anuncio.getStatus())
                .categoryNombre(anuncio.getCategory().getNombre())
                .subcategoryNombre(anuncio.getSubcategory().getNombre())
                .propietarioId(anuncio.getPropietario().getId())
                .propietarioNombre(anuncio.getPropietario().getNombre())
                .favoritesCount(anuncio.getFavoritesCount())
                .isFavorite(isFavorite)
                .createdAt(anuncio.getCreatedAt())
                .publishedAt(anuncio.getPublishedAt())
                .metadata(metadataMap)
                .build();
    }

    // Helpers para extraer valores del Map
    private Boolean extractBoolean(Map<String, Object> metadata, String key) {
        if (metadata == null || !metadata.containsKey(key)) return null;
        Object val = metadata.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof String) return Boolean.parseBoolean((String) val);
        return null;
    }

    private Integer extractInteger(Map<String, Object> metadata, String key) {
        if (metadata == null || !metadata.containsKey(key)) return null;
        Object val = metadata.get(key);
        if (val instanceof Integer) return (Integer) val;
        if (val instanceof String) {
            try {
                return Integer.parseInt((String) val);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}