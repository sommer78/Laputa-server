package com.laputa.server.core.model.widgets.others.rtc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZoneId;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 04.09.16.
 */
public class StringToZoneId extends JsonDeserializer<ZoneId> {

    @Override
    public ZoneId deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return ZoneId.of(p.readValueAs(String.class));
    }

}
