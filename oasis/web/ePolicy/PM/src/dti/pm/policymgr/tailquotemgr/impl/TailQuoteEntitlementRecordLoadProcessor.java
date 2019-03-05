package dti.pm.policymgr.tailquotemgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.transactionmgr.TransactionFields;

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
public class TailQuoteEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        TransactionStatus tranStatus = TransactionFields.getTransactionStatusCode(record);        

        if (tranStatus == null || tranStatus.isComplete()) {
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
        String tranLogId = TransactionFields.getTransactionLogId(getInputRecord());
        String curTranLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();

        summeryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        if (recordSet.getSize() > 0) {
            if (!(screenMode.isViewPolicy() || screenMode.isOosWIP() || screenMode.isViewEndquote()) &&
                tranLogId.equals(curTranLogId)) {
                summeryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
            }
        }
    }

    public TailQuoteEntitlementRecordLoadProcessor() {
    }

    public TailQuoteEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord) {
        setInputRecord(inputRecord);
        setPolicyHeader(policyHeader);
    }

    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }


    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private Record m_inputRecord;
    private PolicyHeader m_policyHeader;
}
