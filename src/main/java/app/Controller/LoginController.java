package app.Controller;

import app.Class.Login;
import app.Database;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.sql.PreparedStatement;

/**
 * Classe LoginController pour la gestion de la connexion des utilisateurs.
 */
public class LoginController {
    /**
     * Envoie le statut de connexion de l'utilisateur (200 réussi / 403 échec).
     * Set les cookies de fonction et d'ID de connexion.
     * @param ctx le contexte de la requête HTTP
     */
    public void sendLoginStatus(Context ctx) {
        try {
            Login login = ctx.bodyAsClass(Login.class);

            // Prépare la requête SQL pour vérifier les informations de connexion
            String query = "SELECT * FROM employe WHERE idlogin = ? AND mdplogin = ?";
            PreparedStatement stmt = Database.getConnection().prepareStatement(query);
            stmt.setString(1, login.idlogin);
            stmt.setString(2, login.mdplogin);

            ResultSet rs = stmt.executeQuery();

            // Si les informations de connexion sont correctes
            if (rs.next()) {
                String fonction = rs.getString("FK_fonction");

                // Normalise la fonction pour enlever les accents
                String asciiFonction = Normalizer.normalize(fonction, Normalizer.Form.NFD);
                Pattern pattern = Pattern.compile("\\P{ASCII}");
                asciiFonction = pattern.matcher(asciiFonction).replaceAll("");

                // Enregistre la fonction et l'ID de connexion dans les cookies
                ctx.cookie("fonction", asciiFonction);
                ctx.cookie("idLogin", login.idlogin);

                // Affiche un message de connexion réussie
                String nom_prenom = rs.getString("nom") + " " + rs.getString("prenom");
                System.out.println("Connexion de " + nom_prenom + " en tant que " + fonction);

                // Envoie un statut HTTP 200 pour indiquer que la connexion a réussi
                ctx.status(200);
            } else {
                // Envoie un statut HTTP 403 pour indiquer que la connexion a échoué
                ctx.status(403);
            }
        } catch (Exception e) {
            // Envoie un statut HTTP 500 pour indiquer une erreur interne du serveur
            ctx.status(500);
            e.printStackTrace();
        }
    }
}