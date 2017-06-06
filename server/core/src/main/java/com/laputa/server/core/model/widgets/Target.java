package com.laputa.server.core.model.widgets;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 05.01.17.
 */
public interface Target {

    int[] getDeviceIds();

    int getDeviceId();

    default boolean isTag() {
        return false;
    }

}
