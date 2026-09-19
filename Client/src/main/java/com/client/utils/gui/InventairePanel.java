package com.client.utils.gui;

import java.util.ArrayList;

import com.client.gameplay.Inventaire;

import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;

public class InventairePanel extends Widget {

    private ArrayList<ArrayList<ItemSlot>> slots;

    private int slotSpacing = 10;

    private ItemSlot dragSlot;
    private ItemSlot dropSlot;

    private Inventaire inventaire;

    public InventairePanel(int numSlotsX, int numSlotsY, Inventaire inventaire) {
        this.slots = new ArrayList<ArrayList<ItemSlot>>();
        this.inventaire = inventaire;

        ItemSlot.DragListener listener =
                new ItemSlot.DragListener() {
                    public void dragStarted(ItemSlot slot, Event evt) {
                        InventairePanel.this.dragStarted(slot, evt);
                    }

                    public void dragging(ItemSlot slot, Event evt) {
                        InventairePanel.this.dragging(slot, evt);
                    }

                    public void dragStopped(ItemSlot slot, Event evt) {
                        InventairePanel.this.dragStopped(slot, evt);
                    }
                };

        for (int i = 0; i < numSlotsY; i++) {
            slots.add(new ArrayList<ItemSlot>());
            for (int j = 0; j < numSlotsX; j++) {
                slots.get(i).add(new ItemSlot());
                slots.get(i).get(j).setListener(listener);
                this.add(slots.get(i).get(j));
            }
        }

        int j = 0;
        for (int i = 0; i < this.inventaire.getItems().size(); i++) {
            if (i < slots.get(0).size()) {
                slots.get(j).get(i).setItem(inventaire.getItems().get(i));
            } else {
                j++;
            }
        }

        this.setTheme("/inventairepanel");
    }

    @Override
    public int getPreferredInnerWidth() {
        return (slots.get(0).get(0).getWidth() + slotSpacing) * slots.get(0).size() - slotSpacing;
    }

    @Override
    public int getPreferredInnerHeight() {
        return (slots.get(0).get(0).getHeight() + slotSpacing) * slots.size() - slotSpacing;
    }

    @Override
    protected void layout() {
        int slotWidth = slots.get(0).get(0).getWidth();
        int slotHeight = slots.get(0).get(0).getHeight();

        int x = 0, y = 0;
        for (int i = 0; i < slots.size(); i++) {
            x = 0;
            for (int j = 0; j < slots.get(i).size(); j++) {
                slots.get(i).get(j).setPosition(x + getInnerX(), y + getInnerY());
                x += slotWidth + slotSpacing;
            }
            y += slotHeight + slotSpacing;
        }
    }

    @Override
    protected void applyTheme(ThemeInfo themeInfo) {
        super.applyTheme(themeInfo);
        slotSpacing = 10;
    }

    void dragStarted(ItemSlot slot, Event evt) {
        if (slot.getItem() != null) {
            dragSlot = slot;
            dragging(slot, evt);
        }
    }

    void dragging(ItemSlot slot, Event evt) {
        if (dragSlot != null) {
            Widget w = getWidgetAt(evt.getMouseX(), evt.getMouseY());
            if (w instanceof ItemSlot) {
                setDropSlot((ItemSlot) w);
            } else {
                setDropSlot(null);
            }
        }
    }

    void dragStopped(ItemSlot slot, Event evt) {
        if (dragSlot != null) {
            dragging(slot, evt);
            if (dropSlot != null && dropSlot.canDrop() && dropSlot != dragSlot) {
                dropSlot.setItem(dragSlot.getItem());
                dragSlot.setItem(null);
            }
            setDropSlot(null);
            dragSlot = null;
        }
    }

    private void setDropSlot(ItemSlot slot) {
        if (slot != dropSlot) {
            if (dropSlot != null) {
                dropSlot.setDropState(false, false);
            }
            dropSlot = slot;
            if (dropSlot != null) {
                dropSlot.setDropState(true, dropSlot == dragSlot || dropSlot.canDrop());
            }
        }
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }

    public void refresh(Inventaire inventaire) {
        int j = 0;
        for (int i = 0; i < slots.size(); i++) {
            for (int k = 0; k < slots.get(i).size(); k++) {
                slots.get(i).get(k).setItem(null);
            }
        }
        for (int i = 0; i < this.inventaire.getItems().size(); i++) {
            if (i < slots.get(0).size()) {
                slots.get(j).get(i).setItem(inventaire.getItems().get(i));
            } else {
                j++;
            }
        }
    }

    public ArrayList<ArrayList<ItemSlot>> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<ArrayList<ItemSlot>> slots) {
        this.slots = slots;
    }
}
