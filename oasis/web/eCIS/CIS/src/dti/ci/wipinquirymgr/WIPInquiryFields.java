package dti.ci.wipinquirymgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/17/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class WIPInquiryFields {
    public static final String SEARCH_CRITERIA_CLIENT_ENTITY_NAME = "searchCriteria_clientEntityName";
    public static final String SEARCH_CRITERIA_CLIENT_ENTITY_FK = "searchCriteria_clientEntityFK";
    public static final String WIP_INQUIRY_LIST_ROW_COUNT = "wipInquiryListRowCount";
    public static final String SOURCE_NO = "sourceNo";
    public static final String RESTRICT_SOURCE_LIST = "restrictSourceList";

    public static void setSearchCriteriaClientEntityName(Record record, String entityName) {
        record.setFieldValue(SEARCH_CRITERIA_CLIENT_ENTITY_NAME, entityName);
    }

    public static void setClientEntityFK(Record record, String clientEntityFK) {
        record.setFieldValue(SEARCH_CRITERIA_CLIENT_ENTITY_FK, clientEntityFK);
    }

    public static String getClientEntityFK(Record record) {
        return record.getStringValueDefaultEmpty(SEARCH_CRITERIA_CLIENT_ENTITY_FK);
    }

}
