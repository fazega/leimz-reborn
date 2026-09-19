package com.client.gameplay;

import org.newdawn.slick.Animation;

public class Sort {
    private String nom, description;
    private int valeur_min, valeur_max;
    private Animation anim;

    public Sort(String nom, String description, int valeur_min, int valeur_max, Animation anim) {
        this.nom = nom;
        this.valeur_min = valeur_min;
        this.valeur_max = valeur_max;
        this.anim = anim;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getValeur_min() {
        return valeur_min;
    }

    public void setValeur_min(int valeurMin) {
        valeur_min = valeurMin;
    }

    public int getValeur_max() {
        return valeur_max;
    }

    public void setValeur_max(int valeurMax) {
        valeur_max = valeurMax;
    }

    public Animation getAnim() {
        return anim;
    }

    public void setAnim(Animation anim) {
        this.anim = anim;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
