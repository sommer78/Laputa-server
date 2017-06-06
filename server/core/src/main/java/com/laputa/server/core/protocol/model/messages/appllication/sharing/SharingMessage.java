package com.laputa.server.core.protocol.model.messages.appllication.sharing;

import com.laputa.server.core.protocol.enums.Command;
import com.laputa.server.core.protocol.model.messages.StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 05.11.15.
 */
public class SharingMessage extends StringMessage {

    public SharingMessage(int messageId, String body) {
        super(messageId, Command.SHARING, body.length(), body);
    }

    @Override
    public String toString() {
        return "SharingMessage{" + super.toString() + "}";
    }
}
