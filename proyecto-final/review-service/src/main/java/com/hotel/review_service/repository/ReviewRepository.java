package com.hotel.review_service.repository;

import com.hotel.review_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Capa de abstraccion de base de datos para las reseñas
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Obtener todas las reseñas de una propiedad especifica
    List<Review> findByPropiedadIdOrderByFechaCreacionDesc(String propiedadId);

    // Historial de reseñas de un cliente
    List<Review> findByClienteId(String clienteId);

    // Verificar si ya existe una reseña para una reserva (evitar duplicados)
    boolean existsByReservaId(Long reservaId);
}
