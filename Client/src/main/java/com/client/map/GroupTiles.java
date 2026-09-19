package com.client.map;

import java.util.ArrayList;

public class GroupTiles {

    private int id;
    private ArrayList<Tile> tiles;
    private Tile base;
    private TypeTile type;

    public GroupTiles(int id)
    {
        this.id = id;
        this.tiles = new ArrayList<>();
    }

    public GroupTiles(Tile base, ArrayList<Tile> tiles, TypeTile type, int id)
    {
        this.tiles = tiles;
        this.id = id;
        this.type = type;
        this.base = base;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }

    public TypeTile getType() {
        return type;
    }

    public void setType(TypeTile type) {
        this.type = type;
    }

    public Tile getBase() {
        return base;
    }

    public void setBase(Tile base) {
        this.base = base;
    }



}
