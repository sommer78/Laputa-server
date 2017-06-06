package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.DELETE_DEVICE;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class DeleteDevice extends StringMessage {

    public DeleteDevice(int messageId, String body) {
        super(messageId, DELETE_DEVICE, body.length(), body);
    }

    @Override
    public String toString() {
        return "DeleteDevice{" + super.toString() + "}";
    }
}
