package dti.pm.policymgr.tailquotemgr.impl;

import dti.pm.policymgr.tailquotemgr.TailQuoteManager;
import dti.pm.policymgr.tailquotemgr.TailQuoteFields;
import dti.pm.policymgr.tailquotemgr.dao.TailQuoteDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.busobjs.TransactionStatus;
import dti.pm.workflowmgr.impl.WorkflowAgentImpl;
import dti.oasis.workflowmgr.WorkflowAgent;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.util.Iterator;

/**
 * This Class provides the implementation details of TailQuoteManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 23, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/21/2008       fcb         Added support for long running transactions.
 * 06/27/2014       wdang       154039 - Modified validateAllTailQuoteTransaction()
 *                                       to change the logic of comparison between 
 *                                       tailQuoteDate and termEff.
 * ---------------------------------------------------
 */
public class TailQuoteManagerImpl implements TailQuoteManager {

    /**
     * method to load all tail quote transactions
     *
     * @param inputRecord
     * @param policyHeader
     * @return the recordset of tail quote transactions
     */
    public RecordSet loadAllTailQuoteTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTailQuoteTransaction", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        TailQuoteTransactionEntitlementRecordLoadProcessor tailQuoteTransactionEntitlementRecordLoadProcessor =
            new TailQuoteTransactionEntitlementRecordLoadProcessor(policyHeader);
        RecordSet tailQuoteTransRs =
            getTailQuoteDAO().loadAllTailQuoteTransaction(inputRecord, tailQuoteTransactionEntitlementRecordLoadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTailQuoteTransaction", tailQuoteTransRs);
        }

        return tailQuoteTransRs;
    }

    /**
     * method to load all tail quote
     *
     * @param inputRecord
     * @param policyHeader
     * @return the recordset of tail quote transactions
     */
    public RecordSet loadAllTailQuote(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTailQuote", new Object[]{policyHeader, inputRecord});
        }

        TailQuoteEntitlementRecordLoadProcessor tailQuoteEntitlementRecordLoadProcessor =
            new TailQuoteEntitlementRecordLoadProcessor(policyHeader, inputRecord);
        inputRecord.setFields(policyHeader.toRecord(), false);
        RecordSet tailQuoteRs = getTailQuoteDAO().loadAllTailQuote(inputRecord, tailQuoteEntitlementRecordLoadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTailQuote", tailQuoteRs);
        }
        return tailQuoteRs;
    }

    /**
     * perform process tail quote transaction
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     * @return processed records count
     */
    public int performProcessTailQuoteTransaction(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performProcessTailQuoteTransaction", new Object[]{policyHeader, inputRecords, inputRecord});
        }

        int processCount = 0;

        RecordSet changedRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        changedRs.addRecords(inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED)));

        if (changedRs.getSize() > 0) {
            //validate tail quote transactions
            validateAllTailQuoteTransaction(policyHeader, changedRs, inputRecord);

            processCount = getTailQuoteDAO().saveAllTailQuoteTransaction(changedRs);

            if(getTransactionManager().isRatingLongRunning()) {
                WorkflowAgent wa = WorkflowAgentImpl.getInstance();
                wa.initializeWorkflow(policyHeader.getPolicyNo(),
                        RATE_TRANSACTION_PROCESS,
                        RATE_TRANSACTION_INITIAL_STATE);
            }
            else {
                //rate the policy
                getTransactionManager().performTransactionRating(policyHeader.toRecord());
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performProcessTailQuoteTransaction", String.valueOf(processCount));
        }

        return processCount;
    }

    /**
     * validate tail quote transaction
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     * @return processed records count
     */
    private void validateAllTailQuoteTransaction(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllTailQuoteTransaction", new Object[]{policyHeader, inputRecords, inputRecord});
        }

        Iterator valRsIter = inputRecords.getRecords();
        while (valRsIter.hasNext()) {
            Record valRec = (Record) valRsIter.next();
            String rowId = TailQuoteFields.getTransactionLogId(valRec);
            String rowNum = String.valueOf(valRec.getRecordNumber() + 1);
            if(!valRec.hasStringValue(TailQuoteFields.TAIL_QUOTE_DATE)){
               MessageManager.getInstance().addErrorMessage("pm.maintainTailQuote.tailQuoteDate.required.error",
                    new String[]{rowNum}, TailQuoteFields.TAIL_QUOTE_DATE, rowId);
            }

            Date tailQuoteDate = DateUtils.parseDate(TailQuoteFields.getTailQuoteDate(valRec));
            Date termEff = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());

            if (tailQuoteDate.before(termEff)) {
                MessageManager.getInstance().addErrorMessage("pm.maintainTailQuote.tailQuoteDateGreaterTermEff.error",
                    new String[]{rowNum}, TailQuoteFields.TAIL_QUOTE_DATE, rowId);
            }

            if(MessageManager.getInstance().hasErrorMessages()){
                throw new ValidationException("tail quote transaction invalid");
            }

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllTailQuoteTransaction",
                String.valueOf(MessageManager.getInstance().hasErrorMessages()));
        }
    }


    /**
     * save all tail quote data
     *
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     * @return processed records count
     */
    public int saveAllTailQuote(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllTailQuote", new Object[]{policyHeader, inputRecords, inputRecord});
        }

        int processCount = 0;

        RecordSet changedRs = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        if (changedRs.getSize() > 0) {
            processCount = getTailQuoteDAO().saveAllTailQuote(changedRs);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllTailQuote", String.valueOf(processCount));
        }

        return processCount;
    }


    /**
     * To get initial values for a newly inserted Tail Quote Transaction record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForTailQuoteTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForTailQuoteTransaction", new Object[]{policyHeader, inputRecord});
        }

        Record outputRecord = new Record();
        Transaction currentTransaction = policyHeader.getLastTransactionInfo();

        TransactionFields.setTransactionLogId(outputRecord, currentTransaction.getTransactionLogId());
        TransactionFields.setTransactionTypeCode(outputRecord, currentTransaction.getTransactionTypeCode());
        TransactionFields.setTransactionCode(outputRecord, currentTransaction.getTransactionCode());
        TransactionFields.setTransactionStatusCode(outputRecord, TransactionStatus.INPROGRESS);
        PolicyHeaderFields.setPolicyId(outputRecord, policyHeader.getPolicyId());
        PolicyHeaderFields.setTermBaseRecordId(outputRecord, policyHeader.getTermBaseRecordId());
        PolicyFields.setEffectiveFromDate(outputRecord, policyHeader.getTermEffectiveFromDate());
        PolicyFields.setAccountingDate(outputRecord, DateUtils.formatDate(new Date()));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForTailQuoteTransaction", outputRecord);
        }

        return outputRecord;

    }

    public void verifyConfig() {
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getTailQuoteDAO() == null)
            throw new ConfigurationException("The required property 'tailQuoteDAO' is missing.");
    }


    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public TailQuoteDAO getTailQuoteDAO() {
        return m_tailQuoteDAO;
    }

    public void setTailQuoteDAO(TailQuoteDAO tailQuoteDAO) {
        m_tailQuoteDAO = tailQuoteDAO;
    }

    private TransactionManager m_transactionManager;
    private TailQuoteDAO m_tailQuoteDAO;
    private static final String RATE_TRANSACTION_PROCESS = "RateTransactionWorkflow";
    private static final String RATE_TRANSACTION_INITIAL_STATE = "invokeRateTransacction";
}
