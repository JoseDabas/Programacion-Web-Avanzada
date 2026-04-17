package com.hotel.booking_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// Entidad JPA estructurada para almacenarse en PostgreSQL
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID unico generado por bd

    @Column(nullable = false)
    private String clienteId; // Guardamos quien hizo la reservacion (Por ejemplo token ID)

    @Column(nullable = false)
    private String propiedadId; // Id relacional logico apuntando a un registro MongoDB del catalog-service

    @Column(nullable = false)
    private LocalDate fechaInicio; // Fecha CheckIn

    @Column(nullable = false)
    private LocalDate fechaFin; // Fecha CheckOut

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado; // PENDIENTE, COMPLETADO, CANCELADO

    @Column(nullable = false)
    private Double totalPagar; // Monto facturado

    public Reserva() {
    }

    public Reserva(String clienteId, String propiedadId, LocalDate fechaInicio, LocalDate fechaFin, EstadoReserva estado, Double totalPagar) {
        this.clienteId = clienteId;
        this.propiedadId = propiedadId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.totalPagar = totalPagar;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getPropiedadId() { return propiedadId; }
    public void setPropiedadId(String propiedadId) { this.propiedadId = propiedadId; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public Double getTotalPagar() { return totalPagar; }
    public void setTotalPagar(Double totalPagar) { this.totalPagar = totalPagar; }
}
