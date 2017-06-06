package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/3/2015.
 */
public class InvalidTokenException extends BaseServerException {

    public InvalidTokenException(String message, int msgId) {
        super(message, msgId, Response.INVALID_TOKEN);
    }

}
