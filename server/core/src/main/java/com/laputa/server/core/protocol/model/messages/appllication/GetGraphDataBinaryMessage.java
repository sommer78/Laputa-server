package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.BinaryMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class GetGraphDataBinaryMessage extends BinaryMessage {

    public GetGraphDataBinaryMessage(int messageId, byte[] data) {
        super(messageId, GET_GRAPH_DATA_RESPONSE, data);
    }

    @Override
    public String toString() {
        return "GetGraphDataBinaryMessage{" + super.toString() + "}";
    }
}
