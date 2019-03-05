package dti.oasis.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Print the Log similar to the java.util.logging.SimpleFormatter, with the application name added after the date.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 10, 2006
 *
 * @author wreeder
 * @see java.util.logging.SimpleFormatter
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SingleLineLogFormatter extends Formatter {


    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
        StringBuffer buf = new StringBuffer();

        // Date
        buf.append(SECTION_PREFIX);
        appendFormattedDate(buf, record.getMillis());
        buf.append(SECTION_SUFFIX);

        // Level
        buf.append(SECTION_PREFIX).append(record.getLevel().getLocalizedName()).append(SECTION_SUFFIX);

        // Class Name
        buf.append(SECTION_PREFIX);
        if (record.getSourceClassName() != null) {
            buf.append(record.getSourceClassName());
        } else {
            buf.append(record.getLoggerName());
        }

        // Method Name
        if (record.getSourceMethodName() != null) {
            buf.append(" ");
            buf.append(record.getSourceMethodName());
        }
        buf.append(SECTION_SUFFIX);

        String message = formatMessage(record);
        buf.append(SECTION_PREFIX).append(message).append(SECTION_SUFFIX);
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


    private static final String SECTION_PREFIX = "<";
    private static final String SECTION_SUFFIX = "> ";
//    private static final String m_dateFormat = "{0,date} {0,time,HH:mm:ss aa zzz}";
    private static final String m_dateFormat = "{0,date} {0,time,HH:mm:ss.SSS aa zzz}"; // Add milliseconds

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SingleLineLogFormatter was created.
    private String m_defaultLineSeparator = System.getProperty("line.separator");
}
