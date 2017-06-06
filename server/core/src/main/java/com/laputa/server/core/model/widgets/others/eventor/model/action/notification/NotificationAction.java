package com.laputa.server.core.model.widgets.others.eventor.model.action.notification;

import com.laputa.server.core.model.widgets.others.eventor.model.action.BaseAction;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 23.08.16.
 */
public abstract class NotificationAction extends BaseAction {

    public String message;

    @Override
    public boolean isValid() {
        return message != null && !message.isEmpty();
    }

}
