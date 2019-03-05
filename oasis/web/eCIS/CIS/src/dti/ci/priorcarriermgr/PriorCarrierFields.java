package dti.ci.priorcarriermgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   9/27/12
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/08/2012       kshen       Created for refactor Prior Carrier page.
 * ---------------------------------------------------
 */
public class PriorCarrierFields {
    public static final String PRIOR_CARRIER_ACTION_CLASS_NAME = "dti.ci.priorcarriermgr.struts.MaintainPriorCarrierAction";
    public static final String FILTER_CRITERIA_PREFIX = "filter_";
    
    public static final String DEFAULT_TERM_YEAR = "defaultTermYear";
    
    public static String getDefaultTermYear(Record record) {
        return record.getStringValue(DEFAULT_TERM_YEAR, "");
    }
    
    public static void setDefaultTermYear(Record record, String defaultTermYear) {
        record.setFieldValue(DEFAULT_TERM_YEAR, defaultTermYear);
    }

}
