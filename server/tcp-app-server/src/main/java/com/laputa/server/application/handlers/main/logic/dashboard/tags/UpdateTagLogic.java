package com.laputa.server.application.handlers.main.logic.dashboard.tags;

import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Tag;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.JsonParser;
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
public class UpdateTagLogic {

    private static final Logger log = LogManager.getLogger(UpdateTagLogic.class);

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            throw new IllegalCommandException("Wrong income message format.");
        }

        int dashId = ParseUtil.parseInt(split[0]) ;
        String tagString = split[1];

        if (tagString == null || tagString.isEmpty()) {
            throw new IllegalCommandException("Income tag message is empty.");
        }

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        Tag newTag = JsonParser.parseTag(tagString);

        log.debug("Updating new tag {}.", tagString);

        if (newTag.isNotValid()) {
            throw new IllegalCommandException("Income tag name is not valid.");
        }

        Tag existingTag = dash.getTagById(newTag.id);

        if (existingTag == null) {
            throw new IllegalCommandException("Attempt to update tag with non existing id.");
        }

        existingTag.update(newTag);
        dash.updatedAt = System.currentTimeMillis();
        user.lastModifiedTs = dash.updatedAt;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
