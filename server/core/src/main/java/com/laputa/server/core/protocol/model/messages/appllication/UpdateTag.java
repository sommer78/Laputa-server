package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.UPDATE_TAG;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class UpdateTag extends StringMessage {

    public UpdateTag(int messageId, String body) {
        super(messageId, UPDATE_TAG, body.length(), body);
    }

    @Override
    public String toString() {
        return "UpdateDevice{" + super.toString() + "}";
    }
}
