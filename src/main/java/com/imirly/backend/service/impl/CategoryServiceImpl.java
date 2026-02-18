package com.imirly.backend.service.impl;

import com.imirly.backend.dto.response.CategoryResponse;
import com.imirly.backend.dto.response.SubcategoryResponse;
import com.imirly.backend.entity.Category;
import com.imirly.backend.entity.Subcategory;
import com.imirly.backend.repository.CategoryRepository;
import com.imirly.backend.repository.SubcategoryRepository; // AÑADIR
import com.imirly.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository; // AÑADIR ESTO

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
        // Siempre actualizar los formularios, incluso si las categorías existen
        System.out.println("🔄 Verificando/actualizando formularios de categorías...");

        if (categoryRepository.count() == 0) {
            // No hay categorías, crearlas todas
            crearTodasLasCategorias();
            System.out.println("✅ Categorías creadas con formularios dinámicos");
        } else {
            // Ya existen categorías, solo actualizar los formConfigJson
            actualizarFormConfigJson();
            System.out.println("✅ Formularios actualizados en categorías existentes");
        }
    }

    private void actualizarFormConfigJson() {
        // Mapa de código -> JSON para actualizar
        Map<String, String> formConfigs = Map.ofEntries(
                Map.entry("limpieza", "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_limpieza\",\"label\":\"Tipo de limpieza\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Limpieza general\",\"Limpieza profunda\",\"Limpieza por horas\",\"Limpieza de fin de obra\"]},{\"id\":\"materiales\",\"label\":\"¿Aporta materiales?\",\"tipo\":\"boolean\"},{\"id\":\"anos_experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"Sin experiencia\",\"1-2\",\"3-5\",\"6-10\",\"+10\"]}]"),
                Map.entry("plancha", "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_prendas\",\"label\":\"Tipo de prendas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Ropa diaria\",\"Camisas\",\"Prendas delicadas\",\"Ropa de cama\"]},{\"id\":\"servicio_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"}]"),
                Map.entry("montaje_muebles", "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_montajes\",\"label\":\"Tipo de montajes\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Muebles\",\"Cuadros\",\"Estanterías\",\"Armarios\"]},{\"id\":\"herramientas\",\"label\":\"¿Aporta herramientas?\",\"tipo\":\"boolean\"}]"),
                Map.entry("mudanzas", "[{\"id\":\"precio_servicio\",\"label\":\"Precio orientativo (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_mudanza\",\"label\":\"Tipo de mudanza\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Local\",\"Provincial\",\"Nacional\",\"Internacional\"]},{\"id\":\"vehiculo\",\"label\":\"¿Aporta vehículo?\",\"tipo\":\"boolean\"}]"),
                Map.entry("fontaneria", "[{\"id\":\"precio_servicio\",\"label\":\"Precio orientativo (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_trabajos\",\"label\":\"Trabajos realizados\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Fugas\",\"Atascos\",\"Instalaciones\",\"Reparaciones urgentes\"]},{\"id\":\"urgencias\",\"label\":\"Disponibilidad urgente\",\"tipo\":\"boolean\"},{\"id\":\"anos_experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"1-2\",\"3-5\",\"6-10\",\"+10\"]}]"),
                Map.entry("pintura", "[{\"id\":\"precio_metro\",\"label\":\"Precio por m² (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_pintura\",\"label\":\"Tipo de pintura\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Interior\",\"Exterior\",\"Texturizada\",\"Decorativa\"]},{\"id\":\"materiales\",\"label\":\"¿Aporta materiales?\",\"tipo\":\"boolean\"}]"),
                Map.entry("electricista", "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Instalaciones\",\"Reparaciones\",\"Iluminación\",\"Cuadros eléctricos\"]},{\"id\":\"urgencias\",\"label\":\"Atiendo urgencias\",\"tipo\":\"boolean\"}]"),
                Map.entry("electrodomesticos", "[{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_electrodomesticos\",\"label\":\"Electrodomésticos que repara\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Frigoríficos\",\"Lavadoras\",\"Hornos\",\"Lavavajillas\"]}]"),
                Map.entry("reformas", "[{\"id\":\"precio_presupuesto\",\"label\":\"Presupuesto base (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_reformas\",\"label\":\"Tipo de reformas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cocinas\",\"Baños\",\"Habitaciones\",\"Integral\"]}]"),
                Map.entry("jardineria", "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Poda\",\"Riego\",\"Diseño\",\"Mantenimiento\"]}]"),
                Map.entry("cerrajero", "[{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Aperturas\",\"Cambio de cerraduras\",\"Copias de llaves\",\"Sistemas de seguridad\"]},{\"id\":\"urgencias\",\"label\":\"Atiendo urgencias 24h\",\"tipo\":\"boolean\"}]"),
                Map.entry("climatizacion", "[{\"id\":\"precio_servicio\",\"label\":\"Precio por servicio (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_instalaciones\",\"label\":\"Tipo de instalaciones\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Aire acondicionado\",\"Calefacción\",\"Ventilación\",\"Mantenimiento\"]}]"),
                Map.entry("cocina", "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Chef a domicilio\",\"Catering\",\"Clases de cocina\",\"Menús especiales\"]}]"),
                Map.entry("eso", "[{\"id\":\"asignaturas\",\"label\":\"Asignaturas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Matemáticas\",\"Lengua\",\"Inglés\",\"Física\",\"Química\",\"Historia\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"titulo\",\"label\":\"Profesor con título\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("idiomas", "[{\"id\":\"idiomas\",\"label\":\"Idiomas que enseña\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Inglés\",\"Francés\",\"Alemán\",\"Italiano\",\"Chino\",\"Portugués\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\",\"Conversación\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("musica", "[{\"id\":\"instrumentos\",\"label\":\"Instrumentos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Guitarra\",\"Piano\",\"Batería\",\"Violín\",\"Canto\",\"Ukelele\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("dibujo", "[{\"id\":\"tecnicas\",\"label\":\"Técnicas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Lápiz\",\"Acuarela\",\"Óleo\",\"Digital\",\"Carboncillo\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("baile", "[{\"id\":\"estilos\",\"label\":\"Estilos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Salsa\",\"Bachata\",\"Flamenco\",\"Tango\",\"Hip Hop\",\"Kizomba\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("programacion", "[{\"id\":\"lenguajes\",\"label\":\"Lenguajes\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Python\",\"Java\",\"JavaScript\",\"C++\",\"PHP\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("diseno", "[{\"id\":\"software\",\"label\":\"Software\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Photoshop\",\"Illustrator\",\"Figma\",\"Canva\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("fotografia", "[{\"id\":\"tipo_fotografia\",\"label\":\"Tipo de fotografía\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Retrato\",\"Paisaje\",\"Producto\",\"Eventos\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("cocina_clases", "[{\"id\":\"tipo_cocina\",\"label\":\"Tipo de cocina\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Mediterránea\",\"Japonesa\",\"Mexicana\",\"Repostería\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("yoga", "[{\"id\":\"modalidades\",\"label\":\"Modalidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Hatha\",\"Vinyasa\",\"Ashtanga\",\"Pilates mat\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("oposiciones", "[{\"id\":\"oposiciones\",\"label\":\"Oposiciones\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Administrativo\",\"Justicia\",\"Sanidad\",\"Educación\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("autoescuela", "[{\"id\":\"permisos\",\"label\":\"Permisos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"B\",\"A2\",\"C\",\"D\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                Map.entry("paseador", "[{\"id\":\"precio_paseo\",\"label\":\"Precio por paseo (€)\",\"tipo\":\"number\"},{\"id\":\"duracion\",\"label\":\"Duración del paseo\",\"tipo\":\"select\",\"opciones\":[\"30 min\",\"45 min\",\"1 hora\"]},{\"id\":\"tipos_perros\",\"label\":\"Tipos de perros\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cachorros\",\"Adultos\",\"Senior\",\"PPP\"]},{\"id\":\"experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"Sin experiencia\",\"1-2\",\"3-5\",\"+5\"]}]"),
                Map.entry("cuidador", "[{\"id\":\"tipo_cuidado\",\"label\":\"Tipo de cuidado\",\"tipo\":\"checkbox-group\",\"opciones\":[\"A domicilio\",\"En casa del cuidador\",\"Paseos incluidos\"]},{\"id\":\"tipo_mascotas\",\"label\":\"Tipo de mascotas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros\",\"Gatos\",\"Pájaros\",\"Roedores\",\"Reptiles\"]},{\"id\":\"precio_dia\",\"label\":\"Precio por día (€)\",\"tipo\":\"number\"}]"),
                Map.entry("adiestrador", "[{\"id\":\"especialidades\",\"label\":\"Especialidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Obediencia básica\",\"Conducta problemática\",\"Agility\",\"Protección\"]},{\"id\":\"modalidad\",\"label\":\"Modalidad\",\"tipo\":\"checkbox-group\",\"opciones\":[\"A domicilio\",\"En centro\",\"Online\"]},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                Map.entry("peluqueria", "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Corte\",\"Baño\",\"Secado\",\"Cepillado\",\"Uñas\"]},{\"id\":\"tipo_mascotas\",\"label\":\"Tipo de mascotas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros pequeños\",\"Perros medianos\",\"Perros grandes\",\"Gatos\"]},{\"id\":\"precio_base\",\"label\":\"Precio base (€)\",\"tipo\":\"number\"}]"),
                Map.entry("vet_domicilio", "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Consulta general\",\"Vacunación\",\"Análisis\",\"Urgencias\"]},{\"id\":\"tipo_mascotas\",\"label\":\"Tipo de mascotas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros\",\"Gatos\",\"Exóticos\"]},{\"id\":\"precio_consulta\",\"label\":\"Precio consulta (€)\",\"tipo\":\"number\"}]"),
                Map.entry("gatos", "[{\"id\":\"problemas\",\"label\":\"Problemas a tratar\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Marcaje\",\"Agresividad\",\"Miedo\",\"Introducción hogar\"]},{\"id\":\"modalidad\",\"label\":\"Modalidad\",\"tipo\":\"checkbox-group\",\"opciones\":[\"A domicilio\",\"Online\"]},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                Map.entry("exoticos", "[{\"id\":\"tipo_animales\",\"label\":\"Tipo de animales\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Reptiles\",\"Aves\",\"Roedores\",\"Insectos\",\"Acuáticos\"]},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cuidado temporal\",\"Asesoramiento\",\"Veterinario especializado\"]},{\"id\":\"precio_dia\",\"label\":\"Precio por día (€)\",\"tipo\":\"number\"}]")
        );

        formConfigs.forEach((codigo, json) -> {
            subcategoryRepository.findByCodigo(codigo).ifPresent(sub -> {
                sub.setFormConfigJson(json);
                subcategoryRepository.save(sub);
                System.out.println("✅ Actualizado: " + codigo);
            });
        });
    }

    // ESTE ES EL MÉTODO QUE FALTABA - copia aquí todo tu código de crear categorías
    private void crearTodasLasCategorias() {
        // HOGAR
        Category hogar = createCategory("hogar", "Hogar y Reparaciones", "category_hogar.jpg");
        addSubcategories(hogar, List.of(
                createSub("limpieza", "Limpieza", "hogar_limpieza.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_limpieza\",\"label\":\"Tipo de limpieza\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Limpieza general\",\"Limpieza profunda\",\"Limpieza por horas\",\"Limpieza de fin de obra\"]},{\"id\":\"materiales\",\"label\":\"¿Aporta materiales?\",\"tipo\":\"boolean\"},{\"id\":\"anos_experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"Sin experiencia\",\"1-2\",\"3-5\",\"6-10\",\"+10\"]}]"),
                createSub("plancha", "Plancha", "hogar_plancha.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_prendas\",\"label\":\"Tipo de prendas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Ropa diaria\",\"Camisas\",\"Prendas delicadas\",\"Ropa de cama\"]},{\"id\":\"servicio_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"}]"),
                createSub("montaje_muebles", "Montaje de muebles", "hogar_montaje.jpg",
                        "[{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"},{\"id\":\"tipo_montajes\",\"label\":\"Tipo de montajes\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Muebles\",\"Cuadros\",\"Estanterías\",\"Armarios\"]},{\"id\":\"herramientas\",\"label\":\"¿Aporta herramientas?\",\"tipo\":\"boolean\"}]"),
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
        Category clases = createCategory("clases", "Clases y Formación", "category_clases.jpg");
        addSubcategories(clases, List.of(
                createSub("eso", "Clases particulares ESO/Bachillerato", "clases_eso.jpg",
                        "[{\"id\":\"asignaturas\",\"label\":\"Asignaturas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Matemáticas\",\"Lengua\",\"Inglés\",\"Física\",\"Química\",\"Historia\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"titulo\",\"label\":\"Profesor con título\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("idiomas", "Idiomas", "clases_idiomas.jpg",
                        "[{\"id\":\"idiomas\",\"label\":\"Idiomas que enseña\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Inglés\",\"Francés\",\"Alemán\",\"Italiano\",\"Chino\",\"Portugués\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\",\"Conversación\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("musica", "Música", "clases_musica.jpg",
                        "[{\"id\":\"instrumentos\",\"label\":\"Instrumentos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Guitarra\",\"Piano\",\"Batería\",\"Violín\",\"Canto\",\"Ukelele\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("dibujo", "Dibujo y pintura", "clases_dibujo.jpg",
                        "[{\"id\":\"tecnicas\",\"label\":\"Técnicas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Lápiz\",\"Acuarela\",\"Óleo\",\"Digital\",\"Carboncillo\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("baile", "Baile", "clases_baile.jpg",
                        "[{\"id\":\"estilos\",\"label\":\"Estilos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Salsa\",\"Bachata\",\"Flamenco\",\"Tango\",\"Hip Hop\",\"Kizomba\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("programacion", "Programación", "clases_programacion.jpg",
                        "[{\"id\":\"lenguajes\",\"label\":\"Lenguajes\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Python\",\"Java\",\"JavaScript\",\"C++\",\"PHP\"]},{\"id\":\"niveles\",\"label\":\"Niveles\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Principiante\",\"Intermedio\",\"Avanzado\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("diseno", "Diseño gráfico", "clases_diseno.jpg",
                        "[{\"id\":\"software\",\"label\":\"Software\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Photoshop\",\"Illustrator\",\"Figma\",\"Canva\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("fotografia", "Fotografía", "clases_fotografia.jpg",
                        "[{\"id\":\"tipo_fotografia\",\"label\":\"Tipo de fotografía\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Retrato\",\"Paisaje\",\"Producto\",\"Eventos\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("cocina", "Cocina", "clases_cocina.jpg",
                        "[{\"id\":\"tipo_cocina\",\"label\":\"Tipo de cocina\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Mediterránea\",\"Japonesa\",\"Mexicana\",\"Repostería\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("yoga", "Yoga y pilates", "clases_yoga.jpg",
                        "[{\"id\":\"modalidades\",\"label\":\"Modalidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Hatha\",\"Vinyasa\",\"Ashtanga\",\"Pilates mat\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("oposiciones", "Preparación oposiciones", "clases_oposiciones.jpg",
                        "[{\"id\":\"oposiciones\",\"label\":\"Oposiciones\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Administrativo\",\"Justicia\",\"Sanidad\",\"Educación\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("autoescuela", "Conductores", "clases_autoescuela.jpg",
                        "[{\"id\":\"permisos\",\"label\":\"Permisos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"B\",\"A2\",\"C\",\"D\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]")
        ));

        // MASCOTAS
        Category mascotas = createCategory("mascotas", "Mascotas", "category_mascotas.jpg");
        addSubcategories(mascotas, List.of(
                createSub("paseador", "Paseador de perros", "mascotas_paseador.jpg",
                        "[{\"id\":\"precio_paseo\",\"label\":\"Precio por paseo (€)\",\"tipo\":\"number\"},{\"id\":\"duracion\",\"label\":\"Duración del paseo\",\"tipo\":\"select\",\"opciones\":[\"30 min\",\"45 min\",\"1 hora\"]},{\"id\":\"tipos_perros\",\"label\":\"Tipos de perros\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cachorros\",\"Adultos\",\"Senior\",\"PPP\"]},{\"id\":\"experiencia\",\"label\":\"Años de experiencia\",\"tipo\":\"select\",\"opciones\":[\"Sin experiencia\",\"1-2\",\"3-5\",\"+5\"]}]"),
                createSub("cuidador", "Cuidador de mascotas", "mascotas_cuidador.jpg",
                        "[{\"id\":\"tipo_cuidado\",\"label\":\"Tipo de cuidado\",\"tipo\":\"checkbox-group\",\"opciones\":[\"A domicilio\",\"En casa del cuidador\",\"Paseos incluidos\"]},{\"id\":\"tipo_mascotas\",\"label\":\"Tipo de mascotas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros\",\"Gatos\",\"Pájaros\",\"Roedores\",\"Reptiles\"]},{\"id\":\"precio_dia\",\"label\":\"Precio por día (€)\",\"tipo\":\"number\"}]"),
                createSub("adiestrador", "Adiestrador", "mascotas_adiestrador.jpg",
                        "[{\"id\":\"especialidades\",\"label\":\"Especialidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Obediencia básica\",\"Conducta problemática\",\"Agility\",\"Protección\"]},{\"id\":\"modalidad\",\"label\":\"Modalidad\",\"tipo\":\"checkbox-group\",\"opciones\":[\"A domicilio\",\"En centro\",\"Online\"]},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("peluqueria", "Peluquería canina", "mascotas_peluqueria.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Corte\",\"Baño\",\"Secado\",\"Cepillado\",\"Uñas\"]},{\"id\":\"tipo_mascotas\",\"label\":\"Tipo de mascotas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros pequeños\",\"Perros medianos\",\"Perros grandes\",\"Gatos\"]},{\"id\":\"precio_base\",\"label\":\"Precio base (€)\",\"tipo\":\"number\"}]"),
                createSub("vet_domicilio", "Veterinario a domicilio", "mascotas_vet.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Consulta general\",\"Vacunación\",\"Análisis\",\"Urgencias\"]},{\"id\":\"tipo_mascotas\",\"label\":\"Tipo de mascotas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros\",\"Gatos\",\"Exóticos\"]},{\"id\":\"precio_consulta\",\"label\":\"Precio consulta (€)\",\"tipo\":\"number\"}]"),
                createSub("gatos", "Educador felino", "mascotas_gatos.jpg",
                        "[{\"id\":\"problemas\",\"label\":\"Problemas a tratar\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Marcaje\",\"Agresividad\",\"Miedo\",\"Introducción hogar\"]},{\"id\":\"modalidad\",\"label\":\"Modalidad\",\"tipo\":\"checkbox-group\",\"opciones\":[\"A domicilio\",\"Online\"]},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("exoticos", "Cuidado de animales exóticos", "mascotas_exoticos.jpg",
                        "[{\"id\":\"tipo_animales\",\"label\":\"Tipo de animales\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Reptiles\",\"Aves\",\"Roedores\",\"Insectos\",\"Acuáticos\"]},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cuidado temporal\",\"Asesoramiento\",\"Veterinario especializado\"]},{\"id\":\"precio_dia\",\"label\":\"Precio por día (€)\",\"tipo\":\"number\"}]")
        ));

        // BIENESTAR
        Category bienestar = createCategory("bienestar", "Bienestar y Salud", "category_bienestar.jpg");
        addSubcategories(bienestar, List.of(
                createSub("masajista", "Masajista", "bienestar_masajista.jpg",
                        "[{\"id\":\"tipo_masaje\",\"label\":\"Tipo de masaje\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Relajante\",\"Deportivo\",\"Descontracturante\",\"Piedras calientes\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("fisio", "Fisioterapeuta", "bienestar_fisio.jpg",
                        "[{\"id\":\"especialidades\",\"label\":\"Especialidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Deportiva\",\"Neurológica\",\"Respiratoria\",\"Pediátrica\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("personal_trainer", "Entrenador personal", "bienestar_trainer.jpg",
                        "[{\"id\":\"especialidades\",\"label\":\"Especialidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Pérdida de peso\",\"Musculación\",\"Cardio\",\"Rehabilitación\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"online\",\"label\":\"Entrenamiento online\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("nutricionista", "Nutricionista", "bienestar_nutricionista.jpg",
                        "[{\"id\":\"especialidades\",\"label\":\"Especialidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Pérdida de peso\",\"Deportiva\",\"Clínica\",\"Vegetariana/Vegana\"]},{\"id\":\"online\",\"label\":\"Consulta online\",\"tipo\":\"boolean\"},{\"id\":\"precio_consulta\",\"label\":\"Precio por consulta (€)\",\"tipo\":\"number\"}]"),
                createSub("psicologia", "Psicología", "bienestar_psicologia.jpg",
                        "[{\"id\":\"especialidades\",\"label\":\"Especialidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Ansiedad\",\"Depresión\",\"Pareja\",\"Infantil\",\"Coaching\"]},{\"id\":\"online\",\"label\":\"Sesión online\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("peluqueria_persona", "Peluquería y estética", "bienestar_peluqueria.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Corte\",\"Tinte\",\"Mechas\",\"Tratamientos\",\"Manicura\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_servicio\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("maquillaje", "Maquillaje", "bienestar_maquillaje.jpg",
                        "[{\"id\":\"tipo_evento\",\"label\":\"Tipo de evento\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Boda\",\"Evento\",\"Fotografía\",\"Día a día\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_servicio\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("yoga_terapeutico", "Yoga terapéutico", "bienestar_yoga.jpg",
                        "[{\"id\":\"especialidades\",\"label\":\"Especialidades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Dolor de espalda\",\"Estrés\",\"Embarazo\",\"Tercera edad\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]")
        ));

        // EVENTOS
        Category eventos = createCategory("eventos", "Eventos y Entretenimiento", "category_eventos.jpg");
        addSubcategories(eventos, List.of(
                createSub("foto_eventos", "Fotógrafo de eventos", "eventos_foto.jpg",
                        "[{\"id\":\"tipo_evento\",\"label\":\"Tipo de evento\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Boda\",\"Cumpleaños\",\"Corporativo\",\"Concierto\"]},{\"id\":\"duracion\",\"label\":\"Duración\",\"tipo\":\"select\",\"opciones\":[\"2 horas\",\"4 horas\",\"8 horas\",\"Todo el día\"]},{\"id\":\"precio\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("dj", "DJ", "eventos_dj.jpg",
                        "[{\"id\":\"tipo_evento\",\"label\":\"Tipo de evento\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Boda\",\"Fiesta privada\",\"Corporativo\",\"Bar/Discoteca\"]},{\"id\":\"equipamiento\",\"label\":\"Incluye equipamiento\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("musicos", "Músicos", "eventos_musicos.jpg",
                        "[{\"id\":\"tipo_musica\",\"label\":\"Tipo de música\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Clásica\",\"Jazz\",\"Pop/Rock\",\"Latina\",\"Flamenco\"]},{\"id\":\"formacion\",\"label\":\"Formación\",\"tipo\":\"select\",\"opciones\":[\"Solista\",\"Dúo\",\"Trío\",\"Cuarteto\",\"Banda completa\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("animadores", "Animadores", "eventos_animadores.jpg",
                        "[{\"id\":\"tipo_evento\",\"label\":\"Tipo de evento\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Infantil\",\"Adultos\",\"Familiar\",\"Corporativo\"]},{\"id\":\"actividades\",\"label\":\"Actividades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Juegos\",\"Magia\",\"Payasos\",\"Talleres\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("catering", "Catering", "eventos_catering.jpg",
                        "[{\"id\":\"tipo_comida\",\"label\":\"Tipo de comida\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Cóctel\",\"Buffet\",\"Banquete\",\"BBQ\"]},{\"id\":\"num_personas\",\"label\":\"Nº personas\",\"tipo\":\"select\",\"opciones\":[\"Hasta 20\",\"20-50\",\"50-100\",\"Más de 100\"]},{\"id\":\"precio_persona\",\"label\":\"Precio por persona (€)\",\"tipo\":\"number\"}]"),
                createSub("decoracion", "Decoración de eventos", "eventos_decoracion.jpg",
                        "[{\"id\":\"tipo_evento\",\"label\":\"Tipo de evento\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Boda\",\"Cumpleaños\",\"Bautizo\",\"Corporativo\"]},{\"id\":\"estilo\",\"label\":\"Estilo\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Clásico\",\"Moderno\",\"Rústico\",\"Temático\"]},{\"id\":\"precio_desde\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("wedding", "Wedding planner", "eventos_wedding.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Planificación completa\",\"Coordinación día B\",\"Proveedores\",\"Decoración\"]},{\"id\":\"num_invitados\",\"label\":\"Nº invitados\",\"tipo\":\"select\",\"opciones\":[\"Hasta 50\",\"50-100\",\"100-200\",\"Más de 200\"]},{\"id\":\"precio_desde\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("alquiler", "Alquiler de material", "eventos_alquiler.jpg",
                        "[{\"id\":\"tipo_material\",\"label\":\"Tipo de material\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Mobiliario\",\"Iluminación\",\"Sonido\",\"Carpas\",\"Vajilla\"]},{\"id\":\"entrega\",\"label\":\"Entrega y recogida\",\"tipo\":\"boolean\"},{\"id\":\"precio_dia\",\"label\":\"Precio por día (€)\",\"tipo\":\"number\"}]")
        ));

        // TECNOLOGÍA
        Category tecnologia = createCategory("tecnologia", "Tecnología", "category_tecnologia.jpg");
        addSubcategories(tecnologia, List.of(
                createSub("reparacion_pc", "Reparación de ordenadores", "tecno_pc.jpg",
                        "[{\"id\":\"tipo_reparacion\",\"label\":\"Tipo de reparación\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Hardware\",\"Software\",\"Virus\",\"Recuperación datos\"]},{\"id\":\"a_domicilio\",\"label\":\"Servicio a domicilio\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("reparacion_movil", "Reparación de móviles", "tecno_movil.jpg",
                        "[{\"id\":\"tipo_reparacion\",\"label\":\"Tipo de reparación\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Pantalla\",\"Batería\",\"Conector\",\"Software\",\"Desbloqueo\"]},{\"id\":\"garantia\",\"label\":\"Garantía\",\"tipo\":\"boolean\"},{\"id\":\"precio_desde\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("redes", "Instalación de redes", "tecno_redes.jpg",
                        "[{\"id\":\"tipo_red\",\"label\":\"Tipo de red\",\"tipo\":\"checkbox-group\",\"opciones\":[\"WiFi\",\"Cableada\",\"Cámaras seguridad\",\"Domótica\"]},{\"id\":\"tamanio\",\"label\":\"Tamaño instalación\",\"tipo\":\"select\",\"opciones\":[\"Hogar\",\"Oficina pequeña\",\"Oficina grande\",\"Edificio\"]},{\"id\":\"precio_desde\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("web", "Desarrollo web", "tecno_web.jpg",
                        "[{\"id\":\"tipo_web\",\"label\":\"Tipo de web\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Web corporativa\",\"Tienda online\",\"Blog\",\"Web app\"]},{\"id\":\"tecnologias\",\"label\":\"Tecnologías\",\"tipo\":\"checkbox-group\",\"opciones\":[\"WordPress\",\"Shopify\",\"React\",\"Laravel\",\"A medida\"]},{\"id\":\"precio_desde\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("soporte", "Soporte informático", "tecno_soporte.jpg",
                        "[{\"id\":\"tipo_soporte\",\"label\":\"Tipo de soporte\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Remoto\",\"Presencial\",\"Mantenimiento\",\"Consultoría\"]},{\"id\":\"modalidad\",\"label\":\"Modalidad\",\"tipo\":\"select\",\"opciones\":[\"Por hora\",\"Por proyecto\",\"Mensual\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("recuperacion", "Recuperación de datos", "tecno_recuperacion.jpg",
                        "[{\"id\":\"tipo_dispositivo\",\"label\":\"Tipo de dispositivo\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Disco duro\",\"SSD\",\"USB\",\"Tarjeta SD\",\"Móvil\"]},{\"id\":\"urgente\",\"label\":\"Servicio urgente\",\"tipo\":\"boolean\"},{\"id\":\"precio_desde\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("clases_info", "Clases de informática", "tecno_clases.jpg",
                        "[{\"id\":\"nivel\",\"label\":\"Nivel\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Básico\",\"Intermedio\",\"Avanzado\",\"Ofimática\",\"Programación\"]},{\"id\":\"online\",\"label\":\"Clases online\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]")
        ));

        // TRANSPORTE
        Category transporte = createCategory("transporte", "Transporte", "category_transporte.jpg");
        addSubcategories(transporte, List.of(
                createSub("transporte_muebles", "Transporte de muebles", "trans_muebles.jpg",
                        "[{\"id\":\"tipo_vehiculo\",\"label\":\"Tipo de vehículo\",\"tipo\":\"select\",\"opciones\":[\"Furgoneta\",\"Camión pequeño\",\"Camión grande\"]},{\"id\":\"servicios\",\"label\":\"Servicios incluidos\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Carga y descarga\",\"Desmontaje\",\"Montaje\",\"Embalaje\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("chofer", "Conductor privado", "trans_chofer.jpg",
                        "[{\"id\":\"tipo_servicio\",\"label\":\"Tipo de servicio\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Por horas\",\"Aeropuerto\",\"Eventos\",\"Viajes largos\"]},{\"id\":\"tipo_vehiculo\",\"label\":\"Tipo de vehículo\",\"tipo\":\"select\",\"opciones\":[\"Berlina\",\"SUV\",\"Furgoneta\",\"Premium\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("furgoneta", "Alquiler de furgonetas con conductor", "trans_furgoneta.jpg",
                        "[{\"id\":\"tamano\",\"label\":\"Tamaño\",\"tipo\":\"select\",\"opciones\":[\"Pequeña\",\"Mediana\",\"Grande\",\"Con plataforma\"]},{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Conductor\",\"Carga\",\"Mudanza completa\"]},{\"id\":\"precio_dia\",\"label\":\"Precio por día (€)\",\"tipo\":\"number\"}]"),
                createSub("escolar", "Transporte escolar", "trans_escolar.jpg",
                        "[{\"id\":\"tipo_vehiculo\",\"label\":\"Tipo de vehículo\",\"tipo\":\"select\",\"opciones\":[\"Coche\",\"Furgoneta\",\"Autobús pequeño\"]},{\"id\":\"acompanante\",\"label\":\"Con acompañante\",\"tipo\":\"boolean\"},{\"id\":\"precio_mes\",\"label\":\"Precio por mes (€)\",\"tipo\":\"number\"}]"),
                createSub("mensajeria", "Mensajería", "trans_mensajeria.jpg",
                        "[{\"id\":\"tipo_envio\",\"label\":\"Tipo de envío\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Urgente\",\"24h\",\"48h\",\"Internacional\"]},{\"id\":\"tamano\",\"label\":\"Tamaño máximo\",\"tipo\":\"select\",\"opciones\":[\"Sobre\",\"Paquete pequeño\",\"Paquete grande\",\"Pale\"]},{\"id\":\"precio_desde\",\"label\":\"Precio desde (€)\",\"tipo\":\"number\"}]"),
                createSub("transporte_mascotas", "Traslado de mascotas", "trans_mascotas.jpg",
                        "[{\"id\":\"tipo_mascota\",\"label\":\"Tipo de mascota\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Perros\",\"Gatos\",\"Exóticos\"]},{\"id\":\"tipo_viaje\",\"label\":\"Tipo de viaje\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Veterinario\",\"Aeropuerto\",\"Mudanza\",\"Paseo\"]},{\"id\":\"precio_trayecto\",\"label\":\"Precio por trayecto (€)\",\"tipo\":\"number\"}]")
        ));

        // OTROS
        Category otros = createCategory("otros", "Otros Servicios", "category_otros.jpg");
        addSubcategories(otros, List.of(
                createSub("traductor", "Traductor e intérprete", "otros_traductor.jpg",
                        "[{\"id\":\"idiomas\",\"label\":\"Idiomas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Inglés\",\"Francés\",\"Alemán\",\"Italiano\",\"Portugués\",\"Chino\"]},{\"id\":\"tipo_traduccion\",\"label\":\"Tipo de traducción\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Escrita\",\"Simultánea\",\"Consecutiva\",\"Jurada\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("guia", "Guía turístico", "otros_guia.jpg",
                        "[{\"id\":\"idiomas\",\"label\":\"Idiomas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Español\",\"Inglés\",\"Francés\",\"Alemán\",\"Italiano\"]},{\"id\":\"tipo_tour\",\"label\":\"Tipo de tour\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Histórico\",\"Gastronómico\",\"Nocturno\",\"A medida\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("ninera", "Niñera", "otros_ninera.jpg",
                        "[{\"id\":\"edades\",\"label\":\"Edades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Bebés\",\"Preescolar\",\"Primaria\",\"Adolescentes\"]},{\"id\":\"tareas\",\"label\":\"Tareas incluidas\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Solo cuidado\",\"Comidas\",\"Tareas\",\"Paseos\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("mayores", "Cuidado de mayores", "otros_mayores.jpg",
                        "[{\"id\":\"tipo_cuidado\",\"label\":\"Tipo de cuidado\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Compañía\",\"Ayuda higiene\",\"Administración medicación\",\"Paseos\"]},{\"id\":\"formacion\",\"label\":\"Formación sanitaria\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("gestor", "Gestor administrativo", "otros_gestor.jpg",
                        "[{\"id\":\"tramites\",\"label\":\"Trámites\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Hacienda\",\"Seguridad Social\",\"Subvenciones\",\"Herencias\",\"Vehículos\"]},{\"id\":\"online\",\"label\":\"Gestión online\",\"tipo\":\"boolean\"},{\"id\":\"precio_tramite\",\"label\":\"Precio por trámite (€)\",\"tipo\":\"number\"}]"),
                createSub("detective", "Detective privado", "otros_detective.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Infidelidades\",\"Localización personas\",\"Investigación empresas\",\"Vigilancia\"]},{\"id\":\"online\",\"label\":\"Informe online\",\"tipo\":\"boolean\"},{\"id\":\"precio_consulta\",\"label\":\"Precio consulta (€)\",\"tipo\":\"number\"}]"),
                createSub("seguridad", "Seguridad", "otros_seguridad.jpg",
                        "[{\"id\":\"tipo_servicio\",\"label\":\"Tipo de servicio\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Vigilancia\",\"Escolta\",\"Eventos\",\"Residencial\"]},{\"id\":\"licencia\",\"label\":\"Licencia de armas\",\"tipo\":\"boolean\"},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("coach", "Coach personal", "otros_coach.jpg",
                        "[{\"id\":\"especialidad\",\"label\":\"Especialidad\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Vida\",\"Ejecutivo\",\"Deportivo\",\"Pareja\",\"Nutrición\"]},{\"id\":\"online\",\"label\":\"Sesiones online\",\"tipo\":\"boolean\"},{\"id\":\"precio_sesion\",\"label\":\"Precio por sesión (€)\",\"tipo\":\"number\"}]"),
                createSub("organizacion", "Organización del hogar", "otros_organizacion.jpg",
                        "[{\"id\":\"servicios\",\"label\":\"Servicios\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Organización armarios\",\"Limpieza profunda\",\"Minimalismo\",\"Trasteros\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]"),
                createSub("weekend_sitter", "Babysitter de fin de semana", "otros_weekend.jpg",
                        "[{\"id\":\"horario\",\"label\":\"Horario\",\"tipo\":\"checkbox-group\",\"opciones\":[\"Viernes noche\",\"Sábado\",\"Domingo\",\"Fin de semana completo\"]},{\"id\":\"actividades\",\"label\":\"Actividades\",\"tipo\":\"checkbox-group\",\"opciones\":[\"En casa\",\"Salidas\",\"Dormir en casa\",\"Tareas escolares\"]},{\"id\":\"precio_hora\",\"label\":\"Precio por hora (€)\",\"tipo\":\"number\"}]")
        ));

        categoryRepository.saveAll(List.of(hogar, clases, mascotas, bienestar, eventos, tecnologia, transporte, otros));
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
                                .formConfigJson(sub.getFormConfigJson())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}