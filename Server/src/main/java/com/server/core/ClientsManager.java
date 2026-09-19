package com.server.core;

import java.util.ArrayList;
import com.server.core.ClientList;
import com.server.entities.Joueur;
import com.server.entities.managers.EntitiesManager;

public class ClientsManager
{
    private ArrayList<ClientList> lists;

    public static ClientsManager instance;

    public ClientsManager()
    {
        instance = this;
        this.lists = new ArrayList<ClientList>();
    }

    public ClientList findProperList()
    {
        ClientList toreturn = null;
        for(ClientList c : lists)
        {
            // Si le nombre de client present dans une liste est inferieur au nombre maximum de client par thread.On se prepare a  retourner la liste
            if(c.getClients().size()<GlobalConstant.maxClientsPerThread)
                toreturn = c;
        }
        // Si aucune liste n'est prete a etre retournee on cree une liste.
        if(toreturn == null)
        {
            toreturn = new ClientList();
            this.lists.add(toreturn);
        }
        return toreturn;
    }

    public void removeClient(Client client)
    {
        for(int i = 0; i < lists.size(); i++)
        {
            if(lists.get(i).getClients().contains(client))
            {
                lists.get(i).getClients().remove(client);
                return;
            }
        }
    }

    public Client getClient(String nompersonnage)
    {
        Client cli = null ;
        for(int i =0; i<this.lists.size() && cli == null;i++)
        {
            for(int j = 0;j<this.lists.get(i).getClients().size() && cli == null ;j++)
            {
                if(this.lists.get(i).getClients().get(j).getCompte().getCurrent_joueur().getPerso().getNom().equals(nompersonnage))
                {
                    cli = this.lists.get(i).getClients().get(j);
                }
            }
        }
        return cli;
    }

    public ArrayList<Client> getClientsNear(Client client)
    {
        ArrayList<Joueur> entities_near = EntitiesManager.instance.getPlayersAround(client.getCompte().getCurrent_joueur());
        ArrayList<Client> clients_around = new ArrayList<>();
        ArrayList<Client> clients = getClients();
        for(int i = 0; i < entities_near.size(); i++)
        {
            for(int k = 0; k < clients.size(); k++)
            {
                if(clients.get(k).getCompte().getCurrent_joueur().equals(entities_near.get(i)))
                {
                    clients_around.add(clients.get(k));
                }
            }
        }
        return clients_around;
    }

    public ArrayList<Client> getClients() {
        ArrayList<Client> clients = new ArrayList<>();
        for(int i = 0; i < lists.size(); i++)
        {
            for(int j = 0; j < lists.get(i).getClients().size(); j++)
            {
                clients.add(lists.get(i).getClients().get(j));
            }
        }
        return clients;
    }

    public ArrayList<ClientList> getLists() {
        return lists;
    }

    public void setLists(ArrayList<ClientList> lists) {
        this.lists = lists;
    }
}
