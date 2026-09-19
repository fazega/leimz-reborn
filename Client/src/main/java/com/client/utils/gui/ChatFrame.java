package com.client.utils.gui;

import org.lwjgl.Sys;
import org.newdawn.slick.geom.Vector2f;

import com.client.display.gui.GUI_Manager;
import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.entities.managers.EntitiesManager;
import com.client.network.NetworkListener;
import com.client.network.NetworkManager;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;

public class ChatFrame extends ResizableFrame implements NetworkListener {
    private final StringBuilder sb;
    private final HTMLTextAreaModel textAreaModel;
    private final TextArea textArea;

    private final EditField editField;
    private final ScrollPane scrollPane;
    private String curColor = "black";

    public ChatFrame(Vector2f size) {

        this.sb = new StringBuilder();
        this.textAreaModel = new HTMLTextAreaModel();
        this.textArea = new TextArea(textAreaModel);
        this.textArea.setTheme("/textarea");
        this.editField = new EditField();
        this.editField.setTheme("/editfield");

        editField.addCallback(
                new EditField.Callback() {
                    public void callback(int key) {
                        if (key == Event.KEY_RETURN) {
                            String path = null;
                            if (curColor.equals("black")) {
                                path = "default";
                            } else {
                                path = "font_" + curColor;
                            }

                            appendWhenCallBack(path);

                            editField.setText("");
                            curColor = "black";
                        }
                    }
                });

        textArea.addCallback(
                new TextArea.Callback() {
                    public void handleLinkClicked(String href) {
                        Sys.openURL(href);
                    }
                });

        scrollPane = new ScrollPane(textArea);
        scrollPane.setTheme("/scrollpane");
        scrollPane.setFixed(ScrollPane.Fixed.HORIZONTAL);
        scrollPane.setPosition(70, 20);

        DialogLayout l = new DialogLayout();
        l.setTheme("/dialoglayout");
        l.setHorizontalGroup(l.createParallelGroup(scrollPane, editField));
        l.setVerticalGroup(l.createSequentialGroup(scrollPane, editField));

        this.add(l);
    }

    public void releaseKeyboardFocusOutside(int x, int y) {
        if (editField.hasKeyboardFocus() && !editField.isInside(x, y)) {
            editField.giveupKeyboardFocus();
        }
    }

    public void appendRow(String font, String text) {
        sb.append("<div style=\"word-wrap: break-word; font-family: ").append(font).append("; \">");
        for (int i = 0, l = text.length(); i < l; i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case ':':
                    if (text.startsWith(":)", i)) {
                        sb.append("<img src=\"smiley\" alt=\":)\"/>");
                        i += 1;
                        break;
                    }
                    sb.append(ch);
                    break;
                case 'h':
                    if (text.startsWith("http://", i)) {
                        int end = i + 7;
                        while (end < l && isURLChar(text.charAt(end))) {
                            end++;
                        }
                        String href = text.substring(i, end);
                        sb.append("<a style=\"font: link\" href=\"")
                                .append(href)
                                .append("\" >")
                                .append(href)
                                .append("</a>");
                        i = end - 1;
                        break;
                    }
                case '/':
                    if (text.startsWith("/n", i)) {
                        sb.append("<br/>");
                        i += 1;
                        break;
                    }
                default:
                    sb.append(ch);
            }
        }
        sb.append("</div>");

        boolean isAtEnd = scrollPane.getMaxScrollPosY() == scrollPane.getScrollPositionY();

        textAreaModel.setHtml(sb.toString());

        if (isAtEnd) {
            // scrollPane.validateLayout();
            scrollPane.setScrollPositionY(scrollPane.getMaxScrollPosY());
        }
    }

    public void appendWhenCallBack(String path) {
        /*if(editField.getText().contains("\\i"))
        {
            appendRow("font_red", "/nInventaire");
            appendRow(path, "---------------------------");
            appendRow(path, "/n");
            for(int i = 0; i < main_player.getPerso().getInventaire().getObjets().size(); i++)
            {
                appendRow("font_red", "Objet " + i);
                appendRow(path, "--> NOM : " + main_player.getPerso().getInventaire().getObjets().get(i).getNom());
                appendRow(path, "--> DESCRIPTION : " + main_player.getPerso().getInventaire().getObjets().get(i).getDescription());
                appendRow(path, "--> TYPE : " + main_player.getPerso().getInventaire().getObjets().get(i).getType());

                if(main_player.getPerso().getInventaire().getObjets().get(i).getEffet() != null)
                    appendRow(path, "--> EFFET : " + main_player.getPerso().getInventaire().getObjets().get(i).getEffet().get(0).toString() + ", " + main_player.getPerso().getInventaire().getObjets().get(i).getEffet().get(0));

                else
                    appendRow(path, "--> EFFET : Aucun effet");
                appendRow(path, "/n");
            }
            appendRow(path, "-----------------------------");
        }*/
        if (editField.getText().contains("\\pos")) {
            appendRow(
                    "font_red",
                    "/n/nPosition : "
                            + "["
                            + MainJoueur.instance.getTile().getPos().x
                            + "]["
                            + MainJoueur.instance.getTile().getPos().y
                            + "]");
            appendRow(path, "/n/n");
        } else {
            NetworkManager.instance.sendToServer(
                    "sa;" + MainJoueur.instance.getPerso().getNom() + ";a;" + editField.getText());
            appendRow(
                    "default",
                    (MainJoueur.instance.getPerso().getNom() + " : " + editField.getText()));
            createBulle(MainJoueur.instance.getPerso().getNom(), editField.getText());
        }
    }

    @Override
    public void receiveMessage(String str) {
        final String[] temp = str.split(";");
        if (!temp[0].equals(MainJoueur.instance.getPerso().getNom())) {
            appendRow("default", temp[0] + " : " + temp[2]);
            GUI_Manager.instance
                    .getGui()
                    .invokeLater(
                            new Runnable() {

                                @Override
                                public void run() {
                                    createBulle(temp[0], temp[2]);
                                }
                            });
        }
    }

    private void createBulle(String nom_perso, String text) {
        final String ftext = text;
        final Joueur joueur = EntitiesManager.instance.getPlayers_manager().getJoueur(nom_perso);

        TextBubble bubble = new TextBubble(ftext);
        PrincipalGui.instance.addBacksideWidget(bubble);
        bubble.getTextarea().adjustSize();
        bubble.adjustSize();
        bubble.setPosition(
                (int) (joueur.getPos_real_on_screen().x - bubble.getWidth()) + 40,
                (int) (joueur.getPos_real_on_screen().y - bubble.getHeight() + 7));
        joueur.setCurrent_textbubble(bubble);
    }

    private boolean isURLChar(char ch) {
        return (ch == '.')
                || (ch == '/')
                || (ch == '%')
                || (ch >= '0' && ch <= '9')
                || (ch >= 'a' && ch <= 'z')
                || (ch >= 'A' && ch <= 'Z');
    }

    @Override
    protected void layout() {
        super.layout();
    }

    public String getCurColor() {
        return curColor;
    }

    public void setCurColor(String curColor) {
        this.curColor = curColor;
    }
}
