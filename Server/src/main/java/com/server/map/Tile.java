package com.server.map;

import com.server.core.GlobalConstant;

public class Tile
{
    private int pos_x, pos_y; //Ces positions sont les coordonnees
    private float pos_real_x, pos_real_y;


    private int state;
    public static int NONE = 0, OVER = 1, CLICKED = 2;

    private boolean collidable, drawn;


    /**
     * Constructeur utilise pour l'initilisation des Tiles dans une grille/map.
     * @param pos_x Position en X de la tyle dans le tableau (ex: 0)
     * @param pos_y Position en Y de la tyle dans le tableau (ex: 3)
     * @param type Type de la tyle
     */
    public Tile(int pos_x, int pos_y)
    {
        this.pos_x = pos_x;
        this.pos_y = pos_y;
    }

    public void refreshEvent()
    {
        //RIEN, (pour l'instant)
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return state;
    }



    public boolean isPointed(float pos_x, float pos_y)
    {

        //Cette methode renvoie si oui ou non, la tile est pointee par la souris.


        //ATTENTION, cette methode est assez compliquee mathematiquement
        //Pour la demontrer, dessiner sur un schema un losange dont les diagonales font 80 et 40 (UA)
        //Trouver alors la condition pour qu'un point (x;y) soit dans le losange.


        int m_x, m_y; //On definit de nouvelles coordonnees pour la souris
        m_x = (int) ((pos_x - this.pos_real_x) - (80/2)); //La coordonnee x par rapport au centre de l'image
        m_y = (int) ((pos_y - this.pos_real_y) - (40/2)); //La coordonnee y par rapport au centre de l'image

        //Si la souris est dans la tile en x, et que |x/2|+|y| < height/2 soit 20
        return (m_x >= -40 && m_x < 40 && ((Math.abs(m_x/2)+Math.abs(m_y))<=20));
    }

    public boolean isPointed(float pos_x, float pos_y, float pos_absolute_x, float pos_absolute_y)
    {

        //Cette methode renvoie si oui ou non, la tile est pointee par la souris.


        //ATTENTION, cette methode est assez compliquee mathematiquement
        //Pour la demontrer, dessiner sur un schema un losange dont les diagonales font 80 et 40 (UA)
        //Trouver alors la condition pour qu'un point (x;y) soit dans le losange.


        int m_x, m_y; //On definit de nouvelles coordonnees pour la souris
        m_x = (int) ((pos_x - pos_absolute_x) - (80/2)); //La coordonnee x par rapport au centre de l'image
        m_y = (int) ((pos_y - pos_absolute_y) - (40/2)); //La coordonnee y par rapport au centre de l'image

        //Si la souris est dans la tile en x, et que |x/2|+|y| < height/2 soit 20
        return (m_x >= -40 && m_x < 40 && ((Math.abs(m_x/2)+Math.abs(m_y))<=20));
    }



    public int getPos_x() {
        return pos_x;
    }

    public void setPos_x(int pos_x) {
        this.pos_x = pos_x;
    }

    public int getPos_y() {
        return pos_y;
    }

    public void setPos_y(int pos_y) {
        this.pos_y = pos_y;
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

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }



    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public float getPos_real_x_barycentre() {
        return pos_real_x+(GlobalConstant.Tile_x/2);
    }

    public float getPos_real_y_barycentre() {
        return pos_real_y+(GlobalConstant.Tile_y/2);
    }
}
