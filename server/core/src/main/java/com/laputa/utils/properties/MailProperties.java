package com.laputa.utils.properties;

import com.laputa.server.notifications.mail.MailWrapper;
import com.laputa.utils.ServerProperties;

import java.util.Map;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 03.01.17.
 */
public class MailProperties extends ServerProperties {

    public MailProperties(Map<String, String> cmdProperties) {
        super(cmdProperties, MailWrapper.MAIL_PROPERTIES_FILENAME);
    }
}
