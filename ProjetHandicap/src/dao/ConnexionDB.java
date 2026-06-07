package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/gestion_handicap";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connexion reussie a MySQL !");
        return cn;
    }
}
