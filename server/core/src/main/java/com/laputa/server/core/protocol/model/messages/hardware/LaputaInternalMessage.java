package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.LAPUTA_INTERNAL;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class LaputaInternalMessage extends StringMessage {

    public LaputaInternalMessage(int messageId, String body) {
        super(messageId, LAPUTA_INTERNAL, body.length(), body);
    }

    @Override
    public String toString() {
        return "LaputaInternalMessage{" + super.toString() + "}";
    }
}
