package com.client.utils.pathfinder;

import java.util.ArrayList;

public class Chemin {
    ArrayList<Noeud> noeuds;
    Noeud encours;
    Noeud objectif, depart;

    public Chemin(Noeud depart, Noeud objectif) {
        this.noeuds = new ArrayList<Noeud>();
        this.noeuds.add(depart);
        this.depart = depart;
        this.encours = depart;
        this.objectif = objectif;
    }

    public ArrayList<Noeud> getNoeuds() {
        return noeuds;
    }

    public void setNoeuds(ArrayList<Noeud> noeuds) {
        this.noeuds = noeuds;
    }

    public Noeud getEncours() {
        return encours;
    }

    public void setEncours(Noeud encours) {
        this.encours = encours;
    }

    public Noeud getObjectif() {
        return objectif;
    }

    public void setObjectif(Noeud objectif) {
        this.objectif = objectif;
    }

    public Noeud getDepart() {
        return depart;
    }

    public void setDepart(Noeud depart) {
        this.depart = depart;
    }
}
