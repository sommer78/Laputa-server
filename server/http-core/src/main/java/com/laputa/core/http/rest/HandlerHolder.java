package com.laputa.core.http.rest;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 12.03.17.
 */
public class HandlerHolder {

    public final Handler handler;

    private final Matcher matcher;

    public HandlerHolder(Handler handler, Matcher matcher) {
        this.handler = handler;
        this.matcher = matcher;
    }

    public Map<String, String> extractParameters() {
        return handler.uriTemplate.extractParameters(matcher);
    }
}
