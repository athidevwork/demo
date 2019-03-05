package dti.pm.transactionmgr.reissueprocessmgr.impl;

import dti.oasis.util.FormatUtils;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.busobjs.TransactionCode;
import dti.pm.transactionmgr.reissueprocessmgr.dao.ReissueProcessDAO;
import dti.pm.transactionmgr.reissueprocessmgr.ReissueProcessManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.CreatePolicyFields;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.validationmgr.impl.AccountingMonthRecordValidator;
import dti.pm.validationmgr.impl.AvailablePolicyTypeRecordValidator;
import dti.pm.validationmgr.impl.ValidTermDurationRecordValidator;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.DateUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;

import java.util.logging.Logger;
import java.util.Date;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 22, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/18/2011       syang       116360 - Modified the validateForReissuePolicy() to validate effective date.
 * 08/30/2011       ryzhao      124458 - Modified validateForReissuePolicy to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 10/19/2011       wfu         125007 - Added logic to catch policy locking or policy picture changing
 *                                       validation exception for correct error message displaying.
 * ---------------------------------------------------
 */
public class ReissueProcessManagerImpl implements ReissueProcessManager {

    private static String EFFECTIVE_DATE_FIELD = "termEffectiveFromDate";
    private static String EXPIRATION_DATE_FIELD = "termEffectiveToDate";
    private static String actionClassName = "dti.pm.transactionmgr.reissueprocessmgr.struts.ReissuePolicyAction";

    /**
     *
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord  an input record from request prior to get the initial values
     * @return  record containing initial values for reissuing policy
     */

    public Record getInitialValuesForReissuePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForReissuePolicy", new Object[]{policyHeader, inputRecord});

        // get the initial values from wb bench
        Record outputRecord = getWorkbenchConfiguration().getDefaultValues(actionClassName);

        // 'add' inputRecord
        outputRecord.setFields(inputRecord);

        // overwrite with policyHeader.
        outputRecord.setFields(policyHeader.toRecord(), true);

        outputRecord.setFieldValue(EFFECTIVE_DATE_FIELD,"");
        outputRecord.setFieldValue(EXPIRATION_DATE_FIELD,"");

        // is it possible to lock down the policy? but do not lock it until we are about to createTransaction
        boolean canLockPolicy = getLockManager().canLockPolicy(policyHeader);
        if (!canLockPolicy) {
            MessageManager.getInstance().addErrorMessage("pm.transactionmgr.captureTransationDetails.cantLockPolicy.error");
            EntitlementFields.setReadOnly(outputRecord, YesNoFlag.Y);
        }
        else {
            EntitlementFields.setReadOnly(outputRecord, YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "getInitialValuesForReissuePolicy", outputRecord);
        return outputRecord;
    }

    /**
     *
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord  an input record being used for reissuing policy
     *  It throws a validation exception when the inputRecord does not pass the validation
     */

    public void reissuePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "reissuePolicy", new Object[]{policyHeader, inputRecord});

        // validate the inputRecord.
        validateForReissuePolicy(policyHeader, inputRecord);

        inputRecord.setFields(policyHeader.toRecord(),false);

        // to lock policy before any inserts..
        boolean policyLockedSuccessfully = getLockManager().lockPolicy(policyHeader, "reissuePolicy: lock before creating transaction for reissue.");
        //[90.4] policy locked (by others)
        if (!policyLockedSuccessfully) {
            String notLockedMessageKey = "pm.transactionmgr.reissueprocessmgr.error.policyNotLocked";
            MessageManager.getInstance().addErrorMessage(notLockedMessageKey, new String[]{policyHeader.getPolicyIdentifier().getPolicyLockMessage()});
            ValidationException ve = new ValidationException("Policy can not be locked for this process..");
            throw ve;
        }

        //check COI processing, add 2 fields to inputRecord: transactionCode, coiCarryForwardB,
        // which are used subsequently

        checkForCoiCarryForward(policyHeader, inputRecord);
        Transaction transaction = createReissueTransaction(policyHeader, inputRecord);

        // add transationLogId for reissuePolicy
        inputRecord.setFieldValue("transactionLogId",transaction.getTransactionLogId());
        getReissueProcessDAO().reissuePolicy(inputRecord);

        l.exiting(getClass().getName(), "reissuePolicy");
    }

    /**  
     *
     * @param policyHeader
     * @param inputRecord
     */
    protected void validateForReissuePolicy(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateForReissuePolicy", new Object[]{policyHeader, inputRecord});
        MessageManager messageManagerInstance = MessageManager.getInstance();

        String userEnteredExpirationDate = inputRecord.getStringValue(EXPIRATION_DATE_FIELD);

        // copy over the policyHeader information prior to validate
        inputRecord.setFields(policyHeader.toRecord(), false);

        String defaultTermExpirationDate = getPolicyManager().getDefaultTermExpirationDate(inputRecord);

        inputRecord.setFieldValue(EXPIRATION_DATE_FIELD,defaultTermExpirationDate);

        // overwridate termEffectiveFromdate, termEff ToDate with curr dates, they are used for many validations
        //if we do not do it, it will get the policyHeader's term dates incorrectly
        inputRecord.setFieldValue("termEffectiveFromDateFromHeader",policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("termEffectiveToDateFromHeader",policyHeader.getTermEffectiveToDate());

        // [90.5] validate accounting month ..using commonValidator
        AccountingMonthRecordValidator accountingMonthValidator = new AccountingMonthRecordValidator();
        accountingMonthValidator.validate(inputRecord);

        //[90.6.2.2.1 ~ 90.6.2.2.3]
        //the required validation performed by javascript, configued by wb
        
        // [90.6.2.6]
        Date effectiveFromDate = inputRecord.getDateValue(EFFECTIVE_DATE_FIELD);
        Date effectiveToDateFromHeader = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
        // The Effective From Data must be existing.
        if (!inputRecord.hasStringValue(EFFECTIVE_DATE_FIELD)) {
            messageManagerInstance.addErrorMessage("pm.transactionmgr.reissueprocessmgr.effDate.missing", EFFECTIVE_DATE_FIELD);
            throw new ValidationException("The effective date is missing.");
        }
        
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_VAL_PRIOR_REISSUE", "Y")).booleanValue()) {
            if (!messageManagerInstance.hasErrorMessages() &&
                 effectiveFromDate.before(effectiveToDateFromHeader)) {
                if (getReissueProcessDAO().isPolicyTermBooked(inputRecord)) { // policy has booked terms
                    String bookedTermDateKey = "pm.transactionmgr.reissueprocessmgr.error.bookedTermDate";
                    messageManagerInstance.addErrorMessage(bookedTermDateKey,
                        new String[]{FormatUtils.formatDateForDisplay(policyHeader.getTermEffectiveToDate())}, EFFECTIVE_DATE_FIELD);
                }
            }
        }

        Date effectiveToDate = inputRecord.getDateValue(EXPIRATION_DATE_FIELD);
        if (!messageManagerInstance.hasErrorMessages() &&
            !effectiveFromDate.before(effectiveToDate)) {
            String effectiveAfterExpirationDateKey = "pm.transactionmgr.reissueprocessmgr.error.effectiveAfterExpirationDate";
            messageManagerInstance.addErrorMessage(effectiveAfterExpirationDateKey, EXPIRATION_DATE_FIELD);
        }

        // validate RegionalOffice as same as createPolicy. 
        if (!messageManagerInstance.hasErrorMessages() &&
            YesNoFlag.getInstance(inputRecord.getStringValue(CreatePolicyFields.REGIONAL_OFFICE+"IsVisible")).booleanValue() &&
            StringUtils.isBlank(inputRecord.getStringValue(CreatePolicyFields.REGIONAL_OFFICE))) {
            messageManagerInstance.addErrorMessage("pm.createPolicy.regionlOffice.null.error",
                CreatePolicyFields.REGIONAL_OFFICE);
         }

        //[90.7] invalid combination..using commonValidator, with a messageKey
        String invalidCombinationKey = "pm.transactionmgr.reissueprocessmgr.error.invalidCombination";
        if (!messageManagerInstance.hasErrorMessages()) {
            AvailablePolicyTypeRecordValidator policyTypeValidator = new AvailablePolicyTypeRecordValidator(invalidCombinationKey);
            policyTypeValidator.validate(inputRecord);
        }

        //[90.8] overlaping term dates
        if (!messageManagerInstance.hasErrorMessages() &&
            getReissueProcessDAO().areTermDatesOverlapping(inputRecord)) {
            String overlappingKey = "pm.transactionmgr.reissueprocessmgr.error.overlappingTermDates";
            messageManagerInstance.addErrorMessage(overlappingKey, EFFECTIVE_DATE_FIELD);
        }

        //[UC90.9] Alternate Flow:  Invalid Term Duration.. using commonValidator
        if (!messageManagerInstance.hasErrorMessages()) {
            ValidTermDurationRecordValidator termDurationValidator = new ValidTermDurationRecordValidator();
            termDurationValidator.validate(inputRecord);
        }

        if (messageManagerInstance.hasErrorMessages()) {
            //set the date back, and throw validation exception
            inputRecord.setFieldValue(EXPIRATION_DATE_FIELD, userEnteredExpirationDate);
            ValidationException ve = new ValidationException("error occurred from the valiation process");
            l.throwing(getClass().getName(), "validateForReissuePolicy", ve);
            throw ve;
        }

        l.exiting(getClass().getName(), "validateForReissuePolicy");
    }

    /**
     * method to handle the creation of the renewal transaction.
     *
     * @param policyHeader
     * @param inputRecord
     * @return  the transaction that was created
     */
    private Transaction createReissueTransaction(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "createReissueTransaction", new Object[]{""});

        inputRecord.setFields(policyHeader.toRecord(),false);

        TransactionCode transactionCode = null;
        Transaction transaction = null;
        if (TransactionFields.hasTransactionCode(inputRecord)) {
            transactionCode = TransactionFields.getTransactionCode(inputRecord);
        }
        else {
            transactionCode = TransactionCode.getInstance(getReissueProcessDAO().getTransactionCodeForReissueRenewalTransaction(inputRecord));
        }

        String endorsementCode = "";
        // conditionally set endorsementCode per [UC87.17]
        if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_REISSUE_TO_RENEW", "N")).booleanValue()) {
            if (transactionCode.isManualRenewal()) {
                endorsementCode = "REISSUE";
                inputRecord.setFieldValue("endorsementcode",endorsementCode);
            }
        }

        String transactionEffectiveFromDate = inputRecord.getStringValue("termEffectiveFromDate");
        transaction = getTransactionManager().createTransaction(policyHeader, inputRecord, transactionEffectiveFromDate, transactionCode);

        l.exiting(getClass().getName(), "createReissueTransaction", transactionCode);
        return transaction;
    }

    /**
     * method to handle COI process.
     * It checks PM_COI_FORWARD system configuration to see if it needs to be generate a COI message first.
     * it aslo adds 2 fields to the records no matter what the above system parameter was configured:
     *   transactionCode: which is needed for createRenewalTransaction
     *   coiCarryForwardB: needed  for reissuePolicy
     */
    private void checkForCoiCarryForward(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "checkForCoiCarryForward", new Object[]{policyHeader, inputRecord});
        inputRecord.setFields(policyHeader.toRecord(),false);

        YesNoFlag coiCarryForward = YesNoFlag.Y;
        String forwardCoiConfigured = SysParmProvider.getInstance().getSysParm("PM_COI_FORWARD", "N");
        TransactionCode transactionCode = TransactionCode.getInstance(getReissueProcessDAO().getTransactionCodeForReissueRenewalTransaction(inputRecord));

        if (YesNoFlag.getInstance(forwardCoiConfigured).booleanValue() && transactionCode.isReissue()) {
            coiCarryForward = getReissueProcessDAO().isCoiCarriedForward(inputRecord);
            if (coiCarryForward.equals(YesNoFlag.N)) {
                MessageManager.getInstance().addInfoMessage("pm.transactionmgr.reissueprocessmgr.info.coiMessage");
            }
        }

        // add 2 fields, they are needed later
        inputRecord.setFieldValue("coiCarryForwardB", coiCarryForward);
        TransactionFields.setTransactionCode(inputRecord, transactionCode);
        
        l.exiting(getClass().getName(), "checkForCoiCarryForward");
    }

    /**
     * @param policyHeader the summary information for the policy being reissued
     * @param inputRecord a record that is used to get the term expiration date by OASIS
     * @return  record that is used by AJAX to set field values
     */
   public Record getExpirationDateForReissuePolicy(PolicyHeader policyHeader, Record inputRecord){
       Logger l = LogUtils.enterLog(this.getClass(), "getDefaultTermExpirationDate", new Object[]{policyHeader, inputRecord});
       inputRecord.setFields(policyHeader.toRecord(), false);
       String defaultTermExpirationDate = getPolicyManager().getDefaultTermExpirationDate(inputRecord);

       Record record = new Record();
       record.setFieldValue(EXPIRATION_DATE_FIELD,defaultTermExpirationDate);     

       l.exiting(getClass().getName(), "getDefaultTermExpirationDate", record);
       return record;
   }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {

        if (getReissueProcessDAO() == null)
            throw new ConfigurationException("The required property 'reissueProcessDAO' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyDAO' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public ReissueProcessDAO getReissueProcessDAO() {
        return m_reissueProcessDAO;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setReissueProcessDAO(ReissueProcessDAO reissueProcessDao) {
        m_reissueProcessDAO = reissueProcessDao;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private ReissueProcessDAO m_reissueProcessDAO;
    private TransactionManager m_transactionManager;
    private PolicyManager m_policyManager;
    private LockManager m_lockManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
}
