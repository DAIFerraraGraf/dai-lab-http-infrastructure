package app.Controller;

import app.Database;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.util.ArrayList;

import app.Class.DropDown;
/**
 * Classe DropDownController pour la gestion des listes déroulantes.
 */
public class DropDownController {
    /**
     * Récupère la liste des fonctions.
     * @param ctx le contexte de la requête HTTP
     */
    public void getFonction(Context ctx) {
        ArrayList<DropDown> fonctions = new ArrayList<DropDown>();
        try {
            ResultSet rs = Database.executeQuery("SELECT * FROM fonction");
            while (rs.next()) {
                fonctions.add(new DropDown(rs.getString("nom"), rs.getString("nom")));
            }
            ctx.json(fonctions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Récupère la liste des adresses.
     * @param ctx le contexte de la requête HTTP
     */
    public void getAdresse(Context ctx) {
        ArrayList<DropDown> adresses = new ArrayList<DropDown>();
        try {
            ResultSet rs = Database.executeQuery("SELECT id, rue, numero, npa, nomville FROM adresse");
            while (rs.next()) {
                String adresse = rs.getString("rue") + " " + rs.getString("numero") + ", " + rs.getString("npa") + " " + rs.getString("nomville");
                adresses.add(new DropDown(adresse, String.valueOf(rs.getInt("id"))));
            }
            ctx.json(adresses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Récupère la liste des déchèteries.
     * @param ctx le contexte de la requête HTTP
     */
    public void getDecheterie(Context ctx) {
        ArrayList<DropDown> decheteries = new ArrayList<DropDown>();
        try {
            ResultSet rs = Database.executeQuery("SELECT DISTINCT id, nom FROM decheterie");
            while (rs.next()) {
                decheteries.add(new DropDown(rs.getString("nom"), String.valueOf(rs.getInt("id"))));
            }
            ctx.json(decheteries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Récupère la liste des véhicules.
     * @param ctx le contexte de la requête HTTP
     */
    public void getVehicule(Context ctx) {
        ArrayList<DropDown> vehicules = new ArrayList<DropDown>();
        try {
            ResultSet rs = Database.executeQuery("SELECT DISTINCT immatriculation, type, remorque FROM vehicule");
            while (rs.next()) {
                String vehicule = rs.getString("immatriculation") + " (" + rs.getString("type") + ")";
                if (rs.getBoolean("remorque")) {
                    vehicule += " + remorque";
                }
                vehicules.add(new DropDown(vehicule, rs.getString("immatriculation")));
            }
            ctx.json(vehicules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Récupère la liste des employés qui sont Chauffeur.
     * @param ctx le contexte de la requête HTTP
     */
    public void getEmploye(Context ctx) {
        ArrayList<DropDown> employes = new ArrayList<DropDown>();
        try {
            ResultSet rs = Database.executeQuery("SELECT DISTINCT idlogin, nom, prenom, typepermis FROM employe WHERE fk_fonction = 'Chauffeur'");
            while (rs.next()) {
                String employe = rs.getString("nom") + " " + rs.getString("prenom") + " ( Permis : " + rs.getString("typepermis") + " )";
                employes.add(new DropDown(employe, rs.getString("idlogin")));
            }
            ctx.json(employes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Récupère la liste des contenants en fonction de l'id d'une déchèterie.
     * @param ctx le contexte de la requête HTTP
     */
    public void getContenant(Context ctx) {
        ArrayList<DropDown> contenants = new ArrayList<DropDown>();
        try {
            ResultSet rs = Database.executeQuery("SELECT contenant.id, contenant.nom, type FROM contenant INNER JOIN decheterie ON contenant.fk_decheterie = decheterie.id " +
                    "INNER JOIN dechet ON dechet.type = contenant.fk_dechet WHERE decheterie.id = '" + ctx.pathParam("idDecheterie") + "'");
            while (rs.next()) {
                String contenant = rs.getString("nom") + " (" + rs.getString("type") + ")";
                contenants.add(new DropDown(contenant, String.valueOf(rs.getInt("id"))));
            }
            ctx.json(contenants);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
