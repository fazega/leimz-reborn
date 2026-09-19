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

    private float zoomScale = 1.0f;
    private float fuzzy = 1.0f;

    private ArrayList<Entity> visible_entities;

    public Camera() {
        visible_entities = new ArrayList<Entity>();
    }

    public void focusOn(Tile tile, Vector2f decalage) {
        MapManager manager = MapManager.instance;
        Tile[][] grid = manager.getEntire_map().getGrille();
        if (manager.getMap_visible() != null) {
            for (Tile[] column : manager.getMap_visible().getGrille()) {
                for (Tile previous : column) previous.setDrawn(false);
            }
        }
        // One world-to-screen translation, independent of which staggered column contains us.
        Vector2f player = tile.getPos_real().copy().sub(decalage);
        Vector2f offset =
                new Vector2f(
                        Base.sizeOfScreen_x / 2f - player.x,
                        Base.sizeOfScreen_y / 2f + 40 - player.y);
        int startX = Math.max(0, (int) Math.floor(-offset.x / 40) - 6);
        int startY = Math.max(0, (int) Math.floor(-offset.y / 40) - 4);
        int endX =
                Math.min(grid.length, (int) Math.ceil((Base.sizeOfScreen_x - offset.x) / 40) + 6);
        // Tall scenery anchored below the viewport still needs to be drawn.
        int endY =
                Math.min(
                        grid[0].length,
                        (int) Math.ceil((Base.sizeOfScreen_y - offset.y) / 40) + 10);
        Tile[][] visible = new Tile[endX - startX][endY - startY];
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                Tile current = grid[x][y];
                current.setPos_screen(current.getPos_real().copy().add(offset));
                visible[x - startX][y - startY] = current;
            }
        }
        ArrayList<GroupTiles> groups = new ArrayList<>();
        for (GroupTiles group : manager.getEntire_map().getGroups()) {
            Vector2f position = group.getBase().getPos();
            if (position.x >= startX
                    && position.x < endX
                    && position.y >= startY
                    && position.y < endY) groups.add(group);
        }
        visible_entities.clear();
        if (EntitiesManager.instance != null) {
            for (Entity entity : EntitiesManager.instance.getEntities()) {
                if (entity == null || entity.getTile() == null) continue;
                Vector2f position = entity.getTile().getPos();
                if (position.x >= startX
                        && position.x < endX
                        && position.y >= startY
                        && position.y < endY) visible_entities.add(entity);
            }
        }
        manager.setAbsolute(offset);
        manager.setMap_visible(new Map(visible, groups));
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
