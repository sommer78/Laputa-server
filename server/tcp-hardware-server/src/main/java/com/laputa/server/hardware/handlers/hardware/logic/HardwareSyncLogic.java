package com.laputa.server.hardware.handlers.hardware.logic;

import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.Pin;
import com.laputa.server.core.model.PinStorageKey;
import com.laputa.server.core.model.enums.PinType;
import com.laputa.server.core.model.widgets.HardwareSyncWidget;
import com.laputa.server.core.model.widgets.Widget;
import com.laputa.server.core.model.widgets.others.rtc.RTC;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.PinUtil;
import com.laputa.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import static com.laputa.server.core.protocol.enums.Command.HARDWARE;
import static com.laputa.utils.LaputaByteBufUtil.illegalCommand;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class HardwareSyncLogic {

    public static void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        final int dashId = state.dashId;
        final int deviceId = state.deviceId;
        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        if (message.length == 0) {
            syncAll(ctx, message.id, dash, deviceId);
        } else {
            syncSpecificPins(ctx, message.body, message.id, dash, deviceId);
        }
    }

    private static void syncAll(ChannelHandlerContext ctx, int msgId, DashBoard dash, int deviceId) {
        //return all widgets state
        for (Widget widget : dash.widgets) {
            //one exclusion, no need to sync RTC
            if (widget instanceof HardwareSyncWidget && !(widget instanceof RTC) && ctx.channel().isWritable()) {
                ((HardwareSyncWidget) widget).sendHardSync(ctx, msgId, deviceId);
            }
        }
        //return all static server holders
        for (Map.Entry<PinStorageKey, String> entry : dash.pinsStorage.entrySet()) {
            PinStorageKey key = entry.getKey();
            if (deviceId == key.deviceId && ctx.channel().isWritable()) {
                String body = Pin.makeHardwareBody(key.pinType, key.pin, entry.getValue());
                ctx.write(makeUTF8StringMessage(HARDWARE, msgId, body), ctx.voidPromise());
            }
        }

        ctx.flush();
    }

    //message format is "vr 22 33"
    //return specific widget state
    private static void syncSpecificPins(ChannelHandlerContext ctx, String messageBody, int msgId, DashBoard dash, int deviceId) {
        String[] bodyParts = messageBody.split(StringUtils.BODY_SEPARATOR_STRING);

        if (bodyParts.length < 2 || bodyParts[0].isEmpty()) {
            ctx.writeAndFlush(illegalCommand(msgId), ctx.voidPromise());
            return;
        }

        PinType pinType = PinType.getPinType(bodyParts[0].charAt(0));

        if (PinUtil.isReadOperation(bodyParts[0])) {
            for (int i = 1; i < bodyParts.length; i++) {
                byte pin = ParseUtil.parseByte(bodyParts[i]);
                Widget widget = dash.findWidgetByPin(deviceId, pin, pinType);
                if (ctx.channel().isWritable()) {
                    if (widget == null) {
                        String value = dash.pinsStorage.get(new PinStorageKey(deviceId, pinType, pin));
                        if (value != null) {
                            String body = Pin.makeHardwareBody(pinType, pin, value);
                            ctx.write(makeUTF8StringMessage(HARDWARE, msgId, body), ctx.voidPromise());
                        }
                    } else if (widget instanceof HardwareSyncWidget) {
                        ((HardwareSyncWidget) widget).sendHardSync(ctx, msgId, deviceId);
                    }
                }
            }
            ctx.flush();
        }
    }

}
