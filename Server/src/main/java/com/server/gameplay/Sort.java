package com.server.gameplay;

public class Sort {
    private String nom, description;
    private int valeur_min, valeur_max;

    public Sort(String nom, String description, int valeur_min, int valeur_max) {
        this.nom = nom;
        this.valeur_min = valeur_min;
        this.valeur_max = valeur_max;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
