package practica1.mockup_api.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @Column(nullable = false)
    private String path; // e.g., "/api/v1/users"

    @Column(nullable = false)
    private String method; // GET, POST, PUT, PATCH, DELETE, OPTIONS

    @ElementCollection
    @CollectionTable(name = "mock_headers", joinColumns = @JoinColumn(name = "mock_id"))
    @MapKeyColumn(name = "header_key")
    @Column(name = "header_value")
    private Map<String, String> headers = new HashMap<>();

    private int responseCode;

    private String contentType;

    @Lob
    private String responseBody;

    // Expiration Logic
    private LocalDateTime expirationDate; // Default 1 year

    // Simulation Logic
    private int responseDelaySeconds; // 0 for none

    // Security Logic
    private boolean jwtEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
