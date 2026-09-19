package com.client.utils.gui;

import java.util.ArrayList;
import com.client.entities.PNJ;
import com.client.gameplay.PNJ_discours;
import com.client.network.NetworkListener;
import com.client.network.NetworkManager;

import de.matthiasmann.twl.Alignment;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.CallbackWithReason;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.ListBox.CallbackReason;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;

public class PnjDialogFrame extends ResizableFrame implements NetworkListener {
    private PNJ pnj;
    private HTMLTextAreaModel textAreaModel = new HTMLTextAreaModel();
    private TextArea area;
    private ListBox<String> lb;
    private PNJ_discours encours;

    public PnjDialogFrame(PNJ pnj) {
        this.pnj = pnj;

        this.setTheme("/resizableframe");
        this.setTitle(pnj.getNom());
        NetworkManager.instance.sendToServer("pd;" + pnj.getNom() + ";NULL");
    }

    private void init() {
        area = new TextArea(textAreaModel);
        area.setTheme("/textarea");

        String s = encours.getDiscours();
        String d = s.replaceAll("#n", "<br/>");
        String sb =
                "<div style=\"word-wrap: break-word; font-family: default; \"><p>"
                        + d
                        + "</p></div>";

        textAreaModel.setHtml(sb);

        SimpleChangableListModel<String> lm =
                new SimpleChangableListModel<String>(encours.getReponsesString());
        lb = new ListBox<String>(lm);
        lb.setTheme("/listbox");

        lb.addCallback(
                new CallbackWithReason<ListBox.CallbackReason>() {

                    @Override
                    public void callback(CallbackReason cbreason) {
                        if (cbreason == CallbackReason.MOUSE_CLICK) {
                            NetworkManager.instance.sendToServer(
                                    "pd;"
                                            + pnj.getNom()
                                            + ";"
                                            + encours.getReponses().get(lb.getSelected()).getId());
                            return;
                        }
                    }
                });

        Button aButton = new Button("Annuler");
        aButton.setTheme("/button");
        aButton.addCallback(
                new Runnable() {

                    @Override
                    public void run() {
                        setVisible(false);
                        pnj.setPnjDiscours(null);
                    }
                });

        DialogLayout l = new DialogLayout();
        l.setTheme("/dialoglayout");
        l.setHorizontalGroup(
                l.createParallelGroup()
                        .addWidget(area)
                        .addWidget(lb)
                        .addWidget(aButton, Alignment.CENTER));
        l.setVerticalGroup(
                l.createSequentialGroup()
                        .addWidget(area)
                        .addGap(40)
                        .addWidget(lb)
                        .addGap()
                        .addWidget(aButton));

        this.add(l);
    }

    @Override
    public void receiveMessage(String str) {
        String[] temp = str.split(";");
        if (this.pnj.getPnjDiscours() == null) {
            this.pnj.setPnjDiscours(
                    new PNJ_discours(
                            temp[1],
                            null,
                            new ArrayList<PNJ_discours>(),
                            Integer.parseInt(temp[0])));
            for (int i = 2; i < temp.length; i += 3) {
                PNJ_discours r =
                        new PNJ_discours(
                                temp[i + 2],
                                this.pnj.getPnjDiscours(),
                                new ArrayList<PNJ_discours>(),
                                Integer.parseInt(temp[i]));
                this.pnj.getPnjDiscours().getReponses().add(r);
            }
            encours = pnj.getPnjDiscours();
            this.init();
        } else {
            if (temp[0].equals("NULL")) {
                this.setVisible(false);
                pnj.setPnjDiscours(null);
                return;
            }
            PNJ_discours parent = pnj.getDiscoursWithId(Integer.parseInt(temp[0]));
            parent.addReponses(
                    new PNJ_discours(
                            temp[2],
                            parent,
                            new ArrayList<PNJ_discours>(),
                            Integer.parseInt(temp[1])));
            for (int i = 3; i < temp.length; i += 3) {
                PNJ_discours r =
                        new PNJ_discours(
                                temp[i + 2],
                                parent.getReponses().get(0),
                                new ArrayList<PNJ_discours>(),
                                Integer.parseInt(temp[i]));
                parent.getReponses().get(0).getReponses().add(r);
            }
            encours = parent.getReponses().get(0);
            String d = encours.getDiscours().replaceAll("#n", "<br/>");
            String sb =
                    "<div style=\"word-wrap: break-word; font-family: default; \"><p>"
                            + d
                            + "</p></div>";
            textAreaModel.setHtml(sb);

            SimpleChangableListModel<String> sclm =
                    new SimpleChangableListModel<String>(encours.getReponsesString());
            lb.setModel(sclm);
        }
    }

    public PNJ getPnj() {
        return pnj;
    }

    public void setPnj(PNJ pnj) {
        this.pnj = pnj;
    }
}
