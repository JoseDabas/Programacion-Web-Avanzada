package practica1.mockup_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practica1.mockup_api.entities.Project;
import practica1.mockup_api.entities.User;
import practica1.mockup_api.repositories.ProjectRepository;
import practica1.mockup_api.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<Project> findAllByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return projectRepository.findByUser(user);
    }

    public void createProject(String name, String description, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setUser(user);

        projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
