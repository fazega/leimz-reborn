package com.client.utils.pathfinder;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;
import com.client.map.Map;
import com.client.map.Tile;

public class PathFinder {
    public static float epsilon = 2;
    private Map entire_map;

    private ArrayList<Noeud> openList, closedList;

    public PathFinder(Map entire_map) {
        this.entire_map = entire_map;
    }

    private Noeud meilleurNoeud(ArrayList<Noeud> noeuds) {
        Noeud meilleur = null;

        for (int i = 0; i < noeuds.size(); i++) {
            if (i == 0) {
                meilleur = noeuds.get(i);
            }

            if (noeuds.get(i).getCout_f() < meilleur.getCout_f()) {
                meilleur = noeuds.get(i);
            }
        }
        return meilleur;
    }

    public Chemin calculateChemin(Tile tile_depart, Tile tile_arrivee) {
        long time = System.currentTimeMillis();

        openList = new ArrayList<Noeud>();
        closedList = new ArrayList<Noeud>();

        Tile[][] grille = entire_map.getGrille();

        Chemin chemin = null;

        Noeud courant = new Noeud(tile_depart);
        closedList.add(courant);

        //		System.out.println("Tile de depart : "+tile_depart.getPos().x+" ; "+
        // tile_depart.getPos().y);
        //		System.out.println("Tile d'arrivee : "+tile_arrivee.getPos().x+" ; "+
        // tile_arrivee.getPos().y);
        //
        //		System.out.println("Entree dans la boucle");
        while (!(courant.getTile().equals(tile_arrivee))) {
            ArrayList<Tile> tiles_base = new ArrayList<Tile>();

            // Si la pos en x du joueur est paire
            if (courant.getTile().getPos().x % 2 == 0) {
                // On recupere les tiles autour de lui (faire un schema pour comprendre)
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) - 1][
                                (int) (courant.getTile().getPos().y) - 1]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) - 1][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) + 1][
                                (int) (courant.getTile().getPos().y) - 1]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) + 1][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) - 2][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) + 2][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x)][
                                (int) (courant.getTile().getPos().y) - 1]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x)][
                                (int) (courant.getTile().getPos().y) + 1]);
            }

            // Sinon
            else {
                // On recupere aussi les tiles autour de lui (ce ne sont pas les memes)
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) - 1][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) - 1][
                                (int) (courant.getTile().getPos().y) + 1]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) + 1][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) + 1][
                                (int) (courant.getTile().getPos().y) + 1]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) - 2][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x) + 2][
                                (int) (courant.getTile().getPos().y)]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x)][
                                (int) (courant.getTile().getPos().y) - 1]);
                tiles_base.add(
                        grille[(int) (courant.getTile().getPos().x)][
                                (int) (courant.getTile().getPos().y) + 1]);
            }

            ArrayList<Tile> tiles_autour = new ArrayList<Tile>();
            for (int i = 0; i < tiles_base.size(); i++) {
                boolean alreadyInClosedList = false;
                for (int k = 0; k < closedList.size(); k++) {
                    if (closedList.get(k).getTile().equals(tiles_base.get(i))) {
                        alreadyInClosedList = true;
                    }
                }
                if (!tiles_base.get(i).isCollidable() && !alreadyInClosedList) {
                    tiles_autour.add(tiles_base.get(i));
                }
            }

            ArrayList<Noeud> noeuds_autour = new ArrayList<Noeud>();

            for (int i = 0; i < tiles_autour.size(); i++) {
                noeuds_autour.add(new Noeud(tiles_autour.get(i)));

                noeuds_autour.get(i).setParent(courant);
                noeuds_autour
                        .get(i)
                        .setCout_g(
                                noeuds_autour.get(i).getParent().getCout_g()
                                        + calculateDistance(
                                                tiles_autour.get(i).getPos_real_barycentre(),
                                                noeuds_autour
                                                        .get(i)
                                                        .getParent()
                                                        .getTile()
                                                        .getPos_real_barycentre()));
                noeuds_autour
                        .get(i)
                        .setCout_h(
                                calculateDistance(
                                        tiles_autour.get(i).getPos_real_barycentre(),
                                        tile_arrivee.getPos_real_barycentre()));
                noeuds_autour
                        .get(i)
                        .setCout_f(
                                noeuds_autour.get(i).getCout_g()
                                        + noeuds_autour.get(i).getCout_h());
                // System.out.println("Tile "+i+" : pos -->
                // "+tiles_autour.get(i).getPos().x+";"+tiles_autour.get(i).getPos().y+"    |||
                // Couts : g-->"+noeuds_autour.get(i).getCout_g()+"
                // h-->"+noeuds_autour.get(i).getCout_h()+"
                // f-->"+noeuds_autour.get(i).getCout_f());

                boolean alreadyChosen = false;
                for (int j = 0; j < openList.size(); j++) {
                    if (openList.get(j).getTile() == tiles_autour.get(i)) {
                        alreadyChosen = true;
                        if (noeuds_autour.get(i).getCout_f() < openList.get(j).getCout_f()) {
                            openList.set(j, noeuds_autour.get(i));
                        }
                    }
                }

                if (!alreadyChosen) {
                    openList.add(noeuds_autour.get(i));
                }
            }

            courant = meilleurNoeud(openList);
            openList.remove(courant);
            closedList.add(courant);
            // System.out.println("Current pos : " + courant.getTile().getPos().x+" ;
            // "+courant.getTile().getPos().y);
        }

        if (courant.getTile().equals(tile_arrivee)) {
            Noeud fin = null;
            for (int i = 0; i < closedList.size(); i++) {
                if (closedList.get(i).getTile().equals(tile_depart)) fin = closedList.get(i);
            }

            chemin = new Chemin(courant, fin);
            Noeud tmp = courant;

            while (tmp != chemin.getObjectif()) {
                tmp = tmp.getParent();
                chemin.getNoeuds().add(tmp);
            }
        } else {
            System.out.println("Aucun chemin possible !");
        }

        long delta = System.currentTimeMillis() - time;
        System.out.println("Temps de processus : " + delta + " ms");

        return chemin;
    }

    public float getMinValue(ArrayList<Float> numbers, int param) {
        float minValue = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i) < minValue) {
                minValue = numbers.get(i);
            }
        }
        if (param == 0) return numbers.indexOf(minValue);
        else return minValue;
    }

    public float calculateDistance(Vector2f point1, Vector2f point2) {
        return (float)
                Math.sqrt(
                        (point1.x - point2.x) * (point1.x - point2.x)
                                + (point1.y - point2.y) * (point1.y - point2.y));
    }
}
