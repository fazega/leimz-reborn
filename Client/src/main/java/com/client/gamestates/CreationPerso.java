package com.client.gamestates;

import java.awt.Font;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import de.matthiasmann.twl.*;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import com.client.display.gui.GUI_Manager;
import com.client.network.NetworkManager;

public class CreationPerso extends BasicGameState {
    private UnicodeFont labeltitre;
    private UnicodeFont label;
    private Image fond;

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {}

    @SuppressWarnings("unchecked")
    @Override
    public void enter(GameContainer gc, final StateBasedGame sbg) throws SlickException {
        Font f = new Font("Trebuchet MS", 25, Font.BOLD);

        label = new UnicodeFont(f, 15, true, false);
        label.addAsciiGlyphs();
        label.addGlyphs(400, 600);
        label.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        label.loadGlyphs();

        labeltitre = new UnicodeFont(f, 25, true, false);
        labeltitre.addAsciiGlyphs();
        labeltitre.addGlyphs(400, 600);
        labeltitre.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        labeltitre.loadGlyphs();

        fond = new Image("data/Images/leimz2.png");

        GUI_Manager.instance.getRoot().removeAllChildren();

        ResizableFrame frame = new ResizableFrame();
        frame.setTitle("Informations");

        final EditField nomfield = new EditField();
        nomfield.setTheme("/editfield");
        Label nomlabel = new Label("Nom du personnage : ");
        nomlabel.setTheme("/label");
        nomlabel.setLabelFor(nomfield);

        final ComboBox<String> racebox = new ComboBox<String>();
        racebox.setTheme("/combobox");
        final SimpleChangableListModel<String> modelracebox =
                new SimpleChangableListModel<String>();
        modelracebox.addElement("Mohm");
        modelracebox.addElement("Elfe");
        racebox.setModel(modelracebox);
        Label racelabel = new Label("Race : ");
        racelabel.setTheme("/label");
        racelabel.setLabelFor(racebox);

        final ComboBox<String> sexebox = new ComboBox<String>();
        sexebox.setTheme("/combobox");
        final SimpleChangableListModel<String> modelsexebox =
                new SimpleChangableListModel<String>();
        modelsexebox.addElement("Homme");
        modelsexebox.addElement("Femme");
        sexebox.setModel(modelsexebox);
        Label sexelabel = new Label("Sexe : ");
        sexelabel.setTheme("/label");
        sexelabel.setLabelFor(sexebox);

        DialogLayout layout = new DialogLayout();
        layout.setTheme("/dialoglayout");
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(nomlabel, racelabel, sexelabel))
                        .addGroup(layout.createParallelGroup(nomfield, racebox, sexebox)));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(nomlabel, nomfield))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(racelabel, racebox))
                        .addGap(20)
                        .addGroup(layout.createParallelGroup(sexelabel, sexebox))
                        .addGap(20));

        frame.add(layout);
        frame.setPosition(350, 200);
        GUI_Manager.instance.getRoot().add(frame);

        Button creer = new Button("CrÃ©er le Perso");
        creer.setTheme("/button");
        creer.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        NetworkManager.instance.sendToServer(
                                "cp;"
                                        + nomfield.getText()
                                        + ";"
                                        + modelracebox.getEntry(racebox.getSelected())
                                        + ";"
                                        + modelsexebox.getEntry(sexebox.getSelected()));

                        sbg.enterState(Base.CHOIX_PERSO);
                    }
                });
        GUI_Manager.instance.getRoot().add(creer);
        creer.adjustSize();
        creer.setPosition(
                Base.sizeOfScreen_x / 2 - creer.getWidth() / 2, Base.sizeOfScreen_y - 100);

        frame.requestKeyboardFocus();
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
            throws SlickException {

        fond.draw(0, 0, Base.sizeOfScreen_x, Base.sizeOfScreen_y);

        String titre = "Creation de personnage";
        labeltitre.drawString(Base.sizeOfScreen_x / 2 - labeltitre.getWidth(titre) / 2, 100, titre);

        GUI_Manager.instance.getTwlInputAdapter().render();
    }

    @Override
    public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
        GUI_Manager.instance.getTwlInputAdapter().update();
    }

    @Override
    public int getID() {
        // TODO Auto-generated method stub
        return 5;
    }
}
