package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class ActivateDashboardMessage extends StringMessage {

    public ActivateDashboardMessage(int messageId, String body) {
        super(messageId, ACTIVATE_DASHBOARD, body.length(), body);
    }

    @Override
    public String toString() {
        return "ActivateDashboardMessage{" + super.toString() + "}";
    }
}
