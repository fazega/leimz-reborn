package com.server.entities;

import com.server.map.Tile;

public abstract class Entity
{


    //Position de l'entite sur la carte
    //En repere (0;80;40)
    protected Tile tile;
    //En repere (0;1;1)
    protected float pos_real_x, pos_real_y;
    protected float pos_old_x, pos_old_y;

    //Position correspondant au barycentre de la tile sur laquelle l'entite est
    protected float pos_real_on_screen_x, pos_real_on_screen_y;

    //Taille de l'entite
    protected float size_x, size_y;

    //Orientation de l'entite
    protected Orientation orientation;

    //Etat
    public enum Etat
    {
        NORMAL, OVER, CLICKED
    };
    protected Etat etat;

    //Booleen indiquant si l'entite est en collision ou non
    protected boolean on_collision = false;

    //PAS DE GRAPHIQUE POUR LE SERVEUR

    //*******************************ANNEXES********************************

    //La taille relative de l'entite
    protected float scaleSize = 1;

    //La vitesse de l'entite
    protected float speed = 1.0f;

    public Entity(Orientation orientation, Tile tile)
    {
        this.orientation = orientation;
        this.tile = tile;

        this.pos_real_x=0;
        this.pos_real_y=0;
        if(tile != null)
        {
            this.pos_real_x = tile.getPos_real_x();
            this.pos_real_y = tile.getPos_real_y();
        }

        this.etat = Etat.NORMAL;
    }

    //Methode permettant de dessiner l'entite
    public void draw()
    {
        /**
         * A voir en fonction de l'entite
         */
    }

    //Methode permettant de rafraichir des elements de l'entite (formes par exemple)
    public void refresh()
    {
        /**
         * A voir en fonction de l'entite
         */
    }

    public String stringOrientation()
    {
        String o_m = null;
        switch(orientation)
        {
            case DROITE:
                o_m = "d";
                break;
            case GAUCHE:
                o_m = "g";
                break;
            case HAUT:
                o_m = "h";
                break;
            case BAS:
                o_m = "b";
                break;
            case HAUT_DROITE:
                o_m = "hd";
                break;
            case HAUT_GAUCHE:
                o_m = "hg";
                break;
            case BAS_DROITE:
                o_m = "bd";
                break;
            case BAS_GAUCHE:
                o_m = "bg";
                break;
        }
        return o_m;
    }

    public static Orientation parseStringOrientation(String o_m)
    {
        Orientation o = null;

        if(o_m.equals("d"))
            o = Orientation.DROITE;
        else if(o_m.equals("g"))
            o = Orientation.GAUCHE;
        else if(o_m.equals("h"))
            o = Orientation.HAUT;
        else if(o_m.equals("b"))
            o = Orientation.BAS;
        else if(o_m.equals("hd"))
            o = Orientation.HAUT_DROITE;
        else if(o_m.equals("hg"))
            o = Orientation.HAUT_GAUCHE;
        else if(o_m.equals("bd"))
            o = Orientation.BAS_DROITE;
        else if(o_m.equals("bg"))
            o = Orientation.BAS_GAUCHE;

        return o;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isOn_collision() {
        return on_collision;
    }

    public void setOn_collision(boolean onCollision) {
        on_collision = onCollision;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public float getPos_real_x() {
        return pos_real_x;
    }

    public void setPos_real_x(float pos_real_x) {
        this.pos_real_x = pos_real_x;
    }

    public float getPos_real_y() {
        return pos_real_y;
    }

    public void setPos_real_y(float pos_real_y) {
        this.pos_real_y = pos_real_y;
    }

    public float getPos_real_on_screen_x() {
        return pos_real_on_screen_x;
    }

    public void setPos_real_on_screen_x(float pos_real_on_screen_x) {
        this.pos_real_on_screen_x = pos_real_on_screen_x;
    }

    public float getPos_real_on_screen_y() {
        return pos_real_on_screen_y;
    }

    public void setPos_real_on_screen_y(float pos_real_on_screen_y) {
        this.pos_real_on_screen_y = pos_real_on_screen_y;
    }

    public float getSize_x() {
        return size_x;
    }

    public void setSize_x(float size_x) {
        this.size_x = size_x;
    }

    public float getSize_y() {
        return size_y;
    }

    public void setSize_y(float size_y) {
        this.size_y = size_y;
    }

    public float getPos_old_x() {
        return pos_old_x;
    }

    public void setPos_old_x(float pos_old_x) {
        this.pos_old_x = pos_old_x;
    }

    public float getPos_old_y() {
        return pos_old_y;
    }

    public void setPos_old_y(float pos_old_y) {
        this.pos_old_y = pos_old_y;
    }



}
