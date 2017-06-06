package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/3/2015.
 */
public class GetGraphDataException extends BaseServerException {

    public GetGraphDataException() {
        super("Server exception!", Response.GET_GRAPH_DATA);
    }

}
