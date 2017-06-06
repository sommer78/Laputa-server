package com.laputa.server.core.model.widgets.others.eventor.model.action;

import com.laputa.server.core.model.Pin;
import com.laputa.server.core.model.enums.PinType;
import com.laputa.utils.StringUtils;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.08.16.
 */
public class SetPinAction extends BaseAction {

    public Pin pin;

    public String value;

    public SetPinActionType setPinType;

    public SetPinAction() {
    }

    public SetPinAction(byte pin, PinType pinType, String value) {
        this.pin = new Pin(pin, pinType);
        //this is dirty hack for back compatibility.
        //this is mistakes of our youth. sorry for that :).
        //todo remove some day in future.
        if (value.contains(StringUtils.BODY_SEPARATOR_STRING)) {
            String[] split = StringUtils.split3(value);
            this.value = split[2];
        } else {
            this.value = value;
        }
    }

    public SetPinAction(Pin pin, String value) {
        this.pin = pin;
        this.value = value;
    }

    public String makeHardwareBody() {
        return Pin.makeHardwareBody(pin.pwmMode, pin.pinType, pin.pin, value);
    }

    @Override
    public boolean isValid() {
        return pin != null && pin.pinType != null && pin.pin > -1 && value != null;
    }
}
