package com.laputa.server.core.model.widgets.controls;

import com.laputa.server.core.model.widgets.OnePinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class OneAxisJoystick extends OnePinWidget {

    public boolean autoReturnOn;

    public boolean horizontal;

    public int frequency;

    @Override
    public String getModeType() {
        return "out";
    }

    @Override
    public boolean isPWMSupported() {
        return pwmMode;
    }

    @Override
    public int getPrice() {
        return 200;
    }

}
