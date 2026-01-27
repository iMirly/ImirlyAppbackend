package com.imirly.backend.controller;

import com.imirly.backend.dto.AnuncioDTO;
import com.imirly.backend.entity.Anuncio;
import com.imirly.backend.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anuncios")
@CrossOrigin(origins = "http://localhost:4200")
public class AnuncioController {

    @Autowired
    private AnuncioRepository anuncioRepository;

    // Obtener todos los anuncios como DTOs
    @GetMapping
    public List<AnuncioDTO> getAllAnuncios() {
        return anuncioRepository.findAll().stream()
                .map(AnuncioDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener anuncios por subcategoría ID como DTOs
    @GetMapping("/subcategoria/{subcategoryId}")
    public ResponseEntity<List<AnuncioDTO>> getAnunciosBySubcategory(@PathVariable Long subcategoryId) {
        List<AnuncioDTO> anuncios = anuncioRepository.findBySubcategoryId(subcategoryId).stream()
                .map(AnuncioDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(anuncios);
    }

    // Obtener anuncio por ID como DTO
    @GetMapping("/{id}")
    public ResponseEntity<AnuncioDTO> getAnuncioById(@PathVariable Long id) {
        Optional<Anuncio> anuncio = anuncioRepository.findById(id);
        return anuncio.map(a -> ResponseEntity.ok(new AnuncioDTO(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // También puedes mantener el endpoint para entidades si lo necesitas
    @GetMapping("/entidad/{id}")
    public ResponseEntity<Anuncio> getAnuncioEntidadById(@PathVariable Long id) {
        Optional<Anuncio> anuncio = anuncioRepository.findById(id);
        return anuncio.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}