package com.laputa.server.application.handlers.main.auth;

import com.laputa.server.Holder;
import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.dao.UserDao;
import com.laputa.server.core.protocol.model.messages.appllication.GetServerMessage;
import com.laputa.server.redis.RedisClient;
import com.laputa.utils.StringUtils;
import com.laputa.utils.validators.LaputaEmailValidator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.laputa.utils.LaputaByteBufUtil.*;


/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 13.10.16.
 */
@ChannelHandler.Sharable
public class GetServerHandler extends SimpleChannelInboundHandler<GetServerMessage> {

    private final String[] loadBalancingIps;
    private final BlockingIOProcessor blockingIOProcessor;
    private final RedisClient redisClient;
    private final UserDao userDao;
    private final String currentIp;

    public GetServerHandler(Holder holder, String[] ips) {
        super();
        this.loadBalancingIps = ips;
        this.blockingIOProcessor = holder.blockingIOProcessor;
        this.redisClient = holder.redisClient;
        this.userDao = holder.userDao;
        this.currentIp = holder.host;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GetServerMessage msg) throws Exception {
        final String[] parts = StringUtils.split2(msg.body);

        if (parts.length < 2) {
            ctx.writeAndFlush(illegalCommand(msg.id), ctx.voidPromise());
            return;
        }

        final String email = parts[0];
        final String appName = parts[1];

        if (appName == null || appName.isEmpty() || appName.length() > 100) {
            ctx.writeAndFlush(illegalCommand(msg.id), ctx.voidPromise());
            return;
        }

        if (LaputaEmailValidator.isNotValidEmail(email)) {
            ctx.writeAndFlush(illegalCommandBody(msg.id), ctx.voidPromise());
            return;
        }

        if (userDao.contains(email, appName)) {
            //user exists on current server. so returning ip of current server
            ctx.writeAndFlush(makeASCIIStringMessage(msg.command, msg.id, currentIp), ctx.voidPromise());
        } else {
            //user is on other server
            blockingIOProcessor.executeDB(() -> {
                String userServer = redisClient.getServerByUser(email);
                if (userServer == null) {
                    //user not registered yet anywhere
                    redisClient.assignServerToUser(email, currentIp);
                    userServer = currentIp;
                }

                ctx.writeAndFlush(makeASCIIStringMessage(msg.command, msg.id, userServer), ctx.voidPromise());
            });
        }
    }

}
