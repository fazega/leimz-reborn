package com.client.utils.pathfinder;

import com.client.map.Tile;

public class Noeud
{
    private Noeud parent;
    private float cout_g, cout_h, cout_f;

    private Tile tile;

    public Noeud(Tile tile)
    {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }



    public void setTile(Tile tile) {
        this.tile = tile;
    }



    public Noeud getParent() {
        return parent;
    }



    public void setParent(Noeud parent) {
        this.parent = parent;
    }



    public float getCout_g() {
        return cout_g;
    }

    public void setCout_g(float coutG) {
        cout_g = coutG;
    }

    public float getCout_h() {
        return cout_h;
    }

    public void setCout_h(float coutH) {
        cout_h = coutH;
    }

    public float getCout_f() {
        return cout_f;
    }

    public void setCout_f(float coutF) {
        cout_f = coutF;
    }
}
