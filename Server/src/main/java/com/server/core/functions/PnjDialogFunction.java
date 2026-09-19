package com.server.core.functions;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.gameplay.items.Equipement;
import com.gameplay.items.ObjetQuete;
import com.gameplay.items.SimpleItem;
import com.server.core.Client;
import com.server.core.ServerSingleton;

import com.server.entities.PNJ;
import com.server.entities.managers.EntitiesManager;
import com.server.gameplay.PNJ_discours;
import com.server.gameplay.Quete;
import com.server.gameplay.QueteObjectif;

public class PnjDialogFunction implements Functionable {
    public static ArrayList<String> types_concerned = new ArrayList<>();

    static {
        types_concerned.add("parler_a");
        types_concerned.add("don_objet_a");
    };

    public PnjDialogFunction() {}

    @Override
    public void doSomething(String[] args, Client c) {
        // Structure du message : pd; nom_pnj; id_reponse

        System.out.println(args[1]);
        // On essaye de trouver quel PNJ correspond au nom donné
        PNJ pnj = null;
        for (int i = 0; i < EntitiesManager.instance.getPnjs_manager().getPnjs().size(); i++) {
            if (EntitiesManager.instance
                    .getPnjs_manager()
                    .getPnjs()
                    .get(i)
                    .getNom()
                    .equals(args[1])) {
                pnj = EntitiesManager.instance.getPnjs_manager().getPnjs().get(i);
            }
        }
        // Si on ne l'a pas trouvé ou qu'il n'a pas encore été loadé, on demande au joueur de ne pas
        // jouer au hacker ...
        if (pnj == null || !c.getCompte().getCurrent_joueur().getLoaded_entities().contains(pnj)) {
            c.sendMessage(
                    "err;Vous essayez d'accéder à un pnj que vous n'avez même pas en vue ... Attention, le hackage est interdit, et puni de bannissement.");
            c.disconnect();
            return;
        }
        System.out.println(pnj.getNom());
        System.out.println(pnj.getDiscours().get(0).getDiscours());
        // On crée le discours correspondant à l'id
        PNJ_discours discours = null;

        // On crée une liste des quêtes en cours du joueur
        ArrayList<Quete> qlist =
                c.getCompte().getCurrent_joueur().getPerso().getQuetes_manager().getQuetes();
        // La liste suivante va contenir les ids de tous les objectifs de ces quêtes, ce qui va
        // permettre
        // de voir si le pnj répond en fonction des quêtes en cours
        ArrayList<Integer> idlist = new ArrayList<>();
        for (Quete q : qlist) {
            for (QueteObjectif obj : q.getObjectifs()) {
                if (!obj.isAccompli()) {
                    idlist.add(obj.getId());
                }
            }
        }
        // Si l'id envoyé vaut NULL cela signifie que le joueur aborde le PNJ
        if (args[2].equals("NULL")) {
            // On parcourt les dicours pour éventuellement tomber sur un qui correspond à un
            // objectif de quête
            for (PNJ_discours dis : pnj.getDiscours()) {
                if (idlist.contains(dis.getId_obj())) {
                    discours = dis;
                    break;
                }
            }
            // Si aucun n'a été trouvé on prend le premier par défaut
            if (discours == null) discours = pnj.getDiscours().get(0);
        }
        // Sinon, ils étaient déjà en discussion
        else {
            // Si une nouvelle quête apparaît
            if (pnj.getQuetes().containsKey(Integer.parseInt(args[2]))) {
                Quete quete = pnj.getQuetes().get(Integer.parseInt(args[2]));
                // On vérifie que le joueur ne l'a pas déjà
                boolean in = false;
                for (Quete q : qlist) {
                    if (q.getId() == quete.getId()) {
                        in = true;
                        break;
                    }
                }
                if (!in) {
                    c.getCompte()
                            .getCurrent_joueur()
                            .getPerso()
                            .getQuetes_manager()
                            .addQuete(quete);
                    // On crée le message de quête (assez explicite, obj veut dire objectif)
                    String message =
                            "que;new;" + pnj.getNom() + ";" + quete.getNom() + ";" + "obj;";
                    try {
                        String sql =
                                "INSERT INTO quetes_joueur VALUES(\""
                                        + c.getCompte().getCurrent_joueur().getPerso().getNom()
                                        + "\", "
                                        + quete.getId()
                                        + ", \""
                                        + pnj.getNom()
                                        + "\", 0)";
                        Statement stmt =
                                ServerSingleton.getInstance()
                                        .getDbConnexion()
                                        .getConnexion()
                                        .createStatement();
                        stmt.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < quete.getObjectifs().size() - 1; i++) {
                        message += quete.getObjectifs().get(i).getDescription() + ";";
                        message += quete.getObjectifs().get(i).getType() + ";";
                        message += quete.getObjectifs().get(i).getText_objectif() + ";";
                    }
                    c.sendMessage(message);
                    System.out.println("Message quête envoyé ! : " + message);
                }
            }

            // On récupère le discours correpondant à la réponse donnée
            PNJ_discours pd = pnj.getDiscoursWithId(Integer.parseInt(args[2]));
            System.out.println(Integer.parseInt(args[2]));
            // Si cette réponse n'est pas la dernière
            if (pd.getReponses().size() != 0) {
                // Idem que précédemment, on vérifie qu'il n'y a pas un objectif correspondant
                for (PNJ_discours dis : pd.getReponses()) {
                    if (idlist.contains(dis.getId_obj())) {
                        discours = dis;
                        break;
                    }
                }
                // Par défaut on renvoie le premier
                if (discours == null) discours = pd.getReponses().get(0);
            } else {
                // Si c'est la fin, on renvoie NULL pour dire que la conversation est finie
                c.sendMessage("pd;NULL");
                return;
            }
        }

        // -----------------------------TEST DES QUETES--------------------------------
        for (Quete quete : qlist) {
            System.out.println("New quete");
            if (!quete.isFinie()) {
                for (int u = 0; u < quete.getObjectifs().size() - 1; u++) {
                    QueteObjectif obj = quete.getObjectifs().get(u);
                    if (!obj.isAccompli()) {
                        /*if(types_concerned.contains(obj.getType()))
                        {*/
                        switch (obj.getType()) {
                            case "parler_a":
                                if (obj.getObjectifs().get(0).equals(pnj)) {
                                    try {
                                        // On ajoute dans la BDD l'objectif accompli
                                        String sql =
                                                "INSERT INTO quetes_joueur_objectifs_accomplis VALUES(\""
                                                        + c.getCompte()
                                                                .getCurrent_joueur()
                                                                .getPerso()
                                                                .getNom()
                                                        + "\", "
                                                        + quete.getId()
                                                        + ","
                                                        + obj.getId()
                                                        + ", NULL)";
                                        Statement stmt =
                                                ServerSingleton.getInstance()
                                                        .getDbConnexion()
                                                        .getConnexion()
                                                        .createStatement();
                                        stmt.executeUpdate(sql);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    // On le modifie pour le serveur
                                    obj.setAccompli(true);
                                    // On ajoute à l'inventaire du joueur les items
                                    c.getCompte()
                                            .getCurrent_joueur()
                                            .getPerso()
                                            .getInventaire()
                                            .getItems()
                                            .addAll(obj.getRecompenses());
                                    for (SimpleItem item : obj.getRecompenses()) {
                                        System.out.println("New item");
                                        // Ce code vérifiait que l'objet était ou non dans
                                        // l'inventaire, ce qui me paraîssait bizarre ... Je l'ai
                                        // commenté.
                                        /*boolean alreadyin = false;
                                        try {
                                            String sql = "SELECT * FROM inventaire WHERE objet = \""+item.getNom()+"\"";
                                            Statement stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
                                            ResultSet rs = stmt.executeQuery(sql);
                                            while(rs.next())
                                            {
                                                alreadyin = true;
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }*/
                                        boolean alreadyin = false;
                                        if (!alreadyin) {
                                            try {
                                                String sql =
                                                        "INSERT INTO inventaire VALUES(\""
                                                                + c.getCompte()
                                                                        .getCurrent_joueur()
                                                                        .getPerso()
                                                                        .getNom()
                                                                + "\", \""
                                                                + item.getNom()
                                                                + "\", 1)";
                                                Statement stmt =
                                                        ServerSingleton.getInstance()
                                                                .getDbConnexion()
                                                                .getConnexion()
                                                                .createStatement();
                                                stmt.executeUpdate(sql);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            // Modification de l'inventaire
                                            if (item instanceof ObjetQuete)
                                                c.sendMessage(
                                                        "in;new;"
                                                                + item.getNom()
                                                                + ";"
                                                                + item.getDescription()
                                                                + ";objet_quete;;");
                                            if (item instanceof Equipement)
                                                c.sendMessage(
                                                        "in;new;"
                                                                + item.getNom()
                                                                + ";"
                                                                + item.getDescription()
                                                                + ";equipement;"
                                                                + ((Equipement) item).getType());
                                        }
                                    }
                                    c.sendMessage(
                                            "que;update;"
                                                    + quete.getId()
                                                    + "acc;obj;"
                                                    + obj.getId());
                                }
                                break;
                            case "don_objet_a":
                                if (obj.getObjectifs().get(0).equals(pnj)
                                        && c.getCompte()
                                                .getCurrent_joueur()
                                                .getPerso()
                                                .getInventaire()
                                                .getItems()
                                                .contains(obj.getObjectifs().get(1))) {
                                    try {
                                        String sql =
                                                "INSERT INTO quetes_joueur_objectifs_accomplis VALUES(\""
                                                        + c.getCompte()
                                                                .getCurrent_joueur()
                                                                .getPerso()
                                                                .getNom()
                                                        + "\", "
                                                        + quete.getId()
                                                        + ","
                                                        + obj.getId()
                                                        + ", NULL)";
                                        Statement stmt =
                                                ServerSingleton.getInstance()
                                                        .getDbConnexion()
                                                        .getConnexion()
                                                        .createStatement();
                                        stmt.executeUpdate(sql);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        String sql =
                                                "DELETE FROM inventaire WHERE objet=\""
                                                        + ((SimpleItem) obj.getObjectifs().get(1))
                                                                .getNom()
                                                        + "\"";
                                        Statement stmt =
                                                ServerSingleton.getInstance()
                                                        .getDbConnexion()
                                                        .getConnexion()
                                                        .createStatement();
                                        stmt.executeUpdate(sql);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    c.sendMessage(
                                            "in;del;"
                                                    + ((SimpleItem) obj.getObjectifs().get(1))
                                                            .getNom());
                                    obj.setAccompli(true);
                                    c.getCompte()
                                            .getCurrent_joueur()
                                            .getPerso()
                                            .getInventaire()
                                            .getItems()
                                            .remove(obj.getObjectifs().get(1));
                                    c.getCompte()
                                            .getCurrent_joueur()
                                            .getPerso()
                                            .getInventaire()
                                            .getItems()
                                            .addAll(obj.getRecompenses());
                                    c.sendMessage(
                                            "que;update;"
                                                    + quete.getId()
                                                    + "acc;obj;"
                                                    + obj.getId());
                                } else {
                                    u = quete.getObjectifs().size();
                                }
                                break;
                        }
                    }
                }
                boolean finie = true;
                for (int i = 0; i < quete.getObjectifs().size() - 1; i++) {
                    if (!quete.getObjectifs().get(i).isAccompli()) {
                        finie = false;
                        break;
                    }
                }
                quete.setFinie(finie);
                if (finie) {
                    try {
                        String sql =
                                "INSERT INTO quetes_joueur_objectifs_accomplis VALUES(\""
                                        + c.getCompte().getCurrent_joueur().getPerso().getNom()
                                        + "\", "
                                        + quete.getId()
                                        + ","
                                        + quete.getObjectifs()
                                                .get(quete.getObjectifs().size() - 1)
                                                .getId()
                                        + ", NULL)";
                        Statement stmt =
                                ServerSingleton.getInstance()
                                        .getDbConnexion()
                                        .getConnexion()
                                        .createStatement();
                        stmt.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    try {
                        String sql =
                                "UPDATE quetes_joueur SET finie=1 WHERE id_quete=" + quete.getId();
                        Statement stmt =
                                ServerSingleton.getInstance()
                                        .getDbConnexion()
                                        .getConnexion()
                                        .createStatement();
                        stmt.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Le message envoyé
        String envoi = "pd;";
        // Si ce n'est pas le début de la conversation, on met l'id de la réponse précédente
        // TODO pourquoi ?
        if (!args[2].equals("NULL")) envoi += Integer.parseInt(args[2]) + ";";
        envoi += discours.getId() + ";";
        envoi += discours.getDiscours() + ";";
        for (int i = 0; i < discours.getReponses().size(); i++) {
            envoi += discours.getReponses().get(i).getId() + ";";
            envoi += discours.getId() + ";";
            envoi += discours.getReponses().get(i).getDiscours() + ";";
        }
        c.sendMessage(envoi);
    }
}
