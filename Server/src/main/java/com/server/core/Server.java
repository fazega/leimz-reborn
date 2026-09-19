package com.server.core;

import com.server.db.DBConnection;
import com.server.entities.managers.EntitiesManager;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author fazega
 * @author kratisto
 */
public class Server {
    private ServerSocket ss = null;

    private DBConnection dbConnexion;

    public void start() {
        // On initialise le serveur
        initServer();
        // On lance le gestionnaire de commandes
        try {
            dbConnexion = new DBConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        new Load();
        printMessage("Serveur pret.\n");
        printMessage("En attente d'un joueur ...");
        // On attend la connexion des joueurs
        waitPlayers();
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    private void initServer() {

        printMessage("Lancement du serveur de Leimz");
        printMessage("-----------");
        try {
            // On lance le socket
            ss =
                    new ServerSocket(
                            Integer.getInteger("leimz.port", 1500),
                            50,
                            java.net.InetAddress.getByName("127.0.0.1"));
            printMessage("Lancement reussi");
        } catch (Exception e) {
            // On informe de l'echec, on affiche l'erreur et on eteint le serveur
            printMessage("Lancement echoue");
            printMessage("Message d'erreur :");
            e.printStackTrace();
            System.exit(0);
        }
        printMessage("");

        new ClientsManager();
    }

    private void waitPlayers() {
        Socket player;
        while (true) // attente en boucle de connexion (bloquant sur ss.accept)
        {
            try {
                player = ss.accept();
                // On cherche a mettre le client

                ClientList clientl = ClientsManager.instance.findProperList();
                // On l'ajoute
                clientl.addClient(
                        new Client(player, ClientsManager.instance.getLists().indexOf(clientl)));

            } catch (IOException ex) {
                // On affiche l'erreur
                printMessage(ex.toString());
            } catch (Exception e) {
                // On affiche l'exception
                printMessage(e.toString());
            }
        }
    }

    public void sendToClients(List<Client> c, String message) {
        for (Client client : c) {
            client.sendMessage(message);
        }
    }

    public void sendAllClient(String message) {
        for (ClientList c : ClientsManager.instance.getLists()) {
            c.sendAllClientList(message);
        }
    }

    public void deconnexion(Client client) {
        try {
            Statement stmt =
                    ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
            String sql =
                    "UPDATE account SET connected=false WHERE nom_de_compte='"
                            + client.getCompte().getName()
                            + "'";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int n = ClientsManager.instance.getClients().size();
        client.sendMessage("c;deco;aucun");
        ServerSingleton.getInstance()
                .printMessage(
                        "Joueur "
                                + client.getCompte().getName()
                                + " deconnecte. IP = "
                                + client.getS().getLocalAddress().toString()
                                + ". Actuellement il y a "
                                + (n - 1)
                                + " joueur"
                                + (n > 1 ? "s" : "")
                                + ".");

        if (client.getCompte().getCurrent_joueur() != null) {
            for (Client c : ClientsManager.instance.getClients()) {
                if (c.getCompte().getCurrent_joueur() != null
                        && c.getCompte()
                                .getCurrent_joueur()
                                .getLoaded_entities()
                                .contains(client.getCompte().getCurrent_joueur())) {
                    c.getCompte()
                            .getCurrent_joueur()
                            .getLoaded_entities()
                            .remove(client.getCompte().getCurrent_joueur());
                    c.sendMessage(
                            "s;j;"
                                    + client.getCompte().getCurrent_joueur().getPerso().getNom()
                                    + ";deco;");
                }
            }
            EntitiesManager.instance
                    .getPlayers_manager()
                    .getJoueurs()
                    .remove(client.getCompte().getCurrent_joueur());
        }

        ClientsManager.instance.removeClient(client);

        client.getCompte().setCurrent_joueur(null);
    }

    public DBConnection getDbConnexion() {
        return dbConnexion;
    }

    public void setDbConnexion(DBConnection dbConnexion) {
        this.dbConnexion = dbConnexion;
    }
}
