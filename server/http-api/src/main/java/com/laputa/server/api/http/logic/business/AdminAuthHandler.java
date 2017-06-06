package com.laputa.server.api.http.logic.business;

import com.laputa.core.http.BaseHttpHandler;
import com.laputa.core.http.MediaType;
import com.laputa.core.http.Response;
import com.laputa.core.http.annotation.Consumes;
import com.laputa.core.http.annotation.FormParam;
import com.laputa.core.http.annotation.POST;
import com.laputa.core.http.annotation.Path;
import com.laputa.server.Holder;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.dao.UserDao;
import com.laputa.server.core.model.AppName;
import com.laputa.server.core.model.auth.User;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import static com.laputa.core.http.Response.redirect;
import static io.netty.handler.codec.http.HttpHeaderNames.SET_COOKIE;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 09.12.15.
 */
@Path("")
@ChannelHandler.Sharable
public class AdminAuthHandler extends BaseHttpHandler {

    //1 month
    private static final int COOKIE_EXPIRE_TIME = 30 * 60 * 60 * 24;

    private final UserDao userDao;

    public AdminAuthHandler(Holder holder, String adminRootPath) {
        super(holder, adminRootPath);
        this.userDao = holder.userDao;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/login")
    public Response login(@FormParam("email") String email,
                          @FormParam("password") String password) {

        if (email == null || password == null) {
            return redirect(rootPath);
        }

        User user = userDao.getByName(email, AppName.BLYNK);

        if (user == null || !user.isSuperAdmin) {
            return redirect(rootPath);
        }

        if (!password.equals(user.pass)) {
            return redirect(rootPath);
        }

        Response response = redirect(rootPath);

        Cookie cookie = makeDefaultSessionCookie(sessionDao.generateNewSession(user), COOKIE_EXPIRE_TIME);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));

        return response;
    }

    @POST
    @Path("/logout")
    public Response logout() {
        Response response = redirect(rootPath);
        Cookie cookie = makeDefaultSessionCookie("", 0);
        response.headers().add(SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
        return response;
    }

    private static Cookie makeDefaultSessionCookie(String sessionId, int maxAge) {
        DefaultCookie cookie = new DefaultCookie(SessionDao.SESSION_COOKIE, sessionId);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

}
