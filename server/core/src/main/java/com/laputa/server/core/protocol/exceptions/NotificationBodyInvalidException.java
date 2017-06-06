package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/3/2015.
 */
public class NotificationBodyInvalidException extends BaseServerException {

    public NotificationBodyInvalidException() {
        super("Notification message is empty or larger than limit.", Response.NOTIFICATION_INVALID_BODY);
    }

}
