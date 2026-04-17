package com.hotel.booking_service.service;

import com.hotel.booking_service.model.EstadoReserva;
import com.hotel.booking_service.model.Reserva;
import com.hotel.booking_service.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Servicio donde yace la logica profunda de las reservas 
@Service
public class BookingService {

    private final ReservaRepository reservaRepository;

    public BookingService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
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

    // Simula una comunicacion con un ente tercero financiero (PayPal/Stripe)
    public Reserva simulatePaypalPayment(Long reservaId) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(reservaId);
        
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
            // Solamente se puede proceder al cobro si esta pendiente
            if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
                // Aqui iria el POST a un webhook de PayPal. Simulamos exito:
                System.out.println("Cobrando a traves de PayPal un total de $" + reserva.getTotalPagar() + " usd...");
                reserva.setEstado(EstadoReserva.COMPLETADO);
                return reservaRepository.save(reserva);
            } else {
                throw new RuntimeException("La reserva no se encuentra en estado Pendiente para cobro.");
            }
        }
        throw new RuntimeException("El ID proporcionado de Reserva no existe");
    }
}
