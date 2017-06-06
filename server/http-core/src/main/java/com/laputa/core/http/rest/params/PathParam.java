package com.laputa.core.http.rest.params;

import com.laputa.core.http.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 09.12.15.
 */
public class PathParam extends Param {

    public PathParam(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
        return convertTo(uriDecoder.pathData.get(name));
    }

}
