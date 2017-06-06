package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.UPDATE_DEVICE;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class UpdateDevice extends StringMessage {

    public UpdateDevice(int messageId, String body) {
        super(messageId, UPDATE_DEVICE, body.length(), body);
    }

    @Override
    public String toString() {
        return "UpdateDevice{" + super.toString() + "}";
    }
}
