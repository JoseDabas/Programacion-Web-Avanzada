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
        // Buscamos al usuario en base al correo electronico proveido
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Verificamos si la contraseña plana recibida es igual al Hash que tenemos en la base de datos
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Generamos y retornamos un JSON Web Token usando la clase de utilidad JwtUtil
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
                return new AuthResponse(token);
            }
        }
        // Retornamos un RuntimeException simple para ejemplificar cuando el login falla
        throw new RuntimeException("Credenciales invalidas");
    }
}
