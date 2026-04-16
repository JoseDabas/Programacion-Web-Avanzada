package com.hotel.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

// Clase de utilidad que se encarga de desencriptar y verificar la validez de los Tokens JWT 
@Component
public class JwtUtil {

    // Misma clave compartida con security-service (Normalmente se ubicaria en vault o variables de entorno)
    private final String secret = "123456789012345678901234567890_HotelTokenSecret_12345678901234567890";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Metodo principal para validar y extraer los claims del token interceptado
    public Claims validateToken(final String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token) // Si el token es invalido o ya expiro, esto arrojara una excepcion
                .getPayload();
    }
}
