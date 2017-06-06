package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.CREATE_DEVICE;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class CreateDevice extends StringMessage {

    public CreateDevice(int messageId, String body) {
        super(messageId, CREATE_DEVICE, body.length(), body);
    }

    @Override
    public String toString() {
        return "CreateDevice{" + super.toString() + "}";
    }
}
