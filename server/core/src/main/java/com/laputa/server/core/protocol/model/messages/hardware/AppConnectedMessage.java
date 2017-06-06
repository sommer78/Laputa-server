package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.APP_CONNECTED;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class AppConnectedMessage extends StringMessage {

    public AppConnectedMessage(int messageId) {
        super(messageId, APP_CONNECTED, 0, "");
    }

    @Override
    public String toString() {
        return "ConnectRedirectMessage{" + super.toString() + "}";
    }
}
