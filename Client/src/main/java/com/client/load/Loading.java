package com.client.load;

import java.awt.Font;
import com.client.entities.MainJoueur;
import com.client.entities.managers.EntitiesManager;
import com.client.entities.managers.PNJsManager;
import com.client.entities.managers.PlayersManager;
import com.client.gamestates.Base;
import com.client.map.Map;
import com.client.map.managers.MapManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Loading extends BasicGameState {
    private LoadMap load_map;
    private LoadJoueur load_joueur;
    private Image logo;
    private Image fond, barre;
    private UnicodeFont label;
    private float purcent = 0;

    private boolean loadMapFinished = false, loadJoueurFinished = false;

    @Override
    public int getID() {
        return 3;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        Font f = new Font("Trebuchet MS", 25, Font.BOLD);

        logo = new Image("data/Images/Logo/Leimz_Logo_Final_HD_Transparent.png");

        label = new UnicodeFont(f, 25, true, false);
        label.addAsciiGlyphs();
        label.addGlyphs(400, 600);
        label.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        label.loadGlyphs();

        fond = new Image("data/Images/Loading/fond_v2.png");
        barre = new Image("data/Images/Loading/Barre_remplie.png");

        load_joueur = new LoadJoueur();
        load_map = new LoadMap();

        loadMaps();
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
            throws SlickException {
        // FIXME mettre des coordonnÃ©es qui ne dÃ©pendes que des images (donc refaire image)
        purcent = (purcent >= 100) ? 100 : purcent;
        purcent = Math.max(0, Math.min(100, purcent));
        fond.draw(0, 0, Base.sizeOfScreen_x, Base.sizeOfScreen_y);
        int filled = (int) (purcent * barre.getWidth() / 100);
        float sx = Base.sizeOfScreen_x / (float) fond.getWidth();
        float sy = Base.sizeOfScreen_y / (float) fond.getHeight();
        if (filled > 0)
            barre.getSubImage(0, 0, filled, barre.getHeight())
                    .draw(237 * sx, 321 * sy, filled * sx, barre.getHeight() * sy);
        label.drawString(Base.sizeOfScreen_x / 2 - 24, 325 * sy, (int) purcent + "%", Color.yellow);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {

        if (!load_map.getT().isAlive() && !loadMapFinished) {
            new MapManager(new Map(load_map.getGrille(), load_map.getGroups()));
            loadJoueur();
            loadMapFinished = true;
        }

        if (!load_joueur.getT().isAlive() && loadMapFinished) {
            loadJoueurFinished = true;
        }

        purcent = load_map.getPurcent();

        purcent += load_joueur.getPurcent();

        if (loadJoueurFinished && loadMapFinished) {
            int posx = (int) MainJoueur.instance.getTile().getPos().x;
            int posy = (int) MainJoueur.instance.getTile().getPos().y;
            MainJoueur.instance.setTile(
                    MapManager.instance.getEntire_map().getGrille()[posx][posy]);
            MainJoueur.instance.setPos_real(
                    MapManager.instance
                            .getEntire_map()
                            .getGrille()[posx][posy]
                            .getPos_real_barycentre());
            System.out.println(posx + " " + posy);

            EntitiesManager e_m = new EntitiesManager();
            e_m.setPnjs_manager(new PNJsManager());
            e_m.setPlayers_manager(
                    new PlayersManager(MainJoueur.instance, load_joueur.getPlayers()));

            sbg.enterState(Base.PRINCIPAL);
        }
    }

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {}

    private void loadJoueur() {
        load_joueur.start();
    }

    private void loadMaps() {
        load_map.start();
    }
}
