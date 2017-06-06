package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class GetEnergy extends StringMessage {

    public GetEnergy(int messageId, String body) {
        super(messageId, GET_ENERGY, body.length(), body);
    }

    @Override
    public String toString() {
        return "GetEnergy{" + super.toString() + "}";
    }
}
