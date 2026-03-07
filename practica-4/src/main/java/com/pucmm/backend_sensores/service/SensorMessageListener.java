package com.pucmm.backend_sensores.service;

import tools.jackson.databind.ObjectMapper;
import com.pucmm.backend_sensores.entity.SensorData;
import com.pucmm.backend_sensores.repository.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la mensajería, escucha permanentemente la cola de ActiveMQ 
 * y reacciona cuando un sensor envía información.
 * 
 * @Service registra esta clase como un componente / servicio Singleton de Spring.
 */
@Service
public class SensorMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SensorMessageListener.class);

    private final SensorDataRepository sensorDataRepository;
    private final ObjectMapper objectMapper;
    // Template para enviar mensajes al canal de WebSockets (broker STOMP)
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor con inyección de dependencias a través de @Autowired.
     * Integrando SimpMessagingTemplate al constructor para poder retransmitirlo.
     * 
     * @param sensorDataRepository Repositorio que ejecuta el grabado en base de datos.
     * @param objectMapper Usado para mapear JSON robustamente a objetos de entidad.
     * @param messagingTemplate Permite rebotar el mensaje internamente hacia clientes WebSockets suscritos.
     */
    @Autowired
    public SensorMessageListener(SensorDataRepository sensorDataRepository, ObjectMapper objectMapper, SimpMessagingTemplate messagingTemplate) {
        this.sensorDataRepository = sensorDataRepository;
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * El método recibirNotificacionSensor está suscrito (gracias a @JmsListener) 
     * directamente a la cola nombrada "notificacion_sensores" en el broker (ActiveMQ).
     * 
     * @param mensajeJson La cadena JSON recibida mediante la trama.
     */
    @JmsListener(destination = "notificacion_sensores")
    public void recibirYGuardarTrama(String mensajeJson) {
        try {
            logger.info("-> Mensaje JMS recibido en la cola [notificacion_sensores]: {}", mensajeJson);
            
            // 1. Mapear de JSON plano -> Entidad de Java (SensorData)
            SensorData sensorData = objectMapper.readValue(mensajeJson, SensorData.class);
            
            logger.info("   Dispositivo: {}, Temperatura: {}, Humedad: {}", 
                    sensorData.getIdDispositivo(), sensorData.getTemperatura(), sensorData.getHumedad());
            
            // 2. Guardar inmediatamente de forma persistente a través del repositorio JPA
            SensorData guardado = sensorDataRepository.save(sensorData);
            logger.info("✅ Registro guardado en PostgreSQL (ID interno): {}", guardado.getId());
            
            // 3. Retransmitir al frontend (clientes web) usando WebSockets
            // Convertimos la misma entidad o JSON y lo despachamos al tópico STOMP público.
            // Los clientes suscritos a "/topic/mediciones" recibirán este objeto automáticamente en JSON.
            messagingTemplate.convertAndSend("/topic/mediciones", guardado);
            logger.info("📡 Trama retransmitida a WebSockets en [/topic/mediciones]");

        } catch (Exception e) {
            logger.error("❌ Ocurrió un error al procesar y guardar la trama JSON: {}", e.getMessage(), e);
        }
    }
}
