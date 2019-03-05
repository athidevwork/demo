package dti.oasis.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   November 13, 2017
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class StoredProcedureLogFormatter extends Formatter {
    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
        StringBuffer buf = new StringBuffer();

        // Date
        appendFormattedDate(buf, record.getMillis());

        String message = formatMessage(record);
        buf.append(message);
        buf.append(getLineSeparator());
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                buf.append(sw.toString());
            } catch (Exception ex) {
            }
        }
        return buf.toString();
    }

    /**
     * Format the message string from a log record.
     *
     * @param record the log record containing the raw message
     * @return a localized and formatted message
     */
    public synchronized String formatMessage(LogRecord record) {
        String format = "";
                Object params[] = record.getParameters();
        if(params!=null && params.length>0) {
            for (int i = 0; i < params.length; i++) {
                format = format + "\t" + params[i];
            }
        }

        return format;

    }

    private void appendFormattedDate(StringBuffer buf, long timeInMillis) {
        // Minimize memory allocations here.
        m_date.setTime(timeInMillis);
        m_args[0] = m_date;
        if (m_formatter == null) {
            m_formatter = new MessageFormat(m_dateFormat);
        }
        m_formatter.format(m_args, buf, null);
    }

    protected String getLineSeparator() {
        return m_defaultLineSeparator;
    }

    private Date m_date = new Date();
    private MessageFormat m_formatter;
    private Object m_args[] = new Object[1];


    private static final String m_dateFormat = "{0,date,MMddyyyy} {0,time,HH:mm:ss.SSS zzz}";
    private String m_defaultLineSeparator = System.getProperty("line.separator");

}
