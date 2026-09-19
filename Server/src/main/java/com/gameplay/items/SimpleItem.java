package com.gameplay.items;

import java.util.HashMap;
import com.server.gameplay.Caracteristique;

public class SimpleItem {
    private String nom, description;
    private int poids;
    private int id;
    private HashMap<Caracteristique, Integer> effets;

    public SimpleItem(
            String nom,
            String description,
            HashMap<Caracteristique, Integer> effets,
            int poids,
            int id) {
        this.nom = nom;
        this.description = description;
        this.effets = effets;
        this.poids = poids;
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<Caracteristique, Integer> getEffets() {
        return effets;
    }

    public void setEffets(HashMap<Caracteristique, Integer> effets) {
        this.effets = effets;
    }

    public int getPoids() {
        return poids;
    }

    public void setPoids(int poids) {
        this.poids = poids;
    }
}
