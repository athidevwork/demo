package dti.pm.quotemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.core.session.UserSessionIds;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policyattributesmgr.PolicyAttributesManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.dao.PolicyDAO;
import dti.pm.quotemgr.QuoteManager;
import dti.pm.quotemgr.dao.QuoteDAO;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionXrefFields;
import dti.pm.transactionmgr.renewalprocessmgr.RenewalProcessManager;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for QuoteManager.
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   April 27, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2016       wdang       167534 - Initial version.
 * 09/07/2016       wdang       179350 - Modified performCopy:
 *                                       1) Add Policy Header as input parameter.
 *                                       2) Introduce PM_QTE_NO_FROM_POL to generate quote number.
 * 03/20/16         lzhang      190357 - Modified processAutoPendingRenewal:
 *                                       add renewalRecord null check
 * ---------------------------------------------------
 */
public class QuoteManagerImpl implements QuoteManager {

    //@Override
    public RecordSet loadQuoteVersions(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadQuoteVersions", new Object[]{inputRecord});
        }

        RecordSet rs = getQuoteDAO().loadQuoteVersions(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadQuoteVersions", rs);
        }
        return rs;
    }


    @Override
    public Record performCopy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performCopy", new Object[]{inputRecord});
        }

        inputRecord.setFields(policyHeader.toRecord(), false);
        String polNo = null;
        if (policyHeader.getPolicyCycleCode().isPolicy()) {
            Record polNoRec = null;
            inputRecord.setFieldValue("policyCycle", "QUOTE");
            if ("Y".equals(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_QTE_NO_FROM_POL, "Y"))) {
                polNoRec = getPolicyDAO().getParallelQuoteNo(inputRecord);
            }
            else {
                polNoRec = getPolicyDAO().getParallelPolicyNo(inputRecord);
            }
            polNo = polNoRec.getStringValue("polNo");
        }
        else {
            polNo = getPolicyDAO().getNewQuoteNo(inputRecord);
        }

        inputRecord.setFieldValue("quoteNo", polNo);
        inputRecord.setFieldValue("termEffDate", inputRecord.getStringValue("termEffectiveFromDate"));
        inputRecord.setFieldValue("termExpDate", inputRecord.getStringValue("termEffectiveToDate"));
        inputRecord.setFieldValue("comment", inputRecord.getStringValue("newTransactionComment", ""));
        inputRecord.setFieldValue("comment2", inputRecord.getStringValue("newTransactionComment2", ""));
        getQuoteDAO().performCopy(inputRecord);

        Record outputRecord = new Record();
        outputRecord.setFieldValue("parallelPolNo", polNo);
        outputRecord.setFieldValue("copiedQuoteNo", polNo);
        outputRecord.setFieldValue("saveAsOfficial", "N");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performCopy", outputRecord);
        }
        return outputRecord;
    }

    @Override
    public void performTransfer(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTransfer", new Object[]{inputRecord});
        }

        getQuoteDAO().performTransfer(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performTransfer");
        }
    }

    @Override
    public void performApply(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performApply", new Object[]{inputRecord});
        }

        getQuoteDAO().performApply(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performApply");
        }
    }

    @Override
    public void performMerge(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performMerge", new Object[]{ inputRecord});
        }

        getQuoteDAO().performMerge(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performMerge");
        }
    }

    @Override
    public void processAutoPendingRenewal(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performMerge", new Object[]{policyHeader, inputRecord});
        }

        if (getPolicyAttributesManager().isAutoPendingRenewalEnable(
            policyHeader.getTermEffectiveFromDate(),
            policyHeader.getLastTransactionInfo().getTransactionCode(),
            null)) {
            Record renewalRecord = getRenewalProcessManager().loadPendingRenewalTransaction(policyHeader);
            if (renewalRecord != null
                && renewalRecord.hasFieldValue(TransactionFields.TRANS_ID)
                && renewalRecord.hasFieldValue(TransactionFields.TRANS_CODE)
                && getPolicyAttributesManager().isAutoPendingRenewalEnable(
                policyHeader.getTermEffectiveFromDate(),
                policyHeader.getLastTransactionInfo().getTransactionCode(),
                TransactionCode.getInstance(renewalRecord.getStringValue(TransactionFields.TRANS_CODE)))) {

                Record xrefRecord = new Record();
                xrefRecord.setFieldValue(TransactionXrefFields.ORIGINAL_TRANS_ID,
                    renewalRecord.getFieldValue(TransactionFields.TRANS_ID));
                xrefRecord.setFieldValue(TransactionXrefFields.RELATED_TRANS_ID,
                    policyHeader.getLastTransactionInfo().getTransactionLogId());
                xrefRecord.setFieldValue(TransactionXrefFields.XREF_TYPE, TransactionXrefFields.AUTO_PENDING_RENEWAL);
                if (getTransactionManager().hasTransactionXref(xrefRecord)) {
                    Message message = new Message();
                    message.setMessageCategory(MessageCategory.JS_MESSAGE);
                    message.setMessageKey("pm.maintainQuoteTransfer.autoPendingRenewal");
                    UserSession userSession = UserSessionManager.getInstance().getUserSession();
                    if (!userSession.has(UserSessionIds.POLICY_SAVE_MESSAGE)) {
                        userSession.set(UserSessionIds.POLICY_SAVE_MESSAGE, new ArrayList<>());
                    }
                    List<Message> messageList = (List<Message>) userSession.get(UserSessionIds.POLICY_SAVE_MESSAGE);
                    messageList.add(message);
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performMerge");
        }
    }

    public QuoteDAO getQuoteDAO() {
        return m_quoteDAO;
    }

    public void setQuoteDAO(QuoteDAO quoteDAO) {
        m_quoteDAO = quoteDAO;
    }

    public PolicyDAO getPolicyDAO() {
        return m_policyDAO;
    }

    public void setPolicyDAO(PolicyDAO policyDAO) {
        m_policyDAO = policyDAO;
    }

    public RenewalProcessManager getRenewalProcessManager() {
        return m_renewalProcessManager;
    }

    public void setRenewalProcessManager(RenewalProcessManager renewalProcessManager) {
        m_renewalProcessManager = renewalProcessManager;
    }

    public PolicyAttributesManager getPolicyAttributesManager() {
        return m_policyAttributesManager;
    }

    public void setPolicyAttributesManager(PolicyAttributesManager policyAttributesManager) {
        m_policyAttributesManager = policyAttributesManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    private QuoteDAO m_quoteDAO;
    private PolicyDAO m_policyDAO;
    private RenewalProcessManager m_renewalProcessManager;
    private PolicyAttributesManager m_policyAttributesManager;
    private TransactionManager m_transactionManager;
}
