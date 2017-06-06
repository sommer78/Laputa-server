package com.laputa.server.handlers.common;

import com.laputa.server.core.protocol.handlers.DefaultExceptionHandler;
import com.laputa.server.core.protocol.model.messages.MessageBase;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
@ChannelHandler.Sharable
public class UserNotLoggedHandler extends SimpleChannelInboundHandler<MessageBase> implements DefaultExceptionHandler {

    private static final Logger log = LogManager.getLogger(Logger.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase msg) throws Exception {
        log.warn("User not logged. {}. Closing.", ctx.channel().remoteAddress());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }

}
