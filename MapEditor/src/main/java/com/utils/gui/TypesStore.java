package com.utils.gui;

import java.util.ArrayList;
import org.newdawn.slick.geom.Vector2f;
import com.map.Tile;
import com.map.Type_tile;
import de.matthiasmann.twl.*;

public class TypesStore extends ResizableFrame
{
    private TabbedPane onglets;
    private TileSlot draggedTile, dropTile;
    private ArrayList<ArrayList<ArrayList<TileSlot>>> slots_list;

    public static TypesStore instance;

    private Button newTypeButton;

    public Button getNewTypeButton() {
        return newTypeButton;
    }

    public void setNewTypeButton(Button newTypeButton) {
        this.newTypeButton = newTypeButton;
    }

    public TypesStore()
    {

        slots_list = new ArrayList<ArrayList<ArrayList<TileSlot>>>();
        instance=this;
        this.setTheme("resizableframe");

        DialogLayout globalLayout = new DialogLayout();
        globalLayout.setTheme("/dialoglayout");

        onglets = new TabbedPane();
        onglets.setTheme("/tabbedpane");

        ArrayList<ArrayList<TileSlot>> slots = new ArrayList<>();
        ArrayList<TileSlot> tileSlots = new ArrayList<TileSlot>();

        int spacing = 20;
        ArrayList<ArrayList<TileSlot>> lignes = new ArrayList<ArrayList<TileSlot>>();

        int k = 0, h = 0, x = 0, y = 0;
        for(int u = 0; u < Type_tile.types_list.get(0).size(); u++)
        {
            if(k == 0)
            {
                lignes.add(new ArrayList<TileSlot>());
            }

            if(k > 4)
            {
                k = 0;
                h++;
                y += 65 + spacing;
                x=0;
            }


            tileSlots.add(new TileSlot(new Tile(Type_tile.types_list.get(0).get(u))));
            lignes.get(lignes.size()-1).add(tileSlots.get(tileSlots.size()-1));

            tileSlots.get(tileSlots.size()-1).setPos_ini(new Vector2f(x, y));

            x += 80 + spacing;

            k++;
        }
        slots.add(tileSlots);

        Widget widgetTypes = new Widget();
        for(int i = 0 ; i < lignes.size(); i++)
        {
            for(int j = 0; j < lignes.get(i).size(); j++)
            {
                widgetTypes.add(lignes.get(i).get(j));
            }
        }
        slots_list.add(slots);


        ScrollPane scrollPane = new ScrollPane(widgetTypes);
        scrollPane.setTheme("/scrollpane");

        onglets.addTab("Sols", scrollPane);


        ArrayList<ArrayList<TileSlot>> slots_2 = new ArrayList<>();
        ArrayList<TileSlot> tileSlots_2 = new ArrayList<TileSlot>();

        ArrayList<ArrayList<TileSlot>> lignes_2 = new ArrayList<ArrayList<TileSlot>>();

        k = 0;
        h = 0;
        x = 0;
        y = 0;
        for(int u = 0; u < Type_tile.types_list.get(1).size(); u++)
        {
            if(k == 0)
            {
                lignes_2.add(new ArrayList<TileSlot>());
            }

            if(k > 4)
            {
                k = 0;
                h++;
                y += 65 + spacing;
                x=0;
            }

            Tile tile = new Tile(null);
            tile.getTypes().add(null);
            tile.getTypes().add(Type_tile.types_list.get(1).get(u));
            tileSlots_2.add(new TileSlot(tile));
            lignes_2.get(lignes_2.size()-1).add(tileSlots_2.get(tileSlots_2.size()-1));

            tileSlots_2.get(tileSlots_2.size()-1).setPos_ini(new Vector2f(x, y));

            x += 80 + spacing;

            k++;
        }
        slots_2.add(tileSlots_2);

        Widget widgetTypes_2 = new Widget();
        for(int i = 0 ; i < lignes_2.size(); i++)
        {
            for(int j = 0; j < lignes_2.get(i).size(); j++)
            {
                widgetTypes_2.add(lignes_2.get(i).get(j));
            }
        }
        slots_list.add(slots_2);

        ScrollPane scrollPane_2 = new ScrollPane(widgetTypes_2);
        scrollPane_2.setTheme("/scrollpane");


        onglets.addTab("Objets", scrollPane_2);


        /*newTypeButton = new Button("Nouveau type ...");
        newTypeButton.setTheme("/button");*/

        globalLayout.setHorizontalGroup(globalLayout.createParallelGroup(onglets));
        globalLayout.setVerticalGroup(globalLayout.createSequentialGroup().addWidget(onglets));

        this.add(globalLayout);

    }

    public TileSlot getDroppedTile()
    {
        for(int k = 0; k < slots_list.size(); k++)
        {
            for(int u = 0; u < slots_list.get(k).size(); u++)
            {
                for(int i = 0; i < slots_list.get(k).get(u).size(); i++)
                {

                        if(slots_list.get(k).get(u).get(i).isDropped())
                        {
                            dropTile = slots_list.get(k).get(u).get(i);
                            return dropTile;
                        }
                        else
                        {
                            dropTile = null;
                        }

                }
            }
        }

        return dropTile;

    }



    public class TileSlot extends Widget
    {
        private Tile tile;
        private boolean dragActive, dropped;
        private Vector2f pos_ini;

        public TileSlot(Tile tile)
        {
            setSize(80,65);
            this.tile = tile;
        }

        public Vector2f getPos_ini()
        {
            return pos_ini;
        }

        public void setPos_ini(Vector2f posIni)
        {
            setPosition((int)posIni.x, (int)posIni.y);
            pos_ini = posIni;
        }

        @Override
        public void paintWidget(GUI gui)
        {
            tile.getTypes().get(tile.getTypes().size()-1).getImg().draw(getX(), getY());
        }

         @Override
         protected void paintDragOverlay(GUI gui, int mouseX, int mouseY, int modifier)
         {
             tile.getTypes().get(tile.getTypes().size()-1).getImg().draw(mouseX-(getWidth()/2), mouseY-(getHeight()/2));
         }

        @Override
        protected boolean handleEvent(Event evt)
        {
            if(evt.isMouseEventNoWheel())
            {
                if(dragActive)
                {
                    if(evt.isMouseDragEnd())
                    {
                        dropped = true;
                        dragActive = false;
                    }
                }
                else if(evt.isMouseDragEvent())
                {
                    dragActive = true;
                    dropped = false;
                }
                return true;
            }


            return super.handleEvent(evt);
        }

        public boolean isDragActive() {
            return dragActive;
        }

        public void setDragActive(boolean dragActive) {
            this.dragActive = dragActive;
        }

        public Tile getTile() {
            return tile;
        }

        public void setTile(Tile tile) {
            this.tile = tile;
        }

        public boolean isDropped() {
            return dropped;
        }

        public void setDropped(boolean dropped) {
            this.dropped = dropped;
        }


    }


}
