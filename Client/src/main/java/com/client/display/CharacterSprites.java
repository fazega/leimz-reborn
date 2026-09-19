package com.client.display;

import com.client.entities.Orientation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/** Shared cleaned atlas. Each row is a direction; column zero is idle. */
public final class CharacterSprites {
    private static Image[][] frames;

    private CharacterSprites() {}

    public static Image get(Orientation direction, int frame) throws SlickException {
        if (frames == null) {
            Image atlas = new Image("data/Images/Persos/reborn/adventurer.png");
            frames = new Image[8][4];
            for (int row = 0; row < 8; row++) {
                for (int column = 0; column < 4; column++) {
                    int originX = column * atlas.getWidth() / 4;
                    int originY = row * atlas.getHeight() / 8;
                    int width = (column + 1) * atlas.getWidth() / 4 - originX;
                    int height = (row + 1) * atlas.getHeight() / 8 - originY;
                    Image cell = atlas.getSubImage(originX, originY, width, height);
                    // Keep the common canvas, scale and anchor across every pose.
                    frames[row][column] = cell.getScaledCopy(88, 88);
                }
            }
        }
        return frames[row(direction)][frame];
    }

    private static int row(Orientation direction) {
        switch (direction) {
            case BAS:
                return 0;
            case BAS_DROITE:
                return 1;
            case DROITE:
                return 2;
            case HAUT_DROITE:
                return 3;
            case HAUT:
                return 4;
            case HAUT_GAUCHE:
                return 5;
            case GAUCHE:
                return 6;
            case BAS_GAUCHE:
                return 7;
            default:
                return 0;
        }
    }
}
