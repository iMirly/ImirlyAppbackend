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

        // Cargar datos del usuario en el sidebar (NUEVO)
        cargarDatosUsuarioSidebar();

        loadMisAnuncios();
        // No llamar a loadUserProfile() aquí si ya se hace en loginSuccess
    } else {
        document.getElementById('btnLogout').classList.add('hidden');
        document.getElementById('btnH2Console').classList.add('hidden');
    }
}

function cargarDatosUsuarioSidebar() {
    if (!currentUser) return;

    const profileName = document.getElementById('profileName');
    const profileEmail = document.getElementById('profileEmail');
    const profileCompleteness = document.getElementById('profileCompleteness');

    if (profileName) profileName.textContent = currentUser.nombre || 'Usuario';
    if (profileEmail) profileEmail.textContent = currentUser.email || '';

    // Calcular completitud del perfil
    let camposCompletos = 0;
    const camposTotales = 6;

    if (currentUser.nombre) camposCompletos++;
    if (currentUser.email) camposCompletos++;
    if (currentUser.telefono) camposCompletos++;
    if (currentUser.fechaNacimiento) camposCompletos++;
    if (currentUser.direccionCalle) camposCompletos++;
    if (currentUser.direccionCiudad) camposCompletos++;

    const porcentaje = Math.round((camposCompletos / camposTotales) * 100);

    if (profileCompleteness) {
        if (porcentaje === 100) {
            profileCompleteness.textContent = '✓ Perfil completo';
            profileCompleteness.className = 'badge complete';
        } else {
            profileCompleteness.textContent = `Perfil ${porcentaje}% completado`;
            profileCompleteness.className = 'badge incomplete';
        }
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
    //loadPublicAnuncios();
    loadPublicAnunciosExcluyendoMios();
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
    const contenedor = document.getElementById('misAnunciosList');
    if (!contenedor) return;

    try {
        // Intentar cargar de tu API primero
        const response = await fetch(`${API_URL}/anuncios/mis-anuncios`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) {
            throw new Error('Error al cargar anuncios');
        }

        misAnunciosCache = await response.json();

        if (misAnunciosCache.length === 0) {
            contenedor.innerHTML = '<p class="muted">No tienes anuncios publicados aún. ¡Crea tu primer servicio arriba!</p>';
            return;
        }

        renderizarMisAnuncios(misAnunciosCache);

    } catch (error) {
        console.error('Error cargando mis anuncios:', error);
        // Si falla la API, mostrar mensaje amigable
        contenedor.innerHTML = '<p class="muted">No tienes anuncios publicados aún. ¡Crea tu primer servicio arriba!</p>';
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
    if (!container) return;

    if (anuncios.length === 0) {
        container.innerHTML = '<p class="muted">No tienes anuncios. ¡Crea uno!</p>';
        return;
    }

    container.innerHTML = anuncios.map(a => `
        <div class="anuncio-item">
            <img src="${a.imagenPrincipalUrl || generarPlaceholder(a.titulo)}" class="anuncio-imagen" alt="" onerror="this.src='${generarPlaceholder(a.titulo)}'">
            <div class="anuncio-info">
                <div class="anuncio-titulo">${a.titulo}</div>
                <div class="anuncio-meta">
                    ${a.categoryNombre || a.categoria} → ${a.subcategoryNombre || a.subcategoria} |
                    ${a.ubicacion} |
                    <span class="status-badge status-${a.status || a.estado}">${a.status || a.estado}</span>
                </div>
                <div class="anuncio-precio">${a.precio}€ ${(a.tipoPrecio || 'POR_HORA').replace('POR_', '/').toLowerCase()}</div>
                <div class="muted small">❤️ ${a.favoritesCount || 0} favoritos</div>
            </div>
            <div class="anuncio-actions">
                ${(a.status || a.estado) === 'BORRADOR' ? `<button onclick="publicarAnuncio(${a.id})">Publicar</button>` : ''}
                ${(a.status || a.estado) === 'PUBLICADO' ? `<button onclick="despublicarAnuncio(${a.id})">Despublicar</button>` : ''}
                ${(a.status || a.estado) === 'DESPUBLICADO' ? `<button onclick="publicarAnuncio(${a.id})">Republicar</button>` : ''}
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
        // 1. Obtener usuario actual
        const meResponse = await fetch(`${API_URL}/users/me`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!meResponse.ok) {
            // Si no está autenticado, cargar todos los anuncios sin filtrar
            loadPublicAnuncios();
            return;
        }

        const usuarioActual = await meResponse.json();

        // 2. Cargar TODOS los anuncios públicos
        const response = await fetch(`${API_URL}/anuncios/public`);
        const data = await response.json();
        const todosLosAnuncios = data.content || data;

        // 3. Filtrar los del usuario actual (EXCLUIR los míos)
        const anunciosFiltrados = todosLosAnuncios.filter(
            a => a.propietarioId !== usuarioActual.id
        );

        // 4. Cargar mis favoritos para marcarlos
        const favResponse = await fetch(`${API_URL}/favorites/mis-favoritos`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        let favoritosIds = new Set();
        if (favResponse.ok) {
            const favoritos = await favResponse.json();
            const favoritosList = favoritos.content || favoritos;
            favoritosIds = new Set(favoritosList.map(f => f.id));
        }

        // 5. Marcar favoritos
        const anunciosConFavoritos = anunciosFiltrados.map(a => ({
            ...a,
            isFavorite: favoritosIds.has(a.id)
        }));

        renderizarPublicos(anunciosConFavoritos);

    } catch (error) {
        console.error('Error:', error);
        // Fallback: cargar sin filtrar
        loadPublicAnuncios();
    }
}

// Función temporal que carga anuncios y verifica favoritos manualmente
async function loadPublicAnunciosConFavoritosManual() {
    try {
        // 1. Cargar anuncios públicos
        const [anunciosResponse, favoritosResponse] = await Promise.all([
            fetch(`${API_URL}/anuncios/public`),
            fetch(`${API_URL}/favorites/mis-favoritos`, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            })
        ]);

        const anuncios = await anunciosResponse.json();
        const favoritos = favoritosResponse.ok ? await favoritosResponse.json() : {content: []};

        const anunciosList = anuncios.content || anuncios;
        const favoritosList = favoritos.content || favoritos;
        const favoritosIds = new Set(favoritosList.map(f => f.id));

        // Marcar favoritos
        const anunciosConFavoritos = anunciosList.map(a => ({
            ...a,
            isFavorite: favoritosIds.has(a.id)
        }));

        renderizarPublicos(anunciosConFavoritos);

    } catch (error) {
        console.error('Error:', error);
        loadPublicAnuncios();
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
    // Prevenir clicks múltiples
    btnElement.disabled = true;

    const isFavorite = btnElement.classList.contains('favorite-active');

    try {
        if (isFavorite) {
            // Quitar de favoritos
            const response = await fetch(`${API_URL}/favorites/${anuncioId}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText);
            }
        } else {
            // Añadir a favoritos
            const response = await fetch(`${API_URL}/favorites/${anuncioId}`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            if (!response.ok) {
                const errorText = await response.text();
                // Si ya está en favoritos, solo actualizar UI
                if (errorText.includes('Ya está en favoritos') || errorText.includes('already')) {
                    console.log('Ya estaba en favoritos');
                } else {
                    throw new Error(errorText);
                }
            }
        }

        // IMPORTANTE: Actualizar solo el botón y el contador, NO recargar toda la lista
        // Esto evita que el anuncio desaparezca o cambie de posición
        if (isFavorite) {
            btnElement.classList.remove('favorite-active');
            btnElement.innerHTML = '🤍';
            btnElement.title = 'Añadir a favoritos';
            actualizarContadorFavoritos(anuncioId, -1);
        } else {
            btnElement.classList.add('favorite-active');
            btnElement.innerHTML = '❤️';
            btnElement.title = 'Quitar de favoritos';
            actualizarContadorFavoritos(anuncioId, +1);
        }

    } catch (error) {
        console.error('Error:', error);
        alert('Error: ' + error.message);
    } finally {
        btnElement.disabled = false;
    }
}


function actualizarContadorFavoritos(anuncioId, cambio) {
    const card = document.querySelector(`.anuncio-item[data-id="${anuncioId}"]`);
    if (card) {
        const contadorElement = card.querySelector('.muted.small');
        if (contadorElement) {
            const match = contadorElement.textContent.match(/(\d+)/);
            if (match) {
                const nuevoCount = Math.max(0, parseInt(match[1]) + cambio);
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

        const favoritos = data.content || data;

        if (favoritos.length === 0) {
            container.innerHTML = '<p class="muted">No tienes favoritos aún</p>';
            return;
        }

        container.innerHTML = favoritos.map(a => `
            <div class="anuncio-item" data-id="${a.id}">
                <img src="${a.imagenPrincipalUrl || 'https://via.placeholder.com/80'}" class="anuncio-imagen" alt="">
                <div class="anuncio-info">
                    <div class="anuncio-titulo">${a.titulo}</div>
                    <div class="anuncio-meta">${a.categoryNombre} | ${a.ubicacion}</div>
                    <div class="anuncio-precio">${a.precio}€</div>
                </div>
                <div class="anuncio-actions">
                    <button onclick="contactar(${a.id}, '${a.propietarioNombre}')">📞 Contactar</button>
                    <button class="danger" onclick="quitarFavoritoYRecargar(${a.id}, this)">💔 Quitar</button>
                </div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Error cargando favoritos:', error);
        const container = document.getElementById('misFavoritosList');
        if (container) {
            container.innerHTML = '<p class="error">Error cargando favoritos</p>';
        }
    }
}


// Función específica para quitar desde favoritos
async function quitarFavoritoYRecargar(anuncioId, btnElement) {
    try {
        const response = await fetch(`${API_URL}/favorites/${anuncioId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }

        // Recargar la lista de favoritos para que desaparezca el anuncio
        await loadMisFavoritos();

    } catch (error) {
        console.error('Error:', error);
        alert('Error al quitar de favoritos: ' + error.message);
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
// ==========================================
// CONFIGURACIÓN DE CATEGORÍAS Y SUBCATEGORÍAS
// ==========================================

const categoriasData = {
  "Bienestar y Salud": {
    subcategorias: ["Maquillaje", "Peluquería", "Masajes", "Yoga", "Nutrición"],
    filtros: {
      "Maquillaje": [
        { tipo: "checkbox", nombre: "tipo_maquillaje", label: "Tipo de maquillaje", opciones: ["Social", "Novia", "Editorial", "Caracterización", "FX"] },
        { tipo: "select", nombre: "experiencia", label: "Años de experiencia", opciones: ["< 1 año", "1-3 años", "3-5 años", "5+ años"] },
        { tipo: "toggle", nombre: "domicilio", label: "Servicio a domicilio" }
      ],
      "Peluquería": [
        { tipo: "checkbox", nombre: "servicios", label: "Servicios", opciones: ["Corte", "Color", "Mechas", "Alisado", "Permanente", "Recogidos"] },
        { tipo: "select", nombre: "tipo_pelo", label: "Especialidad en", opciones: ["Todo tipo", "Rizado", "Afro", "Liso", "Teñido"] }
      ],
      "Masajes": [
        { tipo: "checkbox", nombre: "tipo_masaje", label: "Tipos de masaje", opciones: ["Relajante", "Deportivo", "Terapéutico", "Linfático", "Con piedras"] },
        { tipo: "toggle", nombre: "aceites", label: "Incluye aceites esenciales" }
      ]
    }
  },
  "Transporte": {
    subcategorias: ["Taxi", "Mudanzas", "Transporte escolar", "Chófer privado", "Mensajería"],
    filtros: {
      "Taxi": [
        { tipo: "toggle", nombre: "licencia", label: "Licencia VTC" },
        { tipo: "select", nombre: "vehiculo", label: "Tipo de vehículo", opciones: ["Berlina", "Monovolumen", "Premium", "Adaptado"] },
        { tipo: "checkbox", nombre: "extras", label: "Servicios adicionales", opciones: ["WiFi", "Aire acondicionado", "Mascotas permitidas", "Inglés hablado"] }
      ],
      "Mudanzas": [
        { tipo: "select", nombre: "tamano", label: "Tamaño de mudanza", opciones: ["Pequeña (furgoneta)", "Mediana (camión pequeño)", "Grande (camión grande)", "Industrial"] },
        { tipo: "toggle", nombre: "embalaje", label: "Incluye embalaje" },
        { tipo: "toggle", nombre: "subida", label: "Incluye subida/bajada de muebles" }
      ],
      "Transporte escolar": [
        { tipo: "select", nombre: "capacidad", label: "Capacidad", opciones: ["1-3 plazas", "4-6 plazas", "7+ plazas"] },
        { tipo: "toggle", nombre: "acompana", label: "Acompañamiento puerta a puerta" }
      ]
    }
  },
  "Hogar": {
    subcategorias: ["Limpieza", "Fontanería", "Electricidad", "Carpintería", "Jardinería", "Pintura"],
    filtros: {
      "Limpieza": [
        { tipo: "checkbox", nombre: "tipo_limpieza", label: "Tipo de limpieza", opciones: ["General", "Profunda", "Cristales", "Después de obra", "Ecológica"] },
        { tipo: "select", nombre: "frecuencia", label: "Frecuencia", opciones: ["Puntual", "Semanal", "Quincenal", "Mensual"] },
        { tipo: "toggle", nombre: "productos", label: "Incluye productos de limpieza" }
      ],
      "Fontanería": [
        { tipo: "checkbox", nombre: "servicios_font", label: "Servicios", opciones: ["Desatascos", "Instalación", "Reparaciones", "Calderas", "Gas"] },
        { tipo: "toggle", nombre: "urgencia", label: "Disponible 24h" }
      ],
      "Electricidad": [
        { tipo: "checkbox", nombre: "servicios_elec", label: "Especialidades", opciones: ["Instalaciones", "Reparaciones", "Boletines", "Domótica", "Placas solares"] },
        { tipo: "toggle", nombre: "autorizado", label: "Instalador autorizado" }
      ]
    }
  },
  "Educación": {
    subcategorias: ["Clases particulares", "Idiomas", "Música", "Deportes", "Informática"],
    filtros: {
      "Clases particulares": [
        { tipo: "checkbox", nombre: "niveles", label: "Niveles", opciones: ["Primaria", "ESO", "Bachillerato", "Universidad", "FP"] },
        { tipo: "checkbox", nombre: "asignaturas", label: "Asignaturas", opciones: ["Matemáticas", "Física", "Química", "Lengua", "Historia", "Inglés"] },
        { tipo: "select", nombre: "modalidad", label: "Modalidad", opciones: ["Presencial", "Online", "Híbrida"] }
      ],
      "Idiomas": [
        { tipo: "checkbox", nombre: "idiomas", label: "Idiomas", opciones: ["Inglés", "Español", "Francés", "Alemán", "Chino", "Italiano"] },
        { tipo: "select", nombre: "nivel", label: "Nivel que imparte", opciones: ["Todos los niveles", "A1-A2", "B1-B2", "C1-C2", "Negocios"] }
      ]
    }
  },
  "Tecnología": {
    subcategorias: ["Reparación ordenadores", "Desarrollo web", "Diseño gráfico", "Fotografía", "Vídeo"],
    filtros: {
      "Reparación ordenadores": [
        { tipo: "checkbox", nombre: "servicios_tec", label: "Servicios", opciones: ["Hardware", "Software", "Virus", "Recuperación datos", "Redes"] },
        { tipo: "toggle", nombre: "domicilio_tec", label: "Reparación a domicilio" }
      ],
      "Desarrollo web": [
        { tipo: "checkbox", nombre: "tecnologias", label: "Tecnologías", opciones: ["WordPress", "React", "Angular", "PHP", "Python", "Node.js"] },
        { tipo: "select", nombre: "tipo_proyecto", label: "Tipo de proyecto", opciones: ["Web corporativa", "Tienda online", "App web", "Mantenimiento"] }
      ]
    }
  }
};

// ==========================================
// VARIABLES GLOBALES Y CONTROL DE ESTADO
// ==========================================

let anuncios = [];
let usuarioActual = null;
let misAnuncios = [];
let filtrosActivos = {
  categoria: "",
  busqueda: ""
};

// Flags para prevenir bucles
let estaCargando = false;
let inicializado = false;
let timeoutBusqueda = null;

// Cache de imágenes
const imagenesFallback = new Map();

// Claves para localStorage
const STORAGE_KEYS = {
  FAVORITOS: 'imirly_favoritos',
  USUARIO: 'imirly_usuario'
};

// ==========================================
// INICIALIZACIÓN ÚNICA
// ==========================================

document.addEventListener('DOMContentLoaded', function() {
  if (inicializado) return;
  inicializado = true;

  console.log('Inicializando aplicación...');
  inicializarCategorias();

  // Cargar favoritos desde localStorage
  cargarFavoritosGuardados();

  // Cargar datos iniciales
  cargarAnunciosIniciales();

  // Verificar si hay sesión guardada
  const usuarioGuardado = localStorage.getItem(STORAGE_KEYS.USUARIO);
  if (usuarioGuardado) {
    usuarioActual = JSON.parse(usuarioGuardado);
    mostrarDashboard();
  }
});

// ==========================================
// GESTIÓN DE FAVORITOS (CON LOCALSTORAGE)
// ==========================================

function cargarFavoritosGuardados() {
  const favoritosGuardados = localStorage.getItem(STORAGE_KEYS.FAVORITOS);
  if (favoritosGuardados) {
    const idsFavoritos = JSON.parse(favoritosGuardados);
    // Marcar anuncios como favoritos
    anuncios.forEach(anuncio => {
      anuncio.esFavorito = idsFavoritos.includes(anuncio.id);
    });
  }
}

function guardarFavoritos() {
  const idsFavoritos = anuncios
    .filter(a => a.esFavorito)
    .map(a => a.id);
  localStorage.setItem(STORAGE_KEYS.FAVORITOS, JSON.stringify(idsFavoritos));
}

function toggleFavorito(id) {
  const anuncio = anuncios.find(a => a.id === id);
  if (anuncio) {
    anuncio.esFavorito = !anuncio.esFavorito;
    guardarFavoritos(); // Persistir en localStorage
    aplicarFiltros(); // Actualizar vista actual

    // Si estamos en la pestaña de favoritos, recargarla
    const screenFavoritos = document.getElementById('screen-favoritos');
    if (screenFavoritos && !screenFavoritos.classList.contains('hidden')) {
      renderizarFavoritos();
    }
  }
}

function renderizarFavoritos() {
  const contenedor = document.getElementById('misFavoritosList');
  if (!contenedor) return;

  const favoritos = anuncios.filter(a => a.esFavorito);

  if (favoritos.length === 0) {
    contenedor.innerHTML = '<p class="muted">No tienes favoritos guardados. Ve a "Ver servicios disponibles" para añadir algunos.</p>';
    return;
  }

  contenedor.innerHTML = '';
  favoritos.forEach(anuncio => {
    const card = crearCardAnuncio(anuncio, true); // true = modo favoritos
    contenedor.appendChild(card);
  });
}

// ==========================================
// 1. DESPLEGABLE DE CATEGORÍAS
// ==========================================

function inicializarCategorias() {
  const filtroCategoria = document.getElementById('filtroCategoria');
  const anuncioCategoria = document.getElementById('anuncioCategoria');

  if (!filtroCategoria || filtroCategoria.options.length > 1) return;

  filtroCategoria.innerHTML = '<option value="">Todas las categorías</option>';
  if (anuncioCategoria) anuncioCategoria.innerHTML = '<option value="">Selecciona categoría</option>';

  Object.keys(categoriasData).forEach(cat => {
    const optionFiltro = document.createElement('option');
    optionFiltro.value = cat;
    optionFiltro.textContent = cat;
    filtroCategoria.appendChild(optionFiltro);

    if (anuncioCategoria) {
      const optionAnuncio = document.createElement('option');
      optionAnuncio.value = cat;
      optionAnuncio.textContent = cat;
      anuncioCategoria.appendChild(optionAnuncio);
    }
  });

  filtroCategoria.removeEventListener('change', handleCategoriaChange);
  filtroCategoria.addEventListener('change', handleCategoriaChange);

  const filtroBusqueda = document.getElementById('filtroBusqueda');
  if (filtroBusqueda) {
    filtroBusqueda.removeEventListener('keyup', handleBusquedaKeyup);
    filtroBusqueda.addEventListener('keyup', handleBusquedaKeyup);
  }
}

function handleCategoriaChange(e) {
  filtrosActivos.categoria = e.target.value;
  aplicarFiltros();
}

function handleBusquedaKeyup(e) {
  if (timeoutBusqueda) clearTimeout(timeoutBusqueda);
  timeoutBusqueda = setTimeout(() => {
    filtrosActivos.busqueda = e.target.value.toLowerCase().trim();
    aplicarFiltros();
  }, 300);
}

// ==========================================
// 2. CARGA DE DATOS
// ==========================================

async function cargarAnunciosIniciales() {
  if (estaCargando) return;
  estaCargando = true;

  try {
    // Datos de ejemplo (en producción: fetch('/api/anuncios/public'))
    anuncios = [
      {
        id: 1,
        titulo: "Maquillo muertos",
        categoria: "Bienestar y Salud",
        subcategoria: "Maquillaje",
        descripcion: "Servicio profesional de maquillaje para difuntos",
        ubicacion: "Córdoba",
        autor: "Margarita Duarte",
        precio: 25,
        tipoPrecio: "POR_HORA",
        estado: "PUBLICADO",
        imagen: "",
        esFavorito: false
      },
      {
        id: 2,
        titulo: "Fontanero 24h",
        categoria: "Hogar",
        subcategoria: "Fontanería",
        descripcion: "Desatascos urgentes",
        ubicacion: "Madrid",
        autor: "Juan García",
        precio: 50,
        tipoPrecio: "POR_SERVICIO",
        estado: "PUBLICADO",
        imagen: "",
        esFavorito: false
      },
      {
        id: 3,
        titulo: "Clases de inglés",
        categoria: "Educación",
        subcategoria: "Idiomas",
        descripcion: "Profesor nativo",
        ubicacion: "Barcelona",
        autor: "Mike Smith",
        precio: 20,
        tipoPrecio: "POR_HORA",
        estado: "PUBLICADO",
        imagen: "",
        esFavorito: false
      }
    ];

    // Restaurar estado de favoritos
    cargarFavoritosGuardados();

    aplicarFiltros();
  } catch (error) {
    console.error('Error cargando anuncios:', error);
    mostrarError('Error al cargar los servicios.');
  } finally {
    estaCargando = false;
  }
}

// ==========================================
// 3. SISTEMA DE BÚSQUEDA Y FILTRADO
// ==========================================

function aplicarFiltros() {
  const contenedor = document.getElementById('publicAnunciosList');
  if (!contenedor) return;

  contenedor.innerHTML = '';

  const anunciosFiltrados = anuncios.filter(anuncio => {
    if (filtrosActivos.categoria && anuncio.categoria !== filtrosActivos.categoria) {
      return false;
    }

    if (filtrosActivos.busqueda) {
      const termino = filtrosActivos.busqueda;
      const camposBusqueda = [
        anuncio.titulo,
        anuncio.descripcion,
        anuncio.ubicacion,
        anuncio.autor,
        anuncio.subcategoria,
        anuncio.categoria
      ].join(' ').toLowerCase();

      if (!camposBusqueda.includes(termino)) return false;
    }

    return true;
  });

  if (anunciosFiltrados.length === 0) {
    contenedor.innerHTML = '<p class="muted">No se encontraron servicios con los filtros seleccionados.</p>';
    return;
  }

  anunciosFiltrados.forEach(anuncio => {
    contenedor.appendChild(crearCardAnuncio(anuncio));
  });
}

function crearCardAnuncio(anuncio, modoFavoritos = false) {
  const div = document.createElement('div');
  div.className = 'anuncio-item';

  const imagenUrl = anuncio.imagen || generarPlaceholder(anuncio.titulo);

  let acciones = '';
  if (modoFavoritos) {
    acciones = `
      <button onclick="contactarAnuncio(${anuncio.id})">📞 Contactar</button>
      <button onclick="toggleFavorito(${anuncio.id})" class="favorite-active">❤️ Quitar</button>
    `;
  } else {
    acciones = `
      <button onclick="contactarAnuncio(${anuncio.id})">📞 Contactar</button>
      <button onclick="toggleFavorito(${anuncio.id})" class="${anuncio.esFavorito ? 'favorite-active' : ''}">
        ${anuncio.esFavorito ? '❤️' : '🤍'}
      </button>
    `;
  }

  div.innerHTML = `
    <img src="${imagenUrl}"
         alt="${anuncio.titulo}"
         class="anuncio-imagen"
         onerror="this.src='${generarPlaceholder(anuncio.titulo)}'"
         loading="lazy">
    <div class="anuncio-info">
      <div class="anuncio-titulo">${escapeHtml(anuncio.titulo)}</div>
      <div class="anuncio-meta">
        ${escapeHtml(anuncio.categoria)} → ${escapeHtml(anuncio.subcategoria)} |
        ${escapeHtml(anuncio.ubicacion)} | Por ${escapeHtml(anuncio.autor)}
      </div>
      <div style="margin-top: 4px;">
        <span class="badge ${anuncio.estado === 'PUBLICADO' ? 'complete' : 'incomplete'}">${anuncio.estado}</span>
      </div>
    </div>
    <div class="anuncio-precio">${anuncio.precio}€ /${formatearTipoPrecio(anuncio.tipoPrecio)}</div>
    <div class="anuncio-actions">
      ${acciones}
    </div>
  `;
  return div;
}

function generarPlaceholder(texto) {
    const iniciales = texto ? texto.substring(0, 2).toUpperCase() : 'NA';
    const svg = `<svg width="80" height="80" xmlns="http://www.w3.org/2000/svg">
        <rect width="80" height="80" fill="#6b5ce7"/>
        <text x="50%" y="50%" font-family="Arial" font-size="24" fill="white" text-anchor="middle" dy=".3em" font-weight="bold">${iniciales}</text>
    </svg>`;
    return 'data:image/svg+xml;base64,' + btoa(svg);
}

function escapeHtml(texto) {
  const div = document.createElement('div');
  div.textContent = texto;
  return div.innerHTML;
}

function formatearTipoPrecio(tipo) {
  return tipo.toLowerCase().replace('por_', '').replace('_', ' ');
}

// ==========================================
// 4. FILTROS DINÁMICOS EN FORMULARIO
// ==========================================

function cargarSubcategorias() {
  const categoriaSelect = document.getElementById('anuncioCategoria');
  const subcategoriaSelect = document.getElementById('anuncioSubcategoria');

  if (!categoriaSelect || !subcategoriaSelect) return;

  const categoria = categoriaSelect.value;
  subcategoriaSelect.innerHTML = '<option value="">Selecciona subcategoría</option>';

  if (categoria && categoriasData[categoria]) {
    categoriasData[categoria].subcategorias.forEach(sub => {
      const option = document.createElement('option');
      option.value = sub;
      option.textContent = sub;
      subcategoriaSelect.appendChild(option);
    });
  }

  const camposEspecificos = document.getElementById('camposEspecificos');
  if (camposEspecificos) camposEspecificos.innerHTML = '';
}

function mostrarInfoSubcategoria() {
  const categoria = document.getElementById('anuncioCategoria')?.value;
  const subcategoria = document.getElementById('anuncioSubcategoria')?.value;
  const contenedor = document.getElementById('camposEspecificos');

  if (!contenedor) return;
  contenedor.innerHTML = '';

  if (categoria && subcategoria && categoriasData[categoria]?.filtros?.[subcategoria]) {
    const filtros = categoriasData[categoria].filtros[subcategoria];

    filtros.forEach(filtro => {
      const div = document.createElement('div');
      div.className = 'campo-dinamico';

      switch(filtro.tipo) {
        case 'checkbox':
          div.innerHTML = crearCheckboxGroup(filtro);
          break;
        case 'select':
          div.innerHTML = crearSelect(filtro);
          break;
        case 'toggle':
          div.innerHTML = crearToggle(filtro);
          break;
      }

      contenedor.appendChild(div);
    });
  }
}

function crearCheckboxGroup(filtro) {
  let html = `<label>${escapeHtml(filtro.label)}</label><div class="checkbox-group">`;
  filtro.opciones.forEach(opcion => {
    const id = `${filtro.nombre}_${opcion.replace(/\s+/g, '_').toLowerCase()}_${Math.random().toString(36).substr(2, 5)}`;
    html += `
      <label class="checkbox-option">
        <input type="checkbox" name="${escapeHtml(filtro.nombre)}" value="${escapeHtml(opcion)}" id="${id}">
        <span>${escapeHtml(opcion)}</span>
      </label>
    `;
  });
  html += '</div>';
  return html;
}

function crearSelect(filtro) {
  let html = `<label for="${filtro.nombre}">${escapeHtml(filtro.label)}</label>
              <select id="${filtro.nombre}" name="${filtro.nombre}">
                <option value="">Selecciona...</option>`;
  filtro.opciones.forEach(opcion => {
    html += `<option value="${escapeHtml(opcion)}">${escapeHtml(opcion)}</option>`;
  });
  html += '</select>';
  return html;
}

function crearToggle(filtro) {
  return `
    <label class="toggle-label">
      <input type="checkbox" name="${filtro.nombre}" id="${filtro.nombre}">
      <span class="toggle-slider"></span>
      <span class="toggle-text">${escapeHtml(filtro.label)}</span>
    </label>
  `;
}

// ==========================================
// 5. NAVEGACIÓN Y PANTALLAS
// ==========================================

function goExplore() {
  showScreen('explore');
  aplicarFiltros();
}

function goDashboard() {
  showScreen('dashboard');
  // Cargar datos del usuario y sus anuncios al entrar
  cargarDatosUsuario();
  loadMisAnuncios();
}

function showScreen(screenId) {
  document.querySelectorAll('section[id^="screen-"]').forEach(s => s.classList.add('hidden'));
  const screen = document.getElementById('screen-' + screenId);
  if (screen) screen.classList.remove('hidden');
}

// ==========================================
// 6. GESTIÓN DE USUARIO Y PERFIL
// ==========================================

function cargarDatosUsuario() {
  if (!usuarioActual) return;

  // Actualizar info en el perfil
  const profileName = document.getElementById('profileName');
  const profileEmail = document.getElementById('profileEmail');
  const profileCompleteness = document.getElementById('profileCompleteness');

  if (profileName) profileName.textContent = usuarioActual.nombre || 'Usuario';
  if (profileEmail) profileEmail.textContent = usuarioActual.email || '';

  // Calcular completitud del perfil
  let camposCompletos = 0;
  let camposTotales = 6; // nombre, email, telefono, fechaNacimiento, direccion, ciudad

  if (usuarioActual.nombre) camposCompletos++;
  if (usuarioActual.email) camposCompletos++;
  if (usuarioActual.telefono) camposCompletos++;
  if (usuarioActual.fechaNacimiento) camposCompletos++;
  if (usuarioActual.direccion) camposCompletos++;
  if (usuarioActual.ciudad) camposCompletos++;

  const porcentaje = Math.round((camposCompletos / camposTotales) * 100);

  if (profileCompleteness) {
    if (porcentaje === 100) {
      profileCompleteness.textContent = '✓ Perfil completo';
      profileCompleteness.className = 'badge complete';
    } else {
      profileCompleteness.textContent = `Perfil ${porcentaje}% completado`;
      profileCompleteness.className = 'badge incomplete';
    }
  }
}

function login() {
  const email = document.getElementById('loginEmail')?.value;
  const password = document.getElementById('loginPassword')?.value;

  // Validación básica
  if (!email || !password) {
    const errorDiv = document.getElementById('err-loginGeneral');
    if (errorDiv) errorDiv.textContent = 'Por favor, introduce email y contraseña';
    return;
  }

  // Simular login (en producción: fetch al backend)
  usuarioActual = {
    id: 1,
    nombre: email.split('@')[0], // Temporal
    email: email,
    telefono: '',
    fechaNacimiento: '',
    direccion: '',
    ciudad: '',
    cp: ''
  };

  // Guardar en localStorage
  localStorage.setItem(STORAGE_KEYS.USUARIO, JSON.stringify(usuarioActual));

  mostrarDashboard();
}

function mostrarDashboard() {
  document.getElementById('screen-auth')?.classList.add('hidden');
  document.getElementById('screen-dashboard')?.classList.remove('hidden');

  const btnLogout = document.getElementById('btnLogout');
  const sessionInfo = document.getElementById('sessionInfo');

  if (btnLogout) btnLogout.classList.remove('hidden');
  if (sessionInfo && usuarioActual) {
    sessionInfo.textContent = `Logueado como ${usuarioActual.email}`;
  }

  // Cargar datos del perfil y anuncios
  cargarDatosUsuario();
  loadMisAnuncios();
}

function logout() {
  usuarioActual = null;
  localStorage.removeItem(STORAGE_KEYS.USUARIO);

  document.getElementById('screen-auth')?.classList.remove('hidden');
  document.getElementById('screen-dashboard')?.classList.add('hidden');

  const btnLogout = document.getElementById('btnLogout');
  const sessionInfo = document.getElementById('sessionInfo');

  if (btnLogout) btnLogout.classList.add('hidden');
  if (sessionInfo) sessionInfo.textContent = '';
}

function register() {
  const nombre = document.getElementById('regNombre')?.value;
  const email = document.getElementById('regEmail')?.value;
  const password = document.getElementById('regPassword')?.value;
  const confirmPassword = document.getElementById('regConfirmPassword')?.value;

  // Validaciones
  if (!nombre || !email || !password || !confirmPassword) {
    const errorDiv = document.getElementById('err-registerGeneral');
    if (errorDiv) errorDiv.textContent = 'Por favor, completa todos los campos';
    return;
  }

  if (password !== confirmPassword) {
    const errorDiv = document.getElementById('err-registerGeneral');
    if (errorDiv) errorDiv.textContent = 'Las contraseñas no coinciden';
    return;
  }

  if (password.length < 6) {
    const errorDiv = document.getElementById('err-registerGeneral');
    if (errorDiv) errorDiv.textContent = 'La contraseña debe tener al menos 6 caracteres';
    return;
  }

  // Simular registro exitoso
  alert('¡Cuenta creada correctamente! Ahora puedes iniciar sesión.');

  // Limpiar formulario
  document.getElementById('regNombre').value = '';
  document.getElementById('regEmail').value = '';
  document.getElementById('regPassword').value = '';
  document.getElementById('regConfirmPassword').value = '';
}

function showEditProfileForm() {
  if (!usuarioActual) return;

  // Rellenar formulario con datos actuales
  document.getElementById('editTelefono').value = usuarioActual.telefono || '';
  document.getElementById('editFechaNacimiento').value = usuarioActual.fechaNacimiento || '';
  document.getElementById('editCalle').value = usuarioActual.direccion || '';
  document.getElementById('editCiudad').value = usuarioActual.ciudad || '';
  document.getElementById('editCP').value = usuarioActual.cp || '';
  document.getElementById('editFoto').value = usuarioActual.foto || '';

  document.getElementById('editProfileForm')?.classList.remove('hidden');
}

function hideEditProfileForm() {
  document.getElementById('editProfileForm')?.classList.add('hidden');
}

function updateProfile() {
  const telefono = document.getElementById('editTelefono')?.value;
  const fechaNacimiento = document.getElementById('editFechaNacimiento')?.value;
  const calle = document.getElementById('editCalle')?.value;
  const ciudad = document.getElementById('editCiudad')?.value;
  const cp = document.getElementById('editCP')?.value;
  const foto = document.getElementById('editFoto')?.value;

  // Validar campos obligatorios
  if (!telefono || !fechaNacimiento || !calle || !ciudad || !cp) {
    alert('Por favor, completa todos los campos obligatorios (*)');
    return;
  }

  // Actualizar usuario
  usuarioActual.telefono = telefono;
  usuarioActual.fechaNacimiento = fechaNacimiento;
  usuarioActual.direccion = calle;
  usuarioActual.ciudad = ciudad;
  usuarioActual.cp = cp;
  usuarioActual.foto = foto;

  // Guardar en localStorage
  localStorage.setItem(STORAGE_KEYS.USUARIO, JSON.stringify(usuarioActual));

  // Actualizar vista
  cargarDatosUsuario();
  hideEditProfileForm();

  alert('Perfil actualizado correctamente');
}

function showChangePasswordForm() {
  document.getElementById('changePasswordForm')?.classList.remove('hidden');
}

function hideChangePasswordForm() {
  document.getElementById('changePasswordForm')?.classList.add('hidden');
}

function updatePassword() {
  const currentPassword = document.getElementById('currentPassword')?.value;
  const newPassword = document.getElementById('newPassword')?.value;
  const confirmNewPassword = document.getElementById('confirmNewPassword')?.value;

  if (!currentPassword || !newPassword || !confirmNewPassword) {
    alert('Por favor, completa todos los campos');
    return;
  }

  if (newPassword !== confirmNewPassword) {
    alert('Las contraseñas nuevas no coinciden');
    return;
  }

  if (newPassword.length < 6) {
    alert('La nueva contraseña debe tener al menos 6 caracteres');
    return;
  }

  alert('Contraseña cambiada correctamente');
  hideChangePasswordForm();

  // Limpiar campos
  document.getElementById('currentPassword').value = '';
  document.getElementById('newPassword').value = '';
  document.getElementById('confirmNewPassword').value = '';
}

function deleteAccount() {
  if (confirm('¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.')) {
    // Eliminar datos
    localStorage.removeItem(STORAGE_KEYS.USUARIO);
    localStorage.removeItem(STORAGE_KEYS.FAVORITOS);

    alert('Cuenta eliminada correctamente');
    logout();
  }
}

// ==========================================
// 7. MIS ANUNCIOS (CORREGIDO)
// ==========================================

function loadMisAnuncios() {
  const contenedor = document.getElementById('misAnunciosList');
  if (!contenedor) return;

  // Simular carga desde backend
  // En producción: fetch('/api/mis-anuncios')

  // Por ahora, filtrar anuncios del usuario actual
  const misAnunciosFiltrados = anuncios.filter(a =>
    usuarioActual && a.autor === usuarioActual.nombre
  );

  if (misAnunciosFiltrados.length === 0) {
    contenedor.innerHTML = '<p class="muted">No tienes anuncios publicados aún. ¡Crea tu primer servicio arriba!</p>';
    return;
  }

  contenedor.innerHTML = '';
  misAnunciosFiltrados.forEach(anuncio => {
    const div = document.createElement('div');
    div.className = 'anuncio-item';

    const imagenUrl = anuncio.imagen || generarPlaceholder(anuncio.titulo);

    div.innerHTML = `
      <img src="${imagenUrl}" alt="${anuncio.titulo}" class="anuncio-imagen"
           onerror="this.src='${generarPlaceholder(anuncio.titulo)}'">
      <div class="anuncio-info">
        <div class="anuncio-titulo">${escapeHtml(anuncio.titulo)}</div>
        <div class="anuncio-meta">
          ${escapeHtml(anuncio.categoria)} → ${escapeHtml(anuncio.subcategoria)} | ${escapeHtml(anuncio.ubicacion)}
        </div>
        <div style="margin-top: 4px;">
          <span class="status-badge status-${anuncio.estado}">${anuncio.estado}</span>
        </div>
      </div>
      <div class="anuncio-precio">${anuncio.precio}€ /${formatearTipoPrecio(anuncio.tipoPrecio)}</div>
      <div class="anuncio-actions">
        <button onclick="editarAnuncio(${anuncio.id})">✏️ Editar</button>
        <button onclick="cambiarEstadoAnuncio(${anuncio.id})" class="secondary">
          ${anuncio.estado === 'PUBLICADO' ? 'Despublicar' : 'Publicar'}
        </button>
        <button onclick="eliminarAnuncio(${anuncio.id})" class="danger">🗑️ Eliminar</button>
      </div>
    `;
    contenedor.appendChild(div);
  });
}

function filtrarMisAnuncios(filtro) {
  const contenedor = document.getElementById('misAnunciosList');
  if (!contenedor) return;

  const misAnunciosFiltrados = anuncios.filter(a => {
    if (usuarioActual && a.autor !== usuarioActual.nombre) return false;
    if (filtro === 'todos') return true;
    return a.estado === filtro;
  });

  if (misAnunciosFiltrados.length === 0) {
    contenedor.innerHTML = `<p class="muted">No tienes anuncios ${filtro === 'todos' ? '' : 'en este estado'}.</p>`;
    return;
  }

  contenedor.innerHTML = '';
  misAnunciosFiltrados.forEach(anuncio => {
    // ... mismo código que loadMisAnuncios
    const div = document.createElement('div');
    div.className = 'anuncio-item';
    const imagenUrl = anuncio.imagen || generarPlaceholder(anuncio.titulo);
    div.innerHTML = `
      <img src="${imagenUrl}" alt="${anuncio.titulo}" class="anuncio-imagen"
           onerror="this.src='${generarPlaceholder(anuncio.titulo)}'">
      <div class="anuncio-info">
        <div class="anuncio-titulo">${escapeHtml(anuncio.titulo)}</div>
        <div class="anuncio-meta">
          ${escapeHtml(anuncio.categoria)} → ${escapeHtml(anuncio.subcategoria)} | ${escapeHtml(anuncio.ubicacion)}
        </div>
        <div style="margin-top: 4px;">
          <span class="status-badge status-${anuncio.estado}">${anuncio.estado}</span>
        </div>
      </div>
      <div class="anuncio-precio">${anuncio.precio}€ /${formatearTipoPrecio(anuncio.tipoPrecio)}</div>
      <div class="anuncio-actions">
        <button onclick="editarAnuncio(${anuncio.id})">✏️ Editar</button>
        <button onclick="cambiarEstadoAnuncio(${anuncio.id})" class="secondary">
          ${anuncio.estado === 'PUBLICADO' ? 'Despublicar' : 'Publicar'}
        </button>
        <button onclick="eliminarAnuncio(${anuncio.id})" class="danger">🗑️ Eliminar</button>
      </div>
    `;
    contenedor.appendChild(div);
  });
}

function editarAnuncio(id) {
  console.log('Editar anuncio:', id);
  alert('Función de editar en desarrollo');
}

function cambiarEstadoAnuncio(id) {
  const anuncio = anuncios.find(a => a.id === id);
  if (anuncio) {
    anuncio.estado = anuncio.estado === 'PUBLICADO' ? 'DESPUBLICADO' : 'PUBLICADO';
    loadMisAnuncios(); // Recargar lista
  }
}

function eliminarAnuncio(id) {
  if (confirm('¿Estás seguro de que quieres eliminar este anuncio?')) {
    const index = anuncios.findIndex(a => a.id === id);
    if (index > -1) {
      anuncios.splice(index, 1);
      loadMisAnuncios();
    }
  }
}

// ==========================================
// 8. PESTAÑA FAVORITOS
// ==========================================

function loadMisFavoritos() {
  renderizarFavoritos();
}

// ==========================================
// 9. FORMULARIO DE CREACIÓN
// ==========================================

function crearAnuncioStep1() {
  const categoria = document.getElementById('anuncioCategoria')?.value;
  const subcategoria = document.getElementById('anuncioSubcategoria')?.value;
  const titulo = document.getElementById('anuncioTitulo')?.value;
  const precio = document.getElementById('anuncioPrecio')?.value;
  const ubicacion = document.getElementById('anuncioUbicacion')?.value;

  const errorDiv = document.getElementById('err-anuncioGeneral');

  if (!categoria || !subcategoria || !titulo || !precio || !ubicacion) {
    if (errorDiv) errorDiv.textContent = 'Por favor, completa todos los campos obligatorios (*)';
    return;
  }

  document.getElementById('anuncioStep1')?.classList.add('hidden');
  document.getElementById('anuncioStep2')?.classList.remove('hidden');
  const indicator = document.getElementById('anuncioStepIndicator');
  if (indicator) indicator.textContent = 'Paso 2 de 2: Detalles específicos';
}

function volverStep1() {
  document.getElementById('anuncioStep2')?.classList.add('hidden');
  document.getElementById('anuncioStep1')?.classList.remove('hidden');
  const indicator = document.getElementById('anuncioStepIndicator');
  if (indicator) indicator.textContent = 'Paso 1 de 2: Información general';
}

function completarAnuncioStep2() {
  // Recoger datos del paso 1
  const imagen = document.getElementById('anuncioImagen')?.value || '';
  const categoria = document.getElementById('anuncioCategoria')?.value;
  const subcategoria = document.getElementById('anuncioSubcategoria')?.value;
  const titulo = document.getElementById('anuncioTitulo')?.value;
  const descripcion = document.getElementById('anuncioDescripcion')?.value || '';
  const precio = parseFloat(document.getElementById('anuncioPrecio')?.value);
  const tipoPrecio = document.getElementById('anuncioTipoPrecio')?.value;
  const ubicacion = document.getElementById('anuncioUbicacion')?.value;

  // Recoger campos dinámicos
  const camposDinamicos = {};
  const contenedor = document.getElementById('camposEspecificos');

  if (contenedor) {
    const inputs = contenedor.querySelectorAll('input, select');
    inputs.forEach(input => {
      if (input.type === 'checkbox') {
        if (!camposDinamicos[input.name]) camposDinamicos[input.name] = [];
        if (input.checked) camposDinamicos[input.name].push(input.value);
      } else {
        camposDinamicos[input.name] = input.value;
      }
    });
  }

  // Crear nuevo anuncio
  const nuevoAnuncio = {
    id: Date.now(), // ID temporal
    titulo,
    categoria,
    subcategoria,
    descripcion,
    ubicacion,
    autor: usuarioActual ? usuarioActual.nombre : 'Anónimo',
    precio,
    tipoPrecio,
    estado: 'PUBLICADO',
    imagen,
    esFavorito: false,
    camposEspecificos: camposDinamicos
  };

  // Añadir a la lista
  anuncios.push(nuevoAnuncio);

  alert('¡Anuncio publicado correctamente!');

  // Resetear formulario
  document.getElementById('anuncioImagen').value = '';
  document.getElementById('anuncioCategoria').value = '';
  document.getElementById('anuncioSubcategoria').innerHTML = '<option value="">Selecciona subcategoría</option>';
  document.getElementById('anuncioTitulo').value = '';
  document.getElementById('anuncioDescripcion').value = '';
  document.getElementById('anuncioPrecio').value = '';
  document.getElementById('anuncioUbicacion').value = '';
  document.getElementById('camposEspecificos').innerHTML = '';

  volverStep1();

  // Recargar mis anuncios
  loadMisAnuncios();
}

// ==========================================
// 10. MENSAJES (PLACEHOLDER)
// ==========================================

function renderizarConversaciones() {
  const list = document.getElementById('listaConversaciones');
  if (list) list.innerHTML = '<p class="muted">No tienes mensajes.</p>';
}

// ==========================================
// 11. UTILIDADES
// ==========================================

function contactarAnuncio(id) {
  console.log('Contactando anuncio:', id);
  alert('Sistema de mensajes en desarrollo. ID del anuncio: ' + id);
}

function mostrarError(mensaje) {
  const errorDiv = document.getElementById('err-publicAnunciosGeneral');
  if (errorDiv) {
    errorDiv.textContent = mensaje;
    setTimeout(() => errorDiv.textContent = '', 5000);
  }
}

function openH2Console() {
  window.open('/h2-console', '_blank');
}