package com.laputa.server.notifications.push.android;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 06.05.17.
 */
public class GCMData {

    private final String message;
    private final int dashId;

    public GCMData(String message, int dashId) {
        this.message = message;
        this.dashId = dashId;
    }

}
