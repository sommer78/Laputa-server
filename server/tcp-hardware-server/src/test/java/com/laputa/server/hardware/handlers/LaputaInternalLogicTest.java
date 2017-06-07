package com.laputa.server.hardware.handlers;

import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.Profile;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.model.messages.hardware.LaputaInternalMessage;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.server.hardware.handlers.hardware.logic.LaputaInternalLogic;
import com.laputa.utils.ServerProperties;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 04.12.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class LaputaInternalLogicTest {

    ServerProperties props = new ServerProperties(Collections.emptyMap());

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private ChannelPipeline pipeline;

    @Mock
    private ByteBufAllocator allocator;

    @Mock
    private ByteBuf byteBuf;

    @Test
    public void testCorrectBehavior() {
        LaputaInternalLogic logic = new LaputaInternalLogic(props.getIntProperty("hard.socket.idle.timeout", 0));

        when(ctx.pipeline()).thenReturn(pipeline);
        when(ctx.alloc()).thenReturn(allocator);
        when(allocator.ioBuffer(anyInt())).thenReturn(byteBuf);
        when(byteBuf.writeByte(eq(0))).thenReturn(byteBuf);
        when(byteBuf.writeShort(eq(1))).thenReturn(byteBuf);
        when(byteBuf.writeShort(eq(200))).thenReturn(byteBuf);

        User user = new User();
        user.profile = new Profile();
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 1;
        user.profile.dashBoards = new DashBoard[] {dashBoard};
        HardwareStateHolder hardwareStateHolder = new HardwareStateHolder(1, 0, user, null);

        LaputaInternalMessage hardwareInfoLogic = new LaputaInternalMessage(1, "ver 0.3.2-beta h-beat 60 buff-in 256 dev ESP8266".replaceAll(" ", "\0"));
        logic.messageReceived(ctx, hardwareStateHolder, hardwareInfoLogic);

        verify(pipeline).replace(eq(ReadTimeoutHandler.class), eq("H_ReadTimeout"), any());
        verify(ctx).writeAndFlush(any(), any());
    }

}
