package com.client.display;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;

/** Keeps the legacy Slick viewport and mouse coordinates in sync with native resizing. */
public final class ResizableGameContainer extends AppGameContainer {
    public ResizableGameContainer(Game game) throws SlickException {
        super(game);
        Display.setResizable(true);
    }

    @Override
    protected void gameLoop() throws SlickException {
        if (Display.isCreated()
                && Display.getWidth() > 0
                && Display.getHeight() > 0
                && (width != Display.getWidth() || height != Display.getHeight())) {
            width = Display.getWidth();
            height = Display.getHeight();
            initGL();
            enterOrtho();
        }
        super.gameLoop();
    }
}
