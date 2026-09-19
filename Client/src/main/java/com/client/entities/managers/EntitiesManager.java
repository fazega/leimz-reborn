package com.client.entities.managers;

import java.util.ArrayList;
import org.newdawn.slick.geom.Vector2f;
import com.client.entities.Entity;
import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.entities.PNJ;
import com.client.gameplay.Caracteristique;
import com.client.gameplay.entities.Personnage;
import com.client.load.LoadPnj;
import com.client.map.managers.MapManager;
import com.client.network.NetworkListener;

public class EntitiesManager implements NetworkListener
{
    private MonstersManager monsters_manager;
    private PlayersManager players_manager;
    private PNJsManager pnjs_manager;
    private ArrayList<Entity> entities;

    public static EntitiesManager instance;

    public EntitiesManager()
    {
        instance = this;
        entities = new ArrayList<Entity>();
    }

    private void refreshEntities()
    {
        this.entities.removeAll(entities);
        for(int i = 0; i < players_manager.getJoueurs().size(); i++)
        {
            this.entities.add(players_manager.getJoueurs().get(i));
        }
        for(int i = 0; i < pnjs_manager.getPnjs().size(); i++)
        {
            this.entities.add(pnjs_manager.getPnjs().get(i));
        }
    }


    @Override
    public void receiveMessage(String str)
    {
        String[] temp = str.split(";");
        if(temp[0].equals("afm"))
        {
            if(temp[1].equals("true"))
                {
                MainJoueur.instance.setPos_real(MainJoueur.instance.getPoscopie());
                com.client.network.NetworkManager.instance.sendToServer("s;pos;"+MainJoueur.instance.getPos_real().x+";"+MainJoueur.instance.getPos_real().y+";"+MainJoueur.instance.stringOrientation());
            }
        }
        if(temp[0].equals("lo"))
        {
            System.out.println("Loading ready.");
            System.out.println(temp[2]);
            if(temp[2].equals("pnj"))
            {
                System.out.println("Loading du pnj en cours ...");
                PNJ pnj = LoadPnj.loadPnj(str.substring(temp[0].length()+1+temp[1].length()+1+temp[2].length()+1, str.length()));
                pnjs_manager.add(pnj);
                System.out.println("Pnj ajoutÃ© !");
            }

            else if(temp[2].equals("j"))
            {
                Joueur j = new Joueur(new Personnage(temp[3], temp[4], temp[5]),
                        MapManager.instance.getEntire_map().getGrille()[Integer.parseInt(temp[6])][Integer.parseInt(temp[7])],
                        Joueur.parseStringOrientation(temp[8]));
                EntitiesManager.instance.getPlayers_manager().addNewPlayer(j);
                EntitiesManager.instance.getPlayers_manager().getJoueurs().get(EntitiesManager.instance.getPlayers_manager().getJoueurs().size()-1).initImgs();
            }
        }
        else if(temp[0].equals("s"))
        {
            if(temp[1].equals("pnj"))
            {
                if(temp[3].equals("pos"))
                {
                    EntitiesManager.instance.getPnjs_manager().getPnj(temp[2]).setPos_real(new Vector2f(Float.parseFloat(temp[4]), Float.parseFloat(temp[5])));
                    EntitiesManager.instance.getPnjs_manager().getPnj(temp[2]).setOrientation(Joueur.parseStringOrientation(temp[6]));
                }
            }
            else if(temp[1].equals("j"))
            {
                if(temp[3].equals("pos"))
                {
                    EntitiesManager.instance.getPlayers_manager().getJoueur(temp[2]).setPos_real(new Vector2f(Float.parseFloat(temp[4]), Float.parseFloat(temp[5])));
                    EntitiesManager.instance.getPlayers_manager().getJoueur(temp[2]).setOrientation(Joueur.parseStringOrientation(temp[6]));
                }
                else if(temp[3].equals("deco"))
                {
                    EntitiesManager.instance.getPlayers_manager().getJoueurs().remove(EntitiesManager.instance.getPlayers_manager().getJoueur(temp[2]));
                }
                else if(temp[3].equals("vie"))
                {
                    EntitiesManager.instance.getPlayers_manager().getJoueur(temp[2]).getPerso().getCaracs().put(Caracteristique.VIE, Integer.parseInt(temp[4]));
                }
            }
        }

    }

    public MonstersManager getMonsters_manager() {
        return monsters_manager;
    }

    public void setMonsters_manager(MonstersManager monstersManager) {
        monsters_manager = monstersManager;
    }

    public PlayersManager getPlayers_manager() {
        return players_manager;
    }

    public void setPlayers_manager(PlayersManager playersManager) {
        players_manager = playersManager;
    }

    public PNJsManager getPnjs_manager() {
        return pnjs_manager;
    }

    public void setPnjs_manager(PNJsManager pnjsManager) {
        pnjs_manager = pnjsManager;
    }


    public ArrayList<Entity> getEntities() {
        this.refreshEntities();
        return entities;
    }


    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
    }




}
