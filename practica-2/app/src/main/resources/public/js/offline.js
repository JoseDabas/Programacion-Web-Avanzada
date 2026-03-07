/**
 * Gestiona la funcionalidad sin conexión de la aplicación ShortURL
 */

// Variables globales
let isOnline = navigator.onLine;
let username = null;

// Verificar si el Service Worker está soportado
if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    registerServiceWorker();

    // Escuchar eventos de conexión
    window.addEventListener("online", handleConnectionChange);
    window.addEventListener("offline", handleConnectionChange);

    // Obtener el nombre de usuario actual de la sesión (si existe)
    getUsernameFromSession();
  });
}

/**
 * Registra el Service Worker
 */
// En offline.js, modifica la línea 44
async function registerServiceWorker() {
  try {
    const registration = await navigator.serviceWorker.register('/service-worker.js', {
      scope: '/' // Asegura que el alcance es la raíz del sitio
    });
    console.log('Service Worker registrado con éxito:', registration.scope);
    
    // resto del código...
  } catch (error) {
    console.error('Error al registrar el Service Worker:', error);
  }
}

/**
 * Registra la sincronización en segundo plano
 * @param {ServiceWorkerRegistration} registration - Registro del Service Worker
 */
function registerBackgroundSync(registration) {
  // Intentar registrar la sincronización de URLs
  registration.sync
    .register("sync-urls")
    .then(() => console.log("Sincronización en segundo plano registrada"))
    .catch((error) =>
      console.error(
        "Error al registrar sincronización en segundo plano:",
        error
      )
    );
}

/**
 * Maneja los mensajes recibidos del Service Worker
 * @param {MessageEvent} event - Evento de mensaje
 */
function handleServiceWorkerMessage(event) {
  const message = event.data;

  if (!message || !message.type) return;

  switch (message.type) {
    case "GET_URLS_TO_SYNC":
      // Obtener URLs pendientes y enviarlas de vuelta al Service Worker
      getPendingUrls()
        .then((urls) => {
          event.ports[0].postMessage(urls);
        })
        .catch((error) => {
          console.error("Error al obtener URLs pendientes:", error);
          event.ports[0].postMessage([]);
        });
      break;

    case "MARK_URL_SYNCED":
      // Marcar una URL como sincronizada
      if (message.id) {
        markUrlAsSynced(message.id)
          .then(() => {
            event.ports[0].postMessage({ success: true });
          })
          .catch((error) => {
            console.error("Error al marcar URL como sincronizada:", error);
            event.ports[0].postMessage({
              success: false,
              error: error.message,
            });
          });
      }
      break;
  }
}

/**
 * Maneja cambios en el estado de la conexión
 */
function handleConnectionChange() {
  isOnline = navigator.onLine;
  updateOfflineUI();
  
  // Mostrar modal al cambiar a modo offline
  if (!isOnline) {
    // Usar jQuery para mostrar el modal
    $('#offlineBanner').removeClass('d-none');
    console.log("Cambiado a modo offline");
  } else {
    $('#offlineBanner').addClass('d-none');
    console.log("Conexión restaurada");
  }
}

/**
 * Actualiza la interfaz según el estado de conexión
 */
function updateOfflineUI() {
  const offlineMessage = document.getElementById("offlineMessage");
  const offlineIcon = document.querySelector(".offline-icon");

  if (offlineMessage) {
    if (isOnline) {
      offlineMessage.style.display = "none";
    } else {
      offlineMessage.style.display = "block";
      offlineMessage.innerHTML =
        '<i class="fas fa-wifi-slash mr-2"></i> Sin conexión. Estás viendo datos guardados localmente.';
    }
  }

  if (offlineIcon) {
    if (isOnline) {
      offlineIcon.classList.remove("fa-wifi-slash");
      offlineIcon.classList.add("fa-wifi");
    } else {
      offlineIcon.classList.remove("fa-wifi");
      offlineIcon.classList.add("fa-wifi-slash");
    }
  }
}

/**
 * Sincroniza URLs con el servidor cuando se recupera la conexión
 */
async function syncUrlsWithServer() {
  if (!isOnline) return;

  try {
    // Obtener URLs pendientes de sincronización
    const pendingUrls = await getPendingUrls();

    if (pendingUrls.length === 0) {
      console.log("No hay URLs pendientes para sincronizar");
      return;
    }

    console.log(`Sincronizando ${pendingUrls.length} URLs...`);

    // Intentar sincronizar cada URL
    for (const url of pendingUrls) {
      try {
        // Llamar a la API para sincronizar
        const response = await fetch("/url/shorten", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          body: `URL=${encodeURIComponent(url.urlViejo)}`,
        });

        if (response.ok) {
          // Marcar como sincronizada
          await markUrlAsSynced(url.id);
          console.log(`URL ${url.urlNuevo} sincronizada correctamente`);
        } else {
          console.error(
            `Error al sincronizar URL ${url.urlNuevo}: ${response.status}`
          );
        }
      } catch (error) {
        console.error(`Error al sincronizar URL ${url.urlNuevo}:`, error);
      }
    }

    // Recargar la página para reflejar los cambios
    if (pendingUrls.length > 0) {
      window.location.reload();
    }
  } catch (error) {
    console.error("Error en la sincronización:", error);
  }
}

/**
 * Obtiene el nombre de usuario actual de la sesión
 */
function getUsernameFromSession() {
  // Intentar obtener el nombre de usuario del elemento HTML
  const userElement = document.querySelector(".nav-item[data-username]");

  if (userElement) {
    // Intentar obtener el nombre de usuario
    username = userElement.getAttribute("data-username");
  } else {
    // Si no encontramos el elemento, usamos un valor predeterminado o null
    username = null;
  }
}

/**
 * Extrae las URLs existentes de la tabla HTML
 * @returns {Array<Object>} Array de objetos URL
 */
function extractUrlsFromTable() {
  const urls = [];
  const rows = document.querySelectorAll("#urlsTable tbody tr");

  rows.forEach((row) => {
    const cells = row.querySelectorAll("td");
    if (cells.length >= 3) {
      const urlViejoElement = cells[0].querySelector("div");
      const urlNuevoElement = cells[1].querySelector("a");
      const clicksElement = cells[2].querySelector("span");

      if (urlViejoElement && urlNuevoElement && clicksElement) {
        const urlViejo = urlViejoElement.textContent.trim();
        const fullShortUrl = urlNuevoElement.textContent.trim();
        // Extraer solo la parte final de la URL
        const urlNuevo = fullShortUrl.split("/").pop();
        const clicks = parseInt(clicksElement.textContent.trim()) || 0;

        urls.push({
          id: "url_" + urlNuevo,
          urlViejo: urlViejo,
          urlNuevo: urlNuevo,
          usuario: username || "anonymous",
          activo: true,
          clicks: clicks,
          fechaCreacion: new Date().toISOString(),
          syncStatus: "synced",
        });
      }
    }
  });

  return urls;
}

/**
 * Carga las URLs desde IndexedDB para mostrarlas en modo sin conexión
 */
async function loadOfflineUrls() {
  try {
    let urls;

    // Si conocemos el nombre de usuario, filtramos por él
    if (username) {
      urls = await getUrlsByUsername(username);
    } else {
      // Si no, cargamos todas las URLs
      urls = await getAllUrls();
    }

    // Si no hay URLs en IndexedDB pero estamos online, extraer de la tabla
    if (urls.length === 0 && isOnline) {
      const tableUrls = extractUrlsFromTable();
      if (tableUrls.length > 0) {
        await saveUrlsToIndexedDB(tableUrls);
        urls = tableUrls;
      }
    }

    if (!isOnline) {
      updateOfflineUI();
    }
  } catch (error) {
    console.error("Error al cargar URLs offline:", error);
    showErrorMessage("No se pudieron cargar las URLs guardadas localmente.");
  }
}

/**
 * Muestra las URLs en la tabla para el modo sin conexión
 * @param {Array<Object>} urls - Array de objetos URL
 */
function displayOfflineUrls(urls) {
  const tableBody = document.querySelector("#urlsTable tbody");
  if (!tableBody) return;

  // Limpiar tabla
  tableBody.innerHTML = "";

  if (urls.length === 0) {
    // Mostrar mensaje si no hay URLs
    const row = document.createElement("tr");
    row.innerHTML = `
      <td colspan="4" class="text-center">
        <i class="fas fa-info-circle mr-2"></i>
        No hay URLs guardadas localmente
      </td>
    `;
    tableBody.appendChild(row);
    return;
  }

  // Agregar cada URL a la tabla
  urls.forEach((url) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>
        <div class="text-truncate" style="max-width: 250px" title="${
          url.urlViejo
        }">
          ${url.urlViejo}
        </div>
      </td>
      <td>
        <a class="text-primary" href="http://localhost:7000/${
          url.urlNuevo
        }" target="_blank">
          http://localhost:7000/${url.urlNuevo}
        </a>
        ${
          url.syncStatus === "pending"
            ? `<span class="badge badge-warning ml-2">
            <i class="fas fa-sync-alt syncing-icon mr-1"></i> Pendiente
          </span>`
            : ""
        }
      </td>
      <td>
        <span class="badge badge-primary p-2">${url.clicks}</span>
      </td>
      <td>
        <button class="btn btn-outline-primary btn-sm qr-button" data-url="http://localhost:7000/${
          url.urlNuevo
        }">
          <i class="fas fa-qrcode mr-1"></i> Ver QR
        </button>
      </td>
    `;
    tableBody.appendChild(row);
  });

  // Agregar eventos para botones QR
  document.querySelectorAll(".qr-button").forEach((button) => {
    button.addEventListener("click", function () {
      const url = this.getAttribute("data-url");
      showQrModal(url);
    });
  });
}

/**
 * Muestra el modal con el código QR
 * @param {string} url - URL para generar el código QR
 */
function showQrModal(url) {
  const modal = document.getElementById("qrModal");
  if (!modal) return;

  const bootstrapModal = new bootstrap.Modal(modal);

  // Generar el código QR
  generateQRCode("qrcodeModal", url);

  // Mostrar el modal
  bootstrapModal.show();
}

/**
 * Muestra un mensaje de error
 * @param {string} message - Mensaje de error
 */
function showErrorMessage(message) {
  const container = document.querySelector(".container");
  if (!container) return;

  const alert = document.createElement("div");
  alert.className = "alert alert-danger mt-3";
  alert.innerHTML = `<i class="fas fa-exclamation-circle mr-2"></i>${message}`;

  // Insertar al principio del contenedor
  container.insertBefore(alert, container.firstChild);

  // Eliminar después de 5 segundos
  setTimeout(() => {
    alert.remove();
  }, 5000);
}

/**
 * Guarda las URLs recuperadas del servidor en IndexedDB
 * @param {Array<Object>} urls - URLs recuperadas del servidor
 */
function cacheUrls(urls) {
  if (!Array.isArray(urls) || urls.length === 0) return;

  // Guardar en IndexedDB
  saveUrlsToIndexedDB(urls)
    .then(() => console.log(`${urls.length} URLs guardadas localmente`))
    .catch((error) =>
      console.error("Error al guardar URLs localmente:", error)
    );
}

/**
 * Crea una nueva URL en modo offline
 * @param {string} originalUrl - URL original a acortar
 * @returns {Promise<boolean>} Promesa que se resuelve con true si se crea correctamente
 */
async function createUrlOffline(originalUrl) {
  if (isOnline) return false; // Solo crear offline si no hay conexión

  if (!username) {
    showErrorMessage("Debes iniciar sesión para crear URLs");
    return false;
  }

  try {
    // Generar un ID temporal
    const tempId = "temp_" + Date.now();

    // Generar una URL corta temporal
    const shortUrl = generateTemporaryShortUrl(originalUrl);

    // Crear objeto URL
    const url = {
      id: tempId,
      urlViejo: originalUrl,
      urlNuevo: shortUrl,
      usuario: username,
      activo: true,
      clicks: 0,
      fechaCreacion: new Date().toISOString(),
      syncStatus: "pending", // Marcar como pendiente de sincronización
    };

    // Guardar en IndexedDB
    await saveUrlToIndexedDB(url);

    // Añadir la nueva URL a la tabla
    const tableBody = document.querySelector("#urlsTable tbody");
    if (tableBody) {
      // Código para actualizar la tabla...
    }

    // Mostrar el modal explícitamente de varias formas
    console.log("Intentando mostrar modal de URL offline creada");
    
    // Intento 1: jQuery estándar
    if (typeof $ !== 'undefined' && typeof $.fn.modal !== 'undefined') {
      console.log("Usando jQuery modal");
      $('#offlineModal').modal('show');
    } 
    // Intento 2: Bootstrap nativo
    else if (typeof bootstrap !== 'undefined' && typeof bootstrap.Modal !== 'undefined') {
      console.log("Usando Bootstrap nativo");
      const modalElement = document.getElementById('offlineModal');
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    }
    // Intento 3: Método manual
    else {
      console.log("Usando método manual");
      const modalElement = document.getElementById('offlineModal');
      if (modalElement) {
        modalElement.style.display = 'block';
        modalElement.classList.add('show');
        document.body.classList.add('modal-open');
        
        // Crear backdrop
        const backdrop = document.createElement('div');
        backdrop.className = 'modal-backdrop fade show';
        document.body.appendChild(backdrop);
      } else {
        console.error("No se encontró el elemento del modal");
      }
    }

    return true;
  } catch (error) {
    console.error("Error al crear URL offline:", error);
    showErrorMessage("No se pudo crear la URL en modo sin conexión");
    return false;
  }
}

/**
 * Función simple para generar una URL corta temporal
 * @param {string} originalUrl - URL original
 * @returns {string} URL corta temporal
 */
function generateTemporaryShortUrl(originalUrl) {
  // Esto es una implementación simple para modo offline
  // En producción, debería ser más sofisticado y asegurar unicidad
  const randomPart = Math.random().toString(36).substring(2, 8);
  return "temp_" + randomPart;
}

/**
 * Funciones para interactuar con db.js
 * Estas funciones son solo envolturas (wrappers) para las funciones definidas en db.js
 */

async function saveUrlsToIndexedDB(urls) {
  if (window.IndexedDBHelper && typeof window.IndexedDBHelper.saveUrlsToIndexedDB === "function") {
    return window.IndexedDBHelper.saveUrlsToIndexedDB(urls);
  } else {
    console.warn("La función saveUrlsToIndexedDB no está disponible correctamente");
    return Promise.resolve();
  }
}

async function getUrlsByUsername(username) {
  if (window.IndexedDBHelper && typeof window.IndexedDBHelper.getUrlsByUsername === "function") {
    return window.IndexedDBHelper.getUrlsByUsername(username);
  } else {
    console.warn("La función getUrlsByUsername no está disponible correctamente");
    return Promise.resolve([]);
  }
}

async function getAllUrls() {
  if (typeof window.getAllUrls === "function" && window.getAllUrls !== getAllUrls) {
    return window.getAllUrls();
  } else {
    console.warn("La función getAllUrls no está disponible correctamente");
    return Promise.resolve([]);
  }
}

async function markUrlAsPending(id) {
  if (typeof window.markUrlAsPending === "function" && window.markUrlAsPending !== markUrlAsPending) {
    return window.markUrlAsPending(id);
  } else {
    console.warn("La función markUrlAsPending no está disponible correctamente");
    return Promise.resolve();
  }
}

async function markUrlAsSynced(id) {
  if (typeof window.markUrlAsSynced === "function" && window.markUrlAsSynced !== markUrlAsSynced) {
    return window.markUrlAsSynced(id);
  } else {
    console.warn("La función markUrlAsSynced no está disponible correctamente");
    return Promise.resolve();
  }
}

async function getPendingUrls() {
  if (typeof window.getPendingUrls === "function" && window.getPendingUrls !== getPendingUrls) {
    return window.getPendingUrls();
  } else {
    console.warn("La función getPendingUrls no está disponible correctamente");
    return Promise.resolve([]);
  }
}

// Exportar funciones para uso global
window.loadOfflineUrls = loadOfflineUrls;
window.createUrlOffline = createUrlOffline;

// Inicializar la interfaz según el estado de conexión inicial
document.addEventListener("DOMContentLoaded", () => {
  updateOfflineUI();

  // Cachear las URLs existentes en la página
  const tableUrls = extractUrlsFromTable();
  if (tableUrls.length > 0) {
    cacheUrls(tableUrls);
  }

  // Detectar si el formulario de acortar URLs está presente
  const shortenForm = document.getElementById("shortenUrlForm");
  if (shortenForm) {
  // Sobreescribir el comportamiento del formulario para trabajar offline
  shortenForm.addEventListener("submit", function (event) {
    if (!isOnline) {
      event.preventDefault();

      const urlInput = document.getElementById("myInput");
      if (urlInput && urlInput.value) {
        createUrlOffline(urlInput.value).then((success) => {
          if (success) {
            // Limpiar formulario
            urlInput.value = "";
            document.getElementById("link-preview").innerHTML = "";

            // Mostrar el modal - Asegúrate que esta línea esté presente
            $('#offlineModal').modal('show');
          }
         });
       }
      }
   });
  }

});

/**
 * Muestra un mensaje de éxito
 * @param {string} message - Mensaje de éxito
 */
function showSuccessMessage(message) {
  const linkPreview = document.getElementById("link-preview");
  if (!linkPreview) return;

  const alert = document.createElement("div");
  alert.className = "alert alert-warning mt-3";
  alert.innerHTML = `<i class="fas fa-wifi-slash mr-2"></i>${message}`;

  linkPreview.appendChild(alert);

  // Eliminar después de 5 segundos
  setTimeout(() => {
    alert.remove();
  }, 5000);
}


