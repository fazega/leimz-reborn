package com.server.map.managers;

import com.server.map.Map;
import com.server.map.Tile;

public class MapManager {
    private Map map_visible, entire_map;

    public static MapManager instance;

    public MapManager(Map entire_map) {
        this.entire_map = entire_map;

        this.init();

        if (instance == null) {
            instance = this;
        } else {
            System.err.println("Erreur : impossible d'instancier un deuxieme MapManager");
        }
    }

    public void init() {
        // On parcourt alors les lignes et les colonnes
        for (int i = 0; i < entire_map.getGrille().length; i++) {
            for (int j = 0; j < entire_map.getGrille()[i].length; j++) {
                if (i % 2 == 0) // Si la colonne est paire
                {
                    // On dï¿½termine la "vraie" position de la tile par rapport aux coordonnï¿½es
                    entire_map.getGrille()[i][j].setPos_real_x(
                            (((float) entire_map.getGrille()[i][j].getPos_x()) / 2) * 80);
                    entire_map.getGrille()[i][j].setPos_real_y(
                            (((float) entire_map.getGrille()[i][j].getPos_y())) * 40);
                } else // Si la ligne est impaire
                {
                    // On dï¿½termine la "vraie" position de la tile par rapport aux coordonnï¿½es
                    entire_map.getGrille()[i][j].setPos_real_x(
                            (((float) entire_map.getGrille()[i][j].getPos_x()) / 2) * 80);
                    entire_map.getGrille()[i][j].setPos_real_y(
                            (((float) entire_map.getGrille()[i][j].getPos_y()) * 40) + 20);
                }
            }
        }
    }

    public Tile getTileScreen(float pos_screen_x, float pos_screen_y) {
        for (int i = 0;
                i < this.entire_map.getGrille().length;
                i++) { // Parcours toute la grille visible
            for (int j = 0; j < this.entire_map.getGrille()[i].length; j++) {
                if (this.entire_map.getGrille()[i][j].isPointed(pos_screen_x, pos_screen_y)
                        && this.entire_map.getGrille()[i][j].isDrawn())
                    return this.entire_map.getGrille()[i][j];
            }
        }
        return null;
    }

    public Tile getTileReal(float pos_real_x, float pos_real_y) {
        for (int i = 0;
                i < this.entire_map.getGrille().length;
                i++) { // Parcours toute la grille visible
            for (int j = 0; j < this.entire_map.getGrille()[i].length; j++) {
                Tile tile = this.entire_map.getGrille()[i][j];
                if (tile.isPointed(
                        pos_real_x, pos_real_y, tile.getPos_real_x(), tile.getPos_real_y()))
                    return tile;
            }
        }
        return null;
    }

    public Tile[][] getTilesAutour(int pos_x, int pos_y, int spread) {
        // Recuperation de l'index de depart en x et y des Tile a afficher.
        int indiceStart_x = ((int) (pos_x - spread)) >= 0 ? ((int) (pos_x - spread)) : 0;
        int indiceStart_y = ((int) (pos_y - spread)) >= 0 ? ((int) (pos_y - spread)) : 0;

        // Recuperation de l'index de fin en x et y des Tile a afficher.
        int indiceFin_x =
                ((int) (indiceStart_x + spread * 2)) <= this.getEntire_map().getGrille().length - 1
                        ? (indiceStart_x + spread * 2)
                        : this.getEntire_map().getGrille().length - 1;
        int indiceFin_y =
                ((int) (indiceStart_y + spread * 2)) <= this.getEntire_map().getGrille().length - 1
                        ? (indiceStart_y + spread * 2)
                        : this.getEntire_map().getGrille().length - 1;

        Tile[][] grille = new Tile[indiceFin_x - indiceStart_x][indiceFin_y - indiceStart_y];

        // On parcourt alors les lignes et les colonnes
        for (int i = 0; i < indiceFin_x - indiceStart_x; i++) {
            if (i >= 0 && i < this.getEntire_map().getGrille().length) {
                for (int j = 0; j < indiceFin_y - indiceStart_y; j++) {
                    // Attention aux cas ou la tile voulue se situe sur une extremite de la map.
                    if (j >= 0 && j < this.getEntire_map().getGrille()[i + indiceStart_x].length) {
                        grille[i][j] =
                                this.getEntire_map()
                                        .getGrille()[i + indiceStart_x][j + indiceStart_y];
                    }
                }
            }
        }
        return grille;
    }

    /*public float getAbsolute_x() {
        return entire_map.getGrille()[0][0].getPos_real_x();
    }

    public float getAbsolute_y() {
        return entire_map.getGrille()[0][0].getPos_real_y();
    }*/
    public Map getMap_visible() {
        return map_visible;
    }

    public void setMap_visible(Map mapVisible) {
        map_visible = mapVisible;
    }

    public Map getEntire_map() {
        return entire_map;
    }

    public void setEntire_map(Map entireMap) {
        entire_map = entireMap;
    }
}
