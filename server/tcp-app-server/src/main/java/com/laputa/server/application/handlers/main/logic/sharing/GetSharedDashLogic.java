package com.laputa.server.application.handlers.main.logic.sharing;

import com.laputa.server.core.dao.SharedTokenValue;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.exceptions.InvalidTokenException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.JsonParser;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.model.messages.MessageFactory.produce;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class GetSharedDashLogic {

    private final TokenManager tokenManager;

    public GetSharedDashLogic(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, StringMessage message) {
        String token = message.body;

        SharedTokenValue tokenValue = tokenManager.getUserBySharedToken(token);

        if (tokenValue == null) {
            throw new InvalidTokenException("Illegal sharing token. No user with those shared token.", message.id);
        }

        User userThatShared = tokenValue.user;

        DashBoard dashBoard = userThatShared.profile.getDashByIdOrThrow(tokenValue.dashId);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(produce(message.id, message.command, JsonParser.toJsonRestrictiveDashboard(dashBoard)), ctx.voidPromise());
        }
    }

}