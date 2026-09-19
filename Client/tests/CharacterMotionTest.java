import com.client.display.CharacterMotion;
import com.client.display.WalkPose;
import com.client.entities.Orientation;

public final class CharacterMotionTest {
    public static void main(String[] args) throws Exception {
        for (Orientation direction : Orientation.values()) {
            float dx =
                    WalkPose.offsetX(direction, 0, 1, 0)
                            - WalkPose.offsetX(direction, 0, 1, (float) Math.PI);
            float dy =
                    WalkPose.offsetY(direction, 0, 1, 0)
                            - WalkPose.offsetY(direction, 0, 1, (float) Math.PI);
            require(Math.abs(dx) + Math.abs(dy) >= 8, "Foot must visibly swing in " + direction);
            require(
                    Math.abs(
                                    WalkPose.offsetX(direction, 0, 1, 0)
                                            + WalkPose.offsetX(direction, 1, 1, 0))
                            < 0.001f,
                    "Feet must alternate in " + direction);
            require(
                    WalkPose.offsetX(direction, 0, WalkPose.HIP, 1) == 0
                            && WalkPose.offsetY(direction, 0, WalkPose.HIP, 1) == 0,
                    "Hip must stay anchored");
            float before = WalkPose.offsetX(direction, 0, 1, 1);
            float after = WalkPose.offsetX(direction, 0, 1, 1.01f);
            require(
                    Math.abs(after - before) < 0.1f,
                    "Gait must move continuously, without frame jumps");
        }
        require(
                !WalkPose.mirrored(Orientation.HAUT_DROITE)
                        && WalkPose.mirrored(Orientation.HAUT_GAUCHE),
                "Rear diagonal views must not be inverted");
        verifyWalkAtlas(new java.io.File(args[0]).getParentFile());
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
        CharacterMotion slowWalk = new CharacterMotion();
        slowWalk.update(0, 0, 0);
        for (int millisecond = 1; millisecond < CharacterMotion.WALK_FRAME_MS; millisecond++) {
            slowWalk.update(millisecond, 0, 1);
            require(slowWalk.getFrame() == 1, "Walking pose must remain visible for 140 ms");
        }
        slowWalk.update(CharacterMotion.WALK_FRAME_MS, 0, 1);
        require(slowWalk.getFrame() == 2, "Walk advances after the full pose duration");
        motion.update(0, 0, 0);
        require(motion.getFrame() == 0, "Initially idle");
        boolean[] seen = new boolean[9];
        for (int i = 1; i <= 40; i++) {
            motion.update(i, 0, 32);
            seen[motion.getFrame()] = true;
        }
        require(
                seen[1] && seen[2] && seen[3] && seen[4] && seen[5] && seen[6] && seen[7]
                        && seen[8],
                "Walk cycle must advance through all poses");
        for (int i = 0; i < 10; i++) motion.update(40, 0, 32);
        require(motion.getFrame() == 0, "Blocked/stopped player must return to idle");
        motion.update(3000, 3000, 32);
        require(motion.getFrame() == 0, "Teleport must not trigger a walk");
        require(Math.abs(motion.getBreathingScale() - 1) <= 0.009f, "Idle breathing stays subtle");
        CharacterMotion delayed = new CharacterMotion();
        delayed.update(0, 0, 0);
        boolean[] delayedFrames = new boolean[9];
        for (int elapsed = 20; elapsed <= 4000; elapsed += 20) {
            delayed.update(elapsed / 180, 0, 20);
            delayedFrames[delayed.getFrame()] = true;
        }
        for (int frame = 1; frame <= 8; frame++)
            require(delayedFrames[frame], "Delayed movement replies must not pin the first pose");
        for (int step : new int[] {10, 20, 40}) {
            CharacterMotion timed = new CharacterMotion();
            timed.update(0, 0, 0);
            for (int elapsed = step; elapsed <= 1120; elapsed += step) {
                timed.update(elapsed / 20f, 0, step);
            }
            require(timed.getFrame() == 1, "Full cycle timing must be independent of update rate");
        }
        System.out.println("PASS: walk cycle, idle, stopped movement and teleport animation.");
    }

    private static void verifyWalkAtlas(java.io.File directory) throws Exception {
        java.awt.image.BufferedImage image =
                javax.imageio.ImageIO.read(new java.io.File(directory, "adventurer-walk.png"));
        require(image.getColorModel().hasAlpha(), "Walking sprites need transparent background");
        require(
                Math.abs(image.getWidth() * 5 - image.getHeight() * 8) < 8,
                "Walk atlas must contain eight columns and five directional rows");
        for (int row = 0; row < 5; row++) {
            for (int frame = 0; frame < 8; frame++) {
                int left = frame * image.getWidth() / 8;
                int top = row * image.getHeight() / 5;
                int right = (frame + 1) * image.getWidth() / 8;
                int bottom = (row + 1) * image.getHeight() / 5;
                int pixels = 0;
                for (int y = top; y < bottom; y++)
                    for (int x = left; x < right; x++)
                        if ((image.getRGB(x, y) >>> 24) > 128) pixels++;
                require(
                        pixels > 100 && pixels < (right - left) * (bottom - top) * 0.6,
                        "Every walk pose must be present and surrounded by transparency");
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
