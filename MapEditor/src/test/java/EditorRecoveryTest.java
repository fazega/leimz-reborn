import com.loop.Base;
import com.loop.Principal;
import com.map.Type_tile;
import com.map.manager.MapManager;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

/** Runs the real editor, checks every recovered tile and round-trips to build only. */
public class EditorRecoveryTest extends Base {
    private static boolean completed;

    public void initStatesList(GameContainer container) throws SlickException {
        addState(
                new Principal() {
                    private int frames;

                    public void init(GameContainer container, StateBasedGame game)
                            throws SlickException {
                        super.init(container, game);
                        try {
                            List<Element> original = readTiles("data/Maps/map2.xml");
                            for (Element tile : original) {
                                for (Object entry : tile.getChild("types").getChildren()) {
                                    String name = ((Element) entry).getText();
                                    check(
                                            Type_tile.types.containsKey(name),
                                            "Missing tile definition: " + name);
                                }
                            }
                            openCarte("data/Maps/map2.xml");
                            Field field = Principal.class.getDeclaredField("mapManager");
                            field.setAccessible(true);
                            MapManager manager = (MapManager) field.get(this);
                            manager.saveMapToXML("build/map-roundtrip.xml");
                            List<Element> saved = readTiles("build/map-roundtrip.xml");
                            check(original.size() == saved.size(), "Tile count changed");
                            for (int i = 0; i < original.size(); i++) {
                                Element before = original.get(i), after = saved.get(i);
                                check(
                                        before.getChildText("id_x")
                                                .equals(after.getChildText("id_x")),
                                        "X changed at " + i);
                                check(
                                        before.getChildText("id_y")
                                                .equals(after.getChildText("id_y")),
                                        "Y changed at " + i);
                                List<Element> a = before.getChild("types").getChildren();
                                List<Element> b = after.getChild("types").getChildren();
                                check(a.size() == b.size(), "Layer count changed at " + i);
                                for (int j = 0; j < a.size(); j++) {
                                    check(
                                            a.get(j).getText().equals(b.get(j).getText()),
                                            "Tile type changed at " + i);
                                }
                            }
                            openCarte("build/map-roundtrip.xml");
                            System.out.println("EDITOR_ROUNDTRIP_OK tiles=" + saved.size());
                        } catch (Exception e) {
                            throw new SlickException("Editor recovery verification failed", e);
                        }
                    }

                    public void render(
                            GameContainer container, StateBasedGame game, Graphics graphics)
                            throws SlickException {
                        super.render(container, game, graphics);
                        if (++frames == 120) {
                            completed = true;
                            System.out.println(
                                    "EDITOR_RENDER_OK frames="
                                            + frames
                                            + " tileTypes="
                                            + Type_tile.types.size());
                            container.exit();
                        }
                    }
                });
    }

    private static List<Element> readTiles(String path) throws Exception {
        return new SAXBuilder().build(new File(path)).getRootElement().getChildren("tile");
    }

    private static void check(boolean valid, String message) {
        if (!valid) throw new IllegalStateException(message);
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new EditorRecoveryTest());
        app.setDisplayMode(1353, 700, false);
        app.setUpdateOnlyWhenVisible(false);
        app.setAlwaysRender(true);
        app.setTargetFrameRate(60);
        app.start();
        if (!completed)
            throw new IllegalStateException("Editor closed before verification completed");
    }
}
