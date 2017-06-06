package com.laputa.server.notifications.push;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 26.06.15.
 */
public class GCMResponseMessage {

    public int success;

    public int failure;

    public long multicast_id;

    public GCMResult[] results;

}
