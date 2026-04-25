package com.hotel.review_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entidad JPA para almacenar las reseñas y calificaciones de los usuarios
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String propiedadId;

    @Column(nullable = false)
    private String clienteId;

    private String clienteNombre;

    @Column(nullable = false, unique = true)
    private Long reservaId;

    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public Review() {}

    public Review(String propiedadId, String clienteId, String clienteNombre, Long reservaId, Integer calificacion, String comentario) {
        this.propiedadId = propiedadId;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.reservaId = reservaId;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPropiedadId() { return propiedadId; }
    public void setPropiedadId(String propiedadId) { this.propiedadId = propiedadId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }
    public Integer getCalificacion() { return calificacion; }
    public void setCalificacion(Integer calificacion) { this.calificacion = calificacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
