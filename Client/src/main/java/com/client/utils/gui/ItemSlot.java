package com.client.utils.gui;


import org.newdawn.slick.geom.Vector2f;


import com.client.display.gui.GUI_Manager;
import com.client.gameplay.items.SimpleItem;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;

public class ItemSlot extends Widget
{

    public static final StateKey STATE_DRAG_ACTIVE = StateKey.get("dragActive");
    public static final StateKey STATE_DROP_OK = StateKey.get("dropOk");
    public static final StateKey STATE_DROP_BLOCKED = StateKey.get("dropBlocked");

    public interface DragListener
    {
        public void dragStarted(ItemSlot slot, Event evt);
        public void dragging(ItemSlot slot, Event evt);
        public void dragStopped(ItemSlot slot, Event evt);
    }

    private SimpleItem item;
    private DragListener listener;
    private boolean dragActive, fenOk;

    public ItemSlot()
    {
        this.setSize(50, 50);
        this.setTheme("/itemslot");
        setItem(null);
    }

    public SimpleItem getItem() {
        return item;
    }

    public void setItem(SimpleItem item) {
        this.item = item;
    }

    public boolean canDrop()
    {
        return item == null;
    }

    public DragListener getListener() {
        return listener;
    }

    public void setListener(DragListener listener) {
        this.listener = listener;
    }

    public void setDropState(boolean drop, boolean ok) {
        AnimationState as = getAnimationState();
        as.setAnimationState(STATE_DROP_OK, drop && ok);
        as.setAnimationState(STATE_DROP_BLOCKED, drop && !ok);
    }

    @Override
    protected boolean handleEvent(Event evt) {
        if(evt.isMouseEventNoWheel())
        {
            if(evt.getType() == Event.Type.MOUSE_CLICKED)
            {
                if(evt.getMouseClickCount() == 2)
                {
                    if(this.getItem() != null)
                    {
                        ResizableFrame panInfo = InventaireUI.panInfo(this.getItem());

                        GUI_Manager.instance.getRoot().add(panInfo);
                        GUI_Manager.instance.getRoot().addToInWaitOfLayout(panInfo, new Vector2f(PrincipalGui.instance.getInventaireUI().getInventory_panel().getX(), PrincipalGui.instance.getInventaireUI().getInventory_panel().getY()), new Vector2f(PrincipalGui.instance.getInventaireUI().getInventory_panel().getWidth(), PrincipalGui.instance.getInventaireUI().getInventory_panel().getHeight()), new Vector2f(300,300));
                        panInfo.setPosition((PrincipalGui.instance.getInventaireUI().getInventory_panel().getX()+(PrincipalGui.instance.getInventaireUI().getInventory_panel().getWidth()/2))-(panInfo.getWidth()/2), (PrincipalGui.instance.getInventaireUI().getInventory_panel().getY()+(PrincipalGui.instance.getInventaireUI().getInventory_panel().getHeight()/2))-(panInfo.getHeight()/2));
                        panInfo.requestKeyboardFocus();
                    }
                }
            }

            if(dragActive)
            {
                if(evt.isMouseDragEnd())
                {
                    if(listener != null)
                    {
                        listener.dragStopped(this, evt);
                    }
                    dragActive = false;
                    getAnimationState().setAnimationState(STATE_DRAG_ACTIVE, false);
                }
                else if(listener != null)
                {
                    listener.dragging(this, evt);
                }
            }
            else if(evt.isMouseDragEvent())
            {
                dragActive = true;
                getAnimationState().setAnimationState(STATE_DRAG_ACTIVE, true);
                if(listener != null)
                {
                    listener.dragStarted(this, evt);
                }
            }
            return true;
        }


        return super.handleEvent(evt);
    }

    @Override
    protected void paintWidget(GUI gui)
    {
        if(!dragActive && item != null)
        {
            LWJGLRenderer renderer = (LWJGLRenderer)gui.getRenderer();
            renderer.pauseRendering();
            try {
             item.getIcon().draw(this.getX(), this.getY());
            } finally {
               renderer.resumeRendering();
            }
        }
    }

    @Override
    protected void paintDragOverlay(GUI gui, int mouseX, int mouseY, int modifier)
    {
        if(item != null)
        {
            item.getIcon().draw(mouseX-(this.getWidth()/2), mouseY-(this.getHeight()/2));
        }
    }

    @Override
    protected void applyTheme(ThemeInfo themeInfo) {
        super.applyTheme(themeInfo);
    }

    public boolean isFenOk() {
        return fenOk;
    }

    public void setFenOk(boolean fenOk) {
        this.fenOk = fenOk;
    }


}
