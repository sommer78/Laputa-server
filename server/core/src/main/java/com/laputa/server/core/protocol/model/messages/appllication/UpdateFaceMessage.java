package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.UPDATE_FACE;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class UpdateFaceMessage extends StringMessage {

    public UpdateFaceMessage(int messageId, String body) {
        super(messageId, UPDATE_FACE, body.length(), body);
    }

    @Override
    public String toString() {
        return "UpdateFaceMessage{" + super.toString() + "}";
    }
}
