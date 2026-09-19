package com.gameplay.items;

import java.util.HashMap;
import com.server.gameplay.Caracteristique;


public class Item extends SimpleItem
{
    private String type;
    public Item(String nom, String type, String description, HashMap<Caracteristique,Integer> effets, int poids, int id)
    {
        super(nom, description, effets, poids, id);
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }



}
