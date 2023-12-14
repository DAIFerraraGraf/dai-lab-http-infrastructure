package ch.api;
import ch.api.Controller.*;
import io.javalin.*;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(CorsPluginConfig::anyHost);
            });
        });
        app.start(80);

        TaskController taskController = new TaskController();
        app.get("/api/tasks", taskController::getAll);
        app.get("/api/tasks/{id}", taskController::getOne);
        app.post("/api/tasks/", taskController::create);
        app.put("/api/tasks/{id}", taskController::update);
        app.delete("/api/tasks/{id}", taskController::delete);

    }
}
