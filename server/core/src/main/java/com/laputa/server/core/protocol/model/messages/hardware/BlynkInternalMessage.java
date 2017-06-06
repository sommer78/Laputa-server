package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.BLYNK_INTERNAL;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class BlynkInternalMessage extends StringMessage {

    public BlynkInternalMessage(int messageId, String body) {
        super(messageId, BLYNK_INTERNAL, body.length(), body);
    }

    @Override
    public String toString() {
        return "BlynkInternalMessage{" + super.toString() + "}";
    }
}
