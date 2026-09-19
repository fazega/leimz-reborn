package com.server.core.functions;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import com.server.gameplay.Caracteristique;
import com.server.core.Client;
import com.server.core.ClientsManager;
import com.server.core.ServerSingleton;
import com.server.core.functions.Functionable;

/** @author fazega */
public class AttackFunction implements Functionable {

    public AttackFunction() {}

    @Override
    public void doSomething(String[] args, Client c) {

        ResultSet rsp = null;
        try {
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            rsp =
                    stmt.executeQuery(
                            "SELECT sorts_classe.value_min, sorts_race.value_min, sorts_race.value_max, sorts_classe.value_max "
                                    + "FROM sorts_classe, sorts_race, classe_sort, race_sort, personnage "
                                    + "WHERE personnage.name=\""
                                    + c.getCompte().getCurrent_joueur().getPerso().getNom()
                                    + "\""
                                    + " AND (sorts_classe.nom=\""
                                    + args[2]
                                    + "\" OR sorts_race.nom=\""
                                    + args[2]
                                    + "\") "
                                    + " AND (classe_sort.sort=\""
                                    + args[2]
                                    + "\" OR race_sort.sort=\""
                                    + args[2]
                                    + "\") "
                                    + " AND race_sort.race=personnage.race"
                                    + " AND classe_sort.classe=personnage.classe");

            int value_min = 0, value_max = 0;
            while (rsp.next()) {
                if (rsp.getInt(1) != 0) {
                    value_min = rsp.getInt(1);
                } else {
                    value_min = rsp.getInt(2);
                }

                if (rsp.getInt(3) != 0) {
                    value_max = rsp.getInt(3);
                } else {
                    value_max = rsp.getInt(4);
                }
            }

            Random random = new Random();
            int degats = value_min + random.nextInt(value_max - value_min);

            Client cible = ClientsManager.instance.getClient(args[1]);
            int vie =
                    cible.getCompte()
                                    .getCurrent_joueur()
                                    .getPerso()
                                    .getCaracs_values()
                                    .get(Caracteristique.VIE)
                            - degats;
            cible.getCompte()
                    .getCurrent_joueur()
                    .getPerso()
                    .getCaracs_values()
                    .put(Caracteristique.VIE, vie);

            rsp.close();
            String toSendToSender =
                    "s;j;"
                            + cible.getCompte().getCurrent_joueur().getPerso().getNom()
                            + ";vie;"
                            + cible.getCompte()
                                    .getCurrent_joueur()
                                    .getPerso()
                                    .getCaracs_values()
                                    .get(Caracteristique.VIE);
            c.sendMessage(toSendToSender);

            String toSendToReceiver =
                    "a;j;"
                            + c.getCompte().getCurrent_joueur().getPerso().getNom()
                            + ";"
                            + args[2]
                            + ";"
                            + degats;
            cible.sendMessage(toSendToReceiver);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
