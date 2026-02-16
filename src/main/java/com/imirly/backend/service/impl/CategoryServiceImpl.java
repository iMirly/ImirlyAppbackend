package com.imirly.backend.service.impl;

import com.imirly.backend.dto.response.CategoryResponse;
import com.imirly.backend.dto.response.SubcategoryResponse;
import com.imirly.backend.entity.Category;
import com.imirly.backend.entity.Subcategory;
import com.imirly.backend.repository.CategoryRepository;
import com.imirly.backend.service.CategoryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByNombreAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }

    @Override
    @Transactional
    public void seedCategories() {
        if (categoryRepository.count() > 0) return;

        // HOGAR
        Category hogar = createCategory("hogar", "Hogar", "category_hogar.jpg");
        addSubcategories(hogar, List.of(
                createSub("limpieza", "Limpieza", "hogar_limpieza.jpg"),
                createSub("plancha", "Plancha", "hogar_plancha.jpg"),
                createSub("mudanzas", "Mudanzas", "hogar_mudanzas.jpg"),
                createSub("fontaneria", "Fontanería", "hogar_fontaneria.jpg"),
                createSub("pintura", "Pintura", "hogar_pintura.jpg"),
                createSub("electricista", "Electricista", "hogar_electricista.jpg"),
                createSub("electrodomesticos", "Electrodomésticos", "hogar_electrodomesticos.jpg"),
                createSub("reformas", "Reformas", "hogar_reformas.jpg"),
                createSub("jardineria", "Jardinería", "hogar_jardineria.jpg"),
                createSub("cerrajero", "Cerrajero", "hogar_cerrajero.jpg"),
                createSub("climatizacion", "Climatización", "hogar_climatizacion.jpg"),
                createSub("cocina", "Cocina", "hogar_cocina.jpg")
        ));

        // CLASES
        Category clases = createCategory("clases", "Clases", "category_clases.jpg");
        addSubcategories(clases, List.of(
                createSub("colegio", "Colegio", "clases_colegio.jpg"),
                createSub("idiomas", "Idiomas", "clases_idiomas.jpg"),
                createSub("musica", "Música", "clases_musica.jpg"),
                createSub("dibujo", "Dibujo", "clases_dibujo.jpg"),
                createSub("baile", "Baile", "clases_baile.jpg"),
                createSub("eso", "E.S.O", "clases_eso.jpg")
        ));

        // DEPORTE
        Category deporte = createCategory("deporte", "Deporte", "category_deporte.jpg");
        addSubcategories(deporte, List.of(
                createSub("boxeo", "Boxeo", "deporte_boxeo.jpg"),
                createSub("entrenador", "Entrenador Personal", "deporte_entrenador.jpg"),
                createSub("yoga", "Yoga", "deporte_yoga.jpg"),
                createSub("pilates", "Pilates", "deporte_pilates.jpg"),
                createSub("padel", "Pádel", "deporte_padel.jpg"),
                createSub("tenis", "Tenis", "deporte_tenis.jpg")
        ));

        // MASCOTAS
        Category mascotas = createCategory("mascotas", "Mascotas", "category_mascotas.jpg");
        addSubcategories(mascotas, List.of(
                createSub("paseador", "Paseador", "mascotas_paseador.jpg"),
                createSub("cuidador", "Cuidador", "mascotas_cuidador.jpg"),
                createSub("peluqueria_mascotas", "Peluquería", "mascotas_peluqueria.jpg"),
                createSub("adiestrador", "Adiestrador", "mascotas_adiestrador.jpg"),
                createSub("conducta", "Conducta", "mascotas_conducta.jpg")
        ));

        // BELLEZA
        Category belleza = createCategory("belleza", "Belleza", "category_belleza.jpg");
        addSubcategories(belleza, List.of(
                createSub("depilacion", "Depilación", "belleza_depilacion.jpg"),
                createSub("unas", "Uñas", "belleza_unas.jpg"),
                createSub("peluqueria", "Peluquería", "belleza_peluqueria.jpg"),
                createSub("maquillaje", "Maquillaje", "belleza_maquillaje.jpg"),
                createSub("facial", "Facial", "belleza_facial.jpg"),
                createSub("masajes", "Masajes", "belleza_masajes.jpg")
        ));

        // CUIDADOS
        Category cuidados = createCategory("cuidados", "Cuidados", "category_cuidados.jpg");
        addSubcategories(cuidados, List.of(
                createSub("ninos", "Niños", "cuidados_ninos.jpg"),
                createSub("ancianos", "Ancianos", "cuidados_ancianos.jpg")
        ));

        // OTROS
        Category otros = createCategory("otros", "Otros", "category_otros.jpg");
        addSubcategories(otros, List.of(
                createSub("foto", "Fotografía", "otros_foto.jpg"),
                createSub("video", "Vídeo", "otros_video.jpg"),
                createSub("tattoo", "Tattoo", "otros_tattoo.jpg"),
                createSub("informatica", "Informática", "otros_informatica.jpg"),
                createSub("redes_sociales", "Redes Sociales", "otros_redes.jpg"),
                createSub("web", "Desarrollo Web", "otros_web.jpg")
        ));

        categoryRepository.saveAll(List.of(hogar, clases, deporte, mascotas, belleza, cuidados, otros));
    }

    private Category createCategory(String codigo, String nombre, String imagen) {
        return Category.builder()
                .codigo(codigo)
                .nombre(nombre)
                .imagen(imagen)
                .build();
    }

    private Subcategory createSub(String codigo, String nombre, String imagen) {
        return Subcategory.builder()
                .codigo(codigo)
                .nombre(nombre)
                .imagen(imagen)
                .build();
    }

    private void addSubcategories(Category category, List<Subcategory> subs) {
        subs.forEach(sub -> sub.setCategory(category));
        category.setSubcategories(subs);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .codigo(category.getCodigo())
                .nombre(category.getNombre())
                .imagen(category.getImagen())
                .subcategories(category.getSubcategories().stream()
                        .map(sub -> SubcategoryResponse.builder()
                                .id(sub.getId())
                                .codigo(sub.getCodigo())
                                .nombre(sub.getNombre())
                                .imagen(sub.getImagen())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}