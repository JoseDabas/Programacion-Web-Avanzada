package practica1.mockup_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.repositories.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verificar si existe el admin, si no, crearlo
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin")) // Contraseña "admin"
                        .role("ROLE_ADMIN")
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println("Usuario admin creado");
            }
        };
    }

}
