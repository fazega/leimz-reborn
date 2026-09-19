package com.server.gameplay.managers;

import java.util.ArrayList;
import com.gameplay.items.SimpleItem;

public class ItemsManager {

    private ArrayList<SimpleItem> items;
    public static ItemsManager instance;

    public ItemsManager()
    {
        instance = this;
        items = new ArrayList<>();
    }

    public SimpleItem getItem(String nom)
    {
        for(SimpleItem item : items)
        {
            if(item.getNom().equals(nom))
                return item;
        }
        return null;
    }

    public SimpleItem getItem(int id)
    {
        for(SimpleItem item : items)
        {
            if(item.getId() == id)
                return item;
        }
        return null;
    }

    public ArrayList<SimpleItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<SimpleItem> items) {
        this.items = items;
    }
}
