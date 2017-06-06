package com.laputa.server.application;

import com.laputa.server.Holder;
import com.laputa.server.application.handlers.main.AppChannelStateHandler;
import com.laputa.server.application.handlers.main.auth.AppLoginHandler;
import com.laputa.server.application.handlers.main.auth.GetServerHandler;
import com.laputa.server.application.handlers.main.auth.RegisterHandler;
import com.laputa.server.application.handlers.sharing.auth.AppShareLoginHandler;
import com.laputa.server.core.BaseServer;
import com.laputa.server.core.protocol.handlers.decoders.MessageDecoder;
import com.laputa.server.core.protocol.handlers.encoders.MessageEncoder;
import com.laputa.server.handlers.common.UserNotLoggedHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * Class responsible for handling all Application connections and netty pipeline initialization.
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class AppServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public AppServer(Holder holder) {
        super(holder.props.getProperty("listen.address"), holder.props.getIntProperty("app.ssl.port"), holder.transportTypeHolder);

        final String[] loadBalancingIPs = holder.props.getCommaSeparatedValueAsArray("load.balancing.ips");

        final AppChannelStateHandler appChannelStateHandler = new AppChannelStateHandler(holder.sessionDao);
        final RegisterHandler registerHandler = new RegisterHandler(holder);
        final AppLoginHandler appLoginHandler = new AppLoginHandler(holder);
        final AppShareLoginHandler appShareLoginHandler = new AppShareLoginHandler(holder);
        final UserNotLoggedHandler userNotLoggedHandler = new UserNotLoggedHandler();
        final GetServerHandler getServerHandler = new GetServerHandler(holder, loadBalancingIPs);

        log.debug("app.socket.idle.timeout = 600");

        this.channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //600 specifies maximum seconds when application socket could be idle. After which
                //socket will be closed due to non activity. In seconds.
                ch.pipeline()
                        .addLast("AReadTimeout", new ReadTimeoutHandler(600))
                        .addLast("ASSL", holder.sslContextHolder.sslCtx.newHandler(ch.alloc()))
                        .addLast("AChannelState", appChannelStateHandler)
                        .addLast("AMessageDecoder", new MessageDecoder(holder.stats))
                        .addLast("AMessageEncoder", new MessageEncoder(holder.stats))
                        .addLast("AGetServer", getServerHandler)
                        .addLast("ARegister", registerHandler)
                        .addLast("ALogin", appLoginHandler)
                        .addLast("AShareLogin", appShareLoginHandler)
                        .addLast("ANotLogged", userNotLoggedHandler);
            }
        };
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    protected String getServerName() {
        return "Application";
    }

    @Override
    public void close() {
        System.out.println("Shutting down Application SSL server...");
        super.close();
    }

}
