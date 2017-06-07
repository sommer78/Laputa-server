package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ParseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.AppStateHolderUtil.getAppState;
import static com.laputa.utils.LaputaByteBufUtil.ok;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class DeActivateDashboardLogic {

    private static final Logger log = LogManager.getLogger(ActivateDashboardLogic.class);

    private final SessionDao sessionDao;

    public DeActivateDashboardLogic(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        final User user = state.user;
        if (message.length > 0) {
            log.debug("DeActivating dash {} for user {}", message.body, user.email);
            int dashId = ParseUtil.parseInt(message.body);
            DashBoard dashBoard = user.profile.getDashByIdOrThrow(dashId);
            dashBoard.deactivate();
        } else {
            for (DashBoard dashBoard : user.profile.dashBoards) {
                dashBoard.deactivate();
            }
        }
        user.lastModifiedTs = System.currentTimeMillis();

        Session session = sessionDao.userSession.get(state.userKey);
        for (Channel appChannel : session.appChannels) {
            if (appChannel != ctx.channel() && getAppState(appChannel) != null && appChannel.isWritable()) {
                appChannel.writeAndFlush(message, appChannel.voidPromise());
            }
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
