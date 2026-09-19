package com.gameplay.items;

import java.util.HashMap;
import com.server.gameplay.Caracteristique;

public class Arme extends Equipement
{
    public int dommages;
    //public int energie_a_depenser;

    public Arme(String nom, String type,String description, HashMap<Caracteristique, Integer> effets, int poids, int id, int dommages)
    {
        super(nom, type, description, effets, poids,id);

        this.dommages = dommages;
    }

    public int getDommages() {
        return dommages;
    }

    public void setDommages(int dommages) {
        this.dommages = dommages;
    }


}
