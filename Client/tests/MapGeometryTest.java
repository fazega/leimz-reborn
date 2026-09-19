import com.client.map.Map;
import com.client.map.Tile;
import com.client.map.TypeTile;
import com.client.map.managers.MapManager;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

/** Headless regressions for the coordinate and collision rules used during recovery. */
public final class MapGeometryTest {
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        Tile[][] grid = new Tile[200][200];
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                grid[x][y] = new Tile(new Vector2f(x, y), null);
            }
        }
        MapManager manager =
                new MapManager(new Map(grid, new java.util.ArrayList<com.client.map.GroupTiles>()));
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                boolean edge = x == 0 || y == 0 || x == grid.length - 1 || y == grid[x].length - 1;
                require(grid[x][y].isCollidable() == edge, "Perimeter collision at " + x + "," + y);
            }
        }
        require(
                grid[0][0].getPos_real_barycentre().equals(new Vector2f(40, 20)),
                "Even column centre");
        require(
                grid[1][0].getPos_real_barycentre().equals(new Vector2f(80, 40)),
                "Odd column centre");

        // World lookup must not depend on the camera's visible subset.
        manager.setMap_visible(new Map(new Tile[][] {{grid[0][0]}}, null));
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                require(
                        manager.getTileReal(grid[x][y].getPos_real_barycentre()) == grid[x][y],
                        "Centre lookup mismatch at " + x + "," + y);
            }
        }
        require(
                manager.getTileReal(new Vector2f(-100, -100)) == null,
                "Outside map must be rejected");
        require(
                manager.getTileReal(new Vector2f(10000, 10000)) == null,
                "Far edge must be rejected");
        require(manager.getTileReal(null) == null, "Missing position must be rejected");
        // The shared vertex must belong to some tile, never a hole between adjacent diamonds.
        require(manager.getTileReal(new Vector2f(80, 20)) != null, "Shared tile vertex has a gap");

        com.client.display.Camera camera = new com.client.display.Camera();
        Vector2f previousOffset = null;
        for (int pixel = 0; pixel < 320; pixel++) {
            Vector2f player = new Vector2f(3200 + pixel, 3200 + pixel / 3f);
            Tile focus = manager.getTileReal(player);
            camera.focusOn(focus, focus.getPos_real().copy().sub(player));
            Vector2f offset = manager.getAbsolute();
            if (previousOffset != null) {
                require(
                        Math.abs(offset.x - previousOffset.x + 1) < 0.001f,
                        "Camera horizontal jump");
                require(
                        Math.abs(offset.y - previousOffset.y + 1f / 3) < 0.001f,
                        "Camera parity jump");
            }
            for (Tile[] column : manager.getMap_visible().getGrille()) {
                for (Tile visible : column) {
                    require(
                            visible.getPos_screen()
                                            .distance(visible.getPos_real().copy().add(offset))
                                    < 0.001f,
                            "Tile uses a different camera transform");
                }
            }
            previousOffset = offset.copy();
        }
        Tile solid = new Tile(new Vector2f(0, 0), null);
        solid.addTypes(new TypeTile("ground", null, new Rectangle(0, 0, 80, 40), false, 1));
        require(!solid.isCollidable(), "Ground must remain walkable");
        solid.addTypes(new TypeTile("barrel", null, new Rectangle(0, 0, 80, 40), true, 1));
        require(solid.isCollidable(), "Solid overlay must block the tile");
        solid.addTypes(new TypeTile("decoration", null, new Rectangle(0, 0, 80, 40), false, 1));
        require(solid.isCollidable(), "A later layer must not erase collision");
        System.out.println(
                "PASS: 40000 world lookups, camera independence, boundaries and layered collisions.");
    }
}
