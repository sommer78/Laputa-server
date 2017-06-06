package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.UPDATE_DASH;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class UpdateDashMessage extends StringMessage {

    public UpdateDashMessage(int messageId, String body) {
        super(messageId, UPDATE_DASH, body.length(), body);
    }

    @Override
    public String toString() {
        return "UpdateDashMessage{" + super.toString() + "}";
    }
}
