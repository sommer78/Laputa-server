package com.laputa.server.hardware.exceptions;

import com.laputa.server.core.protocol.enums.Response;
import com.laputa.server.core.protocol.exceptions.BaseServerException;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/3/2015.
 */
public class NotifNotAuthorizedException extends BaseServerException {

    public NotifNotAuthorizedException(String message) {
        super(message, Response.NOTIFICATION_NOT_AUTHORIZED);
    }

}
