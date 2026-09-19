package com.map.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.newdawn.slick.geom.Vector2f;
import com.map.Grille;
import com.map.Tile;
import com.map.Type_tile;

/** @author fazega */
public class MapManager {
    private Grille grille;
    private Tile
            tileTypeCurrent; // Représente la tile actuellement selectionnée pour la mettre sur la
    // grille
    private Vector2f absolute = new Vector2f(100, 100);

    public MapManager(Grille grille) // Le MapManager recoit une arraylist de maps (calques)
            {
        this.tileTypeCurrent = null;
        this.grille = grille;
    }

    public void drawAndRefreshAll() {
        // Cette méthode permet de dessiner la map en entier
        // Chaque Map représente un calque, et cette méthode permet de les superposer

        int decalage_x = (int) absolute.x, decalage_y = (int) absolute.y;

        // On parcourt alors les lignes et les colonnes
        for (int j = 0; j < grille.get(0).size(); j++) {
            // PAIR
            for (int i = 0; i < grille.size(); i++) {
                // On vérifie qu'on ne sort pas de la grille
                if (i >= 0 && j >= 0 && i < grille.size() && j < grille.get(i).size()) {
                    if (i % 2 == 0) {
                        // On détermine la "vraie" position de la tile par rapport aux coordonnées
                        grille.get(i)
                                .get(j)
                                .setPos_x_real(
                                        (grille.get(i).get(j).getPos_x() / 2) * 80 + decalage_x);
                        grille.get(i)
                                .get(j)
                                .setPos_y_real(grille.get(i).get(j).getPos_y() * 40 + decalage_y);

                        if (grille.get(i).get(j).getTypes() == null
                                || grille.get(i).get(j).getTypes().size() == 0)
                            grille.get(i)
                                    .get(j)
                                    .type0
                                    .draw(
                                            grille.get(i).get(j).getPos_x_real(),
                                            grille.get(i).get(j).getPos_y_real());
                        else {
                            for (Type_tile type : grille.get(i).get(j).getTypes()) {
                                // On crée la position d'affichage
                                Vector2f pos_aff = new Vector2f();
                                // La position vaut la position de la tile moins la position de la
                                // base, créée auparavant par le level designer pour chaque objet
                                pos_aff.x =
                                        grille.get(i).get(j).getPos_x_real()
                                                - type.getBase().getX();
                                pos_aff.y =
                                        grille.get(i).get(j).getPos_y_real()
                                                - type.getBase().getY();

                                type.getImg().draw(pos_aff.x, pos_aff.y);
                            }
                        }
                    }
                }
            }

            // IMPAIR
            for (int i = 0; i < grille.size(); i++) {
                // On vérifie qu'on ne sort pas de la grille
                if (i >= 0 && j >= 0 && i < grille.size() && j < grille.get(i).size()) {
                    if (i % 2 != 0) {
                        // On détermine la "vraie" position de la tile par rapport aux coordonnées
                        grille.get(i)
                                .get(j)
                                .setPos_x_real(
                                        (grille.get(i).get(j).getPos_x() * 80) / 2 + decalage_x);
                        grille.get(i)
                                .get(j)
                                .setPos_y_real(
                                        (grille.get(i).get(j).getPos_y() * 40) + 20 + decalage_y);

                        if (grille.get(i).get(j).getTypes() == null
                                || grille.get(i).get(j).getTypes().size() == 0)
                            grille.get(i)
                                    .get(j)
                                    .type0
                                    .draw(
                                            grille.get(i).get(j).getPos_x_real(),
                                            grille.get(i).get(j).getPos_y_real());
                        else {
                            for (Type_tile type : grille.get(i).get(j).getTypes()) {
                                // On crée la position d'affichage
                                Vector2f pos_aff = new Vector2f();
                                // La position vaut la position de la tile moins la position de la
                                // base, créée auparavant par le level designer pour chaque objet
                                pos_aff.x =
                                        grille.get(i).get(j).getPos_x_real()
                                                - type.getBase().getX();
                                pos_aff.y =
                                        grille.get(i).get(j).getPos_y_real()
                                                - type.getBase().getY();

                                type.getImg().draw(pos_aff.x, pos_aff.y);
                            }
                        }
                    }
                }
            }
        }
    }

    public Vector2f getAbsolute() {
        return absolute;
    }

    public void setAbsolute(Vector2f absolute) {
        this.absolute = absolute;
    }

    public void init() {
        // On parcourt alors les lignes et les colonnes
        for (int i = 0; i < grille.size(); i++) {
            for (int j = 0; j < grille.get(i).size(); j++) {
                if (i % 2 == 0) // Si la ligne est paire
                {
                    // On détermine la "vraie" position de la tile par rapport aux coordonnées
                    grille.get(i).get(j).setPos_x_real((grille.get(i).get(j).getPos_x() / 2) * 80);
                    grille.get(i).get(j).setPos_y_real(grille.get(i).get(j).getPos_y() * 40);
                } else // Si la ligne est impaire
                {
                    // On détermine la "vraie" position de la tile par rapport aux coordonnées
                    grille.get(i).get(j).setPos_x_real((grille.get(i).get(j).getPos_x() * 80) / 2);
                    grille.get(i).get(j).setPos_y_real((grille.get(i).get(j).getPos_y() * 40) + 20);
                }
            }
        }
    }

    public Tile getTypeTileCurrent() {
        return this.tileTypeCurrent;
    }

    public void setTypeTilleCurrent(Tile tile) {
        this.tileTypeCurrent = tile;
    }

    /**
     * Retourne la Tile qui est pointer.
     *
     * @param mouthPosX Position du curseur en X
     * @param mouthPosY Position du cursuer en Y
     * @return Tyle qui a été pointée. Null si aucun Tile n'est pointée
     */
    public Tile getTilePointed(int mouthPosX, int mouthPosY) {
        for (int i = 0; i < this.grille.size(); i++) { // Parcours toute la grille
            for (int j = 0; j < this.grille.get(i).size(); j++) {
                if (this.grille.get(i).get(j).isPointed(mouthPosX, mouthPosY))
                    return this.grille.get(i).get(j);
            }
        }

        return null;
    }

    /** Retourne la tile en fonction du (x;y) */
    public Tile getTile(int posX, int posY) {
        for (int i = 0; i < this.grille.size(); i++) { // Parcours toute la grille visible
            for (int j = 0; j < this.grille.get(i).size(); j++) {
                if (this.grille.get(i).get(j).isPointed(posX, posY))
                    return this.grille.get(i).get(j);
            }
        }

        return null;
    }

    public Grille getGrille() {
        return grille;
    }

    public void setGrille(Grille grille) {
        this.grille = grille;
    }

    /**
     * Sauvegarde la map d'index "index_map" dans l'arrayList des maps et créé en un format XML
     *
     * @param path
     * @param index_map
     */
    public void saveMapToXML(String path) {
        System.out.println("Sauvegarde en cours...");

        Element racine = new Element("map");
        Document document = new Document(racine);

        try {
            // On parcours la grille, plus précisément le claque d'index 0 de la map récupéré
            for (int i = 0; i < grille.size(); i++) {
                for (int j = 0; j < grille.get(i).size(); j++) {
                    Element tile = new Element("tile");
                    racine.addContent(tile);

                    Element id_x = new Element("id_x");
                    id_x.setText(grille.get(i).get(j).getPos_x() + "");
                    tile.addContent(id_x);

                    Element id_y = new Element("id_y");
                    id_y.setText(grille.get(i).get(j).getPos_y() + "");
                    tile.addContent(id_y);

                    Element types = new Element("types");
                    for (int u = 0; u < grille.get(i).get(j).getTypes().size(); u++) {
                        Element type = new Element("type");
                        type.setText(grille.get(i).get(j).getTypes().get(u).getNom());
                        types.addContent(type);
                    }
                    tile.addContent(types);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // On sauvegarde
        XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        try {
            sortie.output(document, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Sauvegarde DONE !");
    }

    public void copyFile(File src, File dest) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(src));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
        byte[] buf = new byte[4096];
        int n;
        while ((n = in.read(buf, 0, buf.length)) > 0) out.write(buf, 0, n);

        in.close();
        out.close();
    }
}
