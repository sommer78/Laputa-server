package com.laputa.server.application.handlers.sharing.auth;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.application.handlers.main.auth.OsType;
import com.laputa.server.core.model.auth.User;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 13.09.15.
 */
public final class AppShareStateHolder extends AppStateHolder {

    public final String token;
    public final int dashId;

    public AppShareStateHolder(User user, OsType osType, String version, String token, int dashId) {
        super(user, osType, version);
        this.token = token;
        this.dashId = dashId;
    }

    @Override
    public boolean contains(String sharedToken) {
        return sharedToken != null && token.equals(sharedToken);
    }

}
