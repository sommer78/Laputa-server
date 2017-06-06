package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class SetWidgetPropertyMessage extends StringMessage {

    public SetWidgetPropertyMessage(int messageId, String body) {
        super(messageId, SET_WIDGET_PROPERTY, body.length(), body);
    }

    @Override
    public String toString() {
        return "SetWidgetPropertyMessage{" + super.toString() + "}";
    }
}
