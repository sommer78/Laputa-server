package com.laputa.server.hardware.handlers.hardware.logic;

import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.core.protocol.model.messages.hardware.BridgeMessage;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static com.laputa.utils.LaputaByteBufUtil.*;
import static com.laputa.utils.StateHolderUtil.getHardState;
import static com.laputa.utils.StringUtils.split3;

/**
 * Bridge handler responsible for forwarding messages between different hardware via Laputa Server.
 * SendTo device defined by Auth Token.
 *
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class BridgeLogic {

    private static final Logger log = LogManager.getLogger(BridgeLogic.class);
    private final HardwareLogic hardwareLogic;
    private final SessionDao sessionDao;
    private HashMap<String, String> sendToMap;

    public BridgeLogic(SessionDao sessionDao, HardwareLogic hardwareLogic) {
        this.sessionDao = sessionDao;
        this.hardwareLogic = hardwareLogic;
    }

    private static boolean isInit(String body) {
        return body.length() > 0 && body.charAt(0) == 'i';
    }

    public void messageReceived(ChannelHandlerContext ctx, HardwareStateHolder state, StringMessage message) {
        Session session = sessionDao.userSession.get(state.userKey);
        String[] split = split3(message.body);

        if (split.length < 3) {
            log.error("Wrong bridge body. '{}'", message.body);
            ctx.writeAndFlush(illegalCommand(message.id), ctx.voidPromise());
            return;
        }

        final String bridgePin = split[0];

        if (isInit(split[1])) {
            final String token = split[2];
            if (sendToMap == null) {
                sendToMap = new HashMap<>();
            }
            if (sendToMap.size() > 100 || token.length() != 32) {
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
            } else {
                sendToMap.put(bridgePin, token);
                ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
            }
        } else {
            if (sendToMap == null || sendToMap.size() == 0) {
                log.debug("Bridge not initialized. {}", state.user.email);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            final String token = sendToMap.get(bridgePin);
            if (token == null) {
                log.debug("No token. Bridge not initialized. {}", state.user.email);
                ctx.writeAndFlush(notAllowed(message.id), ctx.voidPromise());
                return;
            }

            if (session.hardwareChannels.size() > 1) {
                boolean messageWasSent = false;
                final String body = message.body.substring(message.body.indexOf(StringUtils.BODY_SEPARATOR_STRING) + 1);
                BridgeMessage bridgeMessage = new BridgeMessage(message.id, body);
                for (Channel channel : session.hardwareChannels) {
                    if (channel != ctx.channel() && channel.isWritable()) {
                        HardwareStateHolder hardwareState = getHardState(channel);
                        if (token.equals(hardwareState.token)) {
                            messageWasSent = true;
                            hardwareLogic.messageReceived(ctx, hardwareState, bridgeMessage);
                            channel.writeAndFlush(bridgeMessage, channel.voidPromise());
                        }
                    }
                }
                if (!messageWasSent) {
                    ctx.writeAndFlush(deviceNotInNetwork(message.id), ctx.voidPromise());
                }
            } else {
                ctx.writeAndFlush(deviceNotInNetwork(message.id), ctx.voidPromise());
            }
        }
    }
}

