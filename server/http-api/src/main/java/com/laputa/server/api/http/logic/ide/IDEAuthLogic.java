package com.laputa.server.api.http.logic.ide;

import com.laputa.core.http.BaseHttpHandler;
import com.laputa.core.http.MediaType;
import com.laputa.core.http.Response;
import com.laputa.core.http.annotation.Consumes;
import com.laputa.core.http.annotation.POST;
import com.laputa.core.http.annotation.Path;
import com.laputa.server.Holder;
import com.laputa.server.core.dao.UserDao;
import com.laputa.server.core.model.AppName;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.redis.RedisClient;
import io.netty.channel.ChannelHandler;

import java.util.Map;

import static com.laputa.core.http.Response.*;

/**
 * @author gig.
 */
@Path("/ide")
@ChannelHandler.Sharable
public class IDEAuthLogic extends BaseHttpHandler {

    private final UserDao userDao;
    private final RedisClient redisClient;
    protected static final String IDE_AUTHORIZE_ENDPOINT = "/ide/authorize";

    //for tests only
    protected IDEAuthLogic(UserDao userDao, RedisClient redisClient) {
        super(null, null, null, "");
        this.userDao = userDao;
        this.redisClient = redisClient;
    }

    public IDEAuthLogic(Holder holder) {
        super(holder, "");
        this.userDao = holder.userDao;
        this.redisClient = holder.redisClient;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/authorize")
    public Response authorize(Map<String, String> data) {

        final String email = data.get("email");
        final String password = data.get("password");

        if (email == null || password == null) {
            return badRequest();
        }

        User user = userDao.getByName(email, AppName.LAPUTA);

        if (user == null) {
            String userServer = redisClient.getServerByUser(email);
            if (userServer != null) {
                return redirect("//" + userServer + IDE_AUTHORIZE_ENDPOINT);
            }
        }

        if (user == null || !password.equals(user.pass)) {
            return badRequest();
        }

        return ok(user);
    }

}
