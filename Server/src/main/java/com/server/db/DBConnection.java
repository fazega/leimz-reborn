
package com.server.db;
import java.sql.*;

/**
 *
 * @author FaZeGa
 */
public class DBConnection
{
    Connection connexion;

    public DBConnection() throws SQLException
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connexion = DriverManager.getConnection(System.getProperty("leimz.db.url"), System.getProperty("leimz.db.user"), System.getProperty("leimz.db.password"));
        } catch (Exception e) {
            System.out.print("Impossible to connect with the database");
            System.exit(0);
        }
    }

    public Connection getConnexion() {
        return connexion;
    }

    public void setConnexion(Connection connexion) {
        this.connexion = connexion;
    }
}
