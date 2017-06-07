package com.laputa.server.handlers.common;

import io.netty.channel.ChannelHandlerContext;

import static com.laputa.utils.LaputaByteBufUtil.ok;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class PingLogic {

    public static void messageReceived(ChannelHandlerContext ctx, int messageId) {
        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(ok(messageId), ctx.voidPromise());
        }
    }

}
