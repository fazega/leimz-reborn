package com.server.core.functions;

import java.util.ArrayList;

import com.server.core.Client;
import com.server.entities.Entity;
import com.server.entities.Joueur;
import com.server.entities.PNJ;

public class RefreshStateFunction implements Functionable {
    public RefreshStateFunction() {}

    @Override
    public void doSomething(String[] args, Client c) {
        ArrayList<Entity> loaded_entities = c.getCompte().getCurrent_joueur().getLoaded_entities();
        // ArrayList<ArrayList<Tile>> grille =
        // MapManager.instance.getTilesAutour(c.getCompte().getCurrent_joueur().getTile(),
        // GlobalConstant.nbCaseNear);

        for (int i = 0; i < loaded_entities.size(); i++) {
            if (loaded_entities.get(i).getPos_real_x() != loaded_entities.get(i).getPos_old_x()
                    || loaded_entities.get(i).getPos_real_y()
                            != loaded_entities.get(i).getPos_old_y()) {
                if (loaded_entities.get(i) instanceof Joueur
                        && loaded_entities.get(i) != c.getCompte().getCurrent_joueur()) {
                    c.sendMessage(
                            "s;j;"
                                    + ((Joueur) loaded_entities.get(i)).getPerso().getNom()
                                    + ";pos;"
                                    + loaded_entities.get(i).getPos_real_x()
                                    + ";"
                                    + loaded_entities.get(i).getPos_real_y()
                                    + ";"
                                    + loaded_entities.get(i).stringOrientation());
                } else if (loaded_entities.get(i) instanceof PNJ) {
                    c.sendMessage(
                            "s;pnj;"
                                    + ((PNJ) loaded_entities.get(i)).getNom()
                                    + ";pos;"
                                    + loaded_entities.get(i).getPos_real_x()
                                    + ";"
                                    + loaded_entities.get(i).getPos_real_y()
                                    + ";"
                                    + loaded_entities.get(i).stringOrientation());
                }
                loaded_entities.get(i).setPos_old_x(loaded_entities.get(i).getPos_real_x());
                loaded_entities.get(i).setPos_old_y(loaded_entities.get(i).getPos_real_y());
            }
        }
    }
}
