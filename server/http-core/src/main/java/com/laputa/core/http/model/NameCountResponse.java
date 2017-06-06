package com.laputa.core.http.model;

import java.util.Map;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 07.12.15.
 */
public class NameCountResponse {

    public final String name;

    public final int count;

    public NameCountResponse(Map.Entry<String, ?> entry) {
        this(entry.getKey(), ((Number) entry.getValue()).intValue());
    }

    public NameCountResponse(String name, int val) {
        this.name = name;
        this.count = val;
    }

}
