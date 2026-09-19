package com.client.gamestates;

import org.newdawn.slick.GameContainer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import com.client.display.Camera;
import com.client.display.DisplayManager;
import com.client.display.gui.GUI_Manager;
import com.client.entities.MainJoueur;
import com.client.entities.managers.EntitiesManager;
import com.client.events.MainEventListener;
import com.client.gameplay.managers.CombatManager;
import com.client.map.managers.MapManager;
import com.client.network.NetworkManager;
import com.client.utils.gui.PrincipalGui;
import com.client.utils.pathfinder.PathFinder;

import de.matthiasmann.twl.ResizableFrame;

public class Principal extends BasicGameState {

    // ----------------------------Map-----------------------------------
    private DisplayManager disp;

    // ------------------GUI----------------
    @SuppressWarnings("unused")
    private PrincipalGui maingui;

    // EVENTS
    private MainEventListener event_listener;

    // Entites du jeu
    private EntitiesManager entities_manager;

    // Gestionnaire de recherche de chemin
    private PathFinder pathfinder;

    // Camera
    private Camera camera;

    private float current_scale = 1;

    // ------------COMBAT-------------
    private CombatManager combatManager;

    @Override
    public int getID() {
        return 4;
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {

        entities_manager = EntitiesManager.instance;

        GUI_Manager.instance.getRoot().removeAllChildren();

        MainJoueur.instance.initImgs();

        for (int i = 0; i < entities_manager.getPnjs_manager().getPnjs().size(); i++) {
            entities_manager.getPnjs_manager().getPnjs().get(i).initImgs();
        }

        camera = new Camera();
        disp = new DisplayManager(camera);

        combatManager = new CombatManager();

        NetworkManager.instance.init();
        NetworkManager.instance.startRefreshMessages();

        pathfinder = new PathFinder(MapManager.instance.getEntire_map());

        event_listener = new MainEventListener(pathfinder, gc.getInput());
        gc.getInput().addListener(event_listener);
        gc.getInput().addListener(MainJoueur.instance.getEvent_listener());

        maingui = new PrincipalGui();
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {}

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics gr) throws SlickException {
        disp.drawAll(gr, new Vector2f(gc.getInput().getMouseX(), gc.getInput().getMouseY()));

        GUI_Manager.instance.getTwlInputAdapter().render();
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        gc.setMinimumLogicUpdateInterval(10);
        gc.setMaximumLogicUpdateInterval(10);

        for (int i = 0; i < entities_manager.getPlayers_manager().getJoueurs().size(); i++) {
            if (entities_manager.getPlayers_manager().getJoueurs().get(i).getPos_real() != null) {
                entities_manager
                        .getPlayers_manager()
                        .getJoueurs()
                        .get(i)
                        .setTile(
                                MapManager.instance.getTileReal(
                                        entities_manager
                                                .getPlayers_manager()
                                                .getJoueurs()
                                                .get(i)
                                                .getPos_real()));
            }
        }
        for (int i = 0; i < entities_manager.getPnjs_manager().getPnjs().size(); i++) {
            if (entities_manager.getPnjs_manager().getPnjs().get(i).getImgs_repos() == null)
                entities_manager.getPnjs_manager().getPnjs().get(i).initImgs();
        }

        MainJoueur.instance.setTile(
                MapManager.instance.getTileReal(MainJoueur.instance.getPos_real()));
        MainJoueur.instance.move();

        if (NetworkManager.instance.getS().isClosed()) {
            ResizableFrame frame =
                    PrincipalGui.getPopup(
                            "Le serveur vous a dÃ©connectÃ©. Motif : Serveur dÃ©connectÃ©");
            GUI_Manager.instance.getRoot().add(frame);
        }

        camera.focusOn(
                MainJoueur.instance.getTile(),
                MainJoueur.instance
                        .getTile()
                        .getPos_real()
                        .copy()
                        .sub(MainJoueur.instance.getPos_real()));
        camera.zoom(current_scale);

        MainJoueur.instance.refresh();
        for (int i = 0; i < entities_manager.getPlayers_manager().getJoueurs().size(); i++) {
            entities_manager.getPlayers_manager().getJoueurs().get(i).refresh();
            if (entities_manager.getPlayers_manager().getJoueurs().get(i).getCurrent_textbubble()
                    != null) {
                entities_manager
                        .getPlayers_manager()
                        .getJoueurs()
                        .get(i)
                        .getCurrent_textbubble()
                        .setPosition(
                                (int)
                                                (entities_manager
                                                                .getPlayers_manager()
                                                                .getJoueurs()
                                                                .get(i)
                                                                .getPos_real_on_screen()
                                                                .x
                                                        - entities_manager
                                                                .getPlayers_manager()
                                                                .getJoueurs()
                                                                .get(i)
                                                                .getCurrent_textbubble()
                                                                .getWidth())
                                        + 40,
                                (int)
                                        (entities_manager
                                                        .getPlayers_manager()
                                                        .getJoueurs()
                                                        .get(i)
                                                        .getPos_real_on_screen()
                                                        .y
                                                - entities_manager
                                                        .getPlayers_manager()
                                                        .getJoueurs()
                                                        .get(i)
                                                        .getCurrent_textbubble()
                                                        .getHeight()
                                                + 7));
            }

            // System.out.println("Joueur
            // "+entities_manager.getPlayers_manager().getJoueurs().get(i).getPerso().getNom()+" :
            // "+entities_manager.getPlayers_manager().getJoueurs().get(i).getPos_real_on_screen().x+" -- "+entities_manager.getPlayers_manager().getJoueurs().get(i).getPos_real_on_screen().y);
        }
        for (int i = 0; i < entities_manager.getPnjs_manager().getPnjs().size(); i++) {
            entities_manager.getPnjs_manager().getPnjs().get(i).refresh();
        }

        combatManager.refresh();

        PrincipalGui.instance.refresh();

        GUI_Manager.instance.getTwlInputAdapter().update();
        if (!GUI_Manager.instance.getTwlInputAdapter().isOn_gui_event()) {
            event_listener.pollEvents();
            MainJoueur.instance.getEvent_listener().pollEvents();
            for (int i = 0; i < entities_manager.getPnjs_manager().getPnjs().size(); i++) {
                entities_manager.getPnjs_manager().getPnjs().get(i).pollEvents(gc.getInput());
            }
        }
    }
}
