package com.server.core.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.server.core.Client;
import com.server.core.ServerSingleton;

public class PersosInformationsFunction implements Functionable {

    @Override
    public void doSomething(String[] args, Client c) {
        ResultSet rsp;
        Statement stmt;
        try {
            stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rsp =
                    stmt.executeQuery(
                            "SELECT name,race,classe,posx,posy,orientation "
                                    + "FROM personnage WHERE compte = '"
                                    + c.getCompte().getName()
                                    + "'");
        } catch (SQLException e) {
            throw new RuntimeException("Issue with executing query (syntax ?) for finding player");
        }
        String nom, race = null, classe = null, ori = null;
        int posx = 0, posy = 0;
        try {
            String tosend = "ci;";
            while (rsp.next()) {
                nom = rsp.getString("name");
                race = rsp.getString("race");
                classe = rsp.getString("classe");
                posx = rsp.getInt("posx");
                posy = rsp.getInt("posy");
                ori = rsp.getString("orientation");
                tosend +=
                        "new;" + nom + ";" + race + ";" + classe + ";" + posx + ";" + posy + ";"
                                + ori + ";";
            }
            System.out.println(tosend);
            c.sendMessage(tosend);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            rsp.close();
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
