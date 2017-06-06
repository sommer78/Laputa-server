package com.laputa.utils;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 04.09.16.
 */
public class DateTimeUtils {

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final Calendar UTC_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

}
