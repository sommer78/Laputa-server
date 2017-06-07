package com.laputa.server.application.handlers.main.logic.dashboard.device;

import com.laputa.server.Holder;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ArrayUtil;
import com.laputa.utils.JsonParser;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.server.core.protocol.enums.Command.CREATE_DEVICE;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;
import static com.laputa.utils.StringUtils.split2;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class CreateDeviceLogic {

    private static final Logger log = LogManager.getLogger(CreateDeviceLogic.class);

    private final TokenManager tokenManager;
    private final int DEVICE_LIMIT;

    public CreateDeviceLogic(Holder holder) {
        this.tokenManager = holder.tokenManager;
        this.DEVICE_LIMIT = holder.limits.DEVICE_LIMIT;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            throw new IllegalCommandException("Wrong income message format.");
        }

        int dashId = ParseUtil.parseInt(split[0]) ;
        String deviceString = split[1];

        if (deviceString == null || deviceString.isEmpty()) {
            throw new IllegalCommandException("Income device message is empty.");
        }

        DashBoard dash = user.profile.getDashByIdOrThrow(dashId);

        if (dash.devices.length > DEVICE_LIMIT) {
            throw new NotAllowedException("Device limit is reached.");
        }

        Device newDevice = JsonParser.parseDevice(deviceString);

        log.debug("Creating new device {}.", deviceString);

        if (newDevice.isNotValid()) {
            throw new IllegalCommandException("Income device message is not valid.");
        }

        for (Device device : dash.devices) {
            if (device.id == newDevice.id) {
                throw new NotAllowedException("Device with same id already exists.");
            }
        }

        dash.devices = ArrayUtil.add(dash.devices, newDevice, Device.class);

        final String newToken = TokenGeneratorUtil.generateNewToken();
        tokenManager.assignToken(user, dashId, newDevice.id, newToken);

        dash.updatedAt = System.currentTimeMillis();
        user.lastModifiedTs = dash.updatedAt;

        if (ctx.channel().isWritable()) {
            ctx.writeAndFlush(makeUTF8StringMessage(CREATE_DEVICE, message.id, newDevice.toString()), ctx.voidPromise());
        }
    }

}
