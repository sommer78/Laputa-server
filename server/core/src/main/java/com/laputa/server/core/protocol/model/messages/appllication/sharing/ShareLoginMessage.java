package com.laputa.server.core.protocol.model.messages.appllication.sharing;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class ShareLoginMessage extends StringMessage {

    public ShareLoginMessage(int messageId, String body) {
        super(messageId, SHARE_LOGIN, body.length(), body);
    }

    @Override
    public String toString() {
        return "ShareLoginMessage{" + super.toString() + "}";
    }
}
