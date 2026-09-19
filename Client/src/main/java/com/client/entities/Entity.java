package com.client.entities;

import java.util.ArrayList;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.client.map.Tile;

public abstract class Entity {

    // Position de l'entite sur la carte
    // En repere (0;80;40)
    protected Tile tile;
    // En repere (0;1;1)
    protected Vector2f pos_real;

    // Position correspondant au barycentre de la tile sur laquelle l'entite est
    protected Vector2f pos_real_on_screen;

    // Taille de l'entite
    protected Vector2f size;

    // Formes avec la taille + la position
    protected Rectangle pieds, corps;

    // Orientation de l'entite
    protected Orientation orientation;

    // Etat
    public enum Etat {
        NORMAL,
        OVER,
        CLICKED
    };

    protected Etat etat;

    // Booleen indiquant si l'entite est en collision ou non
    protected boolean on_collision = false;

    // *******************************COTE GRAPHIQUE***********************************

    // Les images de l'entite au repos
    protected Image[] imgs_repos;

    // L'image actuelle au repos
    protected Image current_img_repos;

    // Les animations (eventuelles de l'entites)
    protected ArrayList<Animation> animations;

    // *******************************ANNEXES********************************

    // La taille relative de l'entite
    protected float scaleSize = 1;

    // La vitesse de l'entite
    protected float speed = 1.0f;

    public Entity(Orientation orientation, Tile tile) {
        this.orientation = orientation;
        this.tile = tile;

        this.pos_real = new Vector2f(0, 0);
        if (tile != null) {
            this.pos_real.x = tile.getPos_real().x;
            this.pos_real.y = tile.getPos_real().y;
        }

        this.etat = Etat.NORMAL;
    }

    public Image returnImgOrientation(Orientation orientation) {
        switch (orientation) {
            case BAS:
                return imgs_repos[0];
            case HAUT:
                return imgs_repos[4];
            case DROITE:
                return imgs_repos[2];
            case GAUCHE:
                return imgs_repos[6];
            case HAUT_DROITE:
                return imgs_repos[3];
            case HAUT_GAUCHE:
                return imgs_repos[5];
            case BAS_GAUCHE:
                return imgs_repos[7];
            case BAS_DROITE:
                return imgs_repos[1];
            default:
                return null;
        }
    }

    // Methode permettant de dessiner l'entite
    public void draw() {
        /** A voir en fonction de l'entite */
    }

    // Methode permettant de rafraichir des elements de l'entite (formes par exemple)
    public void refresh() {
        /** A voir en fonction de l'entite */
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Vector2f getPos_real_on_screen() {
        return pos_real_on_screen;
    }

    public void setPos_real_on_screen(Vector2f posRealOnScreen) {
        pos_real_on_screen = posRealOnScreen;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public Rectangle getPieds() {
        return pieds;
    }

    public void setPieds(Rectangle pieds) {
        this.pieds = pieds;
    }

    public Rectangle getCorps() {
        return corps;
    }

    public void setCorps(Rectangle corps) {
        this.corps = corps;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Image[] getImgs_repos() {
        return imgs_repos;
    }

    public void setImgs_repos(Image[] imgsRepos) {
        imgs_repos = imgsRepos;
    }

    public Image getCurrent_img_repos() {
        return current_img_repos;
    }

    public void setCurrent_img_repos(Image currentImgRepos) {
        current_img_repos = currentImgRepos;
    }

    public ArrayList<Animation> getAnimations() {
        return animations;
    }

    public void setAnimations(ArrayList<Animation> animations) {
        this.animations = animations;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isOn_collision() {
        return on_collision;
    }

    public void setOn_collision(boolean onCollision) {
        on_collision = onCollision;
    }

    public Vector2f getPos_real() {
        return pos_real;
    }

    public void setPos_real(Vector2f posReal) {
        pos_real = posReal == null ? null : posReal.copy();
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }
}
