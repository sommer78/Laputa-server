package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/3/2015.
 */
public class UnsupportedCommandException extends BaseServerException {

    public UnsupportedCommandException(String message, int msgId) {
        super(message, msgId, Response.ILLEGAL_COMMAND);
    }

}
