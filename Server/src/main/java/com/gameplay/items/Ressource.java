package com.gameplay.items;

import java.util.HashMap;
import com.server.gameplay.Caracteristique;

public class Ressource extends SimpleItem
{

    public Ressource(String nom, String description, HashMap<Caracteristique, Integer> effets, int poids, int id)
    {
        super(nom, description, effets, poids, id);
    }

}
