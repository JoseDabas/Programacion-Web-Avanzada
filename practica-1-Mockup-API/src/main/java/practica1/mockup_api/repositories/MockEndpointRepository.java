package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.MockEndpoint;
import java.util.Optional;

public interface MockEndpointRepository extends JpaRepository<MockEndpoint, Long> {

    Optional<MockEndpoint> findByMethodAndPath(String method, String path);
}
