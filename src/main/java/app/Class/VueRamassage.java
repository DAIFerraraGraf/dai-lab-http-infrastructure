package app.Class;

import java.sql.Date;

/**
 * Classe VueRamassage pour représenter l'affichage d'un ramassage.
 */
public class VueRamassage {
    /**
     * L'identifiant du ramassage.
     */
    public int id_ramassage;

    /**
     * La date du ramassage.
     */
    public Date date_ramassage;

    /**
     * L'identifiant de la déchèterie.
     */
    public int id_decheterie;

    /**
     * Le nom de la déchèterie.
     */
    public String nom_decheterie;

    /**
     * Le statut du ramassage.
     */
    public String status_ramassage;

    /**
     * L'identifiant de l'employé.
     */
    public String id_employe;

    /**
     * Le nom de l'employé.
     */
    public String nom_employe;

    /**
     * Le prénom de l'employé.
     */
    public String prenom_employe;

    /**
     * L'identifiant du contenant.
     */
    public int id_contenant;

    /**
     * Le nom du contenant.
     */
    public String nom_contenant;

    /**
     * Le poids du ramassage.
     */
    public int poids;

    /**
     * La taille du contenant.
     */
    public String taille_contenant;

    /**
     * Le nombre de cadres du contenant.
     */
    public int nbCadre_contenant;

    /**
     * Le type de véhicule.
     */
    public String type_vehicule;

    /**
     * L'immatriculation du véhicule.
     */
    public String immatriculation_vehicule;

    /**
     * Constructeur de la classe VueRamassage avec tous les paramètres.
     * @param id_ramassage L'identifiant du ramassage.
     * @param date_ramassage La date du ramassage.
     * @param id_decheterie L'identifiant de la déchèterie.
     * @param nom_decheterie Le nom de la déchèterie.
     * @param status_ramassage Le statut du ramassage.
     * @param id_employe L'identifiant de l'employé.
     * @param nom_employe Le nom de l'employé.
     * @param prenom_employe Le prénom de l'employé.
     * @param id_contenant L'identifiant du contenant.
     * @param nom_contenant Le nom du contenant.
     * @param poids Le poids du ramassage.
     * @param taille_contenant La taille du contenant.
     * @param nbCadre_contenant Le nombre de cadres du contenant.
     * @param type_vehicule Le type de véhicule.
     * @param immatriculation_vehicule L'immatriculation du véhicule.
     */
    public VueRamassage(int id_ramassage, Date date_ramassage, int id_decheterie, String nom_decheterie, String status_ramassage, String id_employe, String nom_employe, String prenom_employe, int id_contenant, String nom_contenant, int poids, String taille_contenant, int nbCadre_contenant, String type_vehicule, String immatriculation_vehicule) {
        this.id_ramassage = id_ramassage;
        this.date_ramassage = date_ramassage;
        this.id_decheterie = id_decheterie;
        this.nom_decheterie = nom_decheterie;
        this.status_ramassage = status_ramassage;
        this.id_employe = id_employe;
        this.nom_employe = nom_employe;
        this.prenom_employe = prenom_employe;
        this.id_contenant = id_contenant;
        this.nom_contenant = nom_contenant;
        this.poids = poids;
        this.taille_contenant = taille_contenant;
        this.nbCadre_contenant = nbCadre_contenant;
        this.type_vehicule = type_vehicule;
        this.immatriculation_vehicule = immatriculation_vehicule;
    }

    /**
     * Constructeur par défaut de la classe VueRamassage.
     */
    public VueRamassage() {
    }
}