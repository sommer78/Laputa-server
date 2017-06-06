package com.laputa.server.core.model.widgets.others;

import com.laputa.server.core.model.widgets.NoPinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class Bluetooth extends NoPinWidget {

    public String name;

    public int deviceId;

    @Override
    public int getPrice() {
        return 0;
    }
}
