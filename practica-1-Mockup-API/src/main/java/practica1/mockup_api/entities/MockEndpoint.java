package practica1.mockup_api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entidad que representa un endpoint simulado (Mock).
 * Contiene la configuración de cómo debe responder la API ante una petición
 * específica.
 */
@Entity
@Table(name = "mock_endpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockEndpoint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Ruta relativa del endpoint (ej. "/api/v1/usuarios").
     */
    @Column(nullable = false)
    private String path;

    /**
     * Método HTTP para el cual responde este mock (GET, POST, etc.).
     */
    @Column(nullable = false)
    private String method;

    /**
     * Cabeceras HTTP personalizadas que se devolverán en la respuesta.
     */
    @ElementCollection
    @CollectionTable(name = "mock_headers", joinColumns = @JoinColumn(name = "mock_id"))
    @MapKeyColumn(name = "header_key")
    @Column(name = "header_value")
    private Map<String, String> headers = new HashMap<>();

    /**
     * Código de estado HTTP de la respuesta (ej. 200, 404, 500).
     */
    private int responseCode;

    /**
     * Tipo de contenido de la respuesta (ej. "application/json").
     */
    private String contentType;

    /**
     * Cuerpo de la respuesta simulada.
     */
    @Lob
    private String responseBody;

    // Lógica de Expiración
    /**
     * Fecha en la que el endpoint dejará de estar disponible.
     * Por defecto se establece a 1 año desde la creación si no se especifica.
     */
    private LocalDateTime expirationDate;

    // Lógica de Simulación
    /**
     * Tiempo de espera artificial en segundos antes de responder (para simular
     * latencia).
     * 0 indica sin retardo.
     */
    private int responseDelaySeconds;

    // Lógica de Seguridad
    /**
     * Indica si este endpoint específico requiere validación de token JWT para ser
     * accedido.
     */
    private boolean jwtEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
