package com.laputa.server.core.protocol.model.messages;

import com.laputa.server.core.protocol.enums.Command;
import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class ResponseMessage extends MessageBase {

    public ResponseMessage(int messageId, int responseCode) {
        super(messageId, Command.RESPONSE, responseCode);
    }

    @Override
    public byte[] getBytes() {
        return null;
    }

    @Override
    public String toString() {
        return "ResponseMessage{id=" + id +
                ", command=" + Command.getNameByValue(command) +
                ", responseCode=" + Response.getNameByValue(length) + "}";
    }
}
