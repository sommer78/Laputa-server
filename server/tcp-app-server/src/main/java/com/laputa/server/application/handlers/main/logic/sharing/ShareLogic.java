package com.laputa.server.application.handlers.main.logic.sharing;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.protocol.exceptions.IllegalCommandBodyException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.utils.AppStateHolderUtil.getAppState;
import static com.laputa.utils.LaputaByteBufUtil.ok;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class ShareLogic {

    private final SessionDao sessionDao;

    public ShareLogic(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        String[] splitted = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        int dashId = ParseUtil.parseInt(splitted[0]);
        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        switch (splitted[1]) {
            case "on" :
                dash.isShared = true;
                break;
            case "off" :
                dash.isShared = false;
                break;
            default:
                throw new IllegalCommandBodyException("Wrong share command body");
        }

        Session session = sessionDao.userSession.get(state.userKey);
        for (Channel appChannel : session.appChannels) {
            if (appChannel != ctx.channel() && getAppState(appChannel) != null && appChannel.isWritable()) {
                appChannel.writeAndFlush(message, appChannel.voidPromise());
            }
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}