package com.server.core.functions;

import java.sql.SQLException;

import java.sql.Statement;
import com.server.entities.Joueur;
import com.server.map.Tile;
import com.server.map.managers.MapManager;
import com.server.core.Client;
import com.server.core.ServerSingleton;

public class StateFunction implements Functionable {

    public StateFunction() {}

    @Override
    public void doSomething(String[] args, Client client) {
        if (args[1].equals("pos")) {
            try {
                Tile tile =
                        MapManager.instance.getTileReal(
                                Float.parseFloat(args[2]), Float.parseFloat(args[3]));
                Statement stmt =
                        ServerSingleton.getInstance()
                                .getDbConnexion()
                                .getConnexion()
                                .createStatement();
                stmt.executeUpdate(
                        "UPDATE personnage "
                                + " SET posx="
                                + tile.getPos_x()
                                + " , posy="
                                + tile.getPos_y()
                                + " WHERE name='"
                                + client.getCompte().getCurrent_joueur().getPerso().getNom()
                                + "'");
                client.getCompte().getCurrent_joueur().setPos_real_x(Float.parseFloat(args[2]));
                client.getCompte().getCurrent_joueur().setPos_real_y(Float.parseFloat(args[3]));
                if (tile != client.getCompte().getCurrent_joueur().getTile())
                    ServerSingleton.getInstance()
                            .printMessage(
                                    "Position du joueur "
                                            + client.getCompte()
                                                    .getCurrent_joueur()
                                                    .getPerso()
                                                    .getNom()
                                            + ": "
                                            + tile.getPos_x()
                                            + " -- "
                                            + tile.getPos_y());
                client.getCompte().getCurrent_joueur().setTile(tile);
                client.getCompte()
                        .getCurrent_joueur()
                        .setOrientation(Joueur.parseStringOrientation(args[4]));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args[1].equals("vie")) {
            // Check
        }
    }
}
