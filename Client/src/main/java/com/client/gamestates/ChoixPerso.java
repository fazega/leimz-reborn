package com.client.gamestates;

import java.awt.Font;

import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import com.client.display.gui.GUI_Manager;
import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.entities.Orientation;
import com.client.gameplay.entities.Personnage;
import com.client.map.Tile;
import com.client.network.NetworkManager;

import de.matthiasmann.twl.Button;

public class ChoixPerso extends BasicGameState {
    private UnicodeFont label;
    private ArrayList<Joueur> persos;
    private Image fond;

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {}

    @SuppressWarnings("unchecked")
    @Override
    public void enter(GameContainer gc, final StateBasedGame sbg) throws SlickException {
        Font f = new Font("Trebuchet MS", 25, Font.BOLD);

        label = new UnicodeFont(f, 25, true, false);
        label.addAsciiGlyphs();
        label.addGlyphs(400, 600);
        label.getEffects().add(new ColorEffect(java.awt.Color.BLACK));
        label.loadGlyphs();

        fond = new Image("data/Images/fond.png");

        GUI_Manager.instance.getRoot().removeAllChildren();

        persos = new ArrayList<>();
        NetworkManager.instance.sendToServer("ci;");
        NetworkManager.instance.waitForNewMessage("ci");
        String[] args_persos = NetworkManager.instance.receiveFromServer("ci").split("new;");

        if (args_persos.length < 1)
            throw new RuntimeException("Incorrect login message from server");
        for (int i = 1; i < args_persos.length; i++) {
            System.out.println(args_persos[i]);
            String[] args_perso = args_persos[i].split(";");
            final String nom_perso = args_perso[0];
            final String nom_race = args_perso[1];
            final String nom_classe = args_perso[2];
            final int posx = Integer.parseInt(args_perso[3]);
            final int posy = Integer.parseInt(args_perso[4]);
            Orientation ori = Joueur.parseStringOrientation(args_perso[5]);

            final Joueur perso =
                    new Joueur(new Personnage(nom_perso, nom_race, nom_classe), null, ori);
            persos.add(perso);

            Button bouton = new Button("Jouer");
            bouton.setTheme("/button");
            bouton.setPosition(Base.sizeOfScreen_x - 400, 100 + (i - 1) * 100);
            bouton.addCallback(
                    new Runnable() {

                        @Override
                        public void run() {
                            new MainJoueur(perso.getPerso(), null, perso.getOrientation());
                            MainJoueur.instance.setTile(new Tile(new Vector2f(posx, posy), null));
                            NetworkManager.instance.sendToServer(
                                    "lo;j;i;"
                                            + nom_perso
                                            + ";"
                                            + nom_race
                                            + ";"
                                            + nom_classe
                                            + ";"
                                            + posx
                                            + ";"
                                            + posy);
                            sbg.enterState(Base.LOADING);
                        }
                    });
            GUI_Manager.instance.getRoot().add(bouton);
            bouton.adjustSize();
        }

        Button bouton_perso = new Button("CrÃ©er un personnage");
        bouton_perso.setTheme("/button");
        bouton_perso.addCallback(
                new Runnable() {
                    @Override
                    public void run() {
                        sbg.enterState(Base.CREATION_PERSO);
                    }
                });
        GUI_Manager.instance.getRoot().add(bouton_perso);
        bouton_perso.adjustSize();
        bouton_perso.setPosition(
                Base.sizeOfScreen_x / 2 - bouton_perso.getWidth() / 2, Base.sizeOfScreen_y - 200);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        fond.draw(0, 0, Base.sizeOfScreen_x, Base.sizeOfScreen_y);
        for (int i = 0; i < persos.size(); i++) {
            label.drawString(
                    200,
                    100 + i * 100,
                    persos.get(i).getPerso().getNom()
                            + "         "
                            + persos.get(i).getPerso().getRace().getNom()
                            + " - "
                            + persos.get(i).getPerso().getClasse().getNom(),
                    Color.white);
        }
        // Affichage des elements GUI
        GUI_Manager.instance.getTwlInputAdapter().render();
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {

        GUI_Manager.instance.getTwlInputAdapter().update();
    }

    @Override
    public int getID() {
        // TODO Auto-generated method stub
        return 2;
    }
}
