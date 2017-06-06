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
public class HardwareSSLServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public HardwareSSLServer(Holder holder) {
        super(holder.props.getProperty("listen.address"), holder.props.getIntProperty("hardware.ssl.port"), holder.transportTypeHolder);

        final HardwareLoginHandler hardwareLoginHandler = new HardwareLoginHandler(holder, port);
        final HardwareChannelStateHandler hardwareChannelStateHandler = new HardwareChannelStateHandler(holder.sessionDao, holder.gcmWrapper);
        final AlreadyLoggedHandler alreadyLoggedHandler = new AlreadyLoggedHandler();

        final int hardTimeoutSecs = holder.limits.HARDWARE_IDLE_TIMEOUT;

        this.channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                if (hardTimeoutSecs > 0) {
                    pipeline.addLast("HSSLReadTimeout", new ReadTimeoutHandler(hardTimeoutSecs));
                }
                pipeline.addLast("HSSL", holder.sslContextHolder.sslCtx.newHandler(ch.alloc()))
                .addLast("HSSLChannelState", hardwareChannelStateHandler)
                .addLast("HSSLMessageDecoder", new MessageDecoder(holder.stats))
                .addLast("HSSLMessageEncoder", new MessageEncoder(holder.stats))
                .addLast("HSSLLogin", hardwareLoginHandler)
                .addLast("HSSLNotLogged", new HardwareNotLoggedHandler())
                .addLast("HSSLAlreadyLogged", alreadyLoggedHandler);
            }
        };
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    protected String getServerName() {
        return "Hardware SSL";
    }

    @Override
    public void close() {
        System.out.println("Shutting down Hardware SSL server...");
        super.close();
    }

}
