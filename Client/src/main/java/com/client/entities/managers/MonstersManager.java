package com.client.entities.managers;

import java.util.ArrayList;

import com.client.entities.Monstre;

public class MonstersManager
{
    private ArrayList<Monstre> monsters;
    private long tpsReap;
    private int nbMax;

    public MonstersManager(ArrayList<Monstre> monsters, long tpsReap, int nbMax)
    {
        this.setTpsReap(tpsReap);
        this.setNbMax(nbMax);

        this.monsters = monsters;
    }

    public MonstersManager() {
        this.setTpsReap(100);
        this.setNbMax(2);

        monsters = new ArrayList<Monstre>();
    }

    public ArrayList<Monstre> getMonsters() {
        return monsters;
    }

    public void setMonsters(ArrayList<Monstre> monsters) {
        this.monsters = monsters;
    }

    public long getTpsReap() {
        return tpsReap;
    }

    public void setTpsReap(long tpsReap) {
        this.tpsReap = tpsReap;
    }

    public int getNbMax() {
        return nbMax;
    }

    public void setNbMax(int nbMax) {
        this.nbMax = nbMax;
    }
}
