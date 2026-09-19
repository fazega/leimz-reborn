package com.client.gameplay;

import java.util.ArrayList;
import java.util.HashMap;

public class Classe
{
    private String nom;
    private ArrayList<Sort> sorts;
    private HashMap<Caracteristique,Integer> caracs;

    public Classe(String nom, ArrayList<Sort> sorts, HashMap<Caracteristique,Integer> caracs)
    {
        this.nom = nom;
        this.sorts = sorts;
        this.caracs = caracs;
    }

    public Classe(String classe)
    {
        this.nom = classe;
        //TODO : Carac et sort;
    }

    public ArrayList<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(ArrayList<Sort> sorts) {
        this.sorts = sorts;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public HashMap<Caracteristique,Integer> getCaracs() {
        return caracs;
    }

    public void setCaracs(HashMap<Caracteristique,Integer> caracs) {
        this.caracs = caracs;
    }
}
