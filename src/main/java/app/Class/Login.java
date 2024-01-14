package app.Class;

/**
 * Classe Login pour représenter les informations de connexion d'un utilisateur.
 */
public class Login {
    /**
     * L'identifiant de connexion de l'utilisateur.
     */
    public String idlogin;

    /**
     * Le mot de passe de connexion de l'utilisateur.
     */
    public String mdplogin;

    /**
     * Constructeur de la classe Login avec tous les paramètres.
     * @param idlogin L'identifiant de connexion de l'utilisateur.
     * @param mdplogin Le mot de passe de connexion de l'utilisateur.
     */
    public Login(String idlogin, String mdplogin) {
        this.idlogin = idlogin;
        this.mdplogin = mdplogin;
    }

    /**
     * Constructeur par défaut de la classe Login.
     */
    public Login() {
    }
}