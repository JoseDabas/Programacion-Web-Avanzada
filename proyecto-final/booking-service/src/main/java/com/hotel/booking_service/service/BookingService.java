package com.hotel.booking_service.service;

import com.hotel.booking_service.client.PaypalClient;
import com.hotel.booking_service.model.EstadoReserva;
import com.hotel.booking_service.model.Reserva;
import com.hotel.booking_service.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Servicio donde yace la logica profunda de las reservas 
@Service
@SuppressWarnings("unchecked")
public class BookingService {

    private final ReservaRepository reservaRepository;
    private final PaypalClient paypalClient;

    public BookingService(ReservaRepository reservaRepository, PaypalClient paypalClient) {
        this.reservaRepository = reservaRepository;
        this.paypalClient = paypalClient;
    }

    // Metodo llamado al iniciar un proceso de compra/reserva
    public Reserva createReserva(Reserva reserva) {
        // Regla estricta: Toda reserva empieza obligatoriamente como PENDIENTE
        reserva.setEstado(EstadoReserva.PENDIENTE);
        return reservaRepository.save(reserva);
    }

    // Obtener todo el subconjunto de reservas hechas por un usuario especifico
    public List<Reserva> getHistorial(String clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    // Paso 1 de Flujo Autorizado: Crea la orden en Paypal y devuelve el Approval
    // Link
    public String createPaypalPaymentOrder(Long reservaId) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(reservaId);

        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
            // Solamente se puede proceder al cobro si esta pendiente
            if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
                // Generamos la orden en PayPal usando nuestro cliente REST
                Map<String, Object> response = paypalClient.createOrder(reserva.getTotalPagar());

                // Extraemos el link "approve" de los HATEOAS links retornados por paypal
                List<Map<String, String>> links = (List<Map<String, String>>) response.get("links");
                for (Map<String, String> link : links) {
                    if ("approve".equals(link.get("rel"))) {
                        // Devolvemos la URL secreta para redireccionar el usuario al formulario de pago
                        return link.get("href");
                    }
                }
                throw new RuntimeException("No se encontro un link de aprobacion valido desde PayPal.");
            } else {
                throw new RuntimeException("La reserva no se encuentra en estado Pendiente para cobro.");
            }
        }
        throw new RuntimeException("El ID proporcionado de Reserva no existe");
    }

    // Paso 2: El usuario aprobo en paypal, hacemos capture de los fondos y cerramos
    // la transaccion.
    public Reserva capturePaypalPayment(Long reservaId, String paypalOrderId) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(reservaId);
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();

            // Confirmamos a traves del endpoint oficial
            Map<String, Object> captureResponse = paypalClient.captureOrder(paypalOrderId);
            String status = captureResponse.get("status").toString();

            if ("COMPLETED".equals(status)) {
                reserva.setEstado(EstadoReserva.COMPLETADO);
                return reservaRepository.save(reserva);
            } else {
                throw new RuntimeException("No se pudo capturar exitosamente el pago en PayPal. Status: " + status);
            }
        }
        throw new RuntimeException("El ID proporcionado de Reserva no existe");
    }
}
