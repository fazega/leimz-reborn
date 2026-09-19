
package com.server.core.functions;


import com.server.core.Account;

import com.server.core.Client;
import com.server.core.ClientsManager;
import com.server.core.ServerSingleton;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author fazega
 */
public class ConnectFunction implements Functionable
{
    public ConnectFunction()
    {

    }

    @Override
    public void doSomething(String[] args,Client c)
    {
        if(args.length <2)
            ServerSingleton.getInstance().printMessage("Erreur a la connection : message trop court.");

        String ndc = args[1];
        String mdp = args[2];

        ResultSet rsj = null;

            Statement stmt;
            try {
                stmt = ServerSingleton.getInstance().getDbConnexion().getConnexion().createStatement();
                rsj = stmt.executeQuery("SELECT currjoueur,connected FROM account " +
                        "WHERE nom_de_compte='"+ndc+"' " +
                        "AND mot_de_passe='"+mdp+"'");
            } catch (SQLException e) {
                throw new RuntimeException("Issue with executing query (syntax ?) for finding account");
            }

            boolean connected=false;
            try
            {
                rsj.next();
                connected = rsj.getBoolean("connected");
            }
            catch (SQLException e) {
                c.sendMessage("c;CONNECT_FAILED;INCORRECT_NDC_PASS");
                ClientsManager.instance.getLists().get(c.getListe()).deleteClient(c);
                return;
            }

            if(connected)
            {
                c.sendMessage("c;CONNECT_FAILED;ALREADY_CONNECTED");
                ClientsManager.instance.getLists().get(c.getListe()).deleteClient(c);
                return;
            }

            c.sendMessage("c;CONNECT_SUCCEED");
            c.setCompte(new Account(ndc,mdp));


            try {
                rsj.close();
                stmt.executeUpdate("UPDATE account SET connected=true WHERE nom_de_compte='"+ndc+"'");
            } catch (SQLException e) {
                ServerSingleton.getInstance().printMessage("Finding player statement's creation failed");
                return;
            }



            /*
            c.sendMessage("ci;"+name+";"+race+";"+classe+";"+posx+";"+posy+";"+ori);
            ArrayList<Client> listToSend = new ArrayList<>();
            for(int i = 0; i < ClientsManager.instance.getClients().size(); i++)
            {
                if(!ClientsManager.instance.getClients().get(i).equals(c))
                {
                    listToSend.add(ClientsManager.instance.getClients().get(i));
                }
            }*/
            int n = ClientsManager.instance.getClients().size();
            ServerSingleton.getInstance().printMessage("Joueur "+c.getCompte().getName()+" connecte. IP = "+c.getS().getLocalAddress().toString()+". Actuellement il y a "+n+" joueur"+(n>1?"s":"")+".");
            //ServerSingleton.getInstance().sendToClients(listToSend,"lo;ent;j;"+name+";"+race+";"+classe+";"+posx+";"+posy+";"+ori);



    }
}
