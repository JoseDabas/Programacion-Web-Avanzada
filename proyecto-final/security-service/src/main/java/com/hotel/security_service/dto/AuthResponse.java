package com.hotel.security_service.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String name;
    private String lastName;

    public AuthResponse(String token, String email, String role, String name, String lastName) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.name = name;
        this.lastName = lastName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
