package com.laputa.server.core.model.widgets.others;

import com.laputa.server.core.model.widgets.Widget;
import com.laputa.server.core.model.widgets.others.rtc.RTC;
import com.laputa.utils.DateTimeUtils;
import com.laputa.utils.JsonParser;
import org.junit.Test;

import java.time.ZoneId;

import static org.junit.Assert.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 30.03.16.
 */
public class RTCSerializationTest {

    @Test
    public void testDeSerializationIsCorrect() {
        String widgetString = "{\"id\":1, \"x\":1, \"y\":1, \"type\":\"RTC\", \"tzName\":\"Australia/Sydney\"}";
        Widget widget = JsonParser.parseWidget(widgetString);

        assertNotNull(widget);

        RTC rtc = (RTC) widget;
        assertNotNull(rtc.tzName);
        assertEquals(ZoneId.of("Australia/Sydney"), rtc.tzName);
    }

    @Test
    public void testDeSerializationIsCorrectForNull() {
        String widgetString = "{\"id\":1, \"x\":1, \"y\":1, \"type\":\"RTC\"}";
        Widget widget = JsonParser.parseWidget(widgetString);

        assertNotNull(widget);

        RTC rtc = (RTC) widget;
        assertNull(rtc.tzName);
    }

    @Test
    public void testSerializationIsCorrect() throws Exception {
        RTC rtc = new RTC();
        rtc.tzName = ZoneId.of("Australia/Sydney");

        String widgetString = JsonParser.mapper.writeValueAsString(rtc);

        assertNotNull(widgetString);
        assertEquals("{\"type\":\"RTC\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isEnabled\":true,\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":0,\"tzName\":\"Australia/Sydney\"}", widgetString);
    }

    @Test
    public void testSerializationIsCorrectUTC() throws Exception {
        RTC rtc = new RTC();
        rtc.tzName = DateTimeUtils.UTC;

        String widgetString = JsonParser.mapper.writeValueAsString(rtc);

        assertNotNull(widgetString);
        assertEquals("{\"type\":\"RTC\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isEnabled\":true,\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":0,\"tzName\":\"UTC\"}", widgetString);
    }

    @Test
    public void testSerializationIsCorrectForNull() throws Exception {
        RTC rtc = new RTC();
        rtc.tzName = null;

        String widgetString = JsonParser.mapper.writeValueAsString(rtc);

        assertNotNull(widgetString);
        assertEquals("{\"type\":\"RTC\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isEnabled\":true,\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":0}", widgetString);
    }

}
