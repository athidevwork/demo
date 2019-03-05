package dti.pm.billingmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.billingmgr.BillingAccountChangeWSClientManager;
import dti.pm.billingmgr.BillingAccountInitialValuesWSClientManager;
import dti.pm.billingmgr.BillingManager;
import dti.pm.billingmgr.dao.BillingDAO;
import dti.pm.billingmgr.BillingAccountInquiryWSClientManager;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 1, 2007
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  04/02/2008      yhchen      fix issue 81091
 * 04/29/2009       yhyang      91531: Add the method getPolicyRelationValue().
 * 08/25/2010       bhong       110269 - Added isCoverageIdExists
 * 08/30/2011       ryzhao      124458 - Modified getInitialValuesForBilling and validateBillingWithDB to use FormatUtils.formatDateForDisplay()
 *                                       to format date when adding error messages.
 * 02/25/2013       kmv         142138 - Validate billing parameters only when the More button is clicked
 *                                     - Pass NULLs into SaveBilling when More button is not clicked
 * 04/17/2013       htwang      135366 - Add code to determine if the account holder name is editable
 * 07/26/2014       kmv         146420 - Undo the changes done for issue 79970. Account No equals policy no is not a good reason
 *                                       to set acctHolderIsPolHolderB ='Y'. Fixed logic in PM_Web_Billing.Get_Initial_Account_No
 * 11/12/2014       kmv         148461 - Set acctHolderIsPolHolderB flag based on registry
 *                                       BILLING_SETUP/DEFAULT_MODE/ACCTHLDR_IS_POLHLDR.
 * 01/31/2014       kmv         151384 - Change issueCompanyEntityId to issCompEntityId
 * 10/20/1014       awu         145137 - 1. Modified getInitialValuesForBilling to call webService to load the initial data.
 *                                       2. Removed validateBillingWithDB because these validations were moved to FM webService.
 *                                       3. Moved the validation logic from validateBillingWithoutDB to validateBillingForSave.
 *                                       4. Modified saveBilling to call the FM webService to save the billing data.
 * 05/04/2018       kmv         192317 - 1. Modified getInitialValuesForBilling to set the acctHolderIsPolHolderB to default web wb
 *                                          value when BILLING_SETUP/DEFAULT_MODE/ACCTHLDR_IS_POLHLDR is set to PAGE
 * ---------------------------------------------------
 */
public class BillingManagerImpl implements BillingManager {

    private static final String MAINTAIN_BILLING_ACTION_CLASS_NAME = "dti.pm.billingmgr.struts.MaintainBillingAction";

    /**
     * Method that gets the default values
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param policyHeader the summary information for the given policy
     * @param inputRecord  Record that contains input parameters
     * @return Record that contains default (init) values
     */
    public Record getInitialValuesForBilling(PolicyHeader policyHeader, Record inputRecord) {
        return getInitialValuesForBilling(policyHeader, inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
    }

    /**
     * Method that gets the default values with recordLoadProcessor
     * based on parameters stored in the input Record.
     * <p/>
     *
     * @param policyHeader        the summary information for the given policy
     * @param inputRecord         Record that contains input parameters
     * @param recordLoadProcessor a load processor to be applied when getting values from datasource
     * @return Record that contains default (init) values
     */
    public Record getInitialValuesForBilling(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(this.getClass(), "getInitialValuesForBilling", new Object[]{inputRecord, recordLoadProcessor});
        MessageManager messageManager = MessageManager.getInstance();

        inputRecord.setFields(policyHeader.toRecord(), false);

        // if something is wrong with the gettingInitialvaluesFor Billig, we
        // give the user option of displaying whatever we can and set the fields to readOnly.
        // we do not want to use MessageManager.addErrorMessage here, because we might need to display
        // the message as confirmPrompt. so we just have to add a special field isReadOnly to the record
        // based on the boolean value from ReadOnly
        boolean readOnly = false;
        String billingRelationExistMessageKey = "pm.maintainBilling.init.info.billingRelationExists";
        String addressNotExistMessageKey = "pm.maintainBilling.init.alert.addressNotExist";
        String paymentPlanNotExistMessageKey = "pm.maintainBilling.init.alert.paymentPlanNotExist";
        String agentNotSelectedMessageKey = "pm.maintainBilling.init.confirm.agentNotSelected";
        String customPaymentPlanMessageKey = "pm.maintainBilling.init.alert.customPamentPlan";
        String defaultPaymentPlanMessageKey = "pm.maintainBilling.init.alert.defaultPaymentPlan";
        String defaultPaymentPlanLevelMessageKey = "pm.maintainBilling.init.alert.defaultPaymentPlanLevel";
        String defaultMoreFieldsMessageKey = "pm.maintainBilling.init.alert.defaultMoreFields";

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_BILLING_ACTION_CLASS_NAME);
        inputRecord.setFields(defaultValuesRecord);
        RecordSet rs = getBillingDAO().getInitialValuesForBilling(inputRecord);
        Record outputRecord = rs.getFirstRecord();
        inputRecord.setFields(outputRecord);
        Record wsOutputRecord = getInitialValuesWSClientManager().getInitialValuesForBilling(policyHeader, inputRecord);
        outputRecord.setFields(wsOutputRecord);

        // if billingRelationExistsB is Y, then add Info Message for user..
        String billingRelationCreatedKey = "pm.maintainBilling.save.billingRelationCreated";
        if (messageManager.hasMessage(billingRelationCreatedKey)) {
            // containe createdkey, it means we just created the relation. we came here because
            // it was forward to here. so we can simply set readyOny to true and go it out
            readOnly = true;
        }
        else {
            if (outputRecord.getBooleanValue("billingRelationExistsB").booleanValue()) {
                messageManager.addInfoMessage(billingRelationExistMessageKey, new String[]{inputRecord.getStringValue("policyNo")});
                readOnly = true;
            }
            // if addresExistsB is N then add infor
            if (!outputRecord.getBooleanValue("addressExistsB").booleanValue()) {
                messageManager.addConfirmationPrompt(addressNotExistMessageKey, new String[]{inputRecord.getStringValue("policyNo")});
                readOnly = true;
            }
            // if validPlanExistsB is N, add a Error Message
            if (!outputRecord.getBooleanValue("validPlanExistsB").booleanValue()) {
                messageManager.addConfirmationPrompt(paymentPlanNotExistMessageKey, new String[]{inputRecord.getStringValue("policyNo"),
                    FormatUtils.formatDateForDisplay(inputRecord.getStringValue("termEffectiveFromDate"))});
                readOnly = true;
            }
            // if prompt is Y, then added it as info message
            if (outputRecord.getBooleanValue("promptNoAgentB").booleanValue()) {
                messageManager.addConfirmationPrompt(agentNotSelectedMessageKey, new String[]{inputRecord.getStringValue("policyNo"), inputRecord.getStringValue("termEffectiveFromDate")});
            }
            // if customPaymentPlanOK is N, add errorMessage
            if (!outputRecord.getBooleanValue("customPaymentPlanOK").booleanValue()) {
                messageManager.addErrorMessage(customPaymentPlanMessageKey);
                readOnly = true;
            }

            if (StringUtils.isBlank(outputRecord.getStringValue("paymentPlanId"))) {
                // Sharon suggested removing this error message for issue 96696.
                // messageManager.addErrorMessage(defaultPaymentPlanMessageKey);
                // Fix issue 98866:system should allow the user to select payment plan if there is no default. 
                // readOnly = true;
                if (!StringUtils.isBlank(outputRecord.getStringValue("defaultPlanLevel"))) {
                    messageManager.addInfoMessage(defaultPaymentPlanLevelMessageKey);
                }
            }
            // if unable to get values for more fields, add Error
            if (!outputRecord.getBooleanValue("getMoreValuesSuccessB").booleanValue()) {
                messageManager.addErrorMessage(defaultMoreFieldsMessageKey);
                readOnly = true;
            }

            boolean acctHolderIsPolHolder;
            if (outputRecord.hasStringValue("acctHolderIsPolHolderB")) {
                acctHolderIsPolHolder = YesNoFlag.getInstance(outputRecord.getStringValue("acctHolderIsPolHolderB")).booleanValue();
            } else {
                // When registry ACCTHLDR_IS_POLHLDR is set to PAGE use the web WB default value from inputRecord.
                acctHolderIsPolHolder = YesNoFlag.getInstance(defaultValuesRecord.getStringValue("acctHolderIsPolHolderB")).booleanValue();
                outputRecord.setFieldValue("acctHolderIsPolHolderB", defaultValuesRecord.getStringValue("acctHolderIsPolHolderB"));
            }

            // set initial value for accountHolderName
            if (acctHolderIsPolHolder &&
                StringUtils.isBlank(outputRecord.getStringValue("accountHolderName"))) {
                outputRecord.setFieldValue("accountHolderName", inputRecord.getStringValue("policyHolderName"));
            }

            // determine if accountNo and accountHolderName editable initially
            // logic was in db, moved to here as part of fix for issue 73564
            YesNoFlag isAccountNoEditable = isAccountNoEditable(acctHolderIsPolHolder);
            outputRecord.setFieldValue("isAccountNoEditable", isAccountNoEditable);
            outputRecord.setFieldValue("isAccountHolderNameEditable", isAccountNoEditable);
            // Set the acctHolderIsPolHolderB to isAcctMntAvailable for issue 91531
            outputRecord.setFieldValue("isAcctMntAvailable", YesNoFlag.getInstance(!acctHolderIsPolHolder));
            // Set flag isBillingSetupAvailable
            outputRecord.setFieldValue("isBillingSetupAvailable", YesNoFlag.getInstance(!outputRecord.getBooleanValue("brlCheck").booleanValue()));
        }

        // add field readOnly  to outputRecord
        EntitlementFields.setReadOnly(outputRecord, readOnly);
        // Set billingSetupB
        if (inputRecord.hasField("rc") && inputRecord.getIntegerValue("rc").intValue() != -1) {
            outputRecord.setFieldValue("billingSetupB", YesNoFlag.Y);
        }
        else {
            outputRecord.setFieldValue("billingSetupB", YesNoFlag.N);
        }
        l.exiting(getClass().getName(), "getInitialValuesForBilling", new Object[]{outputRecord});
        return outputRecord;
    }

    /**
     * Perform validation before calling webservice.
     * <p/>
     *
     * @param inputRecord Record that contains input parameters
     * @return void
     */
    protected void validateBillingForSave(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "validateBillingForSave", new Object[]{inputRecord});

        // populate values for checkbox fields to save some headache
        // because if unchecked, they are not passed in as part of the request parameters

        if (!inputRecord.hasStringValue("acctHolderIsPolHolderB")) {
            inputRecord.setFieldValue("acctHolderIsPolHolderB", "N");
        }

        if (!inputRecord.hasStringValue("singlePolicyB")) {
            inputRecord.setFieldValue("singlePolicyB", "N");
        }

        // per Gautami, This field is not really used by M1 delivery
        if (!inputRecord.hasStringValue("brlCheck")) {
            inputRecord.setFieldValue("brlCheck", "N");
        }

        MessageManager messageManager = MessageManager.getInstance();

        String baseBillMonthDay = inputRecord.getStringValue("baseBillMonthDay");
        String billLeadDays = inputRecord.getStringValue("billLeadDays");
        String billingFrequency = "";
        if (inputRecord.hasStringValue("billingFrequency")) {
            billingFrequency = inputRecord.getStringValue("billingFrequency");
        }
        String acctHolderIsPolHolderB = inputRecord.getStringValue("acctHolderIsPolHolderB");
        String accountNo = inputRecord.getStringValue("accountNo");
        String singlePolicyB = inputRecord.getStringValue("singlePolicyB");
        String showMoreFlag = inputRecord.getStringValue("showMoreFlag");

        String noBillingFrequencyMessageKey = "pm.maintainBilling.validate.error.noBillingFrequency";
        String noBaseBillMonthDayMessageKey = "pm.maintainBilling.validate.error.noBaseBillMonthDay";
        String noBillLeadDaysMessageKey = "pm.maintainBilling.validate.error.noBillLeadDays";
        String noAccountNoMessageKey = "pm.maintainBilling.validate.error.noAccountNoEntered";
        String invalidPaymentPlanMessageKey = "pm.maintainBilling.validate.error.invalidPaymentPlan";
        // for singlePolicyB ='Y', simply null out the values for
        // BaseBillMonthDay,BillLeadDays,BillingFrequency,NextBillingDate
        // javascript did this. but we do it again for webservices
        // do this only when the account holder is same as policy holder and "More Fields" area is expanded

        if (YesNoFlag.getInstance(acctHolderIsPolHolderB).booleanValue() && YesNoFlag.getInstance(showMoreFlag).booleanValue()) {
            if (singlePolicyB.equalsIgnoreCase("Y")) {
                inputRecord.setFieldValue("baseBillMonthDay", "");
                inputRecord.setFieldValue("billLeadDays", "");
                inputRecord.setFieldValue("billingFrequency", "");
                inputRecord.setFieldValue("nextBillingDate", "");
            }
            else { // for billlingFrequency P, simply null out the values
                if (billingFrequency.equalsIgnoreCase("P")) {
                    inputRecord.setFieldValue("baseBillMonthDay", "");
                    inputRecord.setFieldValue("billLeadDays", "");
                    inputRecord.setFieldValue("nextBillingDate", "");
                }
                else {  // not P, nor single Policy.
                    if (StringUtils.isBlank(baseBillMonthDay) && ((!StringUtils.isBlank(billLeadDays)) || (!StringUtils.isBlank(billingFrequency)))) {
                        messageManager.addErrorMessage(noBaseBillMonthDayMessageKey, "baseBillMonthDay");
                    }
                    if (StringUtils.isBlank(billLeadDays) && ((!StringUtils.isBlank(baseBillMonthDay)) || (!StringUtils.isBlank(billingFrequency)))) {
                        messageManager.addErrorMessage(noBillLeadDaysMessageKey, "billLeadDays");
                    }
                    if (StringUtils.isBlank(billingFrequency) && ((!StringUtils.isBlank(baseBillMonthDay)) || (!StringUtils.isBlank(billLeadDays)))) { // billingFrequency is blank, and one of the billing data is not blank
                        messageManager.addErrorMessage(noBillingFrequencyMessageKey, "billingFrequency");
                    }
                }
            }
        }

        // if the accountHolder is not same as pol holder, user can edit the accout No,
        // so we need to validate acccoutNo field
        if (!YesNoFlag.getInstance(acctHolderIsPolHolderB).booleanValue() && StringUtils.isBlank(accountNo)) {
            messageManager.addErrorMessage(noAccountNoMessageKey, "accountNo");
        }

        if (!inputRecord.hasStringValue("paymentPlanId")) {
            messageManager.addErrorMessage(invalidPaymentPlanMessageKey, new String[]{""}, "paymentPlanId");
        }

        // throw a validationException if ..
        if (messageManager.hasErrorMessages()) {
            ValidationException ve = new ValidationException("validation error(s) found by validateBillingWithoutDB from BillingManageImpl");
            l.throwing(getClass().getName(), "validateBillingForSave", ve);
            throw ve;
        }

        // When More>> fields are collapsed set the billing parameters to NULL before save
        if (inputRecord.getStringValue("showMoreFlag").equalsIgnoreCase("N") ) {
            inputRecord.setFieldValue("baseBillMonthDay", "");
            inputRecord.setFieldValue("billLeadDays", "");
            inputRecord.setFieldValue("billingFrequency", "");
            inputRecord.setFieldValue("nextBillingDate", "");
            inputRecord.setFieldValue("singlePolicyB", "");
            inputRecord.setFieldValue("accountingMode", "");
            inputRecord.setFieldValue("accountType", "");
            inputRecord.setFieldValue("overdueRule", "");
            inputRecord.setFieldValue("issCompEntityId", "");
        }

        l.exiting(getClass().getName(), "validateBillingForSave", new Object[]{inputRecord});
    }

    /**
     * Method that saves billing data based on parameters stored in the input Record.
     * It calls validationBillingForSave first.
     * and if the validationReturns false, it will not call webService to save
     * <p/>
     *
     * @param policyHeader summaryInforamtion for the policy whose
     *                     billing information is about to be saved
     * @param inputRecord  Record that contains input parameters
     * @return Record returned by webService merged with the inputRecord for redisplay
     */
    public Record saveBilling(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "saveBilling", new Object[]{inputRecord});

        inputRecord.setFields(policyHeader.toRecord(), false);

        validateBillingForSave(inputRecord);

        BillingAccountInquiryWSClientManager inquiryWSClientManager = getBillingAccountInquiryWSClientManager();
        boolean isBillingExists = inquiryWSClientManager.isBillingExists(policyHeader, inputRecord);

        Record outputRecord = getBillingAccountChangeWSClientManager().saveBillingForEPolicy(policyHeader, inputRecord, isBillingExists);
        inputRecord.setFields(outputRecord);

        // Set billingSetupB
        if (inputRecord.hasField("rc") && inputRecord.getIntegerValue("rc").intValue() != -1) {
            inputRecord.setFieldValue("billingSetupB", YesNoFlag.Y);
        }
        else {
            inputRecord.setFieldValue("billingSetupB", YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "saveBilling", new Object[]{inputRecord});
        return inputRecord;
    }

    /**
     * Method that updates billing account data.
     *
     * @param inputRecord Record that contains input parameters
     */
    public void updateBillingAccount(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "updateBillingAccount", new Object[]{inputRecord});

        getBillingDAO().updateBillingAccount(inputRecord);

        l.exiting(getClass().getName(), "updateBillingAccount");
    }

    /**
     * Get the policy relation value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getPolicyRelationValue(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "getPolicyRelationValue", new Object[]{inputRecord});

        Record outputRecord = new Record();
        String returnValue = getBillingDAO().getPolicyRelationValue(inputRecord);
        // Set returnValue to the field existB.
        outputRecord.setFieldValue("existB", returnValue);
        l.exiting(getClass().getName(), "getPolicyRelationValue", new Object[]{outputRecord});

        return outputRecord;
    }

    /**
     * Check if coverage id exists
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isCoverageIdExists(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCoverageIdExists", new Object[]{policyHeader,});
        }

        boolean isCoverageIdExists = getBillingDAO().isCoverageIdExists(policyHeader.toRecord());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCoverageIdExists", Boolean.valueOf(isCoverageIdExists));
        }
        return isCoverageIdExists;
    }

    /**
         * To validate if an account already exists for a given entityId
         *
         * @param inputRecord
         */
        public Record validateAccountExistsForEntity(Record inputRecord) {
            Logger l = LogUtils.getLogger(getClass());
            if (l.isLoggable(Level.FINER)) {
                l.entering(getClass().getName(), "validateAccountExistsForEntity", new Object[]{inputRecord});
            }

            Record valRec = getBillingDAO().validateAccountExistsForEntity(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "validateAccountExistsForEntity");
            }

            return valRec;
        }

    // private method to determine if accountNo should be editable.
    // the logic was moved here from database  to be consistent:
    // if accountHoler is PolicyHolder, it is not editable.
    // if it is not policyHolder, and parameter FM_BS_ENTER_ACCT configured to be Y, then accountNo is editable

    private YesNoFlag isAccountNoEditable(boolean accountHolderIsPolicyHolder) {
        YesNoFlag accountNoEditable = YesNoFlag.N;
        if (!accountHolderIsPolicyHolder) {
            String editableFromOasis = SysParmProvider.getInstance().getSysParm("FM_BS_ENTER_ACCT", "N");
            if ("Y".equalsIgnoreCase(editableFromOasis)) {
                accountNoEditable = YesNoFlag.Y;
            }
        }

        return accountNoEditable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getBillingDAO() == null) {
            throw new ConfigurationException("The required property 'billindao' is missing.");
        }

        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public void setBillingDAO(BillingDAO billingDAO) {
        m_billingDAO = billingDAO;
    }

    public BillingDAO getBillingDAO() {
        return m_billingDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public BillingAccountInitialValuesWSClientManager getInitialValuesWSClientManager() {
        return m_initialValuesWSClientManager;
    }

    public void setInitialValuesWSClientManager(BillingAccountInitialValuesWSClientManager initialValuesWSClientManager) {
        m_initialValuesWSClientManager = initialValuesWSClientManager;
    }

    public BillingAccountInquiryWSClientManager getBillingAccountInquiryWSClientManager() {
        return m_billingAccountInquiryWSClientManager;
    }

    public void setBillingAccountInquiryWSClientManager(BillingAccountInquiryWSClientManager billingAccountInquiryWSClientManager) {
        m_billingAccountInquiryWSClientManager = billingAccountInquiryWSClientManager;
    }

    public BillingAccountChangeWSClientManager getBillingAccountChangeWSClientManager() {
        return m_billingAccountChangeWSClientManager;
    }

    public void setBillingAccountChangeWSClientManager(BillingAccountChangeWSClientManager billingAccountChangeWSClientManager) {
        m_billingAccountChangeWSClientManager = billingAccountChangeWSClientManager;
    }

    private BillingDAO m_billingDAO;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private BillingAccountInitialValuesWSClientManager m_initialValuesWSClientManager;
    private BillingAccountInquiryWSClientManager m_billingAccountInquiryWSClientManager;
    private BillingAccountChangeWSClientManager m_billingAccountChangeWSClientManager;
}
