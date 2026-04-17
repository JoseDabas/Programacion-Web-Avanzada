package com.hotel.booking_service.controller;

import com.hotel.booking_service.model.Reserva;
import com.hotel.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    // ENDPOINT 1: Generar link de aprobacion PayPal
    @PostMapping("/{id}/payment/create")
    public ResponseEntity<?> createPayment(
            @PathVariable Long id
    ) {
        try {
            String approvalLink = bookingService.createPaypalPaymentOrder(id);
            // Retornamos un JSON para que el front lo interprete y haga target="_blank" o href
            return ResponseEntity.ok(Map.of("approval_link", approvalLink));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ENDPOINT 2: Confirmar congelamiento de dinero tras retornar logueado de paypal
    @PostMapping("/{id}/payment/capture")
    public ResponseEntity<?> capturePayment(
            @PathVariable Long id,
            @RequestParam String paypalOrderId
    ) {
        try {
            Reserva reservaConfirmada = bookingService.capturePaypalPayment(id, paypalOrderId);
            return ResponseEntity.ok(reservaConfirmada);
        } catch (RuntimeException ex) {
            // Manejamos gracefully el string de error lanzado por el Servicio si no esta pendiente.
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
