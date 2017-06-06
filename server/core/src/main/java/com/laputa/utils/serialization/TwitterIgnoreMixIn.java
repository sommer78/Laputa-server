package com.laputa.utils.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User who see shared dashboard should not see authentification data of original user
 *
 * The Laputa Project.
 * Created by Sommer
 * Created on 08.12.16.
 */
public class TwitterIgnoreMixIn {

    @JsonIgnore
    public String token;

    @JsonIgnore
    public String secret;

    @JsonIgnore
    public String username;

}
