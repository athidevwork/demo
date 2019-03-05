package dti.pm.coveragemgr.prioractmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.coveragemgr.prioractmgr.PriorActFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.tailmgr.dao.TailDAO;
import dti.pm.coveragemgr.prioractmgr.PriorActManager;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.riskmgr.RiskFields;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 11, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2014       adeng       154011 - Hide the closed official record.
 * 11/24/2014       kxiang      159323 - Modify postProcessRecord to change set 'isDelCovgAvailable' logical.
 * ---------------------------------------------------
 */
public class PriorActCoverageEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Returns a synchronized static instance of Prior Act Risk Entitlement Record Load Processor that has the
     * implementation information.
     *
     * @param inputRecord, inuptRecord that provides basic information about selected policy
     * @return an instance of PriorActCoverageEntitlementRecordLoadProcessor class
     */
    public synchronized static PriorActCoverageEntitlementRecordLoadProcessor getInstance(
        Record inputRecord, PolicyHeader policyHeader, PriorActManager priorActManager) {
        Logger l = LogUtils.enterLog(PriorActCoverageEntitlementRecordLoadProcessor.class,
            "getInstance", new Object[]{inputRecord});

        PriorActCoverageEntitlementRecordLoadProcessor instance;
        instance = new PriorActCoverageEntitlementRecordLoadProcessor();
        instance.setInputRecord(inputRecord);
        instance.setPolicyHeader(policyHeader);


        l.exiting(PriorActCoverageEntitlementRecordLoadProcessor.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        record.setFieldValue("isDelCovgAvailable", YesNoFlag.N);

        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (recordMode.isTemp() && !(screenMode.isCancelWIP() || screenMode.isResinstateWIP())) {
            record.setFieldValue("isDelCovgAvailable", YesNoFlag.Y);
        }


        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        List fieldNames = new ArrayList();
        fieldNames.add(RiskFields.PRACTICE_STATE_CODE);
        if (recordSet.getSize() == 0) {          

            fieldNames.add("isDelCovgAvailable");
        }
        recordSet.addFieldNameCollection(fieldNames);

        // Hide the closed official record.
        RecordSet offRecords = recordSet.getSubSet(new RecordFilter(PriorActFields.RECORD_MODE_CODE, PriorActFields.OFFICIAL));
        Iterator offIt = offRecords.getRecords();
        String transactionLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        while (offIt.hasNext()) {
            Record offRecord = (Record) offIt.next();
            if (offRecord.hasStringValue(PriorActFields.CLOSING_TRANS_LOG_ID) && transactionLogId.equals(PriorActFields.getClosingTransLogId(offRecord))) {
                offRecord.setDisplayIndicator("N");
            }
        }
    }

    public Record getInputRecord() {
        return inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        this.inputRecord = inputRecord;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }


    public PriorActManager getPriorActManager() {
        return m_priorActManager;
    }

    public void setPriorActManager(PriorActManager priorActManager) {
        m_priorActManager = priorActManager;
    }

    private Record inputRecord;
    private PolicyHeader m_policyHeader;
    private PriorActManager m_priorActManager;
}
