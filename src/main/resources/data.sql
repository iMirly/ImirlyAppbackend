-- Limpiar tablas si es necesario
DELETE FROM anuncio_dias;
DELETE FROM favorites;
DELETE FROM anuncios;
DELETE FROM subcategories;
DELETE FROM categories;
DELETE FROM users;

-- Insertar usuario demo
INSERT INTO users (id, nombre, apellidos, email, dni, password, created_at)
VALUES (1, 'Usuario', 'Demo', 'demo@imirly.com', '12345678A', '1234', CURRENT_TIMESTAMP);

-- Insertar categorías
INSERT INTO categories (id, name, icon) VALUES
(1, 'Hogar', 'ic_home'),
(2, 'Clases', 'ic_clases'),
(3, 'Deporte', 'ic_deporte'),
(4, 'Mascotas', 'ic_mascotas'),
(5, 'Belleza', 'ic_belleza'),
(6, 'Cuidados', 'ic_cuidados'),
(7, 'Otros', 'ic_otros');

-- Insertar subcategorías
INSERT INTO subcategories (id, name, category_id) VALUES
-- Hogar (category_id = 1)
(1, 'Limpieza', 1),
(2, 'Fontanería', 1),
(3, 'Plancha', 1),
(4, 'Mudanzas', 1),
(5, 'Pintura', 1),
(6, 'Electricista', 1),
(7, 'Electrodomésticos', 1),
(8, 'Reformas', 1),
(9, 'Jardinería', 1),

-- Clases (category_id = 2)
(10, 'Colegio', 2),
(11, 'Idiomas', 2),
(12, 'Música', 2),
(13, 'Dibujo', 2),
(14, 'Baile', 2),
(15, 'E.S.O', 2),

-- Deporte (category_id = 3)
(16, 'Boxeo', 3),
(17, 'Entrenador personal', 3),
(18, 'Yoga', 3),
(19, 'Pilates', 3),
(20, 'Pádel', 3),
(21, 'Tenis', 3),

-- Mascotas (category_id = 4)
(22, 'Paseador', 4),
(23, 'Cuidador', 4),
(24, 'Peluquería', 4),
(25, 'Adiestrador', 4),
(26, 'Conducta', 4),

-- Belleza (category_id = 5)
(27, 'Depilación', 5),
(28, 'Uñas', 5),
(29, 'Peluquería', 5),
(30, 'Maquillaje', 5),
(31, 'Facial', 5),
(32, 'Masajes', 5),

-- Cuidados (category_id = 6)
(33, 'Niños', 6),
(34, 'Ancianos', 6),

-- Otros (category_id = 7)
(35, 'Foto', 7),
(36, 'Vídeo', 7),
(37, 'Tattoo', 7),
(38, 'Informática', 7),
(39, 'Redes sociales', 7),
(40, 'Web', 7);

-- Insertar anuncios para TODAS las subcategorías (3 anuncios por cada una)
-- Hogar: Limpieza (subcategory_id = 1)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(1, 'Limpieza profesional de hogares', 'Limpieza general y profunda con productos ecológicos', 'HORA', 12.50, 'Andalucía', 'Granada', 'Granada', '09:00:00', '18:00:00', '{"experiencia":"5 años","productos_ecologicos":true,"incluye_cristales":true}', true, CURRENT_TIMESTAMP, 1, 1),
(2, 'Limpieza de oficinas a medida', 'Limpieza integral para empresas y negocios', 'SERVICIO', 85.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '18:00:00', '22:00:00', '{"superficie":"hasta 200m2","materiales_incluidos":true,"horario_nocturno":true}', true, CURRENT_TIMESTAMP, 1, 1),
(3, 'Limpieza de comunidades y garajes', 'Limpieza profunda de zonas comunes', 'SERVICIO', 120.00, 'Cataluña', 'Barcelona', 'Barcelona', '08:00:00', '16:00:00', '{"maquinaria_profesional":true,"incluye_parking":true,"certificado_limpieza":true}', true, CURRENT_TIMESTAMP, 1, 1);

-- Hogar: Fontanería (subcategory_id = 2)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(4, 'Fontanero urgente 24 horas', 'Reparaciones de emergencia con garantía', 'SERVICIO', 55.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '00:00:00', '23:59:00', '{"urgencias_24h":true,"garantia_6meses":true,"desplazamiento_gratis":true}', true, CURRENT_TIMESTAMP, 1, 2),
(5, 'Instalación de baño completo', 'Diseño e instalación de cuartos de baño nuevos', 'PROYECTO', 1500.00, 'Andalucía', 'Málaga', 'Málaga', '08:00:00', '17:00:00', '{"diseno_gratuito":true,"materiales_calidad":true,"tiempo_entrega":"1 semana"}', true, CURRENT_TIMESTAMP, 1, 2),
(6, 'Mantenimiento de calderas', 'Revisiones y reparación de sistemas de calefacción', 'SERVICIO', 70.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '09:00:00', '19:00:00', '{"certificado_oficial":true,"marcas":["Junkers","Saunier Duval"],"mantenimiento_anual":true}', true, CURRENT_TIMESTAMP, 1, 2);

-- Hogar: Plancha (subcategory_id = 3)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(7, 'Planchado profesional a domicilio', 'Planchado con vapor para todo tipo de tejidos', 'HORA', 14.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '20:00:00', '{"tejidos_delicados":true,"vapor_profesional":true,"aromatizacion":true}', true, CURRENT_TIMESTAMP, 1, 3),
(8, 'Servicio completo lavandería', 'Lavado, secado y planchado de ropa', 'SERVICIO', 40.00, 'Andalucía', 'Sevilla', 'Sevilla', '09:00:00', '18:00:00', '{"recogida_domicilio":true,"productos_hypoalergenicos":true,"planchado_perfecto":true}', true, CURRENT_TIMESTAMP, 1, 3),
(9, 'Planchado para eventos especiales', 'Preparación de vestidos de novia y trajes de gala', 'SERVICIO', 60.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '08:00:00', '22:00:00', '{"especialidad_novias":true,"trabajo_minucioso":true,"entrega_urgente":true}', true, CURRENT_TIMESTAMP, 1, 3);

-- Hogar: Mudanzas (subcategory_id = 4)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(10, 'Mudanzas económicas para pisos', 'Mudanza completa con embalaje incluido', 'SERVICIO', 350.00, 'Andalucía', 'Sevilla', 'Sevilla', '08:00:00', '20:00:00', '{"embalaje_incluido":true,"seguro_rotura":true,"montaje_muebles":true}', true, CURRENT_TIMESTAMP, 1, 4),
(11, 'Mudanzas internacionales profesionales', 'Traslados a Europa con gestión de aduanas', 'PROYECTO', 2500.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '07:00:00', '23:00:00', '{"destinos_europa":true,"aduana_incluida":true,"almacenaje_temporal":true}', true, CURRENT_TIMESTAMP, 1, 4),
(12, 'Traslado de oficinas y empresas', 'Mudanza corporativa con mínimo tiempo de inactividad', 'PROYECTO', 1800.00, 'Cataluña', 'Barcelona', 'Barcelona', '20:00:00', '06:00:00', '{"horario_nocturno":true,"equipos_informaticos":true,"reinstalacion":true}', true, CURRENT_TIMESTAMP, 1, 4);

-- Hogar: Pintura (subcategory_id = 5)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(13, 'Pintura de interiores económica', 'Pintura de viviendas con materiales de calidad', 'SERVICIO', 8.50, 'Comunidad Valenciana', 'Valencia', 'Valencia', '08:00:00', '17:00:00', '{"preparacion_superficie":true,"pintura_antihumedad":true,"limpieza_final":true}', true, CURRENT_TIMESTAMP, 1, 5),
(14, 'Pintura artística decorativa', 'Murales y efectos especiales para hogares', 'HORA', 35.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '19:00:00', '{"tecnicas_especiales":true,"diseno_personalizado":true,"pintura_ecologica":true}', true, CURRENT_TIMESTAMP, 1, 5),
(15, 'Pintura de fachadas profesionales', 'Restauración y pintura exterior de edificios', 'PROYECTO', 1200.00, 'Andalucía', 'Málaga', 'Málaga', '07:00:00', '15:00:00', '{"trabajos_altura":true,"pintura_acrilica":true,"impermeabilizacion":true}', true, CURRENT_TIMESTAMP, 1, 5);

-- Hogar: Electricista (subcategory_id = 6)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(16, 'Electricista certificado urgente', 'Reparaciones eléctricas con certificado oficial', 'SERVICIO', 50.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '08:00:00', '22:00:00', '{"certificado_installer":true,"urgencias_24h":true,"reparacion_garantizada":true}', true, CURRENT_TIMESTAMP, 1, 6),
(17, 'Instalación eléctrica completa', 'Instalación nueva para viviendas y reformas', 'PROYECTO', 800.00, 'Andalucía', 'Granada', 'Granada', '09:00:00', '18:00:00', '{"cuadro_electrico":true,"tomas_red":true,"certificado_fin_obra":true}', true, CURRENT_TIMESTAMP, 1, 6),
(18, 'Instalación de domótica inteligente', 'Sistemas de automatización para el hogar', 'PROYECTO', 1500.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '19:00:00', '{"iluminacion_inteligente":true,"climatizacion_automatica":true,"control_voz":true}', true, CURRENT_TIMESTAMP, 1, 6);

-- Hogar: Electrodomésticos (subcategory_id = 7)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(19, 'Reparación de lavadoras urgente', 'Técnico especializado en línea blanca', 'SERVICIO', 65.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '09:00:00', '21:00:00', '{"marcas_especializadas":true,"piezas_originales":true,"diagnostico_gratis":true}', true, CURRENT_TIMESTAMP, 1, 7),
(20, 'Instalación de aire acondicionado', 'Montaje de splits y sistemas multisplit', 'SERVICIO', 120.00, 'Andalucía', 'Sevilla', 'Sevilla', '08:00:00', '18:00:00', '{"marcas_premium":true,"instalacion_limpia":true,"mantenimiento_incluido":true}', true, CURRENT_TIMESTAMP, 1, 7),
(21, 'Reparación de neveras y frigoríficos', 'Especialista en frío comercial y doméstico', 'SERVICIO', 75.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '20:00:00', '{"tecnico_certificado":true,"gas_refrigerante":true,"reparacion_garantizada":true}', true, CURRENT_TIMESTAMP, 1, 7);

-- Hogar: Reformas (subcategory_id = 8)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(22, 'Reforma integral de cocina', 'Diseño y ejecución de cocinas a medida', 'PROYECTO', 3500.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '08:00:00', '16:00:00', '{"diseno_3d":true,"electrodomesticos_incluidos":false,"tiempo_entrega":"3 semanas"}', true, CURRENT_TIMESTAMP, 1, 8),
(23, 'Reforma de baño completo', 'Renovación de baños con materiales premium', 'PROYECTO', 2800.00, 'Andalucía', 'Málaga', 'Málaga', '09:00:00', '17:00:00', '{"materiales_antideslizantes":true,"mamparas_cristal":true,"calefaccion_suelo":true}', true, CURRENT_TIMESTAMP, 1, 8),
(24, 'Reforma de local comercial', 'Adaptación de espacios para negocios', 'PROYECTO', 5000.00, 'Cataluña', 'Barcelona', 'Barcelona', '20:00:00', '06:00:00', '{"licencia_actividad":true,"horario_nocturno":true,"finalizacion_rapida":true}', true, CURRENT_TIMESTAMP, 1, 8);

-- Hogar: Jardinería (subcategory_id = 9)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(25, 'Mantenimiento de jardines semanal', 'Cuidado integral de plantas y césped', 'HORA', 18.00, 'Andalucía', 'Granada', 'Granada', '08:00:00', '14:00:00', '{"riego_automatico":true,"poda_especializada":true,"control_plagas":true}', true, CURRENT_TIMESTAMP, 1, 9),
(26, 'Diseño de jardines verticales', 'Creación de jardines para interiores y terrazas', 'PROYECTO', 850.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '18:00:00', '{"sistemas_riego":true,"plantas_autoctonas":true,"mantenimiento_incluido":true}', true, CURRENT_TIMESTAMP, 1, 9),
(27, 'Poda de árboles y palmeras', 'Servicio profesional de poda en altura', 'SERVICIO', 150.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '07:00:00', '15:00:00', '{"seguros_rc":true,"maquinaria_especializada":true,"retirada_residuos":true}', true, CURRENT_TIMESTAMP, 1, 9);

-- Clases: Colegio (subcategory_id = 10)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(28, 'Clases de matemáticas ESO', 'Profesor especializado en secundaria', 'HORA', 16.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '17:00:00', '21:00:00', '{"niveles_eso":true,"preparacion_examenes":true,"material_propio":true}', true, CURRENT_TIMESTAMP, 1, 10),
(29, 'Refuerzo escolar primaria', 'Apoyo en todas las materias para niños', 'HORA', 14.00, 'Andalucía', 'Sevilla', 'Sevilla', '16:00:00', '20:00:00', '{"primaria_completa":true,"tecnicas_estudio":true,"juegos_educativos":true}', true, CURRENT_TIMESTAMP, 1, 10),
(30, 'Clases de física y química', 'Especialista en ciencias para bachillerato', 'HORA', 18.00, 'Cataluña', 'Barcelona', 'Barcelona', '18:00:00', '22:00:00', '{"bachillerato_ciencias":true,"pau_selectividad":true,"laboratorio_virtual":true}', true, CURRENT_TIMESTAMP, 1, 10);

-- Clases: Idiomas (subcategory_id = 11)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(31, 'Clases de inglés conversacional', 'Profesor nativo con certificación CELTA', 'HORA', 20.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '09:00:00', '21:00:00', '{"nativo_ingles":true,"certificado_celta":true,"preparacion_examenes":true}', true, CURRENT_TIMESTAMP, 1, 11),
(32, 'Clases de francés online', 'Profesora nativa con método comunicativo', 'HORA', 18.00, 'Andalucía', 'Málaga', 'Málaga', '10:00:00', '20:00:00', '{"modalidad_online":true,"nivel_delf":true,"cultura_francesa":true}', true, CURRENT_TIMESTAMP, 1, 11),
(33, 'Clases de alemán intensivas', 'Preparación para certificados oficiales', 'HORA', 22.00, 'Cataluña', 'Barcelona', 'Barcelona', '08:00:00', '19:00:00', '{"certificados_goethe":true,"alemán_negocios":true,"inmersión_cultural":true}', true, CURRENT_TIMESTAMP, 1, 11);

-- Clases: Música (subcategory_id = 12)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(34, 'Clases de guitarra española', 'Método personalizado para todas las edades', 'HORA', 18.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '16:00:00', '22:00:00', '{"flamenco_clasico":true,"partituras_digitales":true,"grabacion_clases":true}', true, CURRENT_TIMESTAMP, 1, 12),
(35, 'Clases de piano para niños', 'Iniciación musical con método Montessori', 'HORA', 20.00, 'Andalucía', 'Granada', 'Granada', '17:00:00', '21:00:00', '{"metodo_montessori":true,"piano_proporcionado":true,"conciertos_alumnos":true}', true, CURRENT_TIMESTAMP, 1, 12),
(36, 'Clases de canto moderno', 'Técnica vocal para pop, rock y jazz', 'HORA', 25.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '10:00:00', '20:00:00', '{"tecnicas_respiracion":true,"microfono_estudio":true,"preparacion_castings":true}', true, CURRENT_TIMESTAMP, 1, 12);

-- Clases: Dibujo (subcategory_id = 13)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(37, 'Clases de dibujo artístico', 'Técnicas clásicas y contemporáneas', 'HORA', 15.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '19:00:00', '{"carboncillo_acuarela":true,"modelo_vivo":false,"exposicion_final":true}', true, CURRENT_TIMESTAMP, 1, 13),
(38, 'Clases de cómic y manga', 'Creación de personajes y narrativa gráfica', 'HORA', 18.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '11:00:00', '20:00:00', '{"estilo_manga":true,"digital_tradicional":true,"publicacion_online":true}', true, CURRENT_TIMESTAMP, 1, 13),
(39, 'Clases de pintura al óleo', 'Técnicas clásicas para principiantes', 'HORA', 22.00, 'Andalucía', 'Sevilla', 'Sevilla', '09:00:00', '18:00:00', '{"materiales_incluidos":true,"tecnicas_maestros":true,"lienzo_proporcionado":true}', true, CURRENT_TIMESTAMP, 1, 13);

-- Clases: Baile (subcategoría 14)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(40, 'Clases de salsa y bachata', 'Bailes latinos para todos los niveles', 'HORA', 12.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '19:00:00', '23:00:00', '{"pareja_suelto":true,"ritmos_latinos":true,"fiestas_practicas":true}', true, CURRENT_TIMESTAMP, 1, 14),
(41, 'Clases de flamenco para adultos', 'Técnica, compás y palmas', 'HORA', 15.00, 'Andalucía', 'Sevilla', 'Sevilla', '18:00:00', '22:00:00', '{"vestuario_flamenco":true,"castañuelas_propias":true,"tablao_final":true}', true, CURRENT_TIMESTAMP, 1, 14),
(42, 'Clases de ballet clásico', 'Formación técnica para todas las edades', 'HORA', 20.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '16:00:00', '21:00:00', '{"metodo_vaganova":true,"examenes_rad":true,"espejos_barra":true}', true, CURRENT_TIMESTAMP, 1, 14);

-- Clases: E.S.O (subcategoría 15)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(43, 'Refuerzo escolar ESO completo', 'Todas las materias de 1º a 4º ESO', 'HORA', 16.00, 'Cataluña', 'Barcelona', 'Barcelona', '17:00:00', '21:00:00', '{"todas_materias":true,"tecnica_estudio":true,"seguimiento_personal":true}', true, CURRENT_TIMESTAMP, 1, 15),
(44, 'Preparación exámenes ESO', 'Repaso intensivo antes de evaluaciones', 'HORA', 18.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '16:00:00', '22:00:00', '{"simulacros_examen":true,"resumenes_tematicos":true,"trucos_examen":true}', true, CURRENT_TIMESTAMP, 1, 15),
(45, 'Orientación académica ESO', 'Ayuda en elección de bachillerato y ciclos', 'SERVICIO', 40.00, 'Andalucía', 'Granada', 'Granada', '10:00:00', '18:00:00', '{"test_orientacion":true,"informacion_carreras":true,"asesoramiento_padres":true}', true, CURRENT_TIMESTAMP, 1, 15);

-- Deporte: Boxeo (subcategoría 16)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(46, 'Clases de boxeo para principiantes', 'Técnica básica y acondicionamiento físico', 'HORA', 20.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '07:00:00', '22:00:00', '{"material_incluido":true,"defensa_personal":true,"entrenamiento_funcional":true}', true, CURRENT_TIMESTAMP, 1, 16),
(47, 'Boxeo femenino', 'Clases exclusivas para mujeres', 'HORA', 18.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '21:00:00', '{"grupos_mujeres":true,"autodefensa":true,"entrenadora_mujer":true}', true, CURRENT_TIMESTAMP, 1, 16),
(48, 'Entrenamiento boxeo competitivo', 'Preparación para competición amateur', 'HORA', 25.00, 'Andalucía', 'Málaga', 'Málaga', '08:00:00', '20:00:00', '{"competicion_amateur":true,"nutricion_deportiva":true,"sparring_supervisado":true}', true, CURRENT_TIMESTAMP, 1, 16);

-- Deporte: Entrenador personal (subcategoría 17)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(49, 'Entrenador personal a domicilio', 'Rutinas personalizadas en tu hogar', 'HORA', 30.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '06:00:00', '22:00:00', '{"equipo_portatil":true,"plan_nutricional":true,"seguimiento_app":true}', true, CURRENT_TIMESTAMP, 1, 17),
(50, 'Entrenamiento para embarazadas', 'Ejercicio seguro durante el embarazo', 'HORA', 25.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '19:00:00', '{"especialidad_embarazo":true,"ejercicios_seguros":true,"preparacion_parto":true}', true, CURRENT_TIMESTAMP, 1, 17),
(51, 'Preparación física para oposiciones', 'Entrenamiento específico para pruebas físicas', 'PROYECTO', 300.00, 'Andalucía', 'Sevilla', 'Sevilla', '07:00:00', '21:00:00', '{"oposiciones_policia":true,"pruebas_especificas":true,"simulacros_examen":true}', true, CURRENT_TIMESTAMP, 1, 17);

-- Deporte: Yoga (subcategoría 18)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(52, 'Clases de Hatha Yoga', 'Yoga tradicional para la salud', 'HORA', 12.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '08:00:00', '20:00:00', '{"estilo_hatha":true,"material_proporcionado":true,"meditacion_final":true}', true, CURRENT_TIMESTAMP, 1, 18),
(53, 'Yoga para tercera edad', 'Clases suaves y adaptadas', 'HORA', 10.00, 'Andalucía', 'Granada', 'Granada', '10:00:00', '18:00:00', '{"adaptado_mayores":true,"sillas_apoyo":true,"mejora_movilidad":true}', true, CURRENT_TIMESTAMP, 1, 18),
(54, 'Yoga en la playa', 'Clases al aire libre en temporada estival', 'HORA', 15.00, 'Cataluña', 'Barcelona', 'Barcelona', '07:00:00', '10:00:00', '{"playa_barceloneta":true,"amanecer_atardecer":true,"material_incluido":true}', true, CURRENT_TIMESTAMP, 1, 18);

-- Deporte: Pilates (subcategoría 19)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(55, 'Pilates con máquinas reformer', 'Equipo completo para todos los niveles', 'HORA', 25.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '07:00:00', '22:00:00', '{"reformer_completo":true,"instructor_certificado":true,"grupos_reducidos":true}', true, CURRENT_TIMESTAMP, 1, 19),
(56, 'Pilates para dolor de espalda', 'Rehabilitación y fortalecimiento', 'HORA', 28.00, 'Andalucía', 'Málaga', 'Málaga', '09:00:00', '20:00:00', '{"especialidad_espalda":true,"colaboracion_fisioterapeutas":true,"mejora_postural":true}', true, CURRENT_TIMESTAMP, 1, 19),
(57, 'Pilates prenatal', 'Preparación para el parto y recuperación', 'HORA', 22.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '19:00:00', '{"embarazo_seguro":true,"ejercicios_parto":true,"grupos_mujeres":true}', true, CURRENT_TIMESTAMP, 1, 19);

-- Deporte: Pádel (subcategoría 20)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(58, 'Clases de pádel para adultos', 'Técnica, táctica y partidos dirigidos', 'HORA', 25.00, 'Andalucía', 'Sevilla', 'Sevilla', '08:00:00', '23:00:00', '{"pista_incluida":true,"pelotas_nuevas":true,"video_analisis":true}', true, CURRENT_TIMESTAMP, 1, 20),
(59, 'Clases de pádel para niños', 'Escuela infantil de pádel', 'HORA', 18.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '16:00:00', '20:00:00', '{"material_adaptado":true,"metodo_juego":true,"competiciones_infantiles":true}', true, CURRENT_TIMESTAMP, 1, 20),
(60, 'Entrenamiento pádel avanzado', 'Perfeccionamiento para jugadores experimentados', 'HORA', 35.00, 'Cataluña', 'Barcelona', 'Barcelona', '07:00:00', '22:00:00', '{"analisis_rival":true,"estrategia_partido":true,"preparacion_fisica":true}', true, CURRENT_TIMESTAMP, 1, 20);

-- Deporte: Tenis (subcategoría 21)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(61, 'Clases de tenis para principiantes', 'Técnica básica desde cero', 'HORA', 22.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '08:00:00', '22:00:00', '{"raquetas_prestadas":true,"pelotas_nuevas":true,"pista_incluida":true}', true, CURRENT_TIMESTAMP, 1, 21),
(62, 'Escuela de tenis infantil', 'Formación desde los 4 años', 'HORA', 20.00, 'Andalucía', 'Granada', 'Granada', '16:00:00', '20:00:00', '{"metodo_play_tennis":true,"circuitos_coordinacion":true,"torneos_infantiles":true}', true, CURRENT_TIMESTAMP, 1, 21),
(63, 'Entrenador personal de tenis', 'Mejora técnica individual', 'HORA', 40.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '07:00:00', '22:00:00', '{"video_analisis":true,"preparacion_mental":true,"entrenamiento_fisico":true}', true, CURRENT_TIMESTAMP, 1, 21);

-- Mascotas: Paseador (subcategoría 22)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(64, 'Paseos de perros por las mañanas', 'Paseos en grupo controlados', 'HORA', 10.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '07:00:00', '12:00:00', '{"grupos_separados":true,"seguro_rc":true,"fotos_paseo":true}', true, CURRENT_TIMESTAMP, 1, 22),
(65, 'Paseos individuales personalizados', 'Atención exclusiva para tu mascota', 'HORA', 15.00, 'Cataluña', 'Barcelona', 'Barcelona', '08:00:00', '20:00:00', '{"paseo_individual":true,"entrenamiento_basico":true,"reporte_detallado":true}', true, CURRENT_TIMESTAMP, 1, 22),
(66, 'Paseos para perros mayores', 'Cuidados especiales para perros senior', 'HORA', 12.00, 'Andalucía', 'Sevilla', 'Sevilla', '09:00:00', '18:00:00', '{"especialidad_senior":true,"ritmo_adaptado":true,"medicacion_incluida":true}', true, CURRENT_TIMESTAMP, 1, 22);

-- Mascotas: Cuidador (subcategoría 23)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(67, 'Cuidado de mascotas en vacaciones', 'Alojamiento familiar para tu perro', 'DIA', 25.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '08:00:00', '20:00:00', '{"jardin_cerrado":true,"capacidad_5perros":true,"envio_fotos":true}', true, CURRENT_TIMESTAMP, 1, 23),
(68, 'Guardería diurna para perros', 'Cuidado durante tu jornada laboral', 'DIA', 18.00, 'Cataluña', 'Barcelona', 'Barcelona', '07:00:00', '19:00:00', '{"socializacion_controlada":true,"juegos_grupo":true,"informe_diario":true}', true, CURRENT_TIMESTAMP, 1, 23),
(69, 'Cuidador a domicilio para gatos', 'Visitas diarias mientras estás fuera', 'SERVICIO', 15.00, 'Andalucía', 'Málaga', 'Málaga', '09:00:00', '21:00:00', '{"especialidad_gatos":true,"limpieza_areneros":true,"juegos_interactivos":true}', true, CURRENT_TIMESTAMP, 1, 23);

-- Mascotas: Peluquería (subcategoría 24)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(70, 'Peluquería canina a domicilio', 'Evita el estrés del transporte', 'SERVICIO', 30.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '09:00:00', '19:00:00', '{"domicilio_cliente":true,"productos_naturales":true,"corte_estilizado":true}', true, CURRENT_TIMESTAMP, 1, 24),
(71, 'Peluquería para gatos especializada', 'Manejo experto de razas felinas', 'SERVICIO', 35.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '10:00:00', '18:00:00', '{"gatos_exclusivo":true,"sedacion_natural":false,"experiencia_15anios":true}', true, CURRENT_TIMESTAMP, 1, 24),
(72, 'Baño y secado express', 'Servicio rápido para perros activos', 'SERVICIO', 20.00, 'Andalucía', 'Granada', 'Granada', '08:00:00', '20:00:00', '{"servicio_express":true,"secado_profesional":true,"cepillo_dientes":true}', true, CURRENT_TIMESTAMP, 1, 24);

-- Mascotas: Adiestrador (subcategoría 25)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(73, 'Adiestramiento básico para cachorros', 'Socialización y órdenes esenciales', 'PROYECTO', 200.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '18:00:00', '{"edad_minima_3meses":true,"socializacion":true,"propietario_participa":true}', true, CURRENT_TIMESTAMP, 1, 25),
(74, 'Corrección de conductas problemáticas', 'Especialista en problemas de comportamiento', 'HORA', 40.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '08:00:00', '20:00:00', '{"problemas_agresividad":true,"metodo_positive":true,"seguimiento_mensual":true}', true, CURRENT_TIMESTAMP, 1, 25),
(75, 'Adiestramiento para perros de trabajo', 'Preparación para perros de asistencia', 'PROYECTO', 500.00, 'Andalucía', 'Sevilla', 'Sevilla', '07:00:00', '19:00:00', '{"perros_asistencia":true,"certificacion_oficial":true,"entrenamiento_avanzado":true}', true, CURRENT_TIMESTAMP, 1, 25);

-- Mascotas: Conducta (subcategoría 26)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(76, 'Terapia conductual para mascotas', 'Especialista en problemas emocionales', 'HORA', 45.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '10:00:00', '19:00:00', '{"ansiedad_separacion":true,"miedos_fobias":true,"modificacion_conducta":true}', true, CURRENT_TIMESTAMP, 1, 26),
(77, 'Asesoramiento para adopciones', 'Preparación para nueva mascota', 'SERVICIO', 60.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '18:00:00', '{"pre_adopcion":true,"seleccion_mascota":true,"adaptacion_hogar":true}', true, CURRENT_TIMESTAMP, 1, 26),
(78, 'Terapia para mascotas traumatizadas', 'Rehabilitación emocional para rescates', 'HORA', 50.00, 'Andalucía', 'Málaga', 'Málaga', '09:00:00', '18:00:00', '{"especialidad_traumas":true,"paciencia_extrema":true,"colaboracion_protectoras":true}', true, CURRENT_TIMESTAMP, 1, 26);

-- Belleza: Depilación (subcategoría 27)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(79, 'Depilación láser diodo médico', 'Tecnología avanzada para resultados permanentes', 'SERVICIO', 50.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '10:00:00', '21:00:00', '{"tecnologia_diodo":true,"consulta_gratuita":true,"sesiones_paquete":true}', true, CURRENT_TIMESTAMP, 1, 27),
(80, 'Depilación con cera caliente', 'Técnica tradicional para todo tipo de piel', 'SERVICIO', 25.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '20:00:00', '{"cera_caliente":true,"productos_hypoalergenicos":true,"rapido_eficaz":true}', true, CURRENT_TIMESTAMP, 1, 27),
(81, 'Depilación masculina profesional', 'Especializada en hombres', 'SERVICIO', 35.00, 'Andalucía', 'Sevilla', 'Sevilla', '10:00:00', '19:00:00', '{"especialidad_hombres":true,"diseño_barba":true,"tratamiento_post":true}', true, CURRENT_TIMESTAMP, 1, 27);

-- Belleza: Uñas (subcategoría 28)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(82, 'Uñas acrílicas esculpidas', 'Técnica profesional con materiales premium', 'SERVICIO', 35.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '10:00:00', '20:00:00', '{"tecnica_acrylic":true,"decoracion_detallada":true,"retoques_incluidos":true}', true, CURRENT_TIMESTAMP, 1, 28),
(83, 'Manicura y pedicura spa', 'Tratamiento completo con masaje', 'SERVICIO', 45.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '09:00:00', '19:00:00', '{"spa_completo":true,"masaje_manos":true,"parafina_hidratante":true}', true, CURRENT_TIMESTAMP, 1, 28),
(84, 'Uñas de gel semipermanente', 'Esmaltado duradero hasta 3 semanas', 'SERVICIO', 28.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '20:00:00', '{"gel_semipermanente":true,"amplia_gama_colores":true,"remocion_gratuita":true}', true, CURRENT_TIMESTAMP, 1, 28);

-- Belleza: Peluquería (subcategoría 29)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(85, 'Peluquería unisex premium', 'Corte, color y tratamientos avanzados', 'SERVICIO', 35.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '09:00:00', '20:00:00', '{"productos_lujo":true,"tecnica_balayage":true,"asesoramiento_imagen":true}', true, CURRENT_TIMESTAMP, 1, 29),
(86, 'Barbería clásica vintage', 'Corte tradicional y afeitado con navaja', 'SERVICIO', 25.00, 'Andalucía', 'Granada', 'Granada', '09:00:00', '19:00:00', '{"afeitado_navaja":true,"toallas_calientes":true,"productos_vintage":true}', true, CURRENT_TIMESTAMP, 1, 29),
(87, 'Coloración profesional creativa', 'Técnicas de color avanzadas', 'SERVICIO', 60.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '20:00:00', '{"coloracion_artistica":true,"mexican_blowout":true,"tratamiento_keratina":true}', true, CURRENT_TIMESTAMP, 1, 29);

-- Belleza: Maquillaje (subcategoría 30)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(88, 'Maquillaje para novias', 'Prueba previa y servicio el día de la boda', 'SERVICIO', 120.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '07:00:00', '15:00:00', '{"prueba_previo":true,"accesorios_incluidos":true,"duracion_todo_dia":true}', true, CURRENT_TIMESTAMP, 1, 30),
(89, 'Maquillaje social y de eventos', 'Para comuniones, fiestas y galas', 'SERVICIO', 60.00, 'Andalucía', 'Sevilla', 'Sevilla', '08:00:00', '22:00:00', '{"eventos_especiales":true,"productos_larga_duracion":true,"toca_cabello":true}', true, CURRENT_TIMESTAMP, 1, 30),
(90, 'Clases de automaquillaje', 'Aprende a maquillarte como profesional', 'HORA', 40.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '19:00:00', '{"clases_personalizadas":true,"productos_muestra":true,"dossier_tecnicas":true}', true, CURRENT_TIMESTAMP, 1, 30);

-- Belleza: Facial (subcategoría 31)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(91, 'Limpieza facial profunda', 'Extracción profesional y tratamiento personalizado', 'SERVICIO', 55.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '10:00:00', '20:00:00', '{"extraccion_profesional":true,"mascarilla_personalizada":true,"diagnostico_piel":true}', true, CURRENT_TIMESTAMP, 1, 31),
(92, 'Tratamientos anti-edad avanzados', 'Tecnologías de última generación', 'SERVICIO', 85.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '21:00:00', '{"radiofrecuencia":true,"microcorrientes":true,"resultados_inmediatos":true}', true, CURRENT_TIMESTAMP, 1, 31),
(93, 'Facial hidratante intensivo', 'Hidratación profunda para piel seca', 'SERVICIO', 45.00, 'Andalucía', 'Málaga', 'Málaga', '10:00:00', '19:00:00', '{"acido_hialuronico":true,"mascarilla_hidratante":true,"piel_radiante":true}', true, CURRENT_TIMESTAMP, 1, 31);

-- Belleza: Masajes (subcategoría 32)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(94, 'Masajes descontracturantes', 'Alivio de tensiones musculares', 'HORA', 40.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '09:00:00', '22:00:00', '{"descontracturante_profundo":true,"aromaterapia":true,"musica_relajante":true}', true, CURRENT_TIMESTAMP, 1, 32),
(95, 'Masaje deportivo profesional', 'Pre y post competición', 'HORA', 45.00, 'Cataluña', 'Barcelona', 'Barcelona', '08:00:00', '21:00:00', '{"deportivo_especializado":true,"estiramientos":true,"recuperacion_rapida":true}', true, CURRENT_TIMESTAMP, 1, 32),
(96, 'Masaje relajante con piedras calientes', 'Terapia termal para reducir el estrés', 'HORA', 50.00, 'Andalucía', 'Granada', 'Granada', '10:00:00', '20:00:00', '{"piedras_basaltas":true,"aceites_esenciales":true,"ambiente_relajante":true}', true, CURRENT_TIMESTAMP, 1, 32);

-- Cuidados: Niños (subcategoría 33)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(97, 'Canguro profesional con experiencia', 'Cuidado de niños de todas las edades', 'HORA', 12.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '16:00:00', '23:00:00', '{"primeros_auxilios":true,"idiomas_ingles":true,"actividades_educativas":true}', true, CURRENT_TIMESTAMP, 1, 33),
(98, 'Cuidado de bebés especializado', 'Especialista en recién nacidos', 'HORA', 15.00, 'Cataluña', 'Barcelona', 'Barcelona', '08:00:00', '20:00:00', '{"experiencia_neonatos":true,"lactancia_materna":true,"estimulacion_temprana":true}', true, CURRENT_TIMESTAMP, 1, 33),
(99, 'Canguro para eventos y fiestas', 'Cuidado grupal para celebraciones', 'HORA', 18.00, 'Andalucía', 'Sevilla', 'Sevilla', '18:00:00', '02:00:00', '{"eventos_especiales":true,"grupos_numerosos":true,"juegos_organizados":true}', true, CURRENT_TIMESTAMP, 1, 33);

-- Cuidados: Ancianos (subcategoría 34)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(100, 'Cuidado de mayores con experiencia', 'Acompañamiento y cuidados básicos', 'HORA', 12.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '08:00:00', '16:00:00', '{"experiencia_15anios":true,"movilidad_reducida":true,"administracion_medicacion":true}', true, CURRENT_TIMESTAMP, 1, 34),
(101, 'Acompañante para actividades diarias', 'Paseos, compras y visitas médicas', 'HORA', 11.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '18:00:00', '{"vehiculo_propio":true,"acompanamiento_medico":true,"compra_medicamentos":true}', true, CURRENT_TIMESTAMP, 1, 34),
(102, 'Cuidados post-operatorios', 'Asistencia especializada tras intervenciones', 'HORA', 15.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '00:00:00', '23:59:00', '{"enfermera_cualificada":true,"cuidados_heridas":true,"seguimiento_medico":true}', true, CURRENT_TIMESTAMP, 1, 34);

-- Otros: Foto (subcategoría 35)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(103, 'Fotógrafo para bodas y eventos', 'Cobertura completa con álbum digital', 'PROYECTO', 800.00, 'Andalucía', 'Málaga', 'Málaga', '08:00:00', '00:00:00', '{"fotos_editadas":true,"album_digital":true,"segundo_fotografo":true}', true, CURRENT_TIMESTAMP, 1, 35),
(104, 'Sesiones de fotos para redes sociales', 'Contenido profesional para Instagram', 'SERVICIO', 150.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '10:00:00', '18:00:00', '{"contenido_instagram":true,"fotos_retocadas":true,"plazo_3dias":true}', true, CURRENT_TIMESTAMP, 1, 35),
(105, 'Fotografía de producto profesional', 'Para catálogos y tiendas online', 'SERVICIO', 200.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '19:00:00', '{"estudio_profesional":true,"iluminacion_controlada":true,"recorte_fondo":true}', true, CURRENT_TIMESTAMP, 1, 35);

-- Otros: Vídeo (subcategoría 36)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(106, 'Videógrafo para eventos sociales', 'Cobertura con equipo profesional', 'PROYECTO', 900.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '08:00:00', '00:00:00', '{"equipo_4k":true,"dron_aereo":true,"edicion_completa":true}', true, CURRENT_TIMESTAMP, 1, 36),
(107, 'Videos corporativos y publicitarios', 'Producción para empresas', 'PROYECTO', 1500.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '19:00:00', '{"guion_profesional":true,"locucion":true,"motion_graphics":true}', true, CURRENT_TIMESTAMP, 1, 36),
(108, 'Videos para redes sociales', 'Contenido viral para TikTok/Instagram', 'SERVICIO', 300.00, 'Andalucía', 'Sevilla', 'Sevilla', '10:00:00', '18:00:00', '{"formato_vertical":true,"edicion_rapida":true,"musica_licencia":true}', true, CURRENT_TIMESTAMP, 1, 36);

-- Otros: Tattoo (subcategoría 37)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(109, 'Tatuador estilo realista', 'Especialista en retratos y detalles', 'HORA', 120.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '12:00:00', '20:00:00', '{"estilo_realismo":true,"material_esteril":true,"diseno_personalizado":true}', true, CURRENT_TIMESTAMP, 1, 37),
(110, 'Tatuajes minimalistas', 'Diseños pequeños y delicados', 'SERVICIO', 80.00, 'Cataluña', 'Barcelona', 'Barcelona', '11:00:00', '20:00:00', '{"estilo_minimalista":true,"lineas_finas":true,"color_blanconegro":true}', true, CURRENT_TIMESTAMP, 1, 37),
(111, 'Tatuajes de acuarela artística', 'Técnica pictórica única', 'HORA', 150.00, 'Andalucía', 'Granada', 'Granada', '10:00:00', '19:00:00', '{"tecnica_acuarela":true,"efectos_pintura":true,"colores_vibrantes":true}', true, CURRENT_TIMESTAMP, 1, 37);

-- Otros: Informática (subcategoría 38)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(112, 'Reparación de ordenadores a domicilio', 'Arreglo de hardware y software', 'SERVICIO', 40.00, 'Comunidad Valenciana', 'Valencia', 'Valencia', '09:00:00', '21:00:00', '{"diagnostico_gratis":true,"virus_malware":true,"recuperacion_datos":true}', true, CURRENT_TIMESTAMP, 1, 38),
(113, 'Asesoramiento informático para mayores', 'Paciencia y explicaciones claras', 'HORA', 20.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '18:00:00', '{"paciencia_maxima":true,"explicaciones_sencillas":true,"redes_sociales":true}', true, CURRENT_TIMESTAMP, 1, 38),
(114, 'Instalación de redes y WiFi', 'Optimización de conexión en el hogar', 'SERVICIO', 60.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '09:00:00', '19:00:00', '{"wifi_toda_casa":true,"repetidores":true,"seguridad_red":true}', true, CURRENT_TIMESTAMP, 1, 38);

-- Otros: Redes sociales (subcategoría 39)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(115, 'Community Manager para pymes', 'Gestión completa de redes sociales', 'SERVICIO', 300.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '09:00:00', '18:00:00', '{"redes_completas":true,"creacion_contenido":true,"analisis_metricas":true}', true, CURRENT_TIMESTAMP, 1, 39),
(116, 'Estrategia de marketing digital', 'Planificación de crecimiento online', 'PROYECTO', 500.00, 'Cataluña', 'Barcelona', 'Barcelona', '10:00:00', '19:00:00', '{"analisis_competencia":true,"plan_estrategico":true,"seguimiento_resultados":true}', true, CURRENT_TIMESTAMP, 1, 39),
(117, 'Creación de contenido para influencers', 'Fotos y textos para engagement', 'SERVICIO', 200.00, 'Andalucía', 'Sevilla', 'Sevilla', '09:00:00', '18:00:00', '{"contenido_viral":true,"copywriting":true,"calendario_editorial":true}', true, CURRENT_TIMESTAMP, 1, 39);

-- Otros: Web (subcategoría 40)
INSERT INTO anuncios (id, titulo, descripcion, price_type, price, comunidad_autonoma, provincia, localidad, hora_inicio, hora_fin, detalles_json, activo, created_at, user_id, subcategory_id) VALUES
(118, 'Desarrollo de páginas web WordPress', 'Diseño responsive y optimizado SEO', 'PROYECTO', 600.00, 'Andalucía', 'Málaga', 'Málaga', '09:00:00', '18:00:00', '{"wordpress_profesional":true,"diseno_responsive":true,"posicionamiento_seo":true}', true, CURRENT_TIMESTAMP, 1, 40),
(119, 'Tienda online e-commerce', 'Comercio electrónico completo', 'PROYECTO', 1200.00, 'Comunidad de Madrid', 'Madrid', 'Madrid', '10:00:00', '19:00:00', '{"ecommerce_completo":true,"pasarela_pago":true,"gestion_inventario":true}', true, CURRENT_TIMESTAMP, 1, 40),
(120, 'Mantenimiento web mensual', 'Actualizaciones y seguridad continua', 'SERVICIO', 100.00, 'Cataluña', 'Barcelona', 'Barcelona', '09:00:00', '18:00:00', '{"copias_seguridad":true,"actualizaciones":true,"monitoreo_seguridad":true}', true, CURRENT_TIMESTAMP, 1, 40);

-- Insertar días de disponibilidad para los primeros 20 anuncios (ejemplo)
INSERT INTO anuncio_dias (anuncio_id, dia) VALUES
(1, 'LUNES'), (1, 'MIERCOLES'), (1, 'VIERNES'),
(2, 'MARTES'), (2, 'JUEVES'), (2, 'SABADO'),
(3, 'LUNES'), (3, 'MARTES'), (3, 'MIERCOLES'), (3, 'JUEVES'), (3, 'VIERNES'),
(4, 'LUNES'), (4, 'MARTES'), (4, 'MIERCOLES'), (4, 'JUEVES'), (4, 'VIERNES'), (4, 'SABADO'), (4, 'DOMINGO'),
(5, 'LUNES'), (5, 'MIERCOLES'), (5, 'VIERNES'),
(6, 'MARTES'), (6, 'JUEVES'), (6, 'SABADO'),
(7, 'LUNES'), (7, 'MIERCOLES'), (7, 'VIERNES'),
(8, 'MARTES'), (8, 'JUEVES'), (8, 'SABADO'),
(9, 'LUNES'), (9, 'MIERCOLES'), (9, 'VIERNES'),
(10, 'LUNES'), (10, 'MARTES'), (10, 'MIERCOLES'), (10, 'JUEVES'), (10, 'VIERNES'),
(11, 'LUNES'), (11, 'MARTES'), (11, 'MIERCOLES'), (11, 'JUEVES'), (11, 'VIERNES'),
(12, 'LUNES'), (12, 'MARTES'), (12, 'MIERCOLES'), (12, 'JUEVES'), (12, 'VIERNES'),
(13, 'LUNES'), (13, 'MIERCOLES'), (13, 'VIERNES'),
(14, 'MARTES'), (14, 'JUEVES'), (14, 'SABADO'),
(15, 'LUNES'), (15, 'MARTES'), (15, 'MIERCOLES'), (15, 'JUEVES'), (15, 'VIERNES'),
(16, 'LUNES'), (16, 'MARTES'), (16, 'MIERCOLES'), (16, 'JUEVES'), (16, 'VIERNES'),
(17, 'LUNES'), (17, 'MIERCOLES'), (17, 'VIERNES'),
(18, 'MARTES'), (18, 'JUEVES'), (18, 'SABADO'),
(19, 'LUNES'), (19, 'MIERCOLES'), (19, 'VIERNES'),
(20, 'MARTES'), (20, 'JUEVES'), (20, 'SABADO');