package dti.oasis.util;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FDF Object - generates FDF file
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 * Date:   Jan 16, 2004
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/08/2010       bhong 109557 - fixed error in createFDF method.
 *
 * ---------------------------------------------------
 */

public class FDF {
    /**
     * Collection of name/value paris
     */
    protected Map fields;

    /**
     * Constant representing endObj text
     */
    public static final String endObj = "endobj";

    /**
     * Default Constructor
     */
    public FDF() {
        fields = new HashMap();
    }

    /**
     * Fields collection
     *
     * @return
     */
    public Map getFields() {
        return fields;
    }

    public void setFields(Map map) {
        fields = map;
    }

    /**
     * Add a field to the collection
     *
     * @param field
     * @param value
     */
    public void addField(String field, String value) {
        fields.put(field, value);
    }

    /**
     * Get header for key
     *
     * @param key
     * @return
     */
    protected String getHeader(int key) {
        return new StringBuffer(String.valueOf(key)).append(" 0 obj").toString();
    }

    /**
     * Create FDF File given the fields in the collection
     *
     * @param fileName FDF FileName
     * @throws IOException
     */
    public void createFDF(String fileName) throws IOException {
        Logger l = LogUtils.enterLog(getClass(), "createFDF", fileName);
        BufferedWriter wri = null;
        try {
            wri = new BufferedWriter(new FileWriter((fileName)));

            // build the header portion
            wri.write("%FDF-1.2");
            wri.newLine();
            wri.write("%");

            // The Integration Java build fails when these characters appear, even in the comments
            // See Perforce changelist 247036 for a visual of what these characters look like.
            char char1 = '\u00E2';  // Windows key stroke is Alt 131
            char char2 = '\u00E3';  // Windows key stroke is Alt 0227
            char char3 = '\u00CF';  // Windows key stroke is Alt 0207
            char char4 = '\u00CF';  // Windows key stroke is Alt 0211

            wri.write(char1);
            wri.write(char2);
            wri.write(char3);
            wri.write(char4);
            wri.newLine();

            // build the first object
            wri.write(getHeader(1));
            wri.newLine();
            wri.write("<< ");
            wri.newLine();
            wri.write("/FDF << /F (");
            wri.write(fileName);
            wri.write(")");
            wri.newLine();
            wri.write("/Fields");
            wri.newLine();
            wri.write("[");
            wri.newLine();
            if (fields != null) {
                // loop through the tag/values
                Iterator it = fields.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String val = (String) fields.get(key);
                    if (l.isLoggable(Level.FINEST)) {
                        l.finest("Field Name:" + key + ", Field Value:" + val);
                    }
                    wri.write("<< /T (");
                    if (key == null) key = "";
                    String key1 = key;
                    if (key1.indexOf('(') > -1) {
                        key1 = key1.replaceAll("\\(","\\\\(");
                    }
                    if (key1.indexOf(')') > -1)
                        key1 = key1.replaceAll("\\)","\\\\)");
                    wri.write(key1);
                    wri.write(")/V (");
                    if (val == null) val = "";
                    if (val.indexOf('(') > -1)
                        val = val.replaceAll("\\(","\\\\(");
                    if (val.indexOf(')') > -1)
                        val = val.replaceAll("\\)","\\\\)");
                    wri.write(val);
                    wri.write(")>>");

                    wri.newLine();
                }
            }
            // finish the second object
            wri.write("]");
            wri.newLine();
            wri.write(">>>>");
            wri.newLine();
            wri.write(endObj);
            wri.newLine();
            // put the footer on the end
            wri.write("trailer");
            wri.newLine();
            wri.write("<<");
            wri.newLine();
            wri.write("/Root 1 0 R ");
            wri.newLine();
            wri.write(">>");
            wri.newLine();
            wri.write("%%EOF");
            wri.newLine();
            wri.flush();
            wri.close();
            l.exiting(getClass().getName(), "createFDF");
        }
        finally {
            if (wri != null) {
                wri.close();
            }
        }


    }

}
