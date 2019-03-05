package dti.ci.disabilitymgr;

import dti.ci.helpers.ICIConstants;
import dti.oasis.recordset.Record;


/**
 * Constants for Disability
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   May 12, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
*/
public class DisabilityFields implements ICIConstants {
    public static final String FILTER_CATEGORY_PROPERTY = "entityDisability_categoryCode"; //"fltCategory";
    public static final String FILTER_STARTDATE_PROPERTY = "entityDisability_effectiveFromDate" ;//"fltStartDate";
    public static final String FILTER_ENDDATE_PROPERTY = "entityDisability_effectiveToDate";//"fltEndDate";

    public static String getFltCategory(Record record){
        return record.getStringValue(FILTER_CATEGORY_PROPERTY,"");
    }
    public static void setFltCategory(Record record, String fltCategory) {
        record.setFieldValue(FILTER_CATEGORY_PROPERTY,fltCategory);
    }

    public static String getEffectiveFromDate(Record record){
        return record.getStringValue(FILTER_STARTDATE_PROPERTY,"");
    }
    public static void setEffectiveFromDate(Record record, String fltStartDate) {
        record.setFieldValue(FILTER_STARTDATE_PROPERTY,fltStartDate);
    }

    public static String getEffectiveToDate(Record record){
        return record.getStringValue(FILTER_ENDDATE_PROPERTY,"");
    }
    public static void setEffectiveToDate(Record record, String fltEndDate) {
        record.setFieldValue(FILTER_ENDDATE_PROPERTY,fltEndDate);
    }

}
