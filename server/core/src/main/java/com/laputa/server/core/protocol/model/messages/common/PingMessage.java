package com.laputa.server.core.protocol.model.messages.common;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class PingMessage extends StringMessage {

    public PingMessage(int messageId) {
        super(messageId, PING, 0, "");
    }

    @Override
    public String toString() {
        return "PingMessage{" + super.toString() + "}";
    }
}
