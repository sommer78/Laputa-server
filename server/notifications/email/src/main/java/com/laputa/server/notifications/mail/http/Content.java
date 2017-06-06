package com.laputa.server.notifications.mail.http;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 14.09.16.
 */
class Content {

    public final String from;
    public final String subject;
    public final String text;
    public final String html;

    public Content(String from, String subject, String body, boolean isHtml) {
        this.from = from;
        this.subject = subject;
        if (isHtml) {
            this.html = body;
            this.text = null;
        } else {
            this.text = body;
            this.html = null;
        }
    }

}
