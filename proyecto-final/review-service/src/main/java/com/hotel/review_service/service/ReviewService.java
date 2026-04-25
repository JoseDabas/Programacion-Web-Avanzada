package com.hotel.review_service.service;

import com.hotel.review_service.model.Review;
import com.hotel.review_service.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(Review review) {

        if (review.getCalificacion() == null || review.getCalificacion() < 1 || review.getCalificacion() > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5.");
        }

        if (reviewRepository.existsByReservaId(review.getReservaId())) {
            throw new RuntimeException("Ya existe una reseña para esta reserva.");
        }

        review.setFechaCreacion(LocalDateTime.now());

        return reviewRepository.save(review);
    }
    public List<Review> getReviewsByProperty(String propiedadId) {
        return reviewRepository.findByPropiedadIdOrderByFechaCreacionDesc(propiedadId);
    }

    public Map<String, Object> getAverageRating(String propiedadId) {
        List<Review> reviews = reviewRepository.findByPropiedadIdOrderByFechaCreacionDesc(propiedadId);

        Map<String, Object> result = new HashMap<>();

        if (reviews.isEmpty()) {
            result.put("promedio", 0.0);
            result.put("total", 0);
        } else {
            double promedio = reviews.stream()
                    .mapToInt(Review::getCalificacion)
                    .average()
                    .orElse(0.0);
            result.put("promedio", Math.round(promedio * 10.0) / 10.0);
            result.put("total", reviews.size());
        }

        return result;
    }

    // Eliminar reseña por ID
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("La reseña no fue encontrada.");
        }
        reviewRepository.deleteById(id);
    }
}
