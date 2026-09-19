package com.client.entities;

import java.io.File;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import org.newdawn.slick.geom.Vector2f;

import com.client.gameplay.entities.Personnage;
import com.client.map.Tile;
import com.client.map.managers.MapManager;
import com.client.utils.gui.TextBubble;

public class Joueur extends Entity {
    protected Personnage perso;

    protected ArrayList<Tile> loaded_zone;
    protected TextBubble current_textbubble;

    private String str_file;
    private Element racine;
    private Document doc = null;

    public Joueur(Personnage perso, Tile tile, Orientation orientation) {
        super(orientation, tile);
        this.perso = perso;
        if (perso.getEntity_file() != null) {
            this.str_file = perso.getEntity_file();

            SAXBuilder sxb = new SAXBuilder();
            try {
                doc = sxb.build(new File(str_file));
            } catch (Exception e) {
            }
            racine = doc.getRootElement();
        }
        loaded_zone = new ArrayList<Tile>();
    }

    public Personnage getPerso() {
        return perso;
    }

    public void setPerso(Personnage perso) {
        this.perso = perso;
    }

    public void initImgs() {
        imgs_repos = new Image[racine.getChild("imgs_repos").getChildren().size()];

        try {
            for (int i = 0; i < racine.getChild("imgs_repos").getChildren().size(); i++) {
                imgs_repos[i] =
                        new Image(
                                ((Element) racine.getChild("imgs_repos").getChildren().get(i))
                                        .getText());
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }

        current_img_repos = returnImgOrientation(orientation);

        this.size = new Vector2f(imgs_repos[0].getWidth(), imgs_repos[0].getHeight());

        pos_real_on_screen = new Vector2f();
        pos_real_on_screen.x =
                (pos_real.x + MapManager.instance.getAbsolute().x)
                        - (current_img_repos.getWidth() / 2);
        pos_real_on_screen.y = (pos_real.y + MapManager.instance.getAbsolute().y) - 65;

        int[] numbers = new int[4];
        String[] str = (racine.getChild("shapes").getChild("pieds").getText().split(","));
        for (int i = 0; i < 4; i++) {
            numbers[i] = Integer.parseInt(str[i]);
        }
        this.corps = new Rectangle(0, 0, size.x, size.y);
        this.pieds = new Rectangle(numbers[0], numbers[1], numbers[2], numbers[3]);
    }

    public void refresh() {
        if (imgs_repos != null) {
            current_img_repos = returnImgOrientation(getOrientation());
            if (current_img_repos != null) {
                pos_real_on_screen.x =
                        (pos_real.x + MapManager.instance.getAbsolute().x)
                                - (current_img_repos.getWidth() / 2);
                pos_real_on_screen.y = (pos_real.y + MapManager.instance.getAbsolute().y) - 65;
            }
        }
    }

    public void draw() {
        current_img_repos.draw(pos_real_on_screen.x, pos_real_on_screen.y);
    }

    public void draw(float scale) {
        current_img_repos.draw(
                (pos_real.x + MapManager.instance.getAbsolute().x)
                        - ((current_img_repos.getWidth() * scale) / 2),
                (pos_real.y + MapManager.instance.getAbsolute().y) - (65 * scale),
                scale);
    }

    public String stringOrientation() {
        String o_m = null;
        switch (orientation) {
            case DROITE:
                o_m = "d";
                break;
            case GAUCHE:
                o_m = "g";
                break;
            case HAUT:
                o_m = "h";
                break;
            case BAS:
                o_m = "b";
                break;
            case HAUT_DROITE:
                o_m = "hd";
                break;
            case HAUT_GAUCHE:
                o_m = "hg";
                break;
            case BAS_DROITE:
                o_m = "bd";
                break;
            case BAS_GAUCHE:
                o_m = "bg";
                break;
        }
        return o_m;
    }

    public static Orientation parseStringOrientation(String o_m) {
        Orientation o = null;

        if (o_m.equals("d")) o = Orientation.DROITE;
        else if (o_m.equals("g")) o = Orientation.GAUCHE;
        else if (o_m.equals("h")) o = Orientation.HAUT;
        else if (o_m.equals("b")) o = Orientation.BAS;
        else if (o_m.equals("hd")) o = Orientation.HAUT_DROITE;
        else if (o_m.equals("hg")) o = Orientation.HAUT_GAUCHE;
        else if (o_m.equals("bd")) o = Orientation.BAS_DROITE;
        else if (o_m.equals("bg")) o = Orientation.BAS_GAUCHE;

        return o;
    }

    public ArrayList<Tile> getLoaded_zone() {
        return loaded_zone;
    }

    public void setLoaded_zone(ArrayList<Tile> loaded_zone) {
        this.loaded_zone = loaded_zone;
    }

    public TextBubble getCurrent_textbubble() {
        return current_textbubble;
    }

    public void setCurrent_textbubble(TextBubble current_textbubble) {
        this.current_textbubble = current_textbubble;
    }
}
