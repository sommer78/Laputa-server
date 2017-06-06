package com.laputa.server.core.model.widgets.outputs;

import com.laputa.server.core.model.widgets.OnePinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class LED extends OnePinWidget {

    @Override
    public String getModeType() {
        return "in";
    }

    @Override
    public int getPrice() {
        return 100;
    }
}
