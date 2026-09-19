package com.client.display.gui;

import de.matthiasmann.twl.GUI;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Input;
import org.newdawn.slick.util.InputAdapter;


/**
 * A Slick InputListener which delegates to TWL.
 * <p>
 * It should be added to Slick's Input class as primary listener:<br>
 * {@code input.addPrimaryListener(new TWLInputAdapter(gui, input));}
 * <p>
 * Note: if you get an error with one of the @Override annotations then DO NOT
 * comment them out - upgrade to the latest Slick version. These methods must be
 * called by Slick for correct operation.
 *
 * @author Matthias Mann
 */

/**
 * En gros cette classe permet de vÃ©rifier si les Ã©vÃ©nements de slick sont en fait dÃ©jÃ  gÃ©rer par twl ou non, pour Ã©viter les conflits
 * On teste donc grave Ã  gui.handle si l'Ã©vÃ©nement est dÃ©jÃ  gÃ©rer par TWL, sinon on passe par slick.
 *
 *
 * @author Greg
 *
 */
public class TWLInputAdapter extends InputAdapter {

    private final Input input;
    private final GUI gui;

    private boolean on_gui_event;

    public TWLInputAdapter(GUI gui, Input input) {
        if (gui == null) {
            throw new NullPointerException("gui");
        }
        if (input == null) {
            throw new NullPointerException("input");
        }

        this.gui = gui;
        this.input = input;
    }

    @Override
    public void mouseWheelMoved(int change) {
            if (gui.handleMouseWheel(change)) {
                consume();
            }
            else if(on_gui_event)
            {
                on_gui_event = false;
            }
    }

    @Override
    public void mousePressed(int button, int x, int y)
    {
        //mouseDown |= 1 << button;
        if (gui.handleMouse(x, y, button, true)) {
            consume();
        }
        else if(on_gui_event)
        {
            on_gui_event = false;
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        //mouseDown &= ~(1 << button);

        if (gui.handleMouse(x, y, button, false)) {
            consume();
        }
        on_gui_event = false;
    }

    @Override
    public void mouseMoved(int oldX, int oldY, int newX, int newY) {
        if (gui.handleMouse(newX, newY, -1, false)) {
            consume();
        }

    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newX, int newY) {
        if (gui.handleMouse(newX, newY, -1, false)) {
            consume();
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (gui.handleKey(key, c, true)) {
            consume();
        }

    }

    @Override
    public void keyReleased(int key, char c) {
        if (gui.handleKey(key, c, false)) {
            consume();
            on_gui_event = false;
        }
        else {
            on_gui_event = false;
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        /*if (!ignoreMouse && lastPressConsumed) {
            consume();
        }*/
    }

    private void consume()
    {
        on_gui_event = true;
        input.consumeEvent();
    }

    @Override
    public void inputStarted() {
        gui.updateTime();
    }

    @Override
    public void inputEnded() {
        gui.handleKeyRepeat();
    }

    /**
     * Call this method from {@code BasicGame.update}
     *
     * @see BasicGame#update(org.newdawn.slick.GameContainer, int)
     */
    public void update() {
        gui.setSize();
        gui.handleTooltips();
        gui.updateTimers();
        gui.invokeRunables();
        gui.validateLayout();
        gui.setCursor();
    }

    /**
     * Call this method from {@code BasicGame.render}
     *
     * @see BasicGame#render(org.newdawn.slick.GameContainer,
     *      org.newdawn.slick.Graphics)
     */
    public void render() {
        gui.draw();
    }

    public boolean isOn_gui_event() {
        return on_gui_event;
    }

    public void setOn_gui_event(boolean onGuiEvent) {
        on_gui_event = onGuiEvent;
    }


}
