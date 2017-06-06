package com.laputa.utils;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.11.15.
 */
public class PinUtil {

    public static boolean isReadOperation(String body) {
        return body.length() > 1 && body.charAt(1) == 'r';
    }
}
