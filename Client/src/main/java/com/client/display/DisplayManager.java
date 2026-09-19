package com.client.display;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.HashMap;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.entities.PNJ;
import com.client.entities.Entity.Etat;
import com.client.entities.managers.EntitiesManager;
import com.client.entities.managers.PNJsManager;
import com.client.gameplay.managers.CombatManager;
import com.client.gamestates.Base;
import com.client.map.GroupTiles;
import com.client.map.Tile;
import com.client.map.TypeTile;
import com.client.map.managers.MapManager;

public class DisplayManager {

    // *******************Infos contenues dans ce gestionnaire********************
    // [permet de ne pas avoir a ecrire les getters et setters a chaque fois, du type
    // map_visible_manager.getPlayersManager().getJoueurs().get(0) pour le joueur 0]

    // La grille visible
    private Tile[][] current_grille;
    private Camera camera;
    private Graphics graphics;

    public DisplayManager(Camera camera) {
        this.camera = camera;
    }

    private void drawEntities(float scale) {
        current_grille = MapManager.instance.getMap_visible().getGrille();

        // Joueur principal
        Joueur main_player = EntitiesManager.instance.getPlayers_manager().getMain_player();

        ArrayList<PNJ> list_pnj = new ArrayList<PNJ>();
        for (int i = 0; i < camera.getVisible_entities().size(); i++) {
            if (camera.getVisible_entities().get(i) instanceof PNJ) {
                list_pnj.add((PNJ) camera.getVisible_entities().get(i));
            }
        }

        // On recupere les monstres et pnjs de la grille visible
        PNJsManager pnjs_manager = new PNJsManager();
        pnjs_manager.setPnjs(list_pnj);

        // On associe e chaque pnj un booleen qui indique si il a deje ete affiche
        HashMap<PNJ, Boolean> pnjsAffiche = new HashMap<PNJ, Boolean>();
        for (int i = 0; i < pnjs_manager.getPnjs().size(); i++) {
            pnjsAffiche.put(pnjs_manager.getPnjs().get(i), false);
        }

        // On associe e chaque joueur (il n'y en a qu'un pour l'instant) un booleen qui indique si
        // il a deje ete affiche
        boolean joueurAffiche = false;

        // On passe en boucle de l'indice de depart a l'indice de fin
        // ATTENTION on parcourt ici ligne par ligne et non colonne par colonne pour Ã©viter des
        // aberrations d'affichage
        for (int i = 0; i < current_grille[0].length; i++) {
            int first = (int) (current_grille[0][0].getPos().x % 2);
            for (int h = 0; h < 2; h++) {
                for (int j = first; j < current_grille.length; j += 2) {
                    Tile tile = current_grille[j][i];
                    for (int u = 1; u < tile.getTypes().size(); u++) {
                        TypeTile type = tile.getTypes().get(u);
                        if (type != null) {
                            // On cree la position d'affichage
                            Vector2f pos_aff = new Vector2f();
                            // La position vaut la position de la tile moins la position de la base,
                            // creee auparavant par le level designer pour chaque objet
                            pos_aff.x = tile.getPos_screen().x - type.getBase().getX();
                            pos_aff.y = tile.getPos_screen().y - type.getBase().getY();

                            // On cree la shape associee pour les collisions
                            Rectangle ob =
                                    new Rectangle(
                                            pos_aff.x,
                                            pos_aff.y,
                                            type.getImg().getWidth(),
                                            type.getImg().getHeight() - Base.Tile_y / 2);

                            Rectangle mpps =
                                    new Rectangle(
                                            main_player.getPos_real_on_screen().x
                                                    + main_player.getPieds().getX(),
                                            main_player.getPos_real_on_screen().y
                                                    + main_player.getPieds().getY(),
                                            main_player.getPieds().getWidth(),
                                            main_player.getPieds().getHeight());

                            // --------------------------------JOUEUR---------------------------

                            // Si on est en intersection
                            if (ob.intersects(mpps)) {
                                // -----------CAS OU LE JOUEUR EST DERRIERE
                                // L'OBJET------------------

                                // Si le joueur n'a pas encore ete affiche
                                if (joueurAffiche == false) {
                                    // On affiche le joueur
                                    main_player.draw(scale);
                                    // On indique que le joueur a ete affiche
                                    joueurAffiche = true;
                                }
                            }

                            // -----------------------------PNJs--------------------

                            for (int k = 0; k < pnjs_manager.getPnjs().size(); k++) {
                                if (pnjs_manager.getPnjs().get(k).getImgs_repos() != null) {
                                    Rectangle pnps =
                                            new Rectangle(
                                                    pnjs_manager
                                                                    .getPnjs()
                                                                    .get(k)
                                                                    .getPos_real_on_screen()
                                                                    .x
                                                            + pnjs_manager
                                                                    .getPnjs()
                                                                    .get(k)
                                                                    .getPieds()
                                                                    .getX(),
                                                    pnjs_manager
                                                                    .getPnjs()
                                                                    .get(k)
                                                                    .getPos_real_on_screen()
                                                                    .y
                                                            + pnjs_manager
                                                                    .getPnjs()
                                                                    .get(k)
                                                                    .getPieds()
                                                                    .getY(),
                                                    pnjs_manager
                                                            .getPnjs()
                                                            .get(k)
                                                            .getPieds()
                                                            .getWidth(),
                                                    pnjs_manager
                                                            .getPnjs()
                                                            .get(k)
                                                            .getPieds()
                                                            .getHeight());
                                    if (ob.intersects(pnps)) {
                                        if (!pnjsAffiche.get(pnjs_manager.getPnjs().get(k))) {
                                            pnjs_manager.getPnjs().get(k).draw();
                                            pnjsAffiche.put(pnjs_manager.getPnjs().get(k), true);
                                        }
                                    }
                                }
                            }

                            type.getImg().draw(pos_aff.x, pos_aff.y);
                        }
                    }

                    for (GroupTiles group : MapManager.instance.getMap_visible().getGroups()) {
                        Tile tile_base = group.getBase();
                        if (tile_base.equals(tile)) {
                            // On cree la position d'affichage
                            Vector2f pos_aff = new Vector2f();
                            // La position vaut la position de la tile moins la position de la base,
                            // creee auparavant par le level designer pour chaque objet
                            // De plus, on ajoute une petite formule demontrable simplement en
                            // repere orthonorme pour le zoom
                            pos_aff.x =
                                    (tile_base.getPos_screen().x
                                                    - (group.getType()).getBase().getX())
                                            + ((1 - scale) * Base.Tile_x);
                            pos_aff.y =
                                    (tile_base.getPos_screen().y
                                                    - (group.getType()).getBase().getY())
                                            + ((1 - scale) * Base.Tile_y / 2);

                            // On cree la shape associee pour les collisions
                            Rectangle ob =
                                    new Rectangle(
                                            pos_aff.x,
                                            pos_aff.y,
                                            group.getType().getImg().getWidth(),
                                            group.getType().getImg().getHeight() - Base.Tile_y / 2);

                            Rectangle mpps =
                                    new Rectangle(
                                            main_player.getPos_real_on_screen().x
                                                    + main_player.getPieds().getX(),
                                            main_player.getPos_real_on_screen().y
                                                    + main_player.getPieds().getY(),
                                            main_player.getPieds().getWidth(),
                                            main_player.getPieds().getHeight());

                            // --------------------------------JOUEUR---------------------------

                            // Si on est en intersection
                            if (ob.intersects(mpps)) {
                                // -----------CAS OU LE JOUEUR EST DERRIERE
                                // L'OBJET------------------

                                // Si le joueur n'a pas encore ete affiche
                                if (joueurAffiche == false) {
                                    // On affiche le joueur
                                    main_player.draw(scale);
                                    // On indique que le joueur a ete affiche
                                    joueurAffiche = true;
                                }
                            }

                            // -----------------------------PNJs--------------------

                            /*for(int k = 0; k < pnjs_manager.getPnjs().size(); k++)
                            {
                                if(pnjs_manager.getPnjs().get(k).getImgs_repos()!=null)
                                {
                                    Rectangle pnps = new Rectangle(pnjs_manager.getPnjs().get(k).getPos_real_on_screen().x+pnjs_manager.getPnjs().get(k).getPieds().getX(),
                                            pnjs_manager.getPnjs().get(k).getPos_real_on_screen().y+pnjs_manager.getPnjs().get(k).getPieds().getY(),
                                            pnjs_manager.getPnjs().get(k).getPieds().getWidth(), pnjs_manager.getPnjs().get(k).getPieds().getHeight());
                                    if(ob.intersects(pnps))
                                    {
                                        if(!pnjsAffiche.get(pnjs_manager.getPnjs().get(k)))
                                        {
                                            pnjs_manager.getPnjs().get(k).draw();
                                            pnjsAffiche.put(pnjs_manager.getPnjs().get(k), true);
                                        }
                                    }
                                }
                            }*/

                            (group.getType()).getImg().draw(pos_aff.x, pos_aff.y, scale);
                        }
                    }
                }
                first = (first == 1) ? 0 : 1;
            }
        }

        for (int k = 0;
                k < EntitiesManager.instance.getPlayers_manager().getJoueurs().size();
                k++) {
            if (EntitiesManager.instance
                            .getPlayers_manager()
                            .getJoueurs()
                            .get(k)
                            .getCurrent_img_repos()
                    != null)
                EntitiesManager.instance.getPlayers_manager().getJoueurs().get(k).draw();
            else EntitiesManager.instance.getPlayers_manager().getJoueurs().get(k).initImgs();
        }

        // Si le joueur n'a finalement pas ete affiche (dans le cas ou, par ex, il n'y a aucun objet
        // la ou il est)
        if (joueurAffiche == false) {
            // On l'affiche
            main_player.draw(scale);
        }
        // Si les pnjs n'ont finalement pas ete affiches (dans le cas ou, par ex, il n'y a aucun
        // objet la ou ils sont)
        Iterator<PNJ> i = pnjsAffiche.keySet().iterator();
        int k = 0;
        while (i.hasNext()) {
            if (!pnjsAffiche.get(i.next()) && pnjs_manager.getPnjs().get(k).getImgs_repos() != null)
                pnjs_manager.getPnjs().get(k).draw();
            k++;
        }
    }

    public void drawAll(Graphics g, Vector2f mousePos) {
        this.graphics = g;

        float scale = camera.getZoomScale();

        WorldBorder.drawGround(g);

        if (CombatManager.instance.getCurrent_combat() != null) {
            CombatManager.instance.getCurrent_combat().draw(g, mousePos, scale);
            MapManager.instance.drawMap(
                    MapManager.instance.getMap_visible(), new Color(255, 255, 255, 0.4f), scale);
        } else {
            MapManager.instance.drawMap(MapManager.instance.getMap_visible(), null, scale);
        }
        WorldBorder.drawRocks(g);
        drawEntities(scale);

        if (MainJoueur.instance.getPerso().getCurrent_sort() != null) {
            g.setColor(new Color(60, 0, 255));
            g.drawLine(
                    MainJoueur.instance.getPos_real_on_screen().x
                            + (MainJoueur.instance.getSize().x / 2),
                    MainJoueur.instance.getPos_real_on_screen().y
                            + (MainJoueur.instance.getSize().y / 2),
                    mousePos.x,
                    mousePos.y);
            g.setColor(Color.white);
        }

        /*java.awt.Font font2 = new java.awt.Font("Trebuchet MS", 25, java.awt.Font.BOLD);
        UnicodeFont font = null;
        font = new UnicodeFont(font2, 15, true, false);
        font.addAsciiGlyphs();
        font.addGlyphs(400,600);
        font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
            e.printStackTrace();
        }*/
        if (MainJoueur.instance.getEtat().equals(Etat.OVER)
                || MainJoueur.instance.getEtat().equals(Etat.CLICKED)) {
            MainJoueur.instance.getCurrent_img_repos().setAlpha(0.8f);
            graphics.drawString(
                    MainJoueur.instance.getPerso().getNom(),
                    MainJoueur.instance.getPos_real_on_screen().x
                            + (MainJoueur.instance.getSize().x / 2)
                            - (graphics.getFont().getWidth(MainJoueur.instance.getPerso().getNom())
                                    / 2),
                    MainJoueur.instance.getPos_real_on_screen().y - 20);
        } else {
            MainJoueur.instance.getCurrent_img_repos().setAlpha(1f);
        }

        for (int i = 0; i < camera.getVisible_entities().size(); i++) {
            if (camera.getVisible_entities().get(i) instanceof PNJ) {
                PNJ pnj = (PNJ) camera.getVisible_entities().get(i);
                if (pnj.getImgs_repos() != null) {
                    if (pnj.getEtat().equals(Etat.OVER) || pnj.getEtat().equals(Etat.CLICKED)) {
                        pnj.getCurrent_img_repos().setAlpha(0.8f);
                        graphics.drawString(
                                pnj.getNom(),
                                pnj.getPos_real_on_screen().x
                                        + (pnj.getSize().x / 2)
                                        - (graphics.getFont().getWidth(pnj.getNom()) / 2),
                                pnj.getPos_real_on_screen().y - 20);
                    } else {
                        pnj.getCurrent_img_repos().setAlpha(1f);
                    }
                }
            }
        }
    }
}
