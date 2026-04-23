package com.hotel.security_service.config;

import com.hotel.security_service.model.User;
import com.hotel.security_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@hotel.com".toLowerCase().trim();
        Optional<User> adminOptional = userRepository.findByEmail(adminEmail);

        if (adminOptional.isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setName("Admin");
            userRepository.save(admin);
            System.out.println("Usuario Admin por defecto creado: " + adminEmail);
        } else {
            User admin = adminOptional.get();
            if (!"Admin".equals(admin.getName())) {
                admin.setName("Admin");
                userRepository.save(admin);
                System.out.println("Nombre del Admin actualizado a 'Admin'");
            }
        }
    }
}
