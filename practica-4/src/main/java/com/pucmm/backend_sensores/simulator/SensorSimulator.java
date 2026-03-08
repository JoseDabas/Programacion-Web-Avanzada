package com.pucmm.backend_sensores.simulator;

import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Componente que simula los dispositivos finales (EndPoints)
 * Al implementar CommandLineRunner, Spring Boot ejecutará el método run()
 * automáticamente tan pronto como la aplicación termine de levantar.
 */
@Component
public class SensorSimulator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SensorSimulator.class);

    // Cola destino en ActiveMQ
    private static final String COLA_DESTINO = "notificacion_sensores";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final Random random;

    /**
     * Inyección de dependencias. JmsTemplate ya está auto-configurado por Spring
     * para apuntar al broker local (localhost:61616) definido en
     * application.properties.
     */
    @Autowired
    public SensorSimulator(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.random = new Random();
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 Iniciando el simulador de Sensores...");

        // Creamos un hilo planificador (Scheduler) con un pool de 2 hilos,
        // uno para cada cliente/sensor simulado.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // ==========================================
        // TAREA PROGRAMADA 1: Cliente 1 (Sensor ID: 1)
        // ==========================================
        Runnable tareaCliente1 = () -> enviarTramaSensor(1);

        // ==========================================
        // TAREA PROGRAMADA 2: Cliente 2 (Sensor ID: 2)
        // ==========================================
        Runnable tareaCliente2 = () -> enviarTramaSensor(2);

        // Programar ambas tareas para que se ejecuten cada 1 minuto (60 segundos).
        // initialDelay: 5 segundos (para dar tiempo a que todo el sistema asimile el
        // inicio)
        // period: 60 segundos (cada minuto como lo requieren las instrucciones)
        scheduler.scheduleAtFixedRate(tareaCliente1, 5, 60, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(tareaCliente2, 10, 60, TimeUnit.SECONDS); // Cliente 2 inicia a los 10 segundos
    }

    /**
     * Método central que genera la data aleatoria y envía el JSON estricto al
     * broker.
     * 
     * @param idDispositivo Identificador único del sensor que está "enviando" la
     *                      data.
     */
    private void enviarTramaSensor(int idDispositivo) {
        try {
            // 1. Generar valores aleatorios
            // Temperatura entre 15.0 y 40.0 grados
            double temperaturaAleatoria = 15.0 + (25.0 * random.nextDouble());
            // Humedad entre 30.0% y 90.0%
            double humedadAleatoria = 30.0 + (60.0 * random.nextDouble());

            // 2. Formatear la fecha estrictamente a DD/MM/YYYY HH:mm:ss
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String fechaGeneracion = sdf.format(new Date());

            // 3. Construir la estructura estricta solicitada usando un Map
            Map<String, Object> trama = new HashMap<>();
            trama.put("fechaGeneración", fechaGeneracion);
            trama.put("IdDispositivo", idDispositivo);

            // Truncamos los decimales a 2 posiciones por estética
            trama.put("temperatura", Math.round(temperaturaAleatoria * 100.0) / 100.0);
            trama.put("humedad", Math.round(humedadAleatoria * 100.0) / 100.0);

            // 4. Convertir el Map a un String JSON estricto usando Jackson ObjectMapper
            String jsonOutput = objectMapper.writeValueAsString(trama);

            logger.info("📡 [CLIENTE {}] Enviando trama JSON a '{}': {}", idDispositivo, COLA_DESTINO, jsonOutput);

            // 5. Enviar el JSON string puro al ActiveMQ usando convertAndSend.
            // Como vimos en la Fase 1, el conversor de texto y nuestro método @JmsListener
            // lo atraparán felizmente.
            jmsTemplate.convertAndSend(COLA_DESTINO, jsonOutput);

        } catch (Exception e) {
            logger.error("❌ Error en Cliente {} simulado al intentar enviar JSON al broker: {}", idDispositivo,
                    e.getMessage(), e);
        }
    }
}
