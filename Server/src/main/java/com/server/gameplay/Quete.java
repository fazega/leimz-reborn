package com.server.gameplay;

import java.util.ArrayList;

public class Quete {
    private String nom;
    private int id;
    private boolean finie;
    private ArrayList<QueteObjectif> objectifs;

    public Quete(int id, String nom, ArrayList<QueteObjectif> objectifs, boolean finie) {
        this.id = id;
        this.nom = nom;
        this.objectifs = objectifs;
        this.finie = finie;
    }

    public Quete(int id, String nom) {
        this.nom = nom;
        this.id = id;
        this.objectifs = new ArrayList<>();
    }

    public Quete(int id, String nom, boolean finie) {
        this.nom = nom;
        this.id = id;
        this.objectifs = new ArrayList<>();
        this.finie = finie;
    }

    public boolean isFinie() {
        return finie;
    }

    public void setFinie(boolean finie) {
        this.finie = finie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
