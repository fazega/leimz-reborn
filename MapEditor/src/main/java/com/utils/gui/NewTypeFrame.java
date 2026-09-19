package com.utils.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.ValueAdjusterInt;

public class NewTypeFrame extends ResizableFrame {
    public NewTypeFrame() {

        // *********************************FENETRE POUR AJOUTER UN
        // TYPE*****************************************
        this.setTheme("/resizableframe");

        Label nomL = new Label("Nom --> ");
        nomL.setTheme("/label");
        final EditField nomF = new EditField();
        nomF.setTheme("/editfield");
        nomL.setLabelFor(nomF);

        Label fileL = new Label("Image --> ");
        fileL.setTheme("/label");
        final EditField fileF = new EditField();
        fileF.setTheme("/editfield");

        Label baseL = new Label("Base : ");
        baseL.setTheme("/label");
        Label xL = new Label("X --> ");
        xL.setTheme("/label");
        final ValueAdjusterInt xVA = new ValueAdjusterInt();
        xL.setLabelFor(xVA);
        xVA.setTheme("/valueadjuster");
        Label yL = new Label("Y --> ");
        yL.setTheme("/label");
        final ValueAdjusterInt yVA = new ValueAdjusterInt();
        yL.setLabelFor(yVA);
        yVA.setTheme("/valueadjuster");

        Label collidableL = new Label("Collidable");
        collidableL.setTheme("/label");
        ToggleButton collidableBox = new ToggleButton();
        collidableBox.setTheme("/checkbox");

        Label calqueL = new Label("Calque --> ");
        calqueL.setTheme("/label");
        final ValueAdjusterInt calqueVA = new ValueAdjusterInt();
        calqueL.setLabelFor(calqueVA);
        calqueVA.setTheme("/valueadjuster");

        Button ajouter = new Button("Ajouter");
        ajouter.setTheme("/button");

        Button annuler = new Button("Annuler");
        annuler.setTheme("/button");
        annuler.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        setVisible(false);
                    }
                });

        ajouter.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        Document document = null;
                        Element racine;

                        SAXBuilder sxb = new SAXBuilder();
                        try {
                            document = sxb.build(new File("data/Maps/types_tiles.xml"));
                        } catch (Exception e) {
                        }
                        racine = document.getRootElement();

                        Element nouveauType = new Element("type");

                        Element nom = new Element("nom");
                        nom.setText(nomF.getText());
                        nouveauType.addContent(nom);

                        Element img = new Element("img");
                        img.setText(fileF.getText());
                        nouveauType.addContent(img);

                        Element collidable = new Element("collidable");
                        collidable.setText("false");
                        nouveauType.addContent(collidable);

                        Element base = new Element("base");
                        base.setText(xVA.getValue() + "," + yVA.getValue());
                        nouveauType.addContent(base);

                        racine.getChild("calque" + calqueVA.getValue()).addContent(nouveauType);

                        // On sauvegarde
                        XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
                        try {
                            sortie.output(
                                    document, new FileOutputStream("data/Maps/types_tiles.xml"));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        setVisible(false);
                    }
                });

        DialogLayout layoutTypeFrame = new DialogLayout();
        layoutTypeFrame.setTheme("/dialoglayout");
        DialogLayout.Group hLabels =
                layoutTypeFrame.createParallelGroup(nomL, fileL, baseL, xL, yL, calqueL);
        DialogLayout.Group hFields =
                layoutTypeFrame.createParallelGroup(nomF, fileF, xVA, yVA, calqueVA);
        DialogLayout.Group hBtn =
                layoutTypeFrame.createSequentialGroup().addWidget(ajouter).addWidget(annuler);

        layoutTypeFrame.setHorizontalGroup(
                layoutTypeFrame
                        .createParallelGroup()
                        .addGroup(
                                layoutTypeFrame.createSequentialGroup(
                                        hLabels,
                                        layoutTypeFrame
                                                .createSequentialGroup()
                                                .addWidget(collidableBox)
                                                .addGap(10)
                                                .addWidget(collidableL),
                                        hFields))
                        .addGroup(hBtn));
        layoutTypeFrame.setVerticalGroup(
                layoutTypeFrame
                        .createSequentialGroup()
                        .addGroup(layoutTypeFrame.createParallelGroup(nomL, nomF))
                        .addGap(40)
                        .addGroup(layoutTypeFrame.createParallelGroup(fileL, fileF))
                        .addGap(40)
                        .addWidget(baseL)
                        .addGroup(layoutTypeFrame.createParallelGroup(xL, xVA))
                        .addGroup(layoutTypeFrame.createParallelGroup(yL, yVA))
                        .addGap(40)
                        .addGroup(layoutTypeFrame.createParallelGroup(collidableL, collidableBox))
                        .addGap(40)
                        .addGroup(layoutTypeFrame.createParallelGroup(calqueL, calqueVA))
                        .addGap(75)
                        .addGroup(layoutTypeFrame.createParallelGroup(ajouter, annuler)));

        this.add(layoutTypeFrame);
        this.setVisible(false);
    }
}
