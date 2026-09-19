package com.client.map.managers;


import java.io.File;


import java.util.ArrayList;

import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import com.client.gamestates.Base;
import com.client.map.Map;
import com.client.map.Tile;
import com.client.map.TypeTile;
import com.client.network.NetworkListener;

public class MapManager implements NetworkListener
{


    private Vector2f absolute = new Vector2f(0,0);
    private Map map_visible, entire_map;

    public static MapManager instance;

    private static HashMap<String, TypeTile> types = new HashMap<String, TypeTile>();

    public static void initTypesTile()
    {
        SAXBuilder sxb = new SAXBuilder();
        Document doc = null;
        Element racine;
        try
        {
           doc = sxb.build(new File("data/maps/types_tiles.xml"));
        }
        catch(Exception e){}
        String[] info_tile = new String[6];
        racine = doc.getRootElement();
        for(int i = 0; i < racine.getChild("calque1").getChildren().size(); i++)
        {
            info_tile[5]="1";
            info_tile[0]=((Element) racine.getChild("calque1").getChildren().get(i)).getChild("nom").getText();
            info_tile[1]=((Element) racine.getChild("calque1").getChildren().get(i)).getChild("img").getText();
            info_tile[2]=((Element) racine.getChild("calque1").getChildren().get(i)).getChild("collidable").getText();
            String b =((Element) racine.getChild("calque1").getChildren().get(i)).getChild("base").getText();
            info_tile[3]=b.split(",")[0];
            info_tile[4]=b.split(",")[1];
            TypeTile t = null;
            try {
                t = new TypeTile(info_tile[0], new Image(info_tile[1]),new Rectangle(Integer.parseInt(info_tile[3]),Integer.parseInt(info_tile[4]),80,40),Boolean.parseBoolean(info_tile[2]),Integer.parseInt(info_tile[5]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SlickException e) {
                e.printStackTrace();
            }
            types.put(info_tile[0], t);
        }
    }

    public static TypeTile getTypeTile(String name)
    {
        return types.get(name);
    }

    public MapManager(Map entire_map)
    {
        this.entire_map = entire_map;

        this.init();

        if(instance == null)
        {
             instance = this;
        }
        else
        {
            System.err.println("Erreur : impossible d'instancier un deuxieme MapManager");
        }

    }

    public void drawMap(Map map, Color filter, float scale)
    {
        //On dessine la carte
        for(int j = 0; j < map.getGrille()[0].length; j++)
        {
            /*On veut ici dessiner la carte ligne par ligne en affichant une tile sur 2
             * pair ou impaire en fonction de la premiere tile en haut a gauche
             */
            int first = (int) (map.getGrille()[0][0].getPos().x%2);
            for(int k=0;k<2;k++)
            {
                for(int i = first; i < map.getGrille().length; i+=2)
                {
                    Tile tile = map.getGrille()[i][j];
                    //On cree la position d'affichage
                    Vector2f pos_aff = new Vector2f();
                    //La position vaut la position de la tile moins la position de la base, creee auparavant par le level designer pour chaque objet
                    //De plus, on ajoute une petite formule demontrable simplement en repere orthonorme pour le zoom
                    pos_aff.x = (tile.getPos_screen().x-((TypeTile) tile.getTypes().get(0)).getBase().getX())+((1-scale)*Base.Tile_x);
                    pos_aff.y = (tile.getPos_screen().y-((TypeTile) tile.getTypes().get(0)).getBase().getY())+((1-scale)*Base.Tile_y/2);
                    if(filter != null)
                    {
                        ((TypeTile) tile.getTypes().get(0)).getImg().draw(pos_aff.x, pos_aff.y, scale, filter);
                    }
                    else
                    {
                        ((TypeTile) tile.getTypes().get(0)).getImg().draw(pos_aff.x, pos_aff.y, scale);
                    }
                    map.getGrille()[i][j].setDrawn(true);
                }
                first=(first==1)?0:1;
            }
        }
    }

    public void init()
    {
            //On parcourt alors les lignes et les colonnes
            for(int i = 0; i < entire_map.getGrille().length; i++)
            {
                for(int j = 0; j <entire_map.getGrille()[i].length; j++)
                {
                    if(i%2 ==0) //Si la ligne est paire
                    {
                        //On dï¿½termine la "vraie" position de la tile par rapport aux coordonnï¿½es
                        entire_map.getGrille()[i][j].setPos_screen(
                                new Vector2f((entire_map.getGrille()[i][j].getPos().x/2)*80, entire_map.getGrille()[i][j].getPos().y*40));
                        entire_map.getGrille()[i][j].setPos_real(
                                new Vector2f((entire_map.getGrille()[i][j].getPos().x/2)*80, entire_map.getGrille()[i][j].getPos().y*40));
                    }

                    else //Si la ligne est impaire
                    {
                        //On dï¿½termine la "vraie" position de la tile par rapport aux coordonnï¿½es
                        entire_map.getGrille()[i][j].setPos_screen(
                                new Vector2f((entire_map.getGrille()[i][j].getPos().x*80)/2, (entire_map.getGrille()[i][j].getPos().y*40)+20));
                        entire_map.getGrille()[i][j].setPos_real(
                                new Vector2f((entire_map.getGrille()[i][j].getPos().x*80)/2, (entire_map.getGrille()[i][j].getPos().y*40)+20));
                    }

                }
            }

    }

    /**
     *
     * Retourne la tile en fonction du (x;y)
     *
     *
     */
    public Tile getTileScreen(Vector2f posScreen)
    {
        for(int i=0; i < this.entire_map.getGrille().length; i++)
        { //Parcours toute la grille visible
            for(int j=0; j < this.entire_map.getGrille()[i].length; j++)
            {
                if(this.entire_map.getGrille()[i][j].isPointed(posScreen) && this.entire_map.getGrille()[i][j].isDrawn())
                    return this.entire_map.getGrille()[i][j];
            }
        }

        return null;
    }

    public Tile getTileReal(Vector2f posReal)
    {
        if (posReal == null) return null;
        // Resolve world coordinates independently of the camera's visible subset.
        int column = (int)Math.floor(posReal.x / 40f);
        Tile[][] grid = entire_map.getGrille();
        for (int x = Math.max(0, column - 2); x <= Math.min(grid.length - 1, column + 1); x++) {
            int row = (int)Math.floor((posReal.y - (x % 2) * 20f) / 40f);
            for (int y = Math.max(0, row - 1); y <= Math.min(grid[x].length - 1, row + 1); y++) {
                Tile tile = grid[x][y];
                if (tile.isPointed(posReal, tile.getPos_real())) return tile;
            }
        }
        return null;
    }

    public ArrayList<ArrayList<Tile>> getTilesAutour(Tile tile, int spread)
    {
        ArrayList<ArrayList<Tile>> grille = new ArrayList<ArrayList<Tile>>();

        //Rï¿½cupï¿½ration de l'index de dï¿½part en x et y des Tile ï¿½ afficher.
        int indiceStart_x = (int) (tile.getPos().x - spread);
        int indiceStart_y = (int) (tile.getPos().y - spread);

        //Rï¿½cupï¿½ration de l'index de fin en x et y des Tile ï¿½ afficher.
        int indiceFin_x = indiceStart_x + spread*2;
        int indiceFin_y = indiceStart_y + spread*2;

        int h = 0;
            //On parcourt alors les lignes et les colonnes
            for(int i = indiceStart_x; i < indiceFin_x; i++)
            {
                if(i >= 0 && i < this.getEntire_map().getGrille().length)
                {
                    grille.add(new ArrayList<Tile>());
                }

                for(int j = indiceStart_y; j < indiceFin_y; j++)
                {
                    //Attention aux cas oï¿½ la tile voulue se situe sur une extrï¿½mitï¿½ de la map.
                    if(i >= 0 && j >=0 && i < this.getEntire_map().getGrille().length && j < this.getEntire_map().getGrille()[i].length)
                    {
                        grille.get(h).add(this.getEntire_map().getGrille()[i][j]);
                    }
                }
                if(i >= 0 && i < this.getEntire_map().getGrille().length)
                {
                    h++;
                }
            }
        return grille;
    }


    @Override
    public void receiveMessage(String str) {
        // TODO Auto-generated method stub

    }

    public Map getMap_visible() {
        return map_visible;
    }

    public void setMap_visible(Map mapVisible) {
        map_visible = mapVisible;
    }

    public Map getEntire_map() {
        return entire_map;
    }

    public void setEntire_map(Map entireMap) {
        entire_map = entireMap;
    }

    public Vector2f getAbsolute() {
        return absolute;
    }

    public void setAbsolute(Vector2f absolute) {
        this.absolute = absolute;
    }
}
