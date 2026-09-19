package com.server.entities;

import java.util.ArrayList;

import com.server.entities.Personnage;
import com.server.map.Tile;

public class Joueur extends Entity {
    protected Personnage perso;

    protected ArrayList<Entity> loaded_entities;
    protected ArrayList<Tile> before_loaded;

    public Joueur(Personnage perso, Tile tile, Orientation orientation) {
        super(orientation, tile);
        this.perso = perso;

        loaded_entities = new ArrayList<Entity>();
        before_loaded = new ArrayList<Tile>();
    }

    public Personnage getPerso() {
        return perso;
    }

    public void setPerso(Personnage perso) {
        this.perso = perso;
    }

    public ArrayList<Entity> getLoaded_entities() {
        return loaded_entities;
    }

    public void setLoaded_entities(ArrayList<Entity> loaded_entities) {
        this.loaded_entities = loaded_entities;
    }

    public ArrayList<Tile> getBefore_loaded() {
        return before_loaded;
    }

    public void setBefore_loaded(ArrayList<Tile> before_loaded) {
        this.before_loaded = before_loaded;
    }
}
