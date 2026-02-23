package proyecto.controllers;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;

public abstract class BaseController {

    protected Javalin app;

    public BaseController(Javalin app) {
        this.app = app;
    }

    abstract public void aplicarRutas();

    public void registerTemplates() {
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

}
