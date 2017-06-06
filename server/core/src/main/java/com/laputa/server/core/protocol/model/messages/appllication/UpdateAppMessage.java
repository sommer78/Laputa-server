package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.UPDATE_APP;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class UpdateAppMessage extends StringMessage {

    public UpdateAppMessage(int messageId, String body) {
        super(messageId, UPDATE_APP, body.length(), body);
    }

    @Override
    public String toString() {
        return "UpdateAppMessage{" + super.toString() + "}";
    }
}
