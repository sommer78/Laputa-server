package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.Holder;
import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.dao.UserDao;
import com.laputa.server.core.model.AppName;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.db.DBManager;
import com.laputa.server.db.model.FlashedToken;
import com.laputa.utils.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Command.GET_PROJECT_BY_TOKEN;
import static com.laputa.utils.BlynkByteBufUtil.makeBinaryMessage;
import static com.laputa.utils.BlynkByteBufUtil.notAllowed;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class GetProjectByTokenLogic {

    private static final Logger log = LogManager.getLogger(GetProjectByTokenLogic.class);

    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;
    private final UserDao userDao;

    public GetProjectByTokenLogic(Holder holder) {
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
        this.userDao = holder.userDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String token = message.body;

        blockingIOProcessor.executeDB(() -> {
            FlashedToken dbFlashedToken = dbManager.selectFlashedToken(token);

            if (dbFlashedToken == null) {
                log.error("{} token not exists for app {}.", token, user.appName);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            User publishUser = userDao.getByName(dbFlashedToken.email, AppName.BLYNK);

            DashBoard dash = publishUser.profile.getDashById(dbFlashedToken.dashId);

            if (dash == null) {
                log.error("Dash with {} id not exists in dashboards.", dbFlashedToken.dashId);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            write(ctx, JsonParser.gzipDashRestrictive(dash), message.id);
        });
    }

    public static void write(ChannelHandlerContext ctx, byte[] data, int msgId) {
        if (ctx.channel().isWritable()) {
            ByteBuf outputMsg = makeBinaryMessage(GET_PROJECT_BY_TOKEN, msgId, data);
            ctx.writeAndFlush(outputMsg, ctx.voidPromise());
        }
    }
}
