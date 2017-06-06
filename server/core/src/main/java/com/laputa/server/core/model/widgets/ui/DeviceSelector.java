package com.laputa.server.core.model.widgets.ui;

import com.laputa.server.core.model.widgets.NoPinWidget;
import com.laputa.server.core.model.widgets.Target;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 02.02.17.
 */
public class DeviceSelector extends NoPinWidget implements Target {

    public static final int DEVICE_SELECTOR_STARTING_ID = 200_000;

    //this is selected deviceId in widget
    public volatile int value = 0;

    //this is only for UI. to show only those devices in picker.
    public int[] deviceIds;

    @Override
    public int[] getDeviceIds() {
        return new int[] {value};
    }

    @Override
    public int getDeviceId() {
        return value;
    }

    @Override
    public int getPrice() {
        return 1900;
    }

}
