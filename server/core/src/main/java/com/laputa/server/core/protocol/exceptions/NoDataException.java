package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/3/2015.
 */
public class NoDataException extends BaseServerException {

    public NoDataException() {
        super("No Data", Response.NO_DATA);
    }

}
