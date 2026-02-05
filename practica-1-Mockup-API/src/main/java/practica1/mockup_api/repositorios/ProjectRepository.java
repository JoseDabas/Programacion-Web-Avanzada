package practica1.mockup_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entidades.Project;
import practica1.mockup_api.entidades.User;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUser(User user);
}
