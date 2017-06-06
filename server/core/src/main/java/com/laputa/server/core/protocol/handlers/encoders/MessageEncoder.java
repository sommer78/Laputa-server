package com.laputa.server.core.protocol.handlers.encoders;

import com.laputa.server.core.protocol.enums.Command;
import com.laputa.server.core.protocol.enums.Response;
import com.laputa.server.core.protocol.model.messages.MessageBase;
import com.laputa.server.core.protocol.model.messages.ResponseWithBodyMessage;
import com.laputa.server.core.stats.GlobalStats;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encodes java message into a bytes array.
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class MessageEncoder extends MessageToByteEncoder<MessageBase> {

    private final GlobalStats stats;

    public MessageEncoder(GlobalStats stats) {
        this.stats = stats;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageBase message, ByteBuf out) throws Exception {
        out.writeByte(message.command);
        out.writeShort(message.id);

        if (message.command == Command.RESPONSE) {
            out.writeShort(message.length);
            if (message.length == Response.DEVICE_WENT_OFFLINE) {
                out.writeInt(((ResponseWithBodyMessage) message).dashId);
            }
        } else {
            stats.mark(message.command);

            byte[] body = message.getBytes();
            out.writeShort(body.length);
            if (body.length > 0) {
                out.writeBytes(body);
            }
        }
    }
}
