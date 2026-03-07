package practica1.mockup_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.entities.Role;
import practica1.mockup_api.repositories.RoleRepository;
import practica1.mockup_api.repositories.UserRepository;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            // Crear rol ADMIN si no existe
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("ROLE_ADMIN");
                return roleRepository.save(newRole);
            });

            // Crear rol USER si no existe
            roleRepository.findByName("ROLE_USER").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName("ROLE_USER");
                return roleRepository.save(newRole);
            });

            // Verificar si existe el usuario 'admin', si no, crearlo
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(Set.of(roleAdmin))
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println("Usuario ADMIN creado");
            }
        };
    }
}