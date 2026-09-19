package com.server.map;

import java.util.ArrayList;

public class Map
{
    //Taille des images de fond : 50*50

    public static int SIZE_GRILLE_X=100, SIZE_GRILLE_Y=100;

    private Tile[][] grille;
    private ArrayList<String> data_monstres;

    public Map(Tile[][] grille, ArrayList<String> data_monstres)
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

    public Tile[][] getGrille() {
        return grille;
    }

    public void setGrille(Tile[][] grille) {
        this.grille = grille;
    }

}
