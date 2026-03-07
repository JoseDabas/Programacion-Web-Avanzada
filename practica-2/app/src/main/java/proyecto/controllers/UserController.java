package proyecto.controllers;

import io.javalin.Javalin;
import org.mindrot.jbcrypt.BCrypt;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bson.types.ObjectId;
import proyecto.clases.Usuario;
import proyecto.services.UserServices;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.Cookie;

import javax.crypto.SecretKey;
import javax.crypto.SecretKey;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserController extends BaseController {
    public UserController(Javalin app) {
        super(app);
        registerTemplates();
    }

    @Override
    public void aplicarRutas() {
        app.before("/user/admin/*", ctx -> {
            if (!isAdminAuthorized(ctx)) {
                throw new io.javalin.http.ForbiddenResponse("Access denied");
            }
        });

        app.before("/user/borrar/*", ctx -> {
            if (!isAdminAuthorized(ctx)) {
                throw new io.javalin.http.ForbiddenResponse("Access denied");
            }
        });

        app.before("/stats/*", ctx -> {
            if (!isAdminAuthorized(ctx)) {
                throw new io.javalin.http.ForbiddenResponse("Access denied");
            }
        });

        app.before("/admin/dashboard", ctx -> {
            if (!isAdminAuthorized(ctx)) {
                throw new io.javalin.http.ForbiddenResponse("Access denied");
            }
        });
        app.get("/user/register", ctx -> {
            ctx.render("/public/templates/register.html");
        });

        app.post("/user/register", ctx -> {
            String username = ctx.formParam("usuario");
            String password = ctx.formParam("password");

            Usuario existingUser = UserServices.getInstance().findByUsername(username);

            if (existingUser != null) {
                ctx.render("/public/templates/register.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
                Usuario newUser = new Usuario(new ObjectId(), username, hashed, false);
                UserServices.getInstance().crear(newUser);
                ctx.sessionAttribute("username", newUser);
                ctx.redirect("/");
            }
        });

        app.get("user/login", ctx -> {
            ctx.render("/public/templates/Login.html");
        });

        app.post("user/login", ctx -> {
            String username = ctx.formParam("usuario");
            String password = ctx.formParam("password");

            Usuario user = UserServices.getInstance().findByUsername(username);
            if (user != null) {
                boolean ok = false;
                String stored = user.getPassword();
                if (stored != null && stored.startsWith("$2")) {
                    ok = BCrypt.checkpw(password, stored);
                } else if (stored != null && stored.equals(password)) {
                    String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
                    user.setPassword(hashed);
                    UserServices.getInstance().update(user);
                    ok = true;
                }
                if (ok) {
                    if (ctx.formParam("rememberMe") != null) {
                        String secret = System.getProperty("JWT_SECRET", "dev-secret");
                        javax.crypto.SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                        long duration = 1000L * 60 * 60 * 24 * 30; // 30 días
                        Date expiration = new Date(System.currentTimeMillis() + duration);
                        String role = user.isAdmin() ? "ADMIN" : "USER";
                        String token = Jwts.builder()
                                .setSubject(user.getUsername())
                                .claim("role", role)
                                .setExpiration(expiration)
                                .signWith(key, SignatureAlgorithm.HS256)
                                .compact();
                        String cookieHeader = "remember=" + token + "; Path=/; Max-Age=" + (duration / 1000)
                                + "; Secure; HttpOnly; SameSite=Strict";
                        ctx.res().addHeader("Set-Cookie", cookieHeader);
                    }
                    ctx.sessionAttribute("username", user);
                    ctx.redirect("/");
                } else {
                    ctx.render("/public/templates/Login.html", Map.of("error", "Usuario o contraseña incorrectos"));
                }
            } else {
                ctx.render("/public/templates/Login.html", Map.of("error", "Usuario no existe"));
            }
        });

        app.get("/user/crear", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdmin()) {
                ctx.redirect("/");
                return;
            }
            ctx.render("/public/templates/crear-usuario.html");
        });

        app.post("/user/crear", ctx -> {
            Usuario adminUser = ctx.sessionAttribute("username");
            if (adminUser == null || !adminUser.isAdmin()) {
                ctx.redirect("/");
                return;
            }

            String username = ctx.formParam("usuario");
            String password = ctx.formParam("password");
            boolean isAdmin = "on".equals(ctx.formParam("admin"));

            Usuario existingUser = UserServices.getInstance().findByUsername(username);

            if (existingUser != null) {
                ctx.render("/public/templates/crear-usuario.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
                Usuario newUser = new Usuario(new ObjectId(), username, hashed, isAdmin);
                UserServices.getInstance().crear(newUser);
                ctx.redirect("/user/list");
            }
        });

        app.post("/user/tokenJWS/{usuario}", ctx -> {
            String username = ctx.pathParam("usuario");
            Usuario user = UserServices.getInstance().findByUsername(username);
            if (user != null) {
                System.out.println("Usuario encontrado    " + user.getUsername());
                String secret = System.getProperty("JWT_SECRET", "dev-secret");
                javax.crypto.SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                // Define la duración de la sesión en milisegundos
                long sessionDuration = 1000 * 60 * 60; // 1 hora
                Date expiration = new Date(System.currentTimeMillis() + sessionDuration);

                // Crea el token con una reclamación de expiración
                String role = user.isAdmin() ? "ADMIN" : "USER";
                String token = Jwts.builder()
                        .setSubject(user.getUsername())
                        .claim("role", role)
                        .setExpiration(expiration)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();
                ctx.sessionAttribute("jwt", token);
                System.out.println("Token JWT generado para usuario: " + user.getUsername());
                ctx.redirect("/");
                ctx.result(token);

            } else {
                ctx.status(404);
            }
        });

        app.before("/user/list", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdmin()) {
                ctx.redirect("/");
            }
        });

        app.get("/user/list", ctx -> {
            String pageParam = ctx.queryParam("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            List<Usuario> usuarios = UserServices.getInstance().findAll(page, 5);
            long totalUsers = UserServices.getInstance().find().count();
            int totalPages = (int) Math.ceil((double) totalUsers / 5);
            ctx.render("/public/templates/user-list.html",
                    Map.of("usuarios", usuarios, "totalPages", totalPages, "currentPage", page));
        });

        app.get("/user/close", ctx -> {
            ctx.removeCookie("remember");
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });

        app.post("/user/admin/{username}", ctx -> {
            String username = ctx.pathParam("username");
            Usuario user = UserServices.getInstance().findByUsername(username);
            user.setAdmin(true);
            UserServices.getInstance().update(user);
            ctx.redirect("/user/list");
        });

        app.post("/user/borrar/{username}", ctx -> {
            String username = ctx.pathParam("username");
            if (username.equals("admin")) {
                ctx.redirect("/user/list");
                return;
            }
            UserServices.getInstance().deleteByUsername(username);
            ctx.redirect("/user/list");
        });
    }

    private boolean isAdminAuthorized(io.javalin.http.Context ctx) {
        Usuario u = ctx.sessionAttribute("username");
        if (u != null && u.isAdmin()) {
            return true;
        }
        String cookieToken = ctx.cookie("remember");
        if (cookieToken != null) {
            try {
                String secret = System.getProperty("JWT_SECRET", "dev-secret");
                Claims claims = Jwts.parser()
                        .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(cookieToken)
                        .getBody();
                Object role = claims.get("role");
                if (role != null && "ADMIN".equals(role.toString())) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        String auth = ctx.header("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                String secret = System.getProperty("JWT_SECRET", "dev-secret");
                Claims claims = Jwts.parser()
                        .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();
                Object role = claims.get("role");
                if (role != null && "ADMIN".equals(role.toString())) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
