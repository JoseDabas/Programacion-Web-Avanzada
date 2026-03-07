package com.pucmm.backend_sensores.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Clase de configuración para habilitar y ajustar WebSockets en Spring Boot.
 * La anotación @EnableWebSocketMessageBroker habilita el servidor broker de mensajes,
 * el cual es usado para crear comunicación bidireccional en tiempo real entre el servidor y clientes web.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registra los endpoints a los que los clientes (frontend) se conectarán inicialmente.
     * En este caso, el cliente establecerá la conexión con la ruta HTTP "/ws-sensores".
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registramos el endpoint /ws-sensores.
        // setAllowedOriginPatterns("*") es la práctica recomendada en versiones recientes
        // de Spring Boot (en lugar de setAllowedOrigins("*")) para evitar errores de CORS
        // y permitir conexiones desde cualquier servidor Frontend.
        // withSockJS() provee un fallback si el navegador no soporta WebSockets puros.
        registry.addEndpoint("/ws-sensores")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Configura el broker de mensajería (Message Broker) local en memoria.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Habilitamos un simple broker que retransmitirá los mensajes enviados 
        // a cualquier destino (canal) que comience con "/topic".
        // Los clientes web se suscribirán a estas rutas, como "/topic/mediciones".
        registry.enableSimpleBroker("/topic");
        
        // Define el prefijo usado cuando el cliente envía mensajes expresamente hacia la aplicación (servidor)
        registry.setApplicationDestinationPrefixes("/app");
    }
}
