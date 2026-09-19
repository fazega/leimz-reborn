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
                Image image = CharacterSprites.get(directions[row], frame);
                float anchorX = 190 + frame * 110;
                float anchorY = 92 + row * 96;
                graphics.setColor(new Color(130, 150, 130));
                graphics.drawLine(anchorX - 40, anchorY, anchorX + 40, anchorY);
                image.draw(anchorX - image.getWidth() / 2, anchorY - image.getHeight());
            }
        }
        if (++renders == 3) {
            Image screenshot = new Image(container.getWidth(), container.getHeight());
            graphics.copyArea(screenshot, 0, 0);
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
