package practica1.mockup_api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Controlador para mostrar información de la instancia del servidor y probar la
 * persistencia de sesiones en Redis.
 */
@RestController
public class InfoController {

    @GetMapping("/info")
    public String getServerInfo(HttpSession session) {
        String hostname;
        try {
            // Captura "app-1", "app-2" o "app-3" en Docker
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "Desconocido";
        }

        // Guardamos o leemos un valor en sesión para probar Redis
        Integer vistas = (Integer) session.getAttribute("vistas");
        if (vistas == null)
            vistas = 0;
        vistas++;
        session.setAttribute("vistas", vistas);

        return "<h1>Respondido por instancia: " + hostname + "</h1>" +
                "<p>ID de Sesión: " + session.getId() + "</p>" +
                "<p>Vistas de esta sesión: " + vistas + "</p>";
    }
}
