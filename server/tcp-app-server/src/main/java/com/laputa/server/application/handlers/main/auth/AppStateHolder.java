package com.laputa.server.application.handlers.main.auth;

import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.session.StateHolderBase;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 13.09.15.
 */
public class AppStateHolder extends StateHolderBase {

    public final OsType osType;
    public final String version;

    public AppStateHolder(User user, OsType osType, String version) {
        super(user);
        this.osType = osType;
        this.version = version;
    }

    @Override
    public boolean contains(String sharedToken) {
        return true;
    }
}
