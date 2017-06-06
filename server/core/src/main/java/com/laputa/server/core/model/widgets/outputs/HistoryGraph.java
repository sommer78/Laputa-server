package com.laputa.server.core.model.widgets.outputs;

import com.laputa.server.core.model.enums.GraphPeriod;
import com.laputa.server.core.model.enums.PinType;
import com.laputa.server.core.model.widgets.MultiPinWidget;
import com.laputa.server.core.model.widgets.Widget;
import io.netty.channel.Channel;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 12.08.15.
 */
public class HistoryGraph extends MultiPinWidget {

    public GraphPeriod period;

    public int max;

    public int min;

    public boolean autoYCoords;

    public boolean showLegends;
    
    @Override
    public boolean isSame(int deviceId, byte pinIn, PinType pinType) {
        return false;
    }

    @Override
    public boolean isSplitMode() {
        return false;
    }

    @Override
    public boolean updateIfSame(int deviceId, byte pin, PinType type, String value) {
        return false;
    }

    @Override
    public void updateIfSame(Widget widget) {
        //do nothing
    }

    @Override
    public void sendAppSync(Channel appChannel, int dashId, int targetId) {
    }

    @Override
    public String getModeType() {
        return null;
    }

    @Override
    public String makeHardwareBody(byte pinIn, PinType pinType) {
        return null;
    }

    @Override
    public int getPrice() {
        return 900;
    }
}
