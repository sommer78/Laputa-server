package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.Pin;
import com.laputa.server.core.model.PinStorageKey;
import com.laputa.server.core.model.widgets.AppSyncWidget;
import com.laputa.server.core.model.widgets.Widget;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ParseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import static com.laputa.server.core.model.widgets.AppSyncWidget.ANY_TARGET;
import static com.laputa.server.core.model.widgets.AppSyncWidget.SYNC_DEFAULT_MESSAGE_ID;
import static com.laputa.server.core.protocol.enums.Command.APP_SYNC;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;
import static com.laputa.utils.LaputaByteBufUtil.ok;
import static com.laputa.utils.StringUtils.prependDashIdAndDeviceId;
import static com.laputa.utils.StringUtils.split2Device;

/**
 * Request state sync info for widgets.
 * Supports sync for all widgets and sync for specific target
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class AppSyncLogic {

    public static void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        String[] dashIdAndTargetIdString = split2Device(message.body);
        int dashId = ParseUtil.parseInt(dashIdAndTargetIdString[0]);
        int targetId = AppSyncWidget.ANY_TARGET;

        if (dashIdAndTargetIdString.length == 2) {
            targetId = ParseUtil.parseInt(dashIdAndTargetIdString[1]);
        }

        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        sendSyncAndOk(ctx, dash, targetId, message.id);
    }

    public static void sendSyncAndOk(ChannelHandlerContext ctx, DashBoard dash, int targetId, int msgId) {
        ctx.write(ok(msgId), ctx.voidPromise());

        final Channel appChannel = ctx.channel();
        for (Widget widget : dash.widgets) {
            if (widget instanceof AppSyncWidget && appChannel.isWritable()) {
                ((AppSyncWidget) widget).sendAppSync(appChannel, dash.id, targetId);
            }
        }

        for (Map.Entry<PinStorageKey, String> entry : dash.pinsStorage.entrySet()) {
            PinStorageKey key = entry.getKey();
            if ((targetId == ANY_TARGET || targetId == key.deviceId) && appChannel.isWritable()) {
                String body = prependDashIdAndDeviceId(dash.id, key.deviceId, Pin.makeHardwareBody(key.pinType, key.pin, entry.getValue()));
                ctx.write(makeUTF8StringMessage(APP_SYNC, SYNC_DEFAULT_MESSAGE_ID, body), ctx.voidPromise());
            }
        }

        ctx.flush();
    }

}
