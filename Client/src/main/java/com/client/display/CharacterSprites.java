package com.client.display;

import com.client.entities.Orientation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/** Shared idle poses and an eight-phase walk cycle. Left-facing walks mirror right-facing rows. */
public final class CharacterSprites {
    private static Image[][] frames;

    private CharacterSprites() {}

    public static Image get(Orientation direction, int frame) throws SlickException {
        if (frames == null) {
            Image atlas = new Image("data/Images/Persos/reborn/adventurer.png");
            Image walk = new Image("data/Images/Persos/reborn/adventurer-walk.png");
            if (walk.getWidth() != 1586 || walk.getHeight() != 992)
                throw new IllegalStateException("Walk atlas changed; update frame bounds first");
            // The generated sheet has uneven row spacing. Explicit bounds prevent adjacent
            // heads/feet bleeding into a frame; do not assume a mathematically uniform grid.
            int[] rowTop = {0, 198, 392, 584, 776};
            int[] rowBottom = {190, 386, 577, 770, 971};
            frames = new Image[8][9];
            int[] walkRows = {0, 1, 2, 3, 4, 3, 2, 1};
            for (int row = 0; row < 8; row++) {
                int idleY = row * atlas.getHeight() / 8;
                int idleBottom = idleY;
                for (int y = idleY; y < (row + 1) * atlas.getHeight() / 8; y++) {
                    for (int x = 0; x < atlas.getWidth() / 4; x++) {
                        if (atlas.getColor(x, y).a > 0.5f) idleBottom = y + 1;
                    }
                }
                frames[row][0] =
                        atlas.getSubImage(0, idleY, atlas.getWidth() / 4, idleBottom - idleY)
                                .getScaledCopy(88f / (atlas.getWidth() / 4));
                int walkRow = walkRows[row];
                for (int column = 0; column < CharacterMotion.WALK_FRAME_COUNT; column++) {
                    int x = column * walk.getWidth() / 8;
                    int y = rowTop[walkRow];
                    int cellWidth = (column + 1) * walk.getWidth() / 8 - x;
                    int left = cellWidth, right = 0;
                    for (int headY = y; headY < y + 45; headY++) {
                        for (int headX = 0; headX < cellWidth; headX++) {
                            if (walk.getColor(x + headX, headY).a > 0.5f) {
                                left = Math.min(left, headX);
                                right = Math.max(right, headX);
                            }
                        }
                    }
                    // Register the head, not the changing silhouette of the swinging limbs.
                    x += (left + right - cellWidth) / 2;
                    Image cell = walk.getSubImage(x, y, cellWidth, rowBottom[walkRow] - y);
                    if (row > 4) cell = cell.getFlippedCopy(true, false);
                    frames[row][column + 1] = cell.getScaledCopy(80f / cellWidth);
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
