package com.server.core;

import java.util.ArrayList;
import com.server.entities.Joueur;

public class Account
{
    private String name;
    private String passwd;
    private ArrayList<Joueur> joueurs;
    private Joueur current_joueur;

    public Account()
    {

    }
    public Account(String ndc,String mpd)
    {
        name = ndc;
        passwd = mpd;
    }
    public void setMdp(String mdp)
    {
        passwd = mdp;
    }

    public ArrayList<Joueur> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(ArrayList<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    public Joueur getCurrent_joueur() {
        return current_joueur;
    }

    public void setCurrent_joueur(Joueur currentJoueur) {
        current_joueur = currentJoueur;
    }

    public String getMdp() {
        return passwd;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
