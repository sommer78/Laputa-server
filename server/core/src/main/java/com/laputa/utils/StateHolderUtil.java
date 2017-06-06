package com.laputa.utils;

import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.server.handlers.BaseSimpleChannelInboundHandler;
import io.netty.channel.Channel;

/**
 * Used instead of Netty's DefaultAttributeMap as it faster and
 * doesn't involves any synchronization at all.
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 13.09.15.
 */
public class StateHolderUtil {

    public static HardwareStateHolder getHardState(Channel channel) {
        final BaseSimpleChannelInboundHandler handler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        return handler == null ? null : (HardwareStateHolder) handler.getState();
    }

    public static boolean isSameDash(Channel channel, int dashId) {
        final BaseSimpleChannelInboundHandler handler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        return ((HardwareStateHolder) handler.getState()).dashId == dashId;
    }

    public static boolean isSameDashAndDeviceId(Channel channel, int dashId, int deviceId) {
        final BaseSimpleChannelInboundHandler handler = channel.pipeline().get(BaseSimpleChannelInboundHandler.class);
        if (handler == null) {
            return false;
        }
        final HardwareStateHolder hardwareStateHolder = (HardwareStateHolder) handler.getState();
        return hardwareStateHolder.dashId == dashId && hardwareStateHolder.deviceId == deviceId;
    }

}
