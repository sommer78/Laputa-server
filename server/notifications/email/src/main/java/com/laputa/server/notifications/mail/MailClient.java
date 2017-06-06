package com.laputa.server.notifications.mail;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 14.09.16.
 */
public interface MailClient {

    void sendText(String to, String subj, String body) throws Exception;

    void sendHtml(String to, String subj, String body) throws Exception;

    void sendHtmlWithAttachment(String to, String subj, String body, QrHolder[] attachments) throws Exception;

}
