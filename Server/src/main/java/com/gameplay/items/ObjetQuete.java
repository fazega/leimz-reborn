package com.gameplay.items;

import java.util.HashMap;

import com.server.gameplay.Caracteristique;

public class ObjetQuete extends SimpleItem {

    public ObjetQuete(
            String nom,
            String description,
            HashMap<Caracteristique, Integer> effets,
            int poids,
            int id) {
        super(nom, description, effets, poids, id);
    }
}
