package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class TwitMessage extends StringMessage {

    public TwitMessage(int messageId, String body) {
        super(messageId, TWEET, body.length(), body);
    }

    @Override
    public String toString() {
        return "TwitMessage{" + super.toString() + "}";
    }
}
