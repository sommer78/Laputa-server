package com.laputa.server.core.protocol.model.messages.appllication.sharing;

import com.laputa.server.core.model.widgets.AppSyncWidget;
import com.laputa.server.core.protocol.enums.Command;
import com.laputa.server.core.protocol.model.messages.StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 27.10.15.
 */
public class AppSyncMessage extends StringMessage {

    //only for tests
    public AppSyncMessage(String body) {
        super(AppSyncWidget.SYNC_DEFAULT_MESSAGE_ID, Command.APP_SYNC, body.length(), body);
    }

    public AppSyncMessage(int messageId, String body) {
        super(messageId, Command.APP_SYNC, body.length(), body);
    }

    @Override
    public String toString() {
        return "AppSyncMessage{" + super.toString() + "}";
    }
}
