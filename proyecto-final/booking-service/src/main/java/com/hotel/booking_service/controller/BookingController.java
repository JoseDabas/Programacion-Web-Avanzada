package com.hotel.booking_service.controller;

import com.hotel.booking_service.model.Reserva;
import com.hotel.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Interfaz para la red (REST Controller)
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Endpoint POST (Crear nueva reserva a partir del Body)
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@RequestBody Reserva reserva) {
        Reserva nuevaReserva = bookingService.createReserva(reserva);
        return ResponseEntity.ok(nuevaReserva);
    }

    // Endpoint GET (Historial individual cliente) 
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Reserva>> historialCliente(@PathVariable String clienteId) {
        return ResponseEntity.ok(bookingService.getHistorial(clienteId));
    }

    // Endpoint POST de Accion Especifica (Cobro interactivo)
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payReservation(@PathVariable Long id) {
        try {
            Reserva reservaCobrable = bookingService.simulatePaypalPayment(id);
            return ResponseEntity.ok(reservaCobrable);
        } catch (RuntimeException ex) {
            // Manejamos gracefully el string de error lanzado por el Servicio si no esta pendiente.
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
