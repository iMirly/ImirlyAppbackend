// ==================== CONFIGURACIÓN ====================
const API_URL = 'http://localhost:8080/api';
const H2_CONSOLE_URL = 'http://localhost:8080/h2-console';

// ==================== ESTADO GLOBAL ====================
let currentUser = null;
let authToken = localStorage.getItem('token');
let categorias = [];
let subcategorias = [];
let anuncioTemporal = null;
let misAnunciosCache = [];
let currentFormConfig = null; // Configuración actual de campos dinámicos

// ==================== INICIALIZACIÓN ====================
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM cargado, inicializando...');

    if (authToken) {
        validateToken();
    } else {
        showScreen('auth');
    }

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

// ==================== AUTH ====================
async function register() {
    clearErrors();

    const nombre = document.getElementById('regNombre').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;

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
        nombre: currentUser.nombre,
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
        }
    } catch (error) {
        console.error('Error cargando categorías:', error);
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
        subcategorias = [];
        return;
    }

    const categoria = categorias.find(c => c.id == categoriaId);

    if (categoria && categoria.subcategories) {
        subcategorias = categoria.subcategories;

        subcategorias.forEach(sub => {
            const option = document.createElement('option');
            option.value = sub.id;
            option.textContent = sub.nombre;
            option.dataset.codigo = sub.codigo;
            option.dataset.formConfig = sub.formConfigJson || '';
            select.appendChild(option);
        });
    }
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

/**
 * Genera los campos dinámicos del Paso 2 basándose en el formConfigJson de la subcategoría
 */
function generarCamposStep2(subcategoriaId, metadataExistente = null) {
    console.log('Generando campos para subcategoría:', subcategoriaId);
    console.log('Metadata existente:', metadataExistente);

    const subcategoria = subcategorias.find(s => s.id == subcategoriaId);
    const container = document.getElementById('camposEspecificos');
    container.innerHTML = '';

    if (!subcategoria) {
        console.error('No se encontró la subcategoría:', subcategoriaId);
        return;
    }

    console.log('Subcategoría encontrada:', subcategoria);
    console.log('formConfigJson:', subcategoria.formConfigJson);

    // Parsear la configuración del formulario
    let camposConfig = [];
    if (subcategoria.formConfigJson) {
        try {
            camposConfig = JSON.parse(subcategoria.formConfigJson);
            currentFormConfig = camposConfig;
            console.log('Campos configurados:', camposConfig);
        } catch (e) {
            console.error('Error parseando formConfigJson:', e);
        }
    }

    // Si no hay configuración, usar campos por defecto
    if (camposConfig.length === 0) {
        console.warn('No hay configuración de campos, usando defaults');
        camposConfig = getCamposPorDefecto();
    }

    // Generar HTML para cada campo
    camposConfig.forEach(campo => {
        const div = document.createElement('div');
        div.className = 'campo-dinamico';

        const valorActual = metadataExistente ? metadataExistente[campo.id] : null;
        console.log(`Campo ${campo.id}:`, valorActual);

        div.innerHTML = generarCampoHTML(campo, valorActual);
        container.appendChild(div);
    });

    // Añadir campos comunes si no están en la configuración
    if (!camposConfig.find(c => c.id === 'disponibilidad')) {
        const divDisp = document.createElement('div');
        divDisp.className = 'campo-dinamico';
        divDisp.innerHTML = generarCampoDisponibilidad(metadataExistente?.disponibilidad);
        container.appendChild(divDisp);
    }
}

/**
 * Genera el HTML para un campo específico según su tipo
 */
function generarCampoHTML(campo, valorActual) {
    const valor = valorActual !== undefined && valorActual !== null ? valorActual : '';

    switch (campo.tipo) {
        case 'number':
            return `
                <label for="meta_${campo.id}">${campo.label}</label>
                <input type="number" id="meta_${campo.id}" name="${campo.id}" 
                       step="0.01" min="0" value="${valor}" 
                       placeholder="0.00">
            `;

        case 'text':
            return `
                <label for="meta_${campo.id}">${campo.label}</label>
                <input type="text" id="meta_${campo.id}" name="${campo.id}" 
                       value="${valor}" placeholder="${campo.placeholder || ''}">
            `;

        case 'select':
            const opcionesSelect = campo.opciones.map(op =>
                `<option value="${op}" ${valor === op ? 'selected' : ''}>${op}</option>`
            ).join('');
            return `
                <label for="meta_${campo.id}">${campo.label}</label>
                <select id="meta_${campo.id}" name="${campo.id}">
                    <option value="">Selecciona...</option>
                    ${opcionesSelect}
                </select>
            `;

        case 'checkbox-group':
            const valoresArray = Array.isArray(valor) ? valor : (valor ? [valor] : []);
            const opcionesCheckbox = campo.opciones.map(op => {
                const checked = valoresArray.includes(op) ? 'checked' : '';
                return `
                    <label class="checkbox-option">
                        <input type="checkbox" name="${campo.id}" value="${op}" ${checked}>
                        <span>${op}</span>
                    </label>
                `;
            }).join('');
            return `
                <label>${campo.label}</label>
                <div class="checkbox-group" id="meta_${campo.id}">
                    ${opcionesCheckbox}
                </div>
            `;

        case 'boolean':
            const checkedBool = valor === true || valor === 'true' ? 'checked' : '';
            return `
                <label class="toggle-label">
                    <input type="checkbox" id="meta_${campo.id}" name="${campo.id}" ${checkedBool}>
                    <span class="toggle-slider"></span>
                    <span class="toggle-text">${campo.label}</span>
                </label>
            `;

        case 'textarea':
            return `
                <label for="meta_${campo.id}">${campo.label}</label>
                <textarea id="meta_${campo.id}" name="${campo.id}" rows="3"
                          placeholder="${campo.placeholder || ''}">${valor}</textarea>
            `;

        default:
            return `
                <label for="meta_${campo.id}">${campo.label}</label>
                <input type="text" id="meta_${campo.id}" name="${campo.id}" value="${valor}">
            `;
    }
}

/**
 * Genera el campo de disponibilidad (común a todas las subcategorías)
 */
function generarCampoDisponibilidad(valorActual) {
    const dias = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
    const valoresArray = Array.isArray(valorActual) ? valorActual : ['Lun', 'Mar', 'Mié', 'Jue', 'Vie'];

    const opciones = dias.map(dia => {
        const checked = valoresArray.includes(dia) ? 'checked' : '';
        return `
            <label class="checkbox-option day-option">
                <input type="checkbox" name="disponibilidad" value="${dia}" ${checked}>
                <span>${dia}</span>
            </label>
        `;
    }).join('');

    return `
        <label>Disponibilidad</label>
        <div class="checkbox-group days-group" id="meta_disponibilidad">
            ${opciones}
        </div>
    `;
}

/**
 * Campos por defecto si no hay configuración específica
 */
function getCamposPorDefecto() {
    return [
        {
            id: 'anos_experiencia',
            label: 'Años de experiencia',
            tipo: 'select',
            opciones: ['Sin experiencia', '1-2', '3-5', '6-10', '+10']
        },
        {
            id: 'servicio_domicilio',
            label: 'Ofrezco servicio a domicilio',
            tipo: 'boolean'
        }
    ];
}

function volverStep1() {
    document.getElementById('anuncioStep2').classList.add('hidden');
    document.getElementById('anuncioStep1').classList.remove('hidden');
    document.getElementById('anuncioStepIndicator').textContent = 'Paso 1 de 2: Información general';
}

/**
 * Recoge los valores de los campos dinámicos del Paso 2
 */
function recogerMetadataStep2() {
    const metadata = {};

    if (!currentFormConfig) return metadata;

    currentFormConfig.forEach(campo => {
        switch (campo.tipo) {
            case 'number':
                const valNum = document.getElementById(`meta_${campo.id}`)?.value;
                metadata[campo.id] = valNum ? parseFloat(valNum) : null;
                break;

            case 'select':
            case 'text':
            case 'textarea':
                metadata[campo.id] = document.getElementById(`meta_${campo.id}`)?.value || null;
                break;

            case 'checkbox-group':
                const checkboxes = document.querySelectorAll(`#meta_${campo.id} input[type="checkbox"]:checked`);
                metadata[campo.id] = Array.from(checkboxes).map(cb => cb.value);
                break;

            case 'boolean':
                metadata[campo.id] = document.getElementById(`meta_${campo.id}`)?.checked || false;
                break;
        }
    });

    // Disponibilidad siempre está presente
    const dispChecks = document.querySelectorAll('#meta_disponibilidad input[type="checkbox"]:checked');
    metadata.disponibilidad = Array.from(dispChecks).map(cb => cb.value);

    return metadata;
}

async function completarAnuncioStep2() {
    const metadata = recogerMetadataStep2();

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

    resetBotonesFormulario();
    currentFormConfig = null;
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
        // Obtener datos completos del anuncio
        const response = await fetch(`${API_URL}/anuncios/${id}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) {
            throw new Error('Error al cargar el anuncio');
        }

        const anuncio = await response.json();
        console.log('Datos COMPLETOS del anuncio:', JSON.stringify(anuncio, null, 2));

        // Guardar el ID del anuncio que se está editando
        anuncioTemporal = {
            id: anuncio.id,
            editando: true,
            statusAnterior: anuncio.status,
            metadata: anuncio.metadata // Guardar metadata para usarla luego
        };

        // PRIMERO cargar categorías si no están cargadas
        if (categorias.length === 0) {
            await cargarCategorias();
        }

        // Luego cargar datos en el formulario
        await cargarDatosEnFormulario(anuncio);

        // Mostrar el formulario de edición (Paso 1)
        document.getElementById('anuncioStep1').classList.remove('hidden');
        document.getElementById('anuncioStep2').classList.add('hidden');
        document.getElementById('anuncioStepIndicator').textContent = 'Editando anuncio - Paso 1 de 2';

        // Cambiar el botón "Continuar" por "Guardar cambios"
        const btnContinuar = document.querySelector('#anuncioStep1 button.primary');
        if (btnContinuar) {
            btnContinuar.textContent = 'Guardar cambios →';
            btnContinuar.onclick = guardarEdicionStep1;
        }

        // Añadir botón "Cancelar edición" si no existe
        if (!document.getElementById('btnCancelarEdicion')) {
            const btnCancelar = document.createElement('button');
            btnCancelar.id = 'btnCancelarEdicion';
            btnCancelar.className = 'secondary';
            btnCancelar.textContent = '← Cancelar edición';
            btnCancelar.onclick = cancelarEdicion;
            btnContinuar.parentNode.insertBefore(btnCancelar, btnContinuar);
        }

        // Scroll al formulario
        document.getElementById('anuncioStep1').scrollIntoView({ behavior: 'smooth' });

    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar el anuncio: ' + error.message);
    }
}

function cancelarEdicion() {
    if (confirm('¿Descartar los cambios y salir de la edición?')) {
        resetFormularioAnuncio();
        resetBotonesFormulario();

        // Eliminar botón cancelar si existe
        const btnCancelar = document.getElementById('btnCancelarEdicion');
        if (btnCancelar) btnCancelar.remove();
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
        anuncioTemporal = {
            ...anuncioTemporal,
            ...anuncioActualizado,
            subcategoryId: data.subcategoryId // Guardar para generar campos correctos
        };

        // Mostrar paso 2 para editar metadata
        document.getElementById('anuncioStep1').classList.add('hidden');
        document.getElementById('anuncioStep2').classList.remove('hidden');
        document.getElementById('anuncioStepIndicator').textContent = 'Editando anuncio - Paso 2 de 2: Detalles específicos';

        // Generar campos dinámicos y cargar valores existentes
        generarCamposStep2(data.subcategoryId, anuncioTemporal.metadata);

        // Cambiar botón del paso 2 y añadir cancelar
        const btnPublicar = document.querySelector('#anuncioStep2 button.primary');
        if (btnPublicar) {
            btnPublicar.textContent = 'Guardar y republicar';
            btnPublicar.onclick = guardarEdicionStep2;
        }

        // Añadir botón cancelar en paso 2 si no existe
        if (!document.getElementById('btnCancelarEdicionStep2')) {
            const btnAtras = document.querySelector('#anuncioStep2 button.secondary');
            const btnCancelar = document.createElement('button');
            btnCancelar.id = 'btnCancelarEdicionStep2';
            btnCancelar.className = 'danger';
            btnCancelar.textContent = 'Cancelar';
            btnCancelar.onclick = cancelarEdicion;
            btnAtras.parentNode.insertBefore(btnCancelar, btnAtras.nextSibling);
        }

    } catch (error) {
        showError('anuncioGeneral', error.message);
    }
}

async function guardarEdicionStep2() {
    const metadata = recogerMetadataStep2();

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

        // Republicar automáticamente si estaba publicado
        if (anuncioTemporal.statusAnterior === 'PUBLICADO') {
            await fetch(`${API_URL}/anuncios/${anuncioTemporal.id}/publicar`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${authToken}` }
            });
        }

        alert('✅ Anuncio actualizado correctamente');
        resetFormularioAnuncio();
        resetBotonesFormulario();
        loadMisAnuncios();

    } catch (error) {
        alert('❌ Error: ' + error.message);
    }
}

function resetBotonesFormulario() {
    // Restaurar botón del paso 1
    const btnStep1 = document.querySelector('#anuncioStep1 button.primary');
    if (btnStep1) {
        btnStep1.textContent = 'Continuar →';
        btnStep1.onclick = crearAnuncioStep1;
    }

    // Eliminar botón cancelar del paso 1
    const btnCancelar1 = document.getElementById('btnCancelarEdicion');
    if (btnCancelar1) btnCancelar1.remove();

    // Restaurar botón del paso 2
    const btnStep2 = document.querySelector('#anuncioStep2 button.primary');
    if (btnStep2) {
        btnStep2.textContent = 'Publicar anuncio';
        btnStep2.onclick = completarAnuncioStep2;
    }

    // Eliminar botón cancelar del paso 2
    const btnCancelar2 = document.getElementById('btnCancelarEdicionStep2');
    if (btnCancelar2) btnCancelar2.remove();

    if (anuncioTemporal) {
        anuncioTemporal.editando = false;
    }
}


async function cargarDatosEnFormulario(anuncio) {
    console.log('Cargando anuncio en formulario:', anuncio);

    if (!anuncio) {
        console.error('No hay datos de anuncio para cargar');
        return;
    }

    // Extraer IDs correctamente
    const categoryId = anuncio.category?.id || anuncio.categoryId;
    const subcategoryId = anuncio.subcategory?.id || anuncio.subcategoryId;

    console.log('Category ID:', categoryId, 'Subcategory ID:', subcategoryId);

    // Establecer categoría
    const selectCategoria = document.getElementById('anuncioCategoria');
    selectCategoria.value = categoryId || '';

    // Disparar evento change para cargar subcategorías
    const event = new Event('change');
    selectCategoria.dispatchEvent(event);

    // Esperar a que carguen las subcategorías y luego seleccionar la correcta
    await new Promise(resolve => setTimeout(resolve, 300));

    const selectSubcategoria = document.getElementById('anuncioSubcategoria');
    selectSubcategoria.value = subcategoryId || '';

    console.log('Subcategoría seleccionada:', selectSubcategoria.value);
    console.log('Opciones disponibles:', Array.from(selectSubcategoria.options).map(o => o.value));

    // Cargar resto de campos del paso 1
    document.getElementById('anuncioTitulo').value = anuncio.titulo || '';
    document.getElementById('anuncioDescripcion').value = anuncio.descripcion || '';
    document.getElementById('anuncioPrecio').value = anuncio.precio || '';
    document.getElementById('anuncioTipoPrecio').value = anuncio.tipoPrecio || 'POR_HORA';
    document.getElementById('anuncioUbicacion').value = anuncio.ubicacion || '';
    document.getElementById('anuncioImagen').value = anuncio.imagenPrincipalUrl || '';
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
        <div class="anuncio-item" data-id="${a.id}">
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
                <button onclick="contactar(${a.id}, '${a.propietarioNombre}')">📞 Contactar</button>
                <button class="secondary ${a.isFavorite ? 'favorite-active' : ''}" 
                        onclick="toggleFavorito(${a.id}, this)" 
                        title="${a.isFavorite ? 'Quitar de favoritos' : 'Añadir a favoritos'}">
                    ${a.isFavorite ? '❤️' : '🤍'}
                </button>
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

// ==================== EXPLORAR ANUNCIOS PÚBLICOS (MEJORADO) ====================

async function loadPublicAnunciosExcluyendoMios() {
    try {
        const response = await fetch(`${API_URL}/anuncios/public/excluyendo-mios`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        const data = await response.json();
        renderizarPublicos(data.content || data);
    } catch (error) {
        document.getElementById('publicAnunciosList').innerHTML = '<p class="error">Error cargando anuncios</p>';
    }
}

async function filtrarPublicos() {
    const categoriaId = document.getElementById('filtroCategoria').value;
    const busqueda = document.getElementById('filtroBusqueda').value.trim();

    try {
        let url = `${API_URL}/anuncios/public/excluyendo-mios/search?`;
        if (busqueda) url += `query=${encodeURIComponent(busqueda)}&`;
        if (categoriaId) url += `categoryId=${categoriaId}&`;

        const response = await fetch(url, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        const data = await response.json();
        renderizarPublicos(data.content || data);
    } catch (error) {
        console.error('Error filtrando:', error);
    }
}

// Modificar renderizarPublicos para incluir favoritos funcionales
function renderizarPublicos(anuncios) {
    const container = document.getElementById('publicAnunciosList');

    if (anuncios.length === 0) {
        container.innerHTML = '<p class="muted">No hay servicios disponibles</p>';
        return;
    }

    container.innerHTML = anuncios.map(a => `
        <div class="anuncio-item" data-id="${a.id}">
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
                <button onclick="contactar(${a.id}, '${a.propietarioNombre}')">📞 Contactar</button>
                <button class="secondary ${a.isFavorite ? 'favorite-active' : ''}" 
                        onclick="toggleFavorito(${a.id}, this)" 
                        title="${a.isFavorite ? 'Quitar de favoritos' : 'Añadir a favoritos'}">
                    ${a.isFavorite ? '❤️' : '🤍'}
                </button>
            </div>
        </div>
    `).join('');
}

// ==================== FAVORITOS ====================

async function toggleFavorito(anuncioId, btnElement) {
    const isFavorite = btnElement.classList.contains('favorite-active');

    try {
        if (isFavorite) {
            // Quitar de favoritos
            await fetch(`${API_URL}/favorites/${anuncioId}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            // Si estamos en la pestaña de favoritos, eliminar el card completo
            if (!document.getElementById('publicAnunciosList')) {
                // Estamos en favoritos, eliminar el elemento
                const card = btnElement.closest('.anuncio-item');
                if (card) card.remove();

                // Si no quedan favoritos, mostrar mensaje
                const container = document.getElementById('misFavoritosList');
                if (container && container.children.length === 0) {
                    container.innerHTML = '<p class="muted">No tienes favoritos aún</p>';
                }
            } else {
                // Estamos en explorar, solo cambiar el icono
                btnElement.classList.remove('favorite-active');
                btnElement.innerHTML = '🤍';
                btnElement.title = 'Añadir a favoritos';

                // Actualizar el contador visualmente (opcional, o recargar solo ese dato)
                actualizarContadorFavoritos(anuncioId, -1);
            }

        } else {
            // Añadir a favoritos
            await fetch(`${API_URL}/favorites/${anuncioId}`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            // Solo cambiar el icono, NUNCA redirigir ni eliminar
            btnElement.classList.add('favorite-active');
            btnElement.innerHTML = '❤️';
            btnElement.title = 'Quitar de favoritos';

            // Actualizar contador visualmente
            actualizarContadorFavoritos(anuncioId, +1);
        }

    } catch (error) {
        console.error('Error:', error);
        alert('Error: ' + error.message);
    }
}

function actualizarContadorFavoritos(anuncioId, cambio) {
    const card = document.querySelector(`.anuncio-item[data-id="${anuncioId}"]`);
    if (card) {
        const contadorElement = card.querySelector('.muted.small');
        if (contadorElement) {
            const match = contadorElement.textContent.match(/(\d+)/);
            if (match) {
                const nuevoCount = parseInt(match[1]) + cambio;
                contadorElement.textContent = `❤️ ${nuevoCount} favoritos`;
            }
        }
    }
}

async function loadMisFavoritos() {
    try {
        const response = await fetch(`${API_URL}/favorites/mis-favoritos`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) throw new Error('Error al cargar favoritos');

        const data = await response.json();
        const container = document.getElementById('misFavoritosList');

        if (!container) return;

        // Si no hay contenido o está vacío
        if (!data.content || data.content.length === 0) {
            container.innerHTML = '<p class="muted">No tienes favoritos aún</p>';
            return;
        }

        container.innerHTML = data.content.map(a => `
            <div class="anuncio-item" data-id="${a.id}">
                <img src="${a.imagenPrincipalUrl || 'https://via.placeholder.com/80'}" class="anuncio-imagen" alt="">
                <div class="anuncio-info">
                    <div class="anuncio-titulo">${a.titulo}</div>
                    <div class="anuncio-meta">${a.categoryNombre} | ${a.ubicacion}</div>
                    <div class="anuncio-precio">${a.precio}€</div>
                </div>
                <div class="anuncio-actions">
                    <button onclick="contactar(${a.id}, '${a.propietarioNombre}')">📞 Contactar</button>
                    <button class="danger" onclick="quitarFavoritoDesdeFavoritos(${a.id}, this)">💔 Quitar</button>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Error cargando favoritos:', error);
        document.getElementById('misFavoritosList').innerHTML = '<p class="error">Error cargando favoritos</p>';
    }
}

// Función específica para quitar desde la pestaña favoritos
async function quitarFavoritoDesdeFavoritos(anuncioId, btnElement) {
    try {
        await fetch(`${API_URL}/favorites/${anuncioId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        // Eliminar el card completo
        const card = btnElement.closest('.anuncio-item');
        if (card) {
            card.remove();

            // Si no quedan favoritos, mostrar mensaje
            const container = document.getElementById('misFavoritosList');
            if (container && container.children.length === 0) {
                container.innerHTML = '<p class="muted">No tienes favoritos aún</p>';
            }
        }

    } catch (error) {
        console.error('Error:', error);
        alert('Error al quitar de favoritos');
    }
}

// ==================== MENSAJES ====================

// Almacén temporal de conversaciones (hasta que implementes backend)
let conversaciones = JSON.parse(localStorage.getItem('conversaciones') || '{}');

function contactar(anuncioId, propietarioNombre) {
    // Crear o abrir conversación
    const convId = `conv_${anuncioId}`;
    if (!conversaciones[convId]) {
        conversaciones[convId] = {
            anuncioId: anuncioId,
            propietario: propietarioNombre,
            mensajes: [],
            unread: 0
        };
    }

    localStorage.setItem('conversaciones', JSON.stringify(conversaciones));

    // Mostrar pantalla de mensajes
    showScreen('mensajes');
    renderizarConversaciones();

    // Abrir chat específico
    abrirChat(convId);
}

function renderizarConversaciones() {
    const container = document.getElementById('listaConversaciones');
    if (!container) return;

    const convs = Object.values(conversaciones);

    if (convs.length === 0) {
        container.innerHTML = '<p class="muted">No tienes mensajes aún</p>';
        return;
    }

    container.innerHTML = convs.map(c => `
        <div class="conversacion-item ${c.unread > 0 ? 'unread' : ''}" onclick="abrirChat('${c.anuncioId}')">
            <div class="conv-info">
                <strong>${c.propietario}</strong>
                <span class="last-msg">${c.mensajes.length > 0 ? c.mensajes[c.mensajes.length-1].texto.substring(0, 30) + '...' : 'Sin mensajes'}</span>
            </div>
            ${c.unread > 0 ? `<span class="badge">${c.unread}</span>` : ''}
        </div>
    `).join('');
}

function abrirChat(convId) {
    const conv = conversaciones[convId];
    if (!conv) return;

    // Marcar como leído
    conv.unread = 0;
    localStorage.setItem('conversaciones', JSON.stringify(conversaciones));
    renderizarConversaciones();

    const chatContainer = document.getElementById('chatActivo');
    chatContainer.innerHTML = `
        <div class="chat-header">
            <h4>Chat con ${conv.propietario}</h4>
            <button onclick="cerrarChat()">✕</button>
        </div>
        <div class="chat-mensajes" id="mensajesChat">
            ${conv.mensajes.map(m => `
                <div class="mensaje ${m.mio ? 'mio' : 'suyo'}">
                    <span>${m.texto}</span>
                    <small>${m.fecha}</small>
                </div>
            `).join('')}
        </div>
        <div class="chat-input">
            <input type="text" id="nuevoMensaje" placeholder="Escribe un mensaje..." onkeypress="if(event.key==='Enter') enviarMensaje('${convId}')">
            <button onclick="enviarMensaje('${convId}')">Enviar</button>
        </div>
    `;

    // Scroll al final
    const msgContainer = document.getElementById('mensajesChat');
    if (msgContainer) msgContainer.scrollTop = msgContainer.scrollHeight;
}

function enviarMensaje(convId) {
    const input = document.getElementById('nuevoMensaje');
    const texto = input.value.trim();
    if (!texto) return;

    const mensaje = {
        texto: texto,
        mio: true,
        fecha: new Date().toLocaleString()
    };

    conversaciones[convId].mensajes.push(mensaje);
    localStorage.setItem('conversaciones', JSON.stringify(conversaciones));

    input.value = '';
    abrirChat(convId);

    // Simular respuesta (temporal hasta backend)
    setTimeout(() => {
        conversaciones[convId].mensajes.push({
            texto: "Gracias por tu mensaje. Te responderé pronto.",
            mio: false,
            fecha: new Date().toLocaleString()
        });
        localStorage.setItem('conversaciones', JSON.stringify(conversaciones));

        // Si no está abierto, marcar como no leído
        if (!document.getElementById('mensajesChat')) {
            conversaciones[convId].unread++;
        }

        localStorage.setItem('conversaciones', JSON.stringify(conversaciones));

        // Actualizar UI si está visible
        if (document.getElementById('screen-mensajes').classList.contains('hidden') === false) {
            renderizarConversaciones();
            if (document.getElementById('mensajesChat')) {
                abrirChat(convId);
            }
        }
    }, 2000);
}

function cerrarChat() {
    document.getElementById('chatActivo').innerHTML = '<p class="muted">Selecciona una conversación</p>';
}
