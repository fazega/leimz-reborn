import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.imageout.ImageOut;

import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.entities.Orientation;
import com.client.entities.PNJ;
import com.client.entities.managers.EntitiesManager;
import com.client.gamestates.Base;
import com.client.map.Tile;
import com.client.map.managers.MapManager;
import com.client.network.NetworkManager;

import de.matthiasmann.twl.EditField;

/** End-to-end checks against the recovered demo world and its fixed fixtures. */
public class RecoverySmokeTest extends Base {
    private static final int LOGIN_ACTION_FRAME = 30;
    private static final int GAMEPLAY_SCREENSHOT_FRAME = 100;
    private static final int BARREL_CHECK_FRAME = 150;
    private static final int NPC_CHECK_FRAME = 250;
    private static final int MOVEMENT_REQUESTS_PER_FRAME = 20;
    private static final int START_TILE_X = 24;
    private static final int START_TILE_Y = 43;
    private static final int BARREL_TILE_X = 15;
    private static final int BARREL_TILE_Y = 36;
    private static final int APPROACH_TILE_Y = 37;
    private static final int NPC_APPROACH_TILE_X = 9;
    private static final int NPC_APPROACH_TILE_Y = 13;
    private static final float APPROACH_START_WORLD_Y = 1520;
    private static final String EXPECTED_NPC = "Well Caume";

    private int previousState = -1;
    private int framesInState = 0;

    @Override
    protected void postRenderState(GameContainer container, Graphics graphics)
            throws SlickException {
        int state = getCurrentStateID();
        if (state != previousState) {
            previousState = state;
            framesInState = 0;
            System.out.println("STATE=" + getCurrentState().getClass().getName());
        }
        ++framesInState;
        try {
            if (state == IDENTIFICATION && framesInState == LOGIN_ACTION_FRAME) {
                submitLogin();
            }
            if (state == CHOIX_PERSO && framesInState == LOGIN_ACTION_FRAME) {
                selectFirstCharacter();
            }
            if ((state == LOADING && framesInState == 1)
                    || (state == PRINCIPAL && framesInState == GAMEPLAY_SCREENSHOT_FRAME)) {
                saveScreenshot(container, graphics, state);
            }
            if (state == PRINCIPAL) {
                checkWorldRecovery(container);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(2);
        }
    }

    private void submitLogin() throws Exception {
        Object login = getCurrentState();
        setLoginField(login, "ef_login", System.getProperty("test.user"));
        setLoginField(login, "ef_password", System.getProperty("test.password"));
        Method submit = login.getClass().getDeclaredMethod("test");
        submit.setAccessible(true);
        submit.invoke(login);
    }

    private void setLoginField(Object login, String fieldName, String value) throws Exception {
        Field field = login.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        ((EditField) field.get(login)).setText(value);
    }

    private void selectFirstCharacter() throws Exception {
        Field characters = getCurrentState().getClass().getDeclaredField("persos");
        characters.setAccessible(true);
        Joueur player = (Joueur) ((ArrayList<?>) characters.get(getCurrentState())).get(0);
        new MainJoueur(player.getPerso(), null, player.getOrientation());
        MainJoueur.instance.setTile(new Tile(new Vector2f(START_TILE_X, START_TILE_Y), null));
        NetworkManager.instance.sendToServer("lo;j;i;FaZeGa;Groz;barbare;24;43");
        enterState(LOADING);
    }

    private void saveScreenshot(GameContainer container, Graphics graphics, int state)
            throws Exception {
        Image screenshot = new Image(container.getWidth(), container.getHeight());
        graphics.copyArea(screenshot, 0, 0);
        String name = state == LOADING ? "latest-loading" : "latest-gameplay";
        ImageOut.write(
                screenshot, "png", System.getProperty("test.output") + "/" + name + ".png", false);
    }

    private void checkWorldRecovery(GameContainer container) {
        MapManager maps = MapManager.instance;
        Tile[][] grid = maps.getEntire_map().getGrille();
        if (framesInState == 1) {
            beginBarrelApproach(grid);
        }
        if (framesInState < BARREL_CHECK_FRAME) {
            sendMovementBurst();
        }
        if (maps.getTileReal(MainJoueur.instance.getPos_real()).isCollidable()) {
            throw new RuntimeException("Entered solid tile");
        }
        if (framesInState == BARREL_CHECK_FRAME) {
            checkBarrelBlockedAndApproachNpc(grid);
        }
        if (framesInState == NPC_CHECK_FRAME) {
            checkNpcLoaded();
            container.exit();
        }
    }

    private void beginBarrelApproach(Tile[][] grid) {
        if (!grid[BARREL_TILE_X][BARREL_TILE_Y].isCollidable()) {
            throw new RuntimeException("Barrel not solid");
        }
        MainJoueur.instance.setPos_real(
                grid[BARREL_TILE_X][APPROACH_TILE_Y].getPos_real_barycentre());
        System.out.println("BARREL_TEST_START " + MainJoueur.instance.getPos_real());
    }

    private void sendMovementBurst() {
        MainJoueur.instance.setOrientation(Orientation.HAUT);
        // An old approval must never approve a newer blocked position during burst input.
        for (int request = 0; request < MOVEMENT_REQUESTS_PER_FRAME; request++) {
            MainJoueur.instance.moveKey();
        }
    }

    private void checkBarrelBlockedAndApproachNpc(Tile[][] grid) {
        if (MainJoueur.instance.getPos_real().y >= APPROACH_START_WORLD_Y) {
            throw new RuntimeException("No movement toward barrel");
        }
        System.out.println("BARREL_BLOCKED position=" + MainJoueur.instance.getPos_real());
        MainJoueur.instance.setPos_real(
                grid[NPC_APPROACH_TILE_X][NPC_APPROACH_TILE_Y].getPos_real_barycentre());
        NetworkManager.instance.sendToServer(
                "s;pos;"
                        + MainJoueur.instance.getPos_real().x
                        + ";"
                        + MainJoueur.instance.getPos_real().y
                        + ";h");
    }

    private void checkNpcLoaded() {
        boolean found = false;
        for (PNJ npc : EntitiesManager.instance.getPnjs_manager().getPnjs()) {
            if (npc.getNom().equals(EXPECTED_NPC)) {
                found = true;
            }
        }
        if (!found) {
            throw new RuntimeException("NPC not loaded after position sync");
        }
        System.out.println("VERIFIED_NPC_LOADED_AFTER_POSITION_SYNC");
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new RecoverySmokeTest());
        app.setDisplayMode(1000, 680, false);
        app.setTargetFrameRate(30);
        app.setAlwaysRender(true);
        app.setUpdateOnlyWhenVisible(false);
        app.start();
    }
}
