package practica1.mockup_api.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import practica1.mockup_api.services.UserService;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Solo Admins entran aquí
public class UserController {

    private final UserService userService;

    // Listar Usuarios
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user-list";
    }

    // Formulario de Creación
    @GetMapping("/create")
    public String showCreateForm() {
        return "user-form";
    }

    // Guardar Usuario
    @PostMapping("/save")
    public String saveUser(@RequestParam String username,
            @RequestParam String password,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        try {
            userService.createUser(username, password, isAdmin);
        } catch (Exception e) {
            return "redirect:/users/create?error";
        }
        return "redirect:/users";
    }

    // Activar/Desactivar Usuario
    @GetMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/users";
    }
}
