package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.GET_ENERGY;
import static com.laputa.utils.LaputaByteBufUtil.makeASCIIStringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 14.03.16.
 */
public class GetEnergyLogic {

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeASCIIStringMessage(GET_ENERGY, message.id, "" + user.energy), ctx.voidPromise());
        }
    }

}
