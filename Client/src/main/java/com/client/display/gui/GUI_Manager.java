package com.client.display.gui;

import java.io.IOException;

import java.net.URL;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

public class GUI_Manager {
    private LWJGLRenderer lwjglRenderer;
    private ThemeManager theme;

    private GUI gui;
    private RootWidget root;
    private TWLInputAdapter twlInputAdapter;

    public static GUI_Manager instance;

    public GUI_Manager(URL url, GameContainer gc) {
        if (instance == null) {
            instance = this;
        }

        root = new RootWidget();
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

    public void resize(int width, int height) {
        lwjglRenderer.setViewport(0, 0, width, height);
        gui.setSize();
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

    public RootWidget getRoot() {
        return root;
    }

    public void setRoot(RootWidget root) {
        this.root = root;
    }

    public TWLInputAdapter getTwlInputAdapter() {
        return twlInputAdapter;
    }

    public void setTwlInputAdapter(TWLInputAdapter twlInputAdapter) {
        this.twlInputAdapter = twlInputAdapter;
    }
}
