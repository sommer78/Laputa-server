package com.laputa.server.core.model.widgets.ui;

import com.laputa.server.core.model.widgets.NoPinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 07.02.16.
 */
public class Tabs extends NoPinWidget {

    public Tab[] tabs;

    public boolean bubbleOn;

    public volatile int color;

    public int textColor;

    public Tabs() {
        this.tabId = -1;
    }

    @Override
    public int getPrice() {
        return 0;
    }

}
