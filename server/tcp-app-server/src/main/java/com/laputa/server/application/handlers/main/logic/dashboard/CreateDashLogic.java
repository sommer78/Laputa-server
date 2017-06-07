package com.laputa.server.application.handlers.main.logic.dashboard;

import com.laputa.server.application.handlers.main.auth.AppStateHolder;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.exceptions.QuotaLimitException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.workers.timer.TimerWorker;
import com.laputa.utils.ArrayUtil;
import com.laputa.utils.JsonParser;
import com.laputa.utils.StringUtils;
import com.laputa.utils.TokenGeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.laputa.utils.LaputaByteBufUtil.ok;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class CreateDashLogic {

    private static final Logger log = LogManager.getLogger(CreateDashLogic.class);

    private final int DASH_MAX_LIMIT;
    private final int DASH_MAX_SIZE;
    private final TimerWorker timerWorker;
    private final TokenManager tokenManager;

    public CreateDashLogic(TimerWorker timerWorker, TokenManager tokenManager, int dashMaxLimit, int dashMaxSize) {
        this.tokenManager = tokenManager;
        this.DASH_MAX_LIMIT = dashMaxLimit;
        this.DASH_MAX_SIZE = dashMaxSize;
        this.timerWorker = timerWorker;
    }

    public void messageReceived(ChannelHandlerContext ctx, AppStateHolder state, StringMessage message) {
        boolean generateTokensForDevices = true;
        final String dashString;
        if (message.body.startsWith("no_token")) {
            generateTokensForDevices = false;
            dashString = StringUtils.split2(message.body)[1];
        } else {
            dashString = message.body;
        }

        if (dashString == null || dashString.isEmpty()) {
            throw new IllegalCommandException("Income create dash message is empty.");
        }

        if (dashString.length() > DASH_MAX_SIZE) {
            throw new NotAllowedException("User dashboard is larger then limit.");
        }

        log.debug("Trying to parse user newDash : {}", dashString);
        DashBoard newDash = JsonParser.parseDashboard(dashString);

        log.info("Creating new dashboard.");

        final User user = state.user;
        if (user.profile.dashBoards.length >= DASH_MAX_LIMIT) {
            throw new QuotaLimitException("Dashboards limit reached.");
        }

        for (DashBoard dashBoard : user.profile.dashBoards) {
            if (dashBoard.id == newDash.id) {
                throw new NotAllowedException("Dashboard already exists.");
            }
        }

        if (newDash.createdAt == 0) {
            newDash.createdAt = System.currentTimeMillis();
        }

        user.subtractEnergy(newDash.energySum());
        user.profile.dashBoards = ArrayUtil.add(user.profile.dashBoards, newDash, DashBoard.class);

        if (newDash.devices == null) {
            newDash.devices = ArrayUtil.EMPTY_DEVICES;
        } else {
            for (Device device : newDash.devices) {
                //this case only possible for clone,
                device.erase();
                if (generateTokensForDevices) {
                    String token = TokenGeneratorUtil.generateNewToken();
                    tokenManager.assignToken(user, newDash.id, device.id, token);
                }
            }
        }

        user.lastModifiedTs = System.currentTimeMillis();

        newDash.addTimers(timerWorker, state.userKey);

        if (!generateTokensForDevices) {
            newDash.eraseValues();
        }

        ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
    }

}
