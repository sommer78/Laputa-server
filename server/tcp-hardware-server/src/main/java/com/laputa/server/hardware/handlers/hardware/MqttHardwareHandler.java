package com.laputa.server.hardware.handlers.hardware;

import com.laputa.server.Holder;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.server.core.session.StateHolderBase;
import com.laputa.server.core.stats.GlobalStats;
import com.laputa.server.handlers.BaseSimpleChannelInboundHandler;
import com.laputa.server.hardware.handlers.hardware.mqtt.logic.MqttHardwareLogic;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 29.07.15.
 */
public class MqttHardwareHandler extends BaseSimpleChannelInboundHandler<MqttMessage> {

    public final HardwareStateHolder state;
    private final MqttHardwareLogic hardware;
    private final GlobalStats stats;

    public MqttHardwareHandler(Holder holder, HardwareStateHolder stateHolder) {
        super(MqttMessage.class, holder.limits);
        this.hardware = new MqttHardwareLogic(holder.sessionDao, holder.reportingDao);
        this.state = stateHolder;
        this.stats = holder.stats;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MqttMessage msg) {
        this.stats.incrementMqttStat();
        MqttMessageType messageType = msg.fixedHeader().messageType();

        switch (messageType) {
            case PUBLISH :
                MqttPublishMessage publishMessage = (MqttPublishMessage) msg;
                String topic = publishMessage.variableHeader().topicName();

                switch (topic.toLowerCase()) {
                    case "hardware" :
                        hardware.messageReceived(ctx, state, publishMessage);
                        break;
                }

                break;

            case PINGREQ :
                ctx.writeAndFlush(MqttMessageFactory.newMessage(msg.fixedHeader(), msg.variableHeader(), null), ctx.voidPromise());
                break;

            case DISCONNECT :
                log.trace("Got disconnect. Closing...");
                ctx.close();
                break;
        }
    }

    @Override
    public StateHolderBase getState() {
        return state;
    }
}
