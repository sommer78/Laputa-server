package com.laputa.server.hardware.handlers.hardware;

import com.laputa.server.Holder;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.server.core.session.StateHolderBase;
import com.laputa.server.handlers.BaseSimpleChannelInboundHandler;
import com.laputa.server.handlers.common.PingLogic;
import com.laputa.server.hardware.handlers.hardware.logic.*;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 29.07.15.
 */
public class HardwareHandler extends BaseSimpleChannelInboundHandler<StringMessage> {

    public final HardwareStateHolder state;
    private final HardwareLogic hardware;
    private final MailLogic email;
    private final BridgeLogic bridge;
    private final PushLogic push;
    private final TwitLogic tweet;
    private final SmsLogic smsLogic;
    private final SetWidgetPropertyLogic propertyLogic;
    private final BlynkInternalLogic info;

    public HardwareHandler(Holder holder, HardwareStateHolder stateHolder) {
        super(StringMessage.class, holder.limits);
        this.hardware = new HardwareLogic(holder, stateHolder.user.email);
        this.bridge = new BridgeLogic(holder.sessionDao, hardware);

        this.email = new MailLogic(holder.blockingIOProcessor, holder.mailWrapper, holder.limits.NOTIFICATION_PERIOD_LIMIT_SEC);
        this.push = new PushLogic(holder.gcmWrapper, holder.limits.NOTIFICATION_PERIOD_LIMIT_SEC);
        this.tweet = new TwitLogic(holder.blockingIOProcessor, holder.twitterWrapper, holder.limits.NOTIFICATION_PERIOD_LIMIT_SEC);
        this.smsLogic = new SmsLogic(holder.smsWrapper, holder.limits.NOTIFICATION_PERIOD_LIMIT_SEC);
        this.propertyLogic = new SetWidgetPropertyLogic(holder.sessionDao);
        this.info = new BlynkInternalLogic(holder.limits.HARDWARE_IDLE_TIMEOUT);

        this.state = stateHolder;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        switch (msg.command) {
            case HARDWARE:
                hardware.messageReceived(ctx, state, msg);
                break;
            case PING:
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case BRIDGE:
                bridge.messageReceived(ctx, state, msg);
                break;
            case EMAIL:
                email.messageReceived(ctx, state, msg);
                break;
            case PUSH_NOTIFICATION:
                push.messageReceived(ctx, state, msg);
                break;
            case TWEET:
                tweet.messageReceived(ctx, state, msg);
                break;
            case SMS:
                smsLogic.messageReceived(ctx, state, msg);
                break;
            case HARDWARE_SYNC:
                HardwareSyncLogic.messageReceived(ctx, state, msg);
                break;
            case BLYNK_INTERNAL:
                info.messageReceived(ctx, state, msg);
                break;
            case SET_WIDGET_PROPERTY:
                propertyLogic.messageReceived(ctx, state, msg);
                break;
        }
    }

    @Override
    public StateHolderBase getState() {
        return state;
    }
}
