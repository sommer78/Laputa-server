package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.UPDATE_PROJECT_SETTINGS;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class UpdateDashSettingsMessage extends StringMessage {

    public UpdateDashSettingsMessage(int messageId, String body) {
        super(messageId, UPDATE_PROJECT_SETTINGS, body.length(), body);
    }

    @Override
    public String toString() {
        return "UpdateProjectSetting{" + super.toString() + "}";
    }
}
