package com.laputa.server.core.dao;

import com.laputa.server.core.model.auth.User;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 17.11.16.
 */
public class SharedTokenValue {

    public final User user;

    public final int dashId;

    public SharedTokenValue(User user, int dashId) {
        this.user = user;
        this.dashId = dashId;
    }

}
