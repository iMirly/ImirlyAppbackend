// ==================== CONFIGURACIÓN ====================
const API_URL = 'http://localhost:8080/api';
const H2_CONSOLE_URL = 'http://localhost:8080/h2-console';

// ==================== ESTADO GLOBAL ====================
let currentUser = null;
let authToken = localStorage.getItem('token');
let categorias = [];
let subcategorias = [];
let anuncioTemporal = null; // Guarda el anuncio creado en step 1
let misAnunciosCache = [];

// ==================== INICIALIZACIÓN ====================
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM cargado, inicializando...');

    if (authToken) {
        validateToken();
    } else {
        showScreen('auth');
    }

    // Cargar categorías después de asegurar que el DOM está listo
    setTimeout(() => {
        cargarCategorias();
    }, 100);
});

// ==================== NAVEGACIÓN ====================
function showScreen(screenName) {
    document.querySelectorAll('section').forEach(s => s.classList.add('hidden'));
    document.getElementById(`screen-${screenName}`).classList.remove('hidden');

    if (screenName === 'dashboard') {
        document.getElementById('btnLogout').classList.remove('hidden');
        document.getElementById('btnH2Console').classList.remove('hidden');
        loadMisAnuncios();
        loadUserProfile();
    } else {
        document.getElementById('btnLogout').classList.add('hidden');
        document.getElementById('btnH2Console').classList.add('hidden');
    }
}

function openH2Console() {
    window.open(H2_CONSOLE_URL, '_blank');
}

function goDashboard() {
    showScreen('dashboard');
}

function goExplore() {
    showScreen('explore');
    loadPublicAnuncios();
    cargarCategoriasFiltro();
}

function goExplore() {
    showScreen('explore');
    loadPublicAnuncios();
    cargarCategoriasFiltro();
}

// ==================== AUTH ====================
async function register() {
    clearErrors();

    const nombre = document.getElementById('regNombre').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;

    // Validaciones
    if (!nombre) return showError('regNombre', 'El nombre es obligatorio');
    if (!email) return showError('regEmail', 'El email es obligatorio');
    if (!password) return showError('regPassword', 'La contraseña es obligatoria');
    if (password.length < 6) return showError('regPassword', 'Mínimo 6 caracteres');
    if (password !== confirmPassword) return showError('regConfirmPassword', 'Las contraseñas no coinciden');

    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, email, password })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }

        const data = await response.json();
        loginSuccess(data);
    } catch (error) {
        showError('registerGeneral', error.message || 'Error al registrar');
    }
}

async function login() {
    clearErrors();

    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;

    if (!email) return showError('loginEmail', 'El email es obligatorio');
    if (!password) return showError('loginPassword', 'La contraseña es obligatoria');

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const text = await response.text();

        if (!response.ok) {
            console.error('ERROR BACKEND:', text);
            throw new Error(text || 'Error del servidor');
        }

        const data = JSON.parse(text);
        loginSuccess(data);

    } catch (error) {
        showError('loginGeneral', error.message);
    }
}


function loginSuccess(data) {
    authToken = data.token;
    currentUser = data;
    localStorage.setItem('token', authToken);
    document.getElementById('sessionInfo').textContent = `Logueado como ${data.email}`;
    showScreen('dashboard');
}

function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('token');
    document.getElementById('sessionInfo').textContent = '';
    showScreen('auth');
}

async function validateToken() {
    try {
        const response = await fetch(`${API_URL}/users/me`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (response.ok) {
            const user = await response.json();
            currentUser = user;
            document.getElementById('sessionInfo').textContent = `Logueado como ${user.email}`;
            showScreen('dashboard');
        } else {
            logout();
        }
    } catch {
        logout();
    }
}

// ==================== PERFIL ====================
async function loadUserProfile() {
    try {
        const response = await fetch(`${API_URL}/users/me`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        const user = await response.json();
        currentUser = user;

        document.getElementById('profileName').textContent = user.nombre;
        document.getElementById('profileEmail').textContent = user.email;

        const completenessBadge = document.getElementById('profileCompleteness');
        if (user.datosCompletos) {
            completenessBadge.textContent = '✓ Perfil completo';
            completenessBadge.className = 'badge complete';
        } else {
            completenessBadge.textContent = '⚠ Completa tu perfil para publicar';
            completenessBadge.className = 'badge incomplete';
        }

        // Rellenar formulario de edición
        document.getElementById('editTelefono').value = user.telefono || '';
        document.getElementById('editFechaNacimiento').value = user.fechaNacimiento || '';
        document.getElementById('editCalle').value = user.direccionCalle || '';
        document.getElementById('editCiudad').value = user.direccionCiudad || '';
        document.getElementById('editCP').value = user.direccionCodigoPostal || '';
        document.getElementById('editFoto').value = user.fotoPerfilUrl || '';

    } catch (error) {
        console.error('Error cargando perfil:', error);
    }
}

function showEditProfileForm() {
    document.getElementById('editProfileForm').classList.remove('hidden');
    document.getElementById('changePasswordForm').classList.add('hidden');
}

function hideEditProfileForm() {
    document.getElementById('editProfileForm').classList.add('hidden');
}

function showChangePasswordForm() {
    document.getElementById('changePasswordForm').classList.remove('hidden');
    document.getElementById('editProfileForm').classList.add('hidden');
}

function hideChangePasswordForm() {
    document.getElementById('changePasswordForm').classList.add('hidden');
}

async function updateProfile() {
    const data = {
        nombre: currentUser.nombre, // Mantener el nombre actual
        telefono: document.getElementById('editTelefono').value.trim(),
        fechaNacimiento: document.getElementById('editFechaNacimiento').value,
        direccionCalle: document.getElementById('editCalle').value.trim(),
        direccionCiudad: document.getElementById('editCiudad').value.trim(),
        direccionCodigoPostal: document.getElementById('editCP').value.trim(),
        fotoPerfilUrl: document.getElementById('editFoto').value.trim() || null
    };

    try {
        const response = await fetch(`${API_URL}/users/me`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) throw new Error('Error al actualizar');

        alert('✅ Perfil actualizado correctamente');
        hideEditProfileForm();
        loadUserProfile();
    } catch (error) {
        alert('❌ Error: ' + error.message);
    }
}

async function updatePassword() {
    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmNewPassword = document.getElementById('confirmNewPassword').value;

    if (!currentPassword || !newPassword || !confirmNewPassword) {
        return alert('Completa todos los campos');
    }

    if (newPassword !== confirmNewPassword) {
        return alert('Las contraseñas nuevas no coinciden');
    }

    try {
        const response = await fetch(`${API_URL}/users/me/password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ currentPassword, newPassword, confirmPassword: confirmNewPassword })
        });

        if (!response.ok) throw new Error('Error al cambiar contraseña');

        alert('✅ Contraseña cambiada correctamente');
        hideChangePasswordForm();
        document.getElementById('currentPassword').value = '';
        document.getElementById('newPassword').value = '';
        document.getElementById('confirmNewPassword').value = '';
    } catch (error) {
        alert('❌ Error: ' + error.message);
    }
}

async function deleteAccount() {
    if (!confirm('⚠️ ¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/users/me`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Error al eliminar cuenta');

        alert('Cuenta eliminada');
        logout();
    } catch (error) {
        alert('❌ Error: ' + error.message);
    }
}

// ==================== CATEGORÍAS Y SUBCATEGORÍAS ====================
async function cargarCategorias() {
    console.log('Cargando categorías...');

    try {
        const response = await fetch(`${API_URL}/categories`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        categorias = await response.json();
        console.log('Categorías cargadas:', categorias);

        const select = document.getElementById('anuncioCategoria');
        if (!select) {
            console.error('No se encontró el select de categorías');
            return;
        }

        select.innerHTML = '<option value="">Selecciona categoría</option>';

        if (Array.isArray(categorias) && categorias.length > 0) {
            categorias.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = cat.nombre;
                select.appendChild(option);
            });
            console.log('Categorías añadidas al select');
        } else {
            console.warn('No hay categorías disponibles o el formato es incorrecto');
        }
    } catch (error) {
        console.error('Error cargando categorías:', error);
        const select = document.getElementById('anuncioCategoria');
        if (select) {
            select.innerHTML = '<option value="">Error al cargar categorías</option>';
        }
    }
}

function cargarSubcategorias() {
    console.log('Cargando subcategorías...');

    const categoriaId = document.getElementById('anuncioCategoria').value;
    const select = document.getElementById('anuncioSubcategoria');

    if (!select) {
        console.error('No se encontró el select de subcategorías');
        return;
    }

    select.innerHTML = '<option value="">Selecciona subcategoría</option>';

    if (!categoriaId) {
        console.log('No hay categoría seleccionada');
        return;
    }

    console.log('Buscando categoría con ID:', categoriaId);
    console.log('Categorías disponibles:', categorias);

    const categoria = categorias.find(c => c.id == categoriaId);

    if (categoria) {
        console.log('Categoría encontrada:', categoria);

        if (categoria.subcategories && Array.isArray(categoria.subcategories)) {
            subcategorias = categoria.subcategories;
            console.log('Subcategorias:', subcategorias);

            subcategorias.forEach(sub => {
                const option = document.createElement('option');
                option.value = sub.id;
                option.textContent = sub.nombre;
                option.dataset.codigo = sub.codigo;
                select.appendChild(option);
            });
        } else {
            console.warn('La categoría no tiene subcategorías');
        }
    } else {
        console.error('No se encontró la categoría con ID:', categoriaId);
    }
}


function mostrarInfoSubcategoria() {
    const subcategoriaId = document.getElementById('anuncioSubcategoria').value;
    console.log('Subcategoría seleccionada:', subcategoriaId);
}

// ==================== CREAR ANUNCIO (2 PASOS) ====================
async function crearAnuncioStep1() {
    clearErrors();

    if (!currentUser.datosCompletos) {
        alert('⚠️ Debes completar tus datos personales antes de publicar un anuncio');
        showEditProfileForm();
        return;
    }

    const data = {
        titulo: document.getElementById('anuncioTitulo').value.trim(),
        descripcion: document.getElementById('anuncioDescripcion').value.trim(),
        precio: parseFloat(document.getElementById('anuncioPrecio').value),
        tipoPrecio: document.getElementById('anuncioTipoPrecio').value,
        ubicacion: document.getElementById('anuncioUbicacion').value.trim(),
        imagenPrincipalUrl: document.getElementById('anuncioImagen').value.trim() || null,
        categoryId: parseInt(document.getElementById('anuncioCategoria').value),
        subcategoryId: parseInt(document.getElementById('anuncioSubcategoria').value)
    };

    // Validaciones
    if (!data.titulo) return showError('anuncioGeneral', 'El título es obligatorio');
    if (!data.precio || data.precio <= 0) return showError('anuncioGeneral', 'El precio debe ser mayor a 0');
    if (!data.ubicacion) return showError('anuncioGeneral', 'La ubicación es obligatoria');
    if (!data.categoryId) return showError('anuncioGeneral', 'Selecciona una categoría');
    if (!data.subcategoryId) return showError('anuncioGeneral', 'Selecciona una subcategoría');

    try {
        const response = await fetch(`${API_URL}/anuncios/step1`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) throw new Error('Error al crear anuncio');

        anuncioTemporal = await response.json();

        // Mostrar paso 2
        document.getElementById('anuncioStep1').classList.add('hidden');
        document.getElementById('anuncioStep2').classList.remove('hidden');
        document.getElementById('anuncioStepIndicator').textContent = 'Paso 2 de 2: Detalles específicos del servicio';

        // Generar campos dinámicos según subcategoría
        generarCamposStep2(data.subcategoryId);

    } catch (error) {
        showError('anuncioGeneral', error.message);
    }
}

function generarCamposStep2(subcategoriaId) {
    const subcategoria = subcategorias.find(s => s.id == subcategoriaId);
    const container = document.getElementById('camposEspecificos');
    container.innerHTML = '';

    // Campos por defecto para todas las subcategorías
    const camposDefault = `
        <div class="campo-dinamico">
            <label>Años de experiencia</label>
            <select id="metaExperiencia">
                <option value="0-1">0-1 años</option>
                <option value="1-3">1-3 años</option>
                <option value="3-5">3-5 años</option>
                <option value="5-10">5-10 años</option>
                <option value="+10">Más de 10 años</option>
            </select>
        </div>

        <div class="campo-dinamico">
            <label>Disponibilidad</label>
            <div class="checkbox-group">
                <label><input type="checkbox" value="Lun" checked> Lun</label>
                <label><input type="checkbox" value="Mar" checked> Mar</label>
                <label><input type="checkbox" value="Mie" checked> Mié</label>
                <label><input type="checkbox" value="Jue" checked> Jue</label>
                <label><input type="checkbox" value="Vie" checked> Vie</label>
                <label><input type="checkbox" value="Sab"> Sáb</label>
                <label><input type="checkbox" value="Dom"> Dom</label>
            </div>
        </div>

        <div class="campo-dinamico">
            <label>
                <input type="checkbox" id="metaDomicilio">
                Ofrezco servicio a domicilio
            </label>
        </div>
    `;

    container.innerHTML = camposDefault;

    // Campos específicos según la subcategoría (ejemplos)
    if (subcategoria) {
        const codigo = subcategoria.codigo;

        if (['fontaneria', 'electricista', 'cerrajero'].includes(codigo)) {
            container.innerHTML += `
                <div class="campo-dinamico">
                    <label>
                        <input type="checkbox" id="metaUrgencias">
                        Atiendo urgencias 24h
                    </label>
                </div>
            `;
        }

        if (['colegio', 'idiomas', 'musica', 'dibujo', 'baile', 'eso'].includes(codigo)) {
            container.innerHTML += `
                <div class="campo-dinamico">
                    <label>
                        <input type="checkbox" id="metaOnline">
                        Ofrezco clases online
                    </label>
                </div>
                <div class="campo-dinamico">
                    <label>Titulación</label>
                    <input type="text" id="metaTitulacion" placeholder="Ej: Licenciado en...">
                </div>
            `;
        }

        if (['paseador', 'cuidador', 'adiestrador'].includes(codigo)) {
            container.innerHTML += `
                <div class="campo-dinamico">
                    <label>Tipos de mascotas</label>
                    <div class="checkbox-group">
                        <label><input type="checkbox" value="perros"> Perros</label>
                        <label><input type="checkbox" value="gatos"> Gatos</label>
                        <label><input type="checkbox" value="otros"> Otros</label>
                    </div>
                </div>
            `;
        }
    }
}

function volverStep1() {
    document.getElementById('anuncioStep2').classList.add('hidden');
    document.getElementById('anuncioStep1').classList.remove('hidden');
    document.getElementById('anuncioStepIndicator').textContent = 'Paso 1 de 2: Información general';
}

async function completarAnuncioStep2() {
    // Recoger datos de los campos dinámicos
    const metadata = {
        anosExperiencia: document.getElementById('metaExperiencia').value,
        disponibilidad: Array.from(document.querySelectorAll('.checkbox-group input[type="checkbox"]:checked'))
            .map(cb => cb.value),
        servicioDomicilio: document.getElementById('metaDomicilio').checked
    };

    // Campos opcionales según existan
    if (document.getElementById('metaUrgencias')) {
        metadata.urgencias = document.getElementById('metaUrgencias').checked;
    }
    if (document.getElementById('metaOnline')) {
        metadata.clasesOnline = document.getElementById('metaOnline').checked;
    }
    if (document.getElementById('metaTitulacion')) {
        metadata.titulacion = document.getElementById('metaTitulacion').value;
    }

    try {
        // Guardar metadatos
        const response = await fetch(`${API_URL}/anuncios/step2`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({
                anuncioId: anuncioTemporal.id,
                metadata: metadata
            })
        });

        if (!response.ok) throw new Error('Error al guardar detalles');

        // Publicar automáticamente
        await fetch(`${API_URL}/anuncios/${anuncioTemporal.id}/publicar`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        alert('✅ Anuncio publicado correctamente');

        // Resetear formulario
        resetFormularioAnuncio();
        loadMisAnuncios();

    } catch (error) {
        alert('❌ Error: ' + error.message);
    }
}

function resetFormularioAnuncio() {
    document.getElementById('anuncioStep2').classList.add('hidden');
    document.getElementById('anuncioStep1').classList.remove('hidden');
    document.getElementById('anuncioStepIndicator').textContent = 'Paso 1 de 2: Información general';

    document.getElementById('anuncioTitulo').value = '';
    document.getElementById('anuncioDescripcion').value = '';
    document.getElementById('anuncioPrecio').value = '';
    document.getElementById('anuncioUbicacion').value = '';
    document.getElementById('anuncioImagen').value = '';
    document.getElementById('anuncioCategoria').value = '';
    document.getElementById('anuncioSubcategoria').innerHTML = '<option value="">Selecciona subcategoría</option>';

    // IMPORTANTE: Resetear botones también
    resetBotonesFormulario();

    anuncioTemporal = null;
}

// ==================== MIS ANUNCIOS ====================
async function loadMisAnuncios() {
    try {
        const response = await fetch(`${API_URL}/anuncios/mis-anuncios`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        misAnunciosCache = await response.json();
        renderizarMisAnuncios(misAnunciosCache);
    } catch (error) {
        document.getElementById('misAnunciosList').innerHTML = '<p class="error">Error cargando anuncios</p>';
    }
}

function filtrarMisAnuncios(filtro) {
    if (filtro === 'todos') {
        renderizarMisAnuncios(misAnunciosCache);
    } else {
        const filtrados = misAnunciosCache.filter(a => a.status === filtro);
        renderizarMisAnuncios(filtrados);
    }
}

function renderizarMisAnuncios(anuncios) {
    const container = document.getElementById('misAnunciosList');

    if (anuncios.length === 0) {
        container.innerHTML = '<p class="muted">No tienes anuncios. ¡Crea uno!</p>';
        return;
    }

    container.innerHTML = anuncios.map(a => `
        <div class="anuncio-item">
            <img src="${a.imagenPrincipalUrl || 'https://via.placeholder.com/80'}" class="anuncio-imagen" alt="">
            <div class="anuncio-info">
                <div class="anuncio-titulo">${a.titulo}</div>
                <div class="anuncio-meta">
                    ${a.categoryNombre} → ${a.subcategoryNombre} |
                    ${a.ubicacion} |
                    <span class="status-badge status-${a.status}">${a.status}</span>
                </div>
                <div class="anuncio-precio">${a.precio}€ ${a.tipoPrecio.replace('POR_', '/').toLowerCase()}</div>
                <div class="muted small">❤️ ${a.favoritesCount} favoritos</div>
            </div>
            <div class="anuncio-actions">
                ${a.status === 'BORRADOR' ? `<button onclick="publicarAnuncio(${a.id})">Publicar</button>` : ''}
                ${a.status === 'PUBLICADO' ? `<button onclick="despublicarAnuncio(${a.id})">Despublicar</button>` : ''}
                ${a.status === 'DESPUBLICADO' ? `<button onclick="publicarAnuncio(${a.id})">Republicar</button>` : ''}
                <button class="secondary" onclick="editarAnuncio(${a.id})">Editar</button>
                <button class="danger" onclick="eliminarAnuncio(${a.id})">Eliminar</button>
            </div>
        </div>
    `).join('');
}

async function publicarAnuncio(id) {
    try {
        await fetch(`${API_URL}/anuncios/${id}/publicar`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        loadMisAnuncios();
    } catch (error) {
        alert('Error: ' + error.message);
    }
}

async function despublicarAnuncio(id) {
    try {
        await fetch(`${API_URL}/anuncios/${id}/despublicar`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        loadMisAnuncios();
    } catch (error) {
        alert('Error: ' + error.message);
    }
}

async function eliminarAnuncio(id) {
    if (!confirm('¿Eliminar este anuncio?')) return;

    try {
        await fetch(`${API_URL}/anuncios/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        loadMisAnuncios();
    } catch (error) {
        alert('Error: ' + error.message);
    }
}

// ==================== EDITAR ANUNCIO ====================

async function editarAnuncio(id) {
    console.log('Editando anuncio:', id);

    try {
        // 1. Obtener datos del anuncio
        const response = await fetch(`${API_URL}/anuncios/${id}/edit`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) {
            throw new Error('Error al cargar el anuncio');
        }

        const anuncio = await response.json();
        console.log('Datos del anuncio para editar:', anuncio);

        // 2. Cargar datos en el formulario de Paso 1
        cargarDatosEnFormulario(anuncio);

        // 3. Guardar el ID del anuncio que se está editando
        anuncioTemporal = {
            id: anuncio.id,
            editando: true,
            statusAnterior: anuncio.status
        };

        // 4. Mostrar el formulario de edición (Paso 1)
        document.getElementById('anuncioStep1').classList.remove('hidden');
        document.getElementById('anuncioStep2').classList.add('hidden');
        document.getElementById('anuncioStepIndicator').textContent = 'Editando anuncio - Paso 1 de 2';

        // 5. Cambiar el botón "Continuar" por "Guardar cambios"
        const btnContinuar = document.querySelector('#anuncioStep1 button[onclick="crearAnuncioStep1()"]');
        if (btnContinuar) {
            btnContinuar.textContent = 'Guardar cambios →';
            btnContinuar.setAttribute('onclick', 'guardarEdicionStep1()');
        }

        // Scroll al formulario
        document.getElementById('anuncioStep1').scrollIntoView({ behavior: 'smooth' });

    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar el anuncio: ' + error.message);
    }
}

async function guardarEdicionStep1() {
    clearErrors();

    const data = {
        titulo: document.getElementById('anuncioTitulo').value.trim(),
        descripcion: document.getElementById('anuncioDescripcion').value.trim(),
        precio: parseFloat(document.getElementById('anuncioPrecio').value),
        tipoPrecio: document.getElementById('anuncioTipoPrecio').value,
        ubicacion: document.getElementById('anuncioUbicacion').value.trim(),
        imagenPrincipalUrl: document.getElementById('anuncioImagen').value.trim() || null,
        categoryId: parseInt(document.getElementById('anuncioCategoria').value),
        subcategoryId: parseInt(document.getElementById('anuncioSubcategoria').value)
    };

    // Validaciones
    if (!data.titulo) return showError('anuncioGeneral', 'El título es obligatorio');
    if (!data.precio || data.precio <= 0) return showError('anuncioGeneral', 'El precio debe ser mayor a 0');
    if (!data.ubicacion) return showError('anuncioGeneral', 'La ubicación es obligatoria');
    if (!data.categoryId) return showError('anuncioGeneral', 'Selecciona una categoría');
    if (!data.subcategoryId) return showError('anuncioGeneral', 'Selecciona una subcategoría');

    try {
        // Actualizar el anuncio existente
        const response = await fetch(`${API_URL}/anuncios/${anuncioTemporal.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }

        const anuncioActualizado = await response.json();
        console.log('Anuncio actualizado:', anuncioActualizado);

        // Actualizar anuncioTemporal
        anuncioTemporal = { ...anuncioTemporal, ...anuncioActualizado };

        // Mostrar paso 2 para editar metadata
        document.getElementById('anuncioStep1').classList.add('hidden');
        document.getElementById('anuncioStep2').classList.remove('hidden');
        document.getElementById('anuncioStepIndicator').textContent = 'Editando anuncio - Paso 2 de 2: Detalles específicos';

        // Generar campos dinámicos y cargar valores existentes
        generarCamposStep2(data.subcategoryId);
        await cargarMetadataExistente(anuncioTemporal.id);

        // Cambiar botón del paso 2
        const btnPublicar = document.querySelector('#anuncioStep2 button[onclick="completarAnuncioStep2()"]');
        if (btnPublicar) {
            btnPublicar.textContent = 'Guardar y republicar';
            btnPublicar.setAttribute('onclick', 'guardarEdicionStep2()');
        }

    } catch (error) {
        showError('anuncioGeneral', error.message);
    }
}
async function guardarEdicionStep2() {
    // Recoger datos de los campos dinámicos
    const metadata = {
        anosExperiencia: document.getElementById('metaExperiencia').value,
        disponibilidad: Array.from(document.querySelectorAll('.checkbox-group input[type="checkbox"]:checked'))
            .map(cb => cb.value),
        servicioDomicilio: document.getElementById('metaDomicilio').checked
    };

    if (document.getElementById('metaUrgencias')) {
        metadata.urgencias = document.getElementById('metaUrgencias').checked;
    }
    if (document.getElementById('metaOnline')) {
        metadata.clasesOnline = document.getElementById('metaOnline').checked;
    }
    if (document.getElementById('metaTitulacion')) {
        metadata.titulacion = document.getElementById('metaTitulacion').value;
    }

    try {
        // Actualizar metadata
        const response = await fetch(`${API_URL}/anuncios/${anuncioTemporal.id}/metadata`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({
                anuncioId: anuncioTemporal.id,
                metadata: metadata
            })
        });

        if (!response.ok) throw new Error('Error al guardar detalles');

        // Republicar automáticamente
        await fetch(`${API_URL}/anuncios/${anuncioTemporal.id}/publicar`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        alert('✅ Anuncio actualizado y republicado correctamente');

        // Resetear formulario y recargar lista
        resetFormularioAnuncio();
        resetBotonesFormulario(); // Restaurar textos originales de botones
        loadMisAnuncios();

    } catch (error) {
        alert('❌ Error: ' + error.message);
    }
}

function resetBotonesFormulario() {
    // Restaurar botón del paso 1 (el que tiene onclick="guardarEdicionStep1()")
    const btnStep1 = document.querySelector('#anuncioStep1 button.primary');
    if (btnStep1) {
        btnStep1.textContent = 'Continuar →';
        btnStep1.onclick = crearAnuncioStep1; // Mejor usar onclick en lugar de setAttribute
    }

    // Restaurar botón del paso 2
    const btnStep2 = document.querySelector('#anuncioStep2 button.primary');
    if (btnStep2) {
        btnStep2.textContent = 'Publicar anuncio';
        btnStep2.onclick = completarAnuncioStep2;
    }

    // Limpiar flag de edición
    if (anuncioTemporal) {
        anuncioTemporal.editando = false;
    }
}

async function cargarMetadataExistente(anuncioId) {
    try {
        const response = await fetch(`${API_URL}/anuncios/${anuncioId}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) {
            throw new Error('Error al cargar metadata');
        }

        const anuncio = await response.json();
        console.log('Cargando metadata:', anuncio.metadata);

        if (anuncio.metadata) {
            const meta = anuncio.metadata;

            // Cargar años de experiencia
            const selectExperiencia = document.getElementById('metaExperiencia');
            if (selectExperiencia && meta.anosExperiencia) {
                selectExperiencia.value = meta.anosExperiencia;
            }

            // Cargar disponibilidad (checkboxes)
            if (meta.disponibilidad && Array.isArray(meta.disponibilidad)) {
                const checkboxes = document.querySelectorAll('.checkbox-group input[type="checkbox"]');
                checkboxes.forEach(cb => {
                    cb.checked = meta.disponibilidad.includes(cb.value);
                });
            }

            // Cargar servicio a domicilio
            const chkDomicilio = document.getElementById('metaDomicilio');
            if (chkDomicilio && meta.servicioDomicilio) {
                chkDomicilio.checked = meta.servicioDomicilio;
            }

            // Campos opcionales
            const chkUrgencias = document.getElementById('metaUrgencias');
            if (chkUrgencias && meta.urgencias) {
                chkUrgencias.checked = meta.urgencias;
            }

            const chkOnline = document.getElementById('metaOnline');
            if (chkOnline && meta.clasesOnline) {
                chkOnline.checked = meta.clasesOnline;
            }

            const txtTitulacion = document.getElementById('metaTitulacion');
            if (txtTitulacion && meta.titulacion) {
                txtTitulacion.value = meta.titulacion;
            }
        }
    } catch (error) {
        console.error('Error cargando metadata:', error);
    }
}


function cargarDatosEnFormulario(anuncio) {
    console.log('Cargando anuncio en formulario:', anuncio);

    // Verificar que tenemos los datos necesarios
    if (!anuncio) {
        console.error('No hay datos de anuncio para cargar');
        return;
    }

    // Extraer IDs correctamente (pueden venir como objetos o IDs directos)
    const categoryId = anuncio.category?.id || anuncio.categoryId || anuncio.category_id;
    const subcategoryId = anuncio.subcategory?.id || anuncio.subcategoryId || anuncio.subcategory_id;

    console.log('Category ID:', categoryId, 'Subcategory ID:', subcategoryId);

    // Función para cargar el resto de campos
    const cargarCampos = () => {
        document.getElementById('anuncioCategoria').value = categoryId || '';
        cargarSubcategorias();

        // Esperar a que carguen las subcategorías
        setTimeout(() => {
            document.getElementById('anuncioSubcategoria').value = subcategoryId || '';
            console.log('Subcategoría seleccionada:', document.getElementById('anuncioSubcategoria').value);
        }, 200);

        // Cargar resto de campos
        document.getElementById('anuncioTitulo').value = anuncio.titulo || '';
        document.getElementById('anuncioDescripcion').value = anuncio.descripcion || '';
        document.getElementById('anuncioPrecio').value = anuncio.precio || '';
        document.getElementById('anuncioTipoPrecio').value = anuncio.tipoPrecio || 'POR_HORA';
        document.getElementById('anuncioUbicacion').value = anuncio.ubicacion || '';
        document.getElementById('anuncioImagen').value = anuncio.imagenPrincipalUrl || '';
    };

    // Cargar categorías primero si no están cargadas
    if (categorias.length === 0) {
        console.log('Categorías no cargadas, cargando...');
        cargarCategorias().then(() => {
            cargarCampos();
        });
    } else {
        cargarCampos();
    }
}

// ==================== EXPLORAR ANUNCIOS PÚBLICOS ====================
async function cargarCategoriasFiltro() {
    const select = document.getElementById('filtroCategoria');
    select.innerHTML = '<option value="">Todas las categorías</option>';

    categorias.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat.id;
        option.textContent = cat.nombre;
        select.appendChild(option);
    });
}

async function loadPublicAnuncios() {
    try {
        const response = await fetch(`${API_URL}/anuncios/public`);
        const data = await response.json();
        renderizarPublicos(data.content || data);
    } catch (error) {
        document.getElementById('publicAnunciosList').innerHTML = '<p class="error">Error cargando anuncios</p>';
    }
}

function renderizarPublicos(anuncios) {
    const container = document.getElementById('publicAnunciosList');

    if (anuncios.length === 0) {
        container.innerHTML = '<p class="muted">No hay servicios disponibles</p>';
        return;
    }

    container.innerHTML = anuncios.map(a => `
        <div class="anuncio-item">
            <img src="${a.imagenPrincipalUrl || 'https://via.placeholder.com/80'}" class="anuncio-imagen" alt="">
            <div class="anuncio-info">
                <div class="anuncio-titulo">${a.titulo}</div>
                <div class="anuncio-meta">
                    ${a.categoryNombre} → ${a.subcategoryNombre} |
                    ${a.ubicacion} |
                    Por ${a.propietarioNombre}
                </div>
                <div class="anuncio-precio">${a.precio}€ ${a.tipoPrecio.replace('POR_', '/').toLowerCase()}</div>
                <div class="muted small">❤️ ${a.favoritesCount} favoritos</div>
            </div>
            <div class="anuncio-actions">
                <button onclick="contactar(${a.id})">📞 Contactar</button>
                <button class="secondary" onclick="toggleFavorito(${a.id})">❤️</button>
            </div>
        </div>
    `).join('');
}

function filtrarPublicos() {
    const categoriaId = document.getElementById('filtroCategoria').value;
    const busqueda = document.getElementById('filtroBusqueda').value.toLowerCase();

    // Implementar filtrado local o llamada al backend
    loadPublicAnuncios(); // Por ahora recarga todo
}

function contactar(id) {
    alert('Función de contacto: Implementar chat o mostrar teléfono');
}

function toggleFavorito(id) {
    alert('Función de favoritos: Implementar');
}

// ==================== UTILIDADES ====================
function showError(fieldId, message) {
    const element = document.getElementById(`err-${fieldId}`);
    if (element) element.textContent = message;
}

function clearErrors() {
    document.querySelectorAll('.error').forEach(el => el.textContent = '');
}