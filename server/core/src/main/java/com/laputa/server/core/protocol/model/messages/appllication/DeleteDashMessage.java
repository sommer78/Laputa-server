package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class DeleteDashMessage extends StringMessage {

    public DeleteDashMessage(int messageId, String body) {
        super(messageId, DELETE_DASH, body.length(), body);
    }

    @Override
    public String toString() {
        return "DeleteDashMessage{" + super.toString() + "}";
    }
}
