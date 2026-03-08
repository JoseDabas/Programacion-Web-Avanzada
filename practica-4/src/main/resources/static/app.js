// ==========================================
// Variables Globales
// ==========================================
let stompClient = null;

// Contextos de los Canvas
const ctxTemp = document.getElementById('temperaturaChart').getContext('2d');
const ctxHum = document.getElementById('humedadChart').getContext('2d');

// Paleta de colores atractiva para los distintos sensores (evita colores genéricos)
const paletaColores = [
    '#ff4081', // Rosa brillante (Id: 1)
    '#00e5ff', // Cyan neón (Id: 2)
    '#ffd600', // Amarillo
    '#b2ff59', // Verde lima
    '#e040fb', // Púrpura
    '#ff6e40'  // Naranja
];

// Configuración general compartida para las gráficas
const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    color: '#ffffff', // Texto de la gráfica en blanco
    scales: {
        x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: '#333333' }
        },
        y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: '#333333' }
        }
    },
    plugins: {
        legend: {
            labels: { color: '#ffffff', font: { size: 14 } }
        }
    },
    animation: {
        duration: 400, // micro-animación fluida al cambiar un valor
        easing: 'easeOutQuart'
    }
};

// ==========================================
// Inicialización de Gráficos (Chart.js)
// ==========================================

// Gráfico Independiente 1: Temperatura
const chartTemperatura = new Chart(ctxTemp, {
    type: 'line',
    data: {
        labels: [], // Aquí se guardará el eje X: la "fechaGeneración" (Tiempo)
        datasets: [] // Las líneas se añadirán dinámicamente: una por cada IdDispositivo
    },
    options: {
        ...chartOptions,
        plugins: {
            title: { display: true, text: 'Temperatura (°C) vs Tiempo', color: '#00c853', font: { size: 18 } },
            legend: chartOptions.plugins.legend
        }
    }
});

// Gráfico Independiente 2: Humedad
const chartHumedad = new Chart(ctxHum, {
    type: 'line',
    data: {
        labels: [], // Eje X temporal (fechas)
        datasets: [] // Las líneas de humedad por dispositivo
    },
    options: {
        ...chartOptions,
        plugins: {
            title: { display: true, text: 'Humedad (%) vs Tiempo', color: '#00e5ff', font: { size: 18 } },
            legend: chartOptions.plugins.legend
        }
    }
});

// ==========================================
// Logica de Actualización Dinámica
// ==========================================

/**
 * Función que inyecta los datos de un sensor entrante dentro de los charts.
 * @param {Object} sensorData Trama JSON ya parseada
 */
function actualizarGraficos(sensorData) {
    const timeLabel = sensorData.fechaGeneración.split(' ')[1]; // Extracting just HH:mm:ss for cleaner X axis
    const deviceId = sensorData.IdDispositivo;

    // 1. Añadir la etiqueta de tiempo global si no existe (Mantiene los ejes X sincronizados en ambos ejes numéricos)
    if (!chartTemperatura.data.labels.includes(timeLabel)) {
        chartTemperatura.data.labels.push(timeLabel);
        chartHumedad.data.labels.push(timeLabel);
        
        // Mantener las gráficas limpias: Borrar los más antiguos si superan los 20 registros
        if (chartTemperatura.data.labels.length > 20) {
            chartTemperatura.data.labels.shift();
            chartHumedad.data.labels.shift();

            chartTemperatura.data.datasets.forEach(d => d.data.shift());
            chartHumedad.data.datasets.forEach(d => d.data.shift());
        }
    }

    // 2. Buscar o crear el Dataset del Sensor en el gráfico de Temperatura
    let datasetTemp = chartTemperatura.data.datasets.find(ds => ds.label === `Sensor ${deviceId}`);
    
    // Si no existe, lo inicializamos asignándole un color
    if (!datasetTemp) {
        let color = paletaColores[(deviceId - 1) % paletaColores.length];
        
        datasetTemp = {
            label: `Sensor ${deviceId}`,
            data: new Array(chartTemperatura.data.labels.length - 1).fill(null), // rellenar vacíos previos
            borderColor: color,
            backgroundColor: color + '33', // Versión transparente para el área rellenada
            borderWidth: 3,
            fill: true,
            tension: 0.4 // Para que la curva sea suave y moderna
        };
        chartTemperatura.data.datasets.push(datasetTemp);
    }
    
    // Añadimos el dato nuevo de temperatura (o actualizamos si ya había push en este tick temporal)
    datasetTemp.data.push(sensorData.temperatura);

    // 3. Buscar o crear el Dataset del Sensor en el gráfico de Humedad
    let datasetHum = chartHumedad.data.datasets.find(ds => ds.label === `Sensor ${deviceId}`);
    
    if (!datasetHum) {
        let color = paletaColores[(deviceId - 1) % paletaColores.length];
        datasetHum = {
            label: `Sensor ${deviceId}`,
            data: new Array(chartHumedad.data.labels.length - 1).fill(null),
            borderColor: color,
            backgroundColor: color + '33',
            borderWidth: 3,
            fill: true,
            tension: 0.4
        };
        chartHumedad.data.datasets.push(datasetHum);
    }
    
    // Añadimos el dato nuevo de humedad
    datasetHum.data.push(sensorData.humedad);

    // 4. Mandamos a renderizar los cambios a la pantalla inmediatamente
    chartTemperatura.update();
    chartHumedad.update();
}

// ==========================================
// Logica de Conexión WebSockets (STOMP)
// ==========================================

/**
 * Función para iniciar y establecer conexión mediante WebSockets con Spring Boot.
 */
function connect() {
    // Apuntamos al endpoint que definimos en WebSocketConfig con SockJS Fallback
    const socket = new SockJS('/ws-sensores');
    stompClient = Stomp.over(socket);

    // Oculta los debug logs constantes del Stomp en la consola del navegador para mayor limpieza
    stompClient.debug = null; 

    // Intentamos establecer conexión
    stompClient.connect({}, function (frame) {
        
        // Update Status visual feedback
        document.getElementById('connection-status').classList.add('connected');
        document.getElementById('connection-text').innerText = 'Conectado. Esperando telemetría...';
        console.log('🔗 Conectado vía STOMP: ' + frame);

        // Suscripción al tópico público de retransmisión
        stompClient.subscribe('/topic/mediciones', function (messageOutput) {
            
            // Pasamos de String JSON de ActiveMQ/Spring -> Objeto JS
            const sensorData = JSON.parse(messageOutput.body);
            
            console.log('📡 Nuevo reporte MQTT recibido:', sensorData);
            
            // Cambiar texto de estado
            document.getElementById('connection-text').innerText = `Recibiendo telemetría en vivo... (Último reporte: Sensor ${sensorData.IdDispositivo})`;
            
            // Llamar lógica de dibujado
            actualizarGraficos(sensorData);
        });

    }, function(error) {
        // En caso de que se apague el servidor Java, mostramos interfaz desconectada
        console.error("❌ Error de comunicación STOMP. Reconectando en 5 segundos...", error);
        document.getElementById('connection-status').classList.remove('connected');
        document.getElementById('connection-text').innerText = 'Servidor Caído. Reconectando...';
        
        // Auto-reconexión robusta
        setTimeout(connect, 5000);
    });
}

// Iniciar conexión inmediatamente al abrir la página Html
connect();
