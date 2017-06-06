package com.laputa.server.core.protocol.model.messages.appllication.sharing;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class RefreshShareTokenMessage extends StringMessage {

    public RefreshShareTokenMessage(int messageId, String body) {
        super(messageId, REFRESH_SHARE_TOKEN, body.length(), body);
    }

    @Override
    public String toString() {
        return "RefreshShareTokenMessage{" + super.toString() + "}";
    }
}
