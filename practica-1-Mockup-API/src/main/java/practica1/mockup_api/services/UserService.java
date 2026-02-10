package practica1.mockup_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import practica1.mockup_api.entities.Role;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.repositories.RoleRepository;
import practica1.mockup_api.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void createUser(String username, String rawPassword, boolean isAdmin) {
        // Validar si ya existe
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Buscar Roles
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole); // Todos son usuarios base

        // Si marcaron "Es Admin", agregamos el rol ADMIN
        if (isAdmin) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado."));
            roles.add(adminRole);
        }

        // Crear y Guardar
        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword)) // IMPORTANTE: Encriptar
                .roles(roles)
                .active(true)
                .build();

        userRepository.save(newUser);
    }

    // Método para alternar el estado activo/inactivo (banear usuario)
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        // No permitir deshabilitar al admin principal
        if (!user.getUsername().equals("admin")) {
            user.setActive(!user.isActive());
            userRepository.save(user);
        }
    }
}