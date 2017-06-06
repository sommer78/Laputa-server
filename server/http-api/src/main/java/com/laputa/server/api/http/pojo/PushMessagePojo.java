package com.laputa.server.api.http.pojo;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 28.12.15.
 */
public class PushMessagePojo {

    public String body;

    public PushMessagePojo() {
    }

    public PushMessagePojo(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "PushMessagePojo{" +
                "body='" + body + '\'' +
                '}';
    }
}
