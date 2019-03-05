package dti.pm.componentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.regionalmgr.RmComponentFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Record load processor for maintain rm component
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 19, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RmComponentEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        // Default TransactionEffectiveDate to today.
        String today = DateUtils.formatDate(new Date());
        if(record.hasStringValue(RmComponentFields.TRANSACTION_EFFECTIVE_DATE) &&
            StringUtils.isBlank(RmComponentFields.getTransactionEffectiveDate(record))){
           RmComponentFields.setTransactionEffectiveDate(record, today);
        }
        // Default ProcessStatus to "INPROGRESS".
        if(!record.hasStringValue(RmComponentFields.PROCESS_STATUS) ||
            StringUtils.isBlank(RmComponentFields.getProcessStatus(record))){
           RmComponentFields.setProcessStatus(record, RmComponentFields.ProcessStatusValues.INPROGRESS);
        }
        // If the event process_status is "INPROGRESS", the Delete/Process options are enabled.
        if (RmComponentFields.ProcessStatusValues.INPROGRESS.equals(RmComponentFields.getProcessStatus(record))) {
            record.setFieldValue("isDeleteAvailable", "Y");
            record.setFieldValue("isProcessAvailable", "Y");
            if (!isInProgress) {
                isInProgress = true;
            }
        }
        else {
            record.setFieldValue("isDeleteAvailable", "N");
            record.setFieldValue("isProcessAvailable", "N");
        }
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            List nameList = new ArrayList();
            nameList.add(RmComponentFields.PM_RM_PROCESS_MSTR_ID);
            recordSet.addFieldNameCollection(nameList);
        }
        else if (isInProgress) {
            recordSet.getSummaryRecord().setFieldValue("isSaveAvailable", "Y");
        }
    }

    private boolean isInProgress = false;
}
