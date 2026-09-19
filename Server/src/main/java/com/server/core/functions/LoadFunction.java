package com.server.core.functions;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.gameplay.items.Equipement;
import com.gameplay.items.SimpleItem;
import com.server.entities.Entity;
import com.server.entities.Joueur;
import com.server.entities.Orientation;
import com.server.entities.PNJ;
import com.server.gameplay.Caracteristique;
import com.server.gameplay.Classe;
import com.server.gameplay.Inventaire;
import com.server.gameplay.Quete;
import com.server.gameplay.Race;
import com.server.entities.Personnage;
import com.server.gameplay.managers.ItemsManager;
import com.server.map.Tile;
import com.server.map.managers.MapManager;
import com.server.core.Client;
import com.server.core.GlobalConstant;
import com.server.core.ServerSingleton;
import com.server.entities.managers.EntitiesManager;

public class LoadFunction implements Functionable {
    public LoadFunction() {}

    // fonction repondant aux requetes du client
    @Override
    public void doSomething(String[] args, Client client) {
        switch (args[1]) {
            case "j":
                askJoueur(client, args);
                break;
            case "ent":
                askEntities(client);
                break;
            case "tt":
                askTypeTiles(client, args[2], args[1]);
                break;
            case "map":
                askMap(client, args[1]);
                break;
            case "mon":
                askMonster(client);
                break;
            default:
                throw new RuntimeException("Unimplemented");
        }
    }

    private void askJoueur(Client client, String[] args) {
        switch (args[2]) {
            case "i":
                getInfos(client, args, args[2]);
                break;
            case "rc":
                askRaceCaracteristic(client, args[2]);
                break;
            case "cc":
                askClassCaracteristic(client, args[2]);
                break;
            case "jc":
                askPlayerCaracteristic(client, args[2]);
                break;
            case "jcv":
                askPlayerCaracteristicValue(client, args[2]);
                break;
            case "rs":
                askRaceSort(client, args[2]);
                break;
            case "cs":
                askClassSort(client, args[2]);
                break;
            case "in":
                askInventory(client, args[2]);
                break;
            case "q":
                askQuetes(client, args[2]);
                break;
        }
    }

    public void getInfos(Client client, String[] args, String tag) {
        client.getCompte()
                .setCurrent_joueur(
                        new Joueur(
                                new Personnage(args[3]),
                                MapManager.instance.getEntire_map()
                                        .getGrille()[Integer.parseInt(args[6])][
                                        Integer.parseInt(args[7])],
                                Orientation.BAS));
        client.getCompte().getCurrent_joueur().getPerso().getRace().setNom(args[4]);
        client.getCompte().getCurrent_joueur().getPerso().getClasse().setNom(args[5]);
        EntitiesManager.instance
                .getPlayers_manager()
                .addNewPlayer(client.getCompte().getCurrent_joueur());
    }

    public void askQuetes(Client client, String tag) {
        ResultSet rs = null;
        try {
            String sql =
                    "SELECT quetes_joueur.commanditaire, quetes_joueur.id_quete, quetes_joueur.finie "
                            + "FROM personnage, quetes_joueur "
                            + "WHERE quetes_joueur.nom_perso = personnage.name "
                            + "AND personnage.name='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";

            while (rs.next()) {
                Quete quete = null;
                PNJ pnj =
                        EntitiesManager.instance
                                .getPnjs_manager()
                                .getPnj(rs.getString("quetes_joueur.commanditaire"));
                for (Integer id : pnj.getQuetes().keySet()) {
                    if (pnj.getQuetes().get(id).getId() == rs.getInt("quetes_joueur.id_quete")) {
                        quete = pnj.getQuetes().get(id);
                    }
                }
                rc += "new;";
                rc += rs.getString("quetes_joueur.commanditaire") + ";";
                rc += quete.getNom() + ";";
                quete.setFinie(rs.getBoolean("quetes_joueur.finie"));
                String sql_o =
                        "SELECT quetes_joueur_objectifs_accomplis.id_objectif "
                                + "FROM quetes_joueur_objectifs_accomplis "
                                + "WHERE quetes_joueur_objectifs_accomplis.joueur = \""
                                + client.getCompte().getCurrent_joueur().getPerso().getNom()
                                + "\" "
                                + "AND quetes_joueur_objectifs_accomplis.id_quete = "
                                + quete.getId();
                Statement stmt_o =
                        ServerSingleton.getInstance()
                                .getDbConnexion()
                                .getConnexion()
                                .createStatement();
                ResultSet rs_o = stmt_o.executeQuery(sql_o);
                ArrayList<Integer> id_accomplis = new ArrayList<>();
                while (rs_o.next()) {
                    id_accomplis.add(rs_o.getInt("quetes_joueur_objectifs_accomplis.id_objectif"));
                }
                // On va jusqu'à l'avant-dernier pour ne pas prendre le dernier, fictif, qui est fin
                // de quête.
                for (int i = 0; i < quete.getObjectifs().size() - 1; i++) {
                    if (id_accomplis.contains(quete.getObjectifs().get(i).getId())) {
                        quete.getObjectifs().get(i).setAccompli(true);
                        rc +=
                                i
                                        + ";"
                                        + quete.getObjectifs().get(i).getDescription()
                                        + ";"
                                        + quete.getObjectifs().get(i).getType()
                                        + ";"
                                        + quete.getObjectifs().get(i).getText_objectif()
                                        + ";1;";
                    } else
                        rc +=
                                i
                                        + ";"
                                        + quete.getObjectifs().get(i).getDescription()
                                        + ";"
                                        + quete.getObjectifs().get(i).getType()
                                        + ";"
                                        + quete.getObjectifs().get(i).getText_objectif()
                                        + ";0;";
                }
                client.getCompte()
                        .getCurrent_joueur()
                        .getPerso()
                        .getQuetes_manager()
                        .addQuete(quete);
            }

            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Quetes");
        }
    }

    public void askRaceCaracteristic(Client client, String tag) {
        ResultSet rs = null;
        try {
            Race race = client.getCompte().getCurrent_joueur().getPerso().getRace();
            String sql =
                    "SELECT caracteristiques_race.caracteristique, caracteristiques_race.value "
                            + "FROM caracteristiques_race,personnage "
                            + "WHERE caracteristiques_race.race=personnage.race "
                            + "AND personnage.name='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            HashMap<Caracteristique, Integer> caracs = new HashMap<>();
            while (rs.next()) {
                rc += rs.getString("caracteristiques_race.caracteristique") + ";";
                rc += rs.getInt("caracteristiques_race.value") + ";";
                caracs.put(
                        Caracteristique.valueOf(
                                rs.getString("caracteristiques_race.caracteristique")
                                        .toUpperCase()),
                        rs.getInt("caracteristiques_race.value"));
            }
            race.setCarac(caracs);
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Race caracteristic");
        }
    }

    public void askClassCaracteristic(Client client, String tag) {
        ResultSet rs;
        try {
            Classe classe = client.getCompte().getCurrent_joueur().getPerso().getClasse();
            String sql =
                    "SELECT caracteristiques_classe.caracteristique, caracteristiques_classe.value "
                            + "FROM caracteristiques_classe,personnage "
                            + "WHERE caracteristiques_classe.classe=personnage.classe "
                            + "AND personnage.name='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            HashMap<Caracteristique, Integer> caracs = new HashMap<>();
            while (rs.next()) {
                rc += rs.getString("caracteristiques_classe.caracteristique") + ";";
                rc += rs.getInt("caracteristiques_classe.value") + ";";
                caracs.put(
                        Caracteristique.valueOf(
                                rs.getString("caracteristiques_classe.caracteristique")
                                        .toUpperCase()),
                        rs.getInt("caracteristiques_classe.value"));
            }
            classe.setCaracs(caracs);
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Class caracteristic");
        }
    }

    public void askPlayerCaracteristic(Client client, String tag) {
        ResultSet rs;
        try {
            String sql =
                    "SELECT caracteristique, value "
                            + "FROM caracteristiques_joueur "
                            + "WHERE nom_joueur='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            HashMap<Caracteristique, Integer> caracs = new HashMap<>();
            while (rs.next()) {
                rc += rs.getString("caracteristique") + ";";
                rc += rs.getInt("value") + ";";
                caracs.put(
                        Caracteristique.valueOf(rs.getString("caracteristique").toUpperCase()),
                        rs.getInt("value"));
            }
            client.getCompte().getCurrent_joueur().getPerso().setCaracs(caracs);
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Player Caracteristic");
        }
    }

    public void askPlayerCaracteristicValue(Client client, String tag) {
        ResultSet rs;
        // Valeurs des caracteristiques du joueur
        try {
            String sql =
                    "SELECT caracteristique, current_value "
                            + "FROM caracteristiques_joueur "
                            + "WHERE nom_joueur='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            HashMap<Caracteristique, Integer> caracs = new HashMap<>();
            while (rs.next()) {
                rc += rs.getString("caracteristique") + ";";
                rc += rs.getInt("current_value") + ";";
                caracs.put(
                        Caracteristique.valueOf(rs.getString("caracteristique").toUpperCase()),
                        rs.getInt("current_value"));
            }
            client.getCompte().getCurrent_joueur().getPerso().setCaracs_values(caracs);
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Player Caracteristic values");
        }
    }

    public void askRaceSort(Client client, String tag) {
        ResultSet rs;
        // sorts de race
        try {
            String sql =
                    "SELECT sorts_race.nom, sorts_race.value_min, sorts_race.value_max, sorts_race.description"
                            + " FROM sorts_race,personnage,race_sort "
                            + "WHERE sorts_race.nom=race_sort.sort "
                            + "AND race_sort.race=personnage.race "
                            + "AND personnage.name='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            while (rs.next()) {
                rc += rs.getString("sorts_race.nom") + ";";
                rc += rs.getInt("sorts_race.value_min") + ";";
                rc += rs.getInt("sorts_race.value_max") + ";";
                rc += rs.getString("sorts_race.description") + ";";
            }
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("race sort");
        }
    }

    public void askClassSort(Client client, String tag) {
        ResultSet rs;
        // sorts de classe
        try {
            String sql =
                    "SELECT sorts_classe.nom, sorts_classe.value_min, sorts_classe.value_max, sorts_classe.description"
                            + " FROM sorts_classe,personnage,classe_sort "
                            + "WHERE sorts_classe.nom=classe_sort.sort "
                            + "AND classe_sort.classe=personnage.classe "
                            + "AND personnage.name='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            while (rs.next()) {
                rc += rs.getString("sorts_classe.nom") + ";";
                rc += rs.getInt("sorts_classe.value_min") + ";";
                rc += rs.getInt("sorts_classe.value_max") + ";";
                rc += rs.getString("sorts_classe.Description") + ";";
            }
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Class sort");
        }
    }

    public void askInventory(Client client, String tag) {
        ResultSet rs;
        try {
            String sql =
                    "SELECT inventaire.objet "
                            + "FROM inventaire "
                            + "WHERE inventaire.joueur='"
                            + client.getCompte().getCurrent_joueur().getPerso().getNom()
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            ArrayList<SimpleItem> items = new ArrayList<>();
            while (rs.next()) {
                SimpleItem item = ItemsManager.instance.getItem(rs.getString("inventaire.objet"));
                items.add(item);
                if (item instanceof Equipement) {
                    rc += item.getNom() + ";";
                    rc += item.getDescription() + ";";
                    rc += "equipement" + ";";
                    rc += ((Equipement) item).getType() + ";";
                    /*for(Caracteristique carac : item.getEffets().keySet())
                    {
                        rc += carac + ";";
                        rc += item.getEffets().get(carac) + ";";
                    }*/
                } else {
                    rc += item.getNom() + ";";
                    rc += item.getDescription() + ";";
                    rc += "objet_quete" + ";";
                    rc += ";";
                }
            }
            client.getCompte().getCurrent_joueur().getPerso().setInventaire(new Inventaire(items));
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void askEntities(Client client) {
        Tile[][] grille =
                MapManager.instance.getTilesAutour(
                        client.getCompte().getCurrent_joueur().getTile().getPos_x(),
                        client.getCompte().getCurrent_joueur().getTile().getPos_y(),
                        GlobalConstant.nbCaseNear);

        ArrayList<Tile> tiles_to_load = new ArrayList<Tile>();
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                for (int u = 0;
                        u < client.getCompte().getCurrent_joueur().getBefore_loaded().size();
                        u++) {
                    if (grille[i][j]
                            != client.getCompte().getCurrent_joueur().getBefore_loaded().get(u)) {
                        tiles_to_load.add(grille[i][j]);
                    }
                    client.getCompte().getCurrent_joueur().getBefore_loaded().set(u, grille[i][j]);
                }
            }
        }

        ArrayList<Entity> entities_to_load = new ArrayList<Entity>();
        // Un peu gore, Ã  amÃ©liorer
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                for (int k = 0; k < EntitiesManager.instance.getEntities().size(); k++) {
                    if (EntitiesManager.instance
                            .getEntities()
                            .get(k)
                            .getTile()
                            .equals(grille[i][j])) {
                        entities_to_load.add(EntitiesManager.instance.getEntities().get(k));
                    }
                }
            }
        }

        ArrayList<Entity> already_loaded =
                client.getCompte().getCurrent_joueur().getLoaded_entities();
        // Idem
        for (int k = 0; k < already_loaded.size(); k++) {
            for (int i = 0; i < entities_to_load.size(); i++) {
                for (int u = 0; u < tiles_to_load.size(); u++) {
                    if (entities_to_load.get(i).getTile().equals(tiles_to_load.get(u))) {
                        if (!already_loaded.get(k).equals(entities_to_load.get(i))) {
                            already_loaded.add(entities_to_load.get(i));
                            if (entities_to_load.get(i) instanceof PNJ) {
                                loadPnj(client, (PNJ) entities_to_load.get(i));
                            } else if (entities_to_load.get(i) instanceof Joueur) {
                                // Si le client dÃ©tectÃ© n'est pas celui qui fait la demande
                                if (entities_to_load.get(i)
                                        != client.getCompte().getCurrent_joueur()) {
                                    loadPerso(client, (Joueur) entities_to_load.get(i));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void loadPerso(Client client, Joueur joueur) {
        String rc = "lo;ent;j;";
        rc += joueur.getPerso().getNom() + ";";
        rc += joueur.getPerso().getRace().getNom() + ";";
        rc += joueur.getPerso().getClasse().getNom() + ";";
        rc += (int) joueur.getTile().getPos_x() + ";";
        rc += (int) joueur.getTile().getPos_y() + ";";
        rc += joueur.stringOrientation() + ";";
        client.sendMessage(rc);
    }

    public void loadPnj(Client client, PNJ pnj) {
        String rc = "lo;ent;pnj;";
        rc += (int) pnj.getTile().getPos_x() + ";";
        rc += (int) pnj.getTile().getPos_y() + ";";
        rc += pnj.getNom();
        client.sendMessage(rc);
    }

    public void askTypeTiles(Client client, String name, String tag) {
        ResultSet rs;
        // Chargement des informations d'une tile
        try {
            String sql =
                    "SELECT nom, image, collidable, base_x, base_y "
                            + "FROM tiles_map "
                            + "WHERE nom='"
                            + name
                            + "'";
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            while (rs.next()) {
                rc += rs.getString("nom") + ";";
                rc += rs.getString("image") + ";";
                rc += rs.getBoolean("collidable") + ";";
                rc += rs.getInt("base_x") + ";";
                rc += rs.getInt("base_y") + ";";
                rc += "0";
            }
            if (rc.equals(tag + ";")) {
                sql =
                        "SELECT nom, image, collidable, base_x, base_y "
                                + "FROM tiles_map_content "
                                + "WHERE nom='"
                                + name
                                + "'";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    rc += rs.getString("nom") + ";";
                    rc += rs.getString("image") + ";";
                    rc += rs.getBoolean("collidable") + ";";
                    rc += rs.getInt("base_x") + ";";
                    rc += rs.getInt("base_y") + ";";
                    rc += "1";
                }
            }
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Type tiles");
        }
    }

    public void askMap(Client client, String tag) {
        ResultSet rs;
        // Chargement des informations de la map
        try {
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            String sql = "SELECT MAX(map.x), MAX(map.y)" + "FROM map ";
            rs = stmt.executeQuery(sql);
            String rc = tag + ";";
            rs.next();
            rc += rs.getInt(1) + ";";
            rc += rs.getInt(2) + ";";
            client.sendMessage(rc);

            sql = "SELECT id, x, y, type " + "FROM map_groups WHERE isbase=1 ";
            rs = stmt.executeQuery(sql);
            String s = "map;";
            while (rs.next()) {
                s += rs.getInt("id") + ";";
                s += rs.getInt("x") + ";";
                s += rs.getInt("y") + ";";
                s += rs.getString("type") + ";";
            }
            client.sendMessage(s);

            sql = "SELECT x, y, type1, type2, id_groupe " + "FROM map ";
            rs = stmt.executeQuery(sql);
            int h = 0, packetSize = 500;
            String toSend = "map;" + packetSize + ";";
            while (rs.next()) {
                String m = "";
                m += rs.getInt("x") + ";";
                m += rs.getInt("y") + ";";
                m += rs.getString("type1") + ";";
                m += rs.getString("type2") + ";";
                m += rs.getInt("id_groupe") + ";";
                toSend += m;
                h++;
                if (h % packetSize == 0) {
                    client.sendMessage(toSend);
                    toSend = "map;" + packetSize + ";";
                    // System.out.println(toSend);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Map");
        }
    }

    public void askMonster(Client client) {
        ResultSet rs;
        // Chargement des informations des monstres
        try {
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            String rc = "";
            String sql = "SELECT monster.name " + "FROM monster ";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rc += rs.getString("monster.name") + ";";
            }
            client.sendMessage(rc);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Monster");
        }
    }
}
