import com.server.map.Map;
import com.server.map.Tile;
import com.server.map.managers.MapManager;

/** Server authority must reject every perimeter tile, independently of the client. */
public final class MapBoundaryTest {
    public static void main(String[] args) {
        Tile[][] grid = new Tile[20][15];
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) grid[x][y] = new Tile(x, y);
        }
        grid[5][5].setCollidable(true);
        MapManager manager = new MapManager(new Map(grid, null));
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Tile tile = grid[x][y];
                boolean solid =
                        x == 0
                                || y == 0
                                || x == grid.length - 1
                                || y == grid[x].length - 1
                                || (x == 5 && y == 5);
                Tile destination =
                        manager.getTileReal(tile.getPos_real_x() + 40, tile.getPos_real_y() + 20);
                if (destination != tile || destination.isCollidable() != solid) {
                    throw new AssertionError("Server collision mismatch at " + x + "," + y);
                }
            }
        }
        System.out.println(
                "PASS: server perimeter solid, interior walkable, existing obstacles retained.");
    }
}
