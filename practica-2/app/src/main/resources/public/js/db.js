/**
 * Base de datos IndexedDB para almacenamiento offline
 */

window.DB_FUNCTIONS_ORIGINAL = true;


// Configuración de la base de datos
const DB_NAME = 'shorturl_offline_db';
const DB_VERSION = 1;
const URL_STORE_NAME = 'urls';

// Asegurarse de que todas las funciones están disponibles globalmente
window.saveUrlToIndexedDB = saveUrlToIndexedDB;
window.saveUrlsToIndexedDB = saveUrlsToIndexedDB;
window.getUrlsByUsername = getUrlsByUsername;
window.getAllUrls = getAllUrls;
window.markUrlAsPending = markUrlAsPending;
window.markUrlAsSynced = markUrlAsSynced;
window.getPendingUrls = getPendingUrls;
window.deleteUrl = deleteUrl;
window.clearUrls = clearUrls;

/**
 * Abre la base de datos
 */
function openDatabase() {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);
    
    request.onerror = (event) => {
      console.error('Error al abrir la base de datos:', event.target.error);
      reject(event.target.error);
    };
    
    request.onsuccess = (event) => {
      resolve(event.target.result);
    };
    
    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(URL_STORE_NAME)) {
        const urlStore = db.createObjectStore(URL_STORE_NAME, { keyPath: 'id' });
        urlStore.createIndex('urlNuevo', 'urlNuevo', { unique: true });
        urlStore.createIndex('usuario', 'usuario', { unique: false });
        urlStore.createIndex('syncStatus', 'syncStatus', { unique: false });
      }
    };
  });
}

/**
 * Guarda una URL en IndexedDB
 */
function saveUrlToIndexedDB(url) {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readwrite');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      
      // Asegurarse de que la URL tenga un estado de sincronización
      if (!url.hasOwnProperty('syncStatus')) {
        url.syncStatus = 'synced';
      }
      
      const request = urlStore.put(url);
      
      request.onsuccess = () => resolve();
      
      request.onerror = (event) => {
        console.error('Error al guardar URL:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en saveUrlToIndexedDB:', error);
      reject(error);
    }
  });
}

/**
 * Guarda múltiples URLs en IndexedDB
 */
function saveUrlsToIndexedDB(urls) {
  return new Promise(async (resolve, reject) => {
    if (!Array.isArray(urls)) {
      reject(new Error('El parámetro urls debe ser un array'));
      return;
    }
    
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readwrite');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      
      let completed = 0;
      
      for (const url of urls) {
        // Asegurar que la URL tenga un estado de sincronización
        if (!url.hasOwnProperty('syncStatus')) {
          url.syncStatus = 'synced';
        }
        
        const request = urlStore.put(url);
        
        request.onsuccess = () => {
          completed++;
          if (completed === urls.length) {
            resolve();
          }
        };
        
        request.onerror = (event) => {
          console.error('Error al guardar URL:', event.target.error);
          reject(event.target.error);
        };
      }
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en saveUrlsToIndexedDB:', error);
      reject(error);
    }
  });
}

/**
 * Obtiene URLs por nombre de usuario
 */
function getUrlsByUsername(username) {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readonly');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      const index = urlStore.index('usuario');
      
      const request = index.getAll(username);
      
      request.onsuccess = () => {
        resolve(request.result || []);
      };
      
      request.onerror = (event) => {
        console.error('Error al obtener URLs:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en getUrlsByUsername:', error);
      reject(error);
    }
  });
}

/**
 * Obtiene todas las URLs
 */
function getAllUrls() {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readonly');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      
      const request = urlStore.getAll();
      
      request.onsuccess = () => {
        resolve(request.result || []);
      };
      
      request.onerror = (event) => {
        console.error('Error al obtener URLs:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en getAllUrls:', error);
      reject(error);
    }
  });
}

/**
 * Marca una URL como pendiente de sincronización
 */
function markUrlAsPending(id) {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readwrite');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      
      const getRequest = urlStore.get(id);
      
      getRequest.onsuccess = () => {
        const url = getRequest.result;
        if (url) {
          url.syncStatus = 'pending';
          const updateRequest = urlStore.put(url);
          
          updateRequest.onsuccess = () => resolve();
          
          updateRequest.onerror = (event) => {
            console.error('Error al actualizar URL:', event.target.error);
            reject(event.target.error);
          };
        } else {
          reject(new Error('URL no encontrada'));
        }
      };
      
      getRequest.onerror = (event) => {
        console.error('Error al obtener URL:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en markUrlAsPending:', error);
      reject(error);
    }
  });
}

/**
 * Marca una URL como sincronizada
 */
function markUrlAsSynced(id) {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readwrite');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      
      const getRequest = urlStore.get(id);
      
      getRequest.onsuccess = () => {
        const url = getRequest.result;
        if (url) {
          url.syncStatus = 'synced';
          const updateRequest = urlStore.put(url);
          
          updateRequest.onsuccess = () => resolve();
          
          updateRequest.onerror = (event) => {
            console.error('Error al actualizar URL:', event.target.error);
            reject(event.target.error);
          };
        } else {
          reject(new Error('URL no encontrada'));
        }
      };
      
      getRequest.onerror = (event) => {
        console.error('Error al obtener URL:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en markUrlAsSynced:', error);
      reject(error);
    }
  });
}

/**
 * Obtiene todas las URLs pendientes de sincronización
 */
function getPendingUrls() {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readonly');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      const index = urlStore.index('syncStatus');
      
      const request = index.getAll('pending');
      
      request.onsuccess = () => {
        resolve(request.result || []);
      };
      
      request.onerror = (event) => {
        console.error('Error al obtener URLs pendientes:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en getPendingUrls:', error);
      reject(error);
    }
  });
}

/**
 * Elimina una URL
 */
function deleteUrl(id) {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readwrite');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      
      const request = urlStore.delete(id);
      
      request.onsuccess = () => resolve();
      
      request.onerror = (event) => {
        console.error('Error al eliminar URL:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en deleteUrl:', error);
      reject(error);
    }
  });
}

/**
 * Limpia todas las URLs
 */
function clearUrls() {
  return new Promise(async (resolve, reject) => {
    try {
      const db = await openDatabase();
      const transaction = db.transaction([URL_STORE_NAME], 'readwrite');
      const urlStore = transaction.objectStore(URL_STORE_NAME);
      
      const request = urlStore.clear();
      
      request.onsuccess = () => resolve();
      
      request.onerror = (event) => {
        console.error('Error al limpiar URLs:', event.target.error);
        reject(event.target.error);
      };
      
      transaction.oncomplete = () => {
        db.close();
      };
    } catch (error) {
      console.error('Error en clearUrls:', error);
      reject(error);
    }
  });
}

window.IndexedDBHelper = {
  saveUrlToIndexedDB,
  saveUrlsToIndexedDB,
  getUrlsByUsername,
  getAllUrls,
  markUrlAsPending,
  markUrlAsSynced,
  getPendingUrls,
  deleteUrl,
  clearUrls
};