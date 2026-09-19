package com.client.entities;

import java.io.File;


import java.util.ArrayList;
import java.util.Random;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.client.gameplay.Caracteristique;
import com.client.gameplay.Sort;

public class Monstre
{
    private String nom;
    private ArrayList<Sort> sorts;
    private ArrayList<Caracteristique> caracs;

    private Vector2f pos_real, size, trans_for_move, posOnMap;


    private Polygon zone;
    private Rectangle pieds_screen;

    private boolean onCollision = false;

    private float speed = 1.0f;
    private Orientation orientation;
    private Image current_img;
    private Image[] states_img;
    private float scaleSize = 1;
    private boolean moved = false;
    private Vector2f posOld;

    private Element racine;
    private Document doc = null;

    public Monstre(String nom, Vector2f pos_reap, Vector2f posOnMap)
    {
        this.nom = nom;

        this.posOnMap = posOnMap;
        trans_for_move = new Vector2f();

        caracs = new ArrayList<Caracteristique>();
        sorts = new ArrayList<Sort>();

        SAXBuilder sxb = new SAXBuilder();
        try
        {
           doc = sxb.build(new File("data/Monstres/"+nom+".xml"));
        }
        catch(Exception e){}
        racine = doc.getRootElement();

        states_img = new Image[racine.getChild("imgs_fixe").getChildren().size()];

        try {
            for(int i = 0; i < racine.getChild("imgs_fixe").getChildren().size(); i++)
            {
                states_img[i] = new Image(((Element) racine.getChild("imgs_fixe").getChildren().get(i)).getText());
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }

        zone = new Polygon();
        zone.addPoint(pos_reap.x-160, pos_reap.y);
        zone.addPoint(pos_reap.x, pos_reap.y-80);
        zone.addPoint(pos_reap.x+160, pos_reap.y);
        zone.addPoint(pos_reap.x, pos_reap.y+80);

        this.size = new Vector2f(states_img[0].getWidth(), states_img[0].getHeight());
        this.pos_real = new Vector2f();
        pos_real.x = pos_reap.x-((states_img[0].getWidth()-80)/2);
        pos_real.y = pos_reap.y+35-(states_img[0].getHeight());

        int[] numbers = new int[4];
        String[] str = (racine.getChild("shapes").getChild("pieds").getText().split(","));
        for(int i = 0; i < 4; i++)
        {
            numbers[i] = Integer.parseInt(str[i]);
        }

        this.pieds_screen = new Rectangle(pos_real.x+numbers[0], pos_real.y+numbers[1], numbers[2], numbers[3]);


        this.orientation = Orientation.BAS_GAUCHE;
        current_img = returnImgOrientation(orientation);
    }

    public Vector2f getPosOnMap() {
        return posOnMap;
    }

    public void setPosOnMap(Vector2f posOnMap) {
        this.posOnMap = posOnMap;
    }

    private Image returnImgOrientation(Orientation orientation)
    {
        switch(orientation)
        {
            case HAUT_DROITE:
                return states_img[2];
            case HAUT_GAUCHE:
                return states_img[3];
            case BAS_GAUCHE:
                return states_img[0];
            case BAS_DROITE:
                return states_img[1];
            default:
                return null;
        }
    }

    public Orientation returnRandomOrientation()
    {
        Random r = new Random();
        int nb = r.nextInt(7);

        switch(nb)
        {
            case 0:
                return Orientation.DROITE;
            case 1:
                return Orientation.GAUCHE;
            case 2:
                return Orientation.BAS;
            case 3:
                return Orientation.HAUT;
            case 4:
                return Orientation.HAUT_DROITE;
            case 5:
                return Orientation.HAUT_GAUCHE;
            case 6:
                return Orientation.BAS_GAUCHE;
            case 7:
                return Orientation.BAS_DROITE;
            default:
                return null;
        }
    }

    public void refresh()
    {
        int[] numbers = new int[4];
        String[] str = (racine.getChild("shapes").getChild("pieds").getText().split(","));
        for(int i = 0; i < 4; i++)
        {
            numbers[i] = Integer.parseInt(str[i]);
        }
        this.pieds_screen = new Rectangle(pos_real.x+numbers[0], pos_real.y+numbers[1], numbers[2], numbers[3]);

        if(returnImgOrientation(orientation) != null)
            current_img = returnImgOrientation(orientation);

        if(moved)
        {
            Vector2f off = new Vector2f();
            off.x = Math.abs(posOld.x-pos_real.x);
            off.y = Math.abs(posOld.y-pos_real.y);

            if(off.x <= 80 && off.y <= 40)
            {
                move();
            }
            else
            {
                moved = false;
            }

        }
        else
        {
            posOld = new Vector2f(pos_real);
        }
    }

    public Rectangle getPieds_screen() {
        return pieds_screen;
    }

    public void setPieds_screen(Rectangle piedsScreen) {
        pieds_screen = piedsScreen;
    }

    public void draw()
    {
        this.current_img.draw(pos_real.x, pos_real.y);
    }

    public void manageMove()
    {
        ArrayList<Orientation> o_possibles = new ArrayList<Orientation>();
        float
        d1x = pos_real.x-zone.getX(),
        d2x = (zone.getX()+zone.getWidth())-pos_real.x,
        d1y = pos_real.y-zone.getY(),
        d2y = (zone.getY()+zone.getHeight())-pos_real.y;

        if(d2x >= zone.getWidth()/2)
            o_possibles.add(Orientation.DROITE);
        if(d1x >= zone.getWidth()/2)
            o_possibles.add(Orientation.GAUCHE);
        if(d1y >= zone.getHeight()/2)
            o_possibles.add(Orientation.HAUT);
        if(d2y >= zone.getHeight()/2)
            o_possibles.add(Orientation.BAS);

        if(d2x >= Math.cos(Math.toRadians(26.6))*zone.getWidth()/2 && d1y >= Math.sin(Math.toRadians(26.6))*zone.getHeight()/2)
            o_possibles.add(Orientation.HAUT_DROITE);
        if(d1x >= Math.cos(Math.toRadians(26.6))*zone.getWidth()/2 && d1y >= Math.sin(Math.toRadians(26.6))*zone.getHeight()/2)
            o_possibles.add(Orientation.HAUT_GAUCHE);
        if(d1x >= Math.cos(Math.toRadians(26.6))*zone.getWidth()/2 && d2y >= Math.sin(Math.toRadians(26.6))*zone.getHeight()/2)
            o_possibles.add(Orientation.BAS_GAUCHE);
        if(d2x >= Math.cos(Math.toRadians(26.6))*zone.getWidth()/2 && d2y >= Math.sin(Math.toRadians(26.6))*zone.getHeight()/2)
            o_possibles.add(Orientation.BAS_DROITE);

        Random r = new Random();
        this.setOrientation(o_possibles.get(r.nextInt(o_possibles.size())));

    }

    public void move()
    {
        switch(orientation)
        {
            case HAUT:
                pos_real.y -= 1 * speed;
                break;
            case BAS:
                pos_real.y += 1 * speed;
                break;
            case GAUCHE:
                pos_real.x -= 1 * speed;
                break;
            case DROITE:
                pos_real.x += 1 * speed;
                break;
            case HAUT_DROITE:
                pos_real.y -= 0.5f * speed;
                pos_real.x += 1f * speed;
                break;
            case HAUT_GAUCHE:
                pos_real.y -= 0.5f * speed;
                pos_real.x -= 1f * speed;
                break;
            case BAS_DROITE:
                pos_real.y += 0.5f * speed;
                pos_real.x += 1f * speed;
                break;
            case BAS_GAUCHE:
                pos_real.y += 0.5f * speed;
                pos_real.x -= 1f * speed;
                break;
        }
    }

    public Vector2f getPosOld() {
        return posOld;
    }

    public void setPosOld(Vector2f posOld) {
        this.posOld = posOld;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ArrayList<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(ArrayList<Sort> sorts) {
        this.sorts = sorts;
    }

    public ArrayList<Caracteristique> getCaracs() {
        return caracs;
    }

    public void setCaracs(ArrayList<Caracteristique> caracs) {
        this.caracs = caracs;
    }

    public Vector2f getPos_real() {
        return pos_real;
    }

    public void setPos_real(Vector2f posReal)
    {
        pos_real = posReal;
    }

    public Vector2f getTrans_for_move() {
        return trans_for_move;
    }

    public void setTrans_for_move(Vector2f transForMove) {
        trans_for_move = transForMove;
    }
    public float getSpeed() {
        return this.speed;
    }

    public boolean isOnCollision() {
        return onCollision;
    }

    public void setOnCollision(boolean onCollision) {
        this.onCollision = onCollision;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Image getCurrent_img() {
        return current_img;
    }

    public void setCurrent_img(Image currentImg) {
        current_img = currentImg;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }
}
