package com.laputa.server.core.model.widgets.ui;

import com.laputa.server.core.model.widgets.NoPinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 13.09.16.
 */
public class Image extends NoPinWidget {

    public String imageId;

    @Override
    public int getPrice() {
        return 200;
    }
}
