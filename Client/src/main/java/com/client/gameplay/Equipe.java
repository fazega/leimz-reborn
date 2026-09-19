package com.client.gameplay;

import java.util.ArrayList;

import com.client.entities.Joueur;

public class Equipe {
    private String nom;
    private ArrayList<Joueur> membres;

    public Equipe(String nom, ArrayList<Joueur> membres) {
        this.nom = nom;
        this.membres = membres;
    }

    public Equipe(String nom, Joueur membre) {
        this.nom = nom;
        this.membres = new ArrayList<Joueur>();
        membres.add(membre);
    }

    public ArrayList<Joueur> getMembres() {
        return membres;
    }

    public void setMembres(ArrayList<Joueur> membres) {
        this.membres = membres;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
