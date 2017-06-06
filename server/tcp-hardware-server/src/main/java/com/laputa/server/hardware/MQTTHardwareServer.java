package com.laputa.server.hardware;

import com.laputa.server.Holder;
import com.laputa.server.core.BaseServer;
import com.laputa.server.handlers.common.HardwareNotLoggedHandler;
import com.laputa.server.hardware.handlers.hardware.HardwareChannelStateHandler;
import com.laputa.server.hardware.handlers.hardware.mqtt.auth.MqttHardwareLoginHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class MQTTHardwareServer extends BaseServer {

    private final ChannelInitializer<SocketChannel> channelInitializer;

    public MQTTHardwareServer(Holder holder) {
        super(holder.props.getProperty("listen.address"), holder.props.getIntProperty("hardware.mqtt.port"), holder.transportTypeHolder);

        final int hardTimeoutSecs = holder.limits.HARDWARE_IDLE_TIMEOUT;
        final MqttHardwareLoginHandler mqttHardwareLoginHandler = new MqttHardwareLoginHandler(holder);
        final HardwareChannelStateHandler hardwareChannelStateHandler = new HardwareChannelStateHandler(holder.sessionDao, holder.gcmWrapper);

        channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //non-sharable handlers
                if (hardTimeoutSecs > 0) {
                    pipeline.addLast("MqttReadTimeout", new ReadTimeoutHandler(hardTimeoutSecs));
                }
                pipeline.addLast(hardwareChannelStateHandler)
                .addLast(new MqttDecoder())
                .addLast(MqttEncoder.INSTANCE)
                .addLast(mqttHardwareLoginHandler)
                .addLast(new HardwareNotLoggedHandler());
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
        return "Mqtt hardware";
    }

    @Override
    public void close() {
        System.out.println("Shutting down Mqtt hardware server...");
        super.close();
    }

}
