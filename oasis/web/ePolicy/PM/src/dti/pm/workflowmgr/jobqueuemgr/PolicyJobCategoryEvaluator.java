package dti.pm.workflowmgr.jobqueuemgr;

import dti.pm.core.request.RequestStorageIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.transaction.dao.TransactionDAO;
import dti.oasis.util.LogUtils;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 22, 2008
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2010       fcb         109187: verifyConfig() added.
 * 10/01/2013       fcb         145725: used PolicyHeader to cache the job category.
 * ---------------------------------------------------
 */
public class PolicyJobCategoryEvaluator implements JobCategoryEvaluator {
    public synchronized static JobCategoryEvaluator getInstance() {
        if (c_instance == null) {
            c_instance = new PolicyJobCategoryEvaluator();
        }
        return c_instance;
    }

    public TransactionDAO getTransactionDAO() {
        return m_transactionDAO;
    }

    public void setTransactionDAO(TransactionDAO transactionDAO) {
        m_transactionDAO = transactionDAO;
    }

    public JobCategory evaluate () {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "evaluate");
        }

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        PolicyHeader policyHeader = (PolicyHeader) rsm.get(RequestStorageIds.POLICY_HEADER);
        String category = null;
        if (policyHeader.hasJobCategoryCache()) {
            category = policyHeader.getJobCategoryCache();
        }
        else {
            Record record = new Record();
            record.setFieldValue("key", policyHeader.getTermBaseRecordId());
            record.setFieldValue("keytype", TERM_BASE_RECORD_ID);
            category = getTransactionDAO().getJobCategory(record);
            policyHeader.setJobCategoryCache(category);
        }

        JobCategory jobCategory;

        if (JobCategory.SHORT.equals(category)) {
            jobCategory = JobCategory.SHORT;
        }
        else if (JobCategory.MEDIUM.equals(category)) {
            jobCategory = JobCategory.MEDIUM;
        }
        else if (JobCategory.LONG.equals(category)) {
            jobCategory = JobCategory.LONG;
        }
        else {
            throw new AppException("JobCategoryEvaluator - Invalid job category: "+category);        
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "evaluate", jobCategory);
        }
        return jobCategory;
    }

    public void verifyConfig() {
        if (getTransactionDAO() == null)
            throw new ConfigurationException("The required property 'transactionDAO' is missing.");
    }

    private static JobCategoryEvaluator c_instance;
    private static final String TERM_BASE_RECORD_ID = "TERM_BASE_RECORD_ID";
    private TransactionDAO m_transactionDAO;
}
