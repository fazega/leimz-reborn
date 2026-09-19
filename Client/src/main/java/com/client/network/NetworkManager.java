package com.client.network;

import java.io.BufferedReader;




import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.client.display.gui.GUI_Manager;
import com.client.entities.MainJoueur;
import com.client.entities.managers.EntitiesManager;
import com.client.gameplay.Caracteristique;
import com.client.gameplay.managers.CombatManager;
import com.client.utils.gui.PrincipalGui;

import de.matthiasmann.twl.ResizableFrame;

public class NetworkManager
{
    private BufferedReader br;
    private PrintWriter pw;
    private Socket s;

    private HashMap<String,ArrayList<String>> message_recu_serveur;
    private Timer t;


    private Thread handleServerMessages;

    private static long timeout = 10000;

    public static NetworkManager instance;

    public NetworkManager(InetAddress a, int port) throws UnknownHostException, IOException
    {
        message_recu_serveur = new HashMap<String,ArrayList<String>>();
        s = new Socket(a, port);
        s.setSoTimeout(100);
        br = new BufferedReader(new InputStreamReader(s.getInputStream(), "ISO-8859-15"));
        pw = new PrintWriter(s.getOutputStream());
        t = new Timer();
        t.scheduleAtFixedRate(new StayConnectedTask(pw),0,800);
        instance = this;
    }

    public void sendToServer(String message)
    {
        pw.println(message);
        pw.flush();
    }

    public void waitForNewMessage(String name)
    {
        long start = System.currentTimeMillis();
        do {
            receiveFromServerPossible();
            if((System.currentTimeMillis() - start) > timeout)
            {
                System.err.println("Message "+ name + " du serveur non reÃ§u");
                System.err.println("Message en stock : " + message_recu_serveur.toString());
                System.exit(1);
            }
        } while(!message_recu_serveur.containsKey(name));
    }

    public String receiveFromServer(String name)
    {
        String res = null;
        try
        {
            if(message_recu_serveur.containsKey(name))
            {
                res = message_recu_serveur.get(name).get(0);
                message_recu_serveur.get(name).remove(0);
                if(message_recu_serveur.get(name).size()==0)
                {
                    message_recu_serveur.remove(name);
                }
            }
        }
        catch(Exception e){}
        return res;
    }

    public void receiveFromServerPossible()
    {
        try
        {
            String receive = br.readLine();
            String res[] = receive.split(";", 2);
            if(message_recu_serveur.containsKey(res[0]))
            {
                message_recu_serveur.get(res[0]).add(res[1]);
            }
            else
            {
                message_recu_serveur.put(res[0],new ArrayList<String>());
                message_recu_serveur.get(res[0]).add(res[1]);
            }
        }
        catch(Exception e)
        {
            //e.printStackTrace();
        }
    }

    public void startRefreshMessages()
    {
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                 pw.println("res;");
                 pw.flush();
            }
        }, 0, 10);

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                 pw.println("cea;");
                 pw.flush();
            }
        }, 0, 500);
    }

    public void init()
    {

        handleServerMessages = new Thread(new Runnable() {

            @Override
            public void run()
            {
                while(true)
                {
                    receiveFromServerPossible();

                    String conn_message = receiveFromServer("c");
                    if(conn_message != null)
                    {
                        String[] args = conn_message.split(";");
                        if(args[1].equals("deco"))
                        {
                            ResizableFrame frame = PrincipalGui.getPopup("Le serveur vous a dÃ©connectÃ©. Motif : "+args[2]);
                            GUI_Manager.instance.getRoot().add(frame);
                        }
                    }

                    String state_message = receiveFromServer("s");
                    if(state_message != null)
                    {
                        EntitiesManager.instance.receiveMessage("s;"+state_message);
                    }

                    String quete_message = receiveFromServer("que");
                    if(quete_message != null)
                    {
                        MainJoueur.instance.getPerso().getQuetes_manager().receiveMessage(quete_message);
                    }

                    String invent_message = receiveFromServer("in");
                    if(invent_message != null)
                    {
                        MainJoueur.instance.getPerso().getInventaire().receiveMessage(invent_message);
                    }

                    String pnjdialog_message = receiveFromServer("pd");
                    if(pnjdialog_message != null)
                    {
                        PrincipalGui.instance.getPnjdialogframe().receiveMessage(pnjdialog_message);
                    }

                    String combat_message = receiveFromServer("fi");
                    if(combat_message != null)
                    {
                        CombatManager.instance.receiveMessage(combat_message);
                    }

                    String attack_message = receiveFromServer("a");
                    if(attack_message != null)
                    {
                        String[] temp = attack_message.split(";");
                        @SuppressWarnings("unused")
                        String sender = temp[1];
                        @SuppressWarnings("unused")
                        String nom_sort = temp[2];
                        int degats = Integer.parseInt(temp[3]);

                        MainJoueur.instance.getPerso().getCaracs().put(Caracteristique.VIE, MainJoueur.instance.getPerso().getCaracs().get(Caracteristique.VIE)-degats);
                    }

                    String say_message = receiveFromServer("sa");
                    if(say_message != null)
                    {
                        PrincipalGui.instance.getChat_frame().receiveMessage(say_message);
                    }

                    String move_message = receiveFromServer("afm");
                    if(move_message != null)
                    {
                        EntitiesManager.instance.receiveMessage("afm;"+move_message);
                    }

                    String load_message = receiveFromServer("lo");
                    if(load_message != null)
                    {
                        String[] temp = load_message.split(";");
                        //Loading dynamique
                        if(temp[0].equals("map"))
                        {

                        }
                        else if(temp[0].equals("ent"))
                        {
                            System.out.println("message de loading reÃ§u !");
                            EntitiesManager.instance.receiveMessage("lo;"+load_message);
                        }
                    }
                }
            }
        });
        handleServerMessages.start();

    }

    public Socket getS() {
        return s;
    }

    public void setS(Socket s) {
        this.s = s;
    }


}
