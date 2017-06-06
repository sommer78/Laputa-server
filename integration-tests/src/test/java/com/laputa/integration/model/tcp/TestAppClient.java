package com.laputa.integration.model.tcp;

import com.laputa.client.core.AppClient;
import com.laputa.client.handlers.decoders.ClientMessageDecoder;
import com.laputa.integration.model.SimpleClientHandler;
import com.laputa.server.core.protocol.handlers.encoders.MessageEncoder;
import com.laputa.server.core.protocol.model.messages.MessageBase;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.core.protocol.model.messages.appllication.GetProjectByTokenBinaryMessage;
import com.laputa.server.core.protocol.model.messages.appllication.LoadProfileGzippedBinaryMessage;
import com.laputa.server.core.stats.GlobalStats;
import com.laputa.utils.ByteUtils;
import com.laputa.utils.ServerProperties;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 1/31/2015.
 */
public class TestAppClient extends AppClient {

    public final SimpleClientHandler responseMock = Mockito.mock(SimpleClientHandler.class);
    protected int msgId = 0;

    public TestAppClient(String host, int port) {
        super(host, port, Mockito.mock(Random.class), new ServerProperties(Collections.emptyMap()));
        Mockito.when(random.nextInt(Short.MAX_VALUE)).thenReturn(1);
    }

    public TestAppClient(String host, int port, ServerProperties properties) {
        this(host, port, properties, new NioEventLoopGroup());
    }

    public TestAppClient(String host, int port, ServerProperties properties, NioEventLoopGroup nioEventLoopGroup) {
        super(host, port, Mockito.mock(Random.class), properties);
        Mockito.when(random.nextInt(Short.MAX_VALUE)).thenReturn(1);
        this.nioEventLoopGroup = nioEventLoopGroup;
    }


    public String getBody() throws Exception {
        return getBody(1);
    }

    public String getBody(int expectedMessageOrder) throws Exception {
        ArgumentCaptor<MessageBase> objectArgumentCaptor = ArgumentCaptor.forClass(MessageBase.class);
        verify(responseMock, timeout(1000).times(expectedMessageOrder)).channelRead(any(), objectArgumentCaptor.capture());
        List<MessageBase> arguments = objectArgumentCaptor.getAllValues();
        MessageBase messageBase = arguments.get(expectedMessageOrder - 1);
        if (messageBase instanceof StringMessage) {
            return ((StringMessage) messageBase).body;
        } else if (messageBase instanceof LoadProfileGzippedBinaryMessage) {
            return new String(ByteUtils.decompress(messageBase.getBytes()));
        } else if (messageBase instanceof GetProjectByTokenBinaryMessage) {
            return new String(ByteUtils.decompress(messageBase.getBytes()));
        }

        throw new RuntimeException("Unexpected message");
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        sslCtx.newHandler(ch.alloc(), host, port),
                        new ClientMessageDecoder(),
                        new MessageEncoder(new GlobalStats()),
                        responseMock
                );
            }
        };
    }

    public void send(String line) {
        send(produceMessageBaseOnUserInput(line, ++msgId));
    }

    public void send(String line, int id) {
        send(produceMessageBaseOnUserInput(line, id));
    }

    public void reset() {
        Mockito.reset(responseMock);
        msgId = 0;
    }

    public void replace(SimpleClientHandler simpleClientHandler) {
        this.channel.pipeline().removeLast();
        this.channel.pipeline().addLast(simpleClientHandler);
    }

}
