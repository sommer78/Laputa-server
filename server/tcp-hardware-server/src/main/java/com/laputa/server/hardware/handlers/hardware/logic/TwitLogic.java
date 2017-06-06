package com.laputa.server.hardware.handlers.hardware.logic;

import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.widgets.notifications.Twitter;
import com.laputa.server.core.processors.NotificationBase;
import com.laputa.server.core.protocol.exceptions.NotificationBodyInvalidException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.server.notifications.twitter.TwitterWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Response.NOTIFICATION_NOT_AUTHORIZED;
import static com.laputa.utils.BlynkByteBufUtil.*;

/**
 * Sends tweets from hardware.
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class TwitLogic extends NotificationBase {

    private static final Logger log = LogManager.getLogger(TwitLogic.class);

    private final BlockingIOProcessor blockingIOProcessor;
    private final TwitterWrapper twitterWrapper;

    public TwitLogic(BlockingIOProcessor blockingIOProcessor, TwitterWrapper twitterWrapper, long notificationQuotaLimit) {
        super(notificationQuotaLimit);
        this.blockingIOProcessor = blockingIOProcessor;
        this.twitterWrapper = twitterWrapper;
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        if (Twitter.isWrongBody(message.body)) {
            throw new NotificationBodyInvalidException();
        }

        DashBoard dash = state.user.profile.getDashByIdOrThrow(state.dashId);
        Twitter twitterWidget = dash.getWidgetByType(Twitter.class);

        if (twitterWidget == null || !dash.isActive ||
                twitterWidget.token == null || twitterWidget.token.isEmpty() ||
                twitterWidget.secret == null || twitterWidget.secret.isEmpty()) {
            log.debug("User has no access token provided for twit widget.");
            ctx.writeAndFlush(makeResponse(message.id, NOTIFICATION_NOT_AUTHORIZED), ctx.voidPromise());
            return;
        }

        checkIfNotificationQuotaLimitIsNotReached();

        log.trace("Sending Twit for user {}, with message : '{}'.", state.user.email, message.body);
        twit(ctx.channel(), state.user.email, twitterWidget.token, twitterWidget.secret, message.body, message.id);
    }

    private void twit(Channel channel, String email, String token, String secret, String body, int msgId) {
        blockingIOProcessor.execute(() -> {
            try {
                twitterWrapper.send(token, secret, body);
                channel.writeAndFlush(ok(msgId), channel.voidPromise());
            } catch (Exception e) {
                logError(e.getMessage(), email);
                channel.writeAndFlush(notificationError(msgId), channel.voidPromise());
            }
        });
    }

    private static void logError(String errorMessage, String email) {
        if (errorMessage != null) {
            if (errorMessage.contains("Status is a duplicate")) {
                log.warn("Duplicate twit status for user {}.", email);
            } else if (errorMessage.contains("Authentication credentials")) {
                log.warn("Tweet authentication failure for {}.", email);
            } else if (errorMessage.contains("The request is understood, but it has been refused.")) {
                log.warn("User twit account is banned by twitter. {}.", email);
            } else {
                log.error("Error sending twit for user {}. Reason : {}", email, errorMessage);
            }
        }
    }

}
