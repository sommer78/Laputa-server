package com.laputa.utils.properties;

import com.laputa.server.notifications.push.GCMWrapper;
import com.laputa.utils.ServerProperties;

import java.util.Map;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 03.01.17.
 */
public class GCMProperties extends ServerProperties {

    public GCMProperties(Map<String, String> cmdProperties) {
        super(cmdProperties, GCMWrapper.GCM_PROPERTIES_FILENAME);
    }
}
