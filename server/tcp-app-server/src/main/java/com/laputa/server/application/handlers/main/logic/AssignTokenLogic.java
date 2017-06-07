package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.Holder;
import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.db.DBManager;
import com.laputa.server.db.model.FlashedToken;
import com.laputa.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.LaputaByteBufUtil.notAllowed;
import static com.laputa.utils.LaputaByteBufUtil.ok;
import static com.laputa.utils.StringUtils.split2;

/**
 * Assigns static generated token to assigned device.
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class AssignTokenLogic {

    private static final Logger log = LogManager.getLogger(AssignTokenLogic.class);

    private final TokenManager tokenManager;
    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;

    public AssignTokenLogic(Holder holder) {
        this.tokenManager = holder.tokenManager;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.dbManager = holder.dbManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split2(message.body);

        int dashId = ParseUtil.parseInt(split[0]);
        String token = split[1];
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        blockingIOProcessor.executeDB(() -> {
            FlashedToken dbFlashedToken = dbManager.selectFlashedToken(token);

            if (dbFlashedToken == null) {
                log.error("{} token not exists for app {}.", token, user.appName);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            if (dbFlashedToken.isActivated) {
                log.error("{} token is already activated for app {}.", token, user.appName);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            Device device = dash.getDeviceById(dbFlashedToken.deviceId);

            if (device == null) {
                log.error("Device with {} id not exists in dashboards.", dbFlashedToken.deviceId);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            if (!dbManager.activateFlashedToken(token)) {
                log.error("Error activated flashed token {}", token);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            tokenManager.assignToken(user, dashId, device.id, token);

            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        });
    }

}
