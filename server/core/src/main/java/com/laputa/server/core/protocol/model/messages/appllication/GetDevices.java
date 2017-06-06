package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.GET_DEVICES;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class GetDevices extends StringMessage {

    public GetDevices(int messageId, String body) {
        super(messageId, GET_DEVICES, body.length(), body);
    }

    @Override
    public String toString() {
        return "GetDevices{" + super.toString() + "}";
    }
}
