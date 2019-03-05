package dti.oasis.data;

import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class formats a db procedure or result set column name into a Java Field name format.
 * First, unless otherwise specified, any '_FK' or '_PK' suffix is replaced with '_ID'
 * Next, if requested, any specified prefix strings will be stripped from the front of the column name.
 * The prefix strings consists of a comma delimited list of prefixes.
 * Lastly, unless otherwise specified, the column name is converted to all lower case,
 * all '_' characters will be removed, and the following character will be capitalized.
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
public class ColumnNameToJavaFieldNameFormatter implements JavaFieldNameFormatter {

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

        // Replace the _FK and _PK suffixes with _ID
        if (replacePkFkWithId()) {
            if (columnName.endsWith("_FK") || columnName.endsWith("_PK")) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "format", "Replacing _FK or _PK suffix with _ID.");
                }
                columnName = columnName.substring(0, columnName.length()-3) + "_ID";
            }
        }

        // Strip any unwanted prefixes.
        if (hasPrefixesToStrip()) {
            for(int i = 0; i < m_stripPrefixes.length; i++) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "format", "Checking for prefix: '"+m_stripPrefixes[i]+"'");
                }
                if (columnName.startsWith(m_stripPrefixes[i])) {
                    columnName = columnName.substring(m_stripPrefixes[i].length());
                }
            }
        }

        // Remove the '_' and capitalize the following character
        if (removeUnderscoreAndCapitalize()) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "format", "Removing underscores and capitalizing the following character, "
                    + (capitalizeFirstCharacter() ? "capitalizing the first character." : "leaving the first character lower case."));
            }
            columnName = StringUtils.capitalizeRemovingDelimiter(columnName, capitalizeFirstCharacter());
        }

        if (l.isLoggable(Level.FINE)) {
            l.exiting(getClass().getName(), "format", columnName);
        }
        return columnName;
    }

    public boolean hasPrefixesToStrip() {
        return m_stripPrefixes != null && m_stripPrefixes.length > 0;
    }

    public void setStripPrefixes(String[] stripPrefixes) {
        m_stripPrefixes = stripPrefixes;
        for(int i = 0; i < m_stripPrefixes.length; i++) {
            m_stripPrefixes[i] = m_stripPrefixes[i].toUpperCase();
        }
    }

    public boolean removeUnderscoreAndCapitalize() {
        return m_removeUnderscoreAndCapitalize;
    }

    public void setRemoveUnderscoreAndCapitalize(boolean removeUnderscoreAndCapitalize) {
        m_removeUnderscoreAndCapitalize = removeUnderscoreAndCapitalize;
    }

    public boolean capitalizeFirstCharacter() {
        return m_capitalizeFirstCharacter;
    }

    public void setCapitalizeFirstCharacter(boolean capitalizeFirstCharacter) {
        m_capitalizeFirstCharacter = capitalizeFirstCharacter;
    }

    public boolean replacePkFkWithId() {
        return m_replacePkFkSufixWithId;
    }

    public void setReplacePkFkSufixWithId(boolean replacePkFkSufixWithId) {
        m_replacePkFkSufixWithId = replacePkFkSufixWithId;
    }

    private String[] m_stripPrefixes;
    private boolean m_removeUnderscoreAndCapitalize = true;
    private boolean m_capitalizeFirstCharacter = false;
    private boolean m_replacePkFkSufixWithId = true;
    private final Logger l = LogUtils.getLogger(getClass());
}
