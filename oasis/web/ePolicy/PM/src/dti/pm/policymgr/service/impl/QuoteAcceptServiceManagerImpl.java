package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policy.MedicalMalpracticePolicyType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryType;
import com.delphi_tech.ows.quoteacceptservice.MedicalMalpracticeQuoteAcceptRequestType;
import com.delphi_tech.ows.quoteacceptservice.MedicalMalpracticeQuoteAcceptResultType;
import dti.cs.policynotificationmgr.NotificationTransactionTimeEnum;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.policymgr.service.PolicyChangeServiceHelper;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryServiceManager;
import dti.pm.policymgr.service.PolicyWebServiceHelper;
import dti.pm.policymgr.service.QuoteAcceptServiceManager;
import dti.pm.transactionmgr.TransactionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   01/15/2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 01/12/2016       ssheng      Issue 168559. Roll back issue 141924's change
 *                              to rate, billingsetup in quote accept service
 *                              and add save official logic in quote accept
 *                              service. Call the public method from
 *                              PolicyChangeServiceHelper.
 * 05/16/2016      lzhang       Issue 170647. Modified quoteAccept:
 *                              use messageStatusType result from
 *                              performRateAction(), performIssuePolicy
 *                              to indicate whether validation
 *                              failed instead of validationErrorSet
 * 01/19/2017      wrong        Issue 166929. Added field ignoreSoftValidationActionB,
 *                              parameter isIgnoreSoftValidationActionB in performIssuePolicy
 *                              to return SuccessWithInformation when ignoreSoftValidations
 *                              provided.
 * 02/22/2017      tzeng        Issue 168385. Modified setActionCode and add setRequestedTransTime to set the
 *                              requested transaction time.
 * 04/14/2017      tzeng        Issue 166929.
 *                              1) Modified quoteAccept() to call validateQuoteBeforeProcess in helper before accept.
 *                              2) Added logic to return the status of SuccessWithInformation when issue policy and
 *                              ignoreSoftValidations action code provided but the soft validation existed
  *                             in preRate/postRate.
 *                              3) Moved setValidationErrorToOutput() to helper.
 * ---------------------------------------------------
 */
public class QuoteAcceptServiceManagerImpl implements QuoteAcceptServiceManager {

    private final Logger l = LogUtils.getLogger(getClass());

    private final static QName _MedicalMalpracticeQuoteAcceptRequest_QNAME = new QName("http://www.delphi-tech.com/ows/QuoteAcceptService", "MedicalMalpracticeQuoteAcceptRequest");
    private final static QName _MedicalMalpracticeQuoteAcceptResult_QNAME = new QName("http://www.delphi-tech.com/ows/QuoteAcceptService", "MedicalMalpracticeQuoteAcceptResult");

    public MedicalMalpracticeQuoteAcceptResultType quoteAccept(MedicalMalpracticeQuoteAcceptRequestType quoteAcceptRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "quoteAccept", new Object[]{quoteAcceptRequest});
        }

        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(quoteAcceptRequest, _MedicalMalpracticeQuoteAcceptRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                quoteAcceptRequest.getMessageId(), quoteAcceptRequest.getCorrelationId(), quoteAcceptRequest.getUserId(),_MedicalMalpracticeQuoteAcceptRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "quoteAccept", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(quoteAcceptRequest, _MedicalMalpracticeQuoteAcceptRequest_QNAME,
                quoteAcceptRequest.getMessageId(), quoteAcceptRequest.getCorrelationId(), quoteAcceptRequest.getUserId());
        }
        MedicalMalpracticeQuoteAcceptResultType quoteAcceptResult = new MedicalMalpracticeQuoteAcceptResultType();
        setActionCode(quoteAcceptRequest);
        MessageManager mm = MessageManager.getInstance();
        String view = getViewName(quoteAcceptRequest);

        List<String> policyNumberList = new ArrayList<String>();
        String policyNumber = null;
        PolicyHeader policyHeader = null;
        MessageStatusType messageStatusType = new MessageStatusType();
        try {
            List<MedicalMalpracticePolicyType> inputQuotes = quoteAcceptRequest.getMedicalMalpracticePolicy();
            if (inputQuotes.size()>1) {
                mm.addErrorMessage("ws.quote.accept.multiple.policies.input");
                throw new AppException("Error: cannot process multiple input Quotes.");
            }

            // For each Quote in the input XML
            for (MedicalMalpracticePolicyType quote: quoteAcceptRequest.getMedicalMalpracticePolicy()) {
                if (quote != null && !StringUtils.isBlank(quote.getPolicyId())) {
                    // Get the quote policy header.
                    if(RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)){
                        RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
                    }

                    PolicyHeader quotePolicyHeader = getPolicyManager().loadPolicyHeader(quote.getPolicyId(), requestId, "QuoteAcceptService: quoteAccept");

                    PolicyWebServiceHelper.getInstance().validateQuoteBeforeProcess(quotePolicyHeader);

                    Record outputRecord = new Record();
                    Record inputRecord = new Record();
                    inputRecord.setFieldValue(getMessageKey() + ".confirmed", "Y");
                    // Accept the input Quote as Policy
                    outputRecord = getPolicyManager().acceptQuote(quotePolicyHeader, inputRecord);

                    policyNumber = outputRecord.getStringValue("parallelPolNo");

                    // Add the policy to a list to be used later to load and populate the policy information XML
                    // in one step.
                    policyNumberList.add(policyNumber);

                    // Get the policy header of the newly created policy number.
                    if(RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)){
                        RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
                    }
                    policyHeader = getPolicyManager().loadPolicyHeader(policyNumber, requestId, "QuoteAcceptService: quoteAccept");

                    // Perform Validate and Rate for the newly created policy.
                    if (isRateActionB()) {
                        boolean ignoreSoftValidationToRateB = false;
                        if (isIssueActionB() && isIgnoreSoftValidationActionB()) {
                            ignoreSoftValidationToRateB = true;
                        }
                        messageStatusType = getPolicyChangeServiceHelper().performRateAction(policyHeader, getTransactionManager(), ignoreSoftValidationToRateB);
                        if (messageStatusType.getMessageStatusCode() != null) {
                            if(MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                                || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())){
                                throw new AppException("Error: transaction validation failed for rate policy " + policyNumber);
                            }else {
                                quoteAcceptResult.setMessageStatus(messageStatusType);
                            }
                        }
                    }

                    // Add a default billing setup to the newly created policy
                    if (!YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("PM_COPY_QT_PAYPLAN", "N")).booleanValue()) {
                        getPolicyChangeServiceHelper().performBillingSetupTransaction(policyHeader, null, isIssueActionB());
                    }

                    if ((messageStatusType.getMessageStatusCode() == null ||
                         MessageStatusHelper.STATUS_CODE_SUCCESS_WITH_INFO.equals(messageStatusType.getMessageStatusCode()))
                        && isIssueActionB()) {
                        //system will unlock policy when issue policy
                        MessageStatusType messageStatusTypeTemp = getPolicyChangeServiceHelper().performIssuePolicy(policyHeader, getTransactionManager(), isIgnoreSoftValidationActionB());
                        if (messageStatusTypeTemp.getMessageStatusCode() != null) {
                            messageStatusType.setMessageStatusCode(messageStatusTypeTemp.getMessageStatusCode());
                            messageStatusType.getExtendedStatus().addAll(messageStatusTypeTemp.getExtendedStatus());
                        }
                        if (messageStatusType.getMessageStatusCode() != null) {
                            if(MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                                || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())){
                                throw new AppException("Error: transaction validation failed for issue policy " + policyNumber);
                            }else {
                                quoteAcceptResult.setMessageStatus(messageStatusType);
                            }
                        }
                    }
                    else {
                        // Unlock the newly created policy
                        owsUnlockPolicy(policyHeader);
                    }
                    // Load the policy information for the pre-prepared policy list to be published outside (XML)
                    loadPolicyInfo(policyNumber, view, quoteAcceptResult);
                }
            }
            if (quoteAcceptResult.getMedicalMalpracticePolicy().size() > 0) {
                owsLogRequest.setSourceTableName("POLICY_TERM_HISTORY");
                owsLogRequest.setSourceRecordFk(quoteAcceptResult.getMedicalMalpracticePolicy().get(0).getPolicyTermNumberId());
                owsLogRequest.setSourceRecordNo(quoteAcceptResult.getMedicalMalpracticePolicy().get(0).getPolicyId());
            }
        } catch (MessageStatusAppException wsae) {
            quoteAcceptResult.setMessageStatus(wsae.getMessageStatus());
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR,
                "Failure invoking the MedicalMalpracticeQuoteAcceptResultType", e);
            l.logp(Level.SEVERE, getClass().getName(), "quoteAccept", ae.getMessage(), ae);

            MessageStatusType newMessageStatusType;
            if (MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())){
                newMessageStatusType = new MessageStatusType();
                // If there were any validation messages, we want to publish them
                PolicyWebServiceHelper.getInstance().setValidationErrorToOutput(messageStatusType, policyNumber, newMessageStatusType);
            } else {
                newMessageStatusType = MessageStatusHelper.getInstance().getRejectedMessageStatus(ae);
            }

            quoteAcceptResult.setMessageStatus(newMessageStatusType);
            // Unlock the newly created policy that raised the error anyway.
            if ( policyHeader!=null) {
                owsUnlockPolicy(policyHeader);
            }
        }

        owsLogRequest.setMessageStatusCode(quoteAcceptResult.getMessageStatus().getMessageStatusCode());
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(quoteAcceptResult, _MedicalMalpracticeQuoteAcceptResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "quoteAccept", xmlResult);
        } else {
            owsLogRequest.setServiceResult(quoteAcceptResult);
            owsLogRequest.setServiceResultQName(_MedicalMalpracticeQuoteAcceptResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "quoteAccept", quoteAcceptResult);
        }
        return quoteAcceptResult;
    }
    /*
    * Call Policy Inquiry Service to load policy info.
    */
    private void loadPolicyInfo(String policyNumber, String viewName, MedicalMalpracticeQuoteAcceptResultType quoteAcceptResult) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyInfo", new Object[]{policyNumber});
        }

        PolicyInquiryRequestType policyInquiryRequest = new PolicyInquiryRequestType();
        PolicyInquiryRequestParametersType inquiryRequest = new PolicyInquiryRequestParametersType();

        PolicyInquiryResultParametersType policyInquiryResult = new PolicyInquiryResultParametersType();
        List<String> viewList = new ArrayList<String>();
        viewList.add(viewName);
        policyInquiryResult.getViewName().addAll(viewList);
        policyInquiryRequest.setPolicyInquiryResultParameters(policyInquiryResult);

        PolicyInquiryType inquiry = new PolicyInquiryType();
        inquiry.setPolicyId(policyNumber);
        inquiryRequest.setPolicyInquiry(inquiry);
        policyInquiryRequest.getPolicyInquiryRequestParameters().add(inquiryRequest);
        PolicyInquiryResultType resultType = getPolicyInquiryServiceManager().loadPolicy(policyInquiryRequest);

        if (MessageStatusHelper.getInstance().isRejected(resultType.getMessageStatus())) {
            MessageStatusAppException wsae = MessageStatusHelper.getInstance().handleRejectedServiceCall("Failure invoking the PolicyInquiryServiceManagerImpl",
                resultType.getMessageStatus());
            l.logp(Level.SEVERE, getClass().getName(), "loadPolicyInfo", wsae.getMessage(), wsae);
            throw wsae;
        }
        quoteAcceptResult.getAddress().addAll(resultType.getAddress());
        quoteAcceptResult.setCorrelationId(resultType.getCorrelationId());
        quoteAcceptResult.setMessageId(resultType.getMessageId());

        if (quoteAcceptResult.getMessageStatus() == null ) {
            quoteAcceptResult.setMessageStatus(resultType.getMessageStatus());
        }

        quoteAcceptResult.getMedicalMalpracticePolicy().addAll(resultType.getMedicalMalpracticePolicy());
        quoteAcceptResult.getOrganization().addAll(resultType.getOrganization());
        quoteAcceptResult.getPerson().addAll(resultType.getPerson());
        quoteAcceptResult.getProperty().addAll(resultType.getProperty());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyInfo", quoteAcceptResult);
        }
    }

   /*
    * Get Action Code from input
    */
   private void setActionCode(MedicalMalpracticeQuoteAcceptRequestType quoteAcceptRequest) {
       Logger l = LogUtils.getLogger(getClass());
       if (l.isLoggable(Level.FINER)) {
           l.entering(getClass().getName(), "getActionCode", new Object[]{quoteAcceptRequest.getDataModificationInformation()});
       }

       setRateActionB(false);
       setWipActionB(false);
       setIssueActionB(false);
       setIgnoreSoftValidationActionB(false);

       List<String> actionCodeList = new ArrayList<String>();
       if (quoteAcceptRequest.getDataModificationInformation() != null) {
           actionCodeList = quoteAcceptRequest.getDataModificationInformation().getActionCode();
       }
       for(String actionCode: actionCodeList){
           if (PolicyInquiryFields.WIP.equalsIgnoreCase(actionCode)) {
               this.setWipActionB(true);
           }
           else if (PolicyInquiryFields.RATE_ACTION_CODE.equalsIgnoreCase(actionCode)) {
               this.setRateActionB(true);
           }
           else if (PolicyInquiryFields.ISSUE_ACTION_CODE.equalsIgnoreCase(actionCode)) {
               this.setRateActionB(true);
               this.setIssueActionB(true);
           }
           else if (PolicyInquiryFields.IGNORE_SOFT_VALIDATION_ACTION_CODE.equalsIgnoreCase(actionCode)) {
               this.setIgnoreSoftValidationActionB(true);
           }
       }

       setRequestedTransTime();

       if (l.isLoggable(Level.FINER)) {
           l.exiting(getClass().getName(), "getActionCode");
       }
    }

    /*
     * Get View name.
     */
    private String getViewName(MedicalMalpracticeQuoteAcceptRequestType quoteAcceptRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getViewName", new Object[]{quoteAcceptRequest.getMedicalMalpracticeQuoteAcceptResultParameters()});
        }
        String viewName = "";
        if (quoteAcceptRequest.getMedicalMalpracticeQuoteAcceptResultParameters()!= null) {
            viewName = quoteAcceptRequest.getMedicalMalpracticeQuoteAcceptResultParameters().getViewName();
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getViewName", viewName);
        }
        return viewName;
    }

    /**
     * This method unlocks the policy using an autonomous transaction. This is necessary in case of failure in order
     * to not commit partial changes, and to be able to rollback all the policy changes done by the Web Service call.
     * To accomplish this, the method is configured in applicationConfig.xml for bean Pm.TxAttributes with
     * PROPAGATION_REQUIRES_NEW.
     * @param policyHeader
     */
    public void owsUnlockPolicy(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "owsUnlockPolicy", new Object[]{policyHeader});
        }

        getLockManager().unLockPolicy(policyHeader, YesNoFlag.Y, "Unlock from Quote Accept Web Service");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "owsUnlockPolicy");
        }
    }

    /**
     * Set requested transaction time based on ActionCode element.
     * If user input both RATE and ISSUE in ActionCode, then the requested transaction time will be OFFICIAL.
     */
    public void setRequestedTransTime() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setRequestedTransTime");
        }

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) rsm.get(dti.oasis.request.RequestStorageIds.HTTP_SEVLET_REQUEST);
        HttpSession session = request.getSession();
        if (session.getAttribute(IOasisAction.KEY_OASISUSER) != null) {
            OasisUser userBean = (OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER);
            if (isIssueActionB()) {
                userBean.setRequestedTransactionTime(NotificationTransactionTimeEnum.OFFICIAL.getCode());
            }
            else if (isRateActionB()) {
                userBean.setRequestedTransactionTime(NotificationTransactionTimeEnum.RATE.getCode());
            }
            else {
                userBean.setRequestedTransactionTime(null);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setRequestedTransTime");
        }
    }

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getPolicyInquiryServiceManager() == null)
            throw new ConfigurationException("The required property 'policyInquiryServiceManager' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
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

    public PolicyInquiryServiceManager getPolicyInquiryServiceManager() {
        return m_policyInquiryServiceManager;
    }

    public void setPolicyInquiryServiceManager(PolicyInquiryServiceManager policyInquiryServiceManager) {
        m_policyInquiryServiceManager = policyInquiryServiceManager;
    }

    public String getMessageKey() {
        if (m_messageKey == null) {
            m_messageKey = "pm.similarPolicyRecordValidator.confirm";
        }
        return m_messageKey;
    }

    private boolean isWipActionB() {
        return wipActionB;
    }

    private void setWipActionB(boolean wipActionB) {
        this.wipActionB = wipActionB;
    }

    private boolean isRateActionB() {
        return rateActionB;
    }

    private void setRateActionB(boolean rateActionB) {
        this.rateActionB = rateActionB;
    }

    public boolean isIssueActionB() {
        return issueActionB;
    }

    public void setIssueActionB(boolean issueActionB) {
        this.issueActionB = issueActionB;
    }

    public boolean isIgnoreSoftValidationActionB() { return ignoreSoftValidationActionB; }

    public void setIgnoreSoftValidationActionB(Boolean ignoreSoftValidationActionB) {
        this.ignoreSoftValidationActionB = ignoreSoftValidationActionB;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public PolicyChangeServiceHelper getPolicyChangeServiceHelper() {
        return m_policyChangeServiceHelper;
    }

    public void setPolicyChangeServiceHelper(PolicyChangeServiceHelper policyChangeServiceHelper) {
        m_policyChangeServiceHelper = policyChangeServiceHelper;
    }

    private boolean wipActionB;
    private boolean rateActionB;
    private boolean issueActionB;
    private boolean ignoreSoftValidationActionB;
    private String m_messageKey;

    private PolicyManager m_policyManager;
    private PolicyInquiryServiceManager m_policyInquiryServiceManager;
    private TransactionManager m_transactionManager;
    private LockManager m_lockManager;
    private PolicyChangeServiceHelper m_policyChangeServiceHelper;

    private final String requestId = "dti.pm.policymgr.struts.MaintainPolicyAction&process=loadPolicyDetail";
}
