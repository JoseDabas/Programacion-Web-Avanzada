package practica1.mockup_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entidades.MockEndpoint;
import practica1.mockup_api.entidades.Project;

import java.util.List;
import java.util.Optional;

public interface MockEndpointRepository extends JpaRepository<MockEndpoint, Long> {
    List<MockEndpoint> findByProject(Project project);

    Optional<MockEndpoint> findByPathAndMethod(String path, String method);
}
