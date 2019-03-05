package dti.oasis.util;

import org.apache.struts.util.LabelValueBean;

import java.util.*;
import java.util.logging.Logger;

/**
 * Collection Utility methods
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date: Feb 20, 2004
 * @author jbe
 */

/*
 * Revision Date    Revised By  Description
 * ----------------------------------------------------------------------------
 * 3/22/2004        jbe     isValueInListOfValues - check for null value
 * 5/17/2004		jbe     Handle null labelvaluebean values in getDecodedValue
 *							and isValueInListOfValues
 * 01/23/2007       wer     Changed usage of new Boolean(x) in logging to String.valueOf(x);
 * 02/01/2007       wer     Modified getDecodedValue to return "" if the label is null.
 * 05/10/2007       GCC     Changed isValueInListOfValues to trim value when
 *                          comparing value to list of values.
 * 04/11/2008       FWCH    Set blank char to values array in method getDecodedValues()
 *                          if its element is null to avoid NullPointerException.
 * ----------------------------------------------------------------------------
 */

public class CollectionUtils {
    /**
     * Look up a value in an ArrayList of LabelValueBeans and return the label
     * @param listOfValues ArrayList of LabelValueBean objects
     * @param value        value to find
     * @return String label or value if it is not found in the list
     * @see org.apache.struts.util.LabelValueBean
     */
    public static String getDecodedValue(List listOfValues, String value) {
        Logger l = LogUtils.enterLog(CollectionUtils.class, "getDecodedValue",
                new Object[]{listOfValues, value});
        String label = value;
        if (listOfValues != null){
            int count = listOfValues.size();
            for (int x = 0; x < count; x++) {
                LabelValueBean bean = (LabelValueBean) listOfValues.get(x);
                if (bean !=null && ((value==null && bean.getValue()==null) ||
                	(bean.getValue()!=null && bean.getValue().trim().equals(value)))) {
                    label = bean.getLabel();
                    break;
                }
            }
        }
        label = label == null ? "" : label;
        l.exiting(CollectionUtils.class.getName(), "getDecodedValue", label);
        return label;
    }

    /**
     * Lookup an Array of values in an ArrayList of LabelValueBeans and return
     * a comma separated list of labels
     *
     * @param listOfValues ArrayList of LabelValueBean objects
     * @param values       values to find
     * @return String Comma separated list containing only labels for the values that were found
     * @see org.apache.struts.util.LabelValueBean
     */
    public static String getDecodedValues(ArrayList listOfValues, String[] values) {
        Logger l = LogUtils.enterLog(CollectionUtils.class, "getDecodedValues",
                new Object[]{listOfValues, values});
        StringBuffer retVal = new StringBuffer();
        int count, x;
        int sz = values.length;
        for (x = 0; x < sz; x++) {
            if (values[x] == null) {
                values[x] = "";
            }
        }
        if (listOfValues == null) {
            count = values.length;
            for (x = 0; x < sz; x++)
                retVal.append(",").append(values[x]);
        } else {
            count = listOfValues.size();
            Arrays.sort(values);
            for (x = 0; x < count; x++) {
                LabelValueBean bean = (LabelValueBean) listOfValues.get(x);
                if (Arrays.binarySearch(values, bean.getValue()) >= 0)
                    retVal.append(",").append(bean.getLabel());
            }
        }
        if (retVal.length() > 0)
            retVal.deleteCharAt(0);
        l.exiting(CollectionUtils.class.getName(), "getDecodedValues", retVal);
        return retVal.toString();
    }
 /**
     * Checks for a value in a collection of LabelValueBean objects
     * @param list collection of LabelValueBean objects
     * @param value the value to look for
     * @return true/false
     * @see org.apache.struts.util.LabelValueBean
     */
    public static boolean isValueInListOfValues(Collection list, String value) {
        Logger l = LogUtils.enterLog(CollectionUtils.class, "isValueInListOfValues", new Object[]{list, value});
        boolean rc = false;
        if (value != null) {

            Iterator it = list.iterator();
            while (it.hasNext()) {
                LabelValueBean bean = (LabelValueBean) it.next();
                if (bean !=null && ((value==null && bean.getValue()==null) ||
                	(bean.getValue()!=null && bean.getValue().trim().equals(value.trim())))) {
                    rc = true;
                    break;
                }
            }
        }
        l.exiting(CollectionUtils.class.getName(), "isValueInListOfValues", String.valueOf(rc));
        return rc;
    }
}
