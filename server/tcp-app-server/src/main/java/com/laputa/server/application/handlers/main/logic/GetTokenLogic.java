package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.Holder;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.GET_TOKEN;
import static com.laputa.utils.LaputaByteBufUtil.makeASCIIStringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class GetTokenLogic {

    private final TokenManager tokenManager;

    public GetTokenLogic(Holder holder) {
        this.tokenManager = holder.tokenManager;
    }

    //todo this old and outdated handle just for back compatibility
    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String dashBoardIdString = message.body;

        int dashId = ParseUtil.parseInt(dashBoardIdString);
        int deviceId = 0;

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        Device device = dash.getDeviceById(deviceId);
        String token = device == null ? null : device.token;

        //if token not exists. generate new one
        if (token == null) {
            //todo back compatibility code. remove in future
            dash.devices = new Device[] {
                    new Device(deviceId, "ESP8266", "ESP8266")
            };
            //

            token = TokenGeneratorUtil.generateNewToken();
            tokenManager.assignToken(user, dashId, deviceId, token);
        }

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeASCIIStringMessage(GET_TOKEN, message.id, token), ctx.voidPromise());
        }
    }
}
