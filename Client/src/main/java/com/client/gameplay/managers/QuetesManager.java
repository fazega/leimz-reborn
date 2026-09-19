package com.client.gameplay.managers;

import java.util.ArrayList;

import com.client.display.gui.GUI_Manager;
import com.client.entities.Joueur;
import com.client.gameplay.Quete;
import com.client.gameplay.QueteObjectif;
import com.client.gamestates.Base;
import com.client.network.NetworkListener;

import de.matthiasmann.twl.Alignment;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.model.SimpleChangableListModel;

public class QuetesManager implements NetworkListener {
    private ArrayList<Quete> quetes;

    public QuetesManager(ArrayList<Quete> quetes) {
        this.quetes = quetes;
    }

    public QuetesManager(Quete quete) {
        this.quetes = new ArrayList<Quete>();
        quetes.add(quete);
    }

    public QuetesManager() {
        this.quetes = new ArrayList<Quete>();
    }

    public void addQuetes(Quete quete) {
        quetes.add(quete);
    }

    public void testQuetes(Joueur joueur) {
        for (int i = 0; i < quetes.size(); i++) {
            quetes.get(i).testObjectifs(joueur);
        }
    }

    public ArrayList<Quete> getQuetes() {
        return quetes;
    }

    public void setQuetes(ArrayList<Quete> quetes) {
        this.quetes = quetes;
    }

    @Override
    public void receiveMessage(String str) {
        String[] temp = str.split(";");
        if (temp[0].equals("new")) {
            Quete quete = new Quete(temp[2], temp[1]);
            // Le temp[3] est "obj"
            for (int i = 4; i < temp.length; i += 3) {
                quete.getObjectifs().add(new QueteObjectif(temp[i], temp[i + 1], temp[i + 2]));
            }
            quetes.add(quete);
            System.out.println("Quete ajoutÃ©e !");
            final ResizableFrame frame = newQueteFrame(quete);
            GUI_Manager.instance.getRoot().add(frame);
            frame.adjustSize();
            frame.setPosition(
                    Base.sizeOfScreen_x / 2 - frame.getWidth() / 2,
                    Base.sizeOfScreen_y / 2 - frame.getHeight() / 2);
            // TODO mettre la fenÃªtre en keyboard focus sans planter le truc ...
            /*GUI_Manager.instance.getGui().invokeLater(new Runnable(){
                @Override
                public void run() {
                    frame.requestKeyboardFocus();
                }
            });*/
        }
    }

    public static ResizableFrame newQueteFrame(Quete quete) {
        final ResizableFrame frame = new ResizableFrame();
        frame.setTitle("Nouvelle quÃªte !");

        Label labN = new Label("Nom de la quÃªte : " + quete.getNom());
        labN.setTheme("/label");
        Label labC = new Label("Nom du commanditaire : " + quete.getCommanditaire());
        labC.setTheme("/label");

        ArrayList<String> o_s = new ArrayList<String>();
        for (int i = 0; i < quete.getObjectifs().size(); i++) {
            o_s.add(quete.getObjectifs().get(i).getDescription());
        }
        SimpleChangableListModel<String> lm = new SimpleChangableListModel<String>(o_s);
        @SuppressWarnings("rawtypes")
        ListBox lb = new ListBox<String>(lm);
        lb.setTheme("/listbox");

        Button okButton = new Button("OK");
        okButton.setTheme("/button");
        okButton.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        frame.setVisible(false);
                    }
                });

        DialogLayout l = new DialogLayout();
        l.setTheme("/dialoglayout");
        l.setHorizontalGroup(
                l.createParallelGroup()
                        .addWidget(labN)
                        .addWidget(labC)
                        .addWidget(lb)
                        .addWidget(okButton, Alignment.CENTER));
        l.setVerticalGroup(
                l.createSequentialGroup()
                        .addWidget(labN)
                        .addWidget(labC)
                        .addGap(40)
                        .addWidget(lb)
                        .addGap()
                        .addWidget(okButton));

        frame.add(l);

        return frame;
    }
}
