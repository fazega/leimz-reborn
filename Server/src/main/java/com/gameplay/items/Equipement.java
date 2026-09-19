package com.gameplay.items;

import java.util.HashMap;
import com.server.gameplay.Caracteristique;

public class Equipement extends SimpleItem {
    private String type;

    public Equipement(
            String nom,
            String type,
            String description,
            HashMap<Caracteristique, Integer> effets,
            int poids,
            int id) {
        super(nom, description, effets, poids, id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
