package com.client.utils.gui;

import com.client.utils.Chrono;

import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;

public class TextBubble extends Widget {
    private HTMLTextAreaModel model;
    private TextArea textarea;
    private String text;
    private Chrono chrono;

    public TextBubble(String text) {
        this.text = text;
        this.init();
    }

    public void init() {
        this.setTheme("/bullewidget");
        model = new HTMLTextAreaModel();
        textarea = new TextArea(model);
        model.setHtml("<div style=\"font-family: default; \">" + text + "</div>");
        this.add(textarea);

        chrono = new Chrono();
        chrono.start();
    }

    public TextArea getTextarea() {
        return textarea;
    }

    public void setTextarea(TextArea textarea) {
        this.textarea = textarea;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Chrono getChrono() {
        return chrono;
    }

    public void setChrono(Chrono chrono) {
        this.chrono = chrono;
    }
}
