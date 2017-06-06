package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class GetGraphDataStringMessage extends StringMessage {

    public GetGraphDataStringMessage(int messageId, String body) {
        super(messageId, GET_GRAPH_DATA, body.length(), body);
    }

    @Override
    public String toString() {
        return "GetGraphDataStringMessage{" + super.toString() + "}";
    }
}
