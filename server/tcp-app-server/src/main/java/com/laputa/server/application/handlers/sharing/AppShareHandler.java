package com.laputa.server.application.handlers.sharing;

import com.laputa.server.Holder;
import com.laputa.server.application.handlers.main.logic.AddPushLogic;
import com.laputa.server.application.handlers.main.logic.AppSyncLogic;
import com.laputa.server.application.handlers.main.logic.LoadProfileGzippedLogic;
import com.laputa.server.application.handlers.main.logic.dashboard.device.GetDevicesLogic;
import com.laputa.server.application.handlers.main.logic.reporting.GetGraphDataLogic;
import com.laputa.server.application.handlers.sharing.auth.AppShareStateHolder;
import com.laputa.server.application.handlers.sharing.logic.HardwareAppShareLogic;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.core.session.StateHolderBase;
import com.laputa.server.core.stats.GlobalStats;
import com.laputa.server.handlers.BaseSimpleChannelInboundHandler;
import com.laputa.server.handlers.common.PingLogic;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 *
 */
public class AppShareHandler extends BaseSimpleChannelInboundHandler<StringMessage> {

    public final AppShareStateHolder state;
    private final HardwareAppShareLogic hardwareApp;
    private final GetGraphDataLogic graphData;
    private final LoadProfileGzippedLogic loadProfileGzippedLogic;
    private final GlobalStats stats;

    public AppShareHandler(Holder holder, AppShareStateHolder state) {
        super(StringMessage.class, holder.limits);
        this.hardwareApp = new HardwareAppShareLogic(holder.sessionDao);
        this.graphData = new GetGraphDataLogic(holder.reportingDao, holder.blockingIOProcessor);
        this.loadProfileGzippedLogic = new LoadProfileGzippedLogic(holder);
        this.state = state;
        this.stats = holder.stats;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, StringMessage msg) {
        this.stats.incrementAppStat();
        switch (msg.command) {
            case HARDWARE:
                hardwareApp.messageReceived(ctx, state, msg);
                break;
            case LOAD_PROFILE_GZIPPED :
                loadProfileGzippedLogic.messageReceived(ctx, state, msg);
                break;
            case ADD_PUSH_TOKEN :
                AddPushLogic.messageReceived(ctx, state, msg);
                break;
            case GET_GRAPH_DATA :
                graphData.messageReceived(ctx, state.user, msg);
                break;
            case GET_DEVICES :
                GetDevicesLogic.messageReceived(ctx, state.user, msg);
                break;
            case PING :
                PingLogic.messageReceived(ctx, msg.id);
                break;
            case APP_SYNC :
                AppSyncLogic.messageReceived(ctx, state, msg);
                break;
        }
    }

    @Override
    public StateHolderBase getState() {
        return state;
    }
}
