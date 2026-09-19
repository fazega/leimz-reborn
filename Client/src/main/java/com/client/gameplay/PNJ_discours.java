package com.client.gameplay;

import java.util.ArrayList;

public class PNJ_discours {
    private String discours;
    private PNJ_discours parent;
    private ArrayList<PNJ_discours> reponses;
    private int id;

    public PNJ_discours(
            String discours, PNJ_discours parent, ArrayList<PNJ_discours> reponses, int id) {
        super();
        this.discours = discours;
        this.parent = parent;
        this.reponses = reponses;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiscours() {
        return discours;
    }

    public void setDiscours(String discours) {
        this.discours = discours;
    }

    public PNJ_discours getParent() {
        return parent;
    }

    public void setParent(PNJ_discours parent) {
        this.parent = parent;
    }

    public ArrayList<PNJ_discours> getReponses() {
        return reponses;
    }

    public void setReponses(ArrayList<PNJ_discours> reponses) {
        this.reponses = reponses;
    }

    public PNJ_discours parent(int p) {
        PNJ_discours ret = this;

        for (int i = 0; i < p; i++) ret = ret.parent;

        return ret;
    }

    public void addReponses(PNJ_discours reponse) {
        this.reponses.add(reponse);
    }

    public ArrayList<String> getReponsesString() {
        ArrayList<String> rep = new ArrayList<String>();
        for (PNJ_discours p : reponses) rep.add(p.getDiscours());
        return rep;
    }
}
