package com.laputa.server.core.model.enums;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 12.08.15.
 */
public enum GraphType {

    MINUTE('m', 60 * 1000),
    HOURLY('h', 60 * 60 * 1000),
    DAILY('d', 24 * 60 * 60 * 1000);

    public final char type;
    public final long period;

    GraphType(char type, long period) {
        this.type = type;
        this.period = period;
    }

    public static GraphType getPeriodByType(char type) {
        for (GraphType graphType : values()) {
            if (type == graphType.type) {
                return graphType;
            }
        }
        return null;
    }

}
