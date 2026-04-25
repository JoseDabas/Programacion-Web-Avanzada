package com.hotel.review_service.controller;

import com.hotel.review_service.model.Review;
import com.hotel.review_service.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Interfaz REST para el servicio de reseñas y calificaciones
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> crearReview(@RequestBody Review review) {
        try {
            Review nuevaReview = reviewService.createReview(review);
            return ResponseEntity.ok(nuevaReview);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // Endpoint GET - Obtener reseñas de una propiedad (publico)
    @GetMapping("/propiedad/{propiedadId}")
    public ResponseEntity<List<Review>> getReviewsByProperty(@PathVariable String propiedadId) {
        return ResponseEntity.ok(reviewService.getReviewsByProperty(propiedadId));
    }

    // Endpoint GET - Obtener promedio y total de calificaciones (publico)
    @GetMapping("/propiedad/{propiedadId}/rating")
    public ResponseEntity<Map<String, Object>> getRating(@PathVariable String propiedadId) {
        return ResponseEntity.ok(reviewService.getAverageRating(propiedadId));
    }

    // Endpoint DELETE - Eliminar reseña (requiere JWT)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
