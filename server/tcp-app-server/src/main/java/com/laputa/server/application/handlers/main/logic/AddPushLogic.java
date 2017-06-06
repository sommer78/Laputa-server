package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.widgets.notifications.Notification;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.utils.BlynkByteBufUtil.ok;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class AddPushLogic {

    public static void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        String[] data = StringUtils.split3(message.body);

        int dashId = ParseUtil.parseInt(data[0]);
        String uid = data[1];
        String token = data[2];

        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        Notification notification = dash.getWidgetByType(Notification.class);

        if (notification == null) {
            throw new NotAllowedException("No notification widget.");
        }

        switch (state.osType) {
            case ANDROID :
                notification.androidTokens.put(uid, token);
                break;
            case IOS :
                notification.iOSTokens.put(uid, token);
                break;
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }
}
