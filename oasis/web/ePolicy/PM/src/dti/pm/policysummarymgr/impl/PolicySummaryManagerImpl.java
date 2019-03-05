package dti.pm.policysummarymgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.core.data.CommonTabsEntitlementRecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policysummarymgr.PolicySummaryFields;
import dti.pm.policysummarymgr.PolicySummaryManager;
import dti.pm.policysummarymgr.dao.PolicySummaryDAO;
import dti.pm.transactionmgr.TransactionManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of PolicySummaryManager Interface.
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
public class PolicySummaryManagerImpl implements PolicySummaryManager {
    @Override
    public RecordSet loadPolicySummary(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicySummary", new Object[]{policyHeader});
        }

        Record record = new Record();

        record.setFields(inputRecord);
        record.setFields(policyHeader.toRecord(), true);
        record.setFieldValue(PMCommonFields.RECORD_MODE_CODE, policyHeader.getRecordMode().getName());

        RecordLoadProcessor lp1 = new CommonTabsEntitlementRecordLoadProcessor(policyHeader);
        RecordLoadProcessor lp2 = new PolicySummaryEntitlementRecordLoadProcessor(policyHeader, inputRecord, getPolicyManager());
        RecordLoadProcessor lp3 = new PolicySummaryRowStyleRecordLoadProcessor();
        RecordLoadProcessor lp = lp1;
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp, lp2);
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(lp, lp3);
        RecordSet rs = getPolicySummaryDAO().loadPolicySummary(record, lp);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicySummary", rs);
        }
        return rs;
    }

    @Override
    public void savePolicySummary(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePolicySummary", new Object[]{policyHeader});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        inputRecord.setFieldValue(PolicySummaryFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
        inputRecord.setFieldValue(PolicySummaryFields.LEVEL, "POLICY");
        getTransactionManager().processSaveTransaction(policyHeader, inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePolicySummary");
        }
    }

    public PolicySummaryDAO getPolicySummaryDAO() {
        return m_policySummaryDAO;
    }

    public void setPolicySummaryDAO(PolicySummaryDAO policySummaryDAO) {
        m_policySummaryDAO = policySummaryDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    private PolicySummaryDAO m_policySummaryDAO;
    private TransactionManager m_transactionManager;
    private PolicyManager m_policyManager;
}
