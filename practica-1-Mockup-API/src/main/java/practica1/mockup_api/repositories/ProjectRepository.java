package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.Project;
import practica1.mockup_api.entities.User;

import java.util.List;

/**
 * Repositorio para la gestión de proyectos.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Encuentra todos los proyectos que pertenecen a un usuario específico.
     *
     * @param user El usuario propietario.
     * @return Lista de proyectos del usuario.
     */
    List<Project> findByUser(User user);
}
