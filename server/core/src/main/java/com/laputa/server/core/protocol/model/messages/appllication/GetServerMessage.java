package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.GET_SERVER;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class GetServerMessage extends StringMessage {

    public GetServerMessage(int messageId, String body) {
        super(messageId, GET_SERVER, body.length(), body);
    }

    @Override
    public String toString() {
        return "GetServerMessage{" + super.toString() + "}";
    }
}
