package dti.pm.policymgr.tailquotemgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.policymgr.PolicyHeader;

import java.util.Map;
import java.util.HashMap;

/**
 * This class extends the default record load processor to enforce entitlements for the underwriter web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 23, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class TailQuoteTransactionEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        TransactionStatus tranStatus = TransactionFields.getTransactionStatusCode(record);
        String tranLogId = TransactionFields.getTransactionLogId(record);
        getTransactionLogIdMap().put(tranLogId,tranStatus);
        if(tranStatus.isComplete()){
           record.setEditIndicator(YesNoFlag.N);
        }

        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Record summeryRecord = recordSet.getSummaryRecord();
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        String curTranLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        summeryRecord.setFieldValue("isProcessAvailable", YesNoFlag.N);
        if(!(screenMode.isViewPolicy()||screenMode.isOosWIP()||screenMode.isViewEndquote())){
            if(!getTransactionLogIdMap().containsKey(curTranLogId)){
                summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
            }else{
                summeryRecord.setFieldValue("isProcessAvailable", YesNoFlag.Y);                
            }
        }
    }

    public TailQuoteTransactionEntitlementRecordLoadProcessor() {
    }

    public TailQuoteTransactionEntitlementRecordLoadProcessor(PolicyHeader policyHeader) {
        setPolicyHeader(policyHeader);
    }

    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }


    public Map getTransactionLogIdMap() {
        return m_TransactionLogIdMap;
    }

    public void setTransactionLogIdMap(Map transactionLogIdMap) {
        m_TransactionLogIdMap = transactionLogIdMap;
    }

    private Map m_TransactionLogIdMap = new HashMap();
    private PolicyHeader m_policyHeader;
}
