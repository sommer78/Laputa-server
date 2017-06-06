package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.CREATE_TAG;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class CreateTag extends StringMessage {

    public CreateTag(int messageId, String body) {
        super(messageId, CREATE_TAG, body.length(), body);
    }

    @Override
    public String toString() {
        return "CreateTag{" + super.toString() + "}";
    }
}
