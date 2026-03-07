package com.pucmm.backend_sensores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import jakarta.jms.ConnectionFactory;

/**
 * Clase de configuración para habilitar y ajustar Jakarta Messaging (JMS) en Spring Boot.
 * La anotación @EnableJms le dice a Spring que busque componentes que tengan métodos
 * etiquetados con @JmsListener para suscribirlos automáticamente a las colas.
 */
@Configuration
@EnableJms
public class JmsConfig {

    /**
     * Define la fábrica de contenedores que Spring Boot utilizará para inyectar a 
     * todos nuestros métodos @JmsListener.
     * Le conecta de forma explicita la fábrica de conexión (ActiveMQ).
     *
     * @param connectionFactory La fábrica de conexiones inyectada dinámicamente hacia el ActiveMQ local
     * @return instanciación del DefaultJmsListenerContainerFactory configurado
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        
        // Usamos el convertidor simple por defecto (SimpleMessageConverter) el cual 
        // entrega los mensajes TextMessage automáticamente como String al Listener,
        // donde luego procesamos el JSON de forma manual con ObjectMapper.
        
        return factory;
    }
}
