package com.laputa.server.admin.http.response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 10.12.15.
 */
public class RequestPerSecondResponse {

    public final String name;

    public final int appRate;

    public final int hardRate;

    public RequestPerSecondResponse(String name, int appRate, int hardRate) {
        this.name = name;
        this.appRate = appRate;
        this.hardRate = hardRate;
    }


}
