package app.Class;

/**
 * Classe DropDown pour représenter une option dans une liste déroulante.
 */
public class DropDown {
    /**
     * Le nom de l'option.
     */
    public String nom;

    /**
     * L'identifiant de l'option.
     */
    public String id;

    /**
     * Constructeur de la classe DropDown avec tous les paramètres.
     * @param nom Le nom de l'option.
     * @param id L'identifiant de l'option.
     */
    public DropDown(String nom, String id) {
        this.nom = nom;
        this.id = id;
    }

    /**
     * Constructeur par défaut de la classe DropDown.
     */
    public DropDown() {
    }
}