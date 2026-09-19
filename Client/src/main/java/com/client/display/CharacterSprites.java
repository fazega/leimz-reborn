package com.client.display;

import com.client.entities.Orientation;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/** Stable directional artwork with independently articulated legs. */
public final class CharacterSprites {
    private static Image[] poses;
    private static final float[] JOINTS = {WalkPose.HIP, 0.80f, 0.92f, 1};

    private CharacterSprites() {}

    public static Image get(Orientation direction) throws SlickException {
        if (poses == null) load();
        return poses[direction.ordinal()];
    }

    private static void load() throws SlickException {
        Image atlas = new Image("data/Images/Persos/reborn/adventurer.png");
        Image[] sources = new Image[5];
        for (int row = 0; row < sources.length; row++) {
            int originY = row * atlas.getHeight() / 8;
            int left = atlas.getWidth(), right = 0, top = atlas.getHeight(), bottom = 0;
            for (int y = originY; y < (row + 1) * atlas.getHeight() / 8; y++) {
                for (int x = 0; x < atlas.getWidth() / 4; x++) {
                    if (atlas.getColor(x, y).a > 0.5f) {
                        left = Math.min(left, x);
                        right = Math.max(right, x + 1);
                        top = Math.min(top, y);
                        bottom = Math.max(bottom, y + 1);
                    }
                }
            }
            Image source = atlas.getSubImage(left, top, right - left, bottom - top);
            sources[row] = source.getScaledCopy((float) WalkPose.BODY_HEIGHT / source.getHeight());
        }
        Image[] loaded = new Image[Orientation.values().length];
        for (Orientation direction : Orientation.values()) {
            Image source = sources[WalkPose.sourceRow(direction)];
            loaded[direction.ordinal()] =
                    WalkPose.mirrored(direction) ? source.getFlippedCopy(true, false) : source;
        }
        poses = loaded;
    }

    public static void draw(
            Orientation direction,
            float phase,
            float amount,
            float anchorX,
            float anchorY,
            float scale) {
        Image image;
        try {
            image = get(direction);
        } catch (SlickException exception) {
            throw new IllegalStateException(exception);
        }
        if (amount <= 0) {
            image.draw(
                    anchorX - image.getWidth() * scale / 2,
                    anchorY - WalkPose.BODY_HEIGHT * scale,
                    image.getWidth() * scale,
                    WalkPose.BODY_HEIGHT * scale);
            return;
        }
        Color.white.bind();
        image.getTexture().bind();
        GL11.glBegin(GL11.GL_QUADS);
        for (int leg = 0; leg < 2; leg++) {
            for (int joint = 0; joint < JOINTS.length - 1; joint++) {
                float x1 = leg * 0.5f, x2 = x1 + 0.5f;
                float y1 = JOINTS[joint], y2 = JOINTS[joint + 1];
                vertex(image, direction, leg, x1, y1, phase, amount, anchorX, anchorY, scale);
                vertex(image, direction, leg, x2, y1, phase, amount, anchorX, anchorY, scale);
                vertex(image, direction, leg, x2, y2, phase, amount, anchorX, anchorY, scale);
                vertex(image, direction, leg, x1, y2, phase, amount, anchorX, anchorY, scale);
            }
        }
        // Torso stays on its fixed anchor with no frame-dependent size or recentering.
        vertex(image, direction, 0, 0, 0, phase, 0, anchorX, anchorY, scale);
        vertex(image, direction, 0, 1, 0, phase, 0, anchorX, anchorY, scale);
        vertex(image, direction, 0, 1, WalkPose.HIP, phase, 0, anchorX, anchorY, scale);
        vertex(image, direction, 0, 0, WalkPose.HIP, phase, 0, anchorX, anchorY, scale);
        GL11.glEnd();
    }

    private static void vertex(
            Image image,
            Orientation direction,
            int leg,
            float x,
            float y,
            float phase,
            float amount,
            float anchorX,
            float anchorY,
            float scale) {
        GL11.glTexCoord2f(
                image.getTextureOffsetX() + x * image.getTextureWidth(),
                image.getTextureOffsetY() + y * image.getTextureHeight());
        GL11.glVertex2f(
                anchorX
                        + ((x - 0.5f) * image.getWidth()
                                        + WalkPose.offsetX(direction, leg, y, phase) * amount)
                                * scale,
                anchorY
                        + ((y - 1) * WalkPose.BODY_HEIGHT
                                        + WalkPose.offsetY(direction, leg, y, phase) * amount)
                                * scale);
    }
}
