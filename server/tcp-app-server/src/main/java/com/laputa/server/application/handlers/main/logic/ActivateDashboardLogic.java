package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.model.widgets.AppSyncWidget;
import com.laputa.server.core.model.widgets.Widget;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ParseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Command.HARDWARE;
import static com.laputa.utils.AppStateHolderUtil.getAppState;
import static com.laputa.utils.LaputaByteBufUtil.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class ActivateDashboardLogic {

    public static final int PIN_MODE_MSG_ID = 1;

    private static final Logger log = LogManager.getLogger(ActivateDashboardLogic.class);

    private final SessionDao sessionDao;

    public ActivateDashboardLogic(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        final User user = state.user;
        String dashBoardIdString = message.body;

        int dashId = ParseUtil.parseInt(dashBoardIdString);

        log.debug("Activating dash {} for user {}", dashBoardIdString, user.email);
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);
        dash.activate();
        user.lastModifiedTs = System.currentTimeMillis();

        Session session = sessionDao.userSession.get(state.userKey);

        if (session.isHardwareConnected(dashId)) {
            for (Device device : dash.devices) {
                if (session.sendMessageToHardware(dashId, HARDWARE, PIN_MODE_MSG_ID, dash.buildPMMessage(device.id), device.id)) {
                    log.debug("No device in session.");
                    if (ctx.channel().isWritable()) {
                        ctx.writeAndFlush(deviceNotInNetwork(PIN_MODE_MSG_ID), ctx.voidPromise());
                    }
                }
            }

            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            log.debug("No device in session.");
            ctx.writeAndFlush(deviceNotInNetwork(message.id), ctx.voidPromise());
        }

        for (Channel appChannel : session.appChannels) {
            if (appChannel != ctx.channel() && getAppState(appChannel) != null && appChannel.isWritable()) {
                appChannel.write(makeUTF8StringMessage(message.command, message.id, message.body));
            }

            //todo remove after migration to new "AppSync" method
            for (Widget widget : dash.widgets) {
                if (widget instanceof AppSyncWidget && appChannel.isWritable()) {
                    ((AppSyncWidget) widget).sendAppSync(appChannel, dashId);
                }
            }

            appChannel.flush();
        }
    }

}
