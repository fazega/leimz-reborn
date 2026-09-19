package com.client.gameplay;

import java.util.ArrayList;
import org.newdawn.slick.geom.Vector2f;
import com.client.entities.Joueur;
import com.client.network.NetworkManager;

public class Quete
{
    private String nom;
    private String commanditaire;
    private ArrayList<QueteObjectif> objectifs;

    public Quete(String nom, String commanditaire, ArrayList<QueteObjectif> objectifs)
    {
        this.nom = nom;
        this.commanditaire = commanditaire;
        this.objectifs = objectifs;
    }

    public Quete(String nom, String commanditaire)
    {
        this.nom = nom;
        this.commanditaire = commanditaire;
        this.objectifs = new ArrayList<QueteObjectif>();
    }



    public String getCommanditaire() {
        return commanditaire;
    }

    public void setCommanditaire(String commanditaire) {
        this.commanditaire = commanditaire;
    }

    public void testObjectifs(Joueur main_player)
    {
        for(int i = 0; i < objectifs.size(); i++)
        {
            if(!objectifs.get(i).isAccompli())
            {
                if(objectifs.get(i).getType().equals("endroit"))
                {
                    if(((float)main_player.getTile().getPos().x) == (((Vector2f)objectifs.get(i).getObjectif()).getX()) && ((float)main_player.getTile().getPos().y) == (((Vector2f)objectifs.get(i).getObjectif()).getY()))
                    {
                        objectifs.get(i).setAccompli(true);
                        NetworkManager.instance.sendToServer("qo;"+this.getNom()+";"+i);
                        System.out.println("Objectif accompli !");
                    }
                }
            }
        }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ArrayList<QueteObjectif> getObjectifs() {
        return objectifs;
    }

    public void setObjectifs(ArrayList<QueteObjectif> objectifs) {
        this.objectifs = objectifs;
    }
}
