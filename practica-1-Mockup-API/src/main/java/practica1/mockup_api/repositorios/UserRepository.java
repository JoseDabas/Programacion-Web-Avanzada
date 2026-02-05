package practica1.mockup_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entidades.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
