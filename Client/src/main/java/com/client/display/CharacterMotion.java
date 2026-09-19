package com.client.display;

/** Visual motion only: follows confirmed positions and never changes collision or movement. */
public final class CharacterMotion {
    private static final int[] WALK_FRAMES = {1, 2, 3, 2};
    private static final int WALK_FRAME_MS = 360;
    private float previousX, previousY;
    private boolean initialized;
    private int movingFor, phase, idleTime;

    public void update(float x, float y, int delta) {
        delta = Math.max(0, Math.min(delta, 100));
        float distance = Math.abs(x - previousX) + Math.abs(y - previousY);
        if (initialized && distance > 0.01f && distance < 64) movingFor = 130;
        else movingFor = Math.max(0, movingFor - delta);
        if (initialized && distance >= 64) movingFor = 0;
        if (movingFor > 0) phase = (phase + delta) % (WALK_FRAMES.length * WALK_FRAME_MS);
        else phase = 0;
        idleTime = (idleTime + delta) % 2400;
        previousX = x;
        previousY = y;
        initialized = true;
    }

    public int getFrame() {
        if (movingFor == 0) return 0;
        return WALK_FRAMES[phase / WALK_FRAME_MS];
    }

    public float getBreathingScale() {
        return movingFor > 0 ? 1 : 1 + 0.008f * (float) Math.sin(idleTime * Math.PI / 1200);
    }
}
