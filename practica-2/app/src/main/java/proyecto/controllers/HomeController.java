package proyecto.controllers;

import io.javalin.Javalin;
import proyecto.clases.URL;
import proyecto.clases.Usuario;
import proyecto.services.URLServices;
import proyecto.services.UserServices;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HomeController extends BaseController {
    public HomeController(Javalin app) {
        super(app);
        registerTemplates();
    }

    @Override
    public void aplicarRutas() {
        app.before("/", ctx -> {
            String token = ctx.cookie("remember");
            if (token != null) {
                try {
                    String secret = System.getProperty("JWT_SECRET", "dev-secret");
                    io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser()
                            .setSigningKey(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8))
                            .parseClaimsJws(token)
                            .getBody();
                    String username = claims.getSubject();
                    Usuario user = UserServices.getInstance().findByUsername(username);
                    if (user != null) {
                        ctx.sessionAttribute("username", user);
                    }
                } catch (Exception ignored) {
                }
            }
        });

        app.get("/", ctx -> {
            List<URL> urls = URLServices.getInstance().find().stream().toList();
            Map<String, Object> model = Map.of("urls", urls);
            ctx.render("public/templates/index.html", model);
        });

        app.get("/service-worker.js", ctx -> {
            ctx.contentType("application/javascript");
            // Lee el archivo service-worker.js de recursos y envíalo como respuesta
            InputStream inputStream = getClass().getResourceAsStream("/public/service-worker.js");
            if (inputStream != null) {
                ctx.result(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            } else {
                ctx.status(404);
            }
        });

        app.get("/manifest.json", ctx -> {
            ctx.contentType("application/json");
            InputStream inputStream = getClass().getResourceAsStream("/public/manifest.json");
            if (inputStream != null) {
                ctx.result(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            } else {
                ctx.status(404);
            }
        });
    }
}
