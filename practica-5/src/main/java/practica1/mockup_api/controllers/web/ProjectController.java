package practica1.mockup_api.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import practica1.mockup_api.entities.Project;
import practica1.mockup_api.services.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public String listProjects(Model model, Authentication authentication) {
        // Obtener el usuario logueado
        String username = authentication.getName();

        // Buscar solo los proyectos de ese usuario
        List<Project> projects = projectService.findAllByUser(username);

        model.addAttribute("projects", projects);
        return "projects";
    }

    @PostMapping("/create")
    public String createProject(@RequestParam String name,
            @RequestParam String description,
            Authentication authentication) {
        projectService.createProject(name, description, authentication.getName());
        return "redirect:/projects";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/projects";
    }
}
