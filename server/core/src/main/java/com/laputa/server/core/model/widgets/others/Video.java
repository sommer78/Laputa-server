package com.laputa.server.core.model.widgets.others;

import com.laputa.server.core.model.widgets.NoPinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class Video extends NoPinWidget {

    public String url;

    @Override
    public int getPrice() {
        return 500;
    }

}
