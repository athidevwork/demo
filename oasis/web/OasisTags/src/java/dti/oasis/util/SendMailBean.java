package dti.oasis.util;

import java.io.Serializable;

/**
 * JavaBean containing information to send an email
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 24, 2004 
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 4/19/2005        jbe         Add contentType
 *
 * ---------------------------------------------------
*/

public class SendMailBean implements Serializable{
    private String subject;
    private String body;
    private String file;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String smtpHost;
    private String smtpUserId;
    private String smtpPassword;
    private String contentType;

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.SendMailBean");
        buf.append("{subject=").append(subject);
        buf.append(",body=").append(body);
        buf.append(",file=").append(file);
        buf.append(",from=").append(from);
        buf.append(",to=").append(to);
        buf.append(",cc=").append(cc);
        buf.append(",bcc=").append(bcc);
        buf.append(",smtpHost=").append(smtpHost);
        buf.append(",smtpUserId=").append(smtpUserId);
        buf.append(",smtpPassword=").append(smtpPassword);
        buf.append(",contentType=").append(contentType);
        buf.append('}');
        return buf.toString();
    }

    /**
     * Returns the email content type.
     * @return e.g. text/plain or text/html
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the email content type.
     * @param contentType e.g. text/plain or text/html
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns the email Subject
     * @return
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the email subject
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the email body
     * @return
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the email body
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Returns the file or files (as comma separated list) to attach to email
     * @return
     */
    public String getFile() {
        return file;
    }

    /**
     * Set the file or files (as comma separated list) to attach to email
     * @param file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Returns the email address the email is sent from
     * @return
     */
    public String getFrom() {
        return from;
    }

    /**
     * Set the email address to send the email from
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Set the email address or addresses (as comma separated list) to send email to
     * @return
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the email address or addresses (as comma separated list) to send email to
     * @param to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Set the email address or addresses (as comma separated list) to send email to as CC
     * @return
     */
    public String getCC() {
        return cc;
    }

    /**
     * Returns the email address or addresses (as comma separated list) to send email to as CC
     * @param cc
     */
    public void setCC(String cc) {
        this.cc = cc;
    }

    /**
     * Set the email address or addresses (as comma separated list) to send email to as BCC
     * @return
     */
    public String getBCC() {
        return bcc;
    }

    /**
     * Returns the email address or addresses (as comma separated list) to send email to as BCC
     * @param bcc
     */
    public void setBCC(String bcc) {
        this.bcc = bcc;
    }

    /**
     * Returns the SMTP Server IP Address or host name
     * @return
     */
    public String getSmtpHost() {
        return smtpHost;
    }

    /**
     * Set the SMTP Server IP Address or host name
     * @param smtpHost
     */
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    /**
     * Return the user id for the SMTP Server.  This is optional depending on the
     * SMTP Server's configuration.
     * @return
     */
    public String getSmtpUserId() {
        return smtpUserId;
    }

    /**
     * Set the user id for the SMTP Server.  This is optional depending on the
     * SMTP Server's configuration.
     * @param smtpUserId
     */
    public void setSmtpUserId(String smtpUserId) {
        this.smtpUserId = smtpUserId;
    }

    /**
     * Return the user's password for the SMTP Server.  This is optional depending on the
     * SMTP Server's configuration.
     * @return
     */
    public String getSmtpPassword() {
        return smtpPassword;
    }

    /**
     * Set the user's password for the SMTP Server.  This is optional depending on the
     * SMTP Server's configuration.
     * @param smtpPassword
     */
    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    /**
     * Constructor
     * @param smtpHost SMTP Server IP Address or host name
     * @param subject email subject
     * @param body body of email
     * @param from email address to send email from
     * @param to email address or addresses (as comma separated list) to send email to
     */
    public SendMailBean(String smtpHost, String subject, String body, String from, String to) {
        this.smtpHost = smtpHost;
        this.subject = subject;
        this.body = body;
        this.from = from;
        this.to = to;
    }

    /**
     * Constructor
     * @param smtpHost smtpHost SMTP Server IP Address or host name
     * @param subject email subject
     * @param body body of email
     * @param from email address to send email from
     * @param to email address or addresses (as comma separated list) to send email to
     * @param file file or files (as comma separated list) to attach to email
     */
    public SendMailBean(String smtpHost, String subject, String body, String from, String to, String file) {
        this.smtpHost = smtpHost;
        this.subject = subject;
        this.body = body;
        this.from = from;
        this.to = to;
        this.file = file;
    }

    /**
     * Default no-arg Constructor
     */
    public SendMailBean() {
    }
}
