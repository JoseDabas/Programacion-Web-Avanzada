package practica1.mockup_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class MockEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre y descripción
    private String name;
    private String description;

    // Ruta del endpoint (ej: /api/v1/usuarios)
    @Column(nullable = false)
    private String path;

    // Método (GET, POST, etc.)
    private String method;

    // Headers de respuesta (Guardados como JSON String o texto simple key:value)
    @Column(columnDefinition = "TEXT")
    private String responseHeaders;

    // Código de respuesta
    private int responseStatus;

    // Content-Type (application/json, text/xml)
    private String contentType;

    // Cuerpo del mensaje
    @Column(columnDefinition = "TEXT")
    private String responseBody;

    // Tiempo de Expiración
    private LocalDateTime expirationDate;

    // Demora en segundos
    private int delayInSeconds;

    // Validación JWT
    private boolean jwtProtected;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
