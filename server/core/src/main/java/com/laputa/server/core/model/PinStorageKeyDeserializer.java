package com.laputa.server.core.model;

import com.laputa.server.core.model.enums.PinType;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.StringUtils;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 19.11.16.
 */
public class PinStorageKeyDeserializer extends KeyDeserializer {

    @Override
    public PinStorageKey deserializeKey(String key, DeserializationContext ctx) throws IOException {
        //parsing "123-v24"
        int indexOf = key.indexOf(StringUtils.DEVICE_SEPARATOR);

        int deviceId = ParseUtil.parseInt(key.substring(0, indexOf));
        PinType pinType = PinType.getPinType(key.charAt(indexOf + 1));
        byte pin = ParseUtil.parseByte(key.substring(indexOf + 2, key.length()));
        return new PinStorageKey(deviceId, pinType, pin);
    }
}
