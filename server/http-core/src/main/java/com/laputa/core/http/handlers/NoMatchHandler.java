package com.laputa.core.http.handlers;

import com.laputa.core.http.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 12.03.17.
 */
@ChannelHandler.Sharable
public class NoMatchHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(NoMatchHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                log.debug("Error resolving url. No path found. {} : {}", req.method().name(), req.uri());
                ctx.writeAndFlush(Response.notFound(), ctx.voidPromise());
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
