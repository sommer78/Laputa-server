package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class HardwareSyncMessage extends StringMessage {

    public HardwareSyncMessage(int messageId, String body) {
        super(messageId, HARDWARE_SYNC, body.length(), body);
    }

    @Override
    public String toString() {
        return "HardwareSyncMessage{" + super.toString() + "}";
    }
}
