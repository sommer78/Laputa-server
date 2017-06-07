package com.laputa.server.application.handlers.main.logic.sharing;

import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.GET_SHARE_TOKEN;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class GetShareTokenLogic {

    private static final int PRIVATE_TOKEN_PRICE = 1000;

    private final TokenManager tokenManager;

    public GetShareTokenLogic(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String dashBoardIdString = message.body;

        int dashId;
        try {
            dashId = Integer.parseInt(dashBoardIdString);
        } catch (NumberFormatException ex) {
            throw new NotAllowedException("Dash board id not valid. Id : " + dashBoardIdString);
        }

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        String token = dash.sharedToken;

        //if token not exists. generate new one
        if (token == null) {
            token = tokenManager.refreshSharedToken(user, dash);
            user.subtractEnergy(PRIVATE_TOKEN_PRICE);
        }

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(GET_SHARE_TOKEN, message.id, token), ctx.voidPromise());
        }
    }
}
