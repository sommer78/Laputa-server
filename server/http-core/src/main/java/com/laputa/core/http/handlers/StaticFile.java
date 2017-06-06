package com.laputa.core.http.handlers;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 11.07.16.
 */
public class StaticFile {

    public final String path;

    public final boolean doCaching;

    public StaticFile(String path) {
        this.path = path;
        this.doCaching = false;
    }

    public StaticFile(String path, boolean doCaching) {
        this.path = path;
        this.doCaching = doCaching;
    }

    public boolean isStatic(String url) {
        return url.startsWith(path);
    }
}
