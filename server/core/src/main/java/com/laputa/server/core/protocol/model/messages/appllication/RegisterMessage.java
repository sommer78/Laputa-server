package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class RegisterMessage extends StringMessage {

    public RegisterMessage(int messageId, String body) {
        super(messageId, REGISTER, body.length(), body);
    }

    @Override
    public String toString() {
        return "RegisterMessage{" + super.toString() + "}";
    }
}
