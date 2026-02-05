package practica1.mockup_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entidades.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
