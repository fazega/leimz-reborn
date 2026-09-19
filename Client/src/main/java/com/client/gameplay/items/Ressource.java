package com.client.gameplay.items;

import java.util.HashMap;

import org.newdawn.slick.Image;

import com.client.gameplay.Caracteristique;

public class Ressource extends Item
{

    public Ressource(String nom, String description, Image icon, Image desc, HashMap<Caracteristique, Integer> effets, int poids)
    {
        super(nom, description, icon, desc, effets, poids);
    }

}
