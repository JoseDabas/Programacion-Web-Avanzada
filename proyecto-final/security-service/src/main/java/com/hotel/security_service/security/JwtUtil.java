package com.hotel.security_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// Componente utilitario para manejar la validacion y generacion de tokens JWT
@Component
public class JwtUtil {

    // Clave secreta fuerte (mínimo 256 bits) para la firma de tokens JWT
    private final String secret = "123456789012345678901234567890_HotelTokenSecret_12345678901234567890";

    // Tiempo de expiracion del token, configurado a 24 horas en milisegundos
    private final long expiration = 86400000;

    // Metodo que genera la llave criptografica
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Metodo para generar un token pasandole el email y el rol
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email) // El sujeto del token es el correo del usuario
                .claim("role", role) // Guardamos el rol en el token para evitar consultas extras en el gateway
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de creacion
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Fecha en la que vence el token
                .signWith(getSigningKey()) // Se firma con el algoritmo HMAC usando nuestra llave secreta
                .compact();
    }
}
