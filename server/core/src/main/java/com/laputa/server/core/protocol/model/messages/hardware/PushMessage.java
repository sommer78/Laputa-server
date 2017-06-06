package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class PushMessage extends StringMessage {

    public PushMessage(int messageId, String body) {
        super(messageId, PUSH_NOTIFICATION, body.length(), body);
    }

    @Override
    public String toString() {
        return "PushMessage{" + super.toString() + "}";
    }
}
