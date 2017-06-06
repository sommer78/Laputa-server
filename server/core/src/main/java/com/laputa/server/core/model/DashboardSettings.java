package com.laputa.server.core.model;

import com.laputa.server.core.model.enums.Theme;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 22.04.17.
 */
public class DashboardSettings {

    public String name;

    public boolean isShared;

    public Theme theme = Theme.Laputa;

    public boolean keepScreenOn;

    public boolean isAppConnectedOn;

}
