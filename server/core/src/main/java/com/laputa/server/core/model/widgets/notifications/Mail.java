package com.laputa.server.core.model.widgets.notifications;

import com.laputa.server.core.model.widgets.NoPinWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class Mail extends NoPinWidget {

    public String to;

    @Override
    public int getPrice() {
        return 100;
    }

}
