package com.hotel.notification_service.controller;

import com.hotel.notification_service.model.BookingInvoiceRequest;
import com.hotel.notification_service.model.RegistrationRequest;
import com.hotel.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador principal REST para el gateway de notificaciones del negocio y mensajeria transaccional.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint asincrono-simulado para notificar de un registro exitoso.
     */
    @PostMapping("/register")
    public ResponseEntity<String> sendRegistrationMail(@RequestBody RegistrationRequest request) {
        System.out.println("========== LOGGING START ==========");
        System.out.println("Peticion /register recibida en NotificationController");
        System.out.println("Email: " + request.email());
        System.out.println("===================================");
        notificationService.sendRegistrationConfirmation(request);
        return ResponseEntity.ok("Solicitud de envio de registro recibida de manera exitosa");
    }

    /**
     * Endpoint para generar un PDF .jrxml de factura y notificar la reserva formal al usuario.
     */
    @PostMapping("/invoice")
    public ResponseEntity<String> sendBookingInvoiceMail(@RequestBody BookingInvoiceRequest request) {
        System.out.println("========== LOGGING START ==========");
        System.out.println("Peticion /invoice recibida en NotificationController");
        System.out.println("Email de Cliente a enviar: " + request.clienteEmail());
        System.out.println("===================================");
        notificationService.sendBookingInvoice(request);
        return ResponseEntity.ok("Generacion de factura delegada y enviada para correo");
    }
}
