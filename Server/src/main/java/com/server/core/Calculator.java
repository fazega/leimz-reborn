package com.server.core;

import java.io.IOException;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.server.core.functions.AskForMovingFunction;
import com.server.core.functions.AttackFunction;
import com.server.core.functions.CheckNewEntitiesAroundFunction;
import com.server.core.functions.CombatFunction;
import com.server.core.functions.ConnectFunction;
import com.server.core.functions.CreationPersoFunction;
import com.server.core.functions.Functionable;
import com.server.core.functions.InfoFunction;
import com.server.core.functions.LoadFunction;
import com.server.core.functions.PersosInformationsFunction;
import com.server.core.functions.PnjDialogFunction;
import com.server.core.functions.RefreshStateFunction;
import com.server.core.functions.SayFunction;
import com.server.core.functions.StateFunction;

public class Calculator implements Runnable {
    public static HashMap<String, Functionable> dictfunctions = new HashMap<String, Functionable>();
    private Thread t;
    // Lui ne s'occupe que d'une ClientList et donc qu'un certain nombre (<20) de clients
    private ArrayList<Client> clients;

    public Calculator(ArrayList<Client> clients) {
        this.clients = clients;
        // On ajoute les fonctions
        dictfunctions.put("s", new StateFunction());
        dictfunctions.put("pd", new PnjDialogFunction());
        dictfunctions.put("i", new InfoFunction());
        dictfunctions.put("c", new ConnectFunction());
        dictfunctions.put("cp", new CreationPersoFunction());
        dictfunctions.put("ci", new PersosInformationsFunction());
        dictfunctions.put("sa", new SayFunction());
        dictfunctions.put("lo", new LoadFunction());
        dictfunctions.put("fi", new CombatFunction());
        dictfunctions.put("a", new AttackFunction());
        dictfunctions.put("cea", new CheckNewEntitiesAroundFunction());
        dictfunctions.put("res", new RefreshStateFunction());
        dictfunctions.put("afm", new AskForMovingFunction());

        this.t = new Thread(this);
        t.start();
    }

    public void submit(Client source, String mess) {

        if (mess != null && !mess.equals("none") && !mess.isEmpty()) {
            /*if(!mess.equals("res;") && !mess.equals("cea;"))
            System.out.println("Message reçu : "+mess);*/
            String[] temp = mess.split(";");
            Functionable f = dictfunctions.get(temp[0]);
            try {
                f.doSomething(temp, source);
            } catch (RuntimeException e) {
                source.sendMessage("REQUEST_FAIL");
                e.printStackTrace();
                source.disconnect();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                return;
            }
            try {
                for (int i = 0; i < clients.size(); i++) {
                    Client c = clients.get(i);
                    try {
                        if (c != null) this.submit(c, c.receiveFromClient());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ConcurrentModificationException e) {
                ServerSingleton.getInstance().printMessage(e.getMessage());
            }
        }
    }

    public void addClient(Client c) {
        clients.add(c);
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
}
