package dti.pm.policysummarymgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policysummarymgr.PolicySummaryFields;

import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for Policy Summary page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate java script that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 06, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/06/2016       wdang       168069 - Initial version.
 * ---------------------------------------------------
 */
public class PolicySummaryEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    public PolicySummaryEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord, PolicyManager policyManager){
        this.m_policyHeader = policyHeader;
        this.m_inputRecord = inputRecord;
        this.m_policyManager = policyManager;
    }
    @Override
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return true;
    }


    @Override
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});
        Record sumRecord = recordSet.getSummaryRecord();

        // get determination of the coverage class option
        Record r = new Record();
        r.setFields(getInputRecord());
        r.setFields(getPolicyHeader().toRecord(), false);
        boolean coverageClassAvailable = getPolicyManager().isCoverageClassAvailable(getPolicyHeader(), r, false);
        sumRecord.setFieldValue("isCoverageClassAvailable", YesNoFlag.getInstance(coverageClassAvailable));

        // set anchor column
        for (int i = 0; i < recordSet.getSize(); i ++) {
            Record record = recordSet.getRecord(i);
            record.setFieldValue(PolicySummaryFields.ANCHOR_COLUMN_NAME, i + 1);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private PolicyManager m_policyManager;
    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;
}
