package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.Role;

import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de la entidad Role.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * @param name El nombre del rol (ej. "ROLE_USER").
     * @return Un Optional con el rol si se encuentra.
     */
    Optional<Role> findByName(String name);
}
