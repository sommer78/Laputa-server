package com.laputa.utils;

import java.util.UUID;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 12.05.16.
 */
public class TokenGeneratorUtil {

    public static String generateNewToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
