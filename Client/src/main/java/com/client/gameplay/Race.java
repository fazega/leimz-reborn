package com.client.gameplay;

import java.util.ArrayList;
import java.util.HashMap;

public class Race {
    private String nom;
    private ArrayList<Sort> sorts;
    private HashMap<Caracteristique, Integer> carac;

    public Race(String nom, ArrayList<Sort> sorts, HashMap<Caracteristique, Integer> carac) {
        this.nom = nom;
        this.sorts = sorts;
        this.carac = carac;
    }

    public Race(String race) {
        this.nom = race;
        this.sorts = new ArrayList<>();
        this.carac = new HashMap<>();
    }

    public String getNom() {
        return nom;
    }

    public HashMap<Caracteristique, Integer> getCarac() {
        return carac;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setCarac(HashMap<Caracteristique, Integer> carac) {
        this.carac = carac;
    }

    public ArrayList<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(ArrayList<Sort> sorts) {
        this.sorts = sorts;
    }
}
