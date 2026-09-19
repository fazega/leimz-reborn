package com.server.entities.managers;

import java.util.ArrayList;
import com.server.entities.Joueur;

public class PlayersManager
{
    private ArrayList<Joueur> joueurs;

    public PlayersManager(ArrayList<Joueur> joueurs)
    {
        this.joueurs = joueurs;
    }

    public PlayersManager()
    {
        this.joueurs = new ArrayList<Joueur>();
    }

    public Joueur getJoueur(String nom)
    {
        Joueur j = null;
        for(int i = 0; i < joueurs.size(); i++)
        {
            if(joueurs.get(i).getPerso().getNom().equals(nom))
            {
                j = joueurs.get(i);
            }
        }
        return j;
    }

    public void refresh()
    {
        for(int i = 0; i < joueurs.size(); i++)
        {
            joueurs.get(i).refresh();
        }
    }

    public void addNewPlayer(Joueur joueur)
    {
        this.joueurs.add(joueur);
    }

    public ArrayList<Joueur> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(ArrayList<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

}
