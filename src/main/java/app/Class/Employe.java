package app.Class;

import java.sql.Date;

/**
 * Classe Employe pour représenter un employé.
 */
public class Employe {
    /**
     * L'identifiant de connexion de l'employé.
     */
    public String idlogin;

    /**
     * Le mot de passe de connexion de l'employé.
     */
    public String mdplogin;

    /**
     * Le nom de l'employé.
     */
    public String nom;

    /**
     * Le prénom de l'employé.
     */
    public String prenom;

    /**
     * La date de naissance de l'employé.
     */
    public Date datenaissance;

    /**
     * La date de début de contrat de l'employé.
     */
    public Date datedebutcontrat;

    /**
     * La fonction de l'employé.
     */
    public String fk_fonction;

    /**
     * Le numéro de téléphone de l'employé.
     */
    public String numtelephone;

    /**
     * Le type de permis de l'employé.
     */
    public String typepermis;

    /**
     * L'identifiant de l'adresse de l'employé.
     */
    public int fk_adresse;

    /**
     * L'identifiant de la déchèterie de l'employé.
     */
    public int fk_decheterie;

    /**
     * Constructeur par défaut de la classe Employe.
     */
    public Employe() {
    }

    /**
     * Constructeur de la classe Employe avec tous les paramètres.
     * @param idlogin L'identifiant de connexion de l'employé.
     * @param mdplogin Le mot de passe de connexion de l'employé.
     * @param nom Le nom de l'employé.
     * @param prenom Le prénom de l'employé.
     * @param datenaissance La date de naissance de l'employé.
     * @param datedebutcontrat La date de début de contrat de l'employé.
     * @param fk_fonction La fonction de l'employé.
     * @param numtelephone Le numéro de téléphone de l'employé.
     * @param typepermis Le type de permis de l'employé.
     * @param fk_adresse L'identifiant de l'adresse de l'employé.
     * @param fk_decheterie L'identifiant de la déchèterie de l'employé.
     */
    public Employe(String idlogin, String mdplogin, String nom, String prenom, Date datenaissance, Date datedebutcontrat, String fk_fonction, String numtelephone, String typepermis, int fk_adresse, int fk_decheterie) {
        this.idlogin = idlogin;
        this.mdplogin = mdplogin;
        this.nom = nom;
        this.prenom = prenom;
        this.datenaissance = datenaissance;
        this.datedebutcontrat = datedebutcontrat;
        this.fk_fonction = fk_fonction;
        this.typepermis = typepermis;
        this.numtelephone = numtelephone;
        this.fk_adresse = fk_adresse;
        this.fk_decheterie = fk_decheterie;
    }
}