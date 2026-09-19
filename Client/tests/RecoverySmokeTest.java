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
import com.client.display.gui.GUI_Manager;
import com.client.utils.gui.ChatFrame;
import com.client.utils.gui.PrincipalGui;
import com.client.display.ResizableGameContainer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import de.matthiasmann.twl.Event;

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
            if (state == IDENTIFICATION && framesInState == 5) {
                Display.setDisplayMode(new DisplayMode(1100, 720));
            }
            if (state == IDENTIFICATION && framesInState == 10)
                verifyWindowSize(container, 1100, 720);
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
                if (framesInState == 35) Display.setDisplayMode(new DisplayMode(900, 640));
                if (framesInState == 40) verifyWindowSize(container, 900, 640);
                if (framesInState == 45) Display.setDisplayMode(new DisplayMode(1280, 800));
                if (framesInState == 50) verifyWindowSize(container, 1280, 800);
                if (framesInState == 180) checkChatFocus();
                checkWorldRecovery(container);
                if (Boolean.getBoolean("test.borders")) checkBorders(container, graphics);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(2);
        }
    }

    private void verifyWindowSize(GameContainer container, int width, int height) {
        if (!Display.isResizable()
                || container.getWidth() != width
                || container.getHeight() != height
                || Base.sizeOfScreen_x != width
                || Base.sizeOfScreen_y != height) {
            throw new AssertionError("Viewport did not track native window size");
        }
        System.out.println("VERIFIED_RESIZE=" + width + "x" + height);
        if (GUI_Manager.instance.getGui().getWidth() != width
                || GUI_Manager.instance.getGui().getHeight() != height) {
            throw new AssertionError("GUI viewport did not resize");
        }
        if (getCurrentStateID() == PRINCIPAL) {
            ChatFrame chat = PrincipalGui.instance.getChat_frame();
            if (chat.getY() + chat.getHeight() > height || chat.getX() + chat.getWidth() > width) {
                throw new AssertionError("Chat clipped after resize");
            }
        }
    }

    private void checkChatFocus() throws Exception {
        ChatFrame chat = PrincipalGui.instance.getChat_frame();
        Field field = ChatFrame.class.getDeclaredField("editField");
        field.setAccessible(true);
        EditField input = (EditField) field.get(chat);
        GUI_Manager ui = GUI_Manager.instance;
        input.setText("draft");
        input.requestKeyboardFocus();
        if (!ui.getGui().handleKey(Event.KEY_I, 'i', true)) {
            throw new AssertionError("Chat must consume typing while focused");
        }
        ui.getGui().handleKey(Event.KEY_I, 'i', false);
        String draft = input.getText();
        int x = input.getX() + input.getWidth() / 2;
        int y = input.getY() + input.getHeight() / 2;
        ui.getTwlInputAdapter().mousePressed(0, x, y);
        ui.getTwlInputAdapter().mouseReleased(0, x, y);
        if (!input.hasKeyboardFocus()) throw new AssertionError("Click inside chat lost focus");
        ui.getTwlInputAdapter().mousePressed(0, chat.getX() + 20, chat.getY() + 50);
        ui.getTwlInputAdapter().mouseReleased(0, chat.getX() + 20, chat.getY() + 50);
        if (input.hasKeyboardFocus())
            throw new AssertionError("Chat history click retained input focus");
        input.requestKeyboardFocus();
        ui.getTwlInputAdapter().mousePressed(0, 700, 300);
        ui.getTwlInputAdapter().mouseReleased(0, 700, 300);
        if (input.hasKeyboardFocus()) throw new AssertionError("Outside click retained chat focus");
        if (ui.getGui().handleKey(Event.KEY_I, 'i', true)) {
            throw new AssertionError("GUI still consumes game shortcut after outside click");
        }
        ui.getGui().handleKey(Event.KEY_I, 'i', false);
        if (!draft.equals(input.getText())) throw new AssertionError("Outside click changed draft");
        input.setText("");
        System.out.println("VERIFIED_CHAT_FOCUS_AND_SHORTCUT_RELEASE");
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
        // Let the final server approval settle before relocating to the NPC fixture.
        if (framesInState < BARREL_CHECK_FRAME - 20) {
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
            verifyMapClickStartsWalking();
        }
        if (framesInState == 350) {
            Vector2f destination = grid[9][14].getPos_real_barycentre();
            if (MainJoueur.instance.getPos_real().distance(destination) > 2) {
                throw new AssertionError(
                        "Click-to-move did not reach its destination: "
                                + MainJoueur.instance.getPos_real());
            }
            System.out.println("VERIFIED_CLICK_TO_MOVE_REACHED_DESTINATION");
            if (!Boolean.getBoolean("test.borders")) container.exit();
        }
    }

    private void verifyMapClickStartsWalking() {
        final MapManager maps = MapManager.instance;
        final Vector2f point = maps.getEntire_map().getGrille()[9][14].getPos_screen_barycentre();
        for (Tile[] column : maps.getMap_visible().getGrille()) {
            for (Tile tile : column) tile.setDrawn(false);
        }
        org.newdawn.slick.Input click =
                new org.newdawn.slick.Input(Base.sizeOfScreen_y) {
                    @Override
                    public boolean isMousePressed(int button) {
                        return button == 0;
                    }

                    @Override
                    public int getMouseX() {
                        return (int) point.x;
                    }

                    @Override
                    public int getMouseY() {
                        return (int) point.y;
                    }
                };
        new com.client.events.MainEventListener(
                        new com.client.utils.pathfinder.PathFinder(maps.getEntire_map()), click)
                .pollEvents();
    }

    private void checkBorders(GameContainer container, Graphics graphics) throws Exception {
        int[][] positions = {
            {1, 1}, {100, 1}, {198, 1}, {198, 100}, {198, 198}, {100, 198}, {1, 198}, {1, 100}
        };
        int elapsed = framesInState - 360;
        if (elapsed < 0) return;
        int index = elapsed / 15;
        if (index >= positions.length) {
            MainJoueur.instance.setPos_real(
                    MapManager.instance
                            .getEntire_map()
                            .getGrille()[9][13]
                            .getPos_real_barycentre());
            System.out.println("VERIFIED_ALL_EIGHT_BORDER_VIEWS");
            container.exit();
        } else if (elapsed % 15 == 0) {
            Tile tile =
                    MapManager.instance.getEntire_map()
                            .getGrille()[positions[index][0]][positions[index][1]];
            MainJoueur.instance.setTile(tile);
            MainJoueur.instance.setPos_real(tile.getPos_real_barycentre());
        } else if (elapsed % 15 == 10) {
            Image screenshot = new Image(container.getWidth(), container.getHeight());
            graphics.copyArea(screenshot, 0, 0);
            ImageOut.write(
                    screenshot,
                    "png",
                    System.getProperty("test.output") + "/border-" + index + ".png",
                    false);
            screenshot.destroy();
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
        AppGameContainer app = new ResizableGameContainer(new RecoverySmokeTest());
        app.setDisplayMode(1000, 680, false);
        app.setTargetFrameRate(30);
        app.setAlwaysRender(true);
        app.setUpdateOnlyWhenVisible(false);
        app.start();
    }
}
