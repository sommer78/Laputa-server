package com.laputa.server.core.model.widgets.sensors;

import com.laputa.server.core.model.widgets.OnePinWidget;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class GPSTrigger extends OnePinWidget {

    public boolean triggerOnEnter;

    public float triggerLat;

    public float triggerLon;

    public int triggerRadius;

    @Override
    public String getModeType() {
        return "out";
    }

    @Override
    public void sendHardSync(ChannelHandlerContext ctx, int msgId, int deviceId) {
    }

    @Override
    public int getPrice() {
        return 500;
    }
}
