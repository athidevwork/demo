package dti.oasis.data;

import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class formats a db procedure or result set column name into a XML name format.
 * Unless otherwise specified, the column name is converted to all lower case,
 * and all '_' characters are replaced with '-'.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 26, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class XMLColumnNameToJavaFieldNameFormatter implements JavaFieldNameFormatter {

    /**
     * Format a db procedure column name into a Java Field name format.
     * First, unless otherwise specified, any '_FK' or '_PK' suffix is replaced with '_ID'
     * Next, if requested, any specified prefix strings will be stripped from the front of the column name.
     * The prefix strings consists of a comma delimited list of prefixes.
     * Lastly, unless otherwise specified, the column name is converted to all lower case,
     * all '_' characters will be removed, and the following character will be capitalized.
     */
    public String format(String columnName) {
        // Surround with if check because this is used frequently, and may cause performance problems
        if (l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "format", new Object[]{columnName});
        }

        if (lowercaseAndChangeUnderscoreToDash()) {
            columnName = StringUtils.replace(columnName.toLowerCase(), "_", "-");
        }

        if (l.isLoggable(Level.FINE)) {
            l.exiting(getClass().getName(), "format", columnName);
        }
        return columnName;
    }

    public boolean lowercaseAndChangeUnderscoreToDash() {
        return m_lowercaseAndChangeUnderscoreToDash;
    }

    public void setLowercaseAndChangeUnderscoreToDash(boolean lowercaseAndChangeUnderscoreToDash) {
        m_lowercaseAndChangeUnderscoreToDash = lowercaseAndChangeUnderscoreToDash;
    }

    private boolean m_lowercaseAndChangeUnderscoreToDash = true;
    private final Logger l = LogUtils.getLogger(getClass());
}
