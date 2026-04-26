package com.hotel.notification_service.model;

/**
 * Modelo para la solicitud de generacion de factura y correo de reserva.
 */
public record BookingInvoiceRequest(
    String clienteNombre,
    String clienteEmail,
    String propiedadNombre,
    String fechaEntrada,
    String fechaSalida,
    Double costoTotal,
    Double impuestos
) {}
