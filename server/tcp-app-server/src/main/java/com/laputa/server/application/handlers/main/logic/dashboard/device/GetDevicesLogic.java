package com.laputa.server.application.handlers.main.logic.dashboard.device;

import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.JsonParser;
import com.laputa.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Command.GET_DEVICES;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class GetDevicesLogic {

    private static final Logger log = LogManager.getLogger(GetDevicesLogic.class);

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        int dashId = ParseUtil.parseInt(message.body) ;

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        String response = JsonParser.toJson(dash.devices);
        if (response == null) {
            response = "[]";
        }

        log.debug("Returning devices : {}", response);

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(GET_DEVICES, message.id, response), ctx.voidPromise());
        }
    }

}
