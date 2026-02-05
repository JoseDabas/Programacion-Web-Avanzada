package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.User;

import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de base de datos de la entidad
 * User.
 * Extiende JpaRepository para obtener métodos CRUD estándar.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<User> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     *
     * @param username El nombre de usuario a verificar.
     * @return true si existe, false en caso contrario.
     */
    Boolean existsByUsername(String username);
}
