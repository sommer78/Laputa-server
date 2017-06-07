package com.laputa.server.application.handlers.main.logic.dashboard.tags;

import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.JsonParser;
import com.laputa.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Command.GET_TAGS;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class GetTagsLogic {

    private static final Logger log = LogManager.getLogger(GetTagsLogic.class);

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        int dashId = ParseUtil.parseInt(message.body) ;

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        String response = JsonParser.toJson(dash.tags);
        if (response == null) {
            response = "[]";
        }

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(GET_TAGS, message.id, response), ctx.voidPromise());
        }
    }

}
