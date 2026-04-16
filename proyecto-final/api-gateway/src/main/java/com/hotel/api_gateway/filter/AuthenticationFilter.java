package com.hotel.api_gateway.filter;

import com.hotel.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// Filtro Global reactivo que interceptara cualquier peticion entrante al API Gateway
@Component
public class AuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    // Rutas protegidas que requieran autenticacion. Excluimos /auth/ para porder
    // loguearnos/registrarnos.
    private final String[] openApiEndpoints = new String[] {
            "/auth/login",
            "/auth/register"
    };

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Si la ruta solicitada NO es /auth/login y similares, requiere validacion
        if (isSecured(path)) {
            // Verificamos si existe un Header de Autorizacion en la solicitud
            org.springframework.http.HttpHeaders headers = exchange.getRequest().getHeaders();
            java.util.List<String> authHeaderList = headers.get(HttpHeaders.AUTHORIZATION);
            if (authHeaderList == null || authHeaderList.isEmpty()) {
                return onError(exchange, "Falta cabecera de autorizacion", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = authHeaderList.get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7); // Extraemos la cadena principal
            } else {
                return onError(exchange, "Estructura de token Invalida", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Intentamos parsear y decodificar este token
                jwtUtil.validateToken(authHeader);
            } catch (Exception e) {
                // Si la validacion falla (Vencido, modificado, etc.), bloqueamos la solicitud
                // c/ Error 401
                System.out.println("Token denegado: " + e.getMessage());
                return onError(exchange, "Token no valido", HttpStatus.UNAUTHORIZED);
            }
        }

        // Si el token es correcto o la ruta es publica, el pipeline de solicitudes
        // continua
        return chain.filter(exchange);
    }

    // Metodo helper para arrojar error 401 si no hay token valido en rutas
    // protegidas
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    // Verifica si la ruta solicitada se incluyo en el arreglo de rutas de pase
    // libre
    private boolean isSecured(String path) {
        for (String endpoint : openApiEndpoints) {
            if (path.contains(endpoint)) {
                return false;
            }
        }
        return true; // Si no es libre, es protegida
    }
}
