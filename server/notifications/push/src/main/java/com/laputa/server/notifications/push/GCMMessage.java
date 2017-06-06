package com.laputa.server.notifications.push;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 16.07.15.
 */
public interface GCMMessage {

    String getToken();

    String toJson() throws JsonProcessingException;

}
