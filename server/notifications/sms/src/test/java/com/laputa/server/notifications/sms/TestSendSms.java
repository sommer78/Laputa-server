package com.laputa.server.notifications.sms;

import org.junit.Ignore;
import org.junit.Test;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 19.03.16.
 */
@Ignore
public class TestSendSms {

    @Test
    public void testSend() throws Exception {
        SMSWrapper smsWrapper = new SMSWrapper(null, null);
        smsWrapper.send("", "Hello!!!!!!!!");
    }

}
