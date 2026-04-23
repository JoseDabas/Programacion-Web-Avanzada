package com.hotel.security_service.service;

import com.hotel.security_service.model.User;
import com.hotel.security_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        // Limpiamos el email
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El correo electronico ya esta registrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getName() == null || user.getName().isEmpty()) {
            if (user.getEmail() != null && user.getEmail().contains("@")) {
                user.setName(user.getEmail().split("@")[0]);
            } else {
                user.setName("Usuario");
            }
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CLIENT");
        }

        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus("Activo");
        }

        return userRepository.save(user);
    }

    public User updateUser(String id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            
            // Actualizamos campos permitidos
            existingUser.setName(updatedUser.getName());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setStatus(updatedUser.getStatus());
            
            // Si mandan un nuevo email, verificar duplicado si cambió
            if (updatedUser.getEmail() != null) {
                String newEmail = updatedUser.getEmail().trim().toLowerCase();
                if (!newEmail.equals(existingUser.getEmail())) {
                     if (userRepository.findByEmail(newEmail).isPresent()) {
                         throw new RuntimeException("El correo electronico ya esta en uso");
                     }
                     existingUser.setEmail(newEmail);
                }
            }

            // Si envían contraseña para actualizarla
            if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty() && !updatedUser.getPassword().equals(existingUser.getPassword())) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // Prevenir quitarle el rol admin a la cuenta principal
            if ("admin@hotel.com".equals(existingUser.getEmail()) && !"ADMIN".equals(updatedUser.getRole())) {
                throw new RuntimeException("No se puede revocar el rol de administrador a la cuenta principal");
            }

            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public void deleteUser(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if ("admin@hotel.com".equals(user.getEmail())) {
                throw new RuntimeException("No se puede eliminar la cuenta de administrador principal");
            }
            userRepository.deleteById(id);
        } else {
             throw new RuntimeException("Usuario no encontrado");
        }
    }
}
