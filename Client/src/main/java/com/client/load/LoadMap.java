package com.client.load;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.loading.LoadingList;

import com.client.map.GroupTiles;
import com.client.map.Tile;
import com.client.map.managers.MapManager;
import com.client.network.NetworkManager;

public class LoadMap implements Runnable {
    private Tile[][] grille;
    private ArrayList<GroupTiles> groups;
    private Thread t;
    private int purcent;

    public LoadMap() {
        this.purcent = 0;
        t = new Thread(this);
    }

    public void start() {
        t.start();
    }

    public int getPurcent() {
        return purcent;
    }

    public void setPurcent(int purcent) {
        this.purcent = purcent;
    }

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }

    @Override
    public void run() {
        purcent += 2;

        NetworkManager.instance.sendToServer("lo;map"); // load map
        NetworkManager.instance.waitForNewMessage("map");
        String[] args_map = NetworkManager.instance.receiveFromServer("map").split(";");

        int max_x = Integer.parseInt(args_map[0]);
        int max_y = Integer.parseInt(args_map[1]);

        grille = new Tile[max_x + 1][max_y + 1];
        for (int i = 0; i < max_x + 1; i++) {
            for (int j = 0; j < max_y + 1; j++) {
                grille[i][j] = new Tile(new Vector2f(i, j), null);
            }
        }
        purcent++;

        LoadingList.setDeferredLoading(true);
        MapManager.initTypesTile();

        groups = new ArrayList<GroupTiles>();
        NetworkManager.instance.waitForNewMessage("map");
        String[] args_groups = NetworkManager.instance.receiveFromServer("map").split(";");
        for (int i = 0; i + 3 < args_groups.length; i += 4) {
            groups.add(
                    new GroupTiles(
                            grille[Integer.parseInt(args_groups[i + 1])][
                                    Integer.parseInt(args_groups[i + 2])],
                            new ArrayList<Tile>(),
                            MapManager.getTypeTile(args_groups[i + 3]),
                            Integer.parseInt(args_groups[i])));
        }

        int n = 0;
        int zero = purcent;
        do {
            NetworkManager.instance.receiveFromServerPossible();
            NetworkManager.instance.waitForNewMessage("map");
            String[] args_tile = NetworkManager.instance.receiveFromServer("map").split(";");
            for (int u = 1; u < args_tile.length; u += 5) {
                int id_x = Integer.parseInt(args_tile[u]);
                int id_y = Integer.parseInt(args_tile[u + 1]);
                int id_groupe = Integer.parseInt(args_tile[u + 4]);
                grille[id_x][id_y].addTypes(MapManager.getTypeTile(args_tile[u + 2]));
                for (String layer : args_tile[u + 3].split(","))
                    if (!"none".equals(layer))
                        grille[id_x][id_y].addTypes(MapManager.getTypeTile(layer));
                grille[id_x][id_y].setGroupId(id_groupe);
                if (id_groupe != -1) {
                    for (GroupTiles group : groups) {
                        if (group.getId() == id_groupe) {
                            group.getTiles().add(grille[id_x][id_y]);
                            break;
                        }
                    }
                }
                n++;
            }
            purcent = (int) ((zero + (int) ((n / 5) / (((max_x + 1) * (max_y + 1)) / 500))) * 0.7f);

        } while (n < ((max_x + 1) * (max_y + 1)));

        /*NetworkManager.instance.sendToServer("lo;mapc"); //load map content
        NetworkManager.instance.waitForNewMessage("mapc");
        String[] args_mapc = NetworkManager.instance.receiveFromServer("mapc").split(";");

        if(args_map.length>2)
        {
            for(int u = 0; u < args_mapc.length; u+=3)
            {
                grille[Integer.parseInt(args_mapc[u])]
                [Integer.parseInt(args_mapc[u+1])]
                .addTypes(MapManager.getTypeTile(args_mapc[u+2]));
            }
        }*/
        LoadingList.setDeferredLoading(false);

        purcent += 7;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Tile[][] getGrille() {
        return grille;
    }

    public void setGrille(Tile[][] grille) {
        this.grille = grille;
    }

    public ArrayList<GroupTiles> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<GroupTiles> groups) {
        this.groups = groups;
    }
}
