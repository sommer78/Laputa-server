package com.laputa.server.core.dao;

import com.laputa.server.core.model.auth.User;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 16.11.16.
 */
public final class TokenValue {

    public final User user;

    public final int dashId;

    public final int deviceId;

    public TokenValue(User user, int dashId, int deviceId) {
        this.user = user;
        this.dashId = dashId;
        this.deviceId = deviceId;
    }
}
