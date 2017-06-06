package com.laputa.integration.model.http;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 24.12.15.
 */
public class ResponseUserEntity {

    public final String pass;

    public ResponseUserEntity(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "{\"pass\":\""+ pass +"\"}";
    }

}
