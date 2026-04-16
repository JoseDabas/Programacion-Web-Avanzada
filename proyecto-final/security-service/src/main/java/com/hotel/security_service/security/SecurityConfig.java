package com.hotel.security_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Clase de configuracion general de Spring Security
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean para definir el encriptador de contraseñas usando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para configurar la cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitamos proteccion CSRF porque utilizaremos tokens (JWT) para proteger la sesion
                .csrf(AbstractHttpConfigurer::disable)
                // Autorizamos todas las peticiones a todo tipo de endpoints dentro del contexto actual
                // Nota: El filtro de control principal de acceso a rutas se realizara a nivel del API Gateway
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        
        return http.build();
    }
}
