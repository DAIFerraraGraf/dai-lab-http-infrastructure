package app.Class;

import java.sql.Date;

/**
 * Classe VueEmploye pour représenter l'affichage d'un employé.
 */
public class VueEmploye {
    /**
     * L'identifiant de la déchèterie.
     */
    public int id_decheterie;

    /**
     * Le nom de la déchèterie.
     */
    public String nom_decheterie;

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
     * La fonction de l'employé.
     */
    public String fonction_employe;

    /**
     * La date de naissance de l'employé.
     */
    public Date date_naissance;

    /**
     * La date de début de contrat de l'employé.
     */
    public Date date_debut_contrat;

    /**
     * Le numéro de téléphone de l'employé.
     */
    public String numero_telephone;

    /**
     * Le type de permis de l'employé.
     */
    public String type_permis;

    /**
     * Constructeur de la classe VueEmploye avec tous les paramètres.
     * @param id_decheterie L'identifiant de la déchèterie.
     * @param nom_decheterie Le nom de la déchèterie.
     * @param id_employe L'identifiant de l'employé.
     * @param nom_employe Le nom de l'employé.
     * @param prenom_employe Le prénom de l'employé.
     * @param fonction_employe La fonction de l'employé.
     * @param date_naissance La date de naissance de l'employé.
     * @param date_debut_contrat La date de début de contrat de l'employé.
     * @param numero_telephone Le numéro de téléphone de l'employé.
     * @param type_permis Le type de permis de l'employé.
     */
    public VueEmploye(int id_decheterie, String nom_decheterie, String id_employe, String nom_employe, String prenom_employe, String fonction_employe, Date date_naissance, Date date_debut_contrat, String numero_telephone, String type_permis) {
        this.id_decheterie = id_decheterie;
        this.nom_decheterie = nom_decheterie;
        this.id_employe = id_employe;
        this.nom_employe = nom_employe;
        this.prenom_employe = prenom_employe;
        this.fonction_employe = fonction_employe;
        this.date_naissance = date_naissance;
        this.date_debut_contrat = date_debut_contrat;
        this.numero_telephone = numero_telephone;
        this.type_permis = type_permis;
    }

    /**
     * Constructeur par défaut de la classe VueEmploye.
     */
    public VueEmploye() {
    }
}