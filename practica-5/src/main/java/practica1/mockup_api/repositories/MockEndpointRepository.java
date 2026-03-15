package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.MockEndpoint;
import practica1.mockup_api.entities.Project;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de endpoints simulados (Mocks).
 */
public interface MockEndpointRepository extends JpaRepository<MockEndpoint, Long> {

    /**
     * Encuentra todos los endpoints asociados a un proyecto.
     *
     * @param project El proyecto al que pertenecen.
     * @return Lista de endpoints simulados.
     */
    List<MockEndpoint> findByProjectId(Long projectId);

    /**
     * Busca un endpoint específico por su ruta (path) y método HTTP.
     *
     * @param path   La ruta del endpoint (ej. "/api/v1/users").
     * @param method El método HTTP (ej. "GET").
     * @return Un Optional con el endpoint si se encuentra.
     */
    Optional<MockEndpoint> findByMethodAndPath(String method, String path);
}
