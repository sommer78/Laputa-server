package com.laputa.server.application.handlers.main.logic.dashboard.device;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.utils.ArrayUtil;
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
public class DeleteDeviceLogic {

    private static final Logger log = LogManager.getLogger(DeleteDeviceLogic.class);

    private final TokenManager tokenManager;
    private final SessionDao sessionDao;

    public DeleteDeviceLogic(TokenManager tokenManager, SessionDao sessionDao) {
        this.tokenManager = tokenManager;
        this.sessionDao = sessionDao;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        String[] split = split2(message.body);

        if (split.length < 2) {
            throw new IllegalCommandException("Wrong income message format.");
        }

        int dashId = ParseUtil.parseInt(split[0]) ;
        int deviceId = ParseUtil.parseInt(split[1]);

        if (deviceId == 0) {
            throw new IllegalCommandException("You are not allowed to remove device with id 0.");
        }

        DashBoard dash = state.user.profile.getDashByIdOrThrow(dashId);

        log.debug("Deleting device with id {}.", deviceId);

        int existingDeviceIndex = dash.getDeviceIndexById(deviceId);
        Device device = dash.devices[existingDeviceIndex];
        tokenManager.deleteDevice(device);
        Session session = sessionDao.userSession.get(state.userKey);
        session.closeHardwareChannelByDeviceId(dashId, deviceId);

        dash.devices = ArrayUtil.remove(dash.devices, existingDeviceIndex, Device.class);
        dash.updatedAt = System.currentTimeMillis();
        state.user.lastModifiedTs = dash.updatedAt;

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
