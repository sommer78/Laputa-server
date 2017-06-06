package com.laputa.server.api.http.pojo;

/**
 * The Laputa project
 * Created by Andrew Zakordonets
 * Date : 12/05/2015.
 */
public class TokenUser {

    public final String email;
    public final String appName;
    public final long createdAt;

    public TokenUser(String email, String appName) {
        this.email = email;
        this.appName = appName;
        this.createdAt = System.currentTimeMillis();
    }

}
