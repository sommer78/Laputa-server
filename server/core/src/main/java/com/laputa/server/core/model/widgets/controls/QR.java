package com.laputa.server.core.model.widgets.controls;

import com.laputa.server.core.model.widgets.OnePinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class QR extends OnePinWidget {

    @Override
    public String getModeType() {
        return "out";
    }

    @Override
    public int getPrice() {
        return 200;
    }
}
