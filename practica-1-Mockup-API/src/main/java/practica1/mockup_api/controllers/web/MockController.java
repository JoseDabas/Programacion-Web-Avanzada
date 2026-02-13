package practica1.mockup_api.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import practica1.mockup_api.entities.MockEndpoint;

import practica1.mockup_api.services.MockService;
import practica1.mockup_api.security.jwt.JwtService;

@Controller
@RequestMapping("/projects/{projectId}/mocks")
@RequiredArgsConstructor
public class MockController {

    private final MockService mockService;

    private final JwtService jwtService;

    // Listar Mocks de un Proyecto
    @GetMapping
    public String listMocks(@PathVariable Long projectId, Model model) {
        model.addAttribute("mocks", mockService.findByProject(projectId));
        model.addAttribute("projectId", projectId);
        return "mock-list";
    }

    // Mostrar Formulario de Creación
    @GetMapping("/create")
    public String showCreateForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("mock", new MockEndpoint());
        model.addAttribute("projectId", projectId);
        return "mock-form"; // Vista del formulario (i18n)
    }

    // Mostrar Formulario de Edición
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long projectId, @PathVariable Long id, Model model) {
        model.addAttribute("mock", mockService.findById(id));
        model.addAttribute("projectId", projectId);
        return "mock-form";
    }

    // Guardar Mock
    @PostMapping("/save")
    public String saveMock(@PathVariable Long projectId,
            @ModelAttribute MockEndpoint mock,
            @RequestParam String expirationType,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        // Guardamos y obtenemos el objeto persistido
        MockEndpoint savedMock = mockService.createMock(projectId, mock, expirationType);

        // Si activaron JWT, generamos el token inmediatamente
        if (savedMock.isJwtProtected()) {
            String token = jwtService.generateToken(savedMock);

            // Enviamos el token a la vista de lista como variable temporal
            redirectAttributes.addFlashAttribute("newToken", token);
            redirectAttributes.addFlashAttribute("message", "Mock creado exitosamente. Copia tu token JWT.");
        }

        return "redirect:/projects/" + projectId + "/mocks";
    }

    // Eliminar Mock
    @GetMapping("/delete/{id}")
    public String deleteMock(@PathVariable Long projectId, @PathVariable Long id) {
        mockService.deleteMock(id);
        return "redirect:/projects/" + projectId + "/mocks";
    }
}
