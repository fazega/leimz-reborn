package com.map;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;



public class Type_tile
{

    //NO COMMENT, c'est évident ;)

    private String nom;
    private Image img;
    private boolean collidable;
    private int calque;

    private Rectangle base;

    private static Element racine;
    private static Document doc_types = null;

    public static HashMap<String, Type_tile> types;
    public static ArrayList<ArrayList<Type_tile>> types_list;

    public static void setOnTypes()
    {
        types = new HashMap<String, Type_tile>();
        types_list = new ArrayList<ArrayList<Type_tile>>();

        SAXBuilder sxb = new SAXBuilder();
        try
        {
           doc_types = sxb.build(new File("data/Maps/types_tiles.xml"));
        }
        catch(Exception e){}
        racine = doc_types.getRootElement();

        ArrayList<Element> calques = new ArrayList<Element>();

        for(int i = 0; i < racine.getChildren().size(); i++)
        {
            calques.add((Element) racine.getChildren().get(i));
        }

        for(int u = 0; u < calques.size(); u++)
        {
            types_list.add(new ArrayList<Type_tile>());
            for(int i = 0; i < calques.get(u).getChildren("type").size(); i++)
            {
                try {
                    String[] str_n = ((Element) calques.get(u).getChildren("type").get(i)).getChild("base").getText().split(",");
                    Rectangle base = new Rectangle(Integer.parseInt(str_n[0]), Integer.parseInt(str_n[1]),80,40);

                    types.put(((Element) calques.get(u).getChildren("type").get(i)).getChild("nom").getText()
                            , new Type_tile(
                                    ((Element) calques.get(u).getChildren("type").get(i)).getChild("nom").getText(),
                                    new Image(((Element) calques.get(u).getChildren("type").get(i)).getChild("img").getText()),
                                    base,
                                    Boolean.parseBoolean(((Element) calques.get(u).getChildren("type").get(i)).getChild("collidable").getText()),
                                    u)
                            );
                    types_list.get(types_list.size()-1).add(new Type_tile(
                                ((Element) calques.get(u).getChildren("type").get(i)).getChild("nom").getText(),
                                new Image(((Element) calques.get(u).getChildren("type").get(i)).getChild("img").getText()),
                                base,
                                Boolean.parseBoolean(((Element) calques.get(u).getChildren("type").get(i)).getChild("collidable").getText()),
                                u));

                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SlickException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }



    public Type_tile(String nom, Image img, Rectangle base, boolean collidable, int calque)
    {
        this.nom = nom;
        this.img = img;
        this.collidable = collidable;
        this.calque = calque;
        this.base = base;
    }

    public int getCalque() {
        return calque;
    }

    public void setCalque(int calque) {
        this.calque = calque;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }



    public Rectangle getBase() {
        return base;
    }



    public void setBase(Rectangle base) {
        this.base = base;
    }



}
