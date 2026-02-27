package practica1.mockup_api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practica1.mockup_api.payload.JwtResponse;
import practica1.mockup_api.payload.LoginRequest;
import practica1.mockup_api.security.jwt.JwtService;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Controlador de Autenticación.
 * Maneja las peticiones relacionadas con el inicio de sesión y la generación de
 * tokens JWT.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Endpoint para iniciar sesión.
     * Autentica al usuario con username/password y devuelve un token JWT.
     *
     * @param loginRequest Objeto con las credenciales (username y password).
     * @return ResponseEntity con la respuesta JWT incluyendo el token y datos del
     *         usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // Autenticar usando el AuthenticationManager de Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        // Establecer la autenticación en el contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generar el token JWT
        String jwt = jwtService.generateToken(userDetails);

        // Recuperar el usuario completo de la base de datos (para obtener ID, roles,
        // etc.)
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Construir y devolver la respuesta
        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .id(user.getId())
                .username(user.getUsername())
                .roles(userDetails.getAuthorities().stream().map(Object::toString).toList())
                .type("Bearer")
                .build());
    }
}
