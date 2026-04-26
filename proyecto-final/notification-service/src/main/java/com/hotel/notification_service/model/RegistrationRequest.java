package com.hotel.notification_service.model;

/**
 * Modelo para la solicitud de correo de confirmacion de registro.
 */
public record RegistrationRequest(
    String email, 
    String nombre
) {}
