package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class DeActivateDashboardMessage extends StringMessage {

    public DeActivateDashboardMessage(int messageId, String body) {
        super(messageId, DEACTIVATE_DASHBOARD, body.length(), body);
    }

    @Override
    public String toString() {
        return "DeActivateDashboardMessage{" + super.toString() + "}";
    }
}
