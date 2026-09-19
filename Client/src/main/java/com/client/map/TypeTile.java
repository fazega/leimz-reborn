package com.client.map;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;


public class TypeTile
{
    private Image img;
    private Rectangle base;
    private String nom;
    private int calque;
 private boolean collidable;
 public boolean isCollidable() { return collidable; }


    public TypeTile(String nom, Image img, Rectangle base, boolean collidable, int calque)
    {
        this.nom = nom;
 this.collidable = collidable;
        this.calque = calque;
        this.img = img;
        this.base = base;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public Rectangle getBase() {
        return base;
    }

    public void setBase(Rectangle base) {
        this.base = base;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
 this.collidable = collidable;
    }

    public int getCalque() {
        return calque;
    }

    public void setCalque(int calque) {
        this.calque = calque;
    }


}
