package dti.oasis.util;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Encapsulates JavaMail functionality
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 *         Date:   Jul 7, 2003
 */
 /*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 1/22/2004 jbe   Added main method to allow invocation
 *                 from command line.
 * 2/7/2004  jbe   Added Logging
 * 5/24/2004 jbe   Added use of SendMailBean as primary parameter
 * 4/19/2005 jbe   Support use of contentType from SendMailBean
 * 01/23/2007 wer  Changed usage of new Boolean(x) in logging to String.valueOf(x);
 * ---------------------------------------------------
 *
 */

public class SendMail {

    protected static void sendError() {
        System.err.print("Usage: java ");
        System.err.print(SendMail.class.getName());
        System.err.print(" -host:hostname -from:fromEmail -to:toEmail -subject:\"Subject\" ");
        System.err.print(" -body:\"body text\" -bodyfile:\"filename\" -debug:true/false ");
        System.err.println("-attach:\"comma separated file list\" ");
        System.err.print("\te.g. java SendMail -host:10.1.12.12 -from:ted@mail.com -to:alice@aol.com -subject:\"Hello\" ");
        System.err.println("-body:\"How Are you?\" -debug:false -attach:\"c:\\temp\\file.txt,c:\\temp\\file2.txt\"");
        System.err.println("OR:");
        System.err.print("\te.g. java SendMail -host:10.1.12.12 -from:ted@mail.com -to:alice@aol.com -subject:\"Hello\" ");
        System.err.println("-bodyfile:c:\\temp\\body.txt -debug:false -attach:\"c:\\temp\\file.txt,c:\\temp\\file2.txt\"");
        System.exit(-1);
    }

    protected static String getBody(String file) throws IOException {
        BufferedReader buff = null;
        StringBuffer body = new StringBuffer();
        try {
            buff = new BufferedReader(new FileReader(file));
            do {
                String line = buff.readLine();
                if (line == null)
                    break;
                body.append(line).append(System.getProperty("line.separator"));
            } while (true);

            return body.toString();
        }
        finally {
            if (buff != null) buff.close();
        }
    }

    public static void main(String[] args) {
        int sz = args.length;
        String body = null;
        String bodyFile = null;
        String subject = null;
        String attach = null;
        String to = null;
        String from = null;
        String host = null;
        String debug = null;

        for (int i = 0; i < sz; i++) {
            if (args[i].startsWith("-body:"))
                body = args[i].substring(6);
            if (args[i].startsWith("-subject:"))
                subject = args[i].substring(9);
            if (args[i].startsWith("-bodyfile:"))
                bodyFile = args[i].substring(10);
            if (args[i].startsWith("-attach:"))
                attach = args[i].substring(8);
            if (args[i].startsWith("-to:"))
                to = args[i].substring(4);
            if (args[i].startsWith("-from:"))
                from = args[i].substring(6);
            if (args[i].startsWith("-host:"))
                host = args[i].substring(6);
            if (args[i].startsWith("-debug:"))
                debug = args[i].substring(7);
        }
        if (StringUtils.isBlank(to) || StringUtils.isBlank(from) || StringUtils.isBlank(host)
                || (StringUtils.isBlank(body) && StringUtils.isBlank(bodyFile))) {
            sendError();
            return;
        }
        if (StringUtils.isBlank(debug))
            debug = "false";

        try {
            if (body == null)
                body = getBody(bodyFile);

            if (StringUtils.isBlank(attach))
                sendMail(host, from, to, subject, body, Boolean.valueOf(debug).booleanValue());

            else
                sendMail(host, from, to, subject, body, attach, Boolean.valueOf(debug).booleanValue());
            System.exit(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Sends an email
     * @param bean
     * @param debug
     * @throws MessagingException
     */
    public static void sendMail(SendMailBean bean, boolean debug) throws MessagingException {
        Logger l = LogUtils.enterLog(SendMail.class, "sendMail", new Object[]{bean, String.valueOf(debug)});
        boolean auth = false;
        Transport t = null;
        if (bean.getSmtpHost() == null || bean.getSmtpHost().trim().equals(""))
            throw new MessagingException("Missing SMTP Server");

        if (bean.getTo() == null || bean.getTo().trim().equals(""))
            throw new MessagingException("Missing To Email Address");

        Properties props = new Properties();
        // set smtp host & build mail session
        props.put("mail.smtp.host", bean.getSmtpHost());
        if (!StringUtils.isBlank(bean.getSmtpUserId()) &&
                !StringUtils.isBlank(bean.getSmtpPassword())) {
            props.put("mail.smtp.auth", "true");
            auth = true;
        }
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(debug);

        // create mail msg
        MimeMessage msg = new MimeMessage(session);
        // from address
        msg.setFrom(new InternetAddress(bean.getFrom()));
        // create & parse to email addresses
        InternetAddress[] address = InternetAddress.parse(bean.getTo(), true);
        msg.setRecipients(Message.RecipientType.TO, address);
        if (!StringUtils.isBlank(bean.getCC())) {
            InternetAddress[] cc = InternetAddress.parse(bean.getCC(), true);
            msg.setRecipients(Message.RecipientType.CC, cc);
        }
        if (!StringUtils.isBlank(bean.getBCC())) {
            InternetAddress[] bcc = InternetAddress.parse(bean.getBCC(), true);
            msg.setRecipients(Message.RecipientType.BCC, bcc);
        }

        // set subject, date & text
        msg.setSubject(bean.getSubject());
        msg.setSentDate(new Date());
        // Message Part
        MimeBodyPart part1 = new MimeBodyPart();
        // if we have a content type, use it
        if(!StringUtils.isBlank(bean.getContentType()))
            part1.setContent(bean.getBody(),bean.getContentType());
        else
            part1.setText(bean.getBody());

        // The multipart
        Multipart mPart = new MimeMultipart();
        // Add the message in
        mPart.addBodyPart(part1);
        // comma separated list of files
        if (!StringUtils.isBlank(bean.getFile())) {
            StringTokenizer tok = new StringTokenizer(bean.getFile(), ",");
            while (tok.hasMoreTokens()) {
                String fName = tok.nextToken();
                // Create part for file attachment
                MimeBodyPart part = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(fName);
                part.setDataHandler(new DataHandler(fds));
                part.setFileName(fds.getFile().getName());
                // add to multipart
                mPart.addBodyPart(part);
            }
        }
        // set the multipart into the msg
        msg.setContent(mPart);
        if (auth) {

            t = session.getTransport("smtp");
            t.connect(bean.getSmtpHost(), bean.getSmtpUserId(), bean.getSmtpPassword());
            try {
                msg.saveChanges();
                t.sendMessage(msg, msg.getAllRecipients());
            }
            finally {
                t.close();
            }

        }
        else
        // send mail
            Transport.send(msg);
        l.exiting(SendMail.class.getName(), "sendMail");

    }

    /**
     * Sends an email w/ no attachments
     *
     * @param smtpHost
     * @param mailFrom
     * @param mailTo   can be comma separated list of addresses
     * @param subject
     * @param body
     * @param debug
     * @throws MessagingException
     */
    public static void sendMail(String smtpHost, String mailFrom, String mailTo,
                                String subject, String body, boolean debug) throws MessagingException {
        Logger l = LogUtils.enterLog(SendMail.class, "sendMail", new Object[]
            {smtpHost, mailFrom, mailTo, subject, body, String.valueOf(debug)});
        sendMail(new SendMailBean(smtpHost,subject,body,mailFrom,mailTo),debug);
        l.exiting(SendMail.class.getName(), "sendMail");
    }

    /**
     * Sends an email
     *
     * @param smtpHost
     * @param mailFrom
     * @param mailTo   can be comma separated list of addresses
     * @param subject
     * @param body
     * @param fileName can be comma separated list of files
     * @param debug
     * @throws MessagingException
     */
    public static void sendMail(String smtpHost, String mailFrom, String mailTo,
                                String subject, String body, String fileName, boolean debug) throws MessagingException {
        Logger l = LogUtils.enterLog(SendMail.class, "sendMail", new Object[]
            {smtpHost, mailFrom, mailTo, subject, body, fileName, String.valueOf(debug)});
        sendMail(new SendMailBean(smtpHost,subject,body,mailFrom,mailTo,fileName),debug);
        l.exiting(SendMail.class.getName(), "sendMail");
    }

}
