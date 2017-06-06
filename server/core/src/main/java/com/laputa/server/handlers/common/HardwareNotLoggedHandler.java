package com.laputa.server.handlers.common;

import com.laputa.server.core.protocol.handlers.DefaultExceptionHandler;
import com.laputa.server.core.protocol.model.messages.MessageBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class HardwareNotLoggedHandler extends SimpleChannelInboundHandler<MessageBase> implements DefaultExceptionHandler {

    private static final Logger log = LogManager.getLogger(Logger.class);
    private long lastLoggedTs;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase msg) throws Exception {
        final long now = System.currentTimeMillis();
        if (now - lastLoggedTs > 1000) {
            log.warn("Hardware not logged. {}. Closing.", ctx.channel().remoteAddress());
            this.lastLoggedTs = System.currentTimeMillis();
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }

}
