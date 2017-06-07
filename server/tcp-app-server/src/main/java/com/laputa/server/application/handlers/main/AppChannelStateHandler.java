package com.laputa.server.application.handlers.main;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.protocol.enums.Command;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.AppStateHolderUtil.getAppState;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/20/2015.
 *
 * Removes channel from session in case it became inactive (closed from client side).
 */
@ChannelHandler.Sharable
public class AppChannelStateHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(AppChannelStateHandler.class);

    private final SessionDao sessionDao;

    public AppChannelStateHandler(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        AppStateHolder state = getAppState(ctx.channel());
        if (state != null) {
            Session session = sessionDao.userSession.get(state.userKey);
            if (session != null) {
                session.removeAppChannel(ctx.channel());
                log.trace("Application channel disconnect. {}", ctx.channel());

                for (DashBoard dashBoard : state.user.profile.dashBoards) {
                    if (dashBoard.isAppConnectedOn && dashBoard.isActive) {
                        log.trace("{}-{}. Sendeind App Disconnected event to hardware.", state.user.email, state.user.appName);
                        session.sendMessageToHardware(dashBoard.id, Command.LAPUTA_INTERNAL, 7777, "adis");
                    }
                }
            }
        }
    }

}
