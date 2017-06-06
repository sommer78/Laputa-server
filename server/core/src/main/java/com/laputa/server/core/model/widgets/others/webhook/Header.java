package com.laputa.server.core.model.widgets.others.webhook;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 05.09.16.
 */
public class Header {

    public String name;

    public String value;

    public Header() {
    }

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean isValid() {
        return name != null && value != null;
    }
}
