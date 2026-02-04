package com.webavanzada.pucmm.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockEndpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotBlank
    private String ruta; // Ej: /api/test

    @NotBlank
    private String metodo; // GET, POST, etc.

    @NotNull
    private Integer codigoRespuesta; // 200, 404, etc.

    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String cuerpoRespuesta;

    // Punto 4c: Headers (Key-Value)
    @ElementCollection
    @CollectionTable(name = "endpoint_headers", joinColumns = @JoinColumn(name = "endpoint_id"))
    @MapKeyColumn(name = "header_key")
    @Column(name = "header_value")
    private Map<String, String> headers;

    // Punto 4h: Expiración
    private LocalDateTime fechaExpiracion;

    // Punto 4i: Delay en segundos
    private Integer delaySegundos = 0;

    // Punto 4j: ¿Requiere validar JWT?
    private boolean requiereJwt = false;

    private String jwtTokenGenerado;

    @ManyToOne
    private Usuario usuario; // Punto 3: Asociado a un usuario
}