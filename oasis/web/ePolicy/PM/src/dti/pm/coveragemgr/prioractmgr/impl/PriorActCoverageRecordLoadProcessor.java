package dti.pm.coveragemgr.prioractmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;

import java.util.logging.Logger;
import java.util.Map;

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
 * 08/10/2015       tzeng       164420 - Modified getInstance() and postProcessRecord() to delete relation code of
 *                                      BaseCoveragePracticeStateCodeMap.
 * ---------------------------------------------------
 */
public class PriorActCoverageRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Returns a synchronized static instance of Prior Act Coverage Record Load Processor that has the
     * implementation information.
     *
     * @param inputRecord, inuptRecord that provides basic information about selected policy
     * @return an instance of PriorActCoverageRecordLoadProcessor class
     */
    public synchronized static PriorActCoverageRecordLoadProcessor getInstance(
        Record inputRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(PriorActCoverageEntitlementRecordLoadProcessor.class,
            "getInstance", new Object[]{inputRecord,policyHeader});

        PriorActCoverageRecordLoadProcessor instance;
        instance = new PriorActCoverageRecordLoadProcessor();
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
        return true;
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


    private void setBaseCoveragePracticeStateCodeMap(Map baseCoveragePracticeStateCodeMap) {
        m_baseCoveragePracticeStateCodeMap = baseCoveragePracticeStateCodeMap;
    }


    private Map getBaseCoveragePracticeStateCodeMap() {
        return m_baseCoveragePracticeStateCodeMap;
    }

    private Map m_baseCoveragePracticeStateCodeMap;
    private Record inputRecord;
    private PolicyHeader m_policyHeader;

}
