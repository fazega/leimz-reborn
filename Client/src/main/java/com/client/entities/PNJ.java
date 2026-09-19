package com.client.entities;

import java.io.File;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.client.gameplay.PNJ_discours;
import com.client.gameplay.Quete;
import com.client.map.Tile;

public class PNJ extends Entity
{
    private String nom;
    private Element racine;
    private Document doc = null;

    private ArrayList<Quete> quetes;
    private PNJ_discours discours;

    public PNJ(String nom, PNJ_discours discours, Orientation orientation, Tile tile)
    {
        super(orientation, tile);

        this.nom = nom;
        this.discours = discours;

        SAXBuilder sxb = new SAXBuilder();
        try
        {
           doc = sxb.build(new File("data/PNJs/"+nom.toLowerCase()+".xml"));
        }
        catch(Exception e){}
        racine = doc.getRootElement();
    }

    public PNJ(String nom, PNJ_discours discours, ArrayList<Quete> quetes, Orientation orientation, Tile tile)
    {
        super(orientation, tile);

        this.nom = nom;
        this.discours = discours;
        this.quetes = quetes;

        SAXBuilder sxb = new SAXBuilder();
        try
        {
           doc = sxb.build(new File("data/PNJs/"+nom.toLowerCase()+".xml"));
        }
        catch(Exception e){}
        racine = doc.getRootElement();
    }

    public PNJ_discours getDiscoursWithId(int id)
    {
        return getDiscoursWithIdP(discours,id);
    }

    private PNJ_discours getDiscoursWithIdP(PNJ_discours current, int id)
    {
        for(int i = 0; i < current.getReponses().size(); i++)
        {
            if(current.getReponses().get(i).getId()==id)
            {
                return current.getReponses().get(i);
            }
            else
            {
                PNJ_discours r = getDiscoursWithIdP(current.getReponses().get(i),id);
                if(r!=null)
                    return r;
            }
        }
        return null;
    }

    public void initImgs()
    {
         imgs_repos = new Image[racine.getChild("imgs_repos").getChildren().size()];

            try {
                for(int i = 0; i < racine.getChild("imgs_repos").getChildren().size(); i++)
                {
                    imgs_repos[i] = new Image(((Element) racine.getChild("imgs_repos").getChildren().get(i)).getText());
                }
            } catch (SlickException e) {
                e.printStackTrace();
            }

            this.size = new Vector2f(imgs_repos[0].getWidth(), imgs_repos[0].getHeight());

            int[] numbers = new int[4];
            String[] str = (racine.getChild("shapes").getChild("pieds").getText().split(","));
            for(int i = 0; i < 4; i++)
            {
                numbers[i] = Integer.parseInt(str[i]);
            }
            this.pieds = new Rectangle(numbers[0], numbers[1], numbers[2], numbers[3]);
            this.corps = new Rectangle(0,0, this.size.x, this.size.y);


            this.pos_real_on_screen = new Vector2f();
            pos_real_on_screen.x = tile.getPos_screen().x-((imgs_repos[0].getWidth()-80)/2);
            pos_real_on_screen.y = tile.getPos_screen().y+35-(imgs_repos[0].getHeight());

            current_img_repos = imgs_repos[0];
    }

    public void draw()
    {
        current_img_repos.draw(pos_real_on_screen.x, pos_real_on_screen.y);
    }

    public void refresh()
    {
        if(imgs_repos != null)
        {
            pos_real_on_screen.x = tile.getPos_screen().x-((imgs_repos[0].getWidth()-80)/2);
            pos_real_on_screen.y = tile.getPos_screen().y+35-(imgs_repos[0].getHeight());
        }

    }

    public void pollEvents(Input input)
    {
        float mouseX = input.getMouseX();
        float mouseY = input.getMouseY();

        if(this.pos_real_on_screen!=null)
        {
            if((new Rectangle(this.pos_real_on_screen.x+this.corps.getX(), this.pos_real_on_screen.y+this.corps.getY(), this.corps.getWidth(), this.corps.getHeight())).contains(mouseX, mouseY))
            {
                etat = Etat.OVER;
                if(input.isMouseButtonDown(0))
                {
                    etat = Etat.CLICKED;
                }
            }
            else
            {
                etat = Etat.NORMAL;
            }
        }

    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ArrayList<Quete> getQuetes() {
        return quetes;
    }

    public void setQuetes(ArrayList<Quete> quetes) {
        this.quetes = quetes;
    }

    public PNJ_discours getPnjDiscours() {
        return discours;
    }

    public void setPnjDiscours(PNJ_discours discours) {
        this.discours = discours;
    }
}
