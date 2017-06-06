package com.laputa.server.core.reporting;

import com.laputa.server.core.model.enums.GraphType;
import com.laputa.server.core.model.enums.PinType;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 23.10.15.
 */
public abstract class GraphPinRequest {

    public int dashId;

    public int deviceId;

    public PinType pinType;

    public byte pin;

    public int count;

    public GraphType type;

}
