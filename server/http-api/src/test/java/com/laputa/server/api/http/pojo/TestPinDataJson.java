package com.laputa.server.api.http.pojo;

import com.laputa.utils.JsonParser;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 31.08.16.
 */
public class TestPinDataJson {

    @Test
    public void testParseString() throws Exception {
        String pinDataString = "[{\"timestamp\" : 123, \"value\":\"100\"}]";
        PinData[] pinData = JsonParser.mapper.readValue(pinDataString, PinData[].class);
        assertNotNull(pinData);
    }

}
