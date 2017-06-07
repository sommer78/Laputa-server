package com.laputa.server.application.handlers.main.logic.dashboard.device;

import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.protocol.exceptions.IllegalCommandBodyException;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.JsonParser;
import com.laputa.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.LaputaByteBufUtil.ok;
import static com.laputa.utils.StringUtils.split2;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.02.16.
 */
public class UpdateDeviceLogic {

    private static final Logger log = LogManager.getLogger(UpdateDeviceLogic.class);

    public static void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
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

        Device newDevice = JsonParser.parseDevice(deviceString);

        log.debug("Updating new device {}.", deviceString);

        if (newDevice.isNotValid()) {
            throw new IllegalCommandException("Income device message is not valid.");
        }

        Device existingDevice = dash.getDeviceById(newDevice.id);

        if (existingDevice == null) {
            throw new IllegalCommandBodyException("Attempt to update device with non existing id.");
        }

        existingDevice.update(newDevice);
        dash.updatedAt = System.currentTimeMillis();
        user.lastModifiedTs = dash.updatedAt;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
