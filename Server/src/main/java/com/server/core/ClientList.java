package com.server.core;

import java.util.ArrayList;
/**
 * Classe representant une liste de client. La liste est de taille variable
 *
 * @author fazega
 * @author kratisto
 */
public class ClientList {
    private ArrayList<Client> clients;

    @SuppressWarnings("unused")
    private Calculator calculator;

    public ClientList() {
        clients = new ArrayList<Client>();
        this.calculator = new Calculator(clients);
    }

    public void addClient(Client c) {
        c.sendMessage("co;CONNECT_SERVER");
        clients.add(c);
    }

    public void deleteClient(Client c) {
        clients.remove(c);
    }

    // Envoie un message a tous les clients connectes sur cette liste
    public void sendAllClientList(String message) {
        for (Client c : clients) {
            c.sendMessage(message);
        }
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
