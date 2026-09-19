package com.client.entities.managers;

import java.util.ArrayList;

import com.client.entities.Joueur;
import com.client.entities.MainJoueur;

public class PlayersManager
{
    private ArrayList<Joueur> joueurs;
    private MainJoueur main_player;

    public PlayersManager(MainJoueur main_player,ArrayList<Joueur> joueurs)
    {
        this.main_player = main_player;
        this.joueurs = joueurs;
    }

    public PlayersManager(MainJoueur main_player)
    {
        this.main_player = main_player;
        this.joueurs = new ArrayList<Joueur>();
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
        if(j == null)
        {
            if(nom.equals(MainJoueur.instance.getPerso().getNom()))
            {
                j = MainJoueur.instance;
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

    public MainJoueur getMain_player() {
        return main_player;
    }

    public void setMain_player(MainJoueur mainPlayer) {
        main_player = mainPlayer;
    }




}
