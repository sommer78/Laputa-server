package com.laputa.server.api.http.logic;

import com.laputa.core.http.MediaType;
import com.laputa.core.http.Response;
import com.laputa.core.http.TokenBaseHttpHandler;
import com.laputa.core.http.annotation.*;
import com.laputa.server.Holder;
import com.laputa.server.api.http.pojo.EmailPojo;
import com.laputa.server.api.http.pojo.PinData;
import com.laputa.server.api.http.pojo.PushMessagePojo;
import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.dao.*;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.Pin;
import com.laputa.server.core.model.PinStorageKey;
import com.laputa.server.core.model.auth.Session;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.enums.PinType;
import com.laputa.server.core.model.widgets.MultiPinWidget;
import com.laputa.server.core.model.widgets.OnePinWidget;
import com.laputa.server.core.model.widgets.Widget;
import com.laputa.server.core.model.widgets.notifications.Mail;
import com.laputa.server.core.model.widgets.notifications.Notification;
import com.laputa.server.core.model.widgets.others.rtc.RTC;
import com.laputa.server.core.processors.EventorProcessor;
import com.laputa.server.core.protocol.exceptions.IllegalCommandBodyException;
import com.laputa.server.core.protocol.exceptions.NoDataException;
import com.laputa.server.core.stats.GlobalStats;
import com.laputa.server.notifications.mail.MailWrapper;
import com.laputa.server.notifications.push.GCMWrapper;
import com.laputa.utils.JsonParser;
import com.laputa.utils.StringUtils;
import io.netty.channel.ChannelHandler;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;

import static com.laputa.core.http.Response.ok;
import static com.laputa.core.http.Response.redirect;
import static com.laputa.server.core.protocol.enums.Command.*;
import static com.laputa.utils.StringUtils.BODY_SEPARATOR;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 25.12.15.
 */
@Path("/")
@ChannelHandler.Sharable
public class HttpAPILogic extends TokenBaseHttpHandler {

    private static final Logger log = LogManager.getLogger(HttpAPILogic.class);
    private final BlockingIOProcessor blockingIOProcessor;
    private final MailWrapper mailWrapper;
    private final GCMWrapper gcmWrapper;
    private final ReportingDao reportingDao;
    private final EventorProcessor eventorProcessor;

    public HttpAPILogic(Holder holder) {
        this(holder.tokenManager, holder.sessionDao, holder.blockingIOProcessor,
                holder.mailWrapper, holder.gcmWrapper, holder.reportingDao,
                holder.stats, holder.eventorProcessor);
    }

    private HttpAPILogic(TokenManager tokenManager, SessionDao sessionDao, BlockingIOProcessor blockingIOProcessor,
                         MailWrapper mailWrapper, GCMWrapper gcmWrapper, ReportingDao reportingDao,
                         GlobalStats globalStats, EventorProcessor eventorProcessor) {
        super(tokenManager, sessionDao, globalStats, "");
        this.blockingIOProcessor = blockingIOProcessor;
        this.mailWrapper = mailWrapper;
        this.gcmWrapper = gcmWrapper;
        this.reportingDao = reportingDao;
        this.eventorProcessor = eventorProcessor;
    }

    private static String makeBody(DashBoard dash, int deviceId, byte pin, PinType pinType, String pinValue) {
        Widget widget = dash.findWidgetByPin(deviceId, pin, pinType);
        if (widget == null) {
            return Pin.makeHardwareBody(pinType, pin, pinValue);
        } else {
            if (widget instanceof OnePinWidget) {
                return ((OnePinWidget) widget).makeHardwareBody();
            } else {
                return ((MultiPinWidget) widget).makeHardwareBody(pin, pinType);
            }
        }
    }

    @GET
    @Path("{token}/project")
    public Response getDashboard(@PathParam("token") String token) {
        globalStats.mark(HTTP_GET_PROJECT);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;

        DashBoard dashBoard = user.profile.getDashById(dashId);

        return ok(dashBoard.toString());
    }

    @GET
    @Path("{token}/isHardwareConnected")
    public Response isHardwareConnected(@PathParam("token") String token) {
        globalStats.mark(HTTP_IS_HARDWARE_CONNECTED);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;
        final int deviceId = tokenValue.deviceId;

        final Session session = sessionDao.userSession.get(new UserKey(user));

        return ok(session.isHardwareConnected(dashId, deviceId));
    }

    @GET
    @Path("{token}/isAppConnected")
    public Response isAppConnected(@PathParam("token") String token) {
        globalStats.mark(HTTP_IS_APP_CONNECTED);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;

        final DashBoard dashBoard = user.profile.getDashById(dashId);

        final Session session = sessionDao.userSession.get(new UserKey(user));

        return ok(dashBoard.isActive && session.isAppConnected());
    }

    @GET
    @Path("{token}/get/{pin}")
    public Response getWidgetPinDataNew(@PathParam("token") String token,
                                     @PathParam("pin") String pinString) {
        return getWidgetPinData(token, pinString);
    }

    //todo old API.
    @GET
    @Path("{token}/pin/{pin}")
    public Response getWidgetPinData(@PathParam("token") String token,
                                     @PathParam("pin") String pinString) {

        globalStats.mark(HTTP_GET_PIN_DATA);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;
        final int deviceId = tokenValue.deviceId;

        DashBoard dashBoard = user.profile.getDashById(dashId);

        PinType pinType;
        byte pin;

        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = Byte.parseByte(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        Widget widget = dashBoard.findWidgetByPin(deviceId, pin, pinType);

        if (widget == null) {
            String value = dashBoard.pinsStorage.get(new PinStorageKey(deviceId, pinType, pin));
            if (value == null) {
                log.debug("Requested pin {} not found. User {}", pinString, user.email);
                return Response.badRequest("Requested pin not exists in app.");
            }
            return ok(JsonParser.valueToJsonAsString(value.split(StringUtils.BODY_SEPARATOR_STRING)));
        }

        return ok(widget.getJsonValue());
    }

    @GET
    @Path("{token}/rtc")
    public Response getWidgetPinData(@PathParam("token") String token) {
        globalStats.mark(HTTP_GET_PIN_DATA);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;

        DashBoard dashBoard = user.profile.getDashById(dashId);

        RTC rtc = dashBoard.getWidgetByType(RTC.class);


        if (rtc == null) {
            log.debug("Requested rtc widget not found. User {}", user.email);
            return Response.badRequest("Requested rtc not exists in app.");
        }

        return ok(rtc.getJsonValue());
    }

    @GET
    @Path("{token}/qr")
    //todo cover with test
    public Response getQR(@PathParam("token") String token) {
        globalStats.mark(HTTP_QR);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;

        DashBoard dashBoard = user.profile.getDashById(dashId);

        try {
            byte[] compressed = JsonParser.gzipDashRestrictive(dashBoard);
            String qrData = "bp1" + Base64.getEncoder().encodeToString(compressed);
            byte[] qrDataBinary = QRCode.from(qrData).to(ImageType.PNG).withSize(500, 500).stream().toByteArray();
            return ok(qrDataBinary, "image/png");
        } catch (Throwable e) {
            log.error("Error generating QR. Reason : {}", e.getMessage());
            return Response.badRequest("Error generating QR.");
        }
    }

    @GET
    @Path("{token}/data/{pin}")
    public Response getPinHistoryData(@PathParam("token") String token,
                                      @PathParam("pin") String pinString) {
        globalStats.mark(HTTP_GET_HISTORY_DATA);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;
        final int deviceId = tokenValue.deviceId;

        PinType pinType;
        byte pin;

        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = Byte.parseByte(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        //todo may be optimized
        try {
            java.nio.file.Path path = reportingDao.csvGenerator.createCSV(user, dashId, deviceId, pinType, pin);
            return redirect("/" + path.getFileName().toString());
        } catch (IllegalCommandBodyException e1) {
            log.debug(e1.getMessage());
            return Response.badRequest(e1.getMessage());
        } catch (NoDataException noData) {
            log.debug("No data for pin.");
            return Response.badRequest("No data for pin.");
        } catch (Exception e) {
            log.debug("Error getting pin data.");
            return Response.badRequest("Error getting pin data.");
        }
    }

    public Response updateWidgetProperty(String token,
                                         String pinString,
                                         String property,
                                         String... values) {
        globalStats.mark(HTTP_UPDATE_PIN_DATA);

        if (values.length == 0) {
            log.debug("No properties for update provided.");
            return Response.badRequest("No properties for update provided.");
        }

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;
        final int deviceId = tokenValue.deviceId;

        DashBoard dash = user.profile.getDashById(dashId);

        //todo add test for this use case
        if (!dash.isActive) {
            return Response.badRequest("Project is not active.");
        }

        PinType pinType;
        byte pin;
        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = Byte.parseByte(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        //for now supporting only virtual pins
        Widget widget = dash.findWidgetByPin(deviceId, pin, pinType);

        if (widget == null || pinType != PinType.VIRTUAL) {
            log.debug("No widget for SetWidgetProperty command.");
            return Response.badRequest("No widget for SetWidgetProperty command.");
        }

        try {
            //todo for now supporting only single property
            widget.setProperty(property, values[0]);
        } catch (Exception e) {
            log.debug("Error setting widget property. Reason : {}", e.getMessage());
            return Response.badRequest("Error setting widget property.");
        }

        Session session = sessionDao.userSession.get(new UserKey(user));
        session.sendToApps(SET_WIDGET_PROPERTY, 111, dash.id, deviceId, "" + pin + BODY_SEPARATOR + property + BODY_SEPARATOR + values[0]);
        return Response.ok();
    }

    //todo it is a bit ugly right now. could be simplified by passing map of query params.
    @GET
    @Path("{token}/update/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response updateWidgetPinDataViaGet(@PathParam("token") String token,
                                              @PathParam("pin") String pinString,
                                              @QueryParam("value") String[] pinValues,
                                              @QueryParam("label") String labelValue,
                                              @QueryParam("labels") String labelsValue,
                                              @QueryParam("color") String colorValue,
                                              @QueryParam("onLabel") String onLabelValue,
                                              @QueryParam("offLabel") String offLabelValue,
                                              @QueryParam("isOnPlay") String isOnPlay) {

        if (pinValues != null) {
            return updateWidgetPinData(token, pinString, pinValues);
        }
        if (labelValue != null) {
            return updateWidgetProperty(token, pinString, "label", labelValue);
        }
        if (labelsValue != null) {
            return updateWidgetProperty(token, pinString, "labels", labelsValue);
        }
        if (colorValue != null) {
            return updateWidgetProperty(token, pinString, "color", colorValue);
        }
        if (onLabelValue != null) {
            return updateWidgetProperty(token, pinString, "onLabel", onLabelValue);
        }
        if (offLabelValue != null) {
            return updateWidgetProperty(token, pinString, "offLabel", offLabelValue);
        }
        if (isOnPlay != null) {
            return updateWidgetProperty(token, pinString, "isOnPlay", isOnPlay);
        }

        return Response.badRequest("Wrong request format.");
    }

    @PUT
    @Path("{token}/update/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response updateWidgetPinDataNew(@PathParam("token") String token,
                                        @PathParam("pin") String pinString,
                                        String[] pinValues) {
        return updateWidgetPinData(token, pinString, pinValues);
    }

    //todo remove later?
    @PUT
    @Path("{token}/pin/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response updateWidgetPinData(@PathParam("token") String token,
                                        @PathParam("pin") String pinString,
                                        String[] pinValues) {

        globalStats.mark(HTTP_UPDATE_PIN_DATA);

        if (pinValues.length == 0) {
            log.debug("No pin for update provided.");
            return Response.badRequest("No pin for update provided.");
        }

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;
        final int deviceId = tokenValue.deviceId;

        DashBoard dash = user.profile.getDashById(dashId);

        PinType pinType;
        byte pin;

        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = Byte.parseByte(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        final long now = System.currentTimeMillis();

        String pinValue = String.join(StringUtils.BODY_SEPARATOR_STRING, pinValues);

        reportingDao.process(user, dashId, deviceId, pin, pinType, pinValue, now);

        dash.update(deviceId, pin, pinType, pinValue, now);

        String body = makeBody(dash, deviceId, pin, pinType, pinValue);

        Session session = sessionDao.userSession.get(new UserKey(user));
        if (session == null) {
            log.debug("No session for user {}.", user.email);
            return Response.ok();
        }

        eventorProcessor.process(session, dash, deviceId, pin, pinType, pinValue, now);

        session.sendMessageToHardware(dashId, HARDWARE, 111, body, deviceId);

        if (dash.isActive) {
            session.sendToApps(HARDWARE, 111, dashId, deviceId, body);
        }

        return Response.ok();
    }

    @PUT
    @Path("{token}/extra/pin/{pin}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response updateWidgetPinData(@PathParam("token") String token,
                                        @PathParam("pin") String pinString,
                                        PinData[] pinsData) {

        globalStats.mark(HTTP_UPDATE_PIN_DATA);

        if (pinsData.length == 0) {
            log.debug("No pin for update provided.");
            return Response.badRequest("No pin for update provided.");
        }

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;
        final int deviceId = tokenValue.deviceId;

        DashBoard dash = user.profile.getDashById(dashId);

        PinType pinType;
        byte pin;

        try {
            pinType = PinType.getPinType(pinString.charAt(0));
            pin = Byte.parseByte(pinString.substring(1));
        } catch (NumberFormatException | IllegalCommandBodyException e) {
            log.debug("Wrong pin format. {}", pinString);
            return Response.badRequest("Wrong pin format.");
        }

        for (PinData pinData : pinsData) {
            reportingDao.process(user, dashId, deviceId, pin, pinType, pinData.value, pinData.timestamp);
        }

        final long now = System.currentTimeMillis();
        dash.update(deviceId, pin, pinType, pinsData[0].value, now);

        String body = makeBody(dash, deviceId, pin, pinType, pinsData[0].value);

        if (body != null) {
            Session session = sessionDao.userSession.get(new UserKey(user));
            if (session == null) {
                log.error("No session for user {}.", user.email);
                return Response.ok();
            }
            session.sendMessageToHardware(dashId, HARDWARE, 111, body, deviceId);

            if (dash.isActive) {
                session.sendToApps(HARDWARE, 111, dashId, deviceId, body);
            }
        }

        return Response.ok();
    }

    @POST
    @Path("{token}/notify")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response notify(@PathParam("token") String token,
                                        PushMessagePojo message) {

        globalStats.mark(HTTP_NOTIFY);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        final User user = tokenValue.user;
        final int dashId = tokenValue.dashId;

        if (message == null || Notification.isWrongBody(message.body)) {
            log.debug("Notification body is wrong. '{}'", message == null ? "" : message.body);
            return Response.badRequest("Body is empty or larger than 255 chars.");
        }

        DashBoard dash = user.profile.getDashById(dashId);

        if (!dash.isActive) {
            log.debug("Project is not active.");
            return Response.badRequest("Project is not active.");
        }

        Notification notification = dash.getWidgetByType(Notification.class);

        if (notification == null || notification.hasNoToken()) {
            log.debug("No notification tokens.");
            if (notification == null) {
                return Response.badRequest("No notification widget.");
            } else {
                return Response.badRequest("Notification widget not initialized.");
            }
        }

        log.trace("Sending push for user {}, with message : '{}'.", user.email, message.body);
        notification.push(gcmWrapper, message.body, dash.id);

        return Response.ok();
    }

    @POST
    @Path("{token}/email")
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response email(@PathParam("token") String token,
                                        EmailPojo message) {

        globalStats.mark(HTTP_EMAIL);

        TokenValue tokenValue = tokenManager.getUserByToken(token);

        if (tokenValue == null) {
            log.debug("Requested token {} not found.", token);
            return Response.badRequest("Invalid token.");
        }

        DashBoard dash = tokenValue.user.profile.getDashById(tokenValue.dashId);

        if (dash == null || !dash.isActive) {
            log.debug("Project is not active.");
            return Response.badRequest("Project is not active.");
        }

        Mail mail = dash.getWidgetByType(Mail.class);

        if (mail == null) {
            log.debug("No email widget.");
            return Response.badRequest("No email widget.");
        }

        if (message == null ||
                message.subj == null || message.subj.isEmpty() ||
                message.to == null || message.to.isEmpty()) {
            log.debug("Email body empty. '{}'", message);
            return Response.badRequest("Email body is wrong. Missing or empty fields 'to', 'subj'.");
        }

        log.trace("Sending Mail for user {}, with message : '{}'.", tokenValue.user.email, message.subj);
        mail(tokenValue.user.email, message.to, message.subj, message.title);

        return Response.ok();
    }

    private void mail(String email, String to, String subj, String body) {
        blockingIOProcessor.execute(() -> {
            try {
                mailWrapper.sendText(to, subj, body);
            } catch (Exception e) {
                log.error("Error sending email from HTTP. From : '{}', to : '{}'. Reason : {}", email, to, e.getMessage());
            }
        });
    }

}
