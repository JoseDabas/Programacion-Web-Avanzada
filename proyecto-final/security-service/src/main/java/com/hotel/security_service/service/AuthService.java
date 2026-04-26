package com.hotel.security_service.service;

import com.hotel.security_service.dto.AuthRequest;
import com.hotel.security_service.dto.AuthResponse;
import com.hotel.security_service.model.User;
import com.hotel.security_service.repository.UserRepository;
import com.hotel.security_service.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Servicio central que concentra la logica de negocio para autenticacion
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Inyeccion de dependencias via constructor (Recomendado en Spring)
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // Funcion encargada de validar las credenciales y generar el token
    public AuthResponse authenticate(AuthRequest request) {
        // Limpiamos el email de espacios en blanco y lo pasamos a minusculas
        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        
        // Buscamos al usuario en base al correo electronico proveido
        Optional<User> optionalUser = userRepository.findByEmail(cleanEmail);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Verificamos si la contraseña plana recibida es igual al Hash que tenemos en la base de datos
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Generamos y retornamos un JSON Web Token usando la clase de utilidad JwtUtil
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
                return new AuthResponse(token, user.getEmail(), user.getRole(), user.getName(), user.getLastName());
            }
        }
        // Retornamos un RuntimeException simple para ejemplificar cuando el login falla
        throw new RuntimeException("Credenciales invalidas");
    }

    // Funcion para registrar un nuevo usuario
    public void register(User user) {
        // Limpiamos el email de espacios en blanco y minusculas
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }

        // Validar si el usuario ya existe para evitar errores de duplicados
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electronico ya esta registrado");
        }

        // Encriptamos la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Extraer el nombre del email si no viene uno (ej: de jose@gmail.com obtenemos jose)
        if (user.getName() == null || user.getName().isEmpty()) {
            if (user.getEmail() != null && user.getEmail().contains("@")) {
                String nameFromEmail = user.getEmail().split("@")[0];
                user.setName(nameFromEmail);
            } else {
                user.setName("Usuario"); // Fallback seguro
            }
        }

        // Por defecto asignamos el rol CLIENT si no viene uno
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CLIENT");
        }
        
        // Por defecto estado Activo
        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus("Activo");
        }
        userRepository.save(user);
    }
}
