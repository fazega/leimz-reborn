package com.client.entities.managers;

import java.util.ArrayList;
import com.client.entities.PNJ;

public class PNJsManager {
    public ArrayList<PNJ> pnjs;

    public PNJsManager() {
        pnjs = new ArrayList<PNJ>();
    }

    public PNJ getPnj(String nom) {
        PNJ p = null;
        for (int i = 0; i < pnjs.size(); i++) {
            if (pnjs.get(i).getNom().equals(nom)) {
                p = pnjs.get(i);
            }
        }
        return p;
    }

    public void add(PNJ p) {
        this.pnjs.add(p);
    }

    public ArrayList<PNJ> getPnjs() {
        return pnjs;
    }

    public void setPnjs(ArrayList<PNJ> pnjs) {
        this.pnjs = pnjs;
    }
}
