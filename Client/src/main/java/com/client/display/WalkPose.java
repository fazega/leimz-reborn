package com.client.display;

import com.client.entities.Orientation;

/** Continuous two-leg gait in screen space, independent of sprite frames and camera position. */
public final class WalkPose {
    public static final int BODY_HEIGHT = 76;
    public static final float HIP = 0.65f;

    private WalkPose() {}

    public static int sourceRow(Orientation direction) {
        switch (direction) {
            case BAS:
                return 0;
            case BAS_DROITE:
            case BAS_GAUCHE:
                return 1;
            case DROITE:
            case GAUCHE:
                return 2;
            case HAUT_DROITE:
            case HAUT_GAUCHE:
                return 3;
            default:
                return 4;
        }
    }

    public static boolean mirrored(Orientation direction) {
        return direction == Orientation.GAUCHE
                || direction == Orientation.HAUT_GAUCHE
                || direction == Orientation.BAS_GAUCHE;
    }

    public static float horizontal(Orientation direction) {
        switch (direction) {
            case DROITE:
                return 1;
            case GAUCHE:
                return -1;
            case BAS_DROITE:
            case HAUT_DROITE:
                return 0.8f;
            case BAS_GAUCHE:
            case HAUT_GAUCHE:
                return -0.8f;
            default:
                return 0;
        }
    }

    public static float vertical(Orientation direction) {
        switch (direction) {
            case BAS:
                return 1;
            case HAUT:
                return -1;
            case BAS_DROITE:
            case BAS_GAUCHE:
                return 0.5f;
            case HAUT_DROITE:
            case HAUT_GAUCHE:
                return -0.5f;
            default:
                return 0;
        }
    }

    public static float offsetX(Orientation direction, int leg, float y, float phase) {
        float weight = Math.min(1, Math.max(0, (y - HIP) / (0.92f - HIP)));
        return horizontal(direction) * 7 * (float) Math.cos(phase + leg * Math.PI) * weight;
    }

    public static float offsetY(Orientation direction, int leg, float y, float phase) {
        float weight = Math.min(1, Math.max(0, (y - HIP) / (0.92f - HIP)));
        double cycle = phase + leg * Math.PI;
        float lift = Math.max(0, (float) Math.sin(cycle));
        return (vertical(direction) * 4 * (float) Math.cos(cycle) - 5 * lift) * weight;
    }
}
