package dti.pm.policymgr.applicationmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.applicationmgr.ApplicationFields;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page entitlment load processor for application
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   May 09, 2012
 *
 * @author bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/08/2013       tcheng      Issue 140412 - Modified postProcessRecord to change status "DECLINED" into "DECLINE"
 * ---------------------------------------------------
 */
public class ApplicationEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, rowIsOnCurrentPage});
        }
        boolean result = true;

        String status = ApplicationFields.getStatus(record);
        if ("SUBMITTED".equals(status) ||
            "QUOTE_WIP".equals(status) ||
            "POLICY_WIP".equals(status) ||
            "WIP".equals(status)) {
            record.setFieldValue("isSendReminderAvailable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isSendReminderAvailable", YesNoFlag.N);
        }

        if ("SUBMITTED".equals(status)) {
            record.setFieldValue("isViewAvailable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isViewAvailable", YesNoFlag.N);
        }

        if ("DECLINE".equals(status) || "WITHDRAWN".equals(status)) {
            record.setFieldValue("isRewipAvailable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isRewipAvailable", YesNoFlag.N);
        }
        record.setFieldValue("isReassignAvailable", YesNoFlag.Y);
        record.setFieldValue("isHistoryAvailable", YesNoFlag.Y);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet,});
        }

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            List pageEntitlementFields = Arrays.asList(
                "isViewAvailable", "isSendReminderAvailable", "isRewipAvailable", "isReassignAvailable", "isHistoryAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
            recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else {
            recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", YesNoFlag.Y);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }
    }
}
