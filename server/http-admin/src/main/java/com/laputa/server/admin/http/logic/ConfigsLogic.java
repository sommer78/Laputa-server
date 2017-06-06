package com.laputa.server.admin.http.logic;

import com.laputa.core.http.CookiesBaseHttpHandler;
import com.laputa.core.http.MediaType;
import com.laputa.core.http.Response;
import com.laputa.core.http.annotation.*;
import com.laputa.server.Holder;
import com.laputa.server.Limits;
import com.laputa.server.db.DBManager;
import com.laputa.server.notifications.mail.MailWrapper;
import com.laputa.server.notifications.push.GCMWrapper;
import com.laputa.utils.FileLoaderUtil;
import com.laputa.utils.JsonParser;
import com.laputa.utils.ServerProperties;
import io.netty.channel.ChannelHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.laputa.core.http.Response.*;
import static com.laputa.utils.AdminHttpUtil.sort;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 09.12.15.
 */
@Path("/config")
@ChannelHandler.Sharable
public class ConfigsLogic extends CookiesBaseHttpHandler {

    private final Limits limits;
    private final ServerProperties serverProperties;

    public ConfigsLogic(Holder holder, String rootPath) {
        super(holder, rootPath);
        this.limits = holder.limits;
        this.serverProperties = holder.props;
    }

    @GET
    @Path("")
    public Response getConfigs(@QueryParam("_filters") String filterParam,
                             @QueryParam("_page") int page,
                             @QueryParam("_perPage") int size,
                             @QueryParam("_sortField") String sortField,
                             @QueryParam("_sortDir") String sortOrder) {

        List<Config> configs = new ArrayList<>();
        configs.add(new Config(ServerProperties.SERVER_PROPERTIES_FILENAME));
        configs.add(new Config(MailWrapper.MAIL_PROPERTIES_FILENAME));
        configs.add(new Config(GCMWrapper.GCM_PROPERTIES_FILENAME));
        configs.add(new Config(DBManager.DB_PROPERTIES_FILENAME));
        configs.add(new Config("twitter4j.properties"));
        configs.add(new Config(FileLoaderUtil.TOKEN_MAIL_BODY));

        return appendTotalCountHeader(
                                ok(sort(configs, sortField, sortOrder), page, size), configs.size()
        );
    }

    @GET
    @Path("/{name}")
    public Response getConfigByName(@PathParam("name") String name) {
        switch (name) {
            case FileLoaderUtil.TOKEN_MAIL_BODY:
                return ok(new Config(name, limits.TOKEN_BODY).toString());
            case ServerProperties.SERVER_PROPERTIES_FILENAME :
                return ok(new Config(name, serverProperties).toString());
            default :
                return badRequest();
        }
    }


    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public Response updateConfig(@PathParam("name") String name,
                               Config updatedConfig) {

        log.info("Updating config {}. New body : ", name);
        log.info("{}", updatedConfig.body);

        switch (name) {
            case FileLoaderUtil.TOKEN_MAIL_BODY:
                limits.TOKEN_BODY = updatedConfig.body;
                break;
            case ServerProperties.SERVER_PROPERTIES_FILENAME :
                Properties properties = readPropertiesFromString(updatedConfig.body);
                serverProperties.putAll(properties);
                break;
        }

        return ok(updatedConfig.toString());
    }

    private static Properties readPropertiesFromString(String propertiesAsString) {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(propertiesAsString));
        } catch (IOException e) {
            log.error("Error reading properties as string. {}", e.getMessage());
        }
        return properties;
    }

    /**
     * The Laputa Project.
     * Created by Sommer
     * Created on 04.04.16.
     */
    private static class Config {

        String name;
        String body;

        public Config() {
        }

        public Config(String name) {
            this.name = name;
        }

        public Config(String name, String body) {
            this.name = name;
            this.body = body;
        }

        public Config(String name, ServerProperties serverProperties) {
            this.name = name;
            //return only editable options
            this.body = makeProperties(serverProperties,
                    "allowed.administrator.ips",
                    "user.dashboard.max.limit",
                    "user.profile.max.size");
        }

        private static String makeProperties(ServerProperties properties, String... propertyNames) {
            StringBuilder sb = new StringBuilder();
            for (String name : propertyNames) {
                sb.append(name).append(" = ").append(properties.getProperty(name)).append("\n");
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            try {
                return JsonParser.mapper.writeValueAsString(this);
            } catch (Exception e) {
                return "{}";
            }
        }
    }
}
