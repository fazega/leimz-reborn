package com.server.gameplay.managers;

import java.util.ArrayList;
import com.server.gameplay.Quete;

public class QuetesManager {
    private ArrayList<Quete> quetes;

    public QuetesManager(ArrayList<Quete> quetes) {
        this.quetes = quetes;
    }

    public QuetesManager(Quete quete) {
        this.quetes = new ArrayList<Quete>();
        quetes.add(quete);
    }

    public QuetesManager() {
        this.quetes = new ArrayList<Quete>();
    }

    public void addQuete(Quete quete) {
        quetes.add(quete);
    }

    public ArrayList<Quete> getQuetes() {
        return quetes;
    }

    public void setQuetes(ArrayList<Quete> quetes) {
        this.quetes = quetes;
    }
}
