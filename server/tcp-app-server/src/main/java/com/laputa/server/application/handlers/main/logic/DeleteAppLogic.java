package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.App;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ArrayUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.LaputaByteBufUtil.ok;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class DeleteAppLogic {

    private static final Logger log = LogManager.getLogger(DeleteAppLogic.class);

    public static void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        String id = message.body;

        final User user = state.user;

        int existingAppIndex = user.profile.getAppIndexById(id);

        if (existingAppIndex == -1) {
            throw new NotAllowedException("App with passed is not exists.");
        }

        for (int projectId : user.profile.apps[existingAppIndex].projectIds) {
            try {
                int index = user.profile.getDashIndexOrThrow(projectId);
                user.profile.dashBoards = ArrayUtil.remove(user.profile.dashBoards, index, DashBoard.class);
            } catch (Exception e) {
                log.debug("Can't delete dash {} from app {}.", projectId, id);
            }
        }

        user.profile.apps = ArrayUtil.remove(user.profile.apps, existingAppIndex, App.class);
        user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
