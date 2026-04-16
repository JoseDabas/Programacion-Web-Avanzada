package com.hotel.security_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// Entidad que representa a un usuario en la base de datos MongoDB
@Document(collection = "users")
public class User {
    @Id
    private String id; // Identificador unico del usuario
    private String email; // Correo electronico que servira como nombre de usuario para el login
    private String password; // Contraseña encriptada
    private String role; // Rol del usuario: "ADMIN" o "CLIENT"

    public User() {}

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
