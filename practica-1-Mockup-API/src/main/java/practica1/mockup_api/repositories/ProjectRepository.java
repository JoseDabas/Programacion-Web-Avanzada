package practica1.mockup_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import practica1.mockup_api.entities.Project;
import practica1.mockup_api.entities.User;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUser(User user);
}
