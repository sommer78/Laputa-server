package com.laputa.utils.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.ConcurrentMap;

/**
 * User who see shared dashboard should not see authentification data of original user
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 08.12.16.
 */
public abstract class NotificationIgnoreMixIn {

    @JsonIgnore
    public ConcurrentMap<String, String> androidTokens;

    @JsonIgnore
    public ConcurrentMap<String, String> iOSTokens;

}
