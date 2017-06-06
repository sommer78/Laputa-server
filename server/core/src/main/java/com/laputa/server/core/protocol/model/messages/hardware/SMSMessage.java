package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class SMSMessage extends StringMessage {

    public SMSMessage(int messageId, String body) {
        super(messageId, SMS, body.length(), body);
    }

    @Override
    public String toString() {
        return "SMSMessage{" + super.toString() + "}";
    }
}
