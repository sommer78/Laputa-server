package com.laputa.utils.properties;

import com.laputa.server.notifications.sms.SMSWrapper;
import com.laputa.utils.ServerProperties;

import java.util.Map;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 03.01.17.
 */
public class SmsProperties extends ServerProperties {

    public SmsProperties(Map<String, String> cmdProperties) {
        super(cmdProperties, SMSWrapper.SMS_PROPERTIES_FILENAME);
    }
}
