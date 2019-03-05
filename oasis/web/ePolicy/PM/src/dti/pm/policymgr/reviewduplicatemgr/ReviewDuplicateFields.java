package dti.pm.policymgr.reviewduplicatemgr;

import dti.oasis.recordset.Record;

/**
 * Action class for Renewal Candidate.
 * <p/>
 *
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   June 28, 2016
 *
 * @author ssheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2016        ssheng      164927 - Created this action class for Quote Import enhancement.
 * 03/13/2018        tzeng       189424 - Added INVALID_TO_CIS_SUFFIX_NAME, INVALID_TO_CIS_LICENSE_TYPE,
 *                                        INVALID_TO_CIS_LICENSE_STATE
 * ---------------------------------------------------
 */
public class ReviewDuplicateFields {

    // review Duplicate fields
    public static final String STATE_CODE = "stateCode";
    public static final String CITY = "city";
    public static final String ADDRESS1 = "addr1";
    public static final String QUOTE = "QUOTE";
    public static final String ADD_TO_CIS = "addToCis";
    public static final String CIS_SAVED_B = "cisSavedB";
    public static final String NO_DUPLICATE = "noDuplicate";
    public static final String SAVE_ALL_B = "saveAllB";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String USE_CIS_RECORD = "useCisRecord";
    public static final String POLICY_LOAD_EVENT_DETAIL_ID = "policyLoadEventDetailId";
    public static final String TERM_ID = "termId";
    public static final String POLICY_LOAD_EVENT_HEADER_ID = "policyLoadEventHeaderId";
    public static final String LOAD_EVENT_HEADER_ID = "LoadEventHeaderId";
    public static final String INVALID_TO_CIS_SUFFIX_NAME = ",SUFFIX_NAME,";
    public static final String INVALID_TO_CIS_LICENSE_TYPE = ",LICENSE_TYPE,";
    public static final String INVALID_TO_CIS_LICENSE_STATE = ",LICENSE_STATE,";

    public static String getStateCode(Record record) {
        return record.getStringValue(STATE_CODE);
    }

    public static void setStateCode(Record record, String stateCode) {
        record.setFieldValue(STATE_CODE, stateCode);
    }

    public static String getCity(Record record) {
        return record.getStringValue(CITY);
    }

    public static void setCity(Record record, String city) {
        record.setFieldValue(CITY, city);
    }

    public static String getAddress1(Record record) {
        return record.getStringValue(ADDRESS1);
    }

    public static void setAddress1(Record record, String address1) {
        record.setFieldValue(ADDRESS1, address1);
    }

    public static String getCisSavedB(Record record) {
        return record.getStringValue(CIS_SAVED_B);
    }

    public static void setCisSavedB(Record record, String cisSavedB) {
        record.setFieldValue(CIS_SAVED_B, cisSavedB);
    }

    public static String getSaveAllB(Record record) {
        return record.getStringValue(SAVE_ALL_B);
    }

    public static void setSaveAllB(Record record, String saveAllB) {
        record.setFieldValue(SAVE_ALL_B, saveAllB);
    }

    public static String getAddToCis(Record record) {
        return record.getStringValue(ADD_TO_CIS);
    }

    public static void setAddToCis(Record record, String addToCis) {
        record.setFieldValue(ADD_TO_CIS, addToCis);
    }

    public static String getUseCisRecord(Record record) {
        return record.getStringValue(USE_CIS_RECORD);
    }

    public static void setUseCisRecord(Record record, String useCisRecord) {
        record.setFieldValue(USE_CIS_RECORD, useCisRecord);
    }

    public static String getPolicyLoadEventDetailId(Record record) {
        return record.getStringValue(POLICY_LOAD_EVENT_DETAIL_ID);
    }

    public static void setPolicyLoadEventDetailId(Record record, String policyLoadEventDetailId) {
        record.setFieldValue(POLICY_LOAD_EVENT_DETAIL_ID, policyLoadEventDetailId);
    }
}
