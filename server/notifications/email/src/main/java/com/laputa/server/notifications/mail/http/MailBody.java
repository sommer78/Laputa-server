package com.laputa.server.notifications.mail.http;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 14.09.16.
 */
class MailBody {

    public final Options options;
    public final Content content;
    public final Recipient[] recipients;

    public MailBody(String from, String subj, String body, String to, boolean isHtml) {
        this.content = new Content(from, subj, body, isHtml);
        this.recipients = new Recipient[]{new Recipient(to)};
        this.options = new Options();
    }

}
