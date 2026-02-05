package practica1.mockup_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.entities.Role;
import practica1.mockup_api.repositories.RoleRepository;
import practica1.mockup_api.repositories.UserRepository;

/**
 * Clase de inicialización de datos.
 * Se ejecuta al iniciar la aplicación para asegurar que existen los datos base
 * necesarios
 * (roles y usuario administrador por defecto).
 */
@Configuration
public class DataInitializer {

    /**
     * Bean CommandLineRunner que ejecuta la lógica de inicialización.
     *
     * @param userRepository  Repositorio de usuarios.
     * @param roleRepository  Repositorio de roles.
     * @param passwordEncoder Encriptador de contraseñas.
     * @return El CommandLineRunner a ejecutar.
     */
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            // 1. Crear rol ADMIN si no existe
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("ROLE_ADMIN");
                return roleRepository.save(newRole);
            });

            // 2. Crear rol USER si no existe
            roleRepository.findByName("ROLE_USER").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("ROLE_USER");
                return roleRepository.save(newRole);
            });

            // 3. Verificar si existe el usuario 'admin', si no, crearlo
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin")) // Contraseña por defecto "admin"
                        .roles(java.util.Collections.singleton(roleAdmin))
                        .enabled(true)
                        .build();
                userRepository.save(admin);
                System.out.println("Usuario admin creado por defecto");
            }
        };
    }

}
