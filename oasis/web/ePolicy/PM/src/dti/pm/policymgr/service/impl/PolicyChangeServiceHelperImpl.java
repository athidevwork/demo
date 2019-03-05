package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.account.*;
import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policy.MedicalMalpracticePolicyType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeRequestType;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.service.WebServiceClientHelper;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.ows.common.MessageStatusHelper;
import dti.pm.billingmgr.BillingAccountChangeWSClientManager;
import dti.pm.billingmgr.BillingFields;
import dti.pm.billingmgr.BillingManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.service.PolicyChangeServiceHelper;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.validationmgr.ResponseTypeEnum;
import dti.pm.policymgr.validationmgr.SoftValidationManager;
import dti.pm.transactionmgr.NotifyFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.workflowmgr.jobqueuemgr.impl.JobProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   01/19/2016
 *
 * @author ssheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/19/2016        ssheng      168559 - Created this help class for reusing the method.
 * 01/30/2016        eyin        168882 - Modified performBillingSetupTransaction() and validateBillingSetup() to change
 *                                        PrincipalBillingAccountInformationType element from a single node to a list.
 * 02/03/2016        wdang       169198 - Modified performBillingSetupTransaction() to correct the name of
 *                                        MedicalMalpracticePolicyType.getPrincipalBillingAccountInformation().
 * 05/16/2016        lzhang      170647 - 1)Modified performRateAction: add product notification
 *                                          validation about prerate, postrate and hard validation
 *                                          and display them in messageStatusType
 *                                        2)Modified performIssuePolicy add product notification
 *                                          validation with OFFICIAL and display them
 *                                          in <messageStatus> xml
 *                                        3)Modified PerformValidations: convert error recordSet
 *                                          to messageStatusType
 *                                        4)Add performValidationsForNotify: get INVALID
 *                                          information about product notify and
 *                                          convert it to messageStatusType as XML
 * 01/19/2017        wrong       166929 - Modified performIssuePolicy: add logic to return the status of
 *                                        SuccessWithInformation when ignoreSoftValidations action code provided
 * 04/17/2017        tzeng       166929 - 1)Modified performValidationsForNotify to record soft validation.
 *                                        2)Modified performRateAction: add logic to return the status of
 *                                        SuccessWithInformation when ignoreSoftValidations, ISSUE action code provided
 *                                        and the soft validation existed.
 * 06/15/2018        athi        193637 - Modified performBillingSetupTransaction to use the principal billing request
                                          information from the input xml. This was to support creating a policy with annual
										  payment plan specified in the request.
 * ---------------------------------------------------
 */
public class PolicyChangeServiceHelperImpl implements PolicyChangeServiceHelper {

    /*
     * Perform Rate.
     */
    public MessageStatusType performRateAction(PolicyHeader policyHeader, TransactionManager transactionManager, Boolean isIgnoreSoftValidationB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performRateAction", policyHeader);
        }
        MessageStatusType messageStatusType = new MessageStatusType();
        Record inputRecord = policyHeader.toRecord();
        performValidations(policyHeader, transactionManager, messageStatusType);
        performValidationsForNotify(policyHeader, transactionManager, NotifyFields.PRERATE, messageStatusType);

        if (messageStatusType.getMessageStatusCode() == null ||
            (MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode()) && isIgnoreSoftValidationB)) {
            String result = transactionManager.performTransactionRating(inputRecord);
            if ("FAILED".equalsIgnoreCase(result)) {
                MessageManager mm = MessageManager.getInstance();
                mm.addErrorMessage("pm.validateAndRateTransaction.error.rating", new String[]{policyHeader.getPolicyNo()});
                throw new AppException("Error: cannot rate policy " + policyHeader.getPolicyNo());
            }
            performValidationsForNotify(policyHeader, transactionManager, NotifyFields.POSTRATE, messageStatusType);

            if (MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode()) && isIgnoreSoftValidationB) {
                messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_SUCCESS_WITH_INFO);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performRateAction", messageStatusType);
        }
        return messageStatusType;
    }

    public void performBillingSetupTransaction(PolicyHeader policyHeader, MedicalMalpracticePolicyChangeRequestType policyChangeRequest,
                                               boolean isIssueActionB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performBillingSetupTransaction", new Object[]{});
        }

        //Build FM webService path and set to RequestStorageManager, it will be used later.
        String fmWebServicePath = WebServiceClientHelper.getInstance().buildWebServicePath(WebServiceClientHelper.getInstance().ePOLICY_wsPOLICY,
            WebServiceClientHelper.getInstance().eFM_wsFM);
        RequestStorageManager.getInstance().set("FM_SERVICE_PATH", fmWebServicePath);

        Boolean defaultAccountB = false;
        MedicalMalpracticePolicyType currentPolicy = null;
        PrincipalBillingAccountInformationType principalBillingAccount = null;

        if (policyChangeRequest == null) {
            //Use all the default billing data if account number is empty.
            defaultAccountB = true;
        }
        else {
            List<MedicalMalpracticePolicyType> policyList = policyChangeRequest.getMedicalMalpracticePolicy();
            for (MedicalMalpracticePolicyType policy : policyList) {
                if (policyHeader.getPolicyNo().equals(policy.getPolicyId())) {
                    currentPolicy = policy;
                    break;
                }
            }
            if (currentPolicy == null) {
                return;
            }

            List<PrincipalBillingAccountInformationType> principalBillingAccountList = currentPolicy.getPrincipalBillingAccountInformation();
            if(principalBillingAccountList != null && principalBillingAccountList.size() > 0){
                principalBillingAccount = principalBillingAccountList.get(0);
            }

            if (principalBillingAccount == null || StringUtils.isBlank(principalBillingAccount.getBillingAccountId())) {
                //Use all the default billing data if account number is empty.
                defaultAccountB = true;
            }
            else {
                validateBillingSetup(principalBillingAccount, isIssueActionB);
            }
        }

        if (defaultAccountB) {
            //Use all the default billing data if account number is empty.
            PrincipalBillingAccountInformationType mergedPrincipalBillingAccount = new PrincipalBillingAccountInformationType();
            Record inputRecord = new Record();

            //Call FM webService BillingAccountInitialValuesService to get the default billing data.
            Record defaultBillingRecord = getBillingManager().getInitialValuesForBilling(policyHeader, inputRecord);
            mergeBillingRecordsFromRequestToDefaultBillingRecord(policyHeader,
                                                                    principalBillingAccount,
                                                                    mergedPrincipalBillingAccount,
                                                                    defaultBillingRecord);
            //Call FM webService BillingAccountChangeService to save the billing data.
            getBillingAccountChangeWSClientManager().saveBillingForPolicyChangeService(mergedPrincipalBillingAccount, policyHeader);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performBillingSetupTransaction");
        }
    }

    private void mergeBillingRecordsFromRequestToDefaultBillingRecord(PolicyHeader policyHeader,
                                                                      PrincipalBillingAccountInformationType principalBillingAccount,
                                                                      PrincipalBillingAccountInformationType mergedPrincipalBillingAccount,
                                                                      Record defaultBillingRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeBillingRecordsFromRequestToDefaultBillingRecord", new Object[]{});
        }

        if (principalBillingAccount != null) {
            if (principalBillingAccount.getBillingAccountId() != "") {
                mergedPrincipalBillingAccount.setBillingAccountId(principalBillingAccount.getBillingAccountId());
                principalBillingAccount.setBillingAccountId(principalBillingAccount.getBillingAccountId());
            }
            else {
                mergedPrincipalBillingAccount.setBillingAccountId(BillingFields.getAccountNo(defaultBillingRecord));
                principalBillingAccount.setBillingAccountId(BillingFields.getAccountNo(defaultBillingRecord));
            }

            if (principalBillingAccount.getBillingAccountNumberId() != "") {
                mergedPrincipalBillingAccount.setBillingAccountNumberId(principalBillingAccount.getBillingAccountNumberId());
                principalBillingAccount.setBillingAccountNumberId(principalBillingAccount.getBillingAccountNumberId());
            }
            else {
                mergedPrincipalBillingAccount.setBillingAccountNumberId(BillingFields.getBillingAccountId(defaultBillingRecord));
                principalBillingAccount.setBillingAccountNumberId(BillingFields.getBillingAccountId(defaultBillingRecord));
            }

            if (principalBillingAccount.getBillingAccountDetail() != null) {
                BillingAccountDetailType acctDetailRequest = principalBillingAccount.getBillingAccountDetail();
                BillingAccountDetailType accountDetail = new BillingAccountDetailType();

                if (acctDetailRequest.getBillingAccountingMode() != "")
                    accountDetail.setBillingAccountingMode(acctDetailRequest.getBillingAccountingMode());
                else
                    accountDetail.setBillingAccountingMode(BillingFields.getAccountingMode(defaultBillingRecord));

                if (acctDetailRequest.getBillingAccountType() != "")
                    accountDetail.setBillingAccountType(acctDetailRequest.getBillingAccountType());
                else
                    accountDetail.setBillingAccountType(BillingFields.getAccountType(defaultBillingRecord));

                if (acctDetailRequest.getBillLeadDays() != "")
                    accountDetail.setBillLeadDays(acctDetailRequest.getBillLeadDays());
                else
                    accountDetail.setBillLeadDays(BillingFields.getBillLeadDays(defaultBillingRecord));

                if (acctDetailRequest.getBillOverdueRuleCode() != "")
                    accountDetail.setBillOverdueRuleCode(acctDetailRequest.getBillOverdueRuleCode());
                else
                    accountDetail.setBillOverdueRuleCode(BillingFields.getOverdueRule(defaultBillingRecord));

                if (acctDetailRequest.getIndividualAccountIndicator() != "")
                    accountDetail.setIndividualAccountIndicator(acctDetailRequest.getIndividualAccountIndicator());
                else
                    accountDetail.setIndividualAccountIndicator(BillingFields.getAcctHolderIsPolHolderB(defaultBillingRecord));

                if (acctDetailRequest.getNextBillDate() != "")
                    accountDetail.setNextBillDate(acctDetailRequest.getNextBillDate());
                else
                    accountDetail.setNextBillDate(BillingFields.getNextBillingDate(defaultBillingRecord));

                if (acctDetailRequest.getBaseMonthDate() != "")
                    accountDetail.setBaseMonthDate(acctDetailRequest.getBaseMonthDate());
                else
                    accountDetail.setBaseMonthDate(BillingFields.getBaseBillMonthDay(defaultBillingRecord));

                if (acctDetailRequest.getBillFrequencyCode() != "")
                    accountDetail.setBillFrequencyCode(acctDetailRequest.getBillFrequencyCode());
                else
                    accountDetail.setBillFrequencyCode(BillingFields.getBillingFrequency(defaultBillingRecord));

                IssueCompanyType issueCompany = new IssueCompanyType();
                if (acctDetailRequest.getIssueCompany() != null &&
                        acctDetailRequest.getIssueCompany().getControllingStateOrProvinceCode() != "")
                    issueCompany.setControllingStateOrProvinceCode(acctDetailRequest.getIssueCompany().getControllingStateOrProvinceCode());
                else
                    issueCompany.setControllingStateOrProvinceCode(policyHeader.getIssueStateCode());

                ReferredPartyType partyType = new ReferredPartyType();
                if (acctDetailRequest.getIssueCompany() != null &&
                        acctDetailRequest.getIssueCompany().getReferredParty() != null) {
                    ReferredPartyType referredParty = acctDetailRequest.getIssueCompany().getReferredParty();
                    if (referredParty.getPartyNumberId()!= "")
                        partyType.setPartyNumberId(referredParty.getPartyNumberId());
                    else
                        partyType.setPartyNumberId(policyHeader.getIssueCompanyEntityId());

                    issueCompany.setReferredParty(partyType);
                    accountDetail.setIssueCompany(issueCompany);
                }
                mergedPrincipalBillingAccount.setBillingAccountDetail(accountDetail);
                principalBillingAccount.setBillingAccountDetail(accountDetail);
            }

            if (principalBillingAccount.getAccountHolder() != null) {
                AccountHolderType accountHolderRequest = principalBillingAccount.getAccountHolder().get(0);
                AccountHolderType accountHolder = new AccountHolderType();

                if (accountHolderRequest.getAccountHolderName() != "")
                    accountHolder.setAccountHolderName(accountHolderRequest.getAccountHolderName());

                EffectivePeriodType effectivePeriod = new EffectivePeriodType();
                if (accountHolderRequest.getEffectivePeriod() != null &&
                        accountHolderRequest.getEffectivePeriod().getStartDate() != "")
                    effectivePeriod.setStartDate(accountHolderRequest.getEffectivePeriod().getStartDate());
                else
                    effectivePeriod.setStartDate(DateUtils.parseOasisDateToXMLDate(policyHeader.getTermEffectiveFromDate()));
                accountHolder.setEffectivePeriod(effectivePeriod);

                ReferredPartyType accountParty = new ReferredPartyType();
                if (accountHolderRequest.getReferredParty().getPartyNumberId() != "")
                    accountParty.setPartyNumberId(accountHolderRequest.getReferredParty().getPartyNumberId());
                else
                    accountParty.setPartyNumberId(policyHeader.getPolicyHolderNameEntityId());
                accountHolder.setReferredParty(accountParty);

                mergedPrincipalBillingAccount.getAccountHolder().add(accountHolder);
                principalBillingAccount.getAccountHolder().add(accountHolder);
            }

            if (principalBillingAccount.getLinkedPolicy() != null) {
                LinkedPolicyType linkedPolicyRequest = principalBillingAccount.getLinkedPolicy().get(0);
                LinkedPolicyType linkedPolicy = new LinkedPolicyType();
                if (linkedPolicyRequest.getPolicyId() != "" )
                    linkedPolicy.setPolicyId(linkedPolicyRequest.getPolicyId());
                else
                    linkedPolicy.setPolicyId(policyHeader.getPolicyNo());

                PaymentOptionType paymentOption = new PaymentOptionType();
                if (linkedPolicyRequest.getPaymentOption().get(0).getPaymentPlanId() != "" )
                    paymentOption.setPaymentPlanId(linkedPolicyRequest.getPaymentOption().get(0).getPaymentPlanId());
                else
                    paymentOption.setPaymentPlanId(BillingFields.getPaymentPlanId(defaultBillingRecord));

                linkedPolicy.getPaymentOption().add(paymentOption);
                mergedPrincipalBillingAccount.getLinkedPolicy().add(linkedPolicy);
                principalBillingAccount.getLinkedPolicy().set(0, linkedPolicy);

                List<LinkedPolicyType> linkedPolicyList = principalBillingAccount.getLinkedPolicy();
                for (int i = 0; i < linkedPolicyList.size(); i++) {
                    LinkedPolicyType policy = linkedPolicyList.get(i);
                    policy.setPolicyId(policyHeader.getPolicyNo());
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mergeBillingRecordsFromRequestToDefaultBillingRecord");
        }
    }

    public MessageStatusType performIssuePolicy(PolicyHeader policyHeader, TransactionManager transactionManager, Boolean isIgnoreSoftValidationActionB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performIssuePolicy", new Object[]{});
        }
        MessageStatusType messageStatusType = new MessageStatusType();
        performValidationsForNotify(policyHeader, transactionManager, NotifyFields.OFFICIAL, messageStatusType);
        if (messageStatusType.getMessageStatusCode() == null || isIgnoreSoftValidationActionB) {
            if (MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode()) && isIgnoreSoftValidationActionB) {
                messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_SUCCESS_WITH_INFO);
            }
            Record inputRecord = policyHeader.toRecord();
            inputRecord.setFieldValue(PolicyInquiryFields.NEW_SAVE_OPTION, "OFFICIAL");
            transactionManager.processSaveTransactionOfficialForWS(policyHeader, inputRecord);

            RecordSet relatedErrors;
            try {
                relatedErrors = transactionManager.loadRelatedPolicySaveError(inputRecord);
            }
            catch (AppException appException) {
                // Valid case. No Related policy errors were found.
                relatedErrors = new RecordSet();
            }
            if (relatedErrors.getSize() > 0) {
                Iterator relatedErrorIte = relatedErrors.getRecords();
                List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
                while (relatedErrorIte.hasNext()) {
                    Record relatedErrorRec = (Record) relatedErrorIte.next();
                    String errorMsg = "";
                    String relatedPolicy = "";
                    if (relatedErrorRec.hasStringValue(PolicyInquiryFields.ISSUE_POLICY_RELATED_ERROR)) {
                        errorMsg = relatedErrorRec.getStringValue(PolicyInquiryFields.ISSUE_POLICY_RELATED_ERROR);
                    }
                    if (relatedErrorRec.hasStringValue(PolicyInquiryFields.ISSUE_POLICY_RELATED_POLICY_NO)) {
                        relatedPolicy = relatedErrorRec.getStringValue(PolicyInquiryFields.ISSUE_POLICY_RELATED_POLICY_NO);
                    }
                    if (!StringUtils.isBlank(errorMsg) && !StringUtils.isBlank(relatedPolicy)) {
                        errorMsg = errorMsg + "  Related Policy: " + relatedPolicy;
                    }
                    ExtendedStatusType extendedStatusType = new ExtendedStatusType();
                    extendedStatusType.setExtendedStatusDescription(errorMsg);
                    extendedStatusType.setExtendedStatusCode("Related Policy Warning");
                    extendedStatusTypes.add(extendedStatusType);
                }
                if (extendedStatusTypes.size() > 0) {
                    messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_SUCCESS_WITH_INFO);
                }
                messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performIssuePolicy");
        }

        return messageStatusType;
    }

    /*
    * Perform transaction validation.
    */
    private void performValidations(PolicyHeader policyHeader, TransactionManager transactionManager, MessageStatusType messageStatusType) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performValidations", new Object[]{});
        }

        RecordSet validationErrorSet = new RecordSet();
        //Set requestStoragemanager to avoid app error in LongRunningTransactionInteceptor.invoke if the policy is long policy.
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        rsm.set(JobProcessor.REQUEST_ID_PROCESS_JOB, "processSaveWip");

        Record policyRecord = policyHeader.toRecord();
        String validateResult = transactionManager.performTransactionValidation(policyRecord);
        if ("INVALID".equals(validateResult)) {
            validationErrorSet = transactionManager.loadAllValidationError(policyRecord);
        }

        if (validationErrorSet.getSize() > 0){
            Iterator validationErrorSetIte = validationErrorSet.getRecords();
            List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
            while (validationErrorSetIte.hasNext()){
                Record validationError = (Record) validationErrorSetIte.next();
                ExtendedStatusType extendedStatusType = new ExtendedStatusType();
                extendedStatusType.setExtendedStatusCode(validationError.getStringValue("msgCode"));
                extendedStatusType.setExtendedStatusType("Error");
                extendedStatusType.setExtendedStatusDescription(validationError.getStringValue("validationError"));
                extendedStatusTypes.add(extendedStatusType);
            }
            messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
            messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_SAVED_WITH_ERRORS);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performValidations");
        }
    }


    private void validateBillingSetup(PrincipalBillingAccountInformationType principalBillingAccountInformation, boolean isIssueActionB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateBillingSetup", new Object[]{});
        }

        MessageManager mm = MessageManager.getInstance();

        boolean validateResult = true;
        if (principalBillingAccountInformation.getLinkedPolicy() == null
            || principalBillingAccountInformation.getLinkedPolicy().size() == 0) {
            validateResult = false;
        }

        //Payment plan is required.
        if (validateResult) {
            for (LinkedPolicyType linkedPolicy : principalBillingAccountInformation.getLinkedPolicy()) {
                if (linkedPolicy.getPaymentOption() == null || linkedPolicy.getPaymentOption().size() == 0) {
                    validateResult = false;
                }
                else {
                    for (PaymentOptionType paymentOption : linkedPolicy.getPaymentOption()) {
                        if (StringUtils.isBlank(paymentOption.getPaymentPlanId())) {
                            validateResult = false;
                        }
                    }
                }
            }
        }

        if (principalBillingAccountInformation.getBillingAccountDetail() == null) {
            validateResult = false;
        }

        if (principalBillingAccountInformation.getAccountHolder() == null
            || principalBillingAccountInformation.getAccountHolder().size() == 0) {
            validateResult = false;
        }

        if (isIssueActionB) {
            if (!validateResult) {
                mm.addErrorMessage("ws.policy.change.billing.setup.incomplete.information");
            }
        }

        if (mm.hasErrorMessages()) {
            throw new AppException("Billing Setup validate failed.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateBillingSetup");
        }
    }

    private void performValidationsForNotify(PolicyHeader policyHeader, TransactionManager transactionManager, String notifyLevel, MessageStatusType messageStatusType){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performValidationsForNotify", new Object[]{});
        }

        // Delete validation
        if (notifyLevel.equals(NotifyFields.PRERATE)) {
            getSoftValidationManager().processSoftValidation(policyHeader, null);
        }

        boolean isNotifyConfigured = transactionManager.isNotifyConfigured(policyHeader);
        if (isNotifyConfigured){
            String returnValue = "VALID";
            Record policyRecord = policyHeader.toRecord();
            policyRecord.setFieldValue("notifyLevel", notifyLevel);
            RecordSet rs = transactionManager.loadAllProductNotifications(policyRecord);

            List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
            if (rs.getSize() > 0){
                Iterator notifyRecIte = rs.getRecords();

                while (notifyRecIte.hasNext()){
                    Record notifyRec = (Record) notifyRecIte.next();
                    if (NotifyFields.getStatus(notifyRec).equals("VALID")) {
                        // Get the notification ID
                        String productNotifyId = NotifyFields.getProductNotifyId(notifyRec);

                        // Get the default value
                        String fieldValue = NotifyFields.getDefaultValue(notifyRec);

                        // Get the next step indicator from the system
                        Record record = new Record();
                        record.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
                        record.setFieldValue(PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE, policyHeader.getTermEffectiveFromDate());
                        record.setFieldValue(PolicyHeaderFields.TERM_EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());
                        record.setFieldValue(NotifyFields.PRODUCT_NOTIFY_ID, productNotifyId);
                        record.setFieldValue("userResponse", fieldValue);

                        if (NotifyFields.getMessageCategory(notifyRec).equals(MessageCategory.CONFIRMATION_PROMPT_STRING)) {
                            NotifyFields.setResponse(notifyRec, fieldValue);
                            NotifyFields.setResponseType(notifyRec, ResponseTypeEnum.SYSYTEM_DEFAULT.getResponseTypeValue());
                        }

                        returnValue = transactionManager.productNotificationResponse(record) == 0 ? "VALID" : "INVALID";

                    }
                    if(returnValue.equals("INVALID")){
                        ExtendedStatusType extendedStatusType = new ExtendedStatusType();
                        extendedStatusType.setExtendedStatusCode(NotifyFields.getMsgCode(notifyRec));
                        extendedStatusType.setExtendedStatusType("Information");
                        extendedStatusType.setExtendedStatusDescription(NotifyFields.getMessage(notifyRec));
                        extendedStatusTypes.add(extendedStatusType);
                    }
                }
                if(extendedStatusTypes.size()>0 && !MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())){
                    messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_SAVED_WITH_INFO);
                }
                messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
                // Save validation
                rs.setFieldValueOnAll("notifyLevel", notifyLevel);
                getSoftValidationManager().processSoftValidation(policyHeader, rs);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performValidationsForNotify");
        }
    }
    public void setBillingManager(BillingManager billingManager) {
        m_billingManager = billingManager;
    }

    public BillingManager getBillingManager() {
        return m_billingManager;
    }

    public BillingAccountChangeWSClientManager getBillingAccountChangeWSClientManager() {
        return m_billingAccountChangeWSClientManager;
    }

    public void setBillingAccountChangeWSClientManager(BillingAccountChangeWSClientManager billingAccountChangeWSClientManager) {
        m_billingAccountChangeWSClientManager = billingAccountChangeWSClientManager;
    }

    public SoftValidationManager getSoftValidationManager() {
        return m_softValidationManager;
    }

    public void setSoftValidationManager(SoftValidationManager softValidationManager) {
        m_softValidationManager = softValidationManager;
    }

    private BillingManager m_billingManager;
    private BillingAccountChangeWSClientManager m_billingAccountChangeWSClientManager;
    private SoftValidationManager m_softValidationManager;
}
