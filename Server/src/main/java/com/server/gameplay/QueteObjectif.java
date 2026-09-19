package com.server.gameplay;

import java.util.ArrayList;

import com.gameplay.items.SimpleItem;
import com.server.entities.managers.EntitiesManager;
import com.server.gameplay.managers.ItemsManager;
import com.server.utils.Vector;

public class QueteObjectif {
    private String description;
    private boolean accompli;
    private ArrayList<SimpleItem> recompenses;
    private float argentrecompense;
    private ArrayList<Object> objectifs;
    private String text_objectif;
    private String type;
    private int id;

    public QueteObjectif(String description, String type, String text_objectif, int id) {
        this.description = description;
        this.text_objectif = text_objectif;
        this.type = type;
        this.objectifs = new ArrayList<>();
        this.recompenses = new ArrayList<>();
        this.id = id;
    }

    public QueteObjectif(
            String description,
            String type,
            String text_objectif,
            int id,
            ArrayList<SimpleItem> recompenses,
            int argentrecompense) {
        this.description = description;
        this.text_objectif = text_objectif;
        this.type = type;
        this.objectifs = new ArrayList<>();
        this.recompenses = recompenses;
        this.argentrecompense = argentrecompense;
    }

    public void initObj() {
        if (type.equals("tile")) {
            String[] pos = text_objectif.substring(1, text_objectif.length() - 1).split(",");
            this.objectifs.add(new Vector(Float.parseFloat(pos[0]), Float.parseFloat(pos[1])));
        }
        if (type.equals("parler_a")) {
            this.objectifs.add(EntitiesManager.instance.getPnjs_manager().getPnj(text_objectif));
        }
        if (type.equals("don_objet_a")) {
            String[] temp = text_objectif.split("::");
            this.objectifs.add(EntitiesManager.instance.getPnjs_manager().getPnj(temp[0]));
            this.objectifs.add(ItemsManager.instance.getItem(temp[1]));
        }
    }

    public String getText_objectif() {
        return text_objectif;
    }

    public void setText_objectif(String text_objectif) {
        this.text_objectif = text_objectif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAccompli() {
        return accompli;
    }

    public void setAccompli(boolean accompli) {
        this.accompli = accompli;
    }

    public ArrayList<Object> getObjectifs() {
        return objectifs;
    }

    public void setObjectifs(ArrayList<Object> objectifs) {
        this.objectifs = objectifs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<SimpleItem> getRecompenses() {
        return recompenses;
    }

    public void setRecompenses(ArrayList<SimpleItem> recompenses) {
        this.recompenses = recompenses;
    }

    public float getArgentrecompense() {
        return argentrecompense;
    }

    public void setArgentrecompense(float argentrecompense) {
        this.argentrecompense = argentrecompense;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
