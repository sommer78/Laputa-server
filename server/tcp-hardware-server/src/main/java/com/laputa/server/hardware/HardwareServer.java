package com.laputa.server.hardware;

import com.laputa.server.Holder;
import com.laputa.server.core.BaseServer;
import com.laputa.server.core.protocol.handlers.decoders.MessageDecoder;
import com.laputa.server.core.protocol.handlers.encoders.MessageEncoder;
import com.laputa.server.handlers.common.AlreadyLoggedHandler;
import com.laputa.server.handlers.common.HardwareNotLoggedHandler;
import com.laputa.server.hardware.handlers.hardware.HardwareChannelStateHandler;
import com.laputa.server.hardware.handlers.hardware.auth.HardwareLoginHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class HardwareServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public HardwareServer(Holder holder) {
        super(holder.props.getProperty("listen.address"), holder.props.getIntProperty("hardware.default.port"), holder.transportTypeHolder);

        final int hardTimeoutSecs = holder.limits.HARDWARE_IDLE_TIMEOUT;
        final HardwareLoginHandler hardwareLoginHandler = new HardwareLoginHandler(holder, port);
        final HardwareChannelStateHandler hardwareChannelStateHandler = new HardwareChannelStateHandler(holder.sessionDao, holder.gcmWrapper);
        final AlreadyLoggedHandler alreadyLoggedHandler = new AlreadyLoggedHandler();

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();

                if (hardTimeoutSecs > 0) {
                    pipeline.addLast("H_ReadTimeout", new ReadTimeoutHandler(hardTimeoutSecs));
                }
                pipeline.addLast("H_ChannelState", hardwareChannelStateHandler)
                .addLast("H_MessageDecoder", new MessageDecoder(holder.stats))
                .addLast("H_MessageEncoder", new MessageEncoder(holder.stats))
                .addLast("H_Login", hardwareLoginHandler)
                .addLast("H_NotLogged", new HardwareNotLoggedHandler())
                .addLast("H_AlreadyLogged", alreadyLoggedHandler);
            }
        };

        log.debug("hard.socket.idle.timeout = {}", hardTimeoutSecs);
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    protected String getServerName() {
        return "Hardware plain tcp/ip";
    }

    @Override
    public void close() {
        System.out.println("Shutting down Hardware plain tcp/ip server...");
        super.close();
    }

}
