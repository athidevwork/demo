package dti.pm.policymgr.validationmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.dao.PolicyDAO;
import dti.pm.policymgr.validationmgr.SoftValidationManager;
import dti.pm.policymgr.validationmgr.dao.SoftValidationDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/7/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/07/2016       tzeng       166929 - Initial version.
 * 07/05/2017       tzeng       186465 - Modified loadSoftValidation to change the name of transaction type code to
 *                                       transaction as output.
 * ---------------------------------------------------
 */

public class SoftValidationManagerImpl implements SoftValidationManager {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadSoftValidation(PolicyHeader policyHeader, Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSoftValidation", new Object[]{policyHeader, inputRecord});
        }

        String transactionLogId = "";
        if (inputRecord.hasStringValue("transactionLogId")) {
            transactionLogId = TransactionFields.getTransactionLogId(inputRecord);
        }
        else {
            transactionLogId = String.valueOf(getLatestSoftValidationTransaction(policyHeader));
        }
        TransactionFields.setTransactionLogId(inputRecord, transactionLogId);
        inputRecord.setFields(policyHeader.toRecord(), false);

        RecordSet rs = getSoftValidationDao().loadSoftValidation(inputRecord);
        Record outputRecord = rs.getSummaryRecord();
        if (rs != null && rs.getSize() > 0) {
            outputRecord.setFields(rs.getFirstRecord());
        }
        else {
            Record transRecord = getTransactionManager().loadTransactionById(inputRecord);
            if (transRecord != null) {
                outputRecord.setFields(transRecord);
                outputRecord.setFieldValue(TransactionFields.TRANSACTION, TransactionFields.getTransactionTypeCode(transRecord));
            }
            MessageManager.getInstance().addErrorMessage("pm.viewSoftValidation.validationList.noDataFound");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadSoftValidation", rs);
        }
        return rs;
    }

    @Override
    public void processSoftValidation(PolicyHeader policyHeader, RecordSet inputRecords) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processSoftValidation", new Object[]{policyHeader, inputRecords});
        }

        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_REC_SOFT_VALID, "N")).booleanValue()) {
            if (null == inputRecords || inputRecords.getSize() == 0) {
                int processCount = getSoftValidationDao().deleteAllSoftValidation(policyHeader.toRecord());
                if (processCount == 0) {
                    l.logp(Level.INFO, getClass().getName(), "deleteSoftValidation", "no soft validation data is deleted.");
                }
            }
            else {
                inputRecords.setFieldsOnAll(policyHeader.toRecord(), false);
                inputRecords.setFieldValueOnAll(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId(), true);
                int processCount = getSoftValidationDao().saveAllSoftValidation(inputRecords);
                if (processCount == 0) {
                    l.logp(Level.WARNING, getClass().getName(), "saveAllSoftValidation", "Save soft validation failed.");
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processSoftValidation");
        }
    }

    @Override
    public Record getSoftValidationB(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSoftValidationB", inputRecord);
        }

        Record record = getPolicyDAO().loadSoftValidationB(inputRecord);

        if (YesNoFlag.getInstance(PolicyFields.getSoftValidationB(record)).booleanValue()) {
            MessageManager.getInstance().addConfirmationPrompt("pm.viewSoftValidation.validationExists.prompt");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSoftValidationB", record);
        }
        return record;
    }

    /**
     * Get latest soft validation based transaction of policy.
     * @param policyHeader
     * @return
     */
    private long getLatestSoftValidationTransaction(PolicyHeader policyHeader) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestSoftValidationTransaction", policyHeader);
        }

        Record record = new Record();
        record.setFields(policyHeader.toRecord(), false);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestSoftValidationTransaction", record);
        }
        return getSoftValidationDao().getLatestSoftValidationTransaction(record);
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public SoftValidationDAO getSoftValidationDao() {
        return m_softValidationDAO;
    }
    public void setSoftValidationDAO(SoftValidationDAO softValidationDAO) {
        m_softValidationDAO = softValidationDAO;
    }

    public PolicyDAO getPolicyDAO() {
        return m_policyDAO;
    }

    public void setPolicyDAO(PolicyDAO policyDAO) {
        m_policyDAO = policyDAO;
    }

    private TransactionManager m_transactionManager;
    private SoftValidationDAO m_softValidationDAO;
    private PolicyDAO m_policyDAO;
}
