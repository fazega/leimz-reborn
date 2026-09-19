package com.client.events;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.client.display.gui.GUI_Manager;
import com.client.entities.Joueur;
import com.client.entities.MainJoueur;
import com.client.entities.PNJ;
import com.client.entities.Entity.Etat;
import com.client.entities.managers.EntitiesManager;
import com.client.gameplay.managers.CombatManager;
import com.client.gamestates.Base;
import com.client.map.Tile;
import com.client.map.managers.MapManager;
import com.client.network.NetworkManager;
import com.client.utils.gui.PnjDialogFrame;
import com.client.utils.gui.PrincipalGui;
import com.client.utils.pathfinder.PathFinder;
import de.matthiasmann.twl.CallbackWithReason;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ListBox.CallbackReason;
import de.matthiasmann.twl.model.SimpleChangableListModel;

public class MainEventListener extends EventListener
{
    public static MainEventListener instance;

    private Input input;

    private PathFinder pf;

    public MainEventListener(PathFinder pf, Input input)
    {
        this.pf = pf;
        this.input = input;
    }

    @Override
    public void pollEvents()
    {
        if(input.isMousePressed(0))
        {
            boolean entity_pressed = false;
            for(int i = 0; i < PrincipalGui.instance.getBackside_widgets().size(); i++)
            {
                if(PrincipalGui.instance.getBackside_widgets().get(i) instanceof ListBox)
                {
                    GUI_Manager.instance.getRoot().removeChild(PrincipalGui.instance.getBackside_widgets().get(i));
                    PrincipalGui.instance.getBackside_widgets().remove(i);
                }
            }
            for(int i = 0; i < EntitiesManager.instance.getEntities().size(); i++)
            {
                Rectangle c = new Rectangle(
                        EntitiesManager.instance.getEntities().get(i).getCorps().getX()+EntitiesManager.instance.getEntities().get(i).getPos_real_on_screen().x,
                        EntitiesManager.instance.getEntities().get(i).getCorps().getY()+EntitiesManager.instance.getEntities().get(i).getPos_real_on_screen().y,
                        EntitiesManager.instance.getEntities().get(i).getCorps().getWidth(),
                        EntitiesManager.instance.getEntities().get(i).getCorps().getHeight());

                if(c.contains(input.getMouseX(), input.getMouseY()))
                {
                    entity_pressed = true;
                    EntitiesManager.instance.getEntities().get(i).setEtat(Etat.CLICKED);
                    if((EntitiesManager.instance.getEntities().get(i)) instanceof PNJ)
                    {
                        final PNJ pnj = (PNJ) (EntitiesManager.instance.getEntities().get(i));
                        final SimpleChangableListModel<String> l = new SimpleChangableListModel<String>(
                                "Parler", "Defier");
                        final ListBox<String> combobox = new ListBox<String>(l);
                        combobox.setTheme("/popuplistbox");
                        PrincipalGui.instance.addBacksideWidget(combobox);
                        combobox.adjustSize();
                        combobox.setPosition(input.getMouseX(), input.getMouseY());
                        combobox.addCallback(new CallbackWithReason<ListBox.CallbackReason>() {
                            @Override
                            public void callback(CallbackReason cbreason)
                            {
                                if(cbreason == CallbackReason.MOUSE_CLICK)
                                {
                                    int id = combobox.getSelected();

                                    if(l.getEntry(id).equals("Parler"))
                                    {
                                        final PnjDialogFrame dialog = new PnjDialogFrame(pnj);
                                        PrincipalGui.instance.setPnjdialogframe(dialog);
                                        GUI_Manager.instance.getRoot().add(dialog);
                                        dialog.setSize(400, 400);
                                        dialog.setPosition((Base.sizeOfScreen_x/2)-(dialog.getWidth()/2), (Base.sizeOfScreen_y/2)-(dialog.getHeight()/2));
                                        GUI_Manager.instance.getGui().invokeLater(new Runnable(){
                                            @Override
                                            public void run() {
                                                dialog.requestKeyboardFocus();
                                            }
                                        });
                                    }
                                    combobox.setVisible(false);
                                }
                            }
                        });

                    }

                    else if((EntitiesManager.instance.getEntities().get(i)) instanceof Joueur)
                    {
                        if(MainJoueur.instance.getPerso().getCurrent_sort() != null)
                        {
                            String nom_receveur = ((Joueur)(EntitiesManager.instance.getEntities().get(i))).getPerso().getNom();
                            String nom_sort = MainJoueur.instance.getPerso().getCurrent_sort().getNom();
                            NetworkManager.instance.sendToServer("a;"+nom_receveur+";"+nom_sort);
                        }
                        else
                        {
                            if(CombatManager.instance.getCurrent_combat()==null)
                            {
                                CombatManager.instance.askCombat((Joueur)EntitiesManager.instance.getEntities().get(i));
                            }
                        }
                    }
                    else
                    {
                        Tile tile_pressed = MapManager.instance.getTileScreen(new Vector2f(input.getMouseX(), input.getMouseY()));
                        if(tile_pressed != null)
                        {
                            MainJoueur.instance.startMoving(pf.calculateChemin(MainJoueur.instance.getTile(), tile_pressed));
                        }
                    }

                }
            }

            if(!entity_pressed)
            {
                Tile tile_pressed = MapManager.instance.getTileScreen(new Vector2f(input.getMouseX(), input.getMouseY()));
                if(tile_pressed != null && tile_pressed.getPos().x>=2 && tile_pressed.getPos().x<=MapManager.instance.getEntire_map().getGrille()[0].length-3 && tile_pressed.getPos().y>=2 && tile_pressed.getPos().y<=MapManager.instance.getEntire_map().getGrille()[0].length-3)
                {
                    MainJoueur.instance.startMoving(pf.calculateChemin(MainJoueur.instance.getTile(), tile_pressed));
                }
            }
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
    }

    @Override
    public void mouseWheelMoved(int change) {
    }

    @Override
    public void keyPressed(int key, char c) {
        if(key == Input.KEY_ESCAPE)
        {
            PrincipalGui.instance.getMenu().setVisible(!PrincipalGui.instance.getMenu().isVisible());
            PrincipalGui.instance.getMenu().requestKeyboardFocus();
        }

        if(key==Input.KEY_I)
        {
            PrincipalGui.instance.getInventaireUI().setVisible(!PrincipalGui.instance.getInventaireUI().isVisible());
            PrincipalGui.instance.getInventaireUI().requestKeyboardFocus();
        }

        if(key==Input.KEY_P)
        {
            PrincipalGui.instance.getFencaracs().setVisible(!PrincipalGui.instance.getFencaracs().isVisible());
            PrincipalGui.instance.getFencaracs().requestKeyboardFocus();
        }

        if(key ==Input.KEY_SPACE)
        {
            if(MainJoueur.instance.getPerso().getSorts().get(1).equals(MainJoueur.instance.getPerso().getCurrent_sort()))
            {
                MainJoueur.instance.getPerso().setCurrent_sort(null);
            }
            else
            {
                MainJoueur.instance.getPerso().setCurrent_sort(MainJoueur.instance.getPerso().getSorts().get(1));
            }
        }
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        float mouseX = newx;
        float mouseY = newy;

        for(int i = 0; i < EntitiesManager.instance.getEntities().size(); i++)
        {
            if(EntitiesManager.instance.getEntities().get(i).getPos_real_on_screen()!=null)
            {
                if((new Rectangle(EntitiesManager.instance.getEntities().get(i).getPos_real_on_screen().x+EntitiesManager.instance.getEntities().get(i).getCorps().getX(), EntitiesManager.instance.getEntities().get(i).getPos_real_on_screen().y+EntitiesManager.instance.getEntities().get(i).getCorps().getY(), EntitiesManager.instance.getEntities().get(i).getCorps().getWidth(), EntitiesManager.instance.getEntities().get(i).getCorps().getHeight())).contains(mouseX, mouseY))
                {
                    EntitiesManager.instance.getEntities().get(i).setEtat(Etat.OVER);
                }
                else
                {
                    EntitiesManager.instance.getEntities().get(i).setEtat(Etat.NORMAL);
                }
            }
        }

    }

    @Override
    public void keyReleased(int key, char c) {
        // TODO Auto-generated method stub

    }


}
