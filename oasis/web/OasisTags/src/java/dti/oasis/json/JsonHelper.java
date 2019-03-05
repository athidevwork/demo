package dti.oasis.json;

import com.github.cliftonlabs.json_simple.Jsoner;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   11/14/2018
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
public class JsonHelper {

    public static final String TAB_IN_SPACES = "    ";

    /**
     *  Default values:
     *      - tabsBeforeProperty:   0
     *      - addNewLine:           true
     */
    public static void addObjectStartTag(PrintWriter out) {
        addObjectStartTag(out, 0, true);
    }
    /**
     *  Default values:
     *      - addNewLine:           true
     */
    public static void addObjectStartTag(PrintWriter out, int tabsBeforeProperty) {
        addObjectStartTag(out, tabsBeforeProperty, true);
    }
    /**
     *  Default values:
     *      - tabsBeforeProperty:   0
     */
    public static void addObjectStartTag(PrintWriter out, boolean addNewLine) {
        addObjectStartTag(out, 0, addNewLine);
    }
    public static void addObjectStartTag(PrintWriter out, int tabsBeforeProperty, boolean addNewLine) {
        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }
        out.print("{");
        if (addNewLine) {
            out.println();
        }
    }

    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     *     - addCommaSeparator:     false
     *     - addSemiColon:          true
     *     - addNewLine:            true
    */
    public static void addObjectEndTag(PrintWriter out) {
        addObjectEndTag(out, 0, false, true, true);
    }
    /**
     *  Default values:
     *     - addCommaSeparator:     false
     *     - addSemiColon:          true
     *     - addNewLine:            true
     */
    public static void addObjectEndTag(PrintWriter out, int tabsBeforeProperty) {
        addObjectEndTag(out, tabsBeforeProperty, false, true, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     *     - addSemiColon:          false
     *     - addNewLine:            true
     */
    public static void addObjectEndTag(PrintWriter out, boolean addCommaSeparator) {
        addObjectEndTag(out, 0, addCommaSeparator, false, true);
    }
    /**
     *  Default values:
     *     - addSemiColon:          false
     *     - addNewLine:            true
     */
    public static void addObjectEndTag(PrintWriter out, int tabsBeforeProperty, boolean addCommaSeparator) {
        addObjectEndTag(out, tabsBeforeProperty, addCommaSeparator, false, true);
    }

    /**
     *  Default values:
     *      - tabsBeforeProperty:   0
     *      - addSemiColon:         false
     */
    public static void addObjectEndTag(PrintWriter out, boolean addCommaSeparator, boolean addNewLine) {
        addObjectEndTag(out, 0, addCommaSeparator, false, addNewLine);
    }
    /**
     *  Default values:
     *      - addSemiColon:         false
     */
    public static void addObjectEndTag(PrintWriter out, int tabsBeforeProperty, boolean addCommaSeparator, boolean addNewLine) {
        addObjectEndTag(out, tabsBeforeProperty, addCommaSeparator, false, addNewLine);
    }
    /**
     *  Default values:
     *      - tabsBeforeProperty:   0
     */
    public static void addObjectEndTag(PrintWriter out, boolean addCommaSeparator, boolean addSemiColon, boolean addNewLine) {
        addObjectEndTag(out, 0, addCommaSeparator, addSemiColon, addNewLine);
    }
    public static void addObjectEndTag(PrintWriter out, int tabsBeforeProperty, boolean addCommaSeparator, boolean addSemiColon, boolean addNewLine) {
        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }
        out.print("}");
        if (addSemiColon) {
            out.print(";");
        }
        else if (addCommaSeparator) {
            out.print(",");
        }
        if (addNewLine) {
            out.println();
        }
    }

    /**
     *  Default values:
     *      - tabsBeforeProperty:   0
     *      - addNewLine:            true
     */
    public static void addArrayStartTag(PrintWriter out) {
        addArrayStartTag(out, 0, true);
    }
    /**
     *  Default values:
     *      - addNewLine:            true
     */
    public static void addArrayStartTag(PrintWriter out, int tabsBeforeProperty) {
        addArrayStartTag(out, tabsBeforeProperty, true);
    }
    /**
     *  Default values:
     *      - tabsBeforeProperty:   0
     */
    public static void addArrayStartTag(PrintWriter out, boolean addNewLine) {
        addArrayStartTag(out, 0, addNewLine);
    }
    public static void addArrayStartTag(PrintWriter out, int tabsBeforeProperty, boolean addNewLine) {
        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }
        out.print("[");
        if (addNewLine) {
            out.println();
        }
    }

    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     *     - addCommaSeparator:     false
     *     - addSemiColon:          false
     *     - addNewLine:            true
     */
    public static void addArrayEndTag(PrintWriter out) {
        addArrayEndTag(out, 0, false, false, true);
    }
    /**
     *  Default values:
     *     - addCommaSeparator:     false
     *     - addSemiColon:          false
     *     - addNewLine:            true
     */
    public static void addArrayEndTag(PrintWriter out, int tabsBeforeProperty) {
        addArrayEndTag(out, tabsBeforeProperty, false, false, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     *     - addSemiColon:          false
     *     - addNewLine:            true
     */
    public static void addArrayEndTag(PrintWriter out, boolean addCommaSeparator) {
        addArrayEndTag(out, 0, addCommaSeparator, false, true);
    }
    /**
     *  Default values:
     *     - addSemiColon:          false
     *     - addNewLine:            true
     */
    public static void addArrayEndTag(PrintWriter out, int tabsBeforeProperty, boolean addCommaSeparator) {
        addArrayEndTag(out, tabsBeforeProperty, addCommaSeparator, false, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     *     - addSemiColon:          false
     */
    public static void addArrayEndTag(PrintWriter out, boolean addCommaSeparator, boolean addNewLine) {
        addArrayEndTag(out, 0, addCommaSeparator, false, addNewLine);
    }
    /**
     *  Default values:
     *     - addSemiColon:          false
     */
    public static void addArrayEndTag(PrintWriter out, int tabsBeforeProperty, boolean addCommaSeparator, boolean addNewLine) {
        addArrayEndTag(out, tabsBeforeProperty, addCommaSeparator, false, addNewLine);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     */
    public static void addArrayEndTag(PrintWriter out, boolean addCommaSeparator, boolean addSemiColon, boolean addNewLine) {
        addArrayEndTag(out, 0, addCommaSeparator, addSemiColon, addNewLine);
    }
    public static void addArrayEndTag(PrintWriter out, int tabsBeforeProperty, boolean addCommaSeparator, boolean addSemiColon, boolean addNewLine) {
        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }
        out.print("]");
        if (addSemiColon) {
            out.print(";");
        }
        else if (addCommaSeparator) {
            out.print(",");
        }
        if (addNewLine) {
            out.println();
        }
    }

    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     *     - addNewLine:            true
     */
    public static void addCommaSeparator(PrintWriter out) {
        addCommaSeparator(out, 0, true);
    }
    /**
     *  Default values:
     *     - addNewLine:            true
     */
    public static void addCommaSeparator(PrintWriter out, int tabsBeforeProperty) {
        addCommaSeparator(out, tabsBeforeProperty, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:    0
     */
    public static void addCommaSeparator(PrintWriter out, boolean addNewLine) {
        addCommaSeparator(out, 0, addNewLine);
    }
    public static void addCommaSeparator(PrintWriter out, int tabsBeforeProperty, boolean addNewLine) {
        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }
        out.print(", ");
        if (addNewLine) {
            out.println();
        }
    }

    // Write a String value
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - addCommaSeparator:         true
     *     - surroundValueWithQuotes:   true
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, String value) {
        _writeProperty(out, name, value, 0, true, true, true);
    }
    /**
     *  Default values:
     *     - addCommaSeparator:         true
     *     - surroundValueWithQuotes:   true
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, String value, int tabsBeforeProperty) {
        _writeProperty(out, name, value, tabsBeforeProperty, true, true, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - surroundValueWithQuotes:   true
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, String value, boolean addCommaSeparator) {
        _writeProperty(out, name, value, 0, addCommaSeparator, true, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - surroundValueWithQuotes:   true
     */
    public static void writeProperty(PrintWriter out, String name, String value, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, value, 0, addCommaSeparator, true, addNewLine);
    }
    /**
     *  Default values:
     *     - surroundValueWithQuotes:   true
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, String value, int tabsBeforeProperty, boolean addCommaSeparator) {
        _writeProperty(out, name, value, tabsBeforeProperty, addCommaSeparator, true, true);
    }
    /**
     *  Default values:
     *     - surroundValueWithQuotes:   true
     */
    public static void writeProperty(PrintWriter out, String name, String value, int tabsBeforeProperty, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, value, tabsBeforeProperty, addCommaSeparator, true, addNewLine);
    }

    public static void writeProperty(PrintWriter out, String name, String value, int tabsBeforeProperty, boolean addCommaSeparator, boolean surroundValueWithQuotes, boolean addNewLine) {
        _writeProperty(out, name, value, tabsBeforeProperty, addCommaSeparator, surroundValueWithQuotes, addNewLine);
    }

    // Write a String[] value
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - addCommaSeparator:         true
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, String[] values) {
        _writeProperty(out, name, values, 0, true, true);
    }
    /**
     *  Default values:
     *     - addCommaSeparator:         true
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, String[] values, int tabsBeforeProperty) {
        _writeProperty(out, name, values, tabsBeforeProperty, true, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - addCommaSeparator:         true
     */
    public static void writeProperty(PrintWriter out, String name, String[] values, boolean addCommaSeparator) {
        _writeProperty(out, name, values, 0, addCommaSeparator, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     */
    public static void writeProperty(PrintWriter out, String name, String[] values, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, values, 0, addCommaSeparator, addNewLine);
    }
    /**
     *  Default values:
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, String[] values, int tabsBeforeProperty, boolean addCommaSeparator) {
        _writeProperty(out, name, values, tabsBeforeProperty, addCommaSeparator, true);
    }
    public static void writeProperty(PrintWriter out, String name, String[] values, int tabsBeforeProperty, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, values, tabsBeforeProperty, addCommaSeparator, addNewLine);
    }

    // Write a boolean value
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - addCommaSeparator:         true
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, boolean value) {
        _writeProperty(out, name, Boolean.toString(value), 0, true, false, true);
    }
    /**
     *  Default values:
     *     - addCommaSeparator:         true
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, boolean value, int tabsBeforeProperty) {
        _writeProperty(out, name, Boolean.toString(value), tabsBeforeProperty, true, false, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, boolean value, boolean addCommaSeparator) {
        _writeProperty(out, name, Boolean.toString(value), 0, addCommaSeparator, false, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - surroundValueWithQuotes:   false
     */
    public static void writeProperty(PrintWriter out, String name, boolean value, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, Boolean.toString(value), 0, addCommaSeparator, false, addNewLine);
    }
    /**
     *  Default values:
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, boolean value, int tabsBeforeProperty, boolean addCommaSeparator) {
        _writeProperty(out, name, Boolean.toString(value), tabsBeforeProperty, addCommaSeparator, false, true);
    }
    /**
     *  Default values:
     *     - surroundValueWithQuotes:   false
     */
    public static void writeProperty(PrintWriter out, String name, boolean value, int tabsBeforeProperty, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, Boolean.toString(value), tabsBeforeProperty, addCommaSeparator, false, addNewLine);
    }

    // Write an int value
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - addCommaSeparator:         true
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, int value) {
        _writeProperty(out, name, Integer.toString(value), 0, true, false, true);
    }
    /**
     *  Default values:
     *     - addCommaSeparator:         true
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, int value, int tabsBeforeProperty) {
        _writeProperty(out, name, Integer.toString(value), tabsBeforeProperty, true, false, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, int value, boolean addCommaSeparator) {
        _writeProperty(out, name, Integer.toString(value), 0, addCommaSeparator, false, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - surroundValueWithQuotes:   false
     */
    public static void writeProperty(PrintWriter out, String name, int value, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, Integer.toString(value), 0, addCommaSeparator, false, addNewLine);
    }
    /**
     *  Default values:
     *     - surroundValueWithQuotes:   false
     *     - addNewLine:                true
     */
    public static void writeProperty(PrintWriter out, String name, int value, int tabsBeforeProperty, boolean addCommaSeparator) {
        _writeProperty(out, name, Integer.toString(value), tabsBeforeProperty, addCommaSeparator, false, true);
    }
    /**
     *  Default values:
     *     - surroundValueWithQuotes:   false
     */
    public static void writeProperty(PrintWriter out, String name, int value, int tabsBeforeProperty, boolean addCommaSeparator, boolean addNewLine) {
        _writeProperty(out, name, Integer.toString(value), tabsBeforeProperty, addCommaSeparator, false, addNewLine);
    }

    // Internal writeProperty implementation
    private static void _writeProperty(PrintWriter out, String name, String value, int tabsBeforeProperty, boolean addCommaSeparator, boolean surroundValueWithQuotes, boolean addNewLine) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(JsonHelper.class.getName(), "_writeProperty", new Object[]{name, value, tabsBeforeProperty, addCommaSeparator});
        }

        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }

        writePropertyName(out, name, true);
        writeValue(out, value, surroundValueWithQuotes);

        if (addCommaSeparator) {
            out.print(",");
        }
        if (addNewLine) {
            out.println();
        }

        l.exiting(JsonHelper.class.getName(), "_writeProperty");
    }

    private static void _writeProperty(PrintWriter out, String name, String[] values, int tabsBeforeProperty, boolean addCommaSeparator, boolean addNewLine) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(JsonHelper.class.getName(), "_writeProperty", new Object[]{name, values, tabsBeforeProperty, addCommaSeparator});
        }

        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }

        writePropertyName(out, name, true);
        out.print("[");
        if (values != null) {
            for (String value: values) {
                writeValue(out, value);
            }
        }
        out.print("]");

        if (addCommaSeparator) {
            out.print(",");
        }
        if (addNewLine) {
            out.println();
        }

        l.exiting(JsonHelper.class.getName(), "_writeProperty");
    }

    private static void writeTabs(PrintWriter out, int tabs) {
        for (int i = 0; i < tabs; i++) {
            out.print(TAB_IN_SPACES);
        }
    }

    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - addColonSeparator:         false
     *     - surroundValueWithQuotes:   true
     *     - escapeValue:               true
     */
    public static void writePropertyName(PrintWriter out, String name) {
        writePropertyName(out, name, 0, false, true, true);
    }
    /**
     *  Default values:
     *     - surroundValueWithQuotes:   true
     *     - escapeValue:               true
     */
    public static void writePropertyName(PrintWriter out, String name, int tabsBeforeProperty, boolean addColonSeparator) {
        writePropertyName(out, name, tabsBeforeProperty, addColonSeparator, true,  true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - surroundValueWithQuotes:   true
     *     - escapeValue:               true
     */
    public static void writePropertyName(PrintWriter out, String name, boolean addColonSeparator) {
        writePropertyName(out, name, 0, addColonSeparator, true,  true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     *     - escapeValue:               true
     */
    public static void writePropertyName(PrintWriter out, String name, boolean addColonSeparator, boolean surroundValueWithQuotes) {
        writePropertyName(out, name, 0, addColonSeparator, surroundValueWithQuotes, true);
    }
    /**
     *  Default values:
     *     - tabsBeforeProperty:        0
     */
    public static void writePropertyName(PrintWriter out, String name, boolean addColonSeparator, boolean surroundValueWithQuotes, boolean escapeValue) {
        writePropertyName(out, name, 0, addColonSeparator, surroundValueWithQuotes, escapeValue);
    }
    public static void writePropertyName(PrintWriter out, String name, int tabsBeforeProperty, boolean addColonSeparator, boolean surroundValueWithQuotes, boolean escapeValue) {
        if (tabsBeforeProperty > 0) {
            writeTabs(out, tabsBeforeProperty);
        }
        writeValue(out, name, surroundValueWithQuotes, escapeValue);
        if (addColonSeparator) {
            out.print(": ");
        }
    }

    /**
     *  Default values:
     *     - surroundValueWithQuotes:   true
     *     - escapeValue:               true
     */
    public static void writeValue(PrintWriter out, String value) {
        writeValue(out, value, true, true);
    }
    /**
     *  Default values:
     *     - escapeValue:               true
     */
    public static void writeValue(PrintWriter out, String value, boolean surroundValueWithQuotes) {
        writeValue(out, value, surroundValueWithQuotes, true);
    }
    public static void writeValue(PrintWriter out, String value, boolean surroundValueWithQuotes, boolean escapeValue) {
        if (surroundValueWithQuotes) {
            out.print("\"");
        }
        String escapedValue = (StringUtils.isBlank(value) ? "" : (escapeValue ? Jsoner.escape(value) : value));
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, JsonHelper.class.getName(), "writeStringValue", "escapedValue = " + escapedValue);
        }
        out.print(escapedValue);
        if (surroundValueWithQuotes) {
            out.print("\"");
        }
    }

    private static final Logger l = LogUtils.getLogger(JsonHelper.class);
}
