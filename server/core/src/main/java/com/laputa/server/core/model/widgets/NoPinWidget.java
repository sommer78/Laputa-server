package com.laputa.server.core.model.widgets;

import com.laputa.server.core.model.enums.PinType;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.04.16.
 */
public abstract class NoPinWidget extends Widget {

    @Override
    public boolean updateIfSame(int deviceId, byte pin, PinType type, String values) {
        return false;
    }

    @Override
    public void updateIfSame(Widget widget) {
        //do nothing
    }

    @Override
    public boolean isSame(int deviceId, byte pin, PinType type) {
        return false;
    }

    @Override
    public String getJsonValue() {
        return null;
    }

    @Override
    public String getModeType() {
        return null;
    }

    @Override
    public String getValue(byte pin, PinType type) {
        return null;
    }

    @Override
    public void append(StringBuilder sb, int deviceId) {
    }

}
