package com.imirly.backend.service.impl;

import com.imirly.backend.dto.response.AnuncioResponse;
import com.imirly.backend.entity.Anuncio;
import com.imirly.backend.entity.Favorite;
import com.imirly.backend.entity.User;
import com.imirly.backend.entity.enums.AnuncioStatus;
import com.imirly.backend.exception.BusinessException;
import com.imirly.backend.repository.AnuncioRepository;
import com.imirly.backend.repository.FavoriteRepository;
import com.imirly.backend.repository.UserRepository;
import com.imirly.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final AnuncioRepository anuncioRepository;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long anuncioId) {
        if (favoriteRepository.existsByUserIdAndAnuncioId(userId, anuncioId)) {
            throw new BusinessException("Ya está en favoritos");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Anuncio anuncio = anuncioRepository.findById(anuncioId)
                .orElseThrow(() -> new RuntimeException("Anuncio no encontrado"));

        // No puedes favoritar tu propio anuncio
        if (anuncio.getPropietario().getId().equals(userId)) {
            throw new BusinessException("No puedes favoritar tu propio anuncio");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .anuncio(anuncio)
                .build();

        favoriteRepository.save(favorite);

        // USAR MÉTODO DE NEGOCIO DE LA ENTIDAD (más eficiente)
        anuncio.incrementFavoritesCount();
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long anuncioId) {
        // Verificar que existe antes de borrar
        Favorite favorite = favoriteRepository.findByUserIdAndAnuncioId(userId, anuncioId)
                .orElseThrow(() -> new BusinessException("El favorito no existe"));

        Anuncio anuncio = favorite.getAnuncio();
        favoriteRepository.delete(favorite);

        // USAR MÉTODO DE NEGOCIO
        anuncio.decrementFavoritesCount();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnuncioResponse> getMisFavoritos(Long userId, Pageable pageable) {
        // CORREGIDO: Ahora retorna Page<Favorite> correctamente
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(fav -> mapToResponse(fav.getAnuncio(), true));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long anuncioId) {
        return favoriteRepository.existsByUserIdAndAnuncioId(userId, anuncioId);
    }

    // En FavoriteServiceImpl o AnuncioServiceImpl
    public Page<AnuncioResponse> findPublicExcluyendoMios(Long userId, Pageable pageable) {
        Page<Anuncio> anuncios = anuncioRepository.findByStatusAndPropietarioIdNot(
                AnuncioStatus.PUBLISHED, userId, pageable);

        return anuncios.map(anuncio -> {
            AnuncioResponse dto = new AnuncioResponse();
            // ... copiar todos los campos ...
            dto.setFavorite(favoriteRepository.existsByUserIdAndAnuncioId(userId, anuncio.getId()));
            return dto;
        });
    }

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
}