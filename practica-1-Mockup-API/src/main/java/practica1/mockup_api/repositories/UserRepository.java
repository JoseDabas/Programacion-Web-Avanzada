package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
