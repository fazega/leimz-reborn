import com.client.display.CharacterMotion;

public final class CharacterMotionTest {
    public static void main(String[] args) throws Exception {
        java.awt.image.BufferedImage atlas = javax.imageio.ImageIO.read(new java.io.File(args[0]));
        require(atlas.getColorModel().hasAlpha(), "Atlas must preserve transparency");
        require(
                atlas.getHeight() == atlas.getWidth() * 2,
                "Atlas must have four columns and eight square-cell rows");
        int cellWidth = atlas.getWidth() / 4;
        int cellHeight = atlas.getHeight() / 8;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 4; column++) {
                int opaque = 0, transparent = 0;
                for (int y = row * atlas.getHeight() / 8;
                        y < (row + 1) * atlas.getHeight() / 8;
                        y++) {
                    for (int x = column * atlas.getWidth() / 4;
                            x < (column + 1) * atlas.getWidth() / 4;
                            x++) {
                        int alpha = atlas.getRGB(x, y) >>> 24;
                        if (alpha > 128) opaque++;
                        if (alpha == 0) transparent++;
                    }
                }
                require(
                        opaque > 100 && transparent > cellWidth * cellHeight / 2,
                        "Every animation cell must contain a character on transparent background");
            }
        }
        CharacterMotion motion = new CharacterMotion();
        motion.update(0, 0, 0);
        require(motion.getFrame() == 0, "Initially idle");
        boolean[] seen = new boolean[4];
        for (int i = 1; i <= 30; i++) {
            motion.update(i, 0, 32);
            seen[motion.getFrame()] = true;
        }
        require(seen[1] && seen[2] && seen[3], "Walk cycle must advance through all poses");
        for (int i = 0; i < 10; i++) motion.update(30, 0, 32);
        require(motion.getFrame() == 0, "Blocked/stopped player must return to idle");
        motion.update(3000, 3000, 32);
        require(motion.getFrame() == 0, "Teleport must not trigger a walk");
        require(Math.abs(motion.getBreathingScale() - 1) <= 0.009f, "Idle breathing stays subtle");
        System.out.println("PASS: walk cycle, idle, stopped movement and teleport animation.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
