package app.Controller;

import app.Class.Ramassage;
import app.Class.VueRamassage;
import app.Database;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.util.ArrayList;
/**
 * Classe RamassageController pour la gestion des ramassages.
 */
public class RamassageController {
    /**
     * Récupère tous les ramassages en fonction du rôle de l'utilisateur.
     * Si l'utilisateur est un employé, cette méthode récupère tous les ramassages associés à sa déchèterie.
     * Si l'utilisateur est un chauffeur ou une secrétaire, cette méthode récupère tous les ramassages associés à la déchèterie où ils travaillent.
     * Si l'utilisateur est un responsable, cette méthode récupère tous les ramassages associés à la déchèterie qu'il supervise.
     * Le rôle de l'utilisateur est déterminé à partir du cookie "fonction" récupéré du contexte de la requête HTTP.
     * @param ctx le contexte de la requête HTTP
     */
    public void getAll(Context ctx) {
        ArrayList ramassages = new ArrayList<VueRamassage>();
        try {
            if(ctx.cookie("fonction").equals("Employe")){
                ResultSet rs = Database.executeQuery("SELECT * FROM employe_decheterie " +
                        "INNER JOIN employe ON employe.fk_decheterie = employe_decheterie.id_decheterie " +
                        "WHERE employe.idlogin = '" + ctx.cookie("idLogin") + "' ORDER BY employe_decheterie.id_decheterie");
                while (rs.next()) {
                    ramassages.add(new VueRamassage(rs.getInt("id_ramassage"), rs.getDate("date_ramassage"), rs.getInt("id_decheterie"), rs.getString("nom_decheterie"), rs.getString("status_ramassage"), rs.getString("id_employe"), rs.getString("nom_employe"), rs.getString("prenom_employe"), rs.getInt("id_contenant"), rs.getString("nom_contenant"), rs.getInt("poids"), rs.getString("taille_contenant"), rs.getInt("nbCadre_contenant"), rs.getString("type_vehicule"), rs.getString("immatriculation_vehicule")));
                }
                ctx.json(ramassages);

            } else if(ctx.cookie("fonction").equals("Chauffeur") || ctx.cookie("fonction").equals("Secretaire")){
                ResultSet rs = Database.executeQuery(
                "SELECT sdr.* FROM decheterie_principale AS dp " +
                "RIGHT JOIN secretaire_decheterie_ramassage AS sdr on dp.decheterie_id = sdr.id_decheterie " +
                "WHERE dp.decheterie_principale_id = (SELECT id_decheterie FROM secretaire_decheterie_employe WHERE id_employe = '" + ctx.cookie("idLogin") + "')" +
                "OR sdr.id_decheterie = (SELECT id_decheterie FROM secretaire_decheterie_employe WHERE id_employe = '" + ctx.cookie("idLogin") + "')" +
                "ORDER BY sdr.date_ramassage");
                while (rs.next()) {
                    ramassages.add(new VueRamassage(rs.getInt("id_ramassage"), rs.getDate("date_ramassage"), rs.getInt("id_decheterie"), rs.getString("nom_decheterie"), rs.getString("status_ramassage"), rs.getString("id_employe"), rs.getString("nom_employe"), rs.getString("prenom_employe"), rs.getInt("id_contenant"), rs.getString("nom_contenant"), rs.getInt("poids"), rs.getString("taille_contenant"), rs.getInt("nbCadre_contenant"), rs.getString("type_vehicule"), rs.getString("immatriculation_vehicule")));
                }
                ctx.json(ramassages);

            } else if(ctx.cookie("fonction").equals("Responsable")){
                ResultSet rs = Database.executeQuery("SELECT * FROM secretaire_decheterie_ramassage " +
                        "WHERE secretaire_decheterie_ramassage.id_decheterie = " +
                        " (SELECT id_decheterie FROM secretaire_decheterie_employe " +
                        "WHERE secretaire_decheterie_employe.id_employe = '" + ctx.cookie("idLogin") + "')" +
                        "ORDER BY secretaire_decheterie_ramassage.date_ramassage");
                while (rs.next()) {
                    ramassages.add(new VueRamassage(rs.getInt("id_ramassage"), rs.getDate("date_ramassage"), rs.getInt("id_decheterie"), rs.getString("nom_decheterie"), rs.getString("status_ramassage"), rs.getString("id_employe"), rs.getString("nom_employe"), rs.getString("prenom_employe"), rs.getInt("id_contenant"), rs.getString("nom_contenant"), rs.getInt("poids"), rs.getString("taille_contenant"), rs.getInt("nbCadre_contenant"), rs.getString("type_vehicule"), rs.getString("immatriculation_vehicule")));
                }
                ctx.json(ramassages);

            }

            System.out.println("GET /ramassage de " + ctx.cookie("idLogin") + " en tant que " + ctx.cookie("fonction"));

        } catch (Exception e) {
            ctx.status(500);
            e.printStackTrace();
        }
    }
    /**
     * Récupère un ramassage spécifique en fonction de l'ID.
     * @param ctx le contexte de la requête HTTP
     */
    public void getOne(Context ctx){
        try{
            ResultSet rs = Database.executeQuery("SELECT * FROM ramassage WHERE id = '" + ctx.pathParam("id") + "'");
            if(rs.next()){
                ctx.json(new Ramassage(rs.getInt("id"), rs.getDate("date"), rs.getString("status"), rs.getInt("poids"), rs.getInt("fk_contenant"), rs.getString("fk_employee"), rs.getInt("fk_decheterie"), rs.getString("fk_vehicule")));
            }

        }catch (Exception e) {
            ctx.status(500);
            e.printStackTrace();
        }
    }
    /**
     * Crée un nouveau ramassage à partir des données de la requête.
     * @param ctx le contexte de la requête HTTP
     */
    public void create(Context ctx) {
        try {

            Ramassage ramassage = ctx.bodyAsClass(Ramassage.class);
            ramassage.id = uniqueID();
            Database.executeUpdate("INSERT INTO ramassage (id, date, status, poids, fk_contenant, fk_employee, fk_decheterie, fk_vehicule) VALUES ('" + ramassage.id + "', '" + ramassage.date + "', '" + ramassage.status + "', '" + ramassage.poids + "', '" + ramassage.fk_contenant + "', '" + ramassage.fk_employee + "', '" + ramassage.fk_decheterie + "', '" + ramassage.fk_vehicule + "')");
            ctx.status(201);
        } catch (Exception e) {
            ctx.status(500).result(e.getMessage());
        }
    }
    /**
     * Supprime un ramassage spécifique en fonction de l'ID.
     * @param ctx le contexte de la requête HTTP
     */
    public void delete(Context ctx) {
        try {
            Database.executeUpdate("DELETE FROM ramassage WHERE id = '" + ctx.pathParam("id") + "'");
            ctx.status(204);
        } catch (Exception e) {
            ctx.status(500).result(e.getMessage());
        }
    }
    /**
     * Met à jour un ramassage spécifique en fonction de l'ID.
     * @param ctx le contexte de la requête HTTP
     */
    public void update(Context ctx) {
        try {
            Ramassage ramassage = ctx.bodyAsClass(Ramassage.class);
            Database.executeUpdate("UPDATE ramassage SET date = '" + ramassage.date + "', status = '" + ramassage.status + "', poids = '" + ramassage.poids + "', fk_contenant = '" + ramassage.fk_contenant + "', fk_employee = '" + ramassage.fk_employee + "', fk_decheterie = '" + ramassage.fk_decheterie + "', fk_vehicule = '" + ramassage.fk_vehicule + "' WHERE id = '" + ctx.pathParam("id") + "'");
            ctx.status(204);
        } catch (Exception e) {
            ctx.status(500).result(e.getMessage());
        }
    }
    /**
     * Génère un ID unique pour un nouveau ramassage.
     * @return un ID unique pour un nouveau ramassage
     */
    private int uniqueID() {
        try {
            ResultSet rs = Database.executeQuery("SELECT MAX(id) FROM ramassage");
            if(rs.next()){
                return rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
