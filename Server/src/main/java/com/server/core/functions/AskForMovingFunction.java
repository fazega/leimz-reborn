package com.server.core.functions;

import java.util.ArrayList;


import com.server.core.Client;
import com.server.entities.Entity;
import com.server.map.Tile;
import com.server.utils.Polygon;
import com.server.utils.ShapeManager;
import com.server.utils.Vector;
import com.server.map.managers.MapManager;

public class AskForMovingFunction implements Functionable{

    @Override
    public void doSomething(String[] args, Client c) {
        if(args[1].equals("key"))
        {
            float posx = Float.parseFloat(args[2]);
            float posy = Float.parseFloat(args[3]);
            float sizex= Float.parseFloat(args[4]);
            float sizey = Float.parseFloat(args[5]);

            Tile destination = MapManager.instance.getTileReal(posx, posy);
            c.sendMessage("afm;"+(destination != null && !destination.isCollidable()));

        }

        /*else if(args[1].equals("arrow"))
        {

        }*/

    }



}
