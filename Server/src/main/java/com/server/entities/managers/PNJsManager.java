package com.server.entities.managers;

import java.util.ArrayList;
import com.server.entities.PNJ;


public class PNJsManager
{
    public ArrayList<PNJ> pnjs;

    public PNJsManager()
    {
        pnjs = new ArrayList<PNJ>();
    }

    public PNJ getPnj(String nom)
    {
        PNJ pnj = null;
        for(int i = 0; i < pnjs.size(); i++)
        {
            if(pnjs.get(i).getNom().equals(nom))
            {
                pnj = pnjs.get(i);
            }
        }
        return pnj;
    }

    public void add(PNJ p)
    {
        this.pnjs.add(p);
    }

    public ArrayList<PNJ> getPnjs() {
        return pnjs;
    }

    public void setPnjs(ArrayList<PNJ> pnjs) {
        this.pnjs = pnjs;
    }


}
