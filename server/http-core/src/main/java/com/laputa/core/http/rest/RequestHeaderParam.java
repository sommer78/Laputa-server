package com.laputa.core.http.rest;

import com.laputa.core.http.rest.params.Param;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 09.12.15.
 */
public class RequestHeaderParam extends Param {

    public RequestHeaderParam(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        String header = uriDecoder.headers.get(name);
        if (header == null) {
            return null;
        }
        return header;
    }

}
