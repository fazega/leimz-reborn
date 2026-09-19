package com.client.utils.gui;

import java.util.ArrayList;
import com.client.display.gui.GUI_Manager;
import com.client.entities.MainJoueur;
import com.client.gamestates.Base;
import com.client.network.NetworkListener;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.DialogLayout.Group;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;

public class PrincipalGui implements NetworkListener
{
    private ChatFrame chat_frame;
    private InventaireUI inventaireUI;
    private FenCaracs fencaracs;
    private ResizableFrame menu;
    private PnjDialogFrame pnjdialogframe;

    private ArrayList<Widget> backside_widgets;

    public static PrincipalGui instance = null;
    public static int YES_NO_PANE = 0, CANCEL_PANE = 1;

    public PrincipalGui()
    {
        instance = this;
        init();
    }

    public static ResizableFrame getPopup(String text)
    {
        final ResizableFrame frame;

        frame = new ResizableFrame();
        frame.setTheme("/resizableframe");
        frame.setPosition(350, 300);
        frame.setResizableAxis(ResizableAxis.NONE);

        Label label = new Label(text);
        label.setTheme("/label");
        label.setCanAcceptKeyboardFocus(false);

        Button button = new Button("OK");
        button.setTheme("/button");
        button.addCallback(new Runnable(){

            @Override
            public void run() {
                frame.setVisible(false);
            }
        });

        DialogLayout panel = new DialogLayout();
        panel.setTheme("/dialoglayout");
        Group hbutton = panel.createSequentialGroup().addWidget(button);
        Group hlabel = panel.createSequentialGroup().addWidget(label);
        panel.setHorizontalGroup(panel.createParallelGroup(hbutton, hlabel));
        panel.setVerticalGroup(panel.createSequentialGroup().addWidget(label).addGap(25).addWidget(button));

        frame.add(panel);

        frame.setPosition((Base.sizeOfScreen_x/2)-(frame.getPreferredWidth()/2), (Base.sizeOfScreen_y/2)-(frame.getPreferredHeight()/2)+50);
        panel.adjustSize();
        label.setBackground(null);
        return frame;
    }

    private void init()
    {
        backside_widgets = new ArrayList<Widget>();

        chat_frame = new ChatFrame(null);
        chat_frame.setTheme("/resizableframe");
        chat_frame.setTitle("Leimzochat");
        chat_frame.setPosition(25, 450);
        chat_frame.appendRow("default","Bienvenue sur Leimz !! /n/nTapez votre message sur la barre en dessous de ce message :)/n/n");


        inventaireUI = new InventaireUI(MainJoueur.instance.getPerso().getInventaire());
        inventaireUI.setTheme("/resizableframe");
        GUI_Manager.instance.getRoot().add(inventaireUI);
        inventaireUI.adjustSize();
        inventaireUI.setPosition((Base.sizeOfScreen_x-(inventaireUI.getWidth()))/2, (Base.sizeOfScreen_y-(inventaireUI.getHeight()))/2);
        inventaireUI.setVisible(false);

        fencaracs = new FenCaracs(MainJoueur.instance);
        fencaracs.setVisible(false);
        GUI_Manager.instance.getRoot().add(fencaracs);
        fencaracs.setPosition((Base.sizeOfScreen_x-(fencaracs.getWidth()))/2, (Base.sizeOfScreen_y-(fencaracs.getHeight()))/2);

        menu = new ResizableFrame();
        menu.setTheme("/resizableframe");
        menu.setTitle("Menu");
        Button options = new Button("Options");
        options.setTheme("/button");
        Button deconnexion = new Button("Se Deconnecter");
        deconnexion.setTheme("/button");
        Button retour = new Button("Retour au Jeu");
        retour.setTheme("/button");
        retour.addCallback(new Runnable() {

            @Override
            public void run() {
                menu.setVisible(false);
            }
        });
        Button quitter = new Button("Quitter");
        quitter.setTheme("/button");
        quitter.addCallback(new Runnable() {

            @Override
            public void run() {
                System.exit(0);
            }
        });

        DialogLayout l = new DialogLayout();
        l.setTheme("/dialoglayout");
        l.setHorizontalGroup(l.createParallelGroup(options, deconnexion, retour, quitter));
        l.setVerticalGroup(l.createSequentialGroup(options, deconnexion, retour, quitter));
        menu.add(l);


        GUI_Manager.instance.getRoot().add(chat_frame);
        GUI_Manager.instance.getRoot().add(menu);

        menu.adjustSize();
        menu.setPosition((Base.sizeOfScreen_x/2)-(menu.getWidth()/2), (Base.sizeOfScreen_y/2)-(menu.getHeight()/2));
        menu.setVisible(false);
    }

    public void refresh()
    {
        for(int i = 0; i < backside_widgets.size(); i++)
        {
            if(backside_widgets.get(i) instanceof TextBubble)
            {


                if(((TextBubble)backside_widgets.get(i)).getChrono().getTime() > 5000)
                {
                    ((TextBubble)backside_widgets.get(i)).getChrono().stop();
                    GUI_Manager.instance.getRoot().removeChild(backside_widgets.get(i));
                    backside_widgets.remove(backside_widgets.get(i));
                }
            }
        }

        this.inventaireUI.refresh(MainJoueur.instance.getPerso().getInventaire());
        InventairePanel inventory_panel = this.inventaireUI.getInventory_panel();
        for(int i = 0; i < inventory_panel.getSlots().size(); i++)
        {
            for(int j = 0; j < inventory_panel.getSlots().get(i).size();j++)
            {
                if(inventory_panel.getSlots().get(i).get(j).isFenOk())
                {
                     ResizableFrame panInfo = InventaireUI.panInfo(inventory_panel.getSlots().get(i).get(j).getItem());

                     GUI_Manager.instance.getRoot().add(panInfo);


                     panInfo.setPosition((inventory_panel.getX()+(inventory_panel.getWidth()/2))-(panInfo.getWidth()/2), (inventory_panel.getY()+(inventory_panel.getHeight()/2))-(panInfo.getHeight()/2));
                     inventory_panel.getSlots().get(i).get(j).setFenOk(false);

                }
            }
        }
    }


    @Override
    public void receiveMessage(String str)
    {

    }

    public static ResizableFrame getDialogPane(String text, int type)
    {
        ResizableFrame frame = new ResizableFrame();
        frame.setTheme("/resizableframe");

        DialogLayout l = new DialogLayout();
        l.setTheme("/dialoglayout");

        Label label = new Label(text);
        label.setTheme("/label");

        if(type == YES_NO_PANE)
        {
            Button ouiB = new Button("Oui");
            ouiB.setTheme("/button");
            Button nonB = new Button("Non");
            nonB.setTheme("/button");

            l.setHorizontalGroup(l.createParallelGroup().addWidget(label).addGroup(l.createSequentialGroup(ouiB, nonB)));
            l.setVerticalGroup(l.createSequentialGroup().addWidget(label).addGroup(l.createParallelGroup(ouiB, nonB)));
        }
        else if(type == CANCEL_PANE)
        {
            Button annuler = new Button("Annuler");
            annuler.setTheme("/button");

            l.setHorizontalGroup(l.createParallelGroup(label, annuler));
            l.setVerticalGroup(l.createSequentialGroup(label, annuler));
        }

        frame.add(l);

        return frame;
    }

    public void addBacksideWidget(Widget widget)
    {
        if(widget instanceof TextBubble)
        {
            for(int i = 0; i < backside_widgets.size(); i++)
            {
                if(backside_widgets.get(i) instanceof TextBubble)
                {
                    ((TextBubble)backside_widgets.get(i)).getChrono().stop();
                    GUI_Manager.instance.getRoot().removeChild(backside_widgets.get(i));
                    backside_widgets.remove(backside_widgets.get(i));
                }
            }
        }
        GUI_Manager.instance.getRoot().insertChild(widget,backside_widgets.size());
        backside_widgets.add(widget);

    }

    public ChatFrame getChat_frame() {
        return chat_frame;
    }

    public void setChat_frame(ChatFrame chat_frame) {
        this.chat_frame = chat_frame;
    }

    public InventaireUI getInventaireUI() {
        return inventaireUI;
    }

    public void setInventaireUI(InventaireUI inventaireui) {
        this.inventaireUI = inventaireui;
    }

    public FenCaracs getFencaracs() {
        return fencaracs;
    }

    public void setFencaracs(FenCaracs fencaracs) {
        this.fencaracs = fencaracs;
    }

    public ResizableFrame getMenu() {
        return menu;
    }

    public void setMenu(ResizableFrame menu) {
        this.menu = menu;
    }

    public ArrayList<Widget> getBackside_widgets() {
        return backside_widgets;
    }

    public void setBackside_widgets(ArrayList<Widget> backside_widgets) {
        this.backside_widgets = backside_widgets;
    }

    public PnjDialogFrame getPnjdialogframe() {
        return pnjdialogframe;
    }

    public void setPnjdialogframe(PnjDialogFrame pnjdialogframe) {
        this.pnjdialogframe = pnjdialogframe;
    }




}
