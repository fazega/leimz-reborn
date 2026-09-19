package com.client.gameplay.items;

import java.util.HashMap;

import org.newdawn.slick.Image;

import com.client.gameplay.Caracteristique;

public class Equipement extends Item
{
    private String type;

    public Equipement(String nom, String type, String description, Image icon, Image desc,
            HashMap<Caracteristique, Integer> effets, int poids)
    {
        super(nom, description, icon, desc, effets, poids);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
