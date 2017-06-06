package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class UpdateWidget extends StringMessage {

    public UpdateWidget(int messageId, String body) {
        super(messageId, UPDATE_WIDGET, body.length(), body);
    }

    @Override
    public String toString() {
        return "UpdateWidget{" + super.toString() + "}";
    }
}
