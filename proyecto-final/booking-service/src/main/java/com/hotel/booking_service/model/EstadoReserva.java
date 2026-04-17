package com.hotel.booking_service.model;

// Listado de estados restrictivos en los que se puede encontrar un pago/reserva
public enum EstadoReserva {
    PENDIENTE,      // Reserva recien creada pero sin pago recibido
    COMPLETADO,     // Cobro realizado o simulado por PayPal y pagado exitosamente
    CANCELADO       // Reserva anulada por tiempo excedido o decision manual
}
