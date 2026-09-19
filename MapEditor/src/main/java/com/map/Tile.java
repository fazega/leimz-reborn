package com.map;
import java.util.ArrayList;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;






public class Tile
{
    private int pos_x, pos_y; //Ces positions sont les coordonnées
    private int pos_x_real, pos_y_real; //Ces positions sont les positions par rapport à l'origine (0;0)
    private int pos_game_x, pos_game_y;


    private ArrayList<Type_tile> types;
    private int state; //On définit son état (peu utilisé)
    public static int NONE = 0, OVER = 1, CLICKED = 2;
    private boolean monsterHolder = false;

    private boolean collidable;

    public static Image type0;


    /**
     * Constructeur utilisé pour l'initilisation des Tiles dans une grille/map.
     * @param pos_x Position en X de la tyle dans le tableau (ex: 0)
     * @param pos_y Position en Y de la tyle dans le tableau (ex: 3)
     * @param type Type de la tyle
     */
    public Tile(int pos_x, int pos_y, Type_tile type)
    {
        this.types = new ArrayList<Type_tile>();

        this.pos_x = pos_x;
        this.pos_y = pos_y;

        if(type != null)
            this.types.add(type);
        this.pos_game_x = pos_x * 40;
        this.pos_game_y = pos_y * 40;

        try {
            type0 = new Image("data/tiles/tileSimple.png");
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Tile(int pos_x, int pos_y, Type_tile type, boolean monsterHolder)
    {

        this.types = new ArrayList<Type_tile>();

        this.pos_x = pos_x;
        this.pos_y = pos_y;
        if(type != null)
            this.types.add(type);
        this.monsterHolder = monsterHolder;
        this.pos_game_x = pos_x * 40;
        this.pos_game_y = pos_y * 40;

        try {
            type0 = new Image("data/tiles/tileSimple.png");
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Constructeur utilisé pour l'initilisation des Tyles dans la liste des Type de tile dispo pour
     * forger une map. Ici, pas besoin de préciser la position car le Tyle n'est pas dans la map.
     * @param type Type de la tyle
     */
    public Tile(Type_tile type)
    {
        this.types = new ArrayList<Type_tile>();

        this.pos_x = -1;
        this.pos_y = -1;

        if(type != null)
            this.types.add(type);

        try {
            type0 = new Image("data/tiles/tileSimple.png");
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void checkCollidable()
    {
        for(int i = 0; i < types.size(); i++)
        {
            if(types.get(i).isCollidable())
            {
                collidable = true;
            }
        }
    }


    public void refreshEvent()
    {
        //RIEN, (pour l'instant)
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return state;
    }

    public int getPos_x() {
        return pos_x;
    }


    public void setPos_x(int posX) {
        pos_x = posX;
    }


    public int getPos_y() {
        return pos_y;
    }


    public void setPos_y(int posY) {
        pos_y = posY;
    }

    public boolean isMonsterHolder() {
        return monsterHolder;
    }

    public void setMonsterHolder(boolean monsterHolder) {
        this.monsterHolder = monsterHolder;
    }

    public int getPos_game_x() {
        return pos_game_x;
    }

    public void setPos_game_x(int posGameX) {
        pos_game_x = posGameX;
    }

    public int getPos_game_y() {
        return pos_game_y;
    }

    public void setPos_game_y(int posGameY) {
        pos_game_y = posGameY;
    }



    public boolean isPointed(int mouseX, int mouseY)
    {

        //Cette méthode renvoie si oui ou non, la tile est pointée par la souris.


        //ATTENTION, cette méthode est assez compliquée mathématiquement
        //Pour la démontrer, dessiner sur un schéma un losange dont les diagonales font 80 et 40 (UA)
        //Trouver alors la condition pour qu'un point (x;y) soit dans le losange.


        int m_x, m_y; //On définit de nouvelles coordonnées pour la souris
        m_x = (mouseX - pos_x_real) - (80/2); //La coordonnée x par rapport au centre de l'image
        m_y = (mouseY - pos_y_real) - (40/2); //La coordonnée y par rapport au centre de l'image

        //Si la souris est dans la tile en x, et que |x/2|+|y| < height/2 soit 20
        if(m_x > -40 && m_x < 40 && ((Math.abs(m_x/2)+Math.abs(m_y))<=20))
        {
            return true;
        }

        //Sinon
        else
        {
            return false;
        }
    }

    public int getPos_x_real() {
        return pos_x_real;
    }


    public void setPos_x_real(int posXReal) {
        pos_x_real = posXReal;
    }


    public int getPos_y_real() {
        return pos_y_real;
    }


    public void setPos_y_real(int posYReal) {
        pos_y_real = posYReal;
    }

    public ArrayList<Type_tile> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Type_tile> types) {
        this.types = types;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }



}
