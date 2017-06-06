package com.laputa.integration.model;

import com.laputa.server.core.protocol.model.messages.MessageBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 1/31/2015.
 */
public class SimpleClientHandler extends SimpleChannelInboundHandler<MessageBase> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageBase msg) throws Exception {
        //System.out.println(msg);
    }

}