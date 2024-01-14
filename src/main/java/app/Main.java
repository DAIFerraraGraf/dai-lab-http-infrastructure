package app;
import app.Controller.*;
import io.javalin.*;

/**
 * Classe principale de l'application.
 */
public class Main {
    /**
     * Méthode principale de l'application.
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                    it.allowCredentials = true;

                });
            });

        });

        app.start(80);

        LoginController loginController = new LoginController();
        app.post("/api/login/", loginController::sendLoginStatus);

        EmployeController employeController = new EmployeController();
        app.get("/api/employes", employeController::getAll);
        app.get("/api/employes/{idlogin}", employeController::getOne);
        app.post("/api/employes/", employeController::create);
        app.put("/api/employes/{idlogin}", employeController::update);
        app.delete("/api/employes/{idlogin}", employeController::delete);
        app.get("/api/employesExist/{idlogin}", employeController::getIDlogin);

        DropDownController dropDownController = new DropDownController();
        app.get("/api/fonctions", dropDownController::getFonction);
        app.get("/api/adresses", dropDownController::getAdresse);
        app.get("/api/decheteries", dropDownController::getDecheterie);
        app.get("/api/vehicules", dropDownController::getVehicule);
        app.get("/api/contenants/{idDecheterie}", dropDownController::getContenant);
        app.get("/api/employesList", dropDownController::getEmploye);

        RamassageController ramassageController = new RamassageController();
        app.get("/api/ramassages", ramassageController::getAll);
        app.get("/api/ramassages/{id}", ramassageController::getOne);
        app.post("/api/ramassages", ramassageController::create);
        app.delete("/api/ramassages/{id}", ramassageController::delete);
        app.put("/api/ramassages/{id}", ramassageController::update);


    }
}