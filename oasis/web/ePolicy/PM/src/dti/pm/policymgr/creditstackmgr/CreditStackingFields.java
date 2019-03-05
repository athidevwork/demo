package dti.pm.policymgr.creditstackmgr;

import dti.oasis.recordset.Record;

/**
 * Helper constants and set/get methods to access Credit Stacking  Fields.
 *
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 26, 2011
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CreditStackingFields {

    public static final String SEARCH_B = "searchB";
    public static final String WIN_BUCKET = "winBucket";
    public static final String SEL_ORDER = "selOrder";
    public static final String CAT_PUB = "catPub";
    public static final String RISK_ID = "riskId";
    public static final String COVG_ID = "covgId";

    public static String getSearchB(Record record) {
        return record.getStringValue(SEARCH_B);
    }

    public static void setSearchB(Record record, String searchB) {
        record.setFieldValue(SEARCH_B, searchB);
    }

    public static String getCatPub(Record record) {
        return record.getStringValue(CAT_PUB);
    }

    public static void setCatPub(Record record, String catPub) {
        record.setFieldValue(CAT_PUB, catPub);
    }

    public class CategoryValues {
        public static final String MC = "MC";
        public static final String SB = "SB";
        public static final String OTHER = "OTHER";
    }
}
