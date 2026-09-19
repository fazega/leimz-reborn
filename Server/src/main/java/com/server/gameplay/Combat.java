package com.server.gameplay;

import java.util.ArrayList;

import com.server.map.Tile;
import com.server.utils.Chrono;

public class Combat {
    private ArrayList<Equipe> equipes;
    private ArrayList<Tile> zone;

    public enum EtatCombat {
        INIT,
        EN_COURS,
        FINI
    };

    private EtatCombat etat;
    private Chrono timer_start;
    private static long time_start = 3000;

    public Combat(ArrayList<Equipe> equipes, ArrayList<Tile> zone) {
        this.equipes = equipes;
        this.zone = zone;

        etat = EtatCombat.INIT;

        timer_start = new Chrono(time_start);
    }

    public void stop() {
        this.etat = EtatCombat.FINI;
    }

    public ArrayList<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(ArrayList<Equipe> equipes) {
        this.equipes = equipes;
    }

    public ArrayList<Tile> getZone() {
        return zone;
    }

    public void setZone(ArrayList<Tile> zone) {
        this.zone = zone;
    }

    public EtatCombat getEtat() {
        return etat;
    }

    public void setEtat(EtatCombat etat) {
        this.etat = etat;
    }

    public Chrono getTimer_start() {
        return timer_start;
    }

    public void setTimer_start(Chrono timer_start) {
        this.timer_start = timer_start;
    }
}
