package dti.oasis.struts;

import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class with to handle template functionality.
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 5, 2004
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/8/2004        JBE     Switch to StringUtils.replace
 *                          Also, added includeLabels parm
 *
 * ---------------------------------------------------
*/

public class TemplateHelper {
    protected static final String ALLFIELDS = "<ALLFIELDS/>";
    protected static final String FIELDSTART = "<FIELD>";
    protected static final String FIELDEND = "</FIELD>";
    protected static final int LENSTART = FIELDSTART.length();
    protected static final int LENEND = FIELDEND.length();
    protected static final String clsName = TemplateHelper.class.getName();

    /**
     * Takes a template file, and fills it with field labels and values.
     * A template is a text file containing simple delimited tags that are substituted
     * out for labels and values in the OasisFields object in the current request and
     * Map.  A field in a template looks like this: <FIELD>fieldId</FIELD>.
     * <p>If the field from the template is not found in the OasisFields object, we
     * look for the field in the Map, using the key for the label.  You can also create
     * a specialized template that produces ALL visible fields in OasisFields.  Use the
     * tag <ALLFIELDS/>.  Each field will appear on a separate line. Field Labels are included
     *
     * @param request      HttpServletRequest containing OasisFields as well as any
     *                     ArrayLists produced by calling OasisFields.getListOfValues(...)
     * @param templateFile Name of template file including path
     * @param values       Map of fieldId,values
     * @return Filled in template as a String
     */
    public static String fillTemplate(HttpServletRequest request, String templateFile, Map values)
            throws IOException {
        return fillTemplate(request, templateFile, values, true);
    }

    /**
     * Takes a template file, and fills it with field labels and values.
     * A template is a text file containing simple delimited tags that are substituted
     * out for labels and values in the OasisFields object in the current request and
     * Map.  A field in a template looks like this: <FIELD>fieldId</FIELD>.
     * <p>If the field from the template is not found in the OasisFields object, we
     * look for the field in the Map, using the key for the label.  You can also create
     * a specialized template that produces ALL visible fields in OasisFields.  Use the
     * tag <ALLFIELDS/>.  Each field will appear on a separate line.
     *
     * @param request       HttpServletRequest containing OasisFields as well as any
     *                      ArrayLists produced by calling OasisFields.getListOfValues(...)
     * @param templateFile  Name of template file including path
     * @param values        Map of fieldId,values
     * @param includeLabels TRUE if you want the field labels included, false if not
     * @return Filled in template as a String
     */
    public static String fillTemplate(HttpServletRequest request, String templateFile, Map values,
                                      boolean includeLabels) throws IOException {
        Logger l = LogUtils.enterLog(TemplateHelper.class, "fillTemplate",
                new Object[]{request, templateFile, values, String.valueOf(includeLabels)});
        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        StringBuffer out = new StringBuffer();
        BufferedReader rea = null;
        String lineSep = System.getProperty("line.separator");
        try {
            rea = new BufferedReader(new FileReader(templateFile));
            String line = rea.readLine();
            while (line != null) {
                out.append(processLine(line, request, fields, values, includeLabels)).append(lineSep);
                line = rea.readLine();
            }
            rea.close();
            l.exiting(clsName, "fillTemplate", out);
            return out.toString();

        }
        finally {
            if (rea != null) rea.close();
        }

    }

    /**
     * Create the text for all fields in a template
     *
     * @param request Current HttpServletRequest
     * @param fields  OasisFields collection
     * @param values  Map of fieldId/value entries
     * @return Text for ALL fields in a template
     */
    protected static String getAllFields(HttpServletRequest request, OasisFields fields, Map values) {
        Logger l = LogUtils.enterLog(TemplateHelper.class, "getAllFields",
                new Object[]{request, fields.keySet(), values});
        StringBuffer buff = new StringBuffer();
        // Add the dynamic field values from the page

        buff.append(getFieldValues(fields.getPageFields(), fields, request, values, true));

        // Add the dynamic field values on each layer
        ArrayList layers = fields.getLayerIds();
        int sz = layers.size();
        for (int i = 0; i < sz; i++)
            buff.append(getFieldValues(fields.getLayerFields((String) layers.get(i)), fields, request, values, true));

        // Now check if there are any values in the map that we want to process
        // independently of the OasisFields
        Iterator it = values.keySet().iterator();
        String lineSep = System.getProperty("line.separator");
        while (it.hasNext()) {
            String fieldId = (String) it.next();
            Object o = fields.get(fieldId);
            if (o == null) {
                buff.append(lineSep).append(getField(fieldId, request, fields, values, true));
            }
        }
        l.exiting(clsName, "getAllFields", buff);
        return buff.toString();
    }

    /**
     * Create the text to replace all fields in a list.
     *
     * @param list    ArrayList of OasisFormField objects to process
     * @param fields  OasisFields collection
     * @param request Current HttpServletRequest
     * @param values  Map of fieldId/value entries
     * @param includeLabels TRUE to include field labels
     * @return Text to replace all fields in a list
     */
    protected static String getFieldValues(ArrayList list, OasisFields fields, HttpServletRequest request,
                                           Map values, boolean includeLabels) {

        Logger l = LogUtils.enterLog(TemplateHelper.class, "getFieldValues",
                new Object[]{list, fields.keySet(), request, values, String.valueOf(includeLabels)});
        int sz = list.size();
        StringBuffer buff = new StringBuffer();
        String lineSep = System.getProperty("line.separator");
        // Loop through the fields
        for (int i = 0; i < sz; i++) {
            // Get the field
            OasisFormField field = (OasisFormField) list.get(i);
            if (field.getIsVisible())
                buff.append(lineSep).append(getField(field.getFieldId(), request, fields, values, includeLabels));

        }
        l.exiting(clsName, "getFieldValues", buff);
        return buff.toString();
    }

    /**
     * Create the text to replace a single field in a template
     *
     * @param fieldId       The id of the field to replace
     * @param request       Current HttpServletRequest
     * @param fields        OasisFields collection
     * @param values        Map of fieldId/value entries
     * @param includeLabels true to include labels from fields
     * @return Text to replace a single field
     */
    protected static String getField(String fieldId, HttpServletRequest request,
                                     OasisFields fields, Map values, boolean includeLabels) {
        Logger l = LogUtils.enterLog(TemplateHelper.class, "getField",
                new Object[]{fieldId, request, fields.keySet(), values, String.valueOf(includeLabels)});
        OasisFormField field = (OasisFormField) fields.get(fieldId);
        StringBuffer buff = new StringBuffer();
        Object o = values.get(fieldId);
        String value = null;
        // If an Array of String, create comma separated list of values
        if (o instanceof String[]) {
            String[] str = (String[]) o;
            StringBuffer valBuff = new StringBuffer();
            int ln = str.length;
            for (int j = 0; j < ln; j++)
                valBuff.append((j > 0) ? ", " : "").append(str[j]);
            value = valBuff.toString();
        }
        else if (o instanceof String)
            value = (String) o;
        if (value == null)
            value = "";
        // if we found a field in OasisFields collection
        if (field != null) {
            // if visible,
            if (field.getIsVisible()) {
                if (includeLabels) {
                    // Get the label and format it
                    String label = field.getLabel();
                    if (label == null || label.equalsIgnoreCase("&nbsp;"))
                        label = "";
                    buff.append(label);
                    if (!StringUtils.isBlank(label))
                        buff.append(": ");
                }
                // Check if there is a ListOfValues in the request for this field
                // If so, decode the value
                ArrayList lov = (ArrayList) request.getAttribute(fieldId + "LOV");

                if (lov != null)
                    value = CollectionUtils.getDecodedValue(lov, value);
                buff.append(value);
            }
        }
        // No field in OasisFields, use the fieldId as the label and assume visibility
        else {
            if (values.containsKey(fieldId)) {
            	if(includeLabels)
            		buff.append(fieldId).append(": ");
                buff.append(value);
            }
        }

        l.exiting(clsName, "getField", buff);
        return buff.toString();
    }

    /**
     * Process a line of Template
     *
     * @param in            Line from template
     * @param request       Current HttpServletRequest
     * @param fields        OasisFields collection
     * @param values        Map of fieldId,value entries
     * @param includeLabels true to include labels from field
     * @return line from template with all field tags replaced
     */
    protected static String processLine(String in, HttpServletRequest request, OasisFields fields,
                                        Map values, boolean includeLabels) {
        Logger l = LogUtils.enterLog(TemplateHelper.class, "processLine",
                new Object[]{in, request, fields.keySet(), values, String.valueOf(includeLabels)});
        int pos = in.indexOf(ALLFIELDS);

        if (pos != -1) {
            in = StringUtils.replace(in, ALLFIELDS, getAllFields(request, fields, values));
        }

        pos = in.indexOf(FIELDSTART);
        int len = in.length();
        int safetyValve = 0;
        while (pos >= 0 && pos < len) {

            int pos1 = in.indexOf(FIELDEND, pos + 1);
            String fieldId = in.substring(pos + LENSTART, pos1);
            String val = getField(fieldId, request, fields, values, includeLabels);

            in = StringUtils.replace(in, new StringBuffer(FIELDSTART).append(fieldId).
                    append(FIELDEND).toString(), val);
            if (++safetyValve > 500) {
                l.info(clsName + ":processLine - Safety valve triggered, line=" + in);
                break;
            }

            pos = in.indexOf(FIELDSTART);
        }
        l.exiting(clsName, "processLine", in);
        return in;
    }
}
