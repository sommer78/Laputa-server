package com.laputa.server.core.model.widgets.sensors;

import com.laputa.server.core.model.widgets.OnePinWidget;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 06.09.16.
 */
public class Barometer extends OnePinWidget {

    private int frequency;

    @Override
    public String getModeType() {
        return "out";
    }

    @Override
    public void sendHardSync(ChannelHandlerContext ctx, int msgId, int deviceId) {
    }

    @Override
    public int getPrice() {
        return 300;
    }
}
