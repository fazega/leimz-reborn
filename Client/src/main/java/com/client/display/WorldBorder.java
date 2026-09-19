package com.client.display;

import com.client.gamestates.Base;
import com.client.map.Tile;
import com.client.map.managers.MapManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

/** A continuous rocky wilderness beyond the world's impassable outermost tiles. */
public final class WorldBorder {
    private static final Color EARTH = new Color(69, 79, 54);
    private static final Color SHADOW = new Color(42, 53, 43, 150);
    private static final Color ROCK_LIGHT = new Color(137, 147, 110);
    private static final Color ROCK_TOP = new Color(162, 167, 126);
    private static final Color ROCK_DARK = new Color(88, 106, 84);
    private static final Color MOSS = new Color(111, 139, 62);

    private WorldBorder() {}

    public static void drawGround(Graphics graphics) {
        graphics.setColor(EARTH);
        graphics.fillRect(0, 0, Base.sizeOfScreen_x, Base.sizeOfScreen_y);
        graphics.setColor(Color.white);
    }

    public static void drawRocks(Graphics graphics) {
        MapManager maps = MapManager.instance;
        Tile[][] grid = maps.getEntire_map().getGrille();
        Vector2f offset = maps.getAbsolute();
        int firstX = (int) Math.floor(-offset.x / 40) - 3;
        int lastX = (int) Math.ceil((Base.sizeOfScreen_x - offset.x) / 40) + 2;
        int firstY = (int) Math.floor(-offset.y / 40) - 2;
        int lastY = (int) Math.ceil((Base.sizeOfScreen_y - offset.y) / 40) + 4;
        // Back-to-front rows; peaks may project above their tile into the viewport.
        for (int y = firstY; y <= lastY; y++) {
            for (int parity = 0; parity < 2; parity++) {
                for (int x = firstX; x <= lastX; x++) {
                    if (Math.floorMod(x, 2) != parity) continue;
                    if (x > 0 && y > 0 && x < grid.length - 1 && y < grid[0].length - 1) continue;
                    int variation = Math.floorMod(x * 7349 + y * 9151, 31);
                    float left = x * 40 + offset.x;
                    float bottom = y * 40 + parity * 20 + offset.y + 40;
                    drawRock(graphics, left, bottom, 46 + variation);
                }
            }
        }
        graphics.setColor(Color.white);
    }

    private static void drawRock(Graphics graphics, float x, float y, float height) {
        graphics.setColor(SHADOW);
        graphics.fillOval(x + 3, y - 23, 77, 27);
        float peakX = x + 32;
        float peakY = y - height;
        fill(
                graphics,
                ROCK_LIGHT,
                x + 3,
                y - 15,
                x + 14,
                peakY + 17,
                peakX,
                peakY,
                x + 43,
                peakY + 24,
                x + 46,
                y - 3,
                x + 24,
                y);
        fill(
                graphics,
                ROCK_DARK,
                x + 46,
                y - 3,
                x + 43,
                peakY + 24,
                peakX,
                peakY,
                x + 60,
                peakY + 10,
                x + 78,
                y - 19);
        fill(
                graphics,
                ROCK_TOP,
                x + 14,
                peakY + 17,
                peakX,
                peakY,
                x + 60,
                peakY + 10,
                x + 43,
                peakY + 24);
        fill(
                graphics, MOSS, x + 8, y - 17, x + 23, y - 22, x + 35, y - 11, x + 25, y - 5,
                x + 13, y - 7);
    }

    private static void fill(Graphics graphics, Color color, float... points) {
        graphics.setColor(color);
        graphics.fill(new Polygon(points));
    }
}
