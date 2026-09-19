package com.server.gameplay;

import java.util.ArrayList;
import com.gameplay.items.SimpleItem;


public class Inventaire
{
    private ArrayList<SimpleItem> items;

    public Inventaire(ArrayList<SimpleItem> items)
    {
        this.items = items;
    }

    public Inventaire()
    {
        this.items = new ArrayList<SimpleItem>();
    }

    public void addItem(SimpleItem obj)
    {
        this.items.add(obj);
    }

    public ArrayList<SimpleItem> getItems() {
        return items;
    }

    public void setObjets(ArrayList<SimpleItem> items) {
        this.items = items;
    }
}
