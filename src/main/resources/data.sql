-- ==================== CATEGORÍAS ====================
INSERT INTO categories (codigo, nombre, imagen, icono, orden) VALUES
                                                                  ('hogar', 'Hogar y Reparaciones', 'home.png', '🏠', 1),
                                                                  ('clases', 'Clases y Formacion', 'school.png', '📚', 2),
                                                                  ('mascotas', 'Mascotas', 'pets.png', '🐾', 3),
                                                                  ('bienestar', 'Bienestar y Salud', 'health.png', '💪', 4),
                                                                  ('eventos', 'Eventos y Entretenimiento', 'event.png', '🎉', 5),
                                                                  ('tecnologia', 'Tecnologia', 'computer.png', '💻', 6),
                                                                  ('transporte', 'Transporte', 'car.png', '🚗', 7),
                                                                  ('otros', 'Otros Servicios', 'build.png', '🔧', 8);

-- ==================== SUBCATEGORÍAS: HOGAR (category_id = 1) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (1, 'fontaneria', 'Fontaneria', 'plumbing.png', NULL),
                                                                                      (1, 'electricista', 'Electricista', 'electrical.png', NULL),
                                                                                      (1, 'cerrajero', 'Cerrajeria', 'lock.png', NULL),
                                                                                      (1, 'pintor', 'Pintura', 'paint.png', NULL),
                                                                                      (1, 'carpintero', 'Carpinteria', 'carpenter.png', NULL),
                                                                                      (1, 'limpieza', 'Limpieza', 'cleaning.png', NULL),
                                                                                      (1, 'jardineria', 'Jardineria', 'yard.png', NULL),
                                                                                      (1, 'albanileria', 'Albanileria', 'construction.png', NULL),
                                                                                      (1, 'montaje_muebles', 'Montaje de muebles', 'chair.png', NULL),
                                                                                      (1, 'climatizacion', 'Climatizacion', 'ac_unit.png', NULL),
                                                                                      (1, 'electrodomesticos', 'Electrodomesticos', 'kitchen.png', NULL),
                                                                                      (1, 'cristalero', 'Cristaleria', 'window.png', NULL),
                                                                                      (1, 'persianas', 'Persianas y toldos', 'blinds.png', NULL),
                                                                                      (1, 'antenista', 'Antenista', 'tv.png', NULL);

-- ==================== SUBCATEGORÍAS: CLASES (category_id = 2) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (2, 'eso', 'Clases particulares ESO/Bachillerato', 'school.png', NULL),
                                                                                      (2, 'idiomas', 'Idiomas', 'language.png', NULL),
                                                                                      (2, 'musica', 'Musica', 'music.png', NULL),
                                                                                      (2, 'dibujo', 'Dibujo y pintura', 'brush.png', NULL),
                                                                                      (2, 'baile', 'Baile', 'dance.png', NULL),
                                                                                      (2, 'programacion', 'Programacion', 'code.png', NULL),
                                                                                      (2, 'diseno', 'Diseno grafico', 'design.png', NULL),
                                                                                      (2, 'fotografia', 'Fotografia', 'camera.png', NULL),
                                                                                      (2, 'cocina', 'Cocina', 'restaurant.png', NULL),
                                                                                      (2, 'yoga', 'Yoga y pilates', 'yoga.png', NULL),
                                                                                      (2, 'oposiciones', 'Preparacion oposiciones', 'book.png', NULL),
                                                                                      (2, 'autoescuela', 'Conductores', 'drive.png', NULL);

-- ==================== SUBCATEGORÍAS: MASCOTAS (category_id = 3) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (3, 'paseador', 'Paseador de perros', 'pets.png', NULL),
                                                                                      (3, 'cuidador', 'Cuidador de mascotas', 'pet_care.png', NULL),
                                                                                      (3, 'adiestrador', 'Adiestrador', 'training.png', NULL),
                                                                                      (3, 'peluqueria', 'Peluqueria canina', 'grooming.png', NULL),
                                                                                      (3, 'vet_domicilio', 'Veterinario a domicilio', 'vet.png', NULL),
                                                                                      (3, 'gatos', 'Educador felino', 'cat.png', NULL),
                                                                                      (3, 'exoticos', 'Cuidado de animales exoticos', 'bug.png', NULL);

-- ==================== SUBCATEGORÍAS: BIENESTAR (category_id = 4) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (4, 'masajista', 'Masajista', 'spa.png', NULL),
                                                                                      (4, 'fisio', 'Fisioterapeuta', 'sports.png', NULL),
                                                                                      (4, 'personal_trainer', 'Entrenador personal', 'fitness.png', NULL),
                                                                                      (4, 'nutricionista', 'Nutricionista', 'nutrition.png', NULL),
                                                                                      (4, 'psicologia', 'Psicologia', 'psychology.png', NULL),
                                                                                      (4, 'peluqueria_persona', 'Peluqueria y estetica', 'face.png', NULL),
                                                                                      (4, 'maquillaje', 'Maquillaje', 'palette.png', NULL),
                                                                                      (4, 'yoga_terapeutico', 'Yoga terapeutico', 'yoga.png', NULL);

-- ==================== SUBCATEGORÍAS: EVENTOS (category_id = 5) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (5, 'foto_eventos', 'Fotografo de eventos', 'camera.png', NULL),
                                                                                      (5, 'dj', 'DJ', 'music.png', NULL),
                                                                                      (5, 'musicos', 'Musicos', 'group.png', NULL),
                                                                                      (5, 'animadores', 'Animadores', 'celebration.png', NULL),
                                                                                      (5, 'catering', 'Catering', 'food.png', NULL),
                                                                                      (5, 'decoracion', 'Decoracion de eventos', 'decor.png', NULL),
                                                                                      (5, 'wedding', 'Wedding planner', 'heart.png', NULL),
                                                                                      (5, 'alquiler', 'Alquiler de material', 'tools.png', NULL);

-- ==================== SUBCATEGORÍAS: TECNOLOGÍA (category_id = 6) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (6, 'reparacion_pc', 'Reparacion de ordenadores', 'computer.png', NULL),
                                                                                      (6, 'reparacion_movil', 'Reparacion de moviles', 'smartphone.png', NULL),
                                                                                      (6, 'redes', 'Instalacion de redes', 'wifi.png', NULL),
                                                                                      (6, 'web', 'Desarrollo web', 'web.png', NULL),
                                                                                      (6, 'soporte', 'Soporte informatico', 'support.png', NULL),
                                                                                      (6, 'recuperacion', 'Recuperacion de datos', 'restore.png', NULL),
                                                                                      (6, 'clases_info', 'Clases de informatica', 'school.png', NULL);

-- ==================== SUBCATEGORÍAS: TRANSPORTE (category_id = 7) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (7, 'transporte_muebles', 'Transporte de muebles', 'truck.png', NULL),
                                                                                      (7, 'chofer', 'Conductor privado', 'car.png', NULL),
                                                                                      (7, 'furgoneta', 'Alquiler de furgonetas con conductor', 'van.png', NULL),
                                                                                      (7, 'escolar', 'Transporte escolar', 'child.png', NULL),
                                                                                      (7, 'mensajeria', 'Mensajeria', 'mail.png', NULL),
                                                                                      (7, 'transporte_mascotas', 'Traslado de mascotas', 'pets.png', NULL);

-- ==================== SUBCATEGORÍAS: OTROS (category_id = 8) ====================
INSERT INTO subcategories (category_id, codigo, nombre, imagen, form_config_json) VALUES
                                                                                      (8, 'traductor', 'Traductor e interprete', 'translate.png', NULL),
                                                                                      (8, 'guia', 'Guia turistico', 'tour.png', NULL),
                                                                                      (8, 'ninera', 'Ninera', 'child.png', NULL),
                                                                                      (8, 'mayores', 'Cuidado de mayores', 'elderly.png', NULL),
                                                                                      (8, 'gestor', 'Gestor administrativo', 'folder.png', NULL),
                                                                                      (8, 'detective', 'Detective privado', 'search.png', NULL),
                                                                                      (8, 'seguridad', 'Seguridad', 'security.png', NULL),
                                                                                      (8, 'coach', 'Coach personal', 'trending.png', NULL),
                                                                                      (8, 'organizacion', 'Organizacion del hogar', 'cleaning.png', NULL),
                                                                                      (8, 'weekend_sitter', 'Babysitter de fin de semana', 'weekend.png', NULL);