package com.client.gameplay.items;

import java.util.HashMap;

import org.newdawn.slick.Image;

import com.client.gameplay.Caracteristique;

public class SimpleItem
{
    private String nom, description;
    private Image icon, apercu;
    private int poids;
    private HashMap<Caracteristique,Integer> effets;

    public SimpleItem(String nom, String description, Image icon, Image desc, HashMap<Caracteristique,Integer> effets, int poids)
    {
        this.nom = nom;
        this.description = description;
        this.icon = icon;
        this.apercu = desc;
        this.effets = effets;
        this.poids = poids;
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


    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public Image getApercu() {
        return apercu;
    }

    public void setApercu(Image apercu) {
        this.apercu = apercu;
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
