package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class AddEnergy extends StringMessage {

    public AddEnergy(int messageId, String body) {
        super(messageId, ADD_ENERGY, body.length(), body);
    }

    @Override
    public String toString() {
        return "AddEnergy{" + super.toString() + "}";
    }
}
