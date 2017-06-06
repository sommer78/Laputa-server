package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/23/2015.
 */
public class NotAllowedException extends BaseServerException {

    public NotAllowedException(String message) {
        super(message, Response.NOT_ALLOWED);
    }

}
