package com.laputa.server.application.handlers.main.logic.sharing;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.application.handlers.sharing.auth.AppShareStateHolder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.REFRESH_SHARE_TOKEN;
import static com.laputa.utils.AppStateHolderUtil.getShareState;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;
import static com.laputa.utils.LaputaByteBufUtil.notAllowed;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class RefreshShareTokenLogic {

    private final TokenManager tokenManager;
    private final SessionDao sessionDao;

    public RefreshShareTokenLogic(TokenManager tokenManager, SessionDao sessionDao) {
        this.tokenManager = tokenManager;
        this.sessionDao = sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        String dashBoardIdString = message.body;

        int dashId;
        try {
            dashId = Integer.parseInt(dashBoardIdString);
        } catch (NumberFormatException ex) {
            throw new NotAllowedException("Dash board id not valid. Id : " + dashBoardIdString);
        }

        final User user = state.user;
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        String token = tokenManager.refreshSharedToken(user, dash);

        Session session = sessionDao.userSession.get(state.userKey);
        for (Channel appChannel : session.appChannels) {
            AppShareStateHolder localState = getShareState(appChannel);
            if (localState != null && localState.dashId == dashId) {
                ChannelFuture cf = appChannel.writeAndFlush(notAllowed(message.id));
                cf.addListener(channelFuture -> appChannel.close());
            }
        }

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(REFRESH_SHARE_TOKEN, message.id, token), ctx.voidPromise());
        }
    }
}
