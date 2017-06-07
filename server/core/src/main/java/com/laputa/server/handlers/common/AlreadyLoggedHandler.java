package com.laputa.server.handlers.common;

import com.laputa.server.core.protocol.enums.Response;
import com.laputa.server.core.protocol.handlers.DefaultExceptionHandler;
import com.laputa.server.core.protocol.model.messages.appllication.LoginMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.laputa.utils.LaputaByteBufUtil.makeResponse;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
@ChannelHandler.Sharable
public class AlreadyLoggedHandler extends SimpleChannelInboundHandler<LoginMessage> implements DefaultExceptionHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage msg) throws Exception {
        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeResponse(msg.id, Response.USER_ALREADY_REGISTERED), ctx.voidPromise());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleGeneralException(ctx, cause);
    }

}
