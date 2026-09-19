package com.server.utils;

public class ShapeManager {

    public ShapeManager() {}

    public static boolean intersect(Polygon polygon1, Polygon polygon2) {
        for (int i = 0; i < polygon1.getPoints().size() - 1; i++) {
            Polygon droite1 = new Polygon();
            droite1.getPoints().add(polygon1.getPoints().get(i));
            droite1.getPoints().add(polygon1.getPoints().get(i + 1));
            for (int j = 0; j < polygon2.getPoints().size() - 1; j++) {
                Polygon droite2 = new Polygon();
                droite2.getPoints().add(polygon2.getPoints().get(i));
                droite2.getPoints().add(polygon2.getPoints().get(i + 1));
                if (intersectDroite(droite1, droite2)) return true;
            }
        }

        return false;
    }

    private static boolean intersectDroite(Polygon droite1, Polygon droite2) {
        Vector point1a = droite1.getPoints().get(0);
        Vector point1b = droite1.getPoints().get(1);
        Vector point2a = droite2.getPoints().get(0);
        Vector point2b = droite2.getPoints().get(1);

        if (point1a.x != point1b.x) {
            float m1 = (point1a.y - point1b.y) / (point1a.x - point1b.x);
            float p1 = point1a.y - m1 * point1b.x;
            if (point2a.x != point2b.x) {
                float m2 = (point2a.y - point2b.y) / (point2a.x - point2b.x);
                float p2 = point2a.y - m2 * point2a.x;
                Vector inter = null;
                if ((m2 - m1) != 0) {
                    inter = new Vector((p1 - p2) / (m2 - m1), m2 * ((p1 - p2) / (m2 - m1)) + p2);
                    if (point2b.x <= point2a.x) {
                        if (inter.x >= point2b.x && inter.x <= point2a.x) return true;
                    } else {
                        if (inter.x >= point2a.x && inter.x <= point2b.x) return true;
                    }
                } else {
                    if (p2 - p1 == 0) return true;
                }
            } else {
                Vector inter = new Vector(point2a.x, m1 * point2a.x + p1);
                if (point1b.x <= point1a.x) {
                    if (inter.x >= point1b.x && inter.x <= point1a.x) return true;
                } else {
                    if (inter.x >= point1a.x && inter.x <= point1b.x) return true;
                }
            }
        } else {
            if (point2a.x != point2b.x) {
                float m2 = (point2a.y - point2b.y) / (point2a.x - point2b.x);
                float p2 = point2a.y - m2 * point2a.x;
                Vector inter = new Vector(point1a.x, m2 * point1a.x + p2);
                if (point2b.x <= point2a.x) {
                    if (inter.x >= point2b.x && inter.x <= point2a.x) return true;
                } else {
                    if (inter.x >= point2a.x && inter.x <= point2b.x) return true;
                }
            } else {
                if (point1a.x == point2a.x) return true;
            }
        }
        return false;
    }
}
