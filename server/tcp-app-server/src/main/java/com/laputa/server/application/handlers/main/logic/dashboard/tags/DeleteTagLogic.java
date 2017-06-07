package com.laputa.server.application.handlers.main.logic.dashboard.tags;

import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Tag;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ArrayUtil;
import com.laputa.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.LaputaByteBufUtil.ok;
import static com.laputa.utils.StringUtils.split2;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class DeleteTagLogic {

    private static final Logger log = LogManager.getLogger(DeleteTagLogic.class);

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            throw new IllegalCommandException("Wrong income message format.");
        }

        int dashId = ParseUtil.parseInt(split[0]) ;
        int tagId = ParseUtil.parseInt(split[1]);

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        log.debug("Deleting tag with id {}.", tagId);

        int existingTagIndex = dash.getTagIndexById(tagId);

        dash.tags = ArrayUtil.remove(dash.tags, existingTagIndex, Tag.class);
        dash.updatedAt = System.currentTimeMillis();
        user.lastModifiedTs = dash.updatedAt;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
