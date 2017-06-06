package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/23/2015.
 */
public class EnergyLimitException extends BaseServerException {

    public EnergyLimitException(String message) {
        super(message, Response.ENERGY_LIMIT);
    }

}
