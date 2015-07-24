package net.trendl.procrasthelper.web.domain;

import java.io.Serializable;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
public class UIMessage implements Serializable {
    private String text;
    private Object obj;

    public UIMessage() {}

    public UIMessage(String text) {
        this.text = text;
    }

    public UIMessage(String text, Object object) {
        this.text = text;
        this.obj = object;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
