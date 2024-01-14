package app;

import java.sql.*;

/**
 * Classe Database pour la gestion de la connexion à la base de données.
 */
public class Database {
    // URL de la base de données
    private static final String DATABASE_URL = "jdbc:postgresql://host.docker.internal:5432/bdr?currentSchema=gestion_decheterie";
    // Utilisateur de la base de données
    private static final String DATABASE_USER = "bdr";
    // Mot de passe de la base de données
    private static final String DATABASE_PASSWORD = "bdr";

    // Connexion à la base de données
    private static Connection connection;

    /**
     * Constructeur privé pour initialiser la connexion à la base de données.
     * @throws SQLException si une erreur de base de données se produit
     * @throws ClassNotFoundException si le pilote JDBC n'est pas trouvé
     */
    private Database() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    /**
     * Exécute une requête SQL de type SELECT.
     * @param query la requête SQL à exécuter
     * @return un ResultSet contenant les résultats de la requête
     * @throws SQLException si une erreur de base de données se produit
     * @throws ClassNotFoundException si le pilote JDBC n'est pas trouvé
     */
    public static ResultSet executeQuery(String query)throws SQLException, ClassNotFoundException {
        if (connection == null) {
            new Database();
        }

        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Exécute une requête SQL de type INSERT, UPDATE ou DELETE.
     * @param query la requête SQL à exécuter
     * @return le nombre de lignes affectées par la requête
     * @throws SQLException si une erreur de base de données se produit
     * @throws ClassNotFoundException si le pilote JDBC n'est pas trouvé
     */
    public static int executeUpdate(String query)throws SQLException, ClassNotFoundException {
        if (connection == null) {
            new Database();
        }

        try {
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Récupère la connexion à la base de données.
     * @return la connexion à la base de données
     * @throws SQLException si une erreur de base de données se produit
     * @throws ClassNotFoundException si le pilote JDBC n'est pas trouvé
     */
    public static Connection getConnection()throws SQLException, ClassNotFoundException {
        if (connection == null) {
            new Database();
        }
        return connection;
    }
}