package com.client.gameplay;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;

import com.client.gameplay.items.Equipement;
import com.client.gameplay.items.ObjetQuete;
import com.client.gameplay.items.SimpleItem;
import com.client.network.NetworkListener;
import com.client.utils.gui.PrincipalGui;

public class Inventaire implements NetworkListener {
    public ArrayList<SimpleItem> items;

    public Inventaire(ArrayList<SimpleItem> items) {
        this.items = items;
    }

    public Inventaire() {
        this.items = new ArrayList<SimpleItem>();
    }

    public void addItem(SimpleItem obj) {
        this.items.add(obj);
    }

    public ArrayList<SimpleItem> getItems() {
        return items;
    }

    public void setObjets(ArrayList<SimpleItem> items) {
        this.items = items;
    }

    @Override
    public void receiveMessage(String str) {
        String[] temp = str.split(";");
        LoadingList.setDeferredLoading(true);
        if (temp[0].equals("new")) {
            switch (temp[3]) {
                case "equipement":
                    try {
                        items.add(
                                new Equipement(
                                        temp[1],
                                        temp[4],
                                        temp[2],
                                        new Image("data/Images/Objets/anneau_dfer.png"),
                                        new Image("data/Images/Objets/anneau_dfer.png"),
                                        null,
                                        0));
                    } catch (SlickException e) {
                        e.printStackTrace();
                    }
                    break;
                case "objet_quete":
                    try {
                        items.add(
                                new ObjetQuete(
                                        temp[1],
                                        temp[2],
                                        new Image("data/Images/Objets/anneau_dfer.png"),
                                        new Image("data/Images/Objets/anneau_dfer.png"),
                                        null,
                                        0));
                    } catch (SlickException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            PrincipalGui.instance
                    .getChat_frame()
                    .appendRow("font_green", "[Objet reÃ§u : " + temp[1] + "]");
        }
        if (temp[0].equals("del")) {
            SimpleItem item = null;
            for (SimpleItem it : items) {
                if (it.getNom().equals(temp[1])) {
                    item = it;
                    break;
                }
            }
            items.remove(item);
            PrincipalGui.instance
                    .getChat_frame()
                    .appendRow("font_green", "[Objet donnÃ© : " + temp[1] + "]");
            System.out.println("item enlevÃ© !");
        }
        LoadingList.setDeferredLoading(false);
    }
}
