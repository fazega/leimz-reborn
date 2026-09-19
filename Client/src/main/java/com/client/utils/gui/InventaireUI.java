package com.client.utils.gui;

import com.client.display.gui.GUI_Manager;


import com.client.gameplay.Inventaire;
import com.client.gameplay.items.SimpleItem;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.TabbedPane;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;

public class InventaireUI extends ResizableFrame
{
    private InventairePanel inventory_panel;
    private Inventaire inventaire;

    public InventaireUI(Inventaire inventaire)
    {
        this.setInventaire(inventaire);
        this.setTitle("Inventaire");
        inventory_panel = new InventairePanel(5, 5, inventaire);
        this.add(inventory_panel);
        this.setResizableAxis(ResizableAxis.NONE);
        this.adjustSize();
    }

    public static ResizableFrame panInfo(SimpleItem item)
    {
        final SimpleItem ob = item;

        final ResizableFrame frame = new ResizableFrame();
        frame.setTheme("/resizableframe");
        frame.setTitle("Informations sur l'objet");


        TabbedPane onglets = new TabbedPane();
        onglets.setTheme("/tabbedpane");

        Widget i = new Widget()
        {
            @Override
            protected void paintWidget(GUI gui)
            {
                LWJGLRenderer renderer = (LWJGLRenderer)gui.getRenderer();
                renderer.pauseRendering();
                try {
                 ob.getIcon().draw(this.getX(), this.getY());
                } finally {
                   renderer.resumeRendering();
                }
            }
        };
        onglets.addTab("Apercu", i);

        HTMLTextAreaModel textAreaModel = new HTMLTextAreaModel();
        TextArea textarea = new TextArea(textAreaModel);
        textarea.setTheme("/textarea");
        String sb ="";
        sb+="<div style=\"word-wrap: break-word; font-family: default; \">"+ob.getNom()+"</div>";
        sb+="<div style=\"word-wrap: break-word; font-family: default; \">"+ob.getDescription()+"</div>";
        textAreaModel.setHtml(sb);
        onglets.addTab("Description", textarea);


        Button button = new Button("OK");
        button.setTheme("/button");
        button.addCallback(new Runnable()
        {
            @Override
            public void run() {
                GUI_Manager.instance.getRoot().removeChild(GUI_Manager.instance.getRoot().getChildIndex(frame));
                GUI_Manager.instance.getRoot().focusLastChild();
            }


        });

        DialogLayout l = new DialogLayout();
        l.setTheme("/dialoglayout");
        l.setHorizontalGroup(l.createParallelGroup(onglets,button));
        l.setVerticalGroup(l.createSequentialGroup().addWidget(onglets).addGap().addWidget(button));

        frame.add(l);

        return frame;

    }

    public void refresh(Inventaire inventaire)
    {
        inventory_panel.refresh(inventaire);
    }

    @Override
    protected void layout() {
        super.layout();
    }

    public InventairePanel getInventory_panel() {
        return inventory_panel;
    }

    public void setInventory_panel(InventairePanel inventoryPanel) {
        inventory_panel = inventoryPanel;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }
}
