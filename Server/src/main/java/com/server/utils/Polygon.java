package com.server.utils;

import java.util.ArrayList;

public class Polygon
{
    public ArrayList<Vector> points;

    public Polygon(ArrayList<Vector> points)
    {
        this.points = points;
    }

    public Polygon()
    {
        this.points = new ArrayList<>();
    }

    public void addPoint(Vector point)
    {
        points.add(point);
    }

    public ArrayList<Vector> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Vector> points) {
        this.points = points;
    }
}
