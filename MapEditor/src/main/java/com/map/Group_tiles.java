package com.map;

import java.util.ArrayList;

public class Group_tiles {
    private ArrayList<Tile> tiles;
    private Type_tile type;

    public Group_tiles(ArrayList<Tile> tiles, Type_tile type) {
        this.tiles = tiles;
        this.type = type;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }

    public Type_tile getType() {
        return type;
    }

    public void setType(Type_tile type) {
        this.type = type;
    }
}
