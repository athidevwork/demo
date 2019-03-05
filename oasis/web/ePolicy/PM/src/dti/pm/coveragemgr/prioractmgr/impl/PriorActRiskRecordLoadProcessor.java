package dti.pm.coveragemgr.prioractmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.PolicyHeader;
import java.util.logging.Logger;



/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 20, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/10/2015       tzeng       164420 - Modified postProcessRecord() and getInstance() to delete relation code
 *                                      of BaseCoveragePracticeStateCodeMap.
 * ---------------------------------------------------
 */
public class PriorActRiskRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Returns a synchronized static instance of Prior Act Risk  Record Load Processor that has the
     * implementation information.
     *
     * @param inputRecord, policyHeader PriorActManager instance that provides basic information about selected coverage
     * @return an instance of PriorActRiskRecordLoadProcessor class
     */
    public synchronized static PriorActRiskRecordLoadProcessor getInstance(
        Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(PriorActRiskEntitlementRecordLoadProcessor.class,
            "getInstance", new Object[]{inputRecord});

        PriorActRiskRecordLoadProcessor instance;
        instance = new PriorActRiskRecordLoadProcessor();
        instance.setInputRecord(inputRecord);
        instance.setPolicyHeader(policyHeader);

        l.exiting(PriorActRiskEntitlementRecordLoadProcessor.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return super.postProcessRecord(record, rowIsOnCurrentPage);
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
    }


    private Record getInputRecord() {
        return inputRecord;
    }

    private void setInputRecord(Record inputRecord) {
        this.inputRecord = inputRecord;
    }

    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private Record inputRecord;
    private PolicyHeader m_policyHeader;

}
