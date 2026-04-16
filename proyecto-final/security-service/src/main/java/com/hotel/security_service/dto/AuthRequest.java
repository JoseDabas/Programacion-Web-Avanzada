package com.hotel.security_service.dto;

public class AuthRequest {
    private String email;
    private String password;

    // Getteres y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
