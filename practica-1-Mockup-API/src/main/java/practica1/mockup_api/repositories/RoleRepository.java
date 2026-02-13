package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
