package app.Class;

import java.sql.Date;

/**
 * Classe Ramassage pour représenter un ramassage.
 */
public class Ramassage {
    /**
     * L'identifiant du ramassage.
     */
    public int id;

    /**
     * La date du ramassage.
     */
    public Date date;

    /**
     * Le statut du ramassage.
     */
    public String fk_status;

    /**
     * Le poids du ramassage.
     */
    public double poids;

    /**
     * L'identifiant du contenant utilisé pour le ramassage.
     */
    public int fk_contenant;

    /**
     * L'identifiant de l'employé qui a effectué le ramassage.
     */
    public String fk_employee;

    /**
     * L'identifiant de la déchèterie où le ramassage a eu lieu.
     */
    public int fk_decheterie;

    /**
     * L'identifiant du véhicule utilisé pour le ramassage.
     */
    public String fk_vehicule;

    /**
     * Constructeur de la classe Ramassage avec tous les paramètres.
     * @param id L'identifiant du ramassage.
     * @param date La date du ramassage.
     * @param fk_status Le statut du ramassage.
     * @param poids Le poids du ramassage.
     * @param fk_contenant L'identifiant du contenant utilisé pour le ramassage.
     * @param fk_employee L'identifiant de l'employé qui a effectué le ramassage.
     * @param fk_decheterie L'identifiant de la déchèterie où le ramassage a eu lieu.
     * @param fk_vehicule L'identifiant du véhicule utilisé pour le ramassage.
     */
    public Ramassage(int id, Date date, String fk_status, double poids, int fk_contenant, String fk_employee, int fk_decheterie, String fk_vehicule) {
        this.id = id;
        this.date = date;
        this.fk_status = fk_status;
        this.poids = poids;
        this.fk_contenant = fk_contenant;
        this.fk_employee = fk_employee;
        this.fk_decheterie = fk_decheterie;
        this.fk_vehicule = fk_vehicule;
    }

    /**
     * Constructeur par défaut de la classe Ramassage.
     */
    public Ramassage() {
    }
}