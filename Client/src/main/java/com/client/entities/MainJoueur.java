package com.client.entities;

import java.util.ArrayList;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import com.client.events.EventListener;
import com.client.gameplay.entities.Personnage;
import com.client.map.Tile;
import com.client.map.managers.MapManager;
import com.client.network.NetworkManager;
import com.client.utils.pathfinder.Chemin;
import com.client.utils.pathfinder.Noeud;

public class MainJoueur extends Joueur {
    // Evenements
    private ArrayList<String> events = new ArrayList<String>();
    private EventListener event_listener;

    // PATHFINDING
    private Chemin current_chemin;
    private ArrayList<Tile> list_tiles_done;
    private Tile next_tile;

    private volatile Vector2f poscopie;
    private volatile boolean awaitingMovementApproval;

    public void finishMovementApproval() {
        awaitingMovementApproval = false;
    }

    public Vector2f getPoscopie() {
        return poscopie;
    }

    public void setPoscopie(Vector2f poscopie) {
        this.poscopie = poscopie;
    }

    public static MainJoueur instance;

    public MainJoueur(Personnage perso, Tile tile, Orientation orientation) {
        super(perso, tile, orientation);

        this.pos_real.x += 40;
        this.pos_real.y += 20;

        this.list_tiles_done = new ArrayList<Tile>();

        if (instance == null) {
            instance = this;
        }

        initEvents();
    }

    private void initEvents() {
        event_listener =
                new EventListener() {

                    @Override
                    public void pollEvents() {
                        if (events.contains("GAUCHE")
                                && !(events.contains("DROITE")
                                        && events.contains("HAUT")
                                        && events.contains("BAS"))) {
                            setOrientation(Orientation.GAUCHE);
                        }
                        if (events.contains("DROITE")
                                && !(events.contains("DROITE")
                                        && events.contains("HAUT")
                                        && events.contains("BAS"))) {
                            setOrientation(Orientation.DROITE);
                        }
                        if (events.contains("HAUT")
                                && !(events.contains("DROITE")
                                        && events.contains("HAUT")
                                        && events.contains("BAS"))) {
                            setOrientation(Orientation.HAUT);
                        }
                        if (events.contains("BAS")
                                && !(events.contains("DROITE")
                                        && events.contains("HAUT")
                                        && events.contains("BAS"))) {
                            setOrientation(Orientation.BAS);
                        }
                        if (events.contains("GAUCHE")
                                && events.contains("HAUT")
                                && !(events.contains("DROITE") && events.contains("BAS"))) {
                            setOrientation(Orientation.HAUT_GAUCHE);
                        }
                        if (events.contains("DROITE")
                                && events.contains("HAUT")
                                && !(events.contains("GAUCHE") && events.contains("BAS"))) {
                            setOrientation(Orientation.HAUT_DROITE);
                        }
                        if (events.contains("GAUCHE")
                                && events.contains("BAS")
                                && !(events.contains("DROITE") && events.contains("HAUT"))) {
                            setOrientation(Orientation.BAS_GAUCHE);
                        }
                        if (events.contains("DROITE")
                                && events.contains("BAS")
                                && !(events.contains("GAUCHE") && events.contains("HAUT"))) {
                            setOrientation(Orientation.BAS_DROITE);
                        }

                        if (events.contains("BAS")
                                || events.contains("HAUT")
                                || events.contains("DROITE")
                                || events.contains("GAUCHE")) {
                            moveKey();
                        }
                    }

                    @Override
                    public void mouseWheelMoved(int change) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseReleased(int button, int x, int y) {
                        if (button == Input.MOUSE_LEFT_BUTTON) {
                            if (events.contains("PRESSED")) events.remove("PRESSED");
                        }
                    }

                    @Override
                    public void mousePressed(int button, int x, int y) {
                        if (button == Input.MOUSE_LEFT_BUTTON) {
                            if (!events.contains("PRESSED")) events.add("PRESSED");
                        }
                    }

                    @Override
                    public void keyPressed(int key, char c) {
                        if (key == Input.KEY_D) {
                            if (!events.contains("DROITE")) events.add("DROITE");
                        }
                        if (key == Input.KEY_Z) {
                            if (!events.contains("HAUT")) events.add("HAUT");
                        }
                        if (key == Input.KEY_Q) {
                            if (!events.contains("GAUCHE")) events.add("GAUCHE");
                        }
                        if (key == Input.KEY_S) {
                            if (!events.contains("BAS")) events.add("BAS");
                        }

                        if (key == Input.KEY_LSHIFT) {
                            events.add("SPEED+");
                            setSpeed(2.0f);
                        }
                    }

                    @Override
                    public void keyReleased(int key, char c) {
                        if (key == Input.KEY_D) {
                            if (events.contains("DROITE")) events.remove("DROITE");
                        }
                        if (key == Input.KEY_Z) {
                            if (events.contains("HAUT")) events.remove("HAUT");
                        }
                        if (key == Input.KEY_Q) {
                            if (events.contains("GAUCHE")) events.remove("GAUCHE");
                        }
                        if (key == Input.KEY_S) {
                            if (events.contains("BAS")) events.remove("BAS");
                        }

                        if (key == Input.KEY_LSHIFT) {
                            if (events.contains("SPEED+")) {
                                events.remove("SPEED+");
                                setSpeed(1.0f);
                            }
                        }
                    }

                    @Override
                    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
                        float mouseX = newx;
                        float mouseY = newy;

                        if ((new Rectangle(
                                        pos_real_on_screen.x + corps.getX(),
                                        pos_real_on_screen.y + corps.getY(),
                                        corps.getWidth(),
                                        corps.getHeight()))
                                .contains(mouseX, mouseY)) {
                            etat = Etat.OVER;
                            if (events.contains("PRESSED")) ;

                            {
                                etat = Etat.CLICKED;
                            }
                        } else {
                            etat = Etat.NORMAL;
                        }
                    }
                };
    }

    public void startMoving(Chemin chemin) {
        this.current_chemin = new Chemin(chemin.getObjectif(), chemin.getDepart());
        ArrayList<Noeud> noeuds = new ArrayList<Noeud>();
        for (int i = chemin.getNoeuds().size() - 1; i >= 0; i--) {
            noeuds.add(chemin.getNoeuds().get(i));
        }
        current_chemin.setNoeuds(noeuds);

        next_tile = current_chemin.getDepart().getTile();

        list_tiles_done = new ArrayList<Tile>();
    }

    public void move() {
        if (current_chemin != null && this.tile != null) {
            if (!((this.getPos_real().x
                            >= current_chemin.getObjectif().getTile().getPos_real_barycentre().x
                                    - 1)
                    && (this.getPos_real().x
                            <= current_chemin.getObjectif().getTile().getPos_real_barycentre().x
                                    + 1)
                    && (this.getPos_real().y
                            >= current_chemin.getObjectif().getTile().getPos_real_barycentre().y
                                    - 1)
                    && (this.getPos_real().y
                            <= current_chemin.getObjectif().getTile().getPos_real_barycentre().y
                                    + 1))) {
                if (this.tile.equals(next_tile)
                        && !this.tile.equals(current_chemin.getObjectif().getTile())) {
                    list_tiles_done.add(next_tile);
                    next_tile = current_chemin.getNoeuds().get(list_tiles_done.size()).getTile();
                } else {
                    if (next_tile.getPos_real_barycentre().x > this.getPos_real().x
                            && next_tile.getPos_real_barycentre().y > this.getPos_real().y) {
                        this.orientation = Orientation.BAS_DROITE;
                        this.pos_real.x += 1f * speed;
                        this.pos_real.y += 0.5f * speed;
                    } else if (next_tile.getPos_real_barycentre().x < this.getPos_real().x
                            && next_tile.getPos_real_barycentre().y < this.getPos_real().y) {
                        this.orientation = Orientation.HAUT_GAUCHE;
                        this.pos_real.x -= 1f * speed;
                        this.pos_real.y -= 0.5f * speed;
                    } else if (next_tile.getPos_real_barycentre().x > this.getPos_real().x
                            && next_tile.getPos_real_barycentre().y < this.getPos_real().y) {
                        this.orientation = Orientation.HAUT_DROITE;
                        this.pos_real.x += 1f * speed;
                        this.pos_real.y -= 0.5f * speed;
                    } else if (next_tile.getPos_real_barycentre().x < this.getPos_real().x
                            && next_tile.getPos_real_barycentre().y > this.getPos_real().y) {
                        this.orientation = Orientation.BAS_GAUCHE;
                        this.pos_real.x -= 1f * speed;
                        this.pos_real.y += 0.5f * speed;
                    } else if (next_tile.getPos_real_barycentre().x > this.getPos_real().x) {
                        this.orientation = Orientation.DROITE;
                        this.pos_real.x += 1 * speed;
                    } else if (next_tile.getPos_real_barycentre().x < this.getPos_real().x) {
                        this.orientation = Orientation.GAUCHE;
                        this.pos_real.x -= 1 * speed;
                    } else if (next_tile.getPos_real_barycentre().y > this.getPos_real().y) {
                        this.orientation = Orientation.BAS;
                        this.pos_real.y += 1 * speed;
                    } else if (next_tile.getPos_real_barycentre().y < this.getPos_real().y) {
                        this.pos_real.y -= 1 * speed;
                        this.orientation = Orientation.HAUT;
                    }
                    NetworkManager.instance.sendToServer(
                            "s;pos;" + pos_real.x + ";" + pos_real.y + ";" + stringOrientation());
                }
            }
        }
    }

    public void moveKey() {
        // Replies have no request ID: retain this candidate until its reply arrives.
        if (awaitingMovementApproval) return;
        poscopie = pos_real.copy();
        current_chemin = null;
        if (perso.getCurrent_combat() != null) {
            switch (orientation) {
                case HAUT:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(pos_real.x, pos_real.y - (1 * speed))))) {
                        pos_real.y -= 1 * speed;
                    }
                    break;
                case BAS:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(pos_real.x, pos_real.y + (1 * speed))))) {
                        pos_real.y += 1 * speed;
                    }
                    break;
                case GAUCHE:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(pos_real.x - (1 * speed), pos_real.y)))) {
                        pos_real.x -= 1 * speed;
                    }
                    break;
                case DROITE:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(pos_real.x + (1 * speed), pos_real.y)))) {
                        pos_real.x += 1 * speed;
                    }
                    break;
                case HAUT_DROITE:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(
                                                    pos_real.x + (1 * speed),
                                                    pos_real.y - (0.5f * speed))))) {
                        pos_real.x += 1 * speed;
                        pos_real.y -= 0.5f * speed;
                    }
                    break;
                case HAUT_GAUCHE:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(
                                                    pos_real.x - (1 * speed),
                                                    pos_real.y - (0.5f * speed))))) {
                        pos_real.x -= 1 * speed;
                        pos_real.y -= 0.5f * speed;
                    }
                    break;
                case BAS_DROITE:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(
                                                    pos_real.x + (1 * speed),
                                                    pos_real.y + (0.5f * speed))))) {
                        pos_real.x += 1 * speed;
                        pos_real.y += 0.5f * speed;
                    }
                    break;
                case BAS_GAUCHE:
                    if (perso.getCurrent_combat()
                            .getZone()
                            .contains(
                                    MapManager.instance.getTileReal(
                                            new Vector2f(
                                                    pos_real.x - (1 * speed),
                                                    pos_real.y + (0.5f * speed))))) {
                        pos_real.x -= 1 * speed;
                        pos_real.y += 0.5f * speed;
                    }
                    break;
            }
        } else {
            switch (orientation) {
                case HAUT:
                    poscopie.y -= 1 * speed;
                    break;
                case BAS:
                    poscopie.y += 1 * speed;
                    break;
                case GAUCHE:
                    poscopie.x -= 1 * speed;
                    break;
                case DROITE:
                    poscopie.x += 1 * speed;
                    break;
                case HAUT_DROITE:
                    poscopie.x += 1 * speed;
                    poscopie.y -= 0.5f * speed;
                    break;
                case HAUT_GAUCHE:
                    poscopie.x -= 1 * speed;
                    poscopie.y -= 0.5f * speed;
                    break;
                case BAS_DROITE:
                    poscopie.x += 1 * speed;
                    poscopie.y += 0.5f * speed;
                    break;
                case BAS_GAUCHE:
                    poscopie.x -= 1 * speed;
                    poscopie.y += 0.5f * speed;
                    break;
            }
        }

        Tile candidate = MapManager.instance.getTileReal(poscopie);
        if (candidate == null || candidate.isCollidable()) return;
        awaitingMovementApproval = true;
        NetworkManager.instance.sendToServer(
                "afm;key;" + poscopie.x + ";" + poscopie.y + ";" + size.x + ";" + size.y);
    }

    @Override
    public void refresh() {
        super.refresh();

        // QUETES
        perso.getQuetes_manager().testQuetes(this);
    }

    public EventListener getEvent_listener() {
        return event_listener;
    }

    public void setEvent_listener(EventListener event_listener) {
        this.event_listener = event_listener;
    }
}
