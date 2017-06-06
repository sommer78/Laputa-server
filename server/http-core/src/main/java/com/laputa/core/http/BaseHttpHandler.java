package com.laputa.core.http;

import com.laputa.core.http.rest.Handler;
import com.laputa.core.http.rest.HandlerHolder;
import com.laputa.core.http.rest.URIDecoder;
import com.laputa.server.Holder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.dao.TokenManager;
import com.laputa.server.core.protocol.enums.Command;
import com.laputa.server.core.protocol.handlers.DefaultExceptionHandler;
import com.laputa.server.core.stats.GlobalStats;
import com.laputa.server.handlers.DefaultReregisterHandler;
import com.laputa.utils.AnnotationsUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 24.12.15.
 */
public abstract class BaseHttpHandler extends ChannelInboundHandlerAdapter implements DefaultReregisterHandler, DefaultExceptionHandler {

    protected static final Logger log = LogManager.getLogger(BaseHttpHandler.class);

    protected final TokenManager tokenManager;
    protected final SessionDao sessionDao;
    protected final GlobalStats globalStats;
    protected final Handler[] handlers;
    protected final String rootPath;

    public BaseHttpHandler(Holder holder, String rootPath) {
        this(holder.tokenManager, holder.sessionDao, holder.stats, rootPath);
    }

    public BaseHttpHandler(TokenManager tokenManager, SessionDao sessionDao, GlobalStats globalStats, String rootPath) {
        this.tokenManager = tokenManager;
        this.sessionDao = sessionDao;
        this.globalStats = globalStats;
        this.rootPath = rootPath;
        this.handlers = AnnotationsUtil.register(rootPath, this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            process(ctx, req);
        }
    }

    public void process(ChannelHandlerContext ctx, HttpRequest req) {
        HandlerHolder handlerHolder = lookupHandler(req);

        if (handlerHolder != null) {
            log.debug("{} : {}", req.method().name(), req.uri());
            globalStats.mark(Command.HTTP_TOTAL);

            try {
                URIDecoder uriDecoder = new URIDecoder(req);
                uriDecoder.pathData = handlerHolder.extractParameters();
                Object[] params = handlerHolder.handler.fetchParams(ctx, uriDecoder);
                finishHttp(ctx, uriDecoder, handlerHolder.handler, params);
            } catch (Exception e) {
                ctx.writeAndFlush(Response.serverError(e.getMessage()), ctx.voidPromise());
            } finally {
                ReferenceCountUtil.release(req);
            }

        } else {
            ctx.fireChannelRead(req);
        }
    }

    public void finishHttp(ChannelHandlerContext ctx, URIDecoder uriDecoder, Handler handler, Object[] params) {
        FullHttpResponse response = handler.invoke(params);
        if (response != Response.NO_RESPONSE) {
            ctx.writeAndFlush(response);
        }
    }

    private HandlerHolder lookupHandler(HttpRequest req) {
        for (Handler handler : handlers) {
            if (handler.httpMethod == req.method()) {
                Matcher matcher = handler.uriTemplate.matcher(req.uri());
                if (matcher.matches()) {
                    return new HandlerHolder(handler, matcher);
                }
            }
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        handleUnexpectedException(ctx, cause);
    }

}
