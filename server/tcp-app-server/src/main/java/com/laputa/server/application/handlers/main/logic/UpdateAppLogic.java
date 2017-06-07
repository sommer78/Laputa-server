package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.model.auth.App;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.LaputaByteBufUtil.ok;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class UpdateAppLogic {

    private static final Logger log = LogManager.getLogger(UpdateAppLogic.class);

    private final int MAX_WIDGET_SIZE;

    public UpdateAppLogic(int maxWidgetSize) {
        this.MAX_WIDGET_SIZE = maxWidgetSize;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        String appString = message.body;

        if (appString == null || appString.isEmpty()) {
            throw new IllegalCommandException("Income app message is empty.");
        }

        if (appString.length() > MAX_WIDGET_SIZE) {
            throw new NotAllowedException("App is larger then limit.");
        }

        App newApp = JsonParser.parseApp(appString);

        newApp.validate();

        log.debug("Creating new app {}.", newApp);

        final User user = state.user;

        App existingApp = user.profile.getAppById(newApp.id);

        if (existingApp == null) {
            throw new NotAllowedException("App with passed is not exists.");
        }

        existingApp.update(newApp);

        user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
