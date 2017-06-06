package com.laputa.utils.serialization;

import com.laputa.server.core.model.device.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User who see shared dashboard should not see authentification data of original user
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 08.12.16.
 */
public abstract class DeviceIgnoreMixIn {

    @JsonIgnore
    public String token;

    @JsonIgnore
    public Status status;

    @JsonIgnore
    public long disconnectTime;

    @JsonIgnore
    public String lastLoggedIP;

}
