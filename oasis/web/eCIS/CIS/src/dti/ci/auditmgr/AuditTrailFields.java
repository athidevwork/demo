package dti.ci.auditmgr;

import dti.ci.helpers.ICIConstants;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * Interface for CIS Audit Trail constants.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 31, 2005
 *
 * @author HXY
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/09/2018       ylu         109179: refactor from ICIAuditTrailConstants.java
 * ---------------------------------------------------
*/

public class AuditTrailFields implements ICIConstants {
    private final Logger l = LogUtils.getLogger(getClass());

    public static final String HISTORY_TYPE = "historyType";
    public static final String SOURCE_NO = "sourceNo";
    public static final String AUDIT_TRAIL_LIST_GRID_HEADER_LAYER = "Audit_Trail_List_Grid_Header_Layer";
    public static final String AUDIT_TRAIL_DETAIL_LAYER = "Audit_Trail_Detail_Layer";
    public static final String AUDIT_VIEW_PREF = "auditViewPref";
    public static final String FROM_DATE_FILTER = "fromDateFilter";
    public static final String TO_DATE_FILTER = "toDateFilter";
    public static final String OPERATION_TABLE_FILTER = "operationTableFilter";


    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String OPERATION_TABLE = "operationTableName";
    public static final String OPERATION_ID = "operationId";
    public static final String FILTER_CRITERIA_PREFIX = "filterCriteria_";


    public static String getOperationTable(Record record) {
        return record.getStringValue(OPERATION_TABLE,"");
    }

    public static void setOperationTable(Record record, String operationTable) {
        record.setFieldValue(OPERATION_TABLE, operationTable);
    }

    public static String getFromDate(Record record) {
        return record.getStringValue(FROM_DATE,"");
    }

    public static void setFromDate(Record record, String fromDate) {
        record.setFieldValue(FROM_DATE, fromDate);
    }

    public static String getToDate(Record record) {
        return record.getStringValue(TO_DATE,"");
    }

    public static void setToDate(Record record, String toDate) {
        record.setFieldValue(TO_DATE, toDate);
    }

    public static String getHistoryType(Record record) {
        return record.getStringValue(AuditTrailFields.HISTORY_TYPE,"");
    }

    public static void setHistoryType(Record record, String historyType) {
        record.setFieldValue(AuditTrailFields.HISTORY_TYPE,historyType);
    }

    public static String getSourceNo(Record record) {
        return record.getStringValue(AuditTrailFields.SOURCE_NO,"");
    }

    public static void setSourceNo(Record record, String sourceNo) {
        record.setFieldValue(AuditTrailFields.SOURCE_NO,sourceNo);
    }

    public static String getOperationId(Record record) {
        return record.getStringValue(AuditTrailFields.OPERATION_ID,"");
    }

    public static void setOperationId(Record record, String opertionId) {
        record.setFieldValue(AuditTrailFields.OPERATION_ID, opertionId);
    }
}
