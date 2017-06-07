package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.model.AppName;
import com.laputa.server.core.model.auth.App;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ArrayUtil;
import com.laputa.utils.JsonParser;
import com.laputa.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Command.CREATE_APP;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class CreateAppLogic {

    private static final Logger log = LogManager.getLogger(CreateAppLogic.class);

    private final int MAX_WIDGET_SIZE;

    public CreateAppLogic(int maxWidgetSize) {
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

        newApp.id = AppName.LAPUTA_LOWERCASE + StringUtils.randomString(8);

        newApp.validate();

        log.debug("Creating new app {}.", newApp);

        final User user = state.user;

        if (user.profile.apps.length > 25) {
            throw new NotAllowedException("App with same id already exists.");
        }

        for (App app : user.profile.apps) {
            if (app.id.equals(newApp.id)) {
                throw new NotAllowedException("App with same id already exists.");
            }
        }

        user.profile.apps = ArrayUtil.add(user.profile.apps, newApp, App.class);
        user.lastModifiedTs = System.currentTimeMillis();

        ctx.writeAndFlush(makeUTF8StringMessage(CREATE_APP, message.id, JsonParser.toJson(newApp)), ctx.voidPromise());
    }

}
