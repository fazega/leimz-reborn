package com.server.core;

import com.server.entities.Joueur;
import com.server.db.AccountCharacter;

public class Account {
    private String name;
    private String passwd;
    private Joueur current_joueur;
    private AccountCharacter character;

    public AccountCharacter getCharacter() {
        return character;
    }

    public void setCharacter(AccountCharacter character) {
        this.character = character;
    }

    public Account() {}

    public Account(String ndc, String mpd) {
        name = ndc;
        passwd = mpd;
    }

    public void setMdp(String mdp) {
        passwd = mdp;
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
