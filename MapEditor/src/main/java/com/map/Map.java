package com.map;

import java.util.ArrayList;





public class Map
{
    private Grille grille;
    private ArrayList<String> data_monstres;

    public Map(Grille grille, ArrayList<String> data_monstres)
    {
        this.grille = grille;
        this.data_monstres = data_monstres;
    }

    public ArrayList<String> getDataMonstres() {
        return data_monstres;
    }

    public void setDataMonstres(ArrayList<String> data_monstres) {
        this.data_monstres = data_monstres;
    }

    public Grille getGrille() {
        return grille;
    }

    public void setGrille(Grille grille) {
        this.grille = grille;
    }

}
