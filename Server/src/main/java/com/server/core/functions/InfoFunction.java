package com.server.core.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.server.core.Client;
import com.server.core.ServerSingleton;

public class InfoFunction implements Functionable {
    public InfoFunction() {}

    @Override
    public void doSomething(String[] args, Client client) {

        ResultSet rsp = null;
        try {
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rsp =
                    stmt.executeQuery(
                            "SELECT personnage.classe, personnage.race "
                                    + "FROM personnage "
                                    + "WHERE name='"
                                    + args[1]
                                    + "'");

            int id_race = 0, id_classe = 0;
            while (rsp.next()) {
                id_race = rsp.getInt("race");
                id_classe = rsp.getInt("classe");
            }
            rsp.close();
            ResultSet rspr =
                    stmt.executeQuery("SELECT race.name " + "FROM race " + "WHERE id=" + id_race);

            String race = null;

            while (rspr.next()) {
                race = rspr.getString("name");
            }

            rspr.close();
            ResultSet rspc =
                    stmt.executeQuery(
                            "SELECT classe.name " + "FROM classe " + "WHERE id=" + id_classe);

            String classe = null;
            while (rspc.next()) {
                classe = rspc.getString("name");
            }

            rspc.close();
            client.sendMessage("i;" + args[1] + ";" + race + ";" + classe);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
