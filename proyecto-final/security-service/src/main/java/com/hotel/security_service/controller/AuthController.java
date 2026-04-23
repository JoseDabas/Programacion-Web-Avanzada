package com.hotel.security_service.controller;

import com.hotel.security_service.dto.AuthRequest;
import com.hotel.security_service.dto.AuthResponse;
import com.hotel.security_service.model.User;
import com.hotel.security_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controlador de servicios REST para exponer rutas de seguridad y autenticacion
@RestController
@RequestMapping("/api/auth")
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

    // Endpoint para registrar nuevos usuarios
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            authService.register(user);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }
}
