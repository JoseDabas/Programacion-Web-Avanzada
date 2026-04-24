package com.hotel.booking_service.repository;

import com.hotel.booking_service.model.Reserva;
import com.hotel.booking_service.model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Capa de abstraccion de base de datos automatica inyectable por Spring
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Spring Data resuelve magicamente query de tipo SELECT * FROM reservas WHERE cliente_id = ?
    List<Reserva> findByClienteId(String clienteId);

    // Obtener reservas de una propiedad específica que NO estén canceladas
    List<Reserva> findByPropiedadIdAndEstadoNot(String propiedadId, EstadoReserva estado);
}
