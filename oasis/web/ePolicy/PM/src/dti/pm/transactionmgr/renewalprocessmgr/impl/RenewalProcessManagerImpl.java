package dti.pm.transactionmgr.renewalprocessmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.TransactionCode;
import dti.pm.core.session.UserSessionIds;
import dti.pm.entitymgr.EntityManager;
import dti.pm.policyattributesmgr.PolicyAttributesManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.batchrenewalprocessmgr.BatchRenewalProcessManager;
import dti.pm.transactionmgr.renewalprocessmgr.RenewalProcessFields;
import dti.pm.transactionmgr.renewalprocessmgr.RenewalProcessManager;
import dti.pm.transactionmgr.renewalprocessmgr.dao.RenewalProcessDAO;
import dti.pm.validationmgr.impl.ValidTermDurationRecordValidator;
import dti.pm.validationmgr.impl.AvailablePolicyTypeRecordValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This Class provides the implementation details of RenewalProcessManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/25/2011       wqfu        123255 - Added lock check logic before renewal policy.
 * 06/08/2016       sma         177372 - Replaced Integer with Long for transaction FK
 * 08/15/2016       tzeng       177134 - 1)Modified renewPolicy() to indicate each message when renewal error.
 *                                       2)Modified performAutoRenewal() to add prompt if renew successfully.
 * 08/26/2016       wdang       167534 - Modified renewPolicy to add return value.
 * 10/18/2016       lzhang      180263 - Modified performAutoRenewal(): get applyPRT confirmation response
 * ---------------------------------------------------
 */

public class RenewalProcessManagerImpl implements RenewalProcessManager {
    /**
     * renew policy
     *
     * @param policyHeader
     * @param inputRecord  with policy renewal infos
     */
    public Record renewPolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "renewPolicy", inputRecord);

        Record outputRecord;

        //valid renewal infos,before renew a policy
        validateRenewal(policyHeader, inputRecord);

        // can policy be locked
        boolean canLockPolicy = getLockManager().canLockPolicy(policyHeader);
        if (!canLockPolicy) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
            throw new ValidationException();
        }

        //set field values
        inputRecord.setFields(policyHeader.toRecord(),false);

        String confirmApplyPRT;
        if (inputRecord.hasStringValue("pm.maintainRenewal.confirm.applyPRT.confirmed")) {
            confirmApplyPRT = inputRecord.getStringValue("pm.maintainRenewal.confirm.applyPRT.confirmed");
        }
        else {
            confirmApplyPRT = "N";
        }
        RenewalProcessFields.setApplyPrtB(inputRecord, YesNoFlag.getInstance(confirmApplyPRT));

        //if renewal reason code is configed,then passes the endorsment reason to renewal procedure
        if (getTransactionManager().isEndorsementCodeConfigured(policyHeader.toRecord())) {
            RenewalProcessFields.setEndorseCode(inputRecord, policyHeader.getRenewalReasonCode());
        }else{
            RenewalProcessFields.setEndorseCode(inputRecord,"");
        }

        /* call dao method to renew policy,if renewal validation fails throw ValidationException */
        outputRecord = getRenewalProcessDAO().renewPolicy(inputRecord);
        boolean isValid = outputRecord.getBooleanValue("valid").booleanValue();
        if (!isValid) {
            String errMsg = outputRecord.getStringValue("rmsg");
            MessageManager.getInstance().addErrorMessage("pm.maintainRenewal.renewFailed.error",new String[]{errMsg});
            throw new ValidationException("renew policy failed");
        }
        else {
            // set reload code in policy header since renewal transaction is NOT created by TransactionManager
            getTransactionManager().setPolicyHeaderReloadCode(policyHeader, TransactionCode.MANRENEW);
        }

        boolean isRenew = outputRecord.getBooleanValue("renewB").booleanValue();
        if (!isRenew) {
            String renewFailedReason = outputRecord.getStringValue("renewFailedReason");
            if (null != renewFailedReason && renewFailedReason.equals(FAILED_RM_DISC)){
                MessageManager.getInstance().addErrorMessage("pm.maintainRenewal.renewFailed.rmDiscount.error");
                throw new ValidationException("renew policy failed when process RM discount");
            }
            else if (null != renewFailedReason && renewFailedReason.equals(FAILED)){
                MessageManager.getInstance().addErrorMessage("pm.maintainRenewal.renewFailed.general.error");
                throw new ValidationException("renew policy failed in unclear");
            }
        }

        l.exiting(getClass().getName(), "renewPolicy", outputRecord);
        return outputRecord;
    }

    /**
     * load  Renew Term Expiration
     * sets effective date to current term expiration date
     *
     * @param policyHeader
     * @return the return record of PRT
     */
    public Record getInitialValuesForRenewalTermExpiration(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForRenewalTermExpiration", policyHeader);

        Record outputRecord = new Record();
        RenewalProcessFields.setRenewalTermEffDate(outputRecord, policyHeader.getTermEffectiveToDate());

        l.exiting(getClass().getName(), "getInitialValuesForRenewalTermExpiration", outputRecord);
        return outputRecord;
    }

    /**
     * load Pending Renewal Transaction
     *
     * @param policyHeader
     * @return the return record of PRT
     */
    public Record loadPendingRenewalTransaction(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "loadPendingRenewalTransaction", policyHeader);

        Record outputRecord = null;

        //Check if the system business rule has been configured to enable pending renewal changes
        String sysPendRenew = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_PENDING_RENEWAL);
        if (YesNoFlag.getInstance(sysPendRenew).booleanValue()) {
            /* call dao method to get PRT */
            outputRecord = getRenewalProcessDAO().getPendingRenewalTransaction(policyHeader.toRecord());
        }
        l.exiting(getClass().getName(), "loadPendingRenewalTransaction", outputRecord);
        return outputRecord;
    }

    /**
     * load Pending Renewal Transaction
     *
     * @param policyHeader
     * @return is PRT confirmation required
     */
    public boolean checkPRTConfirmationRequired(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "checkPRTConfirmationRequired", policyHeader);
        boolean isPRTConfirmationRequired = false;
        //check if there is pending renewal term
        Record prtRecord = loadPendingRenewalTransaction(policyHeader);
        if (prtRecord != null) {
            String transId = prtRecord.getStringValue(TRANS_ID);
            if (transId != null && Long.parseLong(transId) != -1) {
                MessageManager.getInstance().addConfirmationPrompt("pm.maintainRenewal.confirm.applyPRT", false);
                isPRTConfirmationRequired = true;
            }
        }

        l.exiting(getClass().getName(), "checkPRTConfirmationRequired", Boolean.valueOf(isPRTConfirmationRequired));
        return isPRTConfirmationRequired;
    }


    /**
     * lget initial values for  Renew Term Expiration
     *
     * @param policyHeader
     * @return policy type configured parameter
     */
    public YesNoFlag isRenewalTermExpirationRequired(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "isRenewalTermExpirationRequired", policyHeader);

        boolean isRenewalTermExpirationRequired = false;
        Record inputRecord = policyHeader.toRecord();
        //set fields' value
        inputRecord.setFieldValue("parmCode", "RENEXPDT");
        /* call dao method to get PRT */
        boolean isPolicyTypeConfiged = getRenewalProcessDAO().isPolicyTypeConfigured(inputRecord).booleanValue();
        if (isPolicyTypeConfiged) {
            isRenewalTermExpirationRequired = true;
        }

        l.exiting(getClass().getName(), "isRenewalTermExpirationRequired",
            Boolean.valueOf(isRenewalTermExpirationRequired));
        return YesNoFlag.getInstance(isRenewalTermExpirationRequired);
    }

    /**
     * validate renewal data
     *
     * @param policyHeader
     * @param inputRecord  *
     */
    protected void validateRenewal(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateRenewal",
            new Object[]{policyHeader, inputRecord});

        //validate pending renewal transaction has been confirmed
        //check if there is pending renewal term
        Record prtRecord = loadPendingRenewalTransaction(policyHeader);
        if (prtRecord != null) {
            String transId = prtRecord.getStringValue(TRANS_ID);
            if (transId != null && Long.parseLong(transId) != -1) {
                if (!inputRecord.hasStringValue("pm.maintainRenewal.confirm.applyPRT.confirmed")) {
                    MessageManager.getInstance().addConfirmationPrompt("pm.maintainRenewal.confirm.applyPRT", false);
                }
            }
        }

        //when underwriter manually set term expiration date,do the following validations
        if (inputRecord.hasStringValue(RenewalProcessFields.RENEWAL_TERM_EXP_DATE)) {
            //#1 Validation Errors for renewalTermExp date
            if (RenewalProcessFields.getRenewalTermExpDate(inputRecord) == null) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRenewal.renewalTermExpRequired.error",
                    RenewalProcessFields.RENEWAL_TERM_EXP_DATE);
            }
            String renewalTermEff = RenewalProcessFields.getRenewalTermEffDate(inputRecord);
            String renewalTermExp = RenewalProcessFields.getRenewalTermExpDate(inputRecord);
            if (!DateUtils.parseDate(renewalTermExp).after(DateUtils.parseDate(renewalTermEff))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainRenewal.expPriorToEff.error",
                    RenewalProcessFields.RENEWAL_TERM_EXP_DATE);
            }

            //#2 Available Policy Type
            Record inRec = policyHeader.toRecord();
            PolicyHeaderFields.setTermEffectiveFromDate(inRec, renewalTermEff);
            PolicyHeaderFields.setTermEffectiveToDate(inRec, renewalTermExp);
            new AvailablePolicyTypeRecordValidator(null, RenewalProcessFields.RENEWAL_TERM_EFF_DATE).validate(inRec);

            //#3 Valid Term Duration
            new ValidTermDurationRecordValidator(null, RenewalProcessFields.RENEWAL_TERM_EFF_DATE).validate(inRec);
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts())
            throw new ValidationException();

        l.exiting(getClass().getName(), "validateRenewal", Boolean.valueOf(
            MessageManager.getInstance().hasErrorMessages() || MessageManager.getInstance().hasConfirmationPrompts()));
    }

    /**
     * Validate auto renewal
     *
     * @param policyHeader
     */
    public void validateAutoRenewal(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAutoRenewal", new Object[]{policyHeader,});
        }

        Record inputRecord = policyHeader.toRecord();

        Record rec = getRenewalProcessDAO().validateAutoRenewal(inputRecord);
        if (rec.getStringValue("rc").equals("-1")) {
            MessageManager.getInstance().addErrorMessage(
                "pm.autoRenewal.validation.error", new String[]{rec.getStringValue("rmsg")});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAutoRenewal");
        }
    }

    /**
     * Perform auto renewal
     *
     * @param policyHeader
     */
    public void performAutoRenewal(PolicyHeader policyHeader, Record rec) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAutoRenewal", new Object[]{policyHeader,});
        }
        Record inputRecord = policyHeader.toRecord();

        // Check if need to prompt after auto renew
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_AUTOREN_BAT_SCHED)).booleanValue()) {
            // Check if the phase is valid to be added in batch
            Integer renewalEventId = null;
            if (getPolicyAttributesManager().isPhaseValidToBatch(policyHeader)) {
                // Get latest batch event
                renewalEventId = getBatchRenewalProcessManager().getLatestBatchForPolicy(policyHeader.toRecord());
            }

            Record record = getRenewalProcessDAO().performAutoRenewal(inputRecord);

            String renewB = record.getStringValue("return");
            if (null != renewB && renewB.equals("Y")) {
                // Get message from user session
                UserSession userSession = UserSessionManager.getInstance().getUserSession();
                if (!userSession.has(UserSessionIds.POLICY_SAVE_MESSAGE)) {
                    userSession.set(UserSessionIds.POLICY_SAVE_MESSAGE, new ArrayList<>());
                }
                List<Message> messageList = (List<Message>) userSession.get(UserSessionIds.POLICY_SAVE_MESSAGE);
                if (renewalEventId == null) {
                    // Set message
                    Message message = new Message();
                    message.setMessageCategory(MessageCategory.JS_MESSAGE);
                    message.setMessageKey("pm.batchRenewalProcess.processAutoRenewal.manually.phaseNotQualify.info");
                    messageList.add(message);
                }
                else if (getPolicyAttributesManager().isAutoRenewPromptEnableForRenewalBatch(policyHeader)) {
                    // Set message
                    Message message = new Message();
                    message.setMessageCategory(MessageCategory.CONFIRMATION_PROMPT);
                    if (renewalEventId != 0) {
                        // Set renewal event to user session
                        userSession.set(UserSessionIds.POLICY_BATCH_RENEWAL_ID, renewalEventId);
                        message.setMessageKey("pm.batchRenewalProcess.addToBatch.qualify.afterAutoRenew.prompt");
                    }
                    else {
                        if (userSession.has(UserSessionIds.POLICY_BATCH_RENEWAL_ID)) {
                            userSession.remove(UserSessionIds.POLICY_BATCH_RENEWAL_ID);
                        }
                        message.setMessageKey("pm.batchRenewalProcess.addToBatch.notQualify.afterAutoRenew.prompt");
                    }
                    messageList.add(message);
                    // Set message2
                    Message message2 = new Message();
                    message2.setMessageCategory(MessageCategory.JS_MESSAGE);
                    message2.setMessageKey("pm.batchRenewalProcess.processAutoRenewal.manually.info");
                    messageList.add(message2);
                }
            }
        }
        else {

            String confirmApplyPRT;
            if (rec.hasStringValue("pm.maintainRenewal.confirm.applyPRT.confirmed")) {
                confirmApplyPRT = rec.getStringValue("pm.maintainRenewal.confirm.applyPRT.confirmed");
            }
            else {
                confirmApplyPRT = "N";
            }
            RenewalProcessFields.setApplyPrtB(inputRecord, YesNoFlag.getInstance(confirmApplyPRT));

            getRenewalProcessDAO().performAutoRenewal(inputRecord);
        }
        // Reload policy header, so it will be in the latest term after refreshing
        getTransactionManager().setPolicyHeaderReloadCode(policyHeader, TransactionCode.AUTORENEW);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAutoRenewal");
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public RenewalProcessManagerImpl() {
    }

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getEntityManager() == null)
            throw new ConfigurationException("The required property 'entityManager' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getRenewalProcessDAO() == null)
            throw new ConfigurationException("The required property 'renewalProcessDAO' is missing.");
    }


    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public EntityManager getEntityManager() {
        return m_entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        m_entityManager = entityManager;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public PolicyAttributesManager getPolicyAttributesManager() {
        return m_policyAttributesManager;
    }

    public void setPolicyAttributesManager(PolicyAttributesManager policyAttributesManager) {
        m_policyAttributesManager = policyAttributesManager;
    }

    public BatchRenewalProcessManager getBatchRenewalProcessManager() {
        return m_batchRenewalProcessManager;
    }

    public void setBatchRenewalProcessManager(BatchRenewalProcessManager batchRenewalProcessManager) {
        m_batchRenewalProcessManager = batchRenewalProcessManager;
    }

    public RenewalProcessDAO getRenewalProcessDAO() {
        return m_renewalProcessDAO;
    }

    public void setRenewalProcessDAO(RenewalProcessDAO renewalProcessDAO) {
        m_renewalProcessDAO = renewalProcessDAO;
    }

    private PolicyManager m_policyManager;
    private TransactionManager m_transactionManager;
    private EntityManager m_entityManager;
    private LockManager m_lockManager;
    private RenewalProcessDAO m_renewalProcessDAO;
    private PolicyAttributesManager m_policyAttributesManager;
    private BatchRenewalProcessManager m_batchRenewalProcessManager;


    public static final String TRANS_ID = "transId";
    public static final String FAILED_RM_DISC = "FAILED_RM_DISC";
    public static final String FAILED = "FAILED";

}
