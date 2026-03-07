// Nombre del caché
const CACHE_NAME = 'shorturl-cache-v1';

// Recursos a cachear inicialmente
const INITIAL_CACHED_RESOURCES = [
  '/',
  '/assets/img/Logo.png',
  '/js/offline.js',
  '/js/db.js',
  'https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js',
  'https://cdnjs.cloudflare.com/ajax/libs/bootstrap/4.6.1/js/bootstrap.bundle.min.js',
  'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css',
  'https://cdnjs.cloudflare.com/ajax/libs/bootstrap/4.6.1/css/bootstrap.min.css'
];

// Instalar el Service Worker
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        // Cachear cada recurso individualmente para manejar errores
        return Promise.allSettled(
          INITIAL_CACHED_RESOURCES.map(url => 
            cache.add(url).catch(err => {
              console.warn(`No se pudo cachear ${url}:`, err);
            })
          )
        );
      })
      .then(() => {
        return self.skipWaiting(); // Forzar la activación inmediata
      })
  );
});

// Limpiar caches antiguos durante la activación
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.filter(cacheName => {
          return cacheName !== CACHE_NAME;
        }).map(cacheName => {
          return caches.delete(cacheName);
        })
      );
    }).then(() => {
      return self.clients.claim(); // Tomar control de los clientes inmediatamente
    })
  );
});

// Estrategia de caché: Network First con fallback a Cache
self.addEventListener('fetch', event => {
  // No interceptar solicitudes sensibles o de API
  if (event.request.url.includes('/url/api-list/') || 
      event.request.url.includes('/url/api-acess/') ||
      event.request.url.includes('/user/') ||
      event.request.url.includes('/admin/') ||
      event.request.url.includes('/stats/')) {
    return;
  }

  event.respondWith(
    fetch(event.request)
      .then(response => {
        // Si la respuesta es válida, cachearla y devolverla
        if (response && response.status === 200) {
          const responseClone = response.clone();
          caches.open(CACHE_NAME).then(cache => {
            cache.put(event.request, responseClone);
          });
        }
        return response;
      })
      .catch(() => {
        // Si falla la red, intentar servir desde caché
        return caches.match(event.request)
          .then(cachedResponse => {
            if (cachedResponse) {
              return cachedResponse;
            }
            
            // Si la solicitud es para una página HTML, servir la página offline
            if (event.request.headers.get('accept').includes('text/html')) {
              return caches.match('/offline.html');
            }
            
            return new Response('No hay conexión a Internet', {
              status: 503,
              statusText: 'Service Unavailable'
            });
          });
      })
  );
});

// Evento de sincronización en segundo plano
self.addEventListener('sync', event => {
  if (event.tag === 'sync-urls') {
    event.waitUntil(syncUrls());
  }
});

// Función para sincronizar URLs cuando se recupera la conexión
async function syncUrls() {
  try {
    // Intenta enviar las URLs pendientes al servidor
    const urlsToSync = await getUrlsToSync();
    
    for (const url of urlsToSync) {
      try {
        // Implementar la lógica para sincronizar con el servidor
        await fetch('/url/sync', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(url),
        });
        
        // Marcar como sincronizado en IndexedDB
        await markUrlAsSynced(url.id);
      } catch (error) {
        console.error('Error al sincronizar URL:', error);
      }
    }
  } catch (error) {
    console.error('Error en la sincronización:', error);
  }
}

// Estas funciones utilizarían mensajes para comunicarse con la página
// Las implementaciones reales estarían en db.js
async function getUrlsToSync() {
  return new Promise((resolve) => {
    self.clients.matchAll().then(clients => {
      if (clients.length === 0) {
        resolve([]);
        return;
      }
      
      const client = clients[0];
      const messageChannel = new MessageChannel();
      
      messageChannel.port1.onmessage = event => {
        resolve(event.data);
      };
      
      client.postMessage({type: 'GET_URLS_TO_SYNC'}, [messageChannel.port2]);
    });
  });
}

async function markUrlAsSynced(id) {
  return new Promise((resolve) => {
    self.clients.matchAll().then(clients => {
      if (clients.length === 0) {
        resolve();
        return;
      }
      
      const client = clients[0];
      const messageChannel = new MessageChannel();
      
      messageChannel.port1.onmessage = () => {
        resolve();
      };
      
      client.postMessage(
        {type: 'MARK_URL_SYNCED', id: id},
        [messageChannel.port2]
      );
    });
  });
}