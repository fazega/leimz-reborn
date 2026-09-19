package com.client.display;

import java.util.ArrayList;
import org.newdawn.slick.geom.Vector2f;
import com.client.entities.Entity;
import com.client.entities.managers.EntitiesManager;
import com.client.gamestates.Base;
import com.client.map.GroupTiles;
import com.client.map.Map;
import com.client.map.Tile;
import com.client.map.managers.MapManager;

public class Camera {

    private static int NB_TILES_X = (Base.sizeOfScreen_x / Base.Tile_x) + 1;
    private static int NB_TILES_Y = (Base.sizeOfScreen_y / Base.Tile_y) + 1;

    private Tile lastFocus = null;
    private Vector2f lastDecalage = null;

    private float zoomScale = 1.0f;
    private float fuzzy = 1.0f;

    private ArrayList<Entity> visible_entities;

    public Camera() {
        visible_entities = new ArrayList<Entity>();
    }

    public void focusOn(Tile tile, Vector2f decalage) {
        NB_TILES_X = (Base.sizeOfScreen_x / Base.Tile_x) + 1;
        NB_TILES_Y = (Base.sizeOfScreen_y / Base.Tile_y) + 1;
        if (lastFocus == null && lastDecalage == null) {
            lastFocus = tile;
            lastDecalage = decalage;
        } else if (lastFocus == tile && lastDecalage == decalage) return;

        int decalage_x = (int) decalage.x - 2 * Base.Tile_x * 2;
        int decalage_y = (int) decalage.y - Base.Tile_y * 2;

        // Recuperation de l'index de depart en x et y des Tile e afficher.
        int indiceStart_x = (int) (tile.getPos().x - (NB_TILES_X / 2));
        int indiceStart_y = (int) (tile.getPos().y - (NB_TILES_Y / 2));

        // Recuperation de l'index de fin en x et y des Tile e afficher.
        int indiceFin_x = indiceStart_x + NB_TILES_X;
        int indiceFin_y = indiceStart_y + NB_TILES_Y;

        // Maintenant, on ajoute x pour faire "deborder" la map pour avoir une map rempli sur
        // l'ecran de jeu
        indiceStart_x -= 4;
        indiceStart_y -= 2;
        indiceFin_x += 4;
        indiceFin_y += 2;

        // Attention aux cas ou le joueur se situe sur une extremite de la map.
        int x_offset = (indiceStart_x > 0) ? 0 : -indiceStart_x;
        int y_offset = (indiceStart_y > 0) ? 0 : -indiceStart_y;
        indiceStart_x = (indiceStart_x < 0) ? 0 : indiceStart_x;
        indiceStart_y = (indiceStart_y < 0) ? 0 : indiceStart_y;
        indiceFin_x =
                (indiceFin_x >= MapManager.instance.getEntire_map().getGrille().length)
                        ? MapManager.instance.getEntire_map().getGrille().length
                        : indiceFin_x;
        indiceFin_y =
                (indiceFin_y >= MapManager.instance.getEntire_map().getGrille()[0].length)
                        ? MapManager.instance.getEntire_map().getGrille()[0].length
                        : indiceFin_y;

        Tile[][] current_map = new Tile[indiceFin_x - indiceStart_x][indiceFin_y - indiceStart_y];

        // On parcourt alors les lignes et les colonnes
        for (int i = indiceStart_x, y = x_offset; i < indiceFin_x; i++, y++) {
            for (int j = indiceStart_y, z = y_offset; j < indiceFin_y; j++, z++) {
                int shift = i % 2 + (indiceStart_x + x_offset) % 2;
                shift *= Base.Tile_y / 2;

                // On determine la "vraie" position de la tile par rapport aux coordonnees
                MapManager.instance
                        .getEntire_map()
                        .getGrille()[i][j]
                        .setPos_screen(
                                new Vector2f(
                                        y * Base.Tile_x + decalage_x,
                                        z * Base.Tile_y + shift + decalage_y));
                current_map[y - x_offset][z - y_offset] =
                        MapManager.instance.getEntire_map().getGrille()[i][j];
            }
        }

        ArrayList<GroupTiles> groups = new ArrayList<>();
        for (GroupTiles group : MapManager.instance.getEntire_map().getGroups()) {
            if (group.getBase().getPos().x >= indiceStart_x
                    && group.getBase().getPos().x <= indiceFin_x
                    && group.getBase().getPos().y >= indiceStart_y
                    && group.getBase().getPos().y <= indiceFin_y) groups.add(group);
        }

        visible_entities.removeAll(visible_entities);
        for (int i = 0; i < current_map.length; i++) {
            for (int j = 0; j < current_map[i].length; j++) {
                for (int k = 0; k < EntitiesManager.instance.getEntities().size(); k++) {
                    if (EntitiesManager.instance.getEntities().get(k) != null) {
                        if (EntitiesManager.instance.getEntities().get(k).getTile() != null) {
                            if (EntitiesManager.instance
                                    .getEntities()
                                    .get(k)
                                    .getTile()
                                    .equals(current_map[i][j])) {
                                visible_entities.add(EntitiesManager.instance.getEntities().get(k));
                            }
                        }
                    }
                }
            }
        }

        MapManager.instance.setAbsolute(
                current_map[0][0].getPos_screen().copy().sub(current_map[0][0].getPos_real()));
        MapManager.instance.setMap_visible(new Map(current_map, groups));
    }

    public void zoom(float scale) {
        this.zoomScale = scale;
    }

    public float getFuzzy() {
        return fuzzy;
    }

    public void setFuzzy(float fuzzy) {
        this.fuzzy = fuzzy;
    }

    public float getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }

    public ArrayList<Entity> getVisible_entities() {
        return visible_entities;
    }

    public void setVisible_entities(ArrayList<Entity> visible_entities) {
        this.visible_entities = visible_entities;
    }
}
