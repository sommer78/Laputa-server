package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.DELETE_APP;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class DeleteAppMessage extends StringMessage {

    public DeleteAppMessage(int messageId, String body) {
        super(messageId, DELETE_APP, body.length(), body);
    }

    @Override
    public String toString() {
        return "DeleteAppMessage{" + super.toString() + "}";
    }
}
