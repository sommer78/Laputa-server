package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.BinaryMessage;

import static com.laputa.server.core.protocol.enums.Command.GET_PROJECT_BY_TOKEN;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class GetProjectByTokenBinaryMessage extends BinaryMessage {

    public GetProjectByTokenBinaryMessage(int messageId, byte[] data) {
        super(messageId, GET_PROJECT_BY_TOKEN, data);
    }

    @Override
    public String toString() {
        return "GetProjectByTokenBinaryMessage{" + super.toString() + "}";
    }
}
