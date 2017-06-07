package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.Holder;
import com.laputa.server.Limits;
import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.App;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.model.enums.ProvisionType;
import com.laputa.server.core.protocol.exceptions.IllegalCommandBodyException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.db.DBManager;
import com.laputa.server.db.model.FlashedToken;
import com.laputa.server.notifications.mail.MailWrapper;
import com.laputa.server.notifications.mail.QrHolder;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.StringUtils;
import com.laputa.utils.TokenGeneratorUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.LaputaByteBufUtil.notificationError;
import static com.laputa.utils.LaputaByteBufUtil.ok;

/**
 * Sends email from application.
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class MailQRsLogic {

    private static final Logger log = LogManager.getLogger(MailQRsLogic.class);
    private final Limits limits;

    private final BlockingIOProcessor blockingIOProcessor;
    private final MailWrapper mailWrapper;
    private final DBManager dbManager;

    public MailQRsLogic(Holder holder) {
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.mailWrapper =  holder.mailWrapper;
        this.dbManager = holder.dbManager;
        this.limits = holder.limits;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = message.body.split(StringUtils.BODY_SEPARATOR_STRING);

        int dashId = ParseUtil.parseInt(split[0]);
        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        if (dash.devices.length == 0) {
            throw new IllegalCommandBodyException("No devices in project.");
        }

        String appId = split[1];
        App app = user.profile.getAppById(appId);

        if (app == null) {
            throw new IllegalCommandBodyException("App with passed id not found.");
        }

        log.debug("Sending app preview email to {}, provision type {}", user.email, app.provisionType);
        makePublishPreviewEmail(ctx, dash, app.provisionType, user.email, app.name, appId, message.id);
    }

    private void makePublishPreviewEmail(ChannelHandlerContext ctx, DashBoard dash, ProvisionType provisionType, String to, String publishAppName, String publishAppId, int msgId) {
        String subj = publishAppName + " - App details";
        if (provisionType == ProvisionType.DYNAMIC) {
            mailDynamic(ctx.channel(), to, subj, limits.DYNAMIC_MAIL_BODY, publishAppId, dash, msgId);
        } else {
            mailStatic(ctx.channel(), to, subj, limits.STATIC_MAIL_BODY, publishAppId, dash, msgId);
        }
    }

    private void mailDynamic(Channel channel, String to, String subj, String body, String publishAppId, DashBoard dash, int msgId) {
        blockingIOProcessor.execute(() -> {
            try {
                QrHolder[] qrHolders = makeQRs(to, publishAppId, dash, true);

                String finalBody = body.replace("{project_name}", dash.name);

                mailWrapper.sendWithAttachment(to, subj, finalBody, qrHolders);
                channel.writeAndFlush(ok(msgId), channel.voidPromise());
            } catch (Exception e) {
                log.error("Error sending dynamic email from application. For user {}. Error: ", to, e);
                channel.writeAndFlush(notificationError(msgId), channel.voidPromise());
            }
        });
    }

    private void mailStatic(Channel channel, String to, String subj, String body, String publishAppId, DashBoard dash, int msgId) {
        blockingIOProcessor.execute(() -> {
            try {
                QrHolder[] qrHolders = makeQRs(to, publishAppId, dash, false);
                StringBuilder sb = new StringBuilder();
                for (QrHolder qrHolder : qrHolders) {
                    qrHolder.attach(sb);
                }

                String finalBody = body.replace("{project_name}", dash.name)
                                       .replace("{device_section}", sb.toString());

                mailWrapper.sendWithAttachment(to, subj, finalBody, qrHolders);
                channel.writeAndFlush(ok(msgId), channel.voidPromise());
            } catch (Exception e) {
                log.error("Error sending static email from application. For user {}. Reason: {}", to, e.getMessage());
                channel.writeAndFlush(notificationError(msgId), channel.voidPromise());
            }
        });
    }

    private QrHolder[] makeQRs(String username, String appId, DashBoard dash, boolean onlyFirst) throws Exception {
        int tokensCount = onlyFirst ? 1 : dash.devices.length;
        QrHolder[] qrHolders = new QrHolder[tokensCount];
        FlashedToken[] flashedTokens = new FlashedToken[tokensCount];

        int i = 0;
        for (Device device : dash.devices) {
            String newToken = TokenGeneratorUtil.generateNewToken();
            qrHolders[i] = new QrHolder(dash.id, device.id, device.name, newToken, QRCode.from(newToken).to(ImageType.JPG).stream().toByteArray());
            flashedTokens[i] = new FlashedToken(username, newToken, appId, dash.id, device.id);
            if (onlyFirst) {
                break;
            }
            i++;
        }

        if (!dbManager.insertFlashedTokens(flashedTokens)) {
            throw new Exception("App Publishing Preview requires enabled DB.");
        }

        return qrHolders;
    }
}
