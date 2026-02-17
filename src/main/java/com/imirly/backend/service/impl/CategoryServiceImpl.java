package com.imirly.backend.service.impl;

import com.imirly.backend.dto.response.CategoryResponse;
import com.imirly.backend.dto.response.SubcategoryResponse;
import com.imirly.backend.entity.Category;
import com.imirly.backend.entity.Subcategory;
import com.imirly.backend.repository.CategoryRepository;
import com.imirly.backend.service.CategoryService;
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
        if (categoryRepository.count() > 0) {
            System.out.println("Las categorías ya están inicializadas");
            return;
        }

        // HOGAR
        Category hogar = createCategory("hogar", "Hogar", "category_hogar.jpg");
        addSubcategories(hogar, List.of(
                createSub("limpieza", "Limpieza", "hogar_limpieza.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_limpieza\",\"label\":\"Tipo de limpieza\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Limpieza general\",\"Limpieza profunda\",\"Limpieza por horas\",\"Limpieza de fin de obra\"]},{\"id\":\"materiales\",\"label\":\"¿Aporta materiales?\",\"tipo\":\"boolean\"},{\"id\":\"experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"Sin experiencia\",\"1-2\",\"3-5\",\"6-10\",\"+10\"]}]"),
                createSub("plancha", "Plancha", "hogar_plancha.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_prendas\",\"label\":\"Tipo de prendas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Ropa diaria\",\"Camisas\",\"Prendas delicadas\",\"Ropa de cama\"]},{\"id\":\"servicio_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"}]"),
                createSub("mudanzas", "Mudanzas", "hogar_mudanzas.jpg",
                        "[{\"id\":\"precio_servicio\",\"label\":\"Precio orientativo (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_mudanza\",\"label\":\"Tipo de mudanza\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Local\",\"Provincial\",\"Nacional\",\"Internacional\"]},{\"id\":\"vehiculo\",\"label\":\"¿Aporta vehículo?\",\"tipo\":\"boolean\"}]"),
                createSub("fontaneria", "Fontanería", "hogar_fontaneria.jpg",
                        "[{\"id\":\"precio_servicio\",\"label\":\"Precio orientativo (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_trabajos\",\"label\":\"Trabajos realizados\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Fugas\",\"Atascos\",\"Instalaciones\",\"Reparaciones urgentes\"]},{\"id\":\"urgencias\",\"label\":\"Disponibilidad urgente\",\"tipo\":\"boolean\"},{\"id\":\"anos_experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"1-2\",\"3-5\",\"6-10\",\"+10\"]}]"),
                createSub("pintura", "Pintura", "hogar_pintura.jpg",
                        "[{\"id\":\"precio_metro\",\"label\":\"Precio por m² (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_pintura\",\"label\":\"Tipo de pintura\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Interior\",\"Exterior\",\"Texturizada\",\"Decorativa\"]},{\"id\":\"materiales\",\"label\":\"¿Aporta materiales?\",\"tipo\":\"boolean\"}]"),
                createSub("electricista", "Electricista", "hogar_electricista.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Instalaciones\",\"Reparaciones\",\"Iluminación\",\"Cuadros eléctricos\"]},{\"id\":\"urgencias\",\"label\":\"Atiendo urgencias\",\"tipo\":\"boolean\"}]"),
                createSub("electrodomesticos", "Electrodomésticos", "hogar_electrodomesticos.jpg",
                        "[{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_electrodomesticos\",\"label\":\"Electrodomésticos que repara\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Frigoríficos\",\"Lavadoras\",\"Hornos\",\"Lavavajillas\"]}]"),
                createSub("reformas", "Reformas", "hogar_reformas.jpg",
                        "[{\"id\":\"precio_presupuesto\",\"label\":\"Presupuesto base (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_reformas\",\"label\":\"Tipo de reformas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cocinas\",\"Baños\",\"Habitaciones\",\"Integral\"]}]"),
                createSub("jardineria", "Jardinería", "hogar_jardineria.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Poda\",\"Riego\",\"Diseño\",\"Mantenimiento\"]}]"),
                createSub("cerrajero", "Cerrajero", "hogar_cerrajero.jpg",
                        "[{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Aperturas\",\"Cambio de cerraduras\",\"Copias de llaves\",\"Sistemas de seguridad\"]},{\"id\":\"urgencias\",\"label\":\"Atiendo urgencias 24h\",\"tipo\":\"boolean\"}]"),
                createSub("climatizacion", "Climatización", "hogar_climatizacion.jpg",
                        "[{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_instalaciones\",\"label\":\"Tipo de instalaciones\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Aire acondicionado\",\"Calefacción\",\"Ventilación\",\"Mantenimiento\"]}]"),
                createSub("cocina", "Cocina", "hogar_cocina.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Chef a domicilio\",\"Catering\",\"Clases de cocina\",\"Menús especiales\"]}]")
        ));

        // CLASES
        Category clases = createCategory("clases", "Clases", "category_clases.jpg");
        addSubcategories(clases, List.of(
                createSub("colegio", "Colegio", "clases_colegio.jpg",
                        "[{\"id\":\"asignaturas\",\"label\":\"Asignaturas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Matemáticas\",\"Lengua\",\"Inglés\",\"Historia\",\"Ciencias\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"titulo\",\"label\":\"Profesor con título\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("idiomas", "Idiomas", "clases_idiomas.jpg",
                        "[{\"id\":\"idiomas\",\"label\":\"Idiomas que enseña\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Inglés\",\"Francés\",\"Alemán\",\"Italiano\",\"Chino\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\",\"Conversación\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("musica", "Música", "clases_musica.jpg",
                        "[{\"id\":\"instrumentos\",\"label\":\"Instrumentos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Guitarra\",\"Piano\",\"Batería\",\"Violín\",\"Canto\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("dibujo", "Dibujo", "clases_dibujo.jpg",
                        "[{\"id\":\"tecnicas\",\"label\":\"Técnicas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Lápiz\",\"Acuarela\",\"Óleo\",\"Digital\",\"Carboncillo\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("baile", "Baile", "clases_baile.jpg",
                        "[{\"id\":\"estilos\",\"label\":\"Estilos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Salsa\",\"Bachata\",\"Flamenco\",\"Tango\",\"Hip Hop\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("eso", "E.S.O", "clases_eso.jpg",
                        "[{\"id\":\"asignaturas\",\"label\":\"Asignaturas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Matemáticas\",\"Lengua\",\"Inglés\",\"Física\",\"Química\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]")
        ));

        // DEPORTE
        Category deporte = createCategory("deporte", "Deporte", "category_deporte.jpg");
        addSubcategories(deporte, List.of(
                createSub("boxeo", "Boxeo", "deporte_boxeo.jpg",
                        "[{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\",\"Competitivo\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("entrenador", "Entrenador Personal", "deporte_entrenador.jpg",
                        "[{\"id\":\"especialidad\",\"label\":\"Especialidad\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Fitness\",\"Yoga\",\"Pilates\",\"Running\"]},{\"id\":\"online\",\"label\":\"Entrenamientos online\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("yoga", "Yoga", "deporte_yoga.jpg",
                        "[{\"id\":\"estilos\",\"label\":\"Estilos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Hatha\",\"Vinyasa\",\"Ashtanga\",\"Yin\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("pilates", "Pilates", "deporte_pilates.jpg",
                        "[{\"id\":\"tipos\",\"label\":\"Tipos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Mat\",\"Máquinas\",\"Fusión\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("padel", "Pádel", "deporte_padel.jpg",
                        "[{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("tenis", "Tenis", "deporte_tenis.jpg",
                        "[{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]")
        ));

        // MASCOTAS
        Category mascotas = createCategory("mascotas", "Mascotas", "category_mascotas.jpg");
        addSubcategories(mascotas, List.of(
                createSub("paseador", "Paseador", "mascotas_paseador.jpg",
                        "[{\"id\":\"experiencia\",\"label\":\"Experiencia\",\"tipo\":\"select\",\"opciones\":[\"Sin experiencia\",\"1-2\",\"3-5\",\"6-10\",\"+10\"]},{\"id\":\"tipos_perros\",\"label\":\"Tipos de perros\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cachorros\",\"Senior\",\"PPP\",\"Gigantes (+40kg)\"]},{\"id\":\"precio_paseo\",\"label\":\"Precio por paseo (€)\",\"tipo\":\"number\"}]"),
                createSub("cuidador", "Cuidador", "mascotas_cuidador.jpg",
                        "[{\"id\":\"tipo_cuidado\",\"label\":\"Tipo de cuidado\",\"tipo\":\"checkbox-group\",\"opciones\":[\"A domicilio\",\"Guardería de día\",\"Alojamiento nocturno\"]},{\"id\":\"experiencia\",\"label\":\"Experiencia\",\"tipo\":\"select\",\"opciones\":[\"1-2\",\"3-5\",\"6-10\",\"+10\"]}]"),
                createSub("peluqueria_mascotas", "Peluquería", "mascotas_peluqueria.jpg",
                        "[{\"id\":\"tipo_mascotas\",\"label\":\"Tipos de mascotas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros\",\"Gatos\",\"Otros\"]},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Corte\",\"Baño\",\"Secado\",\"Cepillado\"]},{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"}]"),
                createSub("adiestrador", "Adiestrador", "mascotas_adiestrador.jpg",
                        "[{\"id\":\"tecnicas\",\"label\":\"Técnicas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Refuerzo positivo\",\"Obediencia básica\",\"Socialización\"]},{\"id\":\"experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"1-2\",\"3-5\",\"6-10\",\"+10\"]},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("conducta", "Modificación de conducta", "mascotas_conducta.jpg",
                        "[{\"id\":\"problemas\",\"label\":\"Problemas de conducta\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Agresividad\",\"Ansiedad\",\"Miedo\",\"Ladridos excesivos\"]},{\"id\":\"experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"1-2\",\"3-5\",\"6-10\",\"+10\"]},{\"id\":\"precio_consulta\",\"label\":\"Precio por consulta (€)\",\"tipo\":\"number\"}]")
        ));

        // BELLEZA
        Category belleza = createCategory("belleza", "Belleza", "category_belleza.jpg");
        addSubcategories(belleza, List.of(
                createSub("depilacion", "Depilación", "belleza_depilacion.jpg",
                        "[{\"id\":\"tecnica\",\"label\":\"Técnicas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cera\",\"Láser\",\"Hilo\",\"Cuchilla\"]},{\"id\":\"zonas\",\"label\":\"Zonas del cuerpo\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cejas\",\"Piernas\",\"Axilas\",\"Ingle\",\"Facial\"]},{\"id\":\"precio_zona\",\"label\":\"Precio por zona (€)\",\"tipo\":\"number\"}]"),
                createSub("unas", "Uñas", "belleza_unas.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Manicura\",\"Pedicura\",\"Uñas acrílicas\",\"Gel\",\"Decoración\"]},{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"}]"),
                createSub("peluqueria", "Peluquería", "belleza_peluqueria.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Corte\",\"Tinte\",\"Mechas\",\"Tratamientos\",\"Peinados\"]},{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"}]"),
                createSub("maquillaje", "Maquillaje", "belleza_maquillaje.jpg",
                        "[{\"id\":\"ocasiones\",\"label\":\"Ocasiones\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Bodas\",\"Eventos\",\"Fotografía\",\"Día a día\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"}]"),
                createSub("facial", "Facial", "belleza_facial.jpg",
                        "[{\"id\":\"tratamientos\",\"label\":\"Tratamientos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Limpieza\",\"Hidratación\",\"Antiedad\",\"Acné\"]},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("masajes", "Masajes", "belleza_masajes.jpg",
                        "[{\"id\":\"tipos\",\"label\":\"Tipos de masaje\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Relajante\",\"Deportivo\",\"Terapéutico\",\"Piedras calientes\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]")
        ));

        // CUIDADOS
        Category cuidados = createCategory("cuidados", "Cuidados", "category_cuidados.jpg");
        addSubcategories(cuidados, List.of(
                createSub("ninos", "Niños", "cuidados_ninos.jpg",
                        "[{\"id\":\"edad\",\"label\":\"Edades que cuida\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Bebés\",\"3-6 años\",\"7-12 años\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("ancianos", "Ancianos", "cuidados_ancianos.jpg",
                        "[{\"id\":\"tipo_cuidado\",\"label\":\"Tipo de cuidado\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Acompañamiento\",\"Higiene\",\"Medicaciones\"]},{\"id\":\"experiencia\",\"label\":\"Experiencia\",\"tipo\":\"select\",\"opciones\":[\"1-2\",\"3-5\",\"+5\"]}]")
        ));

        // OTROS
        Category otros = createCategory("otros", "Otros", "category_otros.jpg");
        addSubcategories(otros, List.of(
                createSub("foto", "Fotografía", "otros_foto.jpg",
                        "[{\"id\":\"tipo_fotografia\",\"label\":\"Tipo de fotografía\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Retratos\",\"Bodas\",\"Eventos\",\"Producto\",\"Paisajes\"]},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("video", "Vídeo", "otros_video.jpg",
                        "[{\"id\":\"tipo_video\",\"label\":\"Tipo de vídeo\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Eventos\",\"Corporativo\",\"Publicitario\",\"Bodas\"]},{\"id\":\"precio_proyecto\",\"label\":\"Precio por proyecto (€)\",\"tipo\":\"number\"}]"),
                createSub("tattoo", "Tattoo", "otros_tattoo.jpg",
                        "[{\"id\":\"estilos\",\"label\":\"Estilos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Tradicional\",\"Realista\",\"Acuarela\",\"Geométrico\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("informatica", "Informática", "otros_informatica.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Reparación\",\"Formateo\",\"Redes\",\"Web\"]},{\"id\":\"precio_servicio\",\"label\":\"Precio orientativo (€)\",\"tipo\":\"number\"}]"),
                createSub("redes_sociales", "Redes Sociales", "otros_redes.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Gestión\",\"Contenido\",\"Publicidad\",\"Community Management\"]},{\"id\":\"precio_mensual\",\"label\":\"Precio mensual (€)\",\"tipo\":\"number\"}]"),
                createSub("web", "Desarrollo Web", "otros_web.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Diseño\",\"Programación\",\"Mantenimiento\",\"SEO\"]},{\"id\":\"precio_proyecto\",\"label\":\"Precio por proyecto (€)\",\"tipo\":\"number\"}]")
        ));

        categoryRepository.saveAll(List.of(hogar, clases, deporte, mascotas, belleza, cuidados, otros));
        System.out.println("✅ Categorías inicializadas correctamente");
    }

    private Category createCategory(String codigo, String nombre, String imagen) {
        return Category.builder()
                .codigo(codigo)
                .nombre(nombre)
                .imagen(imagen)
                .build();
    }

    private Subcategory createSub(String codigo, String nombre, String imagen, String formConfigJson) {
        return Subcategory.builder()
                .codigo(codigo)
                .nombre(nombre)
                .imagen(imagen)
                .formConfigJson(formConfigJson)
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