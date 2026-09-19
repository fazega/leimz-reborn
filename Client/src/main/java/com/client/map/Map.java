package com.client.map;

import java.util.ArrayList;

public class Map {
    private Tile[][] grille;
    private ArrayList<GroupTiles> groups;
    private ArrayList<String> data_monstres;

    public Map(Tile[][] grille, ArrayList<GroupTiles> groups) {
        this.grille = grille;
        this.groups = groups;
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

    public ArrayList<GroupTiles> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<GroupTiles> groups) {
        this.groups = groups;
    }
}
