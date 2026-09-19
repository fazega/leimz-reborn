package com.client.gamestates;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.client.load.Loading;

// La base du jeu, elle gere les differents etats de jeux, et possede le main
public class Base extends StateBasedGame {
    static {
        // Establish Windows scaling before LWJGL creates its native window.
        System.setProperty("sun.java2d.dpiaware", "false");
        java.awt.Toolkit.getDefaultToolkit();
        java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }

    // Les entiers correspondants aux etats de jeu
    public static int IDENTIFICATION = 1,
            CHOIX_PERSO = 2,
            CREATION_PERSO = 5,
            LOADING = 3,
            PRINCIPAL = 4;

    // Taille de la fenetre de jeu (largeur (x) et hauteur (y) )
    public static int sizeOfScreen_x = 1000;
    public static int sizeOfScreen_y = 680;
    public static int Tile_x = 40;
    public static int Tile_y = 40;

    public Base() {
        // Appel du constructeur de la classe mere, avec le titre de la fenetre en parametre
        super("Leimz");
    }

    // Methode d'initialisation des etats de jeu
    public void initStatesList(GameContainer gc) throws SlickException {

        // Ajouts des etats de jeu, chaque etat correspond a une classe fille de BasicGameState
        this.addState(new Identification());
        this.addState(new ChoixPerso());
        this.addState(new CreationPerso());
        this.addState(new Loading());
        this.addState(new Principal());
    }

    public static void main(String[] args) throws SlickException {
        Base b = new Base();

        // Creation de la fenetre/appli, qui contient la base, maitre des etats de jeu
        AppGameContainer app = new AppGameContainer(b);

        // app.setSmoothDeltas(true);
        app.setShowFPS(false);

        // Ajout de la taille de la fenetre
        app.setDisplayMode(sizeOfScreen_x, sizeOfScreen_y, false);

        // On demarre l'appli et on entre dans le premier etat
        app.start();
    }
}
