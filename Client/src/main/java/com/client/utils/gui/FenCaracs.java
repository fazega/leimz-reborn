package com.client.utils.gui;

import com.client.entities.Joueur;

import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.TabbedPane;
import de.matthiasmann.twl.Widget;

public class FenCaracs extends ResizableFrame
{
    private Joueur joueur;

    public FenCaracs(Joueur joueur)
    {
        this.joueur = joueur;
        this.setTheme("/resizableframe");
        this.setTitle("CaractÃ©ristiques");
        init();
    }

    private void init()
    {
        Widget i = new Widget()
        {
            @Override
            protected void paintWidget(GUI gui)
            {
                joueur.getCurrent_img_repos().draw(this.getX(), this.getY());
            }

             @Override
                public int getPreferredInnerWidth() {
                    return joueur.getCurrent_img_repos().getWidth();
                }

                @Override
                public int getPreferredInnerHeight() {
                    return joueur.getCurrent_img_repos().getHeight();
                }
        };

        Label labNom = new Label(joueur.getPerso().getNom());
        labNom.setTheme("/label");

        Label labTitre = new Label("Vagabond");
        labTitre.setTheme("/label");

        TabbedPane onglets = new TabbedPane();
        onglets.setTheme("/tabbedpane");
        onglets.addTab("Physiques", null);
        onglets.addTab("Intellectuelles", null);
        onglets.addTab("Sociales", null);

        DialogLayout l = new DialogLayout();
        l.setTheme("/dialoglayout");
        l.setHorizontalGroup(l.createParallelGroup().addGroup(l.createSequentialGroup().addWidget(i).addGroup(l.createParallelGroup(labNom, labTitre))).addWidget(onglets));
        l.setVerticalGroup(l.createSequentialGroup().addGroup(l.createParallelGroup().addWidget(i).addGroup(l.createSequentialGroup(labNom, labTitre))).addGap(40).addWidget(onglets));

        this.add(l);



    }
}
