package com.client.load;

import java.util.ArrayList;

import com.client.network.NetworkManager;

/**
 *
 * @author chelendil
 * Classe chargeant les informations de classe et de race du joueur (sort, competences ...)
 */
public class LoadMonster implements Runnable
{
    private Thread looper;
    private int purcent;
    private boolean running;
    private ArrayList<String> monster_list;

    public LoadMonster()
    {
        this.purcent = 0;
        monster_list = new ArrayList<String>();
        looper = new Thread(this);
        looper.start();
        running = true;
    }

    @Override
    public void run()
    {
        if(running)
        {

            purcent += 13;

            //-------------------GESTION DES PNJs-----------------------

            NetworkManager.instance.sendToServer("lo;mon"); //load monsters
            NetworkManager.instance.waitForNewMessage("mon");
            String[] args_pnj = NetworkManager.instance.receiveFromServer("mon").split(";");

            for( int i=0;i<args_pnj.length;i++)
            {
                monster_list.add(args_pnj[i]);
            }

            try {
                //TODO Pourquoi un sleep?
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            running = false;
        }
    }

    public int getPurcent() {
        return purcent;
    }

    public void setPurcent(int purcent) {
        this.purcent = purcent;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public ArrayList<String> getMonster_list() {
        return monster_list;
    }

    public void setMonster_list(ArrayList<String> monster_list) {
        this.monster_list = monster_list;
    }

}
