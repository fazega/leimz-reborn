package com.client.events;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;

public abstract class EventListener implements InputListener
{

    public abstract void pollEvents();


    @Override
    public void mouseClicked(int button, int x, int y, int clickCount)
    {

    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {

    }

    @Override
    public abstract void mouseMoved(int oldx, int oldy, int newx, int newy);

    @Override
    public abstract void mousePressed(int button, int x, int y);

    @Override
    public abstract void mouseReleased(int button, int x, int y);

    @Override
    public abstract void mouseWheelMoved(int change);

    @Override
    public void inputEnded() {

    }

    @Override
    public void inputStarted() {

    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void setInput(Input arg0) {

    }

    @Override
    public abstract void keyPressed(int key, char c);

    @Override
    public abstract void keyReleased(int key, char c);

    @Override
    public void controllerButtonPressed(int arg0, int arg1) {

    }

    @Override
    public void controllerButtonReleased(int arg0, int arg1) {

    }

    @Override
    public void controllerDownPressed(int arg0) {

    }

    @Override
    public void controllerDownReleased(int arg0) {

    }

    @Override
    public void controllerLeftPressed(int arg0) {

    }

    @Override
    public void controllerLeftReleased(int arg0) {

    }

    @Override
    public void controllerRightPressed(int arg0) {

    }

    @Override
    public void controllerRightReleased(int arg0) {

    }

    @Override
    public void controllerUpPressed(int arg0) {

    }

    @Override
    public void controllerUpReleased(int arg0) {

    }

}
