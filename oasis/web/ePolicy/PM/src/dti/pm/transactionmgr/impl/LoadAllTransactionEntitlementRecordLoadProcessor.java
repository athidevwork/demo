package dti.pm.transactionmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.transactionmgr.TransactionFields;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 30, 2007
 *
 * @author zlzhu
 */
/*
 * This class is processor of transaction loader
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Aug 30, 2007     zlzhu       Created
 * 01/04/2007       fcb         postProcessRecordSet: added logic for 0 recordSet size.        
 * 05/20/2010       syang       107872 - Add constructor method LoadAllTransactionEntitlementRecordLoadProcessor to pass 
 *                              input record. Modified postProcessRecordSet to disable the Show All/Show Term button if
 *                              the transaction page is opened from Cancellation Detail.
 * 02/19/2013       jshen       141982 - 1. Rewrite logic to correctly control the Show All/Show Term buttons.
 *                                       2. Move logic to set Entity Detail button into here.
 * ---------------------------------------------------
 */

public class LoadAllTransactionEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        TransactionStatus transactionStatus = TransactionFields.getTransactionStatus(record);
        if (transactionStatus.isComplete() || transactionStatus.isInvalid()) {
            record.setFieldValue("statusDesc", "Complete");
            record.setFieldValue("isEditable", YesNoFlag.N);
        }
        else {
            record.setFieldValue("statusDesc", "In Progress");
            if (!record.hasStringValue("forceRerateB")) {
                record.setFieldValue("forceRerateB",YesNoFlag.N);
            }
            record.setFieldValue("isEditable", YesNoFlag.Y);
        }
        return true;
    }

    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            List fieldNames = new ArrayList();
            fieldNames.add("statusDesc");
            fieldNames.add("isEditable");
            fieldNames.add("forceRerateB");
            recordSet.addFieldNameCollection(fieldNames);
        }
        
        Record summaryRecord = recordSet.getSummaryRecord();
        summaryRecord.setFieldValue("isShowAllAvailable", YesNoFlag.Y);
        summaryRecord.setFieldValue("isShowTermAvailable", YesNoFlag.N);
        
        Record inputRecord = getInputRecord();
        String showAllOrShowTerm = null;
        if (inputRecord != null && inputRecord.hasStringValue(TransactionFields.SHOW_ALL_OR_SHOW_TERM)) {
            showAllOrShowTerm = TransactionFields.getShowAllOrShowTerm(inputRecord);
        }
        
        if (showAllOrShowTerm != null && TransactionFields.ShowAllOrShowTermValues.ALL.equals(showAllOrShowTerm)) {
            summaryRecord.setFieldValue("isShowAllAvailable", YesNoFlag.N);
            summaryRecord.setFieldValue("isShowTermAvailable", YesNoFlag.Y);
        }

        summaryRecord.setFieldValue("isEntityDetailAvailable", getEntityAvailable());
    }

    public LoadAllTransactionEntitlementRecordLoadProcessor() {
    }

    public LoadAllTransactionEntitlementRecordLoadProcessor(Record inputRecord, String entityAvailable) {
        setInputRecord(inputRecord);
        setEntityAvailable(entityAvailable);
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }
    
    public String getEntityAvailable() {
        return m_entityAvailable;
    }
    
    public void setEntityAvailable(String entityAvailable) {
        m_entityAvailable = entityAvailable;
    }

    private Record m_inputRecord;
    private String m_entityAvailable;
}