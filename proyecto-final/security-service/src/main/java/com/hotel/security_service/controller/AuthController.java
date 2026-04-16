package com.hotel.security_service.controller;

import com.hotel.security_service.dto.AuthRequest;
import com.hotel.security_service.dto.AuthResponse;
import com.hotel.security_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controlador de servicios REST para exponer rutas de seguridad y autenticacion
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // Inyectamos el servicio de logica del negocio
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint POST (ruta de login)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            // Evaluamos la solicitud usando el servicio
            AuthResponse response = authService.authenticate(request);
            // Retornamos OK (200) con el token generado
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            // Si la logica lanza un error de credenciales invalidas, arrojamos un Error 401 Unauthorized
            return ResponseEntity.status(401).build();
        }
    }
}
