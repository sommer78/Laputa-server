package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/3/2015.
 */
public class IllegalCommandException extends BaseServerException {

    public IllegalCommandException(String message) {
        super(message, Response.ILLEGAL_COMMAND);
    }

}
