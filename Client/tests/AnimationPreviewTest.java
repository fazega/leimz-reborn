import com.client.display.CharacterMotion;
import com.client.display.CharacterSprites;
import com.client.entities.Orientation;
import org.newdawn.slick.*;
import org.newdawn.slick.imageout.ImageOut;

/** Visual contact sheet using the exact runtime frame slicing, mirroring and scale. */
public final class AnimationPreviewTest extends BasicGame {
    private int renders;
    private final Orientation[] directions = {
        Orientation.BAS,
        Orientation.BAS_DROITE,
        Orientation.DROITE,
        Orientation.HAUT_DROITE,
        Orientation.HAUT,
        Orientation.HAUT_GAUCHE,
        Orientation.GAUCHE,
        Orientation.BAS_GAUCHE
    };

    public AnimationPreviewTest() {
        super("Leimz animation verification");
    }

    public void init(GameContainer container) {}

    public void update(GameContainer container, int delta) {}

    public void render(GameContainer container, Graphics graphics) throws SlickException {
        graphics.setBackground(new Color(77, 99, 78));
        for (int row = 0; row < directions.length; row++) {
            graphics.setColor(Color.white);
            graphics.drawString(directions[row].name(), 4, row * 96 + 30);
            for (int frame = 0; frame <= CharacterMotion.WALK_FRAME_COUNT; frame++) {
                float anchorX = 190 + frame * 110;
                float anchorY = 92 + row * 96;
                graphics.setColor(new Color(130, 150, 130));
                graphics.drawLine(anchorX - 40, anchorY, anchorX + 40, anchorY);
                CharacterSprites.draw(
                        directions[row],
                        (float) ((frame - 1) * Math.PI / 4),
                        frame == 0 ? 0 : 1,
                        anchorX,
                        anchorY,
                        1);
            }
        }
        if (++renders == 3) {
            Image screenshot = new Image(container.getWidth(), container.getHeight());
            graphics.copyArea(screenshot, 0, 0);
            for (int row = 0; row < directions.length; row++) {
                int changedFeet = 0, changedHead = 0;
                int ground = 92 + row * 96;
                for (int y = ground - 74; y < Math.min(ground + 8, screenshot.getHeight()); y++) {
                    for (int x = -35; x <= 35; x++) {
                        Color first = screenshot.getColor(300 + x, y);
                        Color opposite = screenshot.getColor(740 + x, y);
                        if (!first.equals(opposite)) {
                            if (y < ground - 30) changedHead++;
                            else changedFeet++;
                        }
                    }
                }
                if (changedFeet < 20)
                    throw new AssertionError("No rendered leg motion: " + directions[row]);
                if (changedHead > 5)
                    throw new AssertionError(
                            "Torso drift: " + directions[row] + " pixels=" + changedHead);
            }
            System.out.println(
                    "PASS: rendered feet alternate in all eight directions; torso pixels remain anchored.");
            ImageOut.write(
                    screenshot,
                    "png",
                    System.getProperty("test.output") + "/animation-proof.png",
                    false);
            container.exit();
        }
    }

    public static void main(String[] args) throws Exception {
        AppGameContainer container = new AppGameContainer(new AnimationPreviewTest());
        container.setDisplayMode(1150, 780, false);
        container.setShowFPS(false);
        container.setTargetFrameRate(60);
        container.start();
    }
}
