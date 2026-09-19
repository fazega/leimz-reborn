package com.client.gameplay.items;

import java.util.HashMap;

import org.newdawn.slick.Image;

import com.client.gameplay.Caracteristique;

public class Arme extends Equipement
{
    public int dommages;
    //public int energie_a_depenser;

    public Arme(String nom, String type,String description, Image icon, Image desc,
            HashMap<Caracteristique, Integer> effets, int poids, int dommages)
    {
        super(nom, type, description, icon, desc, effets, poids);

        this.dommages = dommages;
    }

    public int getDommages() {
        return dommages;
    }

    public void setDommages(int dommages) {
        this.dommages = dommages;
    }


}
