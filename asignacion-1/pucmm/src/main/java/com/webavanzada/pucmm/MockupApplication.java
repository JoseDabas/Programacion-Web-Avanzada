package com.webavanzada.pucmm;

import com.webavanzada.pucmm.entidades.Usuario;
import com.webavanzada.pucmm.repositorios.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class MockupApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockupApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository, BCryptPasswordEncoder encoder) {
		return args -> {
			// Punto 2: Crear usuario administrador inicial
			if (usuarioRepository.findByUsername("admin").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setUsername("admin");
				// Contraseña: admin - Encriptada con BCrypt
				admin.setPassword(encoder.encode("admin"));
				admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
				usuarioRepository.save(admin);
				System.out.println("----------");
				System.out.println("SISTEMA INICIALIZADO: Usuario 'admin' creado con éxito.");
				System.out.println("----------");
			}
		};
	}
}