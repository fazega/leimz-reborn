package com.server.entities;

import java.util.ArrayList;
import java.util.HashMap;


import com.server.gameplay.PNJ_discours;
import com.server.gameplay.Quete;
import com.server.map.Tile;

public class PNJ extends Entity
{
    private String nom;
    //L'entier est l'id du discours qui lance la quete
    private HashMap<Integer, Quete> quetes;
    private ArrayList<PNJ_discours> discours;

    public PNJ(String nom, PNJ_discours discours, Orientation orientation, Tile tile)
    {
        super(orientation, tile);

        this.nom = nom;
        this.discours = new ArrayList<>();
        this.discours.add(discours);
        this.quetes = new HashMap<>();

        fillQuetes(discours);
    }

    public PNJ_discours getDiscoursWithId(int id)
    {
        for(int i = 0; i < discours.size(); i++)
        {
            PNJ_discours t = getDiscoursWithIdP(discours.get(i),id);
            if(t!=null)
                return t;
        }
        return null;
    }

    private PNJ_discours getDiscoursWithIdP(PNJ_discours current, int id)
    {
        for(int i = 0; i < current.getReponses().size(); i++)
        {
            System.out.println(current.getReponses().get(i).getId());
            if(current.getReponses().get(i).getId()==id)
            {
                return current.getReponses().get(i);
            }
            else
            {
                PNJ_discours r = getDiscoursWithIdP(current.getReponses().get(i),id);
                if(r!=null)
                    return r;
            }
        }
        return null;
    }

    private void fillQuetes(PNJ_discours d)
    {
        for(int i = 0; i < d.getReponses().size(); i++)
        {
            PNJ_discours rep = d.getReponses().get(i);
            if(rep.getQuete()!=null)
            {
                quetes.put(rep.getId(), rep.getQuete());
            }
            fillQuetes(rep);
        }
    }



    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public HashMap<Integer, Quete> getQuetes() {
        return quetes;
    }

    public void setQuetes(HashMap<Integer, Quete> quetes) {
        this.quetes = quetes;
    }

    public ArrayList<PNJ_discours> getDiscours() {
        return discours;
    }

    public void setDiscours(ArrayList<PNJ_discours> discours) {
        this.discours = discours;
    }




}
