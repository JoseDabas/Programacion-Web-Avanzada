package practica1.mockup_api.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la vista del formulario de inicio de sesión.
 * Sirve la plantilla HTML de login.
 */
@Controller
public class LoginController {

    /**
     * Muestra la página de login.
     *
     * @return El nombre de la vista (template) "login".
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
