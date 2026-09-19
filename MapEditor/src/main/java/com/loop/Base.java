package com.loop;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Base extends StateBasedGame
{

    public static int PRINCIPAL = 1;
    public static int sizeOfScreen_x = 1353;
    public static int sizeOfScreen_y = 700;

    public Base() {
        super("Leïmz Map Editor");
    }

    public void initStatesList(GameContainer gc) throws SlickException
    {
        this.addState(new Principal());

    }

    public static void main(String[] args) throws SlickException
    {

        Base b = new Base();
        AppGameContainer app = new AppGameContainer(b);

        app.setShowFPS(false);
        app.setDisplayMode(1353, 700, false);
        app.setUpdateOnlyWhenVisible(false);

        app.start();


    }
}
