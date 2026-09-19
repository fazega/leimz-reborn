package com.utils.gui;

import java.util.ArrayList;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.Widget;

public class Toolbox extends ResizableFrame {
    private ArrayList<ArrayList<PointeurGUI>> pointeurs;
    private PointeurGUI selected;

    public enum Pointeur {
        MAIN,
        PLUS,
        SELEC,
        SUPPR
    };

    public Toolbox() {
        this.setTheme("/resizableframe");
        this.setTitle("Toolbox");

        try {
            init();
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Pointeur getSelectedPointeur() {
        return selected.getPointeur();
    }

    private void init() throws SlickException {

        pointeurs = new ArrayList<ArrayList<PointeurGUI>>();
        pointeurs.add(new ArrayList<PointeurGUI>());
        pointeurs.get(0).add(new PointeurGUI(new Image("data/main.png"), Pointeur.MAIN));
        pointeurs.get(0).add(new PointeurGUI(new Image("data/plus.png"), Pointeur.PLUS));
        pointeurs.get(0).add(new PointeurGUI(new Image("data/plus.png"), Pointeur.SELEC));
        pointeurs.get(0).add(new PointeurGUI(new Image("data/plus.png"), Pointeur.SUPPR));
        selected = pointeurs.get(0).get(2);

        Widget pointeursLayout = new Widget();
        for (int i = 0; i < pointeurs.size(); i++) {
            for (int j = 0; j < pointeurs.get(i).size(); j++) {
                pointeursLayout.add(pointeurs.get(i).get(j));
            }
        }

        this.add(pointeursLayout);

        int x = 0, y = 0;
        for (int i = 0; i < pointeurs.size(); i++) {
            x = 0;
            for (int j = 0; j < pointeurs.get(i).size(); j++) {
                pointeurs.get(i).get(j).setPosition(x, y);
                x += pointeurs.get(i).get(j).getImg().getWidth() + 15;
            }
            y += 30;
        }
    }

    public void refresh() {
        for (int i = 0; i < pointeurs.size(); i++) {
            for (int j = 0; j < pointeurs.get(i).size(); j++) {
                if (pointeurs.get(i).get(j).isSelected()
                        && !pointeurs.get(i).get(j).equals(selected)) {
                    selected = pointeurs.get(i).get(j);
                    i = pointeurs.size() - 1;
                    j = pointeurs.get(i).size();
                }
            }
        }

        for (int i = 0; i < pointeurs.size(); i++) {
            for (int j = 0; j < pointeurs.get(i).size(); j++) {
                if (!pointeurs.get(i).get(j).equals(selected)) {
                    pointeurs.get(i).get(j).setSelected(false);
                }
            }
        }
    }

    class PointeurGUI extends Widget {
        Image img, selec;
        Pointeur pointeur;
        boolean selected;

        public PointeurGUI(Image img, Pointeur pointeur) {
            this.img = img;
            try {
                this.selec = new Image("data/GUI/Images/selected.png");
            } catch (SlickException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.pointeur = pointeur;
            this.setSize(img.getWidth(), img.getHeight());
        }

        @Override
        public void paintWidget(GUI gui) {
            img.draw(this.getX(), this.getY());
            if (selected) {}
        }

        @Override
        protected boolean handleEvent(Event evt) {
            if (evt.isMouseEventNoWheel()) {
                if (evt.getType() == Event.Type.MOUSE_CLICKED) {
                    System.out.println(pointeur.toString() + " clicked !");
                    selected = true;
                }
                return true;
            }
            return super.handleEvent(evt);
        }

        public Image getImg() {
            return img;
        }

        public void setImg(Image img) {
            this.img = img;
        }

        public Pointeur getPointeur() {
            return pointeur;
        }

        public void setPointeur(Pointeur pointeur) {
            this.pointeur = pointeur;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    };
}
