package com.client.display.gui;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;
import de.matthiasmann.twl.DesktopArea;
import de.matthiasmann.twl.Widget;

public class RootWidget extends DesktopArea {


    private ArrayList<InWaitWidget> in_wait;

    private class InWaitWidget
    {
        private Widget w;
        private Vector2f pos, size, pos_center, size_center;

        public InWaitWidget(Widget w, Vector2f pos, Vector2f size)
        {
            this.w=w;
            this.pos=pos;
            this.size=size;
        }

        public InWaitWidget(Widget w, Vector2f pos_center, Vector2f size_center, Vector2f size)
        {
            this.w=w;
            this.pos_center=pos_center;
            this.size_center=size_center;
            this.size=size;
        }

        public Widget getW() {
            return w;
        }

        public Vector2f getPos() {
            return pos;
        }

        public Vector2f getSize() {
            return size;
        }

        public Vector2f getPos_center() {
            return pos_center;
        }

        public Vector2f getSize_center() {
            return size_center;
        }
    }

    public RootWidget()
    {
        in_wait= new ArrayList<>();
    }

    public void addToInWaitOfLayout(Widget w, Vector2f pos, Vector2f size)
    {
        in_wait.add(new InWaitWidget(w, pos, size));
    }

    public void addToInWaitOfLayout(Widget w, Vector2f pos_center, Vector2f size_center, Vector2f size)
    {
        in_wait.add(new InWaitWidget(w, pos_center, size_center, size));
    }

    @Override
    protected void layout() {
        super.layout();
        for(int i = 0; i < this.in_wait.size(); i++)
        {
            if(this.in_wait.get(i).getSize()!=null)
            {
                this.in_wait.get(i).getW().setSize((int)this.in_wait.get(i).getSize().x, (int)this.in_wait.get(i).getSize().y);
                System.out.println("Resized!");
            }

            else
            {
                System.out.println("Adjusting size !");
                this.in_wait.get(i).getW().adjustSize();
            }

            if(this.in_wait.get(i).getPos_center()==null)
            {
                 this.in_wait.get(i).getW().setPosition((int)this.in_wait.get(i).getPos().x, (int)this.in_wait.get(i).getPos().y);
            }
            else
            {
                this.in_wait.get(i).getW().setPosition((int)(this.in_wait.get(i).getPos_center().x+(this.in_wait.get(i).getSize_center().x/2))-(this.in_wait.get(i).getW().getWidth()/2), (int)(this.in_wait.get(i).getPos_center().y+(this.in_wait.get(i).getSize_center().y/2))-(this.in_wait.get(i).getW().getHeight()/2));
            }
        }
    }
}
