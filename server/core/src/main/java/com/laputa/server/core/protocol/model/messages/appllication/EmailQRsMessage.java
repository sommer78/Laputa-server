package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.EMAIL_QR;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class EmailQRsMessage extends StringMessage {

    public EmailQRsMessage(int messageId, String body) {
        super(messageId, EMAIL_QR, body.length(), body);
    }

    @Override
    public String toString() {
        return "EmailQRsMessage{" + super.toString() + "}";
    }
}
