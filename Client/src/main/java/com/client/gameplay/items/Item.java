package com.client.gameplay.items;

import java.util.HashMap;

import org.newdawn.slick.Image;

import com.client.gameplay.Caracteristique;


public class Item extends SimpleItem
{
    private Image icon, apercu;

    public Item(String nom, String description, Image icon, Image desc, HashMap<Caracteristique,Integer> effets, int poids)
    {
        super(nom, description, icon, desc, effets, poids);
        this.icon = icon;
        this.apercu = desc;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public Image getApercu() {
        return apercu;
    }

    public void setApercu(Image apercu) {
        this.apercu = apercu;
    }


}
