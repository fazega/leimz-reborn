package com.gui;

import java.io.IOException;
import java.net.URL;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;

import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class GUI_Manager {
    private LWJGLRenderer lwjglRenderer;
    private ThemeManager theme;

    private GUI gui;
    private Widget root;
    private TWLInputAdapter twlInputAdapter;

    private boolean on_gui_event;

    public boolean isOn_gui_event() {
        return on_gui_event;
    }

    public void setOn_gui_event(boolean onGuiEvent) {
        on_gui_event = onGuiEvent;
    }

    public GUI_Manager(URL url, GameContainer gc)
    {

        root = new Widget()
        {
            @Override
            public boolean handleEvent(Event evt) {
                if(super.handleEvent(evt))
                {
                    setOn_gui_event(true);
                    return true;
                }
                else
                {
                    setOn_gui_event(false);
                    return false;
                }

            }

        };
        root.setTheme("");

        // save Slick's GL state while loading the theme
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            lwjglRenderer = new LWJGLRenderer();
            theme = ThemeManager.createThemeManager(url, lwjglRenderer);
            gui = new GUI(root, lwjglRenderer);
            gui.applyTheme(theme);
        } catch (LWJGLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // restore Slick's GL state
            GL11.glPopAttrib();
        }

        twlInputAdapter = new TWLInputAdapter(gui, gc.getInput());
        gc.getInput().addPrimaryListener(twlInputAdapter);

    }

    public ThemeManager getTheme() {
        return theme;
    }

    public void setTheme(ThemeManager theme) {
        this.theme = theme;
    }

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public Widget getRoot() {
        return root;
    }

    public void setRoot(Widget root) {
        this.root = root;
    }

    public TWLInputAdapter getTwlInputAdapter() {
        return twlInputAdapter;
    }

    public void setTwlInputAdapter(TWLInputAdapter twlInputAdapter) {
        this.twlInputAdapter = twlInputAdapter;
    }

}
