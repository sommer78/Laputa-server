package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.Holder;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.REFRESH_TOKEN;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class RefreshTokenLogic {

    private final TokenManager tokenManager;

    public RefreshTokenLogic(Holder holder) {
        this.tokenManager = holder.tokenManager;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = StringUtils.split2(message.body);

        int dashId = ParseUtil.parseInt(split[0]);
        int deviceId = 0;

        //new value for multi devices
        if (split.length == 2) {
            deviceId = ParseUtil.parseInt(split[1]);
        }

        user.profile.validateDashId(dashId);

        String token = tokenManager.refreshToken(user, dashId, deviceId);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(REFRESH_TOKEN, message.id, token), ctx.voidPromise());
        }
    }
}
