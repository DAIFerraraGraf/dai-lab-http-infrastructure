package app.Controller;

import app.Class.Employe;
import app.Class.VueEmploye;
import app.Database;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe EmployeController pour la gestion des employés.
 */
public class EmployeController {
    /**
     * Récupère tous les employés en fonction du rôle de l'utilisateur.
     * Si l'utilisateur est une secrétaire, elle récupère les employés de sa déchèterie.
     * Si l'utilisateur est un responsable, elle récupère les employés qu'il supervise.
     * Si l'utilisateur est un chauffeur ou un employé, elle récupère ses propres informations.
     *
     * @param ctx le contexte de la requête HTTP
     */
    public void getAll(Context ctx) {
        List<VueEmploye> employees = new ArrayList<VueEmploye>();
        try {
            if (ctx.cookie("fonction").equals("Secretaire")) {

                ResultSet rs = Database.executeQuery("SELECT sde.* FROM decheterie_principale AS dp " +
                        "RIGHT JOIN secretaire_decheterie_employe AS sde on dp.decheterie_id = sde.id_decheterie " +
                        "WHERE dp.decheterie_principale_id = (SELECT id_decheterie FROM secretaire_decheterie_employe " +
                        "WHERE id_employe = '" + ctx.cookie("idLogin") + "') " +
                        "OR sde.id_decheterie = (SELECT id_decheterie FROM secretaire_decheterie_employe " +
                        "WHERE id_employe = '" + ctx.cookie("idLogin") + "') " +
                        "ORDER BY sde.id_decheterie");
                while (rs.next()) {
                    employees.add(new VueEmploye(rs.getInt("id_decheterie"), rs.getString("nom_decheterie"), rs.getString("id_employe"), rs.getString("nom_employe"), rs.getString("prenom_employe"), rs.getString("fonction_employe"), rs.getDate("date_naissance"), rs.getDate("date_debut_contrat"), rs.getString("numero_telephone"), rs.getString("type_permis")));
                }
                ctx.json(employees);

            } else if (ctx.cookie("fonction").equals("Responsable")) {

                ResultSet rs = Database.executeQuery("SELECT secretaire_decheterie_employe.* FROM secretaire_decheterie_employe " +
                        "INNER JOIN superviseur ON secretaire_decheterie_employe.id_employe = superviseur.fk_employee " +
                        "WHERE superviseur.FK_superviseur = '" + ctx.cookie("idLogin") + "' ORDER BY secretaire_decheterie_employe.id_decheterie");
                while (rs.next()) {
                    employees.add(new VueEmploye(rs.getInt("id_decheterie"), rs.getString("nom_decheterie"), rs.getString("id_employe"), rs.getString("nom_employe"), rs.getString("prenom_employe"), rs.getString("fonction_employe"), rs.getDate("date_naissance"), rs.getDate("date_debut_contrat"), rs.getString("numero_telephone"), rs.getString("type_permis")));
                }
                ctx.json(employees);

            } else if (ctx.cookie("fonction").equals("Chauffeur") || ctx.cookie("fonction").equals("Employe")) {

                ResultSet rs = Database.executeQuery("SELECT * FROM secretaire_decheterie_employe " +
                        "WHERE secretaire_decheterie_employe.id_employe = '" + ctx.cookie("idLogin") + "' " +
                        "ORDER BY secretaire_decheterie_employe.id_decheterie");
                while (rs.next()) {
                    employees.add(new VueEmploye(rs.getInt("id_decheterie"), rs.getString("nom_decheterie"), rs.getString("id_employe"), rs.getString("nom_employe"), rs.getString("prenom_employe"), rs.getString("fonction_employe"), rs.getDate("date_naissance"), rs.getDate("date_debut_contrat"), rs.getString("numero_telephone"), rs.getString("type_permis")));
                }
                ctx.json(employees);
            }

            System.out.println("GET /employe de " + ctx.cookie("idLogin") + " en tant que " + ctx.cookie("fonction"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Récupère un employé spécifique en fonction de l'ID de connexion.
     *
     * @param ctx le contexte de la requête HTTP
     */
    public void getOne(Context ctx) {
        try {
            ResultSet rs = Database.executeQuery("SELECT * FROM employe WHERE idlogin = '" + ctx.pathParam("idlogin") + "'");
            rs.next();
            Employe employe = new Employe(rs.getString("idlogin"), rs.getString("mdplogin"), rs.getString("nom"), rs.getString("prenom"), rs.getDate("datenaissance"), rs.getDate("datedebutcontrat"), rs.getString("FK_fonction"), rs.getString("numtelephone"), rs.getString("typepermis"), rs.getInt("fk_adresse"), rs.getInt("fk_decheterie"));
            ctx.json(employe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée un nouvel employé à partir des données de la requête.
     *
     * @param ctx le contexte de la requête HTTP
     */
    public void create(Context ctx) {
        try {
            Employe employe = ctx.bodyAsClass(Employe.class);
            String sql;
            if (employe.fk_fonction.equals("Chauffeur")) {
                sql = "INSERT INTO employe VALUES ('" + employe.idlogin + "', '" + employe.mdplogin + "', '" + employe.nom + "', '" + employe.prenom + "', '" + employe.datenaissance + "', '" + employe.datedebutcontrat + "', '" + employe.numtelephone + "', '" + employe.typepermis + "', " + employe.fk_adresse + ", " + employe.fk_decheterie + ", '" + employe.fk_fonction + "')";
            } else {
                sql = "INSERT INTO employe (idlogin, mdplogin, nom, prenom, datenaissance, datedebutcontrat, FK_fonction, numtelephone, fk_adresse, fk_decheterie) VALUES ('" + employe.idlogin + "', '" + employe.mdplogin + "', '" + employe.nom + "', '" + employe.prenom + "', '" + employe.datenaissance + "', '" + employe.datedebutcontrat + "', '" + employe.fk_fonction + "', '" + employe.numtelephone + "', " + employe.fk_adresse + ", " + employe.fk_decheterie + ")";

            }
            int affectedRows = Database.executeUpdate(sql);
            if (affectedRows > 0) {
                ctx.status(201);
            } else {
                ctx.status(500).result("Failed to create employee");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result(e.getMessage());
        }
    }

    /**
     * Met à jour un employé spécifique en fonction de l'ID de connexion.
     *
     * @param ctx le contexte de la requête HTTP
     */
    public void update(Context ctx) {
        try {
            Employe employe = ctx.bodyAsClass(Employe.class);
            String sql;
            if (employe.fk_fonction.equals("Chauffeur")) {
                sql = "UPDATE employe SET mdplogin = '" + employe.mdplogin + "', nom = '" + employe.nom + "', prenom = '" + employe.prenom + "', datenaissance = '" + employe.datenaissance + "', datedebutcontrat = '" + employe.datedebutcontrat + "', fk_fonction = '" + employe.fk_fonction + "', numtelephone = '" + employe.numtelephone + "', typepermis = '" + employe.typepermis + "', fk_adresse = " + employe.fk_adresse + ", fk_decheterie = " + employe.fk_decheterie + " WHERE idlogin = '" + ctx.pathParam("idlogin") + "'";

            } else {
                sql = "UPDATE employe SET mdplogin = '" + employe.mdplogin + "', nom = '" + employe.nom + "', prenom = '" + employe.prenom + "', datenaissance = '" + employe.datenaissance + "', datedebutcontrat = '" + employe.datedebutcontrat + "', fk_fonction = '" + employe.fk_fonction + "', numtelephone = '" + employe.numtelephone + "', fk_adresse = " + employe.fk_adresse + ", fk_decheterie = " + employe.fk_decheterie + " WHERE idlogin = '" + ctx.pathParam("idlogin") + "'";

            }
            int affectedRows = Database.executeUpdate(sql);
            if (affectedRows > 0) {
                ctx.status(204);
            } else {
                ctx.status(500).result("Failed to update employee");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result(e.getMessage());
        }
    }

    /**
     * Supprime un employé spécifique en fonction de l'ID de connexion.
     *
     * @param ctx le contexte de la requête HTTP
     */
    public void delete(Context ctx) {
        try {
            String sql = "DELETE FROM employe WHERE idlogin = '" + ctx.pathParam("idlogin") + "'";
            int affectedRows = Database.executeUpdate(sql);
            if (affectedRows > 0) {
                ctx.status(204);
            } else {
                ctx.status(500).result("Failed to delete employee");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(501).result(e.getMessage());
        }
    }

    /**
     * Vérifie si un employé spécifique existe en fonction de l'ID de connexion.
     *
     * @param ctx le contexte de la requête HTTP
     */
    public void getIDlogin(Context ctx) {
        try {
            ResultSet rs = Database.executeQuery("SELECT idlogin FROM employe WHERE idlogin = '" + ctx.pathParam("idlogin") + "'");
            if (rs.next()) {
                ctx.status(200);
            } else {
                ctx.status(404);
            }
        } catch (Exception e) {
            ctx.status(500);
            e.printStackTrace();
        }
    }
}