package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.Holder;
import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.dao.UserDao;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.Profile;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.db.DBManager;
import com.laputa.server.db.model.FlashedToken;
import com.laputa.utils.ParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Command.LOAD_PROFILE_GZIPPED;
import static com.laputa.server.core.protocol.enums.Response.NO_DATA;
import static com.laputa.utils.LaputaByteBufUtil.*;
import static com.laputa.utils.JsonParser.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class LoadProfileGzippedLogic {

    private static final Logger log = LogManager.getLogger(LoadProfileGzippedLogic.class);

    private final UserDao userDao;
    private final DBManager dbManager;
    private final BlockingIOProcessor blockingIOProcessor;

    public LoadProfileGzippedLogic(Holder holder) {
        this.userDao = holder.userDao;
        this.dbManager = holder.dbManager;
        this.blockingIOProcessor = holder.blockingIOProcessor;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        //load all
        if (message.length == 0) {
            Profile profile = state.user.profile;
            write(ctx, gzipProfile(profile), message.id);
            return;
        }

        String[] parts = message.body.split(" |\0");
        if (parts.length == 1) {
            //load specific by id
            int dashId = ParseUtil.parseInt(message.body);
            DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);
            write(ctx, gzipDash(dash), message.id);
        } else {
            String token = parts[0];
            int dashId = ParseUtil.parseInt(parts[1]);
            String publishingEmail = parts[2];

            blockingIOProcessor.executeDB(() -> {
                try {
                    FlashedToken flashedToken = dbManager.selectFlashedToken(token);
                    if (flashedToken != null) {
                        User publishingUser = userDao.getByName(publishingEmail, state.userKey.appName);
                        DashBoard dash = publishingUser.profile.getDashByIdOrThrow(dashId);
                        write(ctx, gzipDashRestrictive(dash), message.id);
                    }
                } catch (Exception e) {
                    ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
                    log.error("Error getting publishing profile.", e.getMessage());
                }
            });
        }
    }

    public static void write(ChannelHandlerContext ctx, byte[] data, int msgId) {
        if (ctx.channel().isWritable()) {
            ByteBuf outputMsg;
            if (data == null) {
                outputMsg = makeResponse(msgId, NO_DATA);
            } else {
                outputMsg = makeBinaryMessage(LOAD_PROFILE_GZIPPED, msgId, data);
            }
            ctx.writeAndFlush(outputMsg, ctx.voidPromise());
        }
    }

}
