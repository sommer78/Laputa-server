package com.laputa.server.admin.http.response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 10.12.15.
 */
public final class IpNameResponse {

    public final String ip;

    public final String name;

    public IpNameResponse(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }


}
