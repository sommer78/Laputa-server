package com.laputa.server.admin.http.logic;

import com.laputa.core.http.CookiesBaseHttpHandler;
import com.laputa.core.http.Response;
import com.laputa.core.http.annotation.GET;
import com.laputa.core.http.annotation.Path;
import com.laputa.core.http.annotation.QueryParam;
import com.laputa.server.Holder;
import com.laputa.server.core.dao.UserDao;
import io.netty.channel.ChannelHandler;

import static com.laputa.core.http.Response.ok;
import static com.laputa.utils.AdminHttpUtil.convertMapToPair;
import static com.laputa.utils.AdminHttpUtil.sort;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 09.12.15.
 */
@Path("/hardwareInfo")
@ChannelHandler.Sharable
public class HardwareStatsLogic extends CookiesBaseHttpHandler {

    private final UserDao userDao;

    public HardwareStatsLogic(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.userDao = holder.userDao;
    }

    @GET
    @Path("/version")
    public Response getLibraryVersion(@QueryParam("_sortField") String sortField,
                                           @QueryParam("_sortDir") String sortOrder) {
        return ok(sort(convertMapToPair(userDao.getLibraryVersion()), sortField, sortOrder, true));
    }

    @GET
    @Path("/cpuType")
    public Response getBoards(@QueryParam("_sortField") String sortField,
                                    @QueryParam("_sortDir") String sortOrder) {
        return ok(sort(convertMapToPair(userDao.getCpuType()), sortField, sortOrder));
    }

    @GET
    @Path("/connectionType")
    public Response getFacebookLogins(@QueryParam("_sortField") String sortField,
                              @QueryParam("_sortDir") String sortOrder) {
        return ok(sort(convertMapToPair(userDao.getConnectionType()), sortField, sortOrder));
    }

    @GET
    @Path("/boards")
    public Response getHardwareBoards(@QueryParam("_sortField") String sortField,
                                      @QueryParam("_sortDir") String sortOrder) {
        return ok(sort(convertMapToPair(userDao.getHardwareBoards()), sortField, sortOrder));
    }

}
