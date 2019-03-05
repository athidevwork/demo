package dti.pm.policymgr.service.impl;


import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.party.OrganizationType;
import com.delphi_tech.ows.party.PersonType;
import com.delphi_tech.ows.party.PropertyType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import com.delphi_tech.ows.policy.AdditionalDataType;
import com.delphi_tech.ows.policy.AdditionalDateTimeType;
import com.delphi_tech.ows.policy.AdditionalNumberType;
import com.delphi_tech.ows.policy.CommissionType;
import com.delphi_tech.ows.policy.CreditSurchargeDeductibleType;
import com.delphi_tech.ows.policy.CreditSurchargeDeductibleVersionType;
import com.delphi_tech.ows.policy.InsuredAdditionalExposureType;
import com.delphi_tech.ows.policy.InsuredAdditionalExposureVersionType;
import com.delphi_tech.ows.policy.InsuredType;
import com.delphi_tech.ows.policy.InsuredVersionType;
import com.delphi_tech.ows.policy.InsurerType;
import com.delphi_tech.ows.policy.LicenseType;
import com.delphi_tech.ows.policy.MedicalMalpracticeCoverageType;
import com.delphi_tech.ows.policy.MedicalMalpracticeCoverageVersionType;
import com.delphi_tech.ows.policy.MedicalMalpracticePolicyType;
import com.delphi_tech.ows.policy.PolicyStatusCodeType;
import com.delphi_tech.ows.policy.ProducerType;
import com.delphi_tech.ows.policy.UnderwriterType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeRequestType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeResultType;
import dti.ci.entitymgr.service.PartyChangeServiceManager;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.cs.policynotificationmgr.NotificationTransactionTimeEnum;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoEmptyFlag;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusHelper;
import dti.pm.agentmgr.AgentManager;
import dti.pm.billingmgr.BillingAccountChangeWSClientManager;
import dti.pm.billingmgr.BillingManager;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coverageclassmgr.impl.CoverageClassSaveProcessor;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.coveragemgr.impl.CoverageSaveProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.impl.PolicySaveProcessor;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.policymgr.service.ComponentInquiryFields;
import dti.pm.policymgr.service.CoverageInquiryFields;
import dti.pm.policymgr.service.PolicyChangeServiceHelper;
import dti.pm.policymgr.service.PolicyChangeServiceManager;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryServiceHelper;
import dti.pm.policymgr.service.PolicyWebServiceHelper;
import dti.pm.policymgr.service.RiskInquiryFields;
import dti.pm.policymgr.underwritermgr.UnderwriterManager;
import dti.pm.policymgr.underwritermgr.UnderwritingFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.riskmgr.addtlexposuremgr.RiskAddtlExposureManager;
import dti.pm.riskmgr.impl.RiskRowStyleRecordLoadprocessor;
import dti.pm.riskmgr.impl.RiskSaveProcessor;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.transactionmgr.TransactionManager;
import dti.pm.transactionmgr.transaction.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/24/12
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/15/2012       fcb         Added logic for adding new policy/risk/coverage/component.
 * 09/06/2012       fcb         137198 - Changes related to creating transactions and handling error.
 * 09/18/2012       fcb         137402 - Added logic to set the primary risk indicator correctly.
 * 02/13/2013       fcb         141942 - Fixed the logging of the XML data as part of this issue.
 *                                       Added logic to rate the policy.
 * 05/21/2013       fcb         144759 - Fixed some bugs.
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 10/30/2014       awu         145137 - Modified performBillingSetupTransaction to call FM services to setup billing.
 * 11/20/2014       awu         154316 - Add changes to support policy endorsement/OOS endorsement.
 * 12/29/2014       awu         159970 - Add changes to support deleting TEMP records.
 * 01/14/2015       awu         160410 - Add changes in changeExistingPolicy to correct the record mode code.
 * 03/06/2015       awu         160916 - Add changes to avoid Null pointer and Index out of bound exceptions.
 * 06/03/2015       cv          163222 - Modified performBillingSetupTransaction to call WebServiceClientHelper to get FM Webservice url.
 * 07/18/2015       fcb         165221 - Logic for Insured (Underwriter) added.
 * 11/19/2015       eyin        167171 - 1) Modified validateForInvokeChangeService(), Add logic to make sure that changedPolicies is not null.
 *                                       2) Modified changeExistingPolicy(), Add logic to make sure that originalPolicy is not null.
 * 01/21/2016       eyin        166395 - Modified createNewPolicy(),we get the value of original policy No && original
 *                                       policy Cycle Code from optional fields 'MedicalMalpracticePolicy-originalPolicyId'
 *                                       && 'MedicalMalpracticePolicy-originalPolicyCycleCode' newly added in policy
 *                                       change service request, and pass them to procedure 'Pm_Web_Policy.Create_Policy'.
 * 1/12/2016       ssheng       Issue 168559. Roll back issue 141924's change to rate, billingsetup in quote accept service
 *                              and add save official logic in quote accept service. Call the public method from
 *                              PolicyChangeServiceHelper.
 * 01/30/2016       eyin        168882 - Modified validateForChangePolicy() and changeExistingPolicy() to change
 *                                       PrincipalBillingAccountInformationType element from a single node to a list.
 * 02/03/2016       fcb         169243 - Bug fixed in performRiskChanges related to the primary risk indicator.
 * 02/03/2016       wdang       169198 - Modified changeExistingPolicy() to correct the name of
 *                                       MedicalMalpracticePolicyType.getPrincipalBillingAccountInformation().
 * 03/04/2016       wdang       169005 - 1) When creating new policy, call changeExistingPolicy after policy is created.
 *                                          So that fields that are not supported in createPolicy can be also saved.
 *                                       2) Print full error statck rather than a simple error message.
 *                                       3) Create a new method setInputPolicyToRecord to encapsulate setting input record.
 *                                       4) Support new created fields of issue 165790,166924,166922.
 * 03/16/2016      lzhang       170141   1) PolicyServiceCallStatus: Save agent and underwriter information
 *                                          only when NB transaction
 *                                       2) validateForChangePolicy: avoid index error
 *                                          when no insurer/underwriter information
 * 05/16/2016      lzhang       170647   Modified changePolicy, doAllPolicyChanges,
 *                                       performPolicyChanges: use messageStatusType result
 *                                       from performRateAction(), performIssuePolicy
 *                                       to indicate whether validation failed
 *                                       instead of validationErrorSet
 * 06/24/2016      lzhang       177716   Modified setInputPolicyToRecord: add originalPolicy != null
 *                                       to avoid null point exception
 * 07/22/2016       bwang       178033 - Modified postProcessInsuredRecordSet(),changed integer type
 *                                       variables to long type which are from PK/FK fields in DB.
 * 08/05/2016      fcb          177135   new field added.
 * 08/23/2016      lzhang       178818 - Modified performUnderwriterSetup: add <TypeCode> element for underwriter
 * 09/16/2016      lzhang       179151 - Modified performUnderwriterSetup: support to add multiple <Underwriter> records
 * 10/07/2016      fcb          179813 - new fields added.
 * 10/10/2016      wdang        179813 - new field QUOTE_CYCLE_CODE in setInputPolicyToRecord().
 * 10/28/2016      ssheng       178920 - Equal dailyLimitAmount to origDailyLimitAmount.
 * 11/04/2016      ssheng       178855 - Modified handleDefaultDbRisk: Add check condition that
 *                                       if the desired risk is already in the DB.
 * 11/25/2016      lzhang       181653 - Modified validateUnderwriterEntity: pass underwriter type code to backend
 * 01/19/2017      wrong        166929 - 1) Added field ignoreSoftValidationActionB.
 *                                       2) Added parameter isIgnoreSoftValidationActionB in performIssuePolicy().
 * 01/24/2017      wrong        182689 - Add logic to analyze whether propertyReference is set the value.
 * 02/08/2017      wrong        182593 - Add logic to accept transaction code in reqeust instead of hardcoded NEW.
 * 02/22/2017      tzeng        168385 - Modified setActionCodeFlags and add setRequestedTransTime to set the requested
 *                                       transaction time.
 * 04/14/2017       tzeng       166929 - 1) Modified changePolicy to add preValidateRequestParameters().
 *                                       2) Modified doAllPolicyChanges to remove validateForInvokeChangeService() to
 *                                          preValidateRequestParameters().
 *                                       3) Modified isPolicyDataChanged() to add non-null condition on changed renewal
 *                                          detail.
 *                                       4) Modified performPolicyChanges() to add logic to return the status of
 *                                          SuccessWithInformation when issue policy and ignoreSoftValidations action
 *                                          code provided and the soft validation existed in preRate/postRate.
 * 06/19/2017      wrong        186163 - Modified setInputCoverageToRecord and mergeCoverageRecords
 *                                       to add claim process code into input record.
 * 07/17/2017      wrong        168374 - 1) Modify setInputRiskToRecord and mergeRiskRecords to add PCF practice county
 *                                          code and PCF insured class code into input record.
 *                                       2) Modify isRiskVersionChanged() to add logic for new added fields
 *                                          PCFInsuredClassCode and PCFPracticeCountyCode.
 * 09/21/2017      eyin         169483 - Modified to support change risk additional exposure info.
 * 12/27/2017      wrong        190394 - Modified setInputComponentToRecord to add coverageId in record to support
 *                                       adding suspension component by policy change service.
 * 03/30/2018      wrong        192050 - Modified performUnderwriterSetup() and validateUnderwriterEntity() to validate
 *                                       the partyNumberId in underwriter element based on system parameter
 *                                       QUNDERWRITER/UNDERWRITER.
 * 06/25/2018      wrong        192846 - 1) Modified performUnderwriterSetup to support underwriter change function
 *                                          in policy change web service.
 *                                       2) Modified validateForChangePolicy to delete insurer validation logic.
 *                                       3) New function getLatestUnderwriters(), getChangedUnderwriters() and
 *                                          getUnderwriterList() added.
 * 10/30/2018      wrong        196732 - Added setReturnPolicyInfo and modified performPolicyChanges to invoke it.
 * 11/02/2018      wrong        196790 - 1) Added validation for clinet Id in issue company for policy change service.
 *                                       2) Modified setInputPolicyToRecord to get/set entity id by client id.
 * 12/04/2018       eyin        197179: Replaced function getPolicyDetail with getFirstPolicyDetail.
 * ---------------------------------------------------
 */
public class PolicyChangeServiceManagerImpl implements PolicyChangeServiceManager, PolicyChangeServiceSaveProcessor {
    private final Logger l = LogUtils.getLogger(getClass());

    public static final String SERVICE_MANAGER_BEAN_NAME = "PolicyChangeServiceManager";
    private final static QName _PolicyChangeRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyChangeService", "MedicalMalpracticePolicyChangeRequest");
    private final static QName _PolicyChangeResult_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyChangeService", "MedicalMalpracticePolicyChangeResult");

    public MedicalMalpracticePolicyChangeResultType changePolicy(MedicalMalpracticePolicyChangeRequestType policyChangeRequest, MedicalMalpracticePolicyChangeResultType policyChangeResult) {
        Logger l = LogUtils.enterLog(getClass(), "savePolicy");

        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(policyChangeRequest, _PolicyChangeRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                policyChangeRequest.getMessageId(), policyChangeRequest.getCorrelationId(), policyChangeRequest.getUserId(),_PolicyChangeRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "savePolicy", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(policyChangeRequest, _PolicyChangeRequest_QNAME,
                policyChangeRequest.getMessageId(), policyChangeRequest.getCorrelationId(), policyChangeRequest.getUserId());
        }
        boolean partySavedSuccessfully = false;
        boolean preValidateSuccessfully = false;

        try {
            preValidateSuccessfully = preValidateRequestParameters(policyChangeRequest);

            try {
                invokePartyChangeService(policyChangeRequest, policyChangeResult);
                partySavedSuccessfully = true;
            }
            catch (Exception e) {
                partySavedSuccessfully = false;
                throw e;
            }

            if (partySavedSuccessfully) {
                doAllPolicyChanges(policyChangeRequest, policyChangeResult);

                copyRequestAndUpdateNumberIds(policyChangeRequest, policyChangeResult);

                if (policyChangeResult.getMessageStatus() == null) {
                    policyChangeResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
                }
            }

        } catch (Exception e) {
            cleanPartyResult(policyChangeResult);
            Boolean validationFailureB = PolicyWebServiceHelper.getInstance().getValidationFailureB(e);
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR,
                "Failure invoking the PolicyChangeServiceManagerImpl", e , false);
            MessageStatusType messageStatusType = new MessageStatusType();
            if (validationFailureB) {
                messageStatusType = policyChangeResult.getMessageStatus();
                if (!partySavedSuccessfully) {
                    messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
                }
            }else {
                messageStatusType = MessageStatusHelper.getInstance().getRejectedMessageStatus(ae);
                if (!preValidateSuccessfully || !partySavedSuccessfully) {
                    messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
                }
                else if (!isDeleteWipOnFailureB()) {
                    messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_SAVED_WITH_ERRORS);
                }
            }
            policyChangeResult.setMessageStatus(messageStatusType);
            l.logp(Level.SEVERE, getClass().getName(), "PolicyChangeServiceManagerImpl", ae.getMessage(), ae);
        }

        owsLogRequest.setMessageStatusCode(policyChangeResult.getMessageStatus().getMessageStatusCode());
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(policyChangeResult, _PolicyChangeResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "savePolicy", xmlResult);
        } else {
            owsLogRequest.setServiceResult(policyChangeResult);
            owsLogRequest.setServiceResultQName(_PolicyChangeResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePolicy", policyChangeResult);
        }

        return policyChangeResult;
    }

    /**
     * Pre-validate request parameters, if validate successfully, then return true.
     * @param policyChangeRequest
     * @return
     */
    private boolean preValidateRequestParameters(MedicalMalpracticePolicyChangeRequestType policyChangeRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "preValidateRequestParameters", policyChangeRequest);
        }

        List<MedicalMalpracticePolicyType> changedPolicies = policyChangeRequest.getMedicalMalpracticePolicy();
        List<MedicalMalpracticePolicyType> originalPolicies = policyChangeRequest.getDataModificationInformation() == null ?
                                                              null : policyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription() == null ?
                                                              null : policyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getMedicalMalpracticePolicy();

        validateForInvokeChangeService(originalPolicies, changedPolicies);

        String errorMsgKey = null;
        if (null != originalPolicies && originalPolicies.size() == 1 &&
            !StringUtils.isSame(changedPolicies.get(0).getPolicyId(), originalPolicies.get(0).getPolicyId(), true)) {
            errorMsgKey = "ws.policy.change.originalPolicy.policyId.noMatch.input";
        }

        if (!StringUtils.isBlank(errorMsgKey)) {
            AppException ae = new AppException(errorMsgKey, "Validate the request parameters for policy change in failure");
            l.throwing(getClass().getName(), "preValidateRequestParameters", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "preValidateRequestParameters", policyChangeRequest);
        }
        return true;
    }

    private void doAllPolicyChanges(MedicalMalpracticePolicyChangeRequestType policyChangeRequest,
                                         MedicalMalpracticePolicyChangeResultType policyChangeResult) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doAllPolicyChanges", new Object[]{policyChangeRequest, policyChangeResult});
        }

        PolicyChangeServiceSaveProcessor policyChangeServiceSaveProcessor =
            (PolicyChangeServiceSaveProcessor) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
        List<MedicalMalpracticePolicyType> changedPolicies = policyChangeRequest.getMedicalMalpracticePolicy();
        if (changedPolicies == null) {
            return;
        }

        List<MedicalMalpracticePolicyType> originalPolicies = null;
        if (policyChangeRequest.getDataModificationInformation()!=null &&
            policyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription()!=null) {
            originalPolicies = policyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription().getMedicalMalpracticePolicy();
        }
        else {
            // Create empty list, no original policies
            originalPolicies = new ArrayList<MedicalMalpracticePolicyType>();
        }

        Iterator chgIt = changedPolicies.iterator();
        while (chgIt.hasNext()) {
            MedicalMalpracticePolicyType changedPolicy = (MedicalMalpracticePolicyType)chgIt.next();
            String policyNo = changedPolicy.getPolicyId();
            Iterator origIt = originalPolicies.iterator();
            MedicalMalpracticePolicyType originalPolicy = null;
            while (origIt.hasNext()) {
                originalPolicy = (MedicalMalpracticePolicyType)origIt.next();
                if (policyNo.equalsIgnoreCase(originalPolicy.getPolicyId())) {
                    break;
                }
            }
            PolicyServiceCallStatus callStatus = performPolicyChanges(policyChangeRequest, policyChangeResult, originalPolicy, changedPolicy);
            if (callStatus.isPolicyLocked()) {
                policyChangeServiceSaveProcessor.owsUnlockPolicy(callStatus.getPolicyHeader());
            }
            if (callStatus.getAppException() != null) {
                AppException ae = ExceptionHelper.getInstance().handleException("Error when executing performPolicyChanges", callStatus.getAppException());
                if (callStatus.getValidationFailureB()) {
                    ae.setMessageParameters(new Object[]{callStatus.getValidationFailureB()});
                }
                if (isDeleteWipOnFailureB() && callStatus.getPolicyHeader() != null) {
                    if (isNewPolicyB() || isNewTransactionB()) {
                        getTransactionManager().delWipTransaction(changedPolicy.getPolicyId());
                    }
                }
                l.throwing(getClass().getName(), "doAllPolicyChanges", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "doAllPolicyChanges");
        }
    }

    private PolicyServiceCallStatus performPolicyChanges(MedicalMalpracticePolicyChangeRequestType policyChangeRequest,
                                      MedicalMalpracticePolicyChangeResultType policyChangeResult,
                                      MedicalMalpracticePolicyType originalPolicy, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPolicyChanges", new Object[]{policyChangeRequest, policyChangeResult, originalPolicy, changedPolicy});
        }

        PolicyServiceCallStatus callStatus = new PolicyServiceCallStatus();
        PolicyChangeServiceSaveProcessor policyChangeServiceSaveProcessor = (PolicyChangeServiceSaveProcessor) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
        setActionCodeFlags(policyChangeRequest);

        PolicyHeader policyHeader = null;
        MessageStatusType messageStatusType = new MessageStatusType();
        try {
            if (originalPolicy == null) {
                setNewPolicyB(true);
                setNewTransactionB(false);
                createNewPolicy(policyChangeRequest, changedPolicy);
                policyHeader = policyChangeServiceSaveProcessor.owsLoadPolicyHeader(
                    changedPolicy.getPolicyId(),
                    changedPolicy.getPolicyNumberId(),
                    null,
                    PolicyViewMode.WIP);
            }
            else {
                setNewPolicyB(false);
                setNewTransactionB(false);
                policyHeader = policyChangeServiceSaveProcessor.owsLoadPolicyHeader(
                    changedPolicy.getPolicyId(),
                    changedPolicy.getPolicyNumberId(),
                    changedPolicy.getPolicyTermNumberId(),
                    PolicyViewMode.WIP);
                if (!policyHeader.isWipB()) {
                    Record inputRecord = setInitialValuesForCreateTransaction();
                    validateForNewTransaction(changedPolicy, policyHeader);
                    String transEffDate = DateUtils.parseXMLDateToOasisDate(changedPolicy.getTransactionDetail().getTransactionEffectiveDate());
                    policyChangeServiceSaveProcessor.owsCreateTransaction(policyHeader, inputRecord, transEffDate, TransactionCode.ENDORSE);
                    setNewTransactionB(true);
                }

                policyHeader = policyChangeServiceSaveProcessor.owsLoadPolicyHeader(changedPolicy.getPolicyId(), changedPolicy.getPolicyNumberId(), changedPolicy.getPolicyTermNumberId(), PolicyViewMode.WIP);
            }

            callStatus.setPolicyHeader(policyHeader);
            setDefaultFlags(policyChangeRequest, policyHeader);

            if (!policyHeader.getPolicyIdentifier().ownLock()) {
                try {
                    policyChangeServiceSaveProcessor.owsLockPolicy(policyHeader);
                }
                catch (Exception e) {
                    MessageManager mm = MessageManager.getInstance();
                    mm.addErrorMessage("ws.policy.change.lock.policy", new String[]{changedPolicy.getPolicyId()});
                    throw new AppException("Error: cannot lock policy " + changedPolicy.getPolicyId());
                }
            }

            // save additional fileds for new created policy
            if (originalPolicy == null) {
                changeExistingPolicy(policyHeader, changedPolicy, changedPolicy);
            }
            // change existing policy
            else {
                validateForChangePolicy(originalPolicy, changedPolicy);
                if (isPolicyDataChanged(originalPolicy, changedPolicy)) {
                    changeExistingPolicy(policyHeader, originalPolicy, changedPolicy);
                }
            }


            setPolicyReturnData(policyHeader, changedPolicy);

            performRiskChanges(policyHeader, originalPolicy, changedPolicy, policyChangeRequest);

            performRiskAddtlExpChanges(policyHeader, originalPolicy, changedPolicy, policyChangeRequest);

            performCoverageChanges(policyHeader, originalPolicy, changedPolicy);

            performSaveTransaction(policyHeader);

            performUnderwriterSetup(policyHeader, changedPolicy, originalPolicy);

            if (isNewPolicyB()){
                performAgentSetup(policyHeader, changedPolicy);
            }

            if (isRateActionB()) {
                boolean ignoreSoftValidationToRateB = false;
                if (isIssueActionB() && isIgnoreSoftValidationActionB()) {
                    ignoreSoftValidationToRateB = true;
                }
                messageStatusType = getPolicyChangeServiceHelper().performRateAction(policyHeader, getTransactionManager(), ignoreSoftValidationToRateB);
                if(messageStatusType.getMessageStatusCode() != null){
                    if(MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                        || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())) {
                        throw new AppException("Error: transaction validation failed for rate policy " + changedPolicy.getPolicyId());
                    }else {
                        policyChangeResult.setMessageStatus(messageStatusType);
                    }
                }
            }

            if (isNewPolicyB() && isIssueActionB() && getTransactionManager().isBillingSetupAvailable(policyHeader)) {
                getPolicyChangeServiceHelper().performBillingSetupTransaction(policyHeader, policyChangeRequest, isIssueActionB());
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
                if(messageStatusType.getMessageStatusCode() != null){
                    if(MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                        || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())){
                        throw new AppException("Error: transaction validation failed for issue policy " + changedPolicy.getPolicyId());
                    }else {
                        policyChangeResult.setMessageStatus(messageStatusType);
                    }
                }
            }
            else {
                callStatus.setPolicyLockedB(true);
            }

            setReturnPolicyInfo(policyHeader, changedPolicy);
            callStatus.setSuccessStatusB(true);
        } catch (Exception e) {
            e.printStackTrace();
            setReturnPolicyInfo(policyHeader, changedPolicy);
            callStatus.setSuccessStatusB(true);
            callStatus.setAppException(new AppException(e.getMessage(), e));
            if (policyHeader != null && policyHeader.getPolicyIdentifier().ownLock()) {
                callStatus.setPolicyLockedB(true);
                if (!isDeleteWipOnFailureB()) {
                    copyRequestAndUpdateNumberIds(policyChangeRequest, policyChangeResult);
                }
            }

            if(MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())){
                callStatus.setValidationFailureB(true);
                policyChangeResult.setMessageStatus(messageStatusType);
            }

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performPolicyChanges");
        }

        return callStatus;
    }

    /**
     * This method loads the policy header using an autonomous transaction. This is necessary becasue while loading
     * the policy header the system attempts also to lock the policy, which in turn execute a commit.
     * To accomplish this, the method is configured in applicationConfig.xml for bean Pm.TxAttributes with
     * PROPAGATION_REQUIRES_NEW.
     * @param policyNo
     * @param termBaseRecordId
     * @param desiredViewMode
     */
    public PolicyHeader owsLoadPolicyHeader(String policyNo, String policyNumberId, String termBaseRecordId, PolicyViewMode desiredViewMode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "owsLoadPolicyHeader", new Object[]{policyNo, policyNumberId, termBaseRecordId, desiredViewMode});
        }

        if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)) {
            RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
        }

        String policyTermHistoryId = null;
        if (termBaseRecordId != null) {
            if (StringUtils.isBlank(policyNumberId)) {
                Record inputRecord = new Record();
                inputRecord.setFieldValue(PolicyInquiryFields.POLICY_NO, policyNo);
                policyNumberId = getPolicyManager().getPolicyId(inputRecord);
            }

            RecordSet rs = getPolicyManager().loadPolicyTermList(policyNumberId);
            Iterator it = rs.getRecords();
            while (it.hasNext()) {
                Record output = (Record) it.next();
                String termBaseId = output.getStringValue(PolicyInquiryFields.TERM_BASE_RECORD_ID);
                if (termBaseRecordId!=null && termBaseRecordId.equalsIgnoreCase(termBaseId)) {
                    policyTermHistoryId = output.getStringValue(PolicyInquiryFields.POLICY_TERM_HISTORY_ID);
                    break;
                }
            }
        }
        if (!isNewPolicyB() && StringUtils.isBlank(policyTermHistoryId)) {
            AppException ae = new AppException("ws.policy.change.term.invalid", "",
                new String[]{termBaseRecordId});
            l.throwing(getClass().getName(), "owsLoadPolicyHeader", ae);
            throw ae;
        }
        String endQuoteId = null;
        PolicyHeader policyHeader = getPolicyManager().loadPolicyHeader(policyNo, policyTermHistoryId, desiredViewMode, endQuoteId, policyRequestId, "owsLoadPolicyHeader", false);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "owsLoadPolicyHeader", policyHeader);
        }

        return policyHeader;
    }

    private void validateForInvokeChangeService(List<MedicalMalpracticePolicyType> originalPolicies, List<MedicalMalpracticePolicyType> changedPolicies) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForInvokeChangeService", new Object[]{originalPolicies, changedPolicies});
        }

        String errorMsg = "";

        if (originalPolicies!=null && originalPolicies.size() > 1 )
            errorMsg = "original data";

        if (changedPolicies ==null)
            errorMsg = errorMsg + " changed data";

        if (changedPolicies != null && changedPolicies.size() > 1 ) {
            if (errorMsg != "") {
                errorMsg = errorMsg + "and modified data";
            }
            else {
                errorMsg = "modified data";
            }
        }

        if (errorMsg != "") {
            AppException ae = new AppException("ws.policy.change.one.policy.term", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForInvokeChangeService", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForInvokeChangeService");
        }
    }

    /**
     * This method sets the ActionCode flags.
     * @param policyChangeRequest
     */
    public void setActionCodeFlags(MedicalMalpracticePolicyChangeRequestType policyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setActionCodeFlags", new Object[]{policyChangeRequest});
        }

        setRateActionB(false);
        setIssueActionB(false);
        setDeleteWipOnFailureB(false);
        setIgnoreSoftValidationActionB(false);

        if (policyChangeRequest.getDataModificationInformation()!=null) {
            for (String actionCode : policyChangeRequest.getDataModificationInformation().getActionCode()) {
                if (PolicyInquiryFields.RATE_ACTION_CODE.equalsIgnoreCase(actionCode)) {
                    setRateActionB(true);
                }
                if (PolicyInquiryFields.ISSUE_ACTION_CODE.equalsIgnoreCase(actionCode)) {
                    setRateActionB(true);
                    setIssueActionB(true);
                }
                if (PolicyInquiryFields.DELETE_WIP_ON_FAILURE.equalsIgnoreCase(actionCode)) {
                    setDeleteWipOnFailureB(true);
                }
                if (PolicyInquiryFields.IGNORE_SOFT_VALIDATION_ACTION_CODE.equalsIgnoreCase(actionCode)) {
                    setIgnoreSoftValidationActionB(true);
                }
            }
        }

        setRequestedTransTime();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setActionCodeFlags");
        }
    }

    /**
     * This method locks the policy using an autonomous transaction. This is necessary in case of failure in order
     * to not commit partial changes (Party Change Service for example).
     * To accomplish this, the method is configured in applicationConfig.xml for bean Pm.TxAttributes with
     * PROPAGATION_REQUIRES_NEW.
     * @param policyHeader
     */
    public void owsLockPolicy(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "owsLockPolicy", new Object[]{policyHeader});
        }

        getLockManager().lockPolicy(policyHeader, "Policy Change Web Service - owsLockPolicy");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "owsLockPolicy");
        }
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

        getLockManager().unLockPolicy(policyHeader, YesNoFlag.Y, "Unlock from Policy Change Web Service");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "owsUnlockPolicy");
        }
    }

    /**
     * This method creates a new policy transaction using an autonomous transaction.
     * To accomplish this, the method is configured in applicationConfig.xml for bean Pm.TxAttributes with
     * PROPAGATION_REQUIRES_NEW.
     * @param policyHeader
     */
    public Transaction owsCreateTransaction(PolicyHeader policyHeader, Record inputRecord, String transactionEffectiveFromDate, TransactionCode defaultTransactionCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "owsCreateTransaction", new Object[]{policyHeader, inputRecord, transactionEffectiveFromDate, defaultTransactionCode});
        }

        TransactionCode transactionCode = null;
        if (policyHeader.isLastTerm()) {
            transactionCode = TransactionCode.ENDORSE;
        }
        else {
            transactionCode = TransactionCode.OOSENDORSE;
        }

        Transaction transaction = getTransactionManager().createTransaction(policyHeader, inputRecord, transactionEffectiveFromDate, transactionCode);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "owsCreateTransaction");
        }

        return transaction;
    }

    private void performSaveTransaction (PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performSaveTransaction", new Object[]{});
        }

        Record saveRecord = new Record();
        saveRecord.setFields(policyHeader.toRecord(), false);

        saveRecord.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
        PolicyHeaderFields.setPolicyId(saveRecord, policyHeader.getPolicyId());

        saveRecord.setFieldValue("level", "ALL");
        saveRecord.setFieldValue(PolicyInquiryFields.NEW_SAVE_OPTION, PolicyInquiryFields.WIP);
        saveRecord.setFieldValue(PolicyInquiryFields.POLICY_SCREEN_MODE, PolicyInquiryFields.WIP);
        saveRecord.setFieldValue(PolicyInquiryFields.POLICY_VIEW_MODE, PolicyInquiryFields.WIP);

        getTransactionManager().processSaveTransaction(policyHeader, saveRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performSaveTransaction");
        }

    }

    private void performAgentSetup(PolicyHeader policyHeader, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAgentSetup", new Object[]{});
        }
        if (!isProducerProvided(changedPolicy)) {
            Record inputRecord = policyHeader.toRecord();

            if (getTransactionManager().isCheckAgentConfigured(inputRecord)) {
                boolean agentFound = getTransactionManager().isAgentExist(inputRecord);
                if (!agentFound) {
                    getTransactionManager().addDefaultAgent(inputRecord);
                }

                String agentValid = getTransactionManager().isAgentValid(inputRecord);
                if (!agentValid.equalsIgnoreCase("VALID")) {
                    MessageManager mm = MessageManager.getInstance();
                    mm.addInfoMessage("ws.policy.change.agent.invalid.agent", new Object[]{agentValid});
                }
            }
        }
        else {
            MessageManager mm = MessageManager.getInstance();
            Record agentRecord = new Record();
            Record inputRecord = new Record();
            List<LicenseType> licenseList = changedPolicy.getProducer().getLicense();
            for (LicenseType license : licenseList) {
                if (PolicyInquiryFields.PRODUCER.equalsIgnoreCase(license.getLicenseClassCode())) {
                    agentRecord.setFieldValue(PolicyInquiryFields.PRODUCER_AGENT_LIC_ID, license.getLicenseOrPermitNumberId());
                    inputRecord.setFieldValue(PolicyInquiryFields.AGENT_LICENSE_ID, license.getLicenseOrPermitNumberId());
                    inputRecord.setFieldValue(PolicyInquiryFields.POLICY_NO, policyHeader.getPolicyNo());
                    inputRecord.setFieldValue(PolicyInquiryFields.LICENSE_CLASS_CODE, PolicyInquiryFields.PRODUCER);
                    inputRecord.setFieldValue(PolicyInquiryFields.POLICY_TERM_HISTORY_ID, policyHeader.getTermBaseRecordId());
                }
                else if (PolicyInquiryFields.SUB_PROD.equalsIgnoreCase(license.getLicenseClassCode())) {
                    agentRecord.setFieldValue(PolicyInquiryFields.SUB_PROD_AGENT_LIC_ID, license.getLicenseOrPermitNumberId());
                }
                else if (PolicyInquiryFields.COUNT_SIGN.equalsIgnoreCase(license.getLicenseClassCode())) {
                    agentRecord.setFieldValue(PolicyInquiryFields.COUNT_SIGN_AGENT_LIC_ID, license.getLicenseOrPermitNumberId());
                }
                else if (PolicyInquiryFields.AUTH_REP.equalsIgnoreCase(license.getLicenseClassCode())) {
                    agentRecord.setFieldValue(PolicyInquiryFields.AUTH_REP_AGENT_LIC_ID, license.getLicenseOrPermitNumberId());
                }
            }

            Record returnRecord = getAgentManager().getInitialValuesForAddPolicyAgent(policyHeader, inputRecord);
            agentRecord.setFieldValue(PolicyInquiryFields.IS_COMM_PAY_CODE_AVAILABLE, "N");
            agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_BASIS, returnRecord.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_BASIS));
            agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_BASIS, returnRecord.getStringValue(PolicyInquiryFields.RENEWAL_COMM_BASIS));
            agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_BASIS, returnRecord.getStringValue(PolicyInquiryFields.ERE_COMM_BASIS));
            agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_RATE, returnRecord.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_RATE));
            agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_RATE, returnRecord.getStringValue(PolicyInquiryFields.RENEWAL_COMM_RATE));
            agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_RATE, returnRecord.getStringValue(PolicyInquiryFields.ERE_COMM_RATE));
            agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_LIMIT, returnRecord.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_LIMIT));
            agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_LIMIT, returnRecord.getStringValue(PolicyInquiryFields.RENEWAL_COMM_LIMIT));
            agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_LIMIT, returnRecord.getStringValue(PolicyInquiryFields.ERE_COMM_LIMIT));

            List<CommissionType> commissionList = changedPolicy.getProducer().getCommission();
            for (CommissionType commission : commissionList) {
                if (PolicyInquiryFields.NEWBUS.equalsIgnoreCase(commission.getCommissionTypeCode())) {
                    String value = commission.getCommissionBasisCode();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_BASIS, value);
                    }
                    value = commission.getCommissionRatePercent();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_RATE, value);
                    }
                    value = commission.getLimitChargeAmount();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_LIMIT, value);
                    }

                    agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_FLAG_AMOUNT, commission.getCommissionAmount());
                    agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_PAYCODE, commission.getCommissionPayCode());
                    agentRecord.setFieldValue(PolicyInquiryFields.NEW_BUS_COMM_RATE_SCHED_ID, commission.getRateScheduleCode());
                }
                else if (PolicyInquiryFields.RENEWAL.equalsIgnoreCase(commission.getCommissionTypeCode())) {
                    String value = commission.getCommissionBasisCode();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_BASIS, value);
                    }
                    value = commission.getCommissionRatePercent();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_RATE, value);
                    }
                    value = commission.getLimitChargeAmount();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_LIMIT, value);
                    }

                    agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_FLAG_AMOUNT, commission.getCommissionAmount());
                    agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_COMM_PAY_CODE, commission.getCommissionPayCode());
                    agentRecord.setFieldValue(PolicyInquiryFields.RENEWAL_BUS_COMM_RATE_SCHED_ID, commission.getRateScheduleCode());
                }
                else if (PolicyInquiryFields.TAIL.equalsIgnoreCase(commission.getCommissionTypeCode())) {
                    String value = commission.getCommissionBasisCode();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_BASIS, value);
                    }
                    value = commission.getCommissionRatePercent();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_RATE, value);
                    }
                    value = commission.getLimitChargeAmount();
                    if (!StringUtils.isBlank(value)) {
                        agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_LIMIT, value);
                    }

                    agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_FLAT_AMOUNT, commission.getCommissionAmount());
                    agentRecord.setFieldValue(PolicyInquiryFields.ERE_COMM_PAY_CODE, commission.getCommissionPayCode());
                    agentRecord.setFieldValue(PolicyInquiryFields.ERE_BUS_COMM_RATE_SCHED_ID, commission.getRateScheduleCode());
                }
                if (!StringUtils.isBlank(commission.getCommissionPayCode())) {
                    agentRecord.setFieldValue(PolicyInquiryFields.IS_COMM_PAY_CODE_AVAILABLE, "Y");
                }
            }
            agentRecord.setFieldValue(PolicyInquiryFields.POLICY_AGENT_ID, changedPolicy.getProducer().getProducerNumberId());
            agentRecord.setFieldValue(PolicyInquiryFields.AUTHORIZATION_CODE, changedPolicy.getProducer().getProducerAuthorizationCode());
            agentRecord.setFieldValue(PolicyInquiryFields.CHANGE_TYPE, changedPolicy.getProducer().getProducerChangeTypeCode());
            agentRecord.setFieldValue(PolicyInquiryFields.EFFECTIVE_FROM_DATE, policyHeader.getTermEffectiveFromDate());

            if (StringUtils.isBlank(changedPolicy.getProducer().getProducerNumberId())) {
                agentRecord.setFieldValue(PolicyInquiryFields.EFFECTIVE_TO_DATE, "01/01/3000");
                agentRecord.setUpdateIndicator(UpdateIndicator.INSERTED);
            }
            else {
                agentRecord.setFieldValue(PolicyInquiryFields.EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());
                agentRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
            }
            validateProducerRecord(agentRecord, policyHeader);
            RecordSet agentSet = new RecordSet();
            agentSet.setSummaryRecord(agentRecord);
            agentSet.addRecord(agentRecord);
            getAgentManager().validateLicensedAgent(agentRecord, policyHeader);
            getAgentManager().validateAllPolicyAgent(policyHeader, agentSet);
            getAgentManager().saveAllPolicyAgent(policyHeader, agentSet);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAgentSetup");
        }

    }

    private void validateProducerRecord(Record agentRecord, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProducerRecord", new Object[]{agentRecord});
        }

        boolean isNewBusCommBasisValid = true;
        boolean isRenewalCommBasisValid = true;
        boolean isEreCommBasisValid = true;
        MessageManager mm = MessageManager.getInstance();
        if (agentRecord.hasStringValue(PolicyInquiryFields.NEW_BUS_COMM_BASIS)) {
            if (StringUtils.isBlank(agentRecord.getStringValue(PolicyInquiryFields.NEW_BUS_COMM_BASIS))) {
                isNewBusCommBasisValid = false;
            }
        }
        else {
            isNewBusCommBasisValid = false;
        }

        if (agentRecord.hasStringValue(PolicyInquiryFields.RENEWAL_COMM_BASIS)) {
            if (StringUtils.isBlank(agentRecord.getStringValue(PolicyInquiryFields.RENEWAL_COMM_BASIS))) {
                isRenewalCommBasisValid = false;
            }
        }
        else {
            isRenewalCommBasisValid = false;
        }

        if (agentRecord.hasStringValue(PolicyInquiryFields.ERE_COMM_BASIS)) {
            if (StringUtils.isBlank(agentRecord.getStringValue(PolicyInquiryFields.ERE_COMM_BASIS))) {
                isEreCommBasisValid = false;
            }
        }
        else {
            isEreCommBasisValid = false;
        }

        if (!isNewBusCommBasisValid) {
            mm.addErrorMessage("ws.policy.change.agent.newbusCommBasis.required", new String[]{policyHeader.getPolicyNo()});
        }
        if (!isRenewalCommBasisValid) {
            mm.addErrorMessage("ws.policy.change.agent.renewalCommBasis.required", new String[]{policyHeader.getPolicyNo()});
        }
        if (!isEreCommBasisValid) {
            mm.addErrorMessage("ws.policy.change.agent.newbusCommBasis.ereCommBasis", new String[]{policyHeader.getPolicyNo()});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateProducerRecord");
        }
    }

    private boolean isProducerProvided(MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isProducerProvided", new Object[]{changedPolicy});
        }

        boolean itExists = false;

        ProducerType producer = changedPolicy.getProducer();
        List<LicenseType> licenseList = null;

        if (producer != null) {
            licenseList = changedPolicy.getProducer().getLicense();
        }

        if (licenseList != null && licenseList.size()>0) {
            for (LicenseType license : licenseList) {
                if (PolicyInquiryFields.PRODUCER.equalsIgnoreCase(license.getLicenseClassCode())) {
                    String producerLicenseId = license.getLicenseOrPermitNumberId();
                    if (!StringUtils.isBlank(producerLicenseId)) {
                        itExists = true;
                        break;
                    }
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isProducerProvided", itExists);
        }

        return itExists;
    }

    private void performUnderwriterSetup(PolicyHeader policyHeader, MedicalMalpracticePolicyType changedPolicy,
                                         MedicalMalpracticePolicyType originalPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performUnderwriterSetup", new Object[]{changedPolicy, originalPolicy});
        }
        RecordSet rs = new RecordSet();
        List <InsurerType> underwriters;
        List <InsurerType> originalUnderwriters;
        underwriters = getUnderwriterList(changedPolicy);

        if (!isNewPolicyB()) {
            originalUnderwriters = getUnderwriterList(originalPolicy);
            if (originalUnderwriters != null && originalUnderwriters.size() > 0) {
                underwriters = getChangedUnderwriters(underwriters, originalUnderwriters);
            }
        }

        if (underwriters != null && underwriters.size() > 0) {
            for (InsurerType underwriter : underwriters) {
                if (underwriter != null) {
                    validateUnderwriterEntity(underwriter, policyHeader);
                    String entityId = underwriter.getReferredParty().getPartyNumberId();
                    if (StringUtils.isBlank(entityId)) {
                        continue;
                    }
                    Record inputRecord = new Record();
                    inputRecord.setFieldValue(PolicyInquiryFields.POLICY_HOLDER_ENTITY_ID, entityId);
                    inputRecord.setFieldValue(PolicyInquiryFields.POLICY_ID, policyHeader.getPolicyId());
                    inputRecord.setFieldValue(PolicyInquiryFields.POLICY_NO, policyHeader.getPolicyNo());
                    inputRecord.setFieldValue(PolicyInquiryFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
                    inputRecord.setFieldValue(PolicyInquiryFields.TERM_EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());
                    String effectiveFromDate = null;
                    String effectiveToDate = null;
                    if (underwriter.getEffectivePeriod() == null || StringUtils.isBlank(underwriter.getEffectivePeriod().getStartDate())) {
                        effectiveFromDate = policyHeader.getTermEffectiveFromDate();
                    }
                    else {
                        effectiveFromDate = DateUtils.parseXMLDateToOasisDate(underwriter.getEffectivePeriod().getStartDate());
                    }
                    if (underwriter.getEffectivePeriod() == null || StringUtils.isBlank(underwriter.getEffectivePeriod().getEndDate())) {
                        effectiveToDate = policyHeader.getTermEffectiveToDate();
                    }
                    else {
                        effectiveToDate = DateUtils.parseXMLDateToOasisDate(underwriter.getEffectivePeriod().getEndDate());
                    }
                    inputRecord.setFieldValue(PolicyInquiryFields.EFFECTIVE_FROM_DATE, effectiveFromDate);
                    inputRecord.setFieldValue(PolicyInquiryFields.EFFECTIVE_TO_DATE, effectiveToDate);

                    String renewalB = null;
                    if (StringUtils.isBlank(underwriter.getRenewalIndicator())) {
                        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_UNDERWRITER_ACTION_CLASS_NAME);
                        renewalB = output.getStringValue(PolicyInquiryFields.RENEWAL_B);
                    }
                    else {
                        renewalB = YesNoEmptyFlag.getInstance(underwriter.getRenewalIndicator()).toString();
                    }
                    inputRecord.setFieldValue(PolicyInquiryFields.RENEWAL_B, renewalB);
                    if (StringUtils.isBlank(underwriter.getInsurerNumberId())) {
                        inputRecord.setFieldValue(PolicyInquiryFields.ROW_STATUS, "NEW");
                        inputRecord.setUpdateIndicator(UpdateIndicator.INSERTED);
                    }
                    else {
                        inputRecord.setFieldValue(PolicyInquiryFields.ROW_STATUS, "MODIFIED");
                        inputRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
                    }
                    inputRecord.setFieldValue(PolicyInquiryFields.ENTITY_ROLE_ID, underwriter.getInsurerNumberId());
                    if (StringUtils.isBlank(underwriter.getTypeCode())){
                        inputRecord.setFieldValue(UnderwritingFields.TYPE, "UNDWRITER");
                    }
                    else{
                        inputRecord.setFieldValue(UnderwritingFields.TYPE, underwriter.getTypeCode());
                    }
                    inputRecord.setFieldValue(PolicyInquiryFields.ADDL_POLICY_INFO_CHANGED_B, "N");
                    inputRecord.setFieldValue(PolicyInquiryFields.LEGACY_POLICY_NO, "");
                    inputRecord.setFieldValue(PolicyInquiryFields.ACCOUNTING_DATE, FormatUtils.formatDate(new Date()));
                    inputRecord.setFieldValue(PolicyInquiryFields.TRANSACTION_COMMENT, "");
                    rs.addRecord(inputRecord);
                }
            }
            if (rs.getSize() > 0) {
                rs.setSummaryRecord(rs.getFirstRecord());
                if (!isNewPolicyB()) {
                    // get the latest underwriters, replace old ones with changed ones.
                    rs = getLatestUnderwriters(rs, policyHeader);
                }
                getUnderwriterManager().saveAllUnderwriters(policyHeader, rs);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performUnderwriterSetup");
        }
    }

    private void setReturnPolicyInfo(PolicyHeader policyHeader, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setReturnPolicyInfo", new Object[]{changedPolicy});
        }

        Record policyRecord = getPolicyInquiryServiceHelper().loadPolicyInformation(policyHeader.getPolicyNo(),
            policyHeader.getTermBaseRecordId(), "");

        if (policyRecord.getSize() != 0) {
            PolicyStatusCodeType policyStatusCodeType = new PolicyStatusCodeType();
            policyStatusCodeType.setValue(policyRecord.getStringValue(PolicyInquiryFields.POLICY_STATUS, ""));
            changedPolicy.getFirstPolicyDetail().setPolicyStatusCode(policyStatusCodeType);
            changedPolicy.setCurrentTermAmount(policyRecord.getStringValue(PolicyInquiryFields.TERM_WRITTEN_PREMIUM));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setReturnPolicyInfo");
        }
    }

    private RecordSet getLatestUnderwriters(RecordSet changedUnderwriters, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestUnderwriters", new Object[]{changedUnderwriters, policyHeader});
        }

        RecordSet underWriterRs = new RecordSet();
        RecordSet addedUnderwriters = new RecordSet();
        RecordSet updatedUnderwriters = new RecordSet();
        //#1. Get existed underwriters in database.
        underWriterRs = getUnderwriterManager().loadUnderwritersByTermForWS(policyHeader);
        if (underWriterRs.getSize() > 0) {
            underWriterRs.setFieldValueOnAll(PolicyInquiryFields.POLICY_NO, policyHeader.getPolicyNo());
            underWriterRs.setFieldValueOnAll(PolicyInquiryFields.TERM_EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());
            underWriterRs.setFieldValueOnAll(PolicyInquiryFields.ADDL_POLICY_INFO_CHANGED_B, "N");
            underWriterRs.setFieldValueOnAll(PolicyInquiryFields.ACCOUNTING_DATE, FormatUtils.formatDate(new Date()));
        }
        for (Record record: changedUnderwriters.getRecordList()) {
            if (record.getUpdateIndicator().equals(UpdateIndicator.INSERTED)) {
                addedUnderwriters.addRecord(record);
            }
            if (record.getUpdateIndicator().equals(UpdateIndicator.UPDATED)) {
                updatedUnderwriters.addRecord(record);
            }
        }
        //#2. Update the underwriters with the latest ones.
        if (updatedUnderwriters.getSize() > 0) {
            for (Record record: updatedUnderwriters.getRecordList()) {
                String insurerNumberId = record.getStringValue(PolicyInquiryFields.INSURER_NUMBER_ID);
                for (Record r: underWriterRs.getRecordList()) {
                    if (insurerNumberId.equals(r.getStringValue(PolicyInquiryFields.INSURER_NUMBER_ID))) {
                        underWriterRs.removeRecord(r, true);
                        underWriterRs.addRecord(record);
                        break;
                    }
                }
            }
        }
        //#3. Add the new underwriters
        if (addedUnderwriters.getSize() > 0) {
            underWriterRs.addRecords(addedUnderwriters);
        }
        underWriterRs.setSummaryRecord(changedUnderwriters.getFirstRecord());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestUnderwriters", underWriterRs);
        }

        return underWriterRs;
    }

    private List <InsurerType> getUnderwriterList(MedicalMalpracticePolicyType medicalMalpracticePolicyType) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUnderwriterList", new Object[]{medicalMalpracticePolicyType});
        }

        List <InsurerType> underwriters;
        // The following logic supports the deprecated <Underwriter> element, which is
        // replaced right now with <Insurer>. It is kept for backwards compatibility.
        List<UnderwriterType> obsoleteUnderwriter = medicalMalpracticePolicyType.getUnderwriter();
        if (obsoleteUnderwriter.size() > 0) {
            underwriters = new ArrayList<InsurerType>();
            for (UnderwriterType obsUdw : obsoleteUnderwriter){
                InsurerType underwriter = new InsurerType();
                underwriter.setInsurerNumberId(obsUdw.getUnderwriterNumberId());
                underwriter.setEffectivePeriod(obsUdw.getEffectivePeriod());
                underwriter.setReferredParty(obsUdw.getReferredParty());
                underwriter.setRenewalIndicator(YesNoEmptyFlag.getInstance(obsUdw.getRenewalB()).trueFalseEmptyValue());
                underwriter.setTypeCode(obsUdw.getTypeCode());
                underwriters.add(underwriter);
            }
        }
        else {
            underwriters = medicalMalpracticePolicyType.getInsurer();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUnderwriterList", underwriters);
        }

        return underwriters;

    }

    private List <InsurerType> getChangedUnderwriters(List <InsurerType> underwriters, List <InsurerType> originalUnderwriters) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChangedUnderwriters", new Object[]{underwriters, originalUnderwriters});
        }

        MessageManager message = MessageManager.getInstance();
        List <InsurerType> changedUnderwriters = new ArrayList<InsurerType>();
        if (underwriters != null && underwriters.size() > 0) {
            for (InsurerType underwriter : underwriters) {
                String insuredNumberId = underwriter.getInsurerNumberId();
                if (StringUtils.isBlank(insuredNumberId)) {
                    changedUnderwriters.add(underwriter);
                    continue;
                }
                for (InsurerType originalUnderwriter: originalUnderwriters) {
                    if (insuredNumberId.equals(originalUnderwriter.getInsurerNumberId())) {
                        String typeCode = underwriter.getTypeCode();
                        String startDate = underwriter.getEffectivePeriod() == null ? null : underwriter.getEffectivePeriod().getStartDate();
                        String endDate = underwriter.getEffectivePeriod() == null ? null : underwriter.getEffectivePeriod().getEndDate();
                        String entityId = underwriter.getReferredParty() == null ? null : underwriter.getReferredParty().getPartyNumberId();
                        String renewalIndicator = underwriter.getRenewalIndicator();

                        if ((null != typeCode && !typeCode.equals(originalUnderwriter.getTypeCode())) ||
                            (null != startDate && !startDate.equals(originalUnderwriter.getEffectivePeriod().getStartDate()))) {
                            message.addErrorMessage("pm.maintainUnderwriter.changeUnderwriter.error", new Object[]{entityId});
                            throw new AppException("Change underwriter failed.");
                        }

                        if ((null != endDate && !endDate.equals(originalUnderwriter.getEffectivePeriod().getEndDate())) ||
                            (null != entityId && !entityId.equals(originalUnderwriter.getReferredParty().getPartyNumberId())) ||
                            (null != renewalIndicator && !renewalIndicator.equals(originalUnderwriter.getRenewalIndicator()))) {
                            changedUnderwriters.add(underwriter);
                        }
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChangedUnderwriters", changedUnderwriters);
        }

        return changedUnderwriters;
    }

    private void validateUnderwriterEntity(InsurerType underwriter, PolicyHeader policyHeader) {
        MessageManager mm = MessageManager.getInstance();
        if (underwriter.getReferredParty() != null) {
            if (!StringUtils.isBlank(underwriter.getReferredParty().getPartyNumberId()) ||
                ((SysParmProvider.getInstance().getSysParm("UNDERWRITER", "N").equalsIgnoreCase("N") &&
                    policyHeader.getPolicyCycleCode().isPolicy()) ||
                    (SysParmProvider.getInstance().getSysParm("QUNDERWRITER", "N").equalsIgnoreCase("N") &&
                    policyHeader.getPolicyCycleCode().isQuote()))) {
                String entityId = underwriter.getReferredParty().getPartyNumberId();
                if (StringUtils.isBlank(entityId)) {
                    return;
                }
                Record inputRecord = new Record();
                inputRecord.setFieldValue(PolicyInquiryFields.UNDERWRITER_ENTITY_ID, entityId);
                inputRecord.setFieldValue(PolicyInquiryFields.UW_TYPE_CODE, underwriter.getTypeCode());
                boolean validateResult = getUnderwriterManager().isUnderwriterEntityExists(inputRecord, policyHeader);
                if (!validateResult) {
                    mm.addErrorMessage("pm.maintainUnderwriter.invalidUnderwriterEntity.error", new Object[]{policyHeader.getPolicyNo()});
                }
            }
            else {
                mm.addErrorMessage("pm.maintainUnderwriter.invalidUnderwriterEntityId.required", new Object[]{policyHeader.getPolicyNo()});
            }
        }
        else {
            mm.addErrorMessage("pm.maintainUnderwriter.invalidUnderwriterEntityId.required", new Object[]{policyHeader.getPolicyNo()});
        }
        if (mm.hasErrorMessages()) {
            throw new AppException("Underwriter validate failed.");
        }
    }

    private void validateForNewTransaction(MedicalMalpracticePolicyType changedPolicy, PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForNewTransaction", new Object[]{changedPolicy});
        }

        String errorMsg = "";

        if (changedPolicy.getTransactionDetail()==null || StringUtils.isBlank(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()))
            errorMsg = errorMsg + "TransactionDetail/TransactionEffectiveDate, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.new.transaction", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForNewTransaction", ae);
            throw ae;
        }

        Date termEff = DateUtils.parseDate(policyHeader.getTermEffectiveFromDate());
        Date termExp = DateUtils.parseDate(policyHeader.getTermEffectiveToDate());
        Date transactionEffectiveDate = DateUtils.parseXMLDate(changedPolicy.getTransactionDetail().getTransactionEffectiveDate());
        if (transactionEffectiveDate.before(termEff) ||
            transactionEffectiveDate.after(termExp) ||
            transactionEffectiveDate.equals(termExp)) {
            AppException ae = new AppException("ws.policy.change.transaction.date.invalid", "",
                new String[]{policyHeader.getTermEffectiveFromDate(), policyHeader.getTermEffectiveToDate()});
            l.throwing(getClass().getName(), "validateForNewTransaction", ae);
            throw ae;
        }
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForNewTransaction");
        }
    }

    private Record setInitialValuesForCreateTransaction() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInitialValuesForCreateTransaction", new Object[]{});
        }

        Record record = new Record();
        SimpleDateFormat sdfDate = new SimpleDateFormat(DateUtils.XML_DATE_FORMAT_PATTERN);
        record.setFieldValue(PolicyInquiryFields.ACCOUNTING_DATE, DateUtils.parseXMLDateToOasisDate(sdfDate.format(new Date())));
        record.setFieldValue(PolicyInquiryFields.TRANSACTION_COMMENT, "");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInitialValuesForCreateTransaction", record);
        }

        return record;
    }

    private void setPolicyReturnData(PolicyHeader policyHeader, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setPolicyReturnData", new Object[]{policyHeader, changedPolicy});
        }

        changedPolicy.setPolicyNumberId(policyHeader.getPolicyId());
        changedPolicy.setPolicyId(policyHeader.getPolicyNo());
        changedPolicy.setPolicyTermNumberId(policyHeader.getTermBaseRecordId());
        if (changedPolicy.getTransactionDetail() != null) {
            changedPolicy.getTransactionDetail().setTransactionNumberId(policyHeader.getLastTransactionId());
            changedPolicy.getTransactionDetail().setTransactionEffectiveDate(DateUtils.parseOasisDateToXMLDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate()));
            changedPolicy.getTransactionDetail().getTransactionCode().setValue(policyHeader.getLastTransactionInfo().getTransactionCode().toString());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setPolicyReturnData", changedPolicy);
        }
    }

    private void createNewPolicy(MedicalMalpracticePolicyChangeRequestType policyChangeRequest, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createNewPolicy", new Object[]{policyChangeRequest, changedPolicy});
        }

        validateForCreateNewPolicy(changedPolicy);

        Record inputRecord = setInputPolicyToRecord(changedPolicy, changedPolicy);

        if (!StringUtils.isBlank(changedPolicy.getOriginalPolicyId())) {
            inputRecord.setFieldValue(PolicyInquiryFields.ORIGINAL_POLICY_NO, changedPolicy.getOriginalPolicyId());
        }

        if (!StringUtils.isBlank(changedPolicy.getOriginalPolicyCycleCode())) {
            inputRecord.setFieldValue(PolicyInquiryFields.ORIGINAL_POLICY_CYCLE_CODE, changedPolicy.getOriginalPolicyCycleCode());
        }

        if (changedPolicy.getPolicyHolder() != null) {
            inputRecord.setFieldValue(PolicyInquiryFields.POLICY_HOLDER_NAME_ENTITY_ID, changedPolicy.getPolicyHolder().getReferredParty().getPartyNumberId());
        }

        if (!inputRecord.hasStringValue(PolicyInquiryFields.POLICY_CYCLE_CODE)) {
            inputRecord.setFieldValue(PolicyInquiryFields.POLICY_CYCLE_CODE, "POLICY");
        }

        if (changedPolicy.getTransactionDetail() != null) {
            inputRecord.setFieldValue(PolicyInquiryFields.USER_TRANSACTION_CODE, changedPolicy.getTransactionDetail().getTransactionCode().getValue());
        }

        String shortRateB = "N";
        if (changedPolicy.getFirstPolicyDetail().getShortTermIndicator()!=null)
            shortRateB = YesNoEmptyFlag.getInstance(changedPolicy.getFirstPolicyDetail().getShortTermIndicator()).getName();
        inputRecord.setFieldValue(PolicyInquiryFields.SHORT_RATE_B, shortRateB);

        inputRecord.setFieldValue(PolicyInquiryFields.TERM_TYPE_CODE, "NON_COMMON");
        inputRecord.setFieldValue(PolicyInquiryFields.REGIONAL_OFFICE_IS_VISIBLE, "N");
        inputRecord.setFieldValue(PolicyInquiryFields.REQUEST_CONTEXT, "PM");
        inputRecord.setFieldValue(PolicyInquiryFields.IS_TERM_EFFECTIVE_TO_DATE_CHANGED, "N");

        SimpleDateFormat sdfDate = new SimpleDateFormat(DateUtils.XML_DATE_FORMAT_PATTERN);
        inputRecord.setFieldValue(PolicyInquiryFields.ACCOUNTING_DATE, DateUtils.parseXMLDateToOasisDate(sdfDate.format(new Date())));

        if (policyChangeRequest.getDataModificationInformation() != null && policyChangeRequest.getDataModificationInformation().getActionCode() != null) {
            for (String actionCode : policyChangeRequest.getDataModificationInformation().getActionCode()) {
                inputRecord.setFieldValue(actionCode, actionCode);
            }
        }

        String policyNo = getPolicyManager().createPolicy(inputRecord, false);

        changedPolicy.setPolicyId(policyNo);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createNewPolicy", policyNo);
        }
    }

    private void validateForCreateNewPolicy(MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForCreateNewPolicy", new Object[]{changedPolicy});
        }

        String errorMsg = "";

        if (changedPolicy.getPolicyHolder()==null)
            errorMsg = errorMsg + "Policy Holder entity, ";
        else {
            String partyNumberId = null;
            if (changedPolicy.getPolicyHolder().getReferredParty()!=null) {
                partyNumberId = changedPolicy.getPolicyHolder().getReferredParty().getPartyNumberId();
            }
            String organizationReference = changedPolicy.getPolicyHolder().getOrganizationReference();
            String personReference = changedPolicy.getPolicyHolder().getPersonReference();

            if (StringUtils.isBlank(partyNumberId) && StringUtils.isBlank(organizationReference) && StringUtils.isBlank(personReference)) {
                errorMsg = errorMsg + "Policy Holder personReference or organizationReference or ReferredParty/PartyNumberId, ";
            }
        }

        if (changedPolicy.getContractPeriod()==null) {
            errorMsg = errorMsg + "Contract Period, ";
        }
        else {
            if (StringUtils.isBlank(changedPolicy.getContractPeriod().getStartDate()))
                errorMsg = errorMsg + "Term Effective Date, ";
            if (StringUtils.isBlank(changedPolicy.getContractPeriod().getEndDate()))
                errorMsg = errorMsg + "Term Expiration Date, ";
        }

        if (changedPolicy.getPolicyDetail()==null) {
            errorMsg = errorMsg + "PolicyDetail, ";
        }
        else {
            if (changedPolicy.getFirstPolicyDetail().getIssueCompany()==null) {
                errorMsg = errorMsg + "IssueCompany, ";
            }
            else {
                if (changedPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty()==null ||
                    (StringUtils.isBlank(changedPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getPartyNumberId())
                     && StringUtils.isBlank(changedPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getClientId())))
                    errorMsg = errorMsg + "Issue Company id or client id, ";
                if (changedPolicy.getFirstPolicyDetail().getIssueCompany().getControllingStateOrProvinceCode()==null ||
                    StringUtils.isBlank(changedPolicy.getFirstPolicyDetail().getIssueCompany().getControllingStateOrProvinceCode().getValue()))
                    errorMsg = errorMsg + "Issue State, ";
                if (changedPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode()==null ||
                    StringUtils.isBlank(changedPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode().getValue()))
                    errorMsg = errorMsg + "Process Location, ";
            }
            if (changedPolicy.getFirstPolicyDetail().getPolicyTypeCode()==null ||
                StringUtils.isBlank(changedPolicy.getFirstPolicyDetail().getPolicyTypeCode().getValue()))
                errorMsg = errorMsg + "Policy Type, ";
        }

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.new.policy.required", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForCreateNewPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForCreateNewPolicy");
        }
    }

    private void validateForChangePolicy(MedicalMalpracticePolicyType originalPolicy, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangePolicy", new Object[]{originalPolicy, changedPolicy});
        }

        String errorMsg = "";

        if (originalPolicy != null) {
            if (originalPolicy.getPolicyId()!=null && changedPolicy.getPolicyId()!=null && !originalPolicy.getPolicyId().equalsIgnoreCase(changedPolicy.getPolicyId()))
                errorMsg = errorMsg + "Policy Id, ";
            if (originalPolicy.getPolicyNumberId()!=null && changedPolicy.getPolicyNumberId()!=null && !originalPolicy.getPolicyNumberId().equalsIgnoreCase(changedPolicy.getPolicyNumberId()))
                errorMsg = errorMsg + "Policy Number Id, ";
            if (originalPolicy.getPolicyTermNumberId()!=null && changedPolicy.getPolicyTermNumberId()!=null && !originalPolicy.getPolicyTermNumberId().equalsIgnoreCase(changedPolicy.getPolicyTermNumberId()))
                errorMsg = errorMsg + "Policy Term Number Id, ";
            if (originalPolicy.getPrintName()!=null && originalPolicy.getPrintName().getFullName()!=null && changedPolicy.getPrintName()!=null &&
                !originalPolicy.getPrintName().getFullName().equalsIgnoreCase(changedPolicy.getPrintName().getFullName()))
                errorMsg = errorMsg + "Full Name, ";
            if (originalPolicy.getContractPeriod()!=null && changedPolicy.getContractPeriod()!=null) {
                if (originalPolicy.getContractPeriod().getStartDate()!=null && !originalPolicy.getContractPeriod().getStartDate().equalsIgnoreCase(changedPolicy.getContractPeriod().getStartDate()))
                    errorMsg = errorMsg + "Start Period Date, ";
                if (originalPolicy.getContractPeriod().getEndDate()!=null && !originalPolicy.getContractPeriod().getEndDate().equalsIgnoreCase(changedPolicy.getContractPeriod().getEndDate()))
                    errorMsg = errorMsg + "End Period Date, ";
            }

            if (originalPolicy.getTransactionDetail()!=null && changedPolicy.getTransactionDetail()!=null) {
                if (!StringUtils.isBlank(originalPolicy.getTransactionDetail().getTransactionNumberId()) &&
                    !StringUtils.isBlank(changedPolicy.getTransactionDetail().getTransactionNumberId()) &&
                    !originalPolicy.getTransactionDetail().getTransactionNumberId().equalsIgnoreCase(changedPolicy.getTransactionDetail().getTransactionNumberId()))
                    errorMsg = errorMsg + "Transaction id, ";
                if (!StringUtils.isBlank(originalPolicy.getTransactionDetail().getTransactionEffectiveDate()) &&
                    !StringUtils.isBlank(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()) &&
                    !originalPolicy.getTransactionDetail().getTransactionEffectiveDate().equalsIgnoreCase(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()))
                    errorMsg = errorMsg + "Transaction Effective Date, ";
                if (!StringUtils.isBlank(originalPolicy.getTransactionDetail().getTransactionCode().getValue()) &&
                    !StringUtils.isBlank(changedPolicy.getTransactionDetail().getTransactionCode().getValue()) &&
                    !originalPolicy.getTransactionDetail().getTransactionCode().getValue().equalsIgnoreCase(changedPolicy.getTransactionDetail().getTransactionCode().getValue()))
                    errorMsg = errorMsg + "Transaction Code, ";
            }

            if (originalPolicy.getPolicyHolder()!=null && originalPolicy.getPolicyHolder().getReferredParty()!=null && originalPolicy.getPolicyHolder().getReferredParty().getPartyNumberId()!=null) {
                if (!originalPolicy.getPolicyHolder().getReferredParty().getPartyNumberId().equalsIgnoreCase(changedPolicy.getPolicyHolder().getReferredParty().getPartyNumberId()))
                    errorMsg = errorMsg + "Policy Holder, ";
            }

            if (originalPolicy.getProducer()!=null && originalPolicy.getProducer().getReferredParty()!=null && originalPolicy.getProducer().getReferredParty().getPartyNumberId()!=null) {
                if (!originalPolicy.getProducer().getReferredParty().getPartyNumberId().equalsIgnoreCase(changedPolicy.getProducer().getReferredParty().getPartyNumberId()))
                    errorMsg = errorMsg + "Producer, ";
            }

            if (originalPolicy.getPrincipalBillingAccountInformation()!=null && originalPolicy.getPrincipalBillingAccountInformation().size() >0
                && originalPolicy.getPrincipalBillingAccountInformation().get(0).getBillingAccountId()!=null) {
                if (!originalPolicy.getPrincipalBillingAccountInformation().get(0).getBillingAccountId().equalsIgnoreCase(changedPolicy.getPrincipalBillingAccountInformation().get(0).getBillingAccountId()))
                    errorMsg = errorMsg + "Billing Account Number, ";
            }

            if (originalPolicy.getPolicyDetail()!=null && originalPolicy.getFirstPolicyDetail().getIssueCompany()!=null && originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty()!=null) {
                if (!StringUtils.isBlank(originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getClientId())) {
                    if (!originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getClientId().equalsIgnoreCase(changedPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getClientId())) {
                        errorMsg = errorMsg + "Issue Company, ";
                    }
                }
                else if (!StringUtils.isBlank(originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getPartyNumberId())) {
                    if (!originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getPartyNumberId().equalsIgnoreCase(changedPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getPartyNumberId())) {
                        errorMsg = errorMsg + "Issue Company, ";
                    }
                }
            }

            if (originalPolicy.getPolicyDetail()!=null && originalPolicy.getFirstPolicyDetail().getIssueCompany()!=null && originalPolicy.getFirstPolicyDetail().getIssueCompany().getControllingStateOrProvinceCode()!=null) {
                if (!originalPolicy.getFirstPolicyDetail().getIssueCompany().getControllingStateOrProvinceCode().getValue().equalsIgnoreCase(changedPolicy.getFirstPolicyDetail().getIssueCompany().getControllingStateOrProvinceCode().getValue()))
                    errorMsg = errorMsg + "Controlling State, ";
            }

            if (originalPolicy.getPolicyDetail()!=null && originalPolicy.getFirstPolicyDetail().getIssueCompany()!=null && originalPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode()!=null) {
                if (!originalPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode().getValue().equalsIgnoreCase(changedPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode().getValue()))
                    errorMsg = errorMsg + "Process Location, ";
            }

            if (errorMsg != "") {
                errorMsg = errorMsg.substring(0, errorMsg.length()-2);
                AppException ae = new AppException("ws.policy.change.existing.policy.required", "", new String[]{errorMsg});
                l.throwing(getClass().getName(), "validateForChangePolicy", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangePolicy");
        }
    }

    private void validateForChangeRisk(InsuredType originalInsured, InsuredType insured) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeRisk", new Object[]{originalInsured, insured});
        }

        String errorMsg = "";
        if (!originalInsured.getReferredParty().getPartyNumberId().equalsIgnoreCase(insured.getReferredParty().getPartyNumberId()))
            errorMsg = errorMsg + "ReferredParty/PartyNumberId";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.existing.risk", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForChangeRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeRisk");
        }
    }

    private void validateForChangeRiskVersion(PolicyHeader policyHeader, InsuredVersionType originalInsured, InsuredVersionType insured, Record dbRiskRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeRiskVersion", new Object[]{policyHeader, originalInsured, insured});
        }

        if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
            if (RecordMode.TEMP.equals(insured.getInsuredVersionDetail().getVersionModeCode()) &&
                !StringUtils.isBlank(insured.getInsuredVersionDetail().getParentVersionNumberId())) {
                AppException ae = new AppException("pm.oose.modified.record.exist.error2", "",
                    new String[]{"risk"});
                l.throwing(getClass().getName(), "validateForChangeRiskVersion", ae);
                throw ae;
            }
        }

        if (dbRiskRecord == null) {
            AppException ae = new AppException("ws.policy.change.version.not.exist", "",
                new String[]{"Insured Version " + insured.getInsuredVersionNumberId()});
            l.throwing(getClass().getName(), "validateForChangeRiskVersion", ae);
            throw ae;
        }

        String errorMsg = "";
        if (!originalInsured.getPracticeStateOrProvinceCode().getValue().equalsIgnoreCase(insured.getPracticeStateOrProvinceCode().getValue()))
            errorMsg = errorMsg + "PracticeStateOrProvinceCode, ";
        if (!originalInsured.getInsuredTypeCode().getValue().equalsIgnoreCase(insured.getInsuredTypeCode().getValue()))
            errorMsg = errorMsg + "InsuredTypeCode, ";
        if (!originalInsured.getPrimaryIndicator().equalsIgnoreCase(insured.getPrimaryIndicator()))
            errorMsg = errorMsg + "PrimaryIndicator, ";
        if (!originalInsured.getEffectivePeriod().getStartDate().equalsIgnoreCase(insured.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";
        if (!originalInsured.getEffectivePeriod().getEndDate().equalsIgnoreCase(insured.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";
        if (!originalInsured.getInsuredVersionDetail().getVersionModeCode().equalsIgnoreCase(insured.getInsuredVersionDetail().getVersionModeCode()))
            errorMsg = errorMsg + "InsuredVersionDetail/VersionModeCode, ";
        if (!originalInsured.getInsuredVersionDetail().getParentVersionNumberId().equalsIgnoreCase(insured.getInsuredVersionDetail().getParentVersionNumberId()))
            errorMsg = errorMsg + "InsuredVersionDetail/ParentVersionNumberId, ";
        if (!originalInsured.getInsuredVersionDetail().getAfterImageIndicator().equalsIgnoreCase(insured.getInsuredVersionDetail().getAfterImageIndicator()))
            errorMsg = errorMsg + "InsuredVersionDetail/AfterImageIndicator, ";
        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.existing.risk.version", "", new String[]{errorMsg, insured.getInsuredVersionNumberId()});
            l.throwing(getClass().getName(), "validateForChangeRiskVersion", ae);
            throw ae;
        }

        Date versionEff = DateUtils.parseXMLDate(insured.getEffectivePeriod().getStartDate());
        Date versionExp = DateUtils.parseXMLDate(insured.getEffectivePeriod().getEndDate());
        Date transactionDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        if (transactionDate.before(versionEff) ||
            transactionDate.after(versionExp) ||
            transactionDate.equals(versionExp)) {
            AppException ae = new AppException("ws.policy.change.version.invalid", "",
                new String[]{"Insured Version: " + insured.getInsuredVersionNumberId()});
            l.throwing(getClass().getName(), "validateForChangeRiskVersion", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeRiskVersion");
        }
    }

    private void validateForNewRiskVersion(InsuredType insured, InsuredVersionType insuredVer) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForNewRiskVersion", new Object[]{insured, insuredVer});
        }

        if (insured.getInsuredNumberId() != null && insured.getInsuredNumberId().length()>0) {
            String errorMsg = "Request for new Insured should not have InsuredNumberId provided ("+insured.getInsuredNumberId()+").";
            AppException ae = new AppException("ws.policy.change.invalid.update", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForNewRiskVersion", ae);
            throw ae;
        }

        String errorMsg = "";

        if (insured.getReferredParty() == null || StringUtils.isBlank(insured.getReferredParty().getPartyNumberId())) {
            if (StringUtils.isBlank(insured.getOrganizationReference())
                && StringUtils.isBlank(insured.getPersonReference())
                && StringUtils.isBlank(insured.getPropertyReference())) {
                errorMsg = errorMsg + "person or organization or property reference, ";
            }
        }
        if (StringUtils.isBlank(insuredVer.getPracticeStateOrProvinceCode().getValue()))
            errorMsg = errorMsg + "PracticeStateOrProvinceCode, ";
        if (StringUtils.isBlank(insuredVer.getInsuredTypeCode().getValue()))
            errorMsg = errorMsg + "InsuredTypeCode, ";
        if (StringUtils.isBlank(insuredVer.getPrimaryIndicator()))
            errorMsg = errorMsg + "PrimaryIndicator, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.new.risk.version", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForNewRiskVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForNewRiskVersion");
        }
    }

    private void validateForNewCoverageVersion(MedicalMalpracticeCoverageType coverage, MedicalMalpracticeCoverageVersionType coverageVer) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForNewCoverageVersion", new Object[]{coverage, coverageVer});
        }

        if (coverage.getCoverageNumberId()!=null && coverage.getCoverageNumberId().length()>0) {
            String errorMsg = "Request for new Coverage should not have CoverageNumberId provided ("+coverage.getCoverageNumberId()+").";
            AppException ae = new AppException("ws.policy.change.invalid.update", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForNewCoverageVersion", ae);
            throw ae;
        }

        String errorMsg = "";
        if (StringUtils.isBlank(coverage.getMedicalMalpracticeCoverageCode().getValue()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageCode, ";
        if (coverage.getReferredInsured() == null ||
            StringUtils.isBlank(coverage.getReferredInsured().getInsuredReference()))
            errorMsg = errorMsg + "ReferredInsured, ";
        if (StringUtils.isBlank(coverageVer.getMedicalMalpracticeCoverageStatusCode()))
            coverageVer.setMedicalMalpracticeCoverageStatusCode("ACTIVE");
        if (coverageVer.getEffectivePeriod() == null)
            errorMsg = errorMsg + "StartDate, EndDate, ";
        else if (StringUtils.isBlank(coverageVer.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "StartDate, ";
        else if (StringUtils.isBlank(coverageVer.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EndDate, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.new.coverage.version", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForNewCoverageVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForNewCoverageVersion");
        }

    }

    private void validateForNewComponentVersion(CreditSurchargeDeductibleType component, CreditSurchargeDeductibleVersionType compVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForNewComponentVersion", new Object[]{component, compVersion});
        }

        if (component.getCreditSurchargeDeductibleNumberId() != null && component.getCreditSurchargeDeductibleNumberId().length()>0) {
            String errorMsg = "Request for new Component should not have CreditSurchargeDeductibleNumberId provided ("+component.getCreditSurchargeDeductibleNumberId()+").";
            AppException ae = new AppException("ws.policy.change.invalid.update", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForNewComponentVersion", ae);
            throw ae;
        }

        String errorMsg = "";
        if (component.getReferredMedicalMalpracticeCoverage() == null ||
            StringUtils.isBlank(component.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference()))
            errorMsg = errorMsg + "ReferredMedicalMalpracticeCoverage, ";
        if (StringUtils.isBlank(component.getCreditSurchargeDeductibleCodeNumberId()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleCodeNumberId, ";
        if (component.getCreditSurchargeDeductibleCode() == null ||
            StringUtils.isBlank(component.getCreditSurchargeDeductibleCode().getValue()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleCode, ";
        if (component.getCreditSurchargeDeductibleTypeCode() == null ||
            StringUtils.isBlank(component.getCreditSurchargeDeductibleTypeCode().getValue()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleTypeCode, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.new.component.version", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForNewComponentVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForNewComponentVersion");
        }
    }

    private Record setInputPolicyToRecord(MedicalMalpracticePolicyType originalPolicy,
                                          MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputPolicyToRecord", new Object[]{originalPolicy, changedPolicy});
        }
        Record inputRecord = new Record();

        inputRecord.setFieldValue(PolicyInquiryFields.ROW_STATUS, "MODIFIED");
        if (originalPolicy!=null) {
            inputRecord.setFieldValue(PolicyInquiryFields.POLICY_NO, originalPolicy.getPolicyId());
            inputRecord.setFieldValue(PolicyInquiryFields.POLICY_NO_ID, originalPolicy.getPolicyId());
            if (originalPolicy.getPolicyDetail()!=null && originalPolicy.getFirstPolicyDetail().getPolicyTypeCode()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.POLICY_TYPE, originalPolicy.getFirstPolicyDetail().getPolicyTypeCode().getValue());
            if (originalPolicy.getContractPeriod()!=null) {
                inputRecord.setFieldValue(PolicyInquiryFields.EFF_FROM_DATE, DateUtils.parseXMLDateToOasisDate(originalPolicy.getContractPeriod().getStartDate()));
                inputRecord.setFieldValue(PolicyInquiryFields.TERM_EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(originalPolicy.getContractPeriod().getStartDate()));
                inputRecord.setFieldValue(PolicyInquiryFields.EXP_DATE, DateUtils.parseXMLDateToOasisDate(originalPolicy.getContractPeriod().getEndDate()));
                inputRecord.setFieldValue(PolicyInquiryFields.TERM_EXP_DATE, DateUtils.parseXMLDateToOasisDate(originalPolicy.getContractPeriod().getEndDate()));
                inputRecord.setFieldValue(PolicyInquiryFields.TERM_EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(originalPolicy.getContractPeriod().getEndDate()));
            }
            if (originalPolicy.getTransactionDetail()!=null && originalPolicy.getTransactionDetail().getTransactionCode()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.TRANS_CODE, originalPolicy.getTransactionDetail().getTransactionCode().getValue());
        }

        if (changedPolicy.getRenewalDetail()!=null) {
            inputRecord.setFieldValue(PolicyInquiryFields.REN_INDICATOR, changedPolicy.getRenewalDetail().getRenewalIndicator());
            inputRecord.setFieldValue(PolicyInquiryFields.RENEWAL_INDICATOR_CODE, changedPolicy.getRenewalDetail().getRenewalIndicator());
            inputRecord.setFieldValue(PolicyInquiryFields.NON_REN_RSN, changedPolicy.getRenewalDetail().getNonRenewalReason());
            inputRecord.setFieldValue(PolicyInquiryFields.NON_RENEWAL_REASON_CODE, changedPolicy.getRenewalDetail().getNonRenewalReason());
            if (changedPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation()!=null) {
                inputRecord.setFieldValue(PolicyInquiryFields.POLICY_RETRO_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate()));
            }
            if (changedPolicy.getFirstPolicyDetail().getShortTermIndicator()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.SHORT_TERM_B, YesNoEmptyFlag.getInstance(changedPolicy.getFirstPolicyDetail().getShortTermIndicator()).getName());
            inputRecord.setFieldValue(PolicyInquiryFields.GUARANTEE_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getGuaranteeDate()));
            inputRecord.setFieldValue(PolicyInquiryFields.ORG_TYPE_CODE, changedPolicy.getFirstPolicyDetail().getOrganizationType());
            inputRecord.setFieldValue(PolicyInquiryFields.ORGANIZATION_TYPE_CODE, changedPolicy.getFirstPolicyDetail().getOrganizationType());
            if (changedPolicy.getFirstPolicyDetail().getBinderIndicator()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.BINDER_B, YesNoEmptyFlag.getInstance(changedPolicy.getFirstPolicyDetail().getBinderIndicator()).getName());
            inputRecord.setFieldValue(PolicyInquiryFields.PROGRAM_CODE, changedPolicy.getFirstPolicyDetail().getProgramCode());
            inputRecord.setFieldValue(PolicyInquiryFields.DECLINATION_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getDeclinationDate()));
            inputRecord.setFieldValue(PolicyInquiryFields.CATEGORY_CODE, changedPolicy.getFirstPolicyDetail().getCategoryCode());
            if (changedPolicy.getFirstPolicyDetail().getCollateralIndicator()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.COLLATERAL_B, YesNoEmptyFlag.getInstance(changedPolicy.getFirstPolicyDetail().getCollateralIndicator()).getName());
            inputRecord.setFieldValue(PolicyInquiryFields.ROLLING_IBNR_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getIbnrDate()));
        }

        if (originalPolicy != null && originalPolicy.getPolicyDetail()!=null) {
            if (originalPolicy.getFirstPolicyDetail().getPolicyStatusCode()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.STATUS_CODE, originalPolicy.getFirstPolicyDetail().getPolicyStatusCode().getValue());
            if (originalPolicy.getPolicyHolder().getReferredParty()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.POLICY_HOLDER_ENTITY_ID, originalPolicy.getPolicyHolder().getReferredParty().getPartyNumberId());
            if (originalPolicy.getFirstPolicyDetail().getPolicyCycleCode()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.POLICY_CYCLE_CODE, originalPolicy.getFirstPolicyDetail().getPolicyCycleCode().getValue());
            if (originalPolicy.getFirstPolicyDetail().getQuoteCycleCode()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.QUOTE_CYCLE_CODE, originalPolicy.getFirstPolicyDetail().getQuoteCycleCode().getValue());
            if (originalPolicy.getFirstPolicyDetail().getPolicyTypeCode()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.POLICY_TYPE_CODE, originalPolicy.getFirstPolicyDetail().getPolicyTypeCode().getValue());
            if (originalPolicy.getFirstPolicyDetail().getIssueCompany()!=null) {
                if (originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty()!=null){
                    String issueCompanyEntityId = getPolicyManager().getEntityIdByClientId(originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getPartyNumberId(),
                        originalPolicy.getFirstPolicyDetail().getIssueCompany().getReferredParty().getClientId());
                    inputRecord.setFieldValue(PolicyInquiryFields.ISSUE_COMPANY_ENTITY_FK, issueCompanyEntityId);
                    inputRecord.setFieldValue(PolicyInquiryFields.ISSUE_COMPANY_FK, issueCompanyEntityId);
                }
                if (originalPolicy.getFirstPolicyDetail().getIssueCompany().getControllingStateOrProvinceCode()!=null)
                    inputRecord.setFieldValue(PolicyInquiryFields.ISSUE_STATE_CODE, originalPolicy.getFirstPolicyDetail().getIssueCompany().getControllingStateOrProvinceCode().getValue());
                if (originalPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode()!=null)
                    inputRecord.setFieldValue(PolicyInquiryFields.PROCESS_LOCATION_CODE, originalPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode().getValue());
                if (originalPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode()!=null)
                    inputRecord.setFieldValue(PolicyInquiryFields.REGIONAL_OFFICE, originalPolicy.getFirstPolicyDetail().getIssueCompany().getProcessLocationCode().getValue());
            }
            if (originalPolicy.getFirstPolicyDetail().getPolicyFormCode()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.POLICY_FORM_CODE, originalPolicy.getFirstPolicyDetail().getPolicyFormCode().getValue());
        }

        if (changedPolicy != null && changedPolicy.getPolicyDetail()!=null) {
            if (changedPolicy.getFirstPolicyDetail().getAdditionalInformation() != null) {
                if (changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().size() < 3) {
                    for (int i = changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                        changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                inputRecord.setFieldValue(PolicyInquiryFields.CHAR1, changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(0).getValue());
                inputRecord.setFieldValue(PolicyInquiryFields.CHAR2, changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(1).getValue());
                inputRecord.setFieldValue(PolicyInquiryFields.CHAR3, changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(2).getValue());

                if (changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                    for (int i = changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                        changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                inputRecord.setFieldValue(PolicyInquiryFields.DATE1, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(0).getValue()));
                inputRecord.setFieldValue(PolicyInquiryFields.DATE2, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(1).getValue()));
                inputRecord.setFieldValue(PolicyInquiryFields.DATE3, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(2).getValue()));

                if (changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().size() < 3) {
                    for (int i = changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                        changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                inputRecord.setFieldValue(PolicyInquiryFields.NUM1, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(0).getValue()));
                inputRecord.setFieldValue(PolicyInquiryFields.NUM2, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(1).getValue()));
                inputRecord.setFieldValue(PolicyInquiryFields.NUM3, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(2).getValue()));
            }
        }

        if (changedPolicy.getPolicyDetail()!=null) {
            inputRecord.setFieldValue(PolicyInquiryFields.POLICY_LAYER_CODE, changedPolicy.getFirstPolicyDetail().getPolicyLayerCode());
            inputRecord.setFieldValue(PolicyInquiryFields.HOSPITAL_TIER, changedPolicy.getFirstPolicyDetail().getHospitalTier());
            inputRecord.setFieldValue(PolicyInquiryFields.CM_YEAR, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getClaimsMadeYear()));
            inputRecord.setFieldValue(PolicyInquiryFields.PEER_GROUP_CODE, changedPolicy.getFirstPolicyDetail().getPeerGroupCode());
            inputRecord.setFieldValue(PolicyInquiryFields.FIRST_POTENTIAL_CANC, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getFirstPotentialCancelDate()));
            inputRecord.setFieldValue(PolicyInquiryFields.SECOND_POTENTIAL_CANC, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getSecondPotentialCancelDate()));
            inputRecord.setFieldValue(PolicyInquiryFields.PL_AGGREGATE, changedPolicy.getFirstPolicyDetail().getPlAggregatCode());
            inputRecord.setFieldValue(PolicyInquiryFields.GL_AGGREGATE, changedPolicy.getFirstPolicyDetail().getGlAggregateCode());
            inputRecord.setFieldValue(PolicyInquiryFields.POLICY_PHASE_CODE, changedPolicy.getFirstPolicyDetail().getPolicyPhaseCode());
            inputRecord.setFieldValue(PolicyInquiryFields.BINDER_EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getFirstPolicyDetail().getBinderEndDate()));
            if (changedPolicy.getFirstPolicyDetail().getInsuredByCompanyIndicator()!=null)
                inputRecord.setFieldValue(PolicyInquiryFields.COMPANY_INSURED_B, YesNoEmptyFlag.getInstance(changedPolicy.getFirstPolicyDetail().getInsuredByCompanyIndicator()).getName());

            if (changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation() != null) {
                inputRecord.setFieldValue(PolicyInquiryFields.RATING_METHOD, changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getMethodCode());
                inputRecord.setFieldValue(PolicyInquiryFields.RATING_DEVIATION, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getDeviationPercent()));
                inputRecord.setFieldValue(PolicyInquiryFields.DISCOVERY_PERIOD_RATING, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getDiscoveryPeriodCode()));
            }

            if (changedPolicy.getFirstPolicyDetail().getExposureInformation() != null) {
                inputRecord.setFieldValue(PolicyInquiryFields.NUMBER_OF_PHYSICIANS, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getExposureInformation().getDoctorCount()));
                inputRecord.setFieldValue(PolicyInquiryFields.NUMBER_OF_EMPLOYEES, StringUtils.validateNumeric(changedPolicy.getFirstPolicyDetail().getExposureInformation().getEmployeeCount()));
                inputRecord.setFieldValue(PolicyInquiryFields.FORM_OF_BUSINESS, changedPolicy.getFirstPolicyDetail().getExposureInformation().getFormOfBusiness());
            }
        }

        if (originalPolicy != null && originalPolicy.getPrincipalBillingAccountInformation()!=null && originalPolicy.getPrincipalBillingAccountInformation().size()> 0)
            inputRecord.setFieldValue(PolicyInquiryFields.ACCOUNT_NO, originalPolicy.getPrincipalBillingAccountInformation().get(0).getBillingAccountId());

        if (isNewTransactionB()) {
            inputRecord.setFieldValue(PolicyInquiryFields.RECORD_MODE_CODE, PolicyInquiryFields.OFFICIAL);
        }
        else {
            if (changedPolicy.getPolicyDetail() != null &&
                changedPolicy.getFirstPolicyDetail().getPolicyVersionDetail() != null &&
                changedPolicy.getFirstPolicyDetail().getPolicyVersionDetail().getVersionModeCode() != null) {
                String recordModeCode = changedPolicy.getFirstPolicyDetail().getPolicyVersionDetail().getVersionModeCode();
                if (recordModeCode.trim().isEmpty()) {
                    recordModeCode = PolicyInquiryFields.TEMP;
                }
                inputRecord.setFieldValue(PolicyInquiryFields.RECORD_MODE_CODE, recordModeCode);
            }
            else {
                inputRecord.setFieldValue(PolicyInquiryFields.RECORD_MODE_CODE, PolicyInquiryFields.TEMP);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputPolicyToRecord", inputRecord);
        }
        return inputRecord;
    }

    private void changeExistingPolicy(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                      MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeExistingPolicy", new Object[]{policyHeader, originalPolicy, changedPolicy});
        }

        Record inputRecord = setInputPolicyToRecord(originalPolicy, changedPolicy);
        inputRecord.setFieldValue(PolicyInquiryFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
        inputRecord.setFieldValue(PolicyInquiryFields.TERM_BASE_ID, policyHeader.getTermBaseRecordId());
        inputRecord.setFieldValue(PolicyInquiryFields.TERM_BASE_RECORD_ID, policyHeader.getTermBaseRecordId());

        inputRecord.setFieldValue(PolicyInquiryFields.NEW_SAVE_OPTION, PolicyInquiryFields.WIP);
        String policyNoEdit = inputRecord.getStringValue("policyNoEdit").toUpperCase();
        inputRecord.setFieldValue("policyNoEdit",policyNoEdit);

        boolean changeB = true;
        if (inputRecord.hasStringValue("changeB") && !inputRecord.getBooleanValue("changeB").booleanValue()) {
            changeB = false;
        }

        if (changeB) {
            PolicySaveProcessor saveProcessor = (PolicySaveProcessor) ApplicationContext.getInstance().getBean(POLICY_SAVE_PROCESSOR);
            saveProcessor.savePolicy(policyHeader, inputRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changeExistingPolicy");
        }
    }

    private boolean isPolicyDataChanged(MedicalMalpracticePolicyType originalPolicy, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyDataChanged", new Object[]{originalPolicy, changedPolicy});
        }

        boolean isChanged = false;

        if (originalPolicy!=null) {
            if (originalPolicy.getRenewalDetail() != null && changedPolicy.getRenewalDetail() != null) {
                if (!isEqual(originalPolicy.getRenewalDetail().getNonRenewalReason(), changedPolicy.getRenewalDetail().getNonRenewalReason()) ||
                    !isEqual(originalPolicy.getRenewalDetail().getRenewalIndicator(), changedPolicy.getRenewalDetail().getRenewalIndicator())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            if (originalPolicy.getPolicyDetail() != null && changedPolicy.getPolicyDetail() != null) {
                if (!isEqual(originalPolicy.getFirstPolicyDetail().getPolicyPhaseCode(), changedPolicy.getFirstPolicyDetail().getPolicyPhaseCode()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getBinderEndDate(), changedPolicy.getFirstPolicyDetail().getBinderEndDate()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getShortTermIndicator(), changedPolicy.getFirstPolicyDetail().getShortTermIndicator()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getPolicyLayerCode(), changedPolicy.getFirstPolicyDetail().getPolicyLayerCode()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getGuaranteeDate(), changedPolicy.getFirstPolicyDetail().getGuaranteeDate()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getDeclinationDate(), changedPolicy.getFirstPolicyDetail().getDeclinationDate()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getIbnrDate(), changedPolicy.getFirstPolicyDetail().getIbnrDate()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getOrganizationType(), changedPolicy.getFirstPolicyDetail().getOrganizationType()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getBinderIndicator(), changedPolicy.getFirstPolicyDetail().getBinderIndicator()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getCollateralIndicator(), changedPolicy.getFirstPolicyDetail().getCollateralIndicator()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getInsuredByCompanyIndicator(), changedPolicy.getFirstPolicyDetail().getInsuredByCompanyIndicator()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getProgramCode(), changedPolicy.getFirstPolicyDetail().getProgramCode()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getCategoryCode(), changedPolicy.getFirstPolicyDetail().getCategoryCode()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getHospitalTier(), changedPolicy.getFirstPolicyDetail().getHospitalTier()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getClaimsMadeYear(), changedPolicy.getFirstPolicyDetail().getClaimsMadeYear()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getPeerGroupCode(), changedPolicy.getFirstPolicyDetail().getPeerGroupCode()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getFirstPotentialCancelDate(), changedPolicy.getFirstPolicyDetail().getFirstPotentialCancelDate()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getSecondPotentialCancelDate(), changedPolicy.getFirstPolicyDetail().getSecondPotentialCancelDate()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getPlAggregatCode(), changedPolicy.getFirstPolicyDetail().getPlAggregatCode()) ||
                    !isEqual(originalPolicy.getFirstPolicyDetail().getGlAggregateCode(), changedPolicy.getFirstPolicyDetail().getGlAggregateCode())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                    }
                    return isChanged;
                }

                if (originalPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation() != null &&
                    changedPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation() != null) {
                    if (!isEqual(originalPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate(),
                        changedPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((originalPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation() == null &&
                    changedPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation() != null) ||
                    (originalPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation() != null &&
                        changedPolicy.getFirstPolicyDetail().getClaimMadeLiabilityPolicyInformation() == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                    }
                    return isChanged;
                }

                if (originalPolicy.getFirstPolicyDetail().getAdditionalInformation() != null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation() != null) {
                    if (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber() != null &&
                        changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber() != null) {
                        if (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().size() < 3) {
                            for (int i = originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                                originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                            }
                        }
                        if (changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().size() < 3) {
                            for (int i = changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                                changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                            }
                        }

                        if (!isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(0).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(0).getValue()) ||
                            !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(1).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(1).getValue()) ||
                            !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(2).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber().get(2).getValue())) {
                            isChanged = true;
                            if (l.isLoggable(Level.FINER)) {
                                l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                            }
                            return isChanged;
                        }
                    }
                    else if ((originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber() == null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber() != null) ||
                        (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber() != null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalNumber() == null)) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                        }
                        return isChanged;
                    }
                    if (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData() != null &&
                        changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData() != null) {

                        if (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().size() < 3) {
                            for (int i = originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                                originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                            }
                        }
                        if (changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().size() < 3) {
                            for (int i = changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                                changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                            }
                        }
                        if (!isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(0).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(0).getValue()) ||
                            !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(1).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(1).getValue()) ||
                            !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(2).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData().get(2).getValue())) {
                            isChanged = true;
                            if (l.isLoggable(Level.FINER)) {
                                l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                            }
                            return isChanged;
                        }
                    }
                    else if ((originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData() == null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData() != null) ||
                        (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData() != null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalData() == null)) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                        }
                        return isChanged;
                    }
                    if (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime() != null &&
                        changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime() != null) {
                        if (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                            for (int i = originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                                originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                            }
                        }
                        if (changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                            for (int i = changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                                changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                            }
                        }
                        if (!isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(0).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(0).getValue()) ||
                            !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(1).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(1).getValue()) ||
                            !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(2).getValue(), changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime().get(2).getValue()) ) {
                            isChanged = true;
                            if (l.isLoggable(Level.FINER)) {
                                l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                            }
                            return isChanged;
                        }
                    }
                    else if ((originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime() == null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime() != null) ||
                        (originalPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime() != null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation().getAdditionalDateTime() == null)) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ( (originalPolicy.getFirstPolicyDetail().getAdditionalInformation() == null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation() != null) ||
                    (originalPolicy.getFirstPolicyDetail().getAdditionalInformation() != null && changedPolicy.getFirstPolicyDetail().getAdditionalInformation() == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                    }
                    return isChanged;
                }

                if (originalPolicy.getFirstPolicyDetail().getAdditionalRatingInformation() != null &&
                    changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation() != null) {
                    if (!isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getMethodCode(), changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getMethodCode()) ||
                        !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getDeviationPercent(), changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getDeviationPercent()) ||
                        !isEqual(originalPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getDiscoveryPeriodCode(), changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation().getDiscoveryPeriodCode())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((originalPolicy.getFirstPolicyDetail().getAdditionalRatingInformation() == null &&
                    changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation() != null) ||
                    (originalPolicy.getFirstPolicyDetail().getAdditionalRatingInformation() != null &&
                        changedPolicy.getFirstPolicyDetail().getAdditionalRatingInformation() == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                    }
                    return isChanged;
                }

                if (originalPolicy.getFirstPolicyDetail().getExposureInformation() != null &&
                    changedPolicy.getFirstPolicyDetail().getExposureInformation() != null) {
                    if (!isEqual(originalPolicy.getFirstPolicyDetail().getExposureInformation().getDoctorCount(), changedPolicy.getFirstPolicyDetail().getExposureInformation().getDoctorCount()) ||
                        !isEqual(originalPolicy.getFirstPolicyDetail().getExposureInformation().getEmployeeCount(), changedPolicy.getFirstPolicyDetail().getExposureInformation().getEmployeeCount()) ||
                        !isEqual(originalPolicy.getFirstPolicyDetail().getExposureInformation().getFormOfBusiness(), changedPolicy.getFirstPolicyDetail().getExposureInformation().getFormOfBusiness())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((originalPolicy.getFirstPolicyDetail().getExposureInformation() == null &&
                    changedPolicy.getFirstPolicyDetail().getExposureInformation() != null) ||
                    (originalPolicy.getFirstPolicyDetail().getExposureInformation() != null &&
                        changedPolicy.getFirstPolicyDetail().getExposureInformation() == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ( (originalPolicy.getPolicyDetail() == null && changedPolicy.getPolicyDetail() != null) ||
                (originalPolicy.getPolicyDetail() != null && changedPolicy.getPolicyDetail() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
                }
                return isChanged;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPolicyDataChanged", isChanged);
        }

        return isChanged;
    }

    private void performRiskChanges(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                    MedicalMalpracticePolicyType changedPolicy, MedicalMalpracticePolicyChangeRequestType policyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performRiskChanges", new Object[]{policyHeader, originalPolicy, changedPolicy, policyChangeRequest});
        }

        RecordLoadProcessor rowStyleLp = new RiskRowStyleRecordLoadprocessor();
        RecordSet dbRisks = getRiskManager().loadAllRisk(policyHeader, rowStyleLp);

        RecordSet changedRisks = getNewOrChangedRisks(policyHeader, originalPolicy, changedPolicy, policyChangeRequest, dbRisks);

        if (changedRisks.getSize()>0) {
            Record summaryRecord = policyHeader.toRecord();
            summaryRecord.setFieldValue(PolicyInquiryFields.NEW_SAVE_OPTION, PolicyInquiryFields.WIP);
            summaryRecord.setFieldValue(PolicyInquiryFields.POLICY_SCREEN_MODE, PolicyInquiryFields.WIP);
            summaryRecord.setFieldValue(PolicyInquiryFields.POLICY_VIEW_MODE, PolicyInquiryFields.WIP);
            summaryRecord.setFieldValue(PolicyInquiryFields.PROCESS, "'saveAllRisk'");
            changedRisks.setSummaryRecord(summaryRecord);

            int existingRisksCount = dbRisks.getSize();
            if (existingRisksCount>0) {
                Iterator it = changedRisks.getRecords();
                while (it.hasNext()) {
                    Record r = (Record)it.next();
                    if (r.hasField(RiskInquiryFields.CAN_RESET_PRIMARY_INDICATOR) &&
                        "N".equalsIgnoreCase(r.getStringValue(RiskInquiryFields.CAN_RESET_PRIMARY_INDICATOR))) {
                        continue;
                    }

                    String primaryIndicatorB = r.getStringValue(RiskInquiryFields.PRIMARY_RISK_B);
                    Iterator dbRisksIt = dbRisks.getRecords();
                    boolean changedRiskFound = false;
                    while (dbRisksIt.hasNext()) {
                        Record dbRec = (Record) dbRisksIt.next();
                        if (dbRec.hasField(RiskInquiryFields.RISK_NUMBER_ID) && r.hasField(RiskInquiryFields.RISK_NUMBER_ID)) {
                            String dbRiskId = dbRec.getFieldValue(RiskInquiryFields.RISK_NUMBER_ID).toString();
                            String changedRiskId = r.getFieldValue(RiskInquiryFields.RISK_NUMBER_ID).toString();
                            if (dbRiskId.equalsIgnoreCase(changedRiskId)) {
                                changedRiskFound = true;
                                r.setFieldValue(RiskInquiryFields.PRIMARY_RISK_B, dbRec.getStringValue(RiskInquiryFields.PRIMARY_RISK_B));
                            }
                        }
                    }

                    if (!changedRiskFound) {
                        if (r.hasField(RiskInquiryFields.PRIMARY_RISK_B))  {
                            if ("Y".equalsIgnoreCase(primaryIndicatorB)) {
                                r.setFieldValue(RiskInquiryFields.PRIMARY_RISK_B, "N");
                            }
                        }
                }
            }
            }

            //merge modified risk records and old risk records
            dbRisks.merge(changedRisks, "riskId");
            dbRisks.setFieldValueOnAll("riskTypeCodeLOVLABEL", "Risk Type");

            setPartyType(dbRisks, changedPolicy);

            // Save the changes
            RiskSaveProcessor saveProcessor = (RiskSaveProcessor) ApplicationContext.getInstance().getBean(RISK_SAVE_PROCESSOR);
            saveProcessor.saveAllRisk(policyHeader, dbRisks);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performRiskChanges");
        }
    }

    public void performRiskAddtlExpChanges(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy, MedicalMalpracticePolicyType changedPolicy,
                                          MedicalMalpracticePolicyChangeRequestType policyChangeRequest) {
        Logger l = LogUtils.enterLog(getClass(), "performRiskAddtlExpChanges", new Object[]{policyHeader, originalPolicy, changedPolicy, policyChangeRequest});

        List<InsuredType> changedRisks = changedPolicy.getInsured();
        List<InsuredType> originRisks = null;
        if (originalPolicy != null) {
            originRisks = originalPolicy.getInsured();
        }
        String updateInd = "";
        RecordSet riskAddtlExpDBRs = getRiskAddtlExposureManager().loadAllRiskAddtlExposureForWS(policyHeader, null);
        RecordSet riskAddtlExpDBRsForSingleRisk = new RecordSet();
        for (InsuredType insuredType : changedRisks) {
            InsuredType oriInsuredType = getOriginalInsuredType(insuredType, originRisks);
            List<InsuredAdditionalExposureType> oriInsAddtlExpTypeList = new ArrayList<>();
            if (oriInsuredType == null) {
                updateInd = UpdateIndicator.INSERTED;
            }
            else {
                oriInsAddtlExpTypeList = oriInsuredType.getInsuredAdditionalExposure();
            }
            RecordSet rs = new RecordSet();
            List<InsuredAdditionalExposureType> insAddtlExpTypeList = insuredType.getInsuredAdditionalExposure();
            if (riskAddtlExpDBRs != null && riskAddtlExpDBRs.getSize() > 0) {
                riskAddtlExpDBRsForSingleRisk = riskAddtlExpDBRs.getSubSet(new RecordFilter(RiskInquiryFields.RISK_NUMBER_ID, insuredType.getInsuredNumberId()));
            }
            boolean deleteRecordExists = false;
            TransactionCode transactionCode = policyHeader.getLastTransactionInfo().getTransactionCode();
            TransactionTypeCode transactionTypeCode = policyHeader.getLastTransactionInfo().getTransactionTypeCode();
            for (InsuredAdditionalExposureType insAddtlExpType : insAddtlExpTypeList) {
                InsuredAdditionalExposureType oriInsAddtlExpType = getInsAddtlExpTypeFromList(insAddtlExpType, oriInsAddtlExpTypeList);
                for (InsuredAdditionalExposureVersionType insAddtlExpVerType : insAddtlExpType.getInsuredAdditionalExposureVersion()) {
                    if (StringUtils.isBlank(insAddtlExpVerType.getPracticeStateOrProvinceCode())) {
                         continue;
                     }
                    boolean endDateCanBeUpdated = false;
                    if ((transactionCode.isEndorsement() || transactionCode.isOosEndorsement() || transactionTypeCode.isRenewal()) &&
                         !(StringUtils.isBlank(insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail().getParentVersionNumberId()) &&
                           RecordMode.TEMP.getName().equals(insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail().getVersionModeCode()))) {
                        endDateCanBeUpdated = true;
                    }
                    if (oriInsAddtlExpType == null) {
                        updateInd = UpdateIndicator.INSERTED;
                    }
                    else if (InsAddtlExpVerChanged(insAddtlExpVerType, oriInsAddtlExpType.getInsuredAdditionalExposureVersion(), endDateCanBeUpdated)) {
                        updateInd = UpdateIndicator.UPDATED;
                    }
                    else {
                        continue;
                    }
                    Record record = setInputRiskAddtlExpToRecord(policyHeader, insuredType, insAddtlExpVerType);
                    record.setFieldValue(RiskInquiryFields.RISK_ADDTL_EXP_BASE_RECORD_ID, insAddtlExpType.getInsuredAdditionalExposureNumberId());
                    record.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, insuredType.getInsuredNumberId());
                    record.setUpdateIndicator(updateInd);
                    if (updateInd.equalsIgnoreCase(UpdateIndicator.INSERTED)) {
                        record.setFieldValue(RiskInquiryFields.ROW_STATUS, "NEW");
                        record.setFieldValue(RiskInquiryFields.RISK_ADDTL_EXPOSURE_ID, getDbUtilityManager().getNextSequenceNo());
                        record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
                        record.setFieldValue(RiskInquiryFields.ORIG_COVERAGE_LIMIT_CODE, "");
                        record.setFieldValue(RiskInquiryFields.ORIG_EFFECTIVE_TO_DATE, "");
                        record.setFieldValue(RiskInquiryFields.ORIG_PERCENT_PRACTICE, "");
                        record.setFieldValue(RiskInquiryFields.OFFICIAL_RECORD_ID, "");
                        record.setFieldValue(PolicyInquiryFields.EFFECTIVE_FROM_DATE, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                        record.setFieldValue(PolicyInquiryFields.EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());
                    }
                    else if (updateInd.equalsIgnoreCase(UpdateIndicator.UPDATED)) {
                        if (isNewTransactionB()) {
                            if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
                                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.REQUEST);
                            }
                            else {
                                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
                            }
                        }
                        else {
                            if (insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail() != null &&
                                RecordMode.OFFICIAL.equals(insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail().getVersionModeCode())) {
                                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
                            }
                            else {
                                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
                            }
                        }
                        record.setFieldValue(RiskInquiryFields.ROW_STATUS, "MODIFIED");
                        RecordSet riskAddtlExpSingleDBRs =
                            riskAddtlExpDBRsForSingleRisk.getSubSet(new RecordFilter(RiskInquiryFields.RISK_ADDTL_EXPOSURE_ID, insAddtlExpVerType.getInsuredAdditionalExposureVersionNumberId()));
                        Record riskAddtlExpDBRecord = null;
                        if (riskAddtlExpSingleDBRs != null && riskAddtlExpSingleDBRs.getSize() > 0) {
                            riskAddtlExpDBRecord = riskAddtlExpSingleDBRs.getRecord(0);
                        }
                        record.setFieldValue(RiskInquiryFields.ORIG_COVERAGE_LIMIT_CODE, riskAddtlExpDBRecord.getStringValue(RiskInquiryFields.COVERAGE_LIMIT_CODE));
                        record.setFieldValue(RiskInquiryFields.ORIG_EFFECTIVE_TO_DATE, riskAddtlExpDBRecord.getStringValue(PolicyInquiryFields.EFFECTIVE_TO_DATE));
                        record.setFieldValue(RiskInquiryFields.ORIG_PERCENT_PRACTICE, riskAddtlExpDBRecord.getStringValue(RiskInquiryFields.PERCENT_PRACTICE));
                        record.setFieldValue(RiskInquiryFields.OFFICIAL_RECORD_ID, riskAddtlExpDBRecord.getStringValue(RiskInquiryFields.OFFICIAL_RECORD_ID));
                        riskAddtlExpDBRsForSingleRisk.removeRecord(riskAddtlExpDBRecord, false);
                    }
                    rs.addRecord(record);
                }
            }

            //get insured additional exposure records to delete.
            for (InsuredAdditionalExposureType oriInsAddtlExpType : oriInsAddtlExpTypeList) {
                InsuredAdditionalExposureType insAddtlExpType = getInsAddtlExpTypeFromList(oriInsAddtlExpType, insAddtlExpTypeList);
                if (insAddtlExpType == null) {
                    List<InsuredAdditionalExposureVersionType> insAddtlExpVerTypeList = oriInsAddtlExpType.getInsuredAdditionalExposureVersion();
                    for (InsuredAdditionalExposureVersionType insAddtlExpVerType : insAddtlExpVerTypeList) {
                        RecordSet riskAddtlExpSingleDBRs =
                            riskAddtlExpDBRsForSingleRisk.getSubSet(new RecordFilter(RiskInquiryFields.RISK_ADDTL_EXPOSURE_ID, insAddtlExpVerType.getInsuredAdditionalExposureVersionNumberId()));
                        Record riskAddtlExpDBRecord = null;
                        if (riskAddtlExpSingleDBRs != null && riskAddtlExpSingleDBRs.getSize() > 0) {
                            riskAddtlExpDBRecord = riskAddtlExpSingleDBRs.getRecord(0);
                            riskAddtlExpDBRecord.setUpdateIndicator(UpdateIndicator.DELETED);
                            deleteRecordExists = true;
                        }
                    }
                }
            }

            if (rs.getSize() > 0 || deleteRecordExists) {
                rs.addRecords(riskAddtlExpDBRsForSingleRisk);
                getRiskAddtlExposureManager().saveAllRiskAddtlExposure(policyHeader, rs);
            }
        }

        l.exiting(getClass().getName(), "performRiskAddtlExpChanges");
    }

    public Record setInputRiskAddtlExpToRecord(PolicyHeader policyHeader, InsuredType insuredType, InsuredAdditionalExposureVersionType insAddtlExpVerType) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.ALL.FINER)) {
            l.entering(getClass().getName(), "setInputRiskAddtlExpToRecord", new Object[]{policyHeader, insuredType, insAddtlExpVerType});
        }

        Record record = new Record();
        record.setFieldValue(RiskInquiryFields.RISK_ADDTL_EXPOSURE_ID, insAddtlExpVerType.getInsuredAdditionalExposureVersionNumberId());
        record.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, insuredType.getInsuredNumberId());
        record.setFieldValue(TransactionFields.TRANSACTION_LOG_ID, policyHeader.getLastTransactionId());
        record.setFieldValue(RiskInquiryFields.PRACTICE_STATE_CODE, insAddtlExpVerType.getPracticeStateOrProvinceCode());
        record.setFieldValue(RiskInquiryFields.PRODUCT_COVERAGE_CODE, insAddtlExpVerType.getProductCoverageCode());
        record.setFieldValue(RiskInquiryFields.COVERAGE_LIMIT_CODE, insAddtlExpVerType.getCoverageLimitCode());
        record.setFieldValue(RiskInquiryFields.RISK_COUNTY, insAddtlExpVerType.getPracticeCountyCode());
        record.setFieldValue(RiskInquiryFields.RISK_CLASS_CODE, insAddtlExpVerType.getInsuredClassCode());
        record.setFieldValue(RiskInquiryFields.PERCENT_PRACTICE, StringUtils.validateNumeric(insAddtlExpVerType.getPracticeValue()));
        record.setFieldValue(RiskInquiryFields.ADDRESS_Id, insAddtlExpVerType.getAddressNumberId());
        if (insAddtlExpVerType.getEffectivePeriod() != null) {
            record.setFieldValue(PolicyInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(insAddtlExpVerType.getEffectivePeriod().getStartDate()));
            record.setFieldValue(PolicyInquiryFields.EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(insAddtlExpVerType.getEffectivePeriod().getEndDate()));
        }
        if (insAddtlExpVerType.getAdditionalInformation() != null) {
            record.setFieldValue(RiskInquiryFields.CHAR1, insAddtlExpVerType.getAdditionalInformation().getAdditionalData().get(0).getValue());
            record.setFieldValue(RiskInquiryFields.CHAR2, insAddtlExpVerType.getAdditionalInformation().getAdditionalData().get(1).getValue());
            record.setFieldValue(RiskInquiryFields.CHAR3, insAddtlExpVerType.getAdditionalInformation().getAdditionalData().get(2).getValue());
            record.setFieldValue(RiskInquiryFields.DATE1, DateUtils.parseXMLDateToOasisDate(insAddtlExpVerType.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()));
            record.setFieldValue(RiskInquiryFields.DATE2, DateUtils.parseXMLDateToOasisDate(insAddtlExpVerType.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()));
            record.setFieldValue(RiskInquiryFields.DATE3, DateUtils.parseXMLDateToOasisDate(insAddtlExpVerType.getAdditionalInformation().getAdditionalDateTime().get(2).getValue()));
            record.setFieldValue(RiskInquiryFields.NUM1, StringUtils.validateNumeric(insAddtlExpVerType.getAdditionalInformation().getAdditionalNumber().get(0).getValue()));
            record.setFieldValue(RiskInquiryFields.NUM2, StringUtils.validateNumeric(insAddtlExpVerType.getAdditionalInformation().getAdditionalNumber().get(1).getValue()));
            record.setFieldValue(RiskInquiryFields.NUM3, StringUtils.validateNumeric(insAddtlExpVerType.getAdditionalInformation().getAdditionalNumber().get(2).getValue()));
        }

        l.exiting(getClass().getName(), "setInputRiskAddtlExpToRecord");
        return record;
    }

    public InsuredType getOriginalInsuredType(InsuredType insuredType, List<InsuredType> originRisks) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalInsuredType", new Object[]{insuredType, originRisks});
        }

        InsuredType oriInsuredType = null;
        if (originRisks != null) {
            for (InsuredType insType : originRisks) {
                if (insType.getInsuredNumberId().equals(insuredType.getInsuredNumberId())) {
                    oriInsuredType = insType;
                    break;
                }
            }
        }

        l.exiting(getClass().getName(), "getOriginalInsuredType");
        return oriInsuredType;
    }

    public InsuredAdditionalExposureType getInsAddtlExpTypeFromList(InsuredAdditionalExposureType insAddtlExpType, List<InsuredAdditionalExposureType> insAddtlExpTypeList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInsAddtlExpTypeFromList", new Object[]{insAddtlExpType, insAddtlExpTypeList});
        }

        InsuredAdditionalExposureType tempInsAddtlExpType = null;
        for (InsuredAdditionalExposureType insAddtlExp : insAddtlExpTypeList) {
            if (insAddtlExpType.getInsuredAdditionalExposureNumberId().equals(insAddtlExp.getInsuredAdditionalExposureNumberId())) {
                tempInsAddtlExpType = insAddtlExp;
                break;
            }
        }

        l.exiting(getClass().getName(), "InsuredAdditionalExposureType");
        return tempInsAddtlExpType;
    }

    public boolean InsAddtlExpVerChanged(InsuredAdditionalExposureVersionType insAddtlExpVerType, List<InsuredAdditionalExposureVersionType> origInsAddtlExpVerTypeList, boolean endDateCanBeUpdated) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "InsAddtlExpVerChanged",
                new Object[]{insAddtlExpVerType, origInsAddtlExpVerTypeList});
        }

        boolean isChanged = false;
        for (InsuredAdditionalExposureVersionType oriInsAddtlVerExp : origInsAddtlExpVerTypeList) {
            if (!StringUtils.isBlank(insAddtlExpVerType.getInsuredAdditionalExposureVersionNumberId()) &&
                insAddtlExpVerType.getInsuredAdditionalExposureVersionNumberId().equals(oriInsAddtlVerExp.getInsuredAdditionalExposureVersionNumberId())) {
                StringBuilder errorMsg = new StringBuilder();
                if (!isEqual(insAddtlExpVerType.getInsuredClassCode(), oriInsAddtlVerExp.getInsuredClassCode()) ||
                    !isEqual(insAddtlExpVerType.getPracticeCountyCode(), oriInsAddtlVerExp.getPracticeCountyCode()) ||
                    !isEqual(insAddtlExpVerType.getPracticeStateOrProvinceCode(), oriInsAddtlVerExp.getPracticeStateOrProvinceCode())) {
                    errorMsg.append("PracticeStateOrProvinceCode/PracticeCountyCode/InsuredClassCode, ");
                }

                if (!isEqual(insAddtlExpVerType.getProductCoverageCode(), oriInsAddtlVerExp.getProductCoverageCode())) {
                    errorMsg.append("ProductCoverageCode, ");
                }

                if (insAddtlExpVerType.getEffectivePeriod() != null && oriInsAddtlVerExp.getEffectivePeriod() != null) {
                    if (!isEqual(insAddtlExpVerType.getEffectivePeriod().getStartDate(), oriInsAddtlVerExp.getEffectivePeriod().getStartDate())) {
                        errorMsg.append("EffectivePeriod/StartDate, ");
                    }

                    if (!endDateCanBeUpdated && !isEqual(insAddtlExpVerType.getEffectivePeriod().getEndDate(), oriInsAddtlVerExp.getEffectivePeriod().getEndDate())) {
                        errorMsg.append("EffectivePeriod/EndDate, ");
                    }
                }

                if (insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail() != null && oriInsAddtlVerExp.getInsuredAdditionalExposureVersionDetail() != null) {
                    if (!isEqual(insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail().getAfterImageIndicator(), oriInsAddtlVerExp.getInsuredAdditionalExposureVersionDetail().getAfterImageIndicator())) {
                        errorMsg.append("InsuredAdditionalExposureVersionDetail/AfterImageIndicator, ");
                    }

                    if (!isEqual(insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail().getParentVersionNumberId(), oriInsAddtlVerExp.getInsuredAdditionalExposureVersionDetail().getParentVersionNumberId())) {
                        errorMsg.append("InsuredAdditionalExposureVersionDetail/ParentVersionNumberId, ");
                    }

                    if (!isEqual(insAddtlExpVerType.getInsuredAdditionalExposureVersionDetail().getVersionModeCode(), oriInsAddtlVerExp.getInsuredAdditionalExposureVersionDetail().getVersionModeCode())) {
                        errorMsg.append("InsuredAdditionalExposureVersionDetail/VersionModeCode, ");
                    }
                }

                if (errorMsg.toString().indexOf(",") >= 0) {
                    AppException ae = new AppException("ws.policy.change.existing.riskAddtlExp.version", "",
                        new String[]{errorMsg.substring(0, errorMsg.length()-2), insAddtlExpVerType.getInsuredAdditionalExposureVersionNumberId()});
                    l.throwing(getClass().getName(), "InsAddtlExpVerChanged", ae);
                    throw ae;
                }

                if (!isEqual(insAddtlExpVerType.getCoverageLimitCode(), oriInsAddtlVerExp.getCoverageLimitCode()) ||
                    !isEqual(insAddtlExpVerType.getPracticeValue(), oriInsAddtlVerExp.getPracticeValue()) ||
                    !isEqual(insAddtlExpVerType.getEffectivePeriod().getEndDate(), oriInsAddtlVerExp.getEffectivePeriod().getEndDate())) {
                    isChanged = true;
                    break;
                }

                if (insAddtlExpVerType.getAdditionalInformation() != null && oriInsAddtlVerExp.getAdditionalInformation() != null) {
                    if (!isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalData().get(0).getValue(),
                        oriInsAddtlVerExp.getAdditionalInformation().getAdditionalData().get(0).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalData().get(1).getValue(),
                         oriInsAddtlVerExp.getAdditionalInformation().getAdditionalData().get(1).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalData().get(2).getValue(),
                            oriInsAddtlVerExp.getAdditionalInformation().getAdditionalData().get(2).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalDateTime().get(0).getValue(),
                         oriInsAddtlVerExp.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalDateTime().get(1).getValue(),
                            oriInsAddtlVerExp.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalDateTime().get(2).getValue(),
                        oriInsAddtlVerExp.getAdditionalInformation().getAdditionalDateTime().get(2).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalNumber().get(0).getValue(),
                            oriInsAddtlVerExp.getAdditionalInformation().getAdditionalNumber().get(0).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalNumber().get(1).getValue(),
                            oriInsAddtlVerExp.getAdditionalInformation().getAdditionalNumber().get(1).getValue()) ||
                        !isEqual(insAddtlExpVerType.getAdditionalInformation().getAdditionalNumber().get(2).getValue(),
                            oriInsAddtlVerExp.getAdditionalInformation().getAdditionalNumber().get(2).getValue())) {
                        isChanged = true;
                        break;
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "InsAddtlExpVerChanged");
        return isChanged;
    }

    /**
     * Method that sets the default flags into the PolicyHeader
     * <p/>
     *
     * @param policyChangeRequest The policy change request
     * @param policyHeader The policy header
     */
    public void setDefaultFlags(MedicalMalpracticePolicyChangeRequestType policyChangeRequest, PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "setDefaultFlags", new Object[]{policyChangeRequest, policyHeader});

        if (policyChangeRequest.getDataModificationInformation() != null && policyChangeRequest.getDataModificationInformation().getActionCode() != null) {
            for (String actionCode : policyChangeRequest.getDataModificationInformation().getActionCode()) {
                if (RiskInquiryFields.SKIP_DEFAULT_INSURED_CREATION.equalsIgnoreCase(actionCode)) {
                    policyHeader.setSkipDefaultRisk(true);
                }
                if (CoverageInquiryFields.SKIP_DEFAULT_COVERAGE_CREATION.equalsIgnoreCase(actionCode)) {
                    policyHeader.setSkipDefaultCoverage(true);
                }
                if (CoverageInquiryFields.SKIP_DEFAULT_SUB_COVERAGE_CREATION.equalsIgnoreCase(actionCode)) {
                    policyHeader.setSkipDefaultSubCoverage(true);
                }
                if (ComponentInquiryFields.SKIP_DEFAULT_COMPONENT_CREATION.equalsIgnoreCase(actionCode)) {
                    policyHeader.setSkipDefaultComponent(true);
                }
            }
        }

        l.exiting(getClass().getName(), "setDefaultFlags");
    }

    private void setPartyType(RecordSet rs, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setPartyType", new Object[]{rs, changedPolicy});
        }

        Iterator it = rs.getRecords();
        while (it.hasNext()) {
            Record r = (Record)it.next();
            if (!r.hasField(RiskInquiryFields.ENTITY_TYPE)) {
                r.setFieldValue(RiskInquiryFields.ENTITY_TYPE, "");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setPartyType");
        }

    }

    private RecordSet getNewOrChangedRisks(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy, MedicalMalpracticePolicyType changedPolicy,
                                           MedicalMalpracticePolicyChangeRequestType policyChangeRequest, RecordSet dbRisks) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNewOrChangedRisks", new Object[]{policyHeader, originalPolicy, changedPolicy, policyChangeRequest, dbRisks});
        }

        RecordSet recordSet = new RecordSet();
        List <InsuredType> risks = changedPolicy.getInsured();

        sortChangedInsuredByPrimaryRiskFirst(changedPolicy);

        for(InsuredType insured : risks) {
            RecordSet rs = getInsuredChanges(policyHeader, insured, originalPolicy, changedPolicy, dbRisks);
            if (rs.getSize()>0) {
                recordSet.addRecords(rs);
            }
        }

        List<InsuredType> origRisks = new ArrayList<InsuredType>();
        if (originalPolicy != null) {
            origRisks = originalPolicy.getInsured();
        }
        for (InsuredType insured : origRisks) {
            RecordSet deleteRs = getDeletedInsured(policyHeader, insured, changedPolicy, dbRisks);
            if (deleteRs.getSize() > 0) {
                recordSet.addRecords(deleteRs);
            }
        }

        postProcessInsuredRecordSet(recordSet, policyChangeRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNewOrChangedRisks");
        }

        return recordSet;
    }
    
    private void postProcessInsuredRecordSet(RecordSet rs, MedicalMalpracticePolicyChangeRequestType policyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessInsuredRecordSet", new Object[]{rs});
        }

        // Logic to handle SLOT and FTE slot ids
        Iterator it = rs.getRecords();
        boolean firstSlot = true;
        while (it.hasNext()) {
            Record rec = (Record)it.next();
            if (rec.hasField(RiskInquiryFields.ADD_CODE)) {
                String addCode = rec.getStringValue(RiskInquiryFields.ADD_CODE);
                if (addCode.equals("SLOT") || addCode.equals("FTE")) {
                    Iterator slotIt = rs.getRecords();
                    String slotId = "0";
                    while (slotIt.hasNext()) {
                        Record slotRecord = (Record)slotIt.next();
                        if (slotRecord.hasField(RiskInquiryFields.SLOT_ID)) {
                            String currSlotId = slotRecord.getStringValue(RiskInquiryFields.SLOT_ID);
                            if (Long.parseLong(currSlotId) > Long.parseLong(slotId)) {
                                slotId = currSlotId;
                            }
                        }
                    }
                    if (rec.hasField(RiskInquiryFields.SLOT_ID) && firstSlot) {
                        firstSlot = false;
                        rec.setFieldValue(RiskInquiryFields.SLOT_ID, Long.toString(Long.parseLong(slotId)));
                    }
                    else {
                        rec.setFieldValue(RiskInquiryFields.SLOT_ID, Long.toString(Long.parseLong(slotId) + 1));
                    }
                }
            }
        }

        // Logic to handle the location information.
        List<PropertyType> properties = policyChangeRequest.getProperty();
        it = rs.getRecords();
        while (it.hasNext()) {
            Record r = (Record)it.next();
            Iterator propIt = properties.iterator();
            //Handle Properties defined within the input XML
            while (propIt.hasNext() && r.hasField(RiskInquiryFields.PROPERTY_REFERENCE) &&
                !StringUtils.isBlank((String)r.getFieldValue(RiskInquiryFields.PROPERTY_REFERENCE))) {
                PropertyType p = (PropertyType)propIt.next();
                if (p.getKey().equalsIgnoreCase(r.getStringValue(RiskInquiryFields.PROPERTY_REFERENCE))) {
                    r.setFieldValue(RiskInquiryFields.LOCATION, p.getPropertyNumberId());
                    r.setFieldValue(RiskInquiryFields.ENTITY_ID, "0");
                }
            }

            //Handle Properties not defined within the XML, but that already exist in the DB.
            if (r.hasField(RiskInquiryFields.PROPERTY_REFERENCE) && !"0".equalsIgnoreCase((String)r.getFieldValue(RiskInquiryFields.ENTITY_ID)) &&
                !StringUtils.isBlank((String)r.getFieldValue(RiskInquiryFields.PROPERTY_REFERENCE))) {
                r.setFieldValue(RiskInquiryFields.LOCATION, r.getFieldValue(RiskInquiryFields.PROPERTY_REFERENCE));
                r.setFieldValue(RiskInquiryFields.ENTITY_ID, "0");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessInsuredRecordSet");
        }
    }

    private RecordSet getInsuredChanges(PolicyHeader policyHeader, InsuredType insured, MedicalMalpracticePolicyType originalPolicy,
                                        MedicalMalpracticePolicyType changedPolicy, RecordSet dbRisks) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInsuredChanges", new Object[]{policyHeader, insured, originalPolicy, changedPolicy, dbRisks});
        }

        boolean isInsuredFound = false;

        RecordSet changes = new RecordSet();
        if (originalPolicy != null) {
            List <InsuredType> risks = originalPolicy.getInsured();
            for (InsuredType origInsured : risks) {
                if (!StringUtils.isBlank(insured.getInsuredNumberId()) && insured.getInsuredNumberId().equalsIgnoreCase(origInsured.getInsuredNumberId())) {
                    isInsuredFound = true;
                    validateForChangeRisk(origInsured, insured);

                    List<InsuredVersionType> insuredVersion = insured.getInsuredVersion();
                    List<InsuredVersionType> origInsuredVersion = origInsured.getInsuredVersion();

                    for (InsuredVersionType insVersion : insuredVersion) {
                        boolean versionFound = false;
                        for (InsuredVersionType origInsVersion : origInsuredVersion) {
                            if (!StringUtils.isBlank(insVersion.getInsuredVersionNumberId()) &&
                                insVersion.getInsuredVersionNumberId().equalsIgnoreCase(origInsVersion.getInsuredVersionNumberId())) {
                                if (isRiskVersionChanged(insVersion, origInsVersion)) {
                                    RecordSet subRecordSet = dbRisks.getSubSet(new RecordFilter(RiskInquiryFields.RISK_ID, insVersion.getInsuredVersionNumberId()));
                                    Record dbRiskRecord = null;
                                    if(subRecordSet != null && subRecordSet.getSize() > 0) {
                                        dbRiskRecord = subRecordSet.getRecord(0);
                                    }
                                    resetRiskPrimaryIndicator(dbRiskRecord, origInsVersion, insVersion);
                                    validateForChangeRiskVersion(policyHeader, origInsVersion, insVersion, dbRiskRecord);
                                    addChangedRiskVersionToRecordSet(policyHeader, insured, insVersion, changes);
                                }
                                versionFound = true;
                                break;
                            }
                        }
                        if (!versionFound) {
                            validateForNewRiskVersion(insured, insVersion);
                            addNewRiskVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, insured, insVersion, changes, dbRisks);
                        }
                    }
                }
                if (isInsuredFound) {
                    break;
                }
            }
        }

        if (!isInsuredFound) {
            List<InsuredVersionType> insuredVersion = insured.getInsuredVersion();
            for (InsuredVersionType insVersion : insuredVersion) {
                validateForNewRiskVersion(insured, insVersion);
                addNewRiskVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, insured, insVersion, changes, dbRisks);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInsuredChanges");
        }

        return changes;
    }

    /**
     * Find the records which are going to be deleted.
     * @param policyHeader
     * @param origInsured
     * @param changePolicy
     * @param dbRisks
     * @return
     */
    private RecordSet getDeletedInsured(PolicyHeader policyHeader,
                                        InsuredType origInsured,
                                        MedicalMalpracticePolicyType changePolicy,
                                        RecordSet dbRisks) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDeletedInsured",
                new Object[]{policyHeader, origInsured, changePolicy, dbRisks});
        }
        RecordSet rs = new RecordSet();
        List<InsuredVersionType> origInsuredVersions = origInsured.getInsuredVersion();
        List<InsuredType> changedRisks = changePolicy.getInsured();
        InsuredType changedInsured = new InsuredType();
        for (InsuredType insured : changedRisks) {
            if (StringUtils.isSame(origInsured.getInsuredNumberId(), insured.getInsuredNumberId())) {
                changedInsured = insured;
            }
        }
        for (InsuredVersionType origInsuredVersion : origInsuredVersions) {
            //If the insured version in Before image does not exist in After image, it need to be deleted.
            boolean existB = false;
            for (InsuredVersionType changedInsuredVersion : changedInsured.getInsuredVersion()) {
                if (StringUtils.isSame(origInsuredVersion.getInsuredVersionNumberId(),
                    changedInsuredVersion.getInsuredVersionNumberId())) {
                    existB = true;
                    break;
                }
            }

            //1. If the insured does not exist in DB, cannot delete.
            //2. If the insured is in OFFICIAL, cannot delete.
            if (!existB) {
                RecordSet subSet = dbRisks.getSubSet(new RecordFilter(RiskInquiryFields.RISK_ID,
                    origInsuredVersion.getInsuredVersionNumberId()));
                if (subSet == null || subSet.getSize() == 0) {
                    String errorMsg = "Cannot delete risk with InsuredVersionNumberId: " +
                        origInsuredVersion.getInsuredVersionNumberId() + ", because it does not exist in the DB";
                    AppException ae = new AppException("ws.policy.change.invalid.update", "",
                        new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getDeletedInsured", ae);
                    throw ae;
                }
                Record dbRisk = subSet.getRecord(0);
                if (!"TEMP".equalsIgnoreCase(dbRisk.getStringValue(RiskInquiryFields.RECORD_MODE_CODE))) {
                    String errorMsg = "Cannot delete a risk (InsuredVersionNumberId: " +
                        origInsuredVersion.getInsuredVersionNumberId() + ") " + "which is not in Temp status.";
                    AppException ae = new AppException("ws.policy.change.invalid.update", "",
                        new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getDeletedInsured", ae);
                    throw ae;
                }

                Record record = setInputRiskToRecord(policyHeader, origInsured, origInsuredVersion);
                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
                record.setUpdateIndicator(UpdateIndicator.DELETED);
                rs.addRecord(record);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDeletedInsured", rs);
        }
        return rs;
    }

    private void resetRiskPrimaryIndicator(Record dbRiskRecord, InsuredVersionType originalVersion, InsuredVersionType changedVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetRiskPrimaryIndicator",
                new Object[]{dbRiskRecord, originalVersion.getPrimaryIndicator(), changedVersion.getPrimaryIndicator()});
        }

        String dbPrimaryIndicator = null;
        if (dbRiskRecord != null) {
            dbPrimaryIndicator = dbRiskRecord.getStringValue("primaryRiskB");
        }
        if ("true".equalsIgnoreCase(originalVersion.getPrimaryIndicator())) {
            originalVersion.setPrimaryIndicator("Y");
        }
        else if ("false".equalsIgnoreCase(originalVersion.getPrimaryIndicator())) {
            originalVersion.setPrimaryIndicator("N");
        }

        if ("true".equalsIgnoreCase(changedVersion.getPrimaryIndicator())) {
            changedVersion.setPrimaryIndicator("Y");
        }
        else if ("false".equalsIgnoreCase(changedVersion.getPrimaryIndicator())) {
            changedVersion.setPrimaryIndicator("N");
        }

        if (dbPrimaryIndicator != null && !dbPrimaryIndicator.equalsIgnoreCase(originalVersion.getPrimaryIndicator())) {
            originalVersion.setPrimaryIndicator(dbPrimaryIndicator);
            changedVersion.setPrimaryIndicator(dbPrimaryIndicator);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resetRiskPrimaryIndicator");
        }
    }

    private void sortChangedInsuredByPrimaryRiskFirst(MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "sortChangedInsuredByPrimaryRiskFirst", new Object[]{changedPolicy});
        }

        List <InsuredType> risks = changedPolicy.getInsured();
        Collections.sort(risks, new Comparator<InsuredType>() {
            public int compare(InsuredType o1, InsuredType o2) {
                // Sort in descending order - Primary Risk first.
                return o2.getInsuredVersion().get(0).getPrimaryIndicator().compareTo(
                    o1.getInsuredVersion().get(0).getPrimaryIndicator());
            }
        });

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "sortChangedInsuredByPrimaryRiskFirst");
        }
    }

    private boolean isRiskVersionChanged(InsuredVersionType insVersion, InsuredVersionType origInsVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRiskVersionChanged", new Object[]{insVersion, origInsVersion});
        }

        boolean isChanged = false;
        if (insVersion ==null || origInsVersion==null) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (!isEqual(insVersion.getPrimaryIndicator(), origInsVersion.getPrimaryIndicator()) ||
            !isEqual(insVersion.getPracticeStateOrProvinceCode().getValue(), origInsVersion.getPracticeStateOrProvinceCode().getValue())) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (insVersion.getEffectivePeriod() != null && origInsVersion.getEffectivePeriod() != null) {
            if (!isEqual(insVersion.getEffectivePeriod().getStartDate(), origInsVersion.getEffectivePeriod().getStartDate()) ||
                !isEqual(insVersion.getEffectivePeriod().getEndDate(), origInsVersion.getEffectivePeriod().getEndDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getEffectivePeriod() == null && origInsVersion.getEffectivePeriod() != null) ||
            (insVersion.getEffectivePeriod() != null && origInsVersion.getEffectivePeriod() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (insVersion.getInsuredClassCode() != null && origInsVersion.getInsuredClassCode() != null) {
            if (!isEqual(insVersion.getInsuredClassCode().getValue(), origInsVersion.getInsuredClassCode().getValue())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getInsuredClassCode() == null && origInsVersion.getInsuredClassCode() != null) ||
            (insVersion.getInsuredClassCode() != null && origInsVersion.getInsuredClassCode() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (insVersion.getPCFInsuredClassCode() != null && origInsVersion.getPCFInsuredClassCode() != null) {
            if (!isEqual(insVersion.getPCFInsuredClassCode().getValue(), origInsVersion.getPCFInsuredClassCode().getValue())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getPCFInsuredClassCode() == null && origInsVersion.getPCFInsuredClassCode() != null) ||
            (insVersion.getPCFInsuredClassCode() != null && origInsVersion.getPCFInsuredClassCode() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (insVersion.getPracticeCountyCode() != null && origInsVersion.getPracticeCountyCode() != null) {
            if (!isEqual(insVersion.getPracticeCountyCode().getValue(), origInsVersion.getPracticeCountyCode().getValue())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getPracticeCountyCode() == null && origInsVersion.getPracticeCountyCode() != null) ||
            (insVersion.getPracticeCountyCode() != null && origInsVersion.getPracticeCountyCode() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (insVersion.getPCFPracticeCountyCode() != null && origInsVersion.getPCFPracticeCountyCode() != null) {
            if (!isEqual(insVersion.getPCFPracticeCountyCode().getValue(), origInsVersion.getPCFPracticeCountyCode().getValue())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getPCFPracticeCountyCode() == null && origInsVersion.getPCFPracticeCountyCode() != null) ||
            (insVersion.getPCFPracticeCountyCode() != null && origInsVersion.getPCFPracticeCountyCode() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (!isEqual(insVersion.getInsuredSubClassCode(), origInsVersion.getInsuredSubClassCode()) ||
            !isEqual(insVersion.getInsuredAlternateSpecialtyCode(), origInsVersion.getInsuredAlternateSpecialtyCode()) ||
            !isEqual(insVersion.getInsuredClaimsDeductibleNumberId(), origInsVersion.getInsuredClaimsDeductibleNumberId()) ||
            !isEqual(insVersion.getInsuredAlternateMethodCode(), origInsVersion.getInsuredAlternateMethodCode()) ||
            !isEqual(insVersion.getInsuredRevenueBandAmount(), origInsVersion.getInsuredRevenueBandAmount()) ||
            !isEqual(insVersion.getInsuredRatingTier(), origInsVersion.getInsuredRatingTier()) ||
            !isEqual(insVersion.getTeachingIndicator(), origInsVersion.getTeachingIndicator()) ||
            !isEqual(insVersion.getInsuredProcedureCode(), origInsVersion.getInsuredProcedureCode()) ||
            !isEqual(insVersion.getInsuredMatureIndicator(), origInsVersion.getInsuredMatureIndicator()) ||
            !isEqual(insVersion.getInsuredMoonlightingIndicator(), origInsVersion.getInsuredMoonlightingIndicator()) ||
            !isEqual(insVersion.getClaimsMadeYear(), origInsVersion.getClaimsMadeYear()) ||
            !isEqual(insVersion.getIbnrIndicator(), origInsVersion.getIbnrIndicator()) ||
            !isEqual(insVersion.getIbnrStatus(), origInsVersion.getIbnrStatus()) ||
            !isEqual(insVersion.getScorecardEligibilityIndicator(), origInsVersion.getScorecardEligibilityIndicator()) ||
            !isEqual(insVersion.getInsuredCityCode(), origInsVersion.getInsuredCityCode()) ||
            !isEqual(insVersion.getAdditionalNotes(), origInsVersion.getAdditionalNotes())) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (insVersion.getFullTimeEquivalencyInformation() != null && origInsVersion.getFullTimeEquivalencyInformation() != null) {
            if (!isEqual(insVersion.getFullTimeEquivalencyInformation().getFullTimeEquivalency(), origInsVersion.getFullTimeEquivalencyInformation().getFullTimeEquivalency()) ||
                !isEqual(insVersion.getFullTimeEquivalencyInformation().getFullTimeHours(), origInsVersion.getFullTimeEquivalencyInformation().getFullTimeHours()) ||
                !isEqual(insVersion.getFullTimeEquivalencyInformation().getPartTimeHours(), origInsVersion.getFullTimeEquivalencyInformation().getPartTimeHours()) ||
                !isEqual(insVersion.getFullTimeEquivalencyInformation().getPerDiemHours(), origInsVersion.getFullTimeEquivalencyInformation().getPerDiemHours())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getFullTimeEquivalencyInformation() == null && origInsVersion.getFullTimeEquivalencyInformation() != null) ||
            (insVersion.getFullTimeEquivalencyInformation() != null && origInsVersion.getFullTimeEquivalencyInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (insVersion.getMalpracticeLiabilityExposureInformation() != null && origInsVersion.getMalpracticeLiabilityExposureInformation() != null) {
            if (!isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getExposureUnit(), origInsVersion.getMalpracticeLiabilityExposureInformation().getExposureUnit()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getExposureBasisCode(), origInsVersion.getMalpracticeLiabilityExposureInformation().getExposureBasisCode()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getDoctorCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getDoctorCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getSquareFootage(), origInsVersion.getMalpracticeLiabilityExposureInformation().getSquareFootage()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getVapCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getVapCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getBedCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getBedCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getExtendedBedCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getExtendedBedCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getSkillBedCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getSkillBedCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getCensusCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getCensusCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getOutpatientVisitCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getOutpatientVisitCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getDeliveryCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getDeliveryCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getImpatientSurgeryCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getImpatientSurgeryCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getOutpatientSurgeryCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getOutpatientSurgeryCount()) ||
                !isEqual(insVersion.getMalpracticeLiabilityExposureInformation().getEmergencyRoomVisitCount(), origInsVersion.getMalpracticeLiabilityExposureInformation().getEmergencyRoomVisitCount())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getMalpracticeLiabilityExposureInformation() == null && origInsVersion.getMalpracticeLiabilityExposureInformation() != null) ||
            (insVersion.getMalpracticeLiabilityExposureInformation() != null && origInsVersion.getMalpracticeLiabilityExposureInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (insVersion.getBuildingInformation() != null && origInsVersion.getBuildingInformation() != null) {
            if (!isEqual(insVersion.getBuildingInformation().getBuildingClassCode(), origInsVersion.getBuildingInformation().getBuildingClassCode()) ||
                !isEqual(insVersion.getBuildingInformation().getBuildingValue(), origInsVersion.getBuildingInformation().getBuildingValue()) ||
                !isEqual(insVersion.getBuildingInformation().getBuildingTypeCode(), origInsVersion.getBuildingInformation().getBuildingTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getBuildingUseTypeCode(), origInsVersion.getBuildingInformation().getBuildingUseTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getFrameTypeCode(), origInsVersion.getBuildingInformation().getFrameTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getProtectionClassCode(), origInsVersion.getBuildingInformation().getProtectionClassCode()) ||
                !isEqual(insVersion.getBuildingInformation().getSprinklerIndicator(), origInsVersion.getBuildingInformation().getSprinklerIndicator()) ||
                !isEqual(insVersion.getBuildingInformation().getConstructionTypeCode(), origInsVersion.getBuildingInformation().getConstructionTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getRoofTypeCode(), origInsVersion.getBuildingInformation().getRoofTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getFloorTypeCode(), origInsVersion.getBuildingInformation().getFloorTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getProtectionTypeCode(), origInsVersion.getBuildingInformation().getProtectionTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getFireServiceTypeCode(), origInsVersion.getBuildingInformation().getFireServiceTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getHydrantTypeCode(), origInsVersion.getBuildingInformation().getHydrantTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getSecurityTypeCode(), origInsVersion.getBuildingInformation().getSecurityTypeCode()) ||
                !isEqual(insVersion.getBuildingInformation().getLocationCode(), origInsVersion.getBuildingInformation().getLocationCode()) ||
                !isEqual(insVersion.getBuildingInformation().getLocationDescription(), origInsVersion.getBuildingInformation().getLocationDescription())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }

            if (insVersion.getVehiclesOperatedInformation() != null && origInsVersion.getVehiclesOperatedInformation() != null) {
                if (!isEqual(insVersion.getVehiclesOperatedInformation().getFleetIndicator(), origInsVersion.getVehiclesOperatedInformation().getFleetIndicator()) ||
                    !isEqual(insVersion.getVehiclesOperatedInformation().getVehicleManufacturerCode(), origInsVersion.getVehiclesOperatedInformation().getVehicleManufacturerCode()) ||
                    !isEqual(insVersion.getVehiclesOperatedInformation().getVehicleManufacturerSubclassCode(), origInsVersion.getVehiclesOperatedInformation().getVehicleManufacturerSubclassCode()) ||
                    !isEqual(insVersion.getVehiclesOperatedInformation().getVehicleModelCode(), origInsVersion.getVehiclesOperatedInformation().getVehicleModelCode()) ||
                    !isEqual(insVersion.getVehiclesOperatedInformation().getVehicleYear(), origInsVersion.getVehiclesOperatedInformation().getVehicleYear()) ||
                    !isEqual(insVersion.getVehiclesOperatedInformation().getVehicleOriginalCost(), origInsVersion.getVehiclesOperatedInformation().getVehicleOriginalCost()) ||
                    !isEqual(insVersion.getVehiclesOperatedInformation().getVehicleVin(), origInsVersion.getVehiclesOperatedInformation().getVehicleVin())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((insVersion.getVehiclesOperatedInformation() == null && origInsVersion.getVehiclesOperatedInformation() != null) ||
                (insVersion.getVehiclesOperatedInformation() != null && origInsVersion.getVehiclesOperatedInformation() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getBuildingInformation() == null && origInsVersion.getBuildingInformation() != null) ||
            (insVersion.getBuildingInformation() != null && origInsVersion.getBuildingInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (insVersion.getAdditionalInformation() != null && origInsVersion.getAdditionalInformation() != null) {
            if (insVersion.getAdditionalInformation().getAdditionalDateTime() != null && origInsVersion.getAdditionalInformation().getAdditionalDateTime() != null) {
                if (insVersion.getAdditionalInformation().getAdditionalDateTime().size() < 4) {
                    for (int i = insVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 4; i++) {
                        insVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (origInsVersion.getAdditionalInformation().getAdditionalDateTime().size() < 4) {
                    for (int i = origInsVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 4; i++) {
                        origInsVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (!isEqual(insVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue(), origInsVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue(), origInsVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue(), origInsVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalDateTime().get(3).getValue(), origInsVersion.getAdditionalInformation().getAdditionalDateTime().get(3).getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((insVersion.getAdditionalInformation().getAdditionalDateTime() == null && origInsVersion.getAdditionalInformation().getAdditionalDateTime() != null) ||
                (insVersion.getAdditionalInformation().getAdditionalDateTime() != null && origInsVersion.getAdditionalInformation().getAdditionalDateTime() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (insVersion.getAdditionalInformation().getAdditionalNumber() != null && origInsVersion.getAdditionalInformation().getAdditionalNumber() != null) {
                if (insVersion.getAdditionalInformation().getAdditionalNumber().size() < 4) {
                    for (int i = insVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 4; i++) {
                        insVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (origInsVersion.getAdditionalInformation().getAdditionalNumber().size() < 4) {
                    for (int i = origInsVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 4; i++) {
                        origInsVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (!isEqual(insVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue(), origInsVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue(), origInsVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue(), origInsVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalNumber().get(3).getValue(), origInsVersion.getAdditionalInformation().getAdditionalNumber().get(3).getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((insVersion.getAdditionalInformation().getAdditionalNumber() == null && origInsVersion.getAdditionalInformation().getAdditionalNumber() != null) ||
                (insVersion.getAdditionalInformation().getAdditionalNumber() != null && origInsVersion.getAdditionalInformation().getAdditionalNumber() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (insVersion.getAdditionalInformation().getAdditionalData() != null && origInsVersion.getAdditionalInformation().getAdditionalData() != null) {
                if (insVersion.getAdditionalInformation().getAdditionalData().size() < 4) {
                    for (int i = insVersion.getAdditionalInformation().getAdditionalData().size(); i < 4; i++) {
                        insVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (origInsVersion.getAdditionalInformation().getAdditionalData().size() < 4) {
                    for (int i = origInsVersion.getAdditionalInformation().getAdditionalData().size(); i < 4; i++) {
                        origInsVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (!isEqual(insVersion.getAdditionalInformation().getAdditionalData().get(0).getValue(), origInsVersion.getAdditionalInformation().getAdditionalData().get(0).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalData().get(1).getValue(), origInsVersion.getAdditionalInformation().getAdditionalData().get(1).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalData().get(2).getValue(), origInsVersion.getAdditionalInformation().getAdditionalData().get(2).getValue()) ||
                    !isEqual(insVersion.getAdditionalInformation().getAdditionalData().get(3).getValue(), origInsVersion.getAdditionalInformation().getAdditionalData().get(3).getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((insVersion.getAdditionalInformation().getAdditionalData() == null && origInsVersion.getAdditionalInformation().getAdditionalData() != null) ||
                (insVersion.getAdditionalInformation().getAdditionalData() != null && origInsVersion.getAdditionalInformation().getAdditionalData() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((insVersion.getAdditionalInformation() == null && origInsVersion.getAdditionalInformation() != null) ||
            (insVersion.getAdditionalInformation() != null && origInsVersion.getAdditionalInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isRiskVersionChanged", isChanged);
        }

        return isChanged;
    }

    private void addNewRiskVersionToRecordSet(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                              MedicalMalpracticePolicyType changedPolicy, InsuredType insured,
                                              InsuredVersionType insVersion, RecordSet changes, RecordSet dbRisks) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addNewRiskVersionToRecordSet", new Object[]{policyHeader, originalPolicy,
                changedPolicy, insured, insVersion, changes, dbRisks});
        }

        Record inputRecord = setInputRiskToRecord(policyHeader, insured, insVersion);
        String firstRiskB = "Y";
        if ( (originalPolicy!= null && originalPolicy.getInsured().size()>0) || changes.getSize()>0) {
            firstRiskB = "N";
        }
        else if ( changedPolicy!=null && changedPolicy.getInsured().size()>1 &&
            ((InsuredType)changedPolicy.getInsured().get(0)).getKey() != insured.getKey()) {
            firstRiskB = "N";
        }

        inputRecord.setFieldValue(RiskInquiryFields.FIRST_RISK_B, firstRiskB);
        String addCode = getRiskManager().getAddCodeForRisk(inputRecord);
        inputRecord.setFieldValue(RiskInquiryFields.ADD_CODE, addCode);
        inputRecord.setFieldValue(RiskInquiryFields.SLOT_ID, "0");

        Record defaultDbRisk = null;
        if (dbRisks != null) {
            defaultDbRisk = handleDefaultDbRisk(policyHeader, inputRecord, dbRisks);
        }

        Record saveValuesRecord = null;
        if (defaultDbRisk != null) {
            // Reuse the already created default risk record.
            saveValuesRecord = defaultDbRisk;
            saveValuesRecord.setFieldValue(RiskInquiryFields.CAN_RESET_PRIMARY_INDICATOR, "N");
        }
        else {
            // Get the initial value for a coverage when is to be added.
            saveValuesRecord = getRiskManager().getInitialValuesForAddRisk(policyHeader, inputRecord);
        }

        String riskNumberId = saveValuesRecord.getStringValue(RiskInquiryFields.RISK_NUMBER_ID);
        String riskId = saveValuesRecord.getStringValue(RiskInquiryFields.RISK_ID);

        if (isNewPolicyB()) {
            if (changedPolicy.getContractPeriod()!=null) {
                inputRecord.setFieldValue(RiskInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getContractPeriod().getStartDate()));
                inputRecord.setFieldValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getContractPeriod().getStartDate()));
            }
        }
        else if (isNewTransactionB()) {
            if (changedPolicy.getTransactionDetail()!=null) {
                inputRecord.setFieldValue(RiskInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()));
                inputRecord.setFieldValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()));
            }
        }

        insured.setInsuredNumberId(riskNumberId);
        insVersion.setInsuredVersionNumberId(riskId);

        mergeRiskRecords(inputRecord, saveValuesRecord);

        saveValuesRecord.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);

        if (defaultDbRisk != null) {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
        }
        else {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.INSERTED);
        }

        changes.addRecord(saveValuesRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addNewRiskVersionToRecordSet");
        }
    }

    private void mergeRiskRecords(Record source, Record target) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeRiskRecords", new Object[]{source, target});
        }

        if (source.hasStringValue(RiskInquiryFields.EFFECTIVE_FROM_DATE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.EFFECTIVE_FROM_DATE)))
            target.setFieldValue(RiskInquiryFields.EFFECTIVE_FROM_DATE, source.getStringValue(RiskInquiryFields.EFFECTIVE_FROM_DATE));
        if (source.hasStringValue(RiskInquiryFields.EFFECTIVE_TO_DATE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.EFFECTIVE_TO_DATE)))
            target.setFieldValue(RiskInquiryFields.EFFECTIVE_TO_DATE, source.getStringValue(RiskInquiryFields.EFFECTIVE_TO_DATE));
        if (source.hasStringValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_FROM_DATE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_FROM_DATE)))
            target.setFieldValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_FROM_DATE, source.getStringValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_FROM_DATE));
        if (source.hasStringValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_TO_DATE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_TO_DATE)))
            target.setFieldValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_TO_DATE, source.getStringValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_TO_DATE));
        if (source.hasStringValue(RiskInquiryFields.PRACTICE_STATE_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PRACTICE_STATE_CODE)))
            target.setFieldValue(RiskInquiryFields.PRACTICE_STATE_CODE, source.getStringValue(RiskInquiryFields.PRACTICE_STATE_CODE));
        if (source.hasStringValue(RiskInquiryFields.RISK_COUNTY) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RISK_COUNTY)))
            target.setFieldValue(RiskInquiryFields.RISK_COUNTY, source.getStringValue(RiskInquiryFields.RISK_COUNTY));
        if (source.hasStringValue(RiskInquiryFields.RISK_TYPE_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RISK_TYPE_CODE)))
            target.setFieldValue(RiskInquiryFields.RISK_TYPE_CODE, source.getStringValue(RiskInquiryFields.RISK_TYPE_CODE));
        if (source.hasStringValue(RiskInquiryFields.RISK_CLASS_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RISK_CLASS_CODE)))
            target.setFieldValue(RiskInquiryFields.RISK_CLASS_CODE, source.getStringValue(RiskInquiryFields.RISK_CLASS_CODE));
        if (source.hasStringValue(RiskInquiryFields.RISK_SUB_CLASS_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RISK_SUB_CLASS_CODE)))
            target.setFieldValue(RiskInquiryFields.RISK_SUB_CLASS_CODE, source.getStringValue(RiskInquiryFields.RISK_SUB_CLASS_CODE));
        if (source.hasStringValue(RiskInquiryFields.RISK_SUB_CLASS_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RISK_SUB_CLASS_CODE)))
            target.setFieldValue(RiskInquiryFields.RISK_SUB_CLASS_CODE, source.getStringValue(RiskInquiryFields.RISK_SUB_CLASS_CODE));
        if (source.hasStringValue(RiskInquiryFields.ALTERNATE_SPECIALTY_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ALTERNATE_SPECIALTY_CODE)))
            target.setFieldValue(RiskInquiryFields.ALTERNATE_SPECIALTY_CODE, source.getStringValue(RiskInquiryFields.ALTERNATE_SPECIALTY_CODE));
        if (source.hasStringValue(RiskInquiryFields.RISK_DEDUCTIBLE_ID) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RISK_DEDUCTIBLE_ID)))
            target.setFieldValue(RiskInquiryFields.RISK_DEDUCTIBLE_ID, source.getStringValue(RiskInquiryFields.RISK_DEDUCTIBLE_ID));
        if (source.hasStringValue(RiskInquiryFields.TEACHING_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.TEACHING_B)))
            target.setFieldValue(RiskInquiryFields.TEACHING_B, source.getStringValue(RiskInquiryFields.TEACHING_B));
        if (source.hasStringValue(RiskInquiryFields.PROCEDURE_CODES) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PROCEDURE_CODES)))
            target.setFieldValue(RiskInquiryFields.PROCEDURE_CODES, source.getStringValue(RiskInquiryFields.PROCEDURE_CODES));
        if (source.hasStringValue(RiskInquiryFields.RATE_MATURE_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RATE_MATURE_B)))
            target.setFieldValue(RiskInquiryFields.RATE_MATURE_B, source.getStringValue(RiskInquiryFields.RATE_MATURE_B));
        if (source.hasStringValue(RiskInquiryFields.MOONLIGHTING_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.MOONLIGHTING_B)))
            target.setFieldValue(RiskInquiryFields.MOONLIGHTING_B, source.getStringValue(RiskInquiryFields.MOONLIGHTING_B));
        if (source.hasStringValue(RiskInquiryFields.CM_YEAR) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.CM_YEAR)))
            target.setFieldValue(RiskInquiryFields.CM_YEAR, source.getStringValue(RiskInquiryFields.CM_YEAR));
        if (source.hasStringValue(RiskInquiryFields.IBNR_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.IBNR_B)))
            target.setFieldValue(RiskInquiryFields.IBNR_B, source.getStringValue(RiskInquiryFields.IBNR_B));
        if (source.hasStringValue(RiskInquiryFields.IBNR_STATUS) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.IBNR_STATUS)))
            target.setFieldValue(RiskInquiryFields.IBNR_STATUS, source.getStringValue(RiskInquiryFields.IBNR_STATUS));
        if (source.hasStringValue(RiskInquiryFields.SCORECARD_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.SCORECARD_B)))
            target.setFieldValue(RiskInquiryFields.SCORECARD_B, source.getStringValue(RiskInquiryFields.SCORECARD_B));
        if (source.hasStringValue(RiskInquiryFields.CITY_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.CITY_CODE)))
            target.setFieldValue(RiskInquiryFields.CITY_CODE, source.getStringValue(RiskInquiryFields.CITY_CODE));
        if (source.hasStringValue(RiskInquiryFields.NOTE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NOTE)))
            target.setFieldValue(RiskInquiryFields.NOTE, source.getStringValue(RiskInquiryFields.NOTE));
        if (source.hasStringValue(RiskInquiryFields.FTE_EQUIVALENT) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FTE_EQUIVALENT)))
            target.setFieldValue(RiskInquiryFields.FTE_EQUIVALENT, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.FTE_EQUIVALENT)));
        if (source.hasStringValue(RiskInquiryFields.FTE_FULL_TIME) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FTE_FULL_TIME)))
            target.setFieldValue(RiskInquiryFields.FTE_FULL_TIME, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.FTE_FULL_TIME)));
        if (source.hasStringValue(RiskInquiryFields.FTE_PART_TIME) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FTE_PART_TIME)))
            target.setFieldValue(RiskInquiryFields.FTE_PART_TIME, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.FTE_PART_TIME)));
        if (source.hasStringValue(RiskInquiryFields.FTE_PER_DIEM) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FTE_PER_DIEM)))
            target.setFieldValue(RiskInquiryFields.FTE_PER_DIEM, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.FTE_PER_DIEM)));
        if (source.hasStringValue(RiskInquiryFields.EXPOSURE_UNIT) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.EXPOSURE_UNIT)))
            target.setFieldValue(RiskInquiryFields.EXPOSURE_UNIT, source.getStringValue(RiskInquiryFields.EXPOSURE_UNIT));
        if (source.hasStringValue(RiskInquiryFields.EXPOSURE_BASIS) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.EXPOSURE_BASIS)))
            target.setFieldValue(RiskInquiryFields.EXPOSURE_BASIS, source.getStringValue(RiskInquiryFields.EXPOSURE_BASIS));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_OF_EMPLOYED_DOCTOR) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_OF_EMPLOYED_DOCTOR)))
            target.setFieldValue(RiskInquiryFields.NUMBER_OF_EMPLOYED_DOCTOR, source.getStringValue(RiskInquiryFields.NUMBER_OF_EMPLOYED_DOCTOR));
        if (source.hasStringValue(RiskInquiryFields.SQUARE_FOOTAGE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.SQUARE_FOOTAGE)))
            target.setFieldValue(RiskInquiryFields.SQUARE_FOOTAGE, source.getStringValue(RiskInquiryFields.SQUARE_FOOTAGE));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_VAP) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_VAP)))
            target.setFieldValue(RiskInquiryFields.NUMBER_VAP, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUMBER_VAP)));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_BED) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_BED)))
            target.setFieldValue(RiskInquiryFields.NUMBER_BED, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUMBER_BED)));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_EXT_BED) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_EXT_BED)))
            target.setFieldValue(RiskInquiryFields.NUMBER_EXT_BED, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUMBER_EXT_BED)));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_SKILL_BED) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_SKILL_BED)))
            target.setFieldValue(RiskInquiryFields.NUMBER_SKILL_BED, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUMBER_SKILL_BED)));
        if (source.hasStringValue(RiskInquiryFields.AVERAGE_DAILY_CENSUS) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.AVERAGE_DAILY_CENSUS)))
            target.setFieldValue(RiskInquiryFields.AVERAGE_DAILY_CENSUS, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.AVERAGE_DAILY_CENSUS)));
        if (source.hasStringValue(RiskInquiryFields.ANNUAL_PATIENT_VISIT) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ANNUAL_PATIENT_VISIT)))
            target.setFieldValue(RiskInquiryFields.ANNUAL_PATIENT_VISIT, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.ANNUAL_PATIENT_VISIT)));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_QB_DELIVERY) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_QB_DELIVERY)))
            target.setFieldValue(RiskInquiryFields.NUMBER_QB_DELIVERY, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUMBER_QB_DELIVERY)));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_IMPATIENT_SURG) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_IMPATIENT_SURG)))
            target.setFieldValue(RiskInquiryFields.NUMBER_IMPATIENT_SURG, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUMBER_IMPATIENT_SURG)));
        if (source.hasStringValue(RiskInquiryFields.ANNUAL_OUTPATIENT_VISIT) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ANNUAL_OUTPATIENT_VISIT)))
            target.setFieldValue(RiskInquiryFields.ANNUAL_OUTPATIENT_VISIT, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.ANNUAL_OUTPATIENT_VISIT)));
        if (source.hasStringValue(RiskInquiryFields.NUMBER_ER_VISIT) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUMBER_ER_VISIT)))
            target.setFieldValue(RiskInquiryFields.NUMBER_ER_VISIT, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUMBER_ER_VISIT)));
        if (source.hasStringValue(RiskInquiryFields.BUILDING_CLASS) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.BUILDING_CLASS)))
            target.setFieldValue(RiskInquiryFields.BUILDING_CLASS, source.getStringValue(RiskInquiryFields.BUILDING_CLASS));
        if (source.hasStringValue(RiskInquiryFields.BUILDING_VALUE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.BUILDING_VALUE)))
            target.setFieldValue(RiskInquiryFields.BUILDING_VALUE, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.BUILDING_VALUE)));
        if (source.hasStringValue(RiskInquiryFields.BUILDING_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.BUILDING_TYPE)))
            target.setFieldValue(RiskInquiryFields.BUILDING_TYPE, source.getStringValue(RiskInquiryFields.BUILDING_TYPE));
        if (source.hasStringValue(RiskInquiryFields.USE_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.USE_TYPE)))
            target.setFieldValue(RiskInquiryFields.USE_TYPE, source.getStringValue(RiskInquiryFields.USE_TYPE));
        if (source.hasStringValue(RiskInquiryFields.FRAME_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FRAME_TYPE)))
            target.setFieldValue(RiskInquiryFields.FRAME_TYPE, source.getStringValue(RiskInquiryFields.FRAME_TYPE));
        if (source.hasStringValue(RiskInquiryFields.PROTECTION_CLASS) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PROTECTION_CLASS)))
            target.setFieldValue(RiskInquiryFields.PROTECTION_CLASS, source.getStringValue(RiskInquiryFields.PROTECTION_CLASS));
        if (source.hasStringValue(RiskInquiryFields.SPRINKLER_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.SPRINKLER_B)))
            target.setFieldValue(RiskInquiryFields.SPRINKLER_B, source.getStringValue(RiskInquiryFields.SPRINKLER_B));
        if (source.hasStringValue(RiskInquiryFields.SPRINKLER_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.SPRINKLER_B)))
            target.setFieldValue(RiskInquiryFields.SPRINKLER_B, source.getStringValue(RiskInquiryFields.SPRINKLER_B));
        if (source.hasStringValue(RiskInquiryFields.CONSTRUCTION_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.CONSTRUCTION_TYPE)))
            target.setFieldValue(RiskInquiryFields.CONSTRUCTION_TYPE, source.getStringValue(RiskInquiryFields.CONSTRUCTION_TYPE));
        if (source.hasStringValue(RiskInquiryFields.ROOF_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ROOF_TYPE)))
            target.setFieldValue(RiskInquiryFields.ROOF_TYPE, source.getStringValue(RiskInquiryFields.ROOF_TYPE));
        if (source.hasStringValue(RiskInquiryFields.FLOOR_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FLOOR_TYPE)))
            target.setFieldValue(RiskInquiryFields.FLOOR_TYPE, source.getStringValue(RiskInquiryFields.FLOOR_TYPE));
        if (source.hasStringValue(RiskInquiryFields.PROTECTION_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PROTECTION_TYPE)))
            target.setFieldValue(RiskInquiryFields.PROTECTION_TYPE, source.getStringValue(RiskInquiryFields.PROTECTION_TYPE));
        if (source.hasStringValue(RiskInquiryFields.FIRE_SERVICE_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FIRE_SERVICE_TYPE)))
            target.setFieldValue(RiskInquiryFields.FIRE_SERVICE_TYPE, source.getStringValue(RiskInquiryFields.FIRE_SERVICE_TYPE));
        if (source.hasStringValue(RiskInquiryFields.HYDRANTS_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.HYDRANTS_TYPE)))
            target.setFieldValue(RiskInquiryFields.HYDRANTS_TYPE, source.getStringValue(RiskInquiryFields.HYDRANTS_TYPE));
        if (source.hasStringValue(RiskInquiryFields.SECURITY_TYPE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.SECURITY_TYPE)))
            target.setFieldValue(RiskInquiryFields.SECURITY_TYPE, source.getStringValue(RiskInquiryFields.SECURITY_TYPE));
        if (source.hasStringValue(RiskInquiryFields.LOCATION) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.LOCATION)))
            target.setFieldValue(RiskInquiryFields.LOCATION, source.getStringValue(RiskInquiryFields.LOCATION));
        if (source.hasStringValue(RiskInquiryFields.LOCATION_DESCRIPTION) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.LOCATION_DESCRIPTION)))
            target.setFieldValue(RiskInquiryFields.LOCATION_DESCRIPTION, source.getStringValue(RiskInquiryFields.LOCATION_DESCRIPTION));
        if (source.hasStringValue(RiskInquiryFields.FLEET_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FLEET_B)))
            target.setFieldValue(RiskInquiryFields.FLEET_B, source.getStringValue(RiskInquiryFields.FLEET_B));
        if (source.hasStringValue(RiskInquiryFields.MAKE_OF_VEHICLE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.MAKE_OF_VEHICLE)))
            target.setFieldValue(RiskInquiryFields.MAKE_OF_VEHICLE, source.getStringValue(RiskInquiryFields.MAKE_OF_VEHICLE));
        if (source.hasStringValue(RiskInquiryFields.VEHICLE_SUBCLASS) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.VEHICLE_SUBCLASS)))
            target.setFieldValue(RiskInquiryFields.VEHICLE_SUBCLASS, source.getStringValue(RiskInquiryFields.VEHICLE_SUBCLASS));
        if (source.hasStringValue(RiskInquiryFields.MODEL_OF_VEHICLE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.MODEL_OF_VEHICLE)))
            target.setFieldValue(RiskInquiryFields.MODEL_OF_VEHICLE, source.getStringValue(RiskInquiryFields.MODEL_OF_VEHICLE));
        if (source.hasStringValue(RiskInquiryFields.YEAR_OF_VEHICLE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.YEAR_OF_VEHICLE)))
            target.setFieldValue(RiskInquiryFields.YEAR_OF_VEHICLE, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.YEAR_OF_VEHICLE)));
        if (source.hasStringValue(RiskInquiryFields.ORIGINAL_COST_NEW) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ORIGINAL_COST_NEW)))
            target.setFieldValue(RiskInquiryFields.ORIGINAL_COST_NEW, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.ORIGINAL_COST_NEW)));
        if (source.hasStringValue(RiskInquiryFields.VIN) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.VIN)))
            target.setFieldValue(RiskInquiryFields.VIN, source.getStringValue(RiskInquiryFields.VIN));
        if (source.hasStringValue(RiskInquiryFields.DATE1) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.DATE1)))
            target.setFieldValue(RiskInquiryFields.DATE1, source.getStringValue(RiskInquiryFields.DATE1));
        if (source.hasStringValue(RiskInquiryFields.DATE2) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.DATE2)))
            target.setFieldValue(RiskInquiryFields.DATE2, source.getStringValue(RiskInquiryFields.DATE2));
        if (source.hasStringValue(RiskInquiryFields.DATE3) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.DATE3)))
            target.setFieldValue(RiskInquiryFields.DATE3, source.getStringValue(RiskInquiryFields.DATE3));
        if (source.hasStringValue(RiskInquiryFields.DATE4) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.DATE4)))
            target.setFieldValue(RiskInquiryFields.DATE4, source.getStringValue(RiskInquiryFields.DATE4));
        if (source.hasStringValue(RiskInquiryFields.NUM1) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUM1)))
            target.setFieldValue(RiskInquiryFields.NUM1, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUM1)));
        if (source.hasStringValue(RiskInquiryFields.NUM2) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUM2)))
            target.setFieldValue(RiskInquiryFields.NUM2, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUM2)));
        if (source.hasStringValue(RiskInquiryFields.NUM3) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUM3)))
            target.setFieldValue(RiskInquiryFields.NUM3, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUM3)));
        if (source.hasStringValue(RiskInquiryFields.NUM4) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.NUM4)))
            target.setFieldValue(RiskInquiryFields.NUM4, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.NUM4)));
        if (source.hasStringValue(RiskInquiryFields.CHAR1) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.CHAR1)))
            target.setFieldValue(RiskInquiryFields.CHAR1, source.getStringValue(RiskInquiryFields.CHAR1));
        if (source.hasStringValue(RiskInquiryFields.CHAR2) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.CHAR2)))
            target.setFieldValue(RiskInquiryFields.CHAR2, source.getStringValue(RiskInquiryFields.CHAR2));
        if (source.hasStringValue(RiskInquiryFields.CHAR3) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.CHAR3)))
            target.setFieldValue(RiskInquiryFields.CHAR3, source.getStringValue(RiskInquiryFields.CHAR3));
        if (source.hasStringValue(RiskInquiryFields.CHAR4) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.CHAR4)))
            target.setFieldValue(RiskInquiryFields.CHAR4, source.getStringValue(RiskInquiryFields.CHAR4));
        if (source.hasStringValue(RiskInquiryFields.FIRST_RISK_B) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.FIRST_RISK_B)))
            target.setFieldValue(RiskInquiryFields.FIRST_RISK_B, source.getStringValue(RiskInquiryFields.FIRST_RISK_B));
        if (source.hasStringValue(RiskInquiryFields.ADD_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ADD_CODE)))
            target.setFieldValue(RiskInquiryFields.ADD_CODE, source.getStringValue(RiskInquiryFields.ADD_CODE));
        if (source.hasStringValue(RiskInquiryFields.PERSON_REFERENCE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PERSON_REFERENCE)))
            target.setFieldValue(RiskInquiryFields.PERSON_REFERENCE, source.getStringValue(RiskInquiryFields.PERSON_REFERENCE));
        if (source.hasStringValue(RiskInquiryFields.ORGANIZATION_REFERENCE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ORGANIZATION_REFERENCE)))
            target.setFieldValue(RiskInquiryFields.ORGANIZATION_REFERENCE, source.getStringValue(RiskInquiryFields.ORGANIZATION_REFERENCE));
        if (source.hasStringValue(RiskInquiryFields.PROPERTY_REFERENCE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PROPERTY_REFERENCE)))
            target.setFieldValue(RiskInquiryFields.PROPERTY_REFERENCE, source.getStringValue(RiskInquiryFields.PROPERTY_REFERENCE));
        if (source.hasStringValue(RiskInquiryFields.ALTERNATIVE_RATING_METHOD) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.ALTERNATIVE_RATING_METHOD)))
            target.setFieldValue(RiskInquiryFields.ALTERNATIVE_RATING_METHOD, source.getStringValue(RiskInquiryFields.ALTERNATIVE_RATING_METHOD));
        if (source.hasStringValue(RiskInquiryFields.REVENUE_BAND) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.REVENUE_BAND)))
            target.setFieldValue(RiskInquiryFields.REVENUE_BAND, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.REVENUE_BAND)));
        if (source.hasStringValue(RiskInquiryFields.RATING_TIER) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.RATING_TIER)))
            target.setFieldValue(RiskInquiryFields.RATING_TIER, StringUtils.validateNumeric(source.getStringValue(RiskInquiryFields.RATING_TIER)));
        if (source.hasStringValue(RiskInquiryFields.PCF_RISK_COUNTY) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PCF_RISK_COUNTY)))
            target.setFieldValue(RiskInquiryFields.PCF_RISK_COUNTY, source.getStringValue(RiskInquiryFields.PCF_RISK_COUNTY));
        if (source.hasStringValue(RiskInquiryFields.PCF_RISK_CLASS_CODE) && !StringUtils.isBlank(source.getStringValue(RiskInquiryFields.PCF_RISK_CLASS_CODE)))
            target.setFieldValue(RiskInquiryFields.PCF_RISK_CLASS_CODE, source.getStringValue(RiskInquiryFields.PCF_RISK_CLASS_CODE));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mergeRiskRecords");
        }
    }

    private void mergeCoverageRecords(Record source, Record target) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeCoverageRecords", new Object[]{source, target});
        }

        if (source.hasStringValue(CoverageInquiryFields.RISK_BASE_RECORD_ID) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.RISK_BASE_RECORD_ID)))
            target.setFieldValue(CoverageInquiryFields.RISK_BASE_RECORD_ID, source.getStringValue(CoverageInquiryFields.RISK_BASE_RECORD_ID));
        if (source.hasStringValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE)))
            target.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, source.getStringValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE));
        if (source.hasStringValue(CoverageInquiryFields.EFFECTIVE_TO_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.EFFECTIVE_TO_DATE)))
            target.setFieldValue(CoverageInquiryFields.EFFECTIVE_TO_DATE, source.getStringValue(CoverageInquiryFields.EFFECTIVE_TO_DATE));
        if (source.hasStringValue(CoverageInquiryFields.ORIG_EFFECTIVE_TO_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.ORIG_EFFECTIVE_TO_DATE)))
            target.setFieldValue(CoverageInquiryFields.ORIG_EFFECTIVE_TO_DATE, source.getStringValue(CoverageInquiryFields.ORIG_EFFECTIVE_TO_DATE));
        if (source.hasStringValue(CoverageInquiryFields.COVERAGE_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.COVERAGE_CODE)))
            target.setFieldValue(CoverageInquiryFields.COVERAGE_CODE, source.getStringValue(CoverageInquiryFields.COVERAGE_CODE));
        if (source.hasStringValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR)))
            target.setFieldValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR, source.getStringValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR));
        if (source.hasStringValue(CoverageInquiryFields.COVERAGE_LIMIT_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.COVERAGE_LIMIT_CODE)))
            target.setFieldValue(CoverageInquiryFields.COVERAGE_LIMIT_CODE, source.getStringValue(CoverageInquiryFields.COVERAGE_LIMIT_CODE));
        if (source.hasStringValue(CoverageInquiryFields.SHARED_LIMIT_B) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.SHARED_LIMIT_B)))
            target.setFieldValue(CoverageInquiryFields.SHARED_LIMIT_B, source.getStringValue(CoverageInquiryFields.SHARED_LIMIT_B));
        if (source.hasStringValue(CoverageInquiryFields.INCIDENT_LIMIT) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.INCIDENT_LIMIT)))
            target.setFieldValue(CoverageInquiryFields.INCIDENT_LIMIT, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.INCIDENT_LIMIT)));
        if (source.hasStringValue(CoverageInquiryFields.AGGREGATE_LIMIT) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.AGGREGATE_LIMIT)))
            target.setFieldValue(CoverageInquiryFields.AGGREGATE_LIMIT, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.AGGREGATE_LIMIT)));
        if (source.hasStringValue(CoverageInquiryFields.PRODUCT_SUBLIMIT_B) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.PRODUCT_SUBLIMIT_B)))
            target.setFieldValue(CoverageInquiryFields.PRODUCT_SUBLIMIT_B, source.getStringValue(CoverageInquiryFields.PRODUCT_SUBLIMIT_B));
        if (source.hasStringValue(CoverageInquiryFields.LIMIT_EROSION_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.LIMIT_EROSION_CODE)))
            target.setFieldValue(CoverageInquiryFields.LIMIT_EROSION_CODE, source.getStringValue(CoverageInquiryFields.LIMIT_EROSION_CODE));
        if (source.hasStringValue(CoverageInquiryFields.MANUAL_INCIDENT_LIMIT) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.MANUAL_INCIDENT_LIMIT)))
            target.setFieldValue(CoverageInquiryFields.MANUAL_INCIDENT_LIMIT, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.MANUAL_INCIDENT_LIMIT)));
        if (source.hasStringValue(CoverageInquiryFields.MANUAL_AGGREGATE_LIMIT) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.MANUAL_AGGREGATE_LIMIT)))
            target.setFieldValue(CoverageInquiryFields.MANUAL_AGGREGATE_LIMIT, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.MANUAL_AGGREGATE_LIMIT)));
        if (source.hasStringValue(CoverageInquiryFields.PER_DAY_LIMIT) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.PER_DAY_LIMIT)))
            target.setFieldValue(CoverageInquiryFields.PER_DAY_LIMIT, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.PER_DAY_LIMIT)));

        target.setFieldValue(CoverageInquiryFields.RETROACTIVE_DATE, source.getStringValue(CoverageInquiryFields.RETROACTIVE_DATE));
        if (source.hasStringValue(CoverageInquiryFields.SPLIT_RETROACTIVE_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.SPLIT_RETROACTIVE_DATE)))
            target.setFieldValue(CoverageInquiryFields.SPLIT_RETROACTIVE_DATE, source.getStringValue(CoverageInquiryFields.SPLIT_RETROACTIVE_DATE));
        if (source.hasStringValue(CoverageInquiryFields.CLAIMS_MADE_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CLAIMS_MADE_DATE)))
            target.setFieldValue(CoverageInquiryFields.CLAIMS_MADE_DATE, source.getStringValue(CoverageInquiryFields.CLAIMS_MADE_DATE));

        target.setFieldValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE, "");
        target.setFieldValue(CoverageInquiryFields.ORIG_RATE_PAYOR_DEPEND_CODE, "");
        if (source.hasStringValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE)))
            target.setFieldValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE, source.getStringValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE));
        if (source.hasStringValue(CoverageInquiryFields.CANCELLATION_METHOD_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CANCELLATION_METHOD_CODE)))
            target.setFieldValue(CoverageInquiryFields.CANCELLATION_METHOD_CODE, source.getStringValue(CoverageInquiryFields.CANCELLATION_METHOD_CODE));

        target.setFieldValue(CoverageInquiryFields.ANNUAL_BASE_RATE, "");
        if (source.hasStringValue(CoverageInquiryFields.ANNUAL_BASE_RATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.ANNUAL_BASE_RATE)))
            target.setFieldValue(CoverageInquiryFields.ANNUAL_BASE_RATE, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.ANNUAL_BASE_RATE)));
        if (source.hasStringValue(CoverageInquiryFields.DEFAULT_AMOUNT_OF_INSURANCE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.DEFAULT_AMOUNT_OF_INSURANCE)))
            target.setFieldValue(CoverageInquiryFields.DEFAULT_AMOUNT_OF_INSURANCE, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.DEFAULT_AMOUNT_OF_INSURANCE)));
        if (source.hasStringValue(CoverageInquiryFields.ADDL_AMOUNT_OF_INSURANCE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.ADDL_AMOUNT_OF_INSURANCE)))
            target.setFieldValue(CoverageInquiryFields.ADDL_AMOUNT_OF_INSURANCE, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.ADDL_AMOUNT_OF_INSURANCE)));
        if (source.hasStringValue(CoverageInquiryFields.LOSS_OF_INCOME_DAYS) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.LOSS_OF_INCOME_DAYS)))
            target.setFieldValue(CoverageInquiryFields.LOSS_OF_INCOME_DAYS, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.LOSS_OF_INCOME_DAYS)));
        if (source.hasStringValue(CoverageInquiryFields.EXPOSURE_UNIT) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.EXPOSURE_UNIT)))
            target.setFieldValue(CoverageInquiryFields.EXPOSURE_UNIT, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.EXPOSURE_UNIT)));
        if (source.hasStringValue(CoverageInquiryFields.BUILDING_RATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.BUILDING_RATE)))
            target.setFieldValue(CoverageInquiryFields.BUILDING_RATE, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.BUILDING_RATE)));
        if (source.hasStringValue(CoverageInquiryFields.USED_FOR_FORECAST_B) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.USED_FOR_FORECAST_B)))
            target.setFieldValue(CoverageInquiryFields.USED_FOR_FORECAST_B, source.getStringValue(CoverageInquiryFields.USED_FOR_FORECAST_B));
        if (source.hasStringValue(CoverageInquiryFields.DIRECT_PRIMARY_B) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.DIRECT_PRIMARY_B)))
            target.setFieldValue(CoverageInquiryFields.DIRECT_PRIMARY_B, source.getStringValue(CoverageInquiryFields.DIRECT_PRIMARY_B));
        if (source.hasStringValue(CoverageInquiryFields.SYMBOL) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.SYMBOL)))
            target.setFieldValue(CoverageInquiryFields.SYMBOL, source.getStringValue(CoverageInquiryFields.SYMBOL));
        if (source.hasStringValue(CoverageInquiryFields.CM_CONV_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CM_CONV_DATE)))
            target.setFieldValue(CoverageInquiryFields.CM_CONV_DATE, source.getStringValue(CoverageInquiryFields.CM_CONV_DATE));
        if (source.hasStringValue(CoverageInquiryFields.CM_CONV_OVERRIDE_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CM_CONV_OVERRIDE_DATE)))
            target.setFieldValue(CoverageInquiryFields.CM_CONV_OVERRIDE_DATE, source.getStringValue(CoverageInquiryFields.CM_CONV_OVERRIDE_DATE));
        if (source.hasStringValue(CoverageInquiryFields.OC_CONV_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.OC_CONV_DATE)))
            target.setFieldValue(CoverageInquiryFields.OC_CONV_DATE, source.getStringValue(CoverageInquiryFields.OC_CONV_DATE));
        if (source.hasStringValue(CoverageInquiryFields.OC_CONV_OVERRIDE_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.OC_CONV_OVERRIDE_DATE)))
            target.setFieldValue(CoverageInquiryFields.OC_CONV_OVERRIDE_DATE, source.getStringValue(CoverageInquiryFields.OC_CONV_OVERRIDE_DATE));
        if (source.hasStringValue(CoverageInquiryFields.PCF_COUNTY_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.PCF_COUNTY_CODE)))
            target.setFieldValue(CoverageInquiryFields.PCF_COUNTY_CODE, source.getStringValue(CoverageInquiryFields.PCF_COUNTY_CODE));
        if (source.hasStringValue(CoverageInquiryFields.PCF_PARTICIPATION_DATE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.PCF_PARTICIPATION_DATE)))
            target.setFieldValue(CoverageInquiryFields.PCF_PARTICIPATION_DATE, source.getStringValue(CoverageInquiryFields.PCF_PARTICIPATION_DATE));
        if (source.hasStringValue(CoverageInquiryFields.DEDUCTIBLE_COMPONENT_ID) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.DEDUCTIBLE_COMPONENT_ID)))
            target.setFieldValue(CoverageInquiryFields.DEDUCTIBLE_COMPONENT_ID, source.getStringValue(CoverageInquiryFields.DEDUCTIBLE_COMPONENT_ID));
        if (source.hasStringValue(CoverageInquiryFields.MANUAL_DED_SIR_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_CODE)))
            target.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_CODE, source.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_CODE));
        if (source.hasStringValue(CoverageInquiryFields.MANUAL_DED_SIR_INC_VALUE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_INC_VALUE)))
            target.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_INC_VALUE, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_INC_VALUE)));
        if (source.hasStringValue(CoverageInquiryFields.MANUAL_DED_SIR_AGG_VALUE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_AGG_VALUE)))
            target.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_AGG_VALUE, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.MANUAL_DED_SIR_AGG_VALUE)));
        if (source.hasStringValue(CoverageInquiryFields.INDEMNITY_TYPE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.INDEMNITY_TYPE)))
            target.setFieldValue(CoverageInquiryFields.INDEMNITY_TYPE, source.getStringValue(CoverageInquiryFields.INDEMNITY_TYPE));
        if (source.hasStringValue(CoverageInquiryFields.DATE1) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.DATE1)))
            target.setFieldValue(CoverageInquiryFields.DATE1, source.getStringValue(CoverageInquiryFields.DATE1));
        if (source.hasStringValue(CoverageInquiryFields.DATE2) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.DATE2)))
            target.setFieldValue(CoverageInquiryFields.DATE2, source.getStringValue(CoverageInquiryFields.DATE2));
        if (source.hasStringValue(CoverageInquiryFields.DATE3) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.DATE3)))
            target.setFieldValue(CoverageInquiryFields.DATE3, source.getStringValue(CoverageInquiryFields.DATE3));
        if (source.hasStringValue(CoverageInquiryFields.NUM1) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.NUM1)))
            target.setFieldValue(CoverageInquiryFields.NUM1, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.NUM1)));
        if (source.hasStringValue(CoverageInquiryFields.NUM2) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.NUM2)))
            target.setFieldValue(CoverageInquiryFields.NUM2, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.NUM2)));
        if (source.hasStringValue(CoverageInquiryFields.NUM3) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.NUM3)))
            target.setFieldValue(CoverageInquiryFields.NUM3, StringUtils.validateNumeric(source.getStringValue(CoverageInquiryFields.NUM3)));
        if (source.hasStringValue(CoverageInquiryFields.CHAR1) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CHAR1)))
            target.setFieldValue(CoverageInquiryFields.CHAR1, source.getStringValue(CoverageInquiryFields.CHAR1));
        if (source.hasStringValue(CoverageInquiryFields.CHAR2) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CHAR2)))
            target.setFieldValue(CoverageInquiryFields.CHAR2, source.getStringValue(CoverageInquiryFields.CHAR2));
        if (source.hasStringValue(CoverageInquiryFields.CHAR3) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CHAR3)))
            target.setFieldValue(CoverageInquiryFields.CHAR3, source.getStringValue(CoverageInquiryFields.CHAR3));
        if (source.hasStringValue(CoverageInquiryFields.CLAIM_PROCESS_CODE) && !StringUtils.isBlank(source.getStringValue(CoverageInquiryFields.CLAIM_PROCESS_CODE)))
            target.setFieldValue(CoverageInquiryFields.CLAIM_PROCESS_CODE, source.getStringValue(CoverageInquiryFields.CLAIM_PROCESS_CODE));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mergeCoverageRecords");
        }
    }

    private void addChangedRiskVersionToRecordSet(PolicyHeader policyHeader, InsuredType insured, InsuredVersionType insVersion, RecordSet changes) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addChangedRiskVersionToRecordSet", new Object[]{policyHeader, insured, insVersion, changes});
        }

        Record record = setInputRiskToRecord(policyHeader, insured, insVersion);
        if (isNewTransactionB()) {
            if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.REQUEST);
            }
            else {
                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
            }
        }
        else {
            if (insVersion.getInsuredVersionDetail() != null &&
                RecordMode.OFFICIAL.equals(insVersion.getInsuredVersionDetail().getVersionModeCode())) {
                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
            }
            else {
                record.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
            }
        }

        record.setUpdateIndicator(UpdateIndicator.UPDATED);
        changes.addRecord(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addChangedRiskVersionToRecordSet");
        }

    }

    private Record setInputRiskToRecord(PolicyHeader policyHeader, InsuredType insured, InsuredVersionType insVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputRiskToRecord", new Object[]{policyHeader, insured, insVersion});
        }

        Record record = new Record();

        record.setFieldValue(PolicyInquiryFields.POLICY_NO, policyHeader.getPolicyNo());
        record.setFieldValue(PolicyInquiryFields.POLICY_ID, policyHeader.getPolicyId());
        record.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, insured.getInsuredNumberId());
        record.setFieldValue(RiskInquiryFields.RISK_STATUS, insVersion.getInsuredStatusCode());
        if (insVersion.getPrimaryIndicator() != null)
            record.setFieldValue(RiskInquiryFields.PRIMARY_RISK_B, YesNoEmptyFlag.getInstance(insVersion.getPrimaryIndicator()).getName());
        else
            record.setFieldValue(RiskInquiryFields.PRIMARY_RISK_B, "N");
        record.setFieldValue(RiskInquiryFields.RISK_ID, insVersion.getInsuredVersionNumberId());
        record.setFieldValue(RiskInquiryFields.BASE_RECORD_B, "N");
        if (insVersion.getInsuredVersionDetail() != null)
            record.setFieldValue(RiskInquiryFields.OFFICIAL_RECORD_ID, insVersion.getInsuredVersionDetail().getParentVersionNumberId());
        String entityId = null;
        if (insured.getReferredParty() != null && !StringUtils.isBlank(insured.getReferredParty().getPartyNumberId()))
            entityId = insured.getReferredParty().getPartyNumberId();
        else if (!StringUtils.isBlank(insured.getPersonReference()))
            entityId = insured.getPersonReference();
        else if (!StringUtils.isBlank(insured.getOrganizationReference()))
            entityId = insured.getOrganizationReference();
        record.setFieldValue(RiskInquiryFields.ENTITY_ID, entityId);

        if (insVersion.getEffectivePeriod() != null) {
            record.setFieldValue(RiskInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(insVersion.getEffectivePeriod().getStartDate()));
            record.setFieldValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(insVersion.getEffectivePeriod().getStartDate()));
            record.setFieldValue(RiskInquiryFields.EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(insVersion.getEffectivePeriod().getEndDate()));
            record.setFieldValue(RiskInquiryFields.ORIG_RISK_EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(insVersion.getEffectivePeriod().getEndDate()));
        }
        if (insVersion.getPracticeStateOrProvinceCode() != null)
            record.setFieldValue(RiskInquiryFields.PRACTICE_STATE_CODE, insVersion.getPracticeStateOrProvinceCode().getValue());
        if (insVersion.getPracticeCountyCode() != null)
            record.setFieldValue(RiskInquiryFields.RISK_COUNTY, insVersion.getPracticeCountyCode().getValue());
        if (insVersion.getPCFPracticeCountyCode() != null)
            record.setFieldValue(RiskInquiryFields.PCF_RISK_COUNTY, insVersion.getPCFPracticeCountyCode().getValue());
        if (insVersion.getInsuredTypeCode() != null)
            record.setFieldValue(RiskInquiryFields.RISK_TYPE_CODE, insVersion.getInsuredTypeCode().getValue());
        if (insVersion.getInsuredClassCode() != null)
            record.setFieldValue(RiskInquiryFields.RISK_CLASS_CODE, insVersion.getInsuredClassCode().getValue());
        if (insVersion.getPCFInsuredClassCode() != null)
            record.setFieldValue(RiskInquiryFields.PCF_RISK_CLASS_CODE, insVersion.getPCFInsuredClassCode().getValue());
        record.setFieldValue(RiskInquiryFields.RISK_SUB_CLASS_CODE, insVersion.getInsuredSubClassCode());
        record.setFieldValue(RiskInquiryFields.ALTERNATE_SPECIALTY_CODE, insVersion.getInsuredAlternateSpecialtyCode());
        record.setFieldValue(RiskInquiryFields.ALTERNATIVE_RATING_METHOD, insVersion.getInsuredAlternateMethodCode());
        record.setFieldValue(RiskInquiryFields.REVENUE_BAND, StringUtils.validateNumeric(insVersion.getInsuredRevenueBandAmount()));
        record.setFieldValue(RiskInquiryFields.RATING_TIER, StringUtils.validateNumeric(insVersion.getInsuredRatingTier()));
        if (insVersion.getTeachingIndicator() != null)
            record.setFieldValue(RiskInquiryFields.TEACHING_B, YesNoEmptyFlag.getInstance(insVersion.getTeachingIndicator()).getName());
        record.setFieldValue(RiskInquiryFields.PROCEDURE_CODES, insVersion.getInsuredProcedureCode());
        if (insVersion.getInsuredMatureIndicator() != null)
            record.setFieldValue(RiskInquiryFields.RATE_MATURE_B, YesNoEmptyFlag.getInstance(insVersion.getInsuredMatureIndicator()).getName());
        if (insVersion.getInsuredMoonlightingIndicator() != null)
            record.setFieldValue(RiskInquiryFields.MOONLIGHTING_B, YesNoEmptyFlag.getInstance(insVersion.getInsuredMoonlightingIndicator()).getName());
        record.setFieldValue(RiskInquiryFields.CM_YEAR, StringUtils.validateNumeric(insVersion.getClaimsMadeYear()));
        if (insVersion.getIbnrIndicator() != null)
            record.setFieldValue(RiskInquiryFields.IBNR_B, YesNoEmptyFlag.getInstance(insVersion.getIbnrIndicator()).getName());
        record.setFieldValue(RiskInquiryFields.IBNR_STATUS, insVersion.getIbnrStatus());
        if (insVersion.getScorecardEligibilityIndicator() != null)
            record.setFieldValue(RiskInquiryFields.SCORECARD_B, YesNoEmptyFlag.getInstance(insVersion.getScorecardEligibilityIndicator()).getName());
        record.setFieldValue(RiskInquiryFields.CITY_CODE, insVersion.getInsuredCityCode());
        record.setFieldValue(RiskInquiryFields.NOTE, insVersion.getAdditionalNotes());

        if (insVersion.getFullTimeEquivalencyInformation() != null) {
            record.setFieldValue(RiskInquiryFields.FTE_EQUIVALENT, StringUtils.validateNumeric(insVersion.getFullTimeEquivalencyInformation().getFullTimeEquivalency()));
            record.setFieldValue(RiskInquiryFields.FTE_FULL_TIME, StringUtils.validateNumeric(insVersion.getFullTimeEquivalencyInformation().getFullTimeHours()));
            record.setFieldValue(RiskInquiryFields.FTE_PART_TIME, StringUtils.validateNumeric(insVersion.getFullTimeEquivalencyInformation().getPartTimeHours()));
            record.setFieldValue(RiskInquiryFields.FTE_PER_DIEM, StringUtils.validateNumeric(insVersion.getFullTimeEquivalencyInformation().getPerDiemHours()));
        }

        if (insVersion.getMalpracticeLiabilityExposureInformation() != null) {
            record.setFieldValue(RiskInquiryFields.EXPOSURE_UNIT, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getExposureUnit()));
            record.setFieldValue(RiskInquiryFields.EXPOSURE_BASIS, insVersion.getMalpracticeLiabilityExposureInformation().getExposureBasisCode());
            record.setFieldValue(RiskInquiryFields.NUMBER_OF_EMPLOYED_DOCTOR, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getDoctorCount()));
            record.setFieldValue(RiskInquiryFields.SQUARE_FOOTAGE, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getSquareFootage()));
            record.setFieldValue(RiskInquiryFields.NUMBER_VAP, insVersion.getMalpracticeLiabilityExposureInformation().getVapCount());
            record.setFieldValue(RiskInquiryFields.NUMBER_BED, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getBedCount()));
            record.setFieldValue(RiskInquiryFields.NUMBER_EXT_BED, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getExtendedBedCount()));
            record.setFieldValue(RiskInquiryFields.NUMBER_SKILL_BED, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getSkillBedCount()));
            record.setFieldValue(RiskInquiryFields.AVERAGE_DAILY_CENSUS, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getCensusCount()));
            record.setFieldValue(RiskInquiryFields.ANNUAL_PATIENT_VISIT, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getOutpatientVisitCount()));
            record.setFieldValue(RiskInquiryFields.NUMBER_QB_DELIVERY, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getDeliveryCount()));
            record.setFieldValue(RiskInquiryFields.NUMBER_IMPATIENT_SURG, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getImpatientSurgeryCount()));
            record.setFieldValue(RiskInquiryFields.ANNUAL_OUTPATIENT_VISIT, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getOutpatientSurgeryCount()));
            record.setFieldValue(RiskInquiryFields.NUMBER_ER_VISIT, StringUtils.validateNumeric(insVersion.getMalpracticeLiabilityExposureInformation().getEmergencyRoomVisitCount()));
        }

        if (insVersion.getBuildingInformation() != null) {
            record.setFieldValue(RiskInquiryFields.BUILDING_CLASS, insVersion.getBuildingInformation().getBuildingClassCode());
            record.setFieldValue(RiskInquiryFields.BUILDING_VALUE, insVersion.getBuildingInformation().getBuildingValue());
            record.setFieldValue(RiskInquiryFields.BUILDING_TYPE, insVersion.getBuildingInformation().getBuildingTypeCode());
            record.setFieldValue(RiskInquiryFields.USE_TYPE, insVersion.getBuildingInformation().getBuildingUseTypeCode());
            record.setFieldValue(RiskInquiryFields.FRAME_TYPE, insVersion.getBuildingInformation().getFrameTypeCode());
            record.setFieldValue(RiskInquiryFields.PROTECTION_CLASS, insVersion.getBuildingInformation().getProtectionClassCode());
            record.setFieldValue(RiskInquiryFields.SPRINKLER_B, YesNoEmptyFlag.getInstance(insVersion.getBuildingInformation().getSprinklerIndicator()).getName());
            record.setFieldValue(RiskInquiryFields.CONSTRUCTION_TYPE, insVersion.getBuildingInformation().getConstructionTypeCode());
            record.setFieldValue(RiskInquiryFields.ROOF_TYPE, insVersion.getBuildingInformation().getRoofTypeCode());
            record.setFieldValue(RiskInquiryFields.FLOOR_TYPE, insVersion.getBuildingInformation().getFloorTypeCode());
            record.setFieldValue(RiskInquiryFields.PROTECTION_TYPE, insVersion.getBuildingInformation().getProtectionTypeCode());
            record.setFieldValue(RiskInquiryFields.FIRE_SERVICE_TYPE, insVersion.getBuildingInformation().getFireServiceTypeCode());
            record.setFieldValue(RiskInquiryFields.HYDRANTS_TYPE, insVersion.getBuildingInformation().getHydrantTypeCode());
            record.setFieldValue(RiskInquiryFields.SECURITY_TYPE, insVersion.getBuildingInformation().getSecurityTypeCode());
            record.setFieldValue(RiskInquiryFields.LOCATION, insVersion.getBuildingInformation().getLocationCode());
            record.setFieldValue(RiskInquiryFields.LOCATION_DESCRIPTION, insVersion.getBuildingInformation().getLocationDescription());
        }

        if (insVersion.getVehiclesOperatedInformation() != null) {
            record.setFieldValue(RiskInquiryFields.FLEET_B, YesNoEmptyFlag.getInstance(insVersion.getVehiclesOperatedInformation().getFleetIndicator()).getName());
            record.setFieldValue(RiskInquiryFields.MAKE_OF_VEHICLE, insVersion.getVehiclesOperatedInformation().getVehicleManufacturerCode());
            record.setFieldValue(RiskInquiryFields.VEHICLE_SUBCLASS, insVersion.getVehiclesOperatedInformation().getVehicleManufacturerSubclassCode());
            record.setFieldValue(RiskInquiryFields.MODEL_OF_VEHICLE, insVersion.getVehiclesOperatedInformation().getVehicleModelCode());
            record.setFieldValue(RiskInquiryFields.YEAR_OF_VEHICLE, StringUtils.validateNumeric(insVersion.getVehiclesOperatedInformation().getVehicleYear()));
            record.setFieldValue(RiskInquiryFields.ORIGINAL_COST_NEW, StringUtils.validateNumeric(insVersion.getVehiclesOperatedInformation().getVehicleOriginalCost()));
            record.setFieldValue(RiskInquiryFields.VIN, insVersion.getVehiclesOperatedInformation().getVehicleVin());
        }

        if (insVersion.getAdditionalInformation() != null) {
            if (insVersion.getAdditionalInformation().getAdditionalDateTime().size() < 4) {
                for (int i = insVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 4; i++) {
                    insVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                }
            }
            record.setFieldValue(RiskInquiryFields.DATE1, DateUtils.parseXMLDateToOasisDate(insVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()));
            record.setFieldValue(RiskInquiryFields.DATE2, DateUtils.parseXMLDateToOasisDate(insVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()));
            record.setFieldValue(RiskInquiryFields.DATE3, DateUtils.parseXMLDateToOasisDate(insVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue()));
            record.setFieldValue(RiskInquiryFields.DATE4, DateUtils.parseXMLDateToOasisDate(insVersion.getAdditionalInformation().getAdditionalDateTime().get(3).getValue()));

            if (insVersion.getAdditionalInformation().getAdditionalNumber().size() < 4) {
                for (int i = insVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 4; i++) {
                    insVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                }
            }
            record.setFieldValue(RiskInquiryFields.NUM1, StringUtils.validateNumeric(insVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue()));
            record.setFieldValue(RiskInquiryFields.NUM2, StringUtils.validateNumeric(insVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue()));
            record.setFieldValue(RiskInquiryFields.NUM3, StringUtils.validateNumeric(insVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue()));
            record.setFieldValue(RiskInquiryFields.NUM4, StringUtils.validateNumeric(insVersion.getAdditionalInformation().getAdditionalNumber().get(3).getValue()));

            if (insVersion.getAdditionalInformation().getAdditionalData().size() < 4) {
                for (int i = insVersion.getAdditionalInformation().getAdditionalData().size(); i < 4; i++) {
                    insVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                }
            }
            record.setFieldValue(RiskInquiryFields.CHAR1, insVersion.getAdditionalInformation().getAdditionalData().get(0).getValue());
            record.setFieldValue(RiskInquiryFields.CHAR2, insVersion.getAdditionalInformation().getAdditionalData().get(1).getValue());
            record.setFieldValue(RiskInquiryFields.CHAR3, insVersion.getAdditionalInformation().getAdditionalData().get(2).getValue());
            record.setFieldValue(RiskInquiryFields.CHAR4, insVersion.getAdditionalInformation().getAdditionalData().get(3).getValue());
        }

        if (insured.getPersonReference()!=null) {
            record.setFieldValue(RiskInquiryFields.PERSON_REFERENCE, insured.getPersonReference());
        }
        if (insured.getOrganizationReference()!=null) {
            record.setFieldValue(RiskInquiryFields.ORGANIZATION_REFERENCE, insured.getOrganizationReference());
        }
        if (insured.getPropertyReference()!=null) {
            record.setFieldValue(RiskInquiryFields.PROPERTY_REFERENCE, insured.getPropertyReference());
        }

        if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode()) &&
            RecordMode.OFFICIAL.equals(insVersion.getInsuredVersionDetail().getVersionModeCode())) {
            String oosRiskId = getRiskManager().getRiskSequenceId();
            record.setFieldValue(RiskInquiryFields.RISK_ID, oosRiskId);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputRiskToRecord", record);
        }

        return record;
    }

    private void performCoverageChanges(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                        MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performCoverageChanges",
                new Object[]{policyHeader, originalPolicy, changedPolicy});
        }

        Record summaryRecord = policyHeader.toRecord();
        summaryRecord.setFieldValue(PolicyInquiryFields.NEW_SAVE_OPTION, PolicyInquiryFields.WIP);
        summaryRecord.setFieldValue(PolicyInquiryFields.POLICY_SCREEN_MODE, PolicyInquiryFields.WIP);
        summaryRecord.setFieldValue(PolicyInquiryFields.POLICY_VIEW_MODE, PolicyInquiryFields.WIP);
        summaryRecord.setFieldValue(PolicyInquiryFields.PROCESS, "'saveAllCoverage'");

        RecordSet dbCoverages = null;
        RecordSet dbSubCoverages = null;
        if (changedPolicy.getMedicalMalpracticeLineOfBusiness() != null &&
            changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage().size() > 0) {
            //we need to compare the coverages from input and DB.
            dbCoverages = getCoverageManager().loadAllCoverageForWs(policyHeader, "");

            List<MedicalMalpracticeCoverageType> requestCoverageList =
                changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            Iterator<MedicalMalpracticeCoverageType> requestCoverageIt = requestCoverageList.iterator();
            while (requestCoverageIt.hasNext()) {
                MedicalMalpracticeCoverageType coverage = requestCoverageIt.next();
                if (!StringUtils.isBlank(coverage.getParentCoverageNumberId())) {
                    //we need to compare the sub coverages from input and DB.
                    dbSubCoverages = getCoverageClassManager().loadAllCoverageClassForWs(policyHeader, "");
                    break;
                }
            }
        }

        List<String> riskKeyList = getRiskList(originalPolicy, changedPolicy);
        Iterator it = riskKeyList.iterator();
        while (it.hasNext()) {
            String insuredKey = (String)it.next();
            List<InsuredType> insureds = changedPolicy.getInsured();
            Iterator insIt = insureds.iterator();
            String insuredNumberId = null;
            while (insIt.hasNext()) {
                InsuredType insured = (InsuredType)insIt.next();
                if (insured.getKey().equalsIgnoreCase(insuredKey)) {
                    insuredNumberId = insured.getInsuredNumberId();
                    break;
                }
            }
            policyHeader = getRiskManager().loadRiskHeader(policyHeader, insuredNumberId);
            if (dbCoverages != null) {
                Iterator dbCoverageIt = dbCoverages.getRecords();
                RecordSet dbCoverageSet = new RecordSet();
                while (dbCoverageIt.hasNext()) {
                    Record dbCoverage = (Record) dbCoverageIt.next();
                    if (insuredNumberId != null &&
                        insuredNumberId.equals(dbCoverage.getStringValue(RiskInquiryFields.RISK_NUMBER_ID))) {
                        dbCoverageSet.addRecord(dbCoverage);
                    }
                }
                RecordSet changedCoverages =
                    getNewOrChangedCoverages(policyHeader, originalPolicy, changedPolicy, insuredKey, dbCoverageSet);
                changedCoverages.setSummaryRecord(summaryRecord);
                CoverageSaveProcessor saveProcessor = null;
                if (changedCoverages.getSize() > 0) {
                    saveProcessor = (CoverageSaveProcessor) ApplicationContext.getInstance().getBean(COVERAGE_SAVE_PROCESSOR);
                    saveProcessor.saveAllCoverageForWs(policyHeader, changedCoverages);
                }

                RecordSet changedComponents =
                    getNewOrChangedComponents(policyHeader, originalPolicy, changedPolicy, insuredKey, insuredNumberId);
                changedComponents.setSummaryRecord(summaryRecord);

                if (changedComponents.getSize() > 0) {
                    if (saveProcessor == null) {
                        saveProcessor = (CoverageSaveProcessor) ApplicationContext.getInstance().getBean(COVERAGE_SAVE_PROCESSOR);
                    }
                    saveProcessor.saveAllComponentForWs(policyHeader, changedCoverages, changedComponents);
                }
            }

            RecordSet changedSubCoverages = getNewOrChangedSubCoverages(policyHeader, originalPolicy, changedPolicy, insuredKey, dbSubCoverages);
            changedSubCoverages.setSummaryRecord(summaryRecord);

            if (changedSubCoverages.getSize() > 0) {
                CoverageClassSaveProcessor saveClassProcessor = (CoverageClassSaveProcessor) ApplicationContext.getInstance().getBean(COVERAGE_CLASS_SAVE_PROCESSOR);
                saveClassProcessor.saveAllCoverageClass(policyHeader, changedSubCoverages);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performCoverageChanges");
        }
    }

    private List<String> getRiskList(MedicalMalpracticePolicyType originalPolicy, MedicalMalpracticePolicyType changedPolicy) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskList", new Object[]{originalPolicy, changedPolicy});
        }

        List<String> riskList = new ArrayList<String>();
        Iterator it;

        if (originalPolicy!=null) {
            it = originalPolicy.getInsured().iterator();
            while (it.hasNext()) {
                String origRiskId = ((InsuredType)it.next()).getKey();
                boolean hasRisk = false;
                Iterator itRisk = riskList.iterator();
                while (itRisk.hasNext()) {
                    String riskId = itRisk.next().toString();
                    if (!StringUtils.isBlank(riskId) && riskId.equalsIgnoreCase(origRiskId)) {
                        hasRisk = true;
                        break;
                    }
                }
                if (!hasRisk) {
                    riskList.add(origRiskId);
                }
            }
        }

        it = changedPolicy.getInsured().iterator();
        while (it.hasNext()) {
            String origRiskId = ((InsuredType)it.next()).getKey();
            boolean hasRisk = false;
            Iterator itRisk = riskList.iterator();
            while (itRisk.hasNext()) {
                String riskId = itRisk.next().toString();
                if (!StringUtils.isBlank(riskId) && riskId.equalsIgnoreCase(origRiskId)) {
                    hasRisk = true;
                    break;
                }
            }
            if (!hasRisk) {
                riskList.add(origRiskId);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskList", riskList);
        }

        return riskList;
    }

    private RecordSet getNewOrChangedCoverages(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                               MedicalMalpracticePolicyType changedPolicy, String riskId, RecordSet dbCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNewOrChangedCoverages", new Object[]{policyHeader, originalPolicy, changedPolicy, riskId, dbCoverages});
        }

        RecordSet recordSet = new RecordSet();

        if (changedPolicy.getMedicalMalpracticeLineOfBusiness() != null) {
            List <MedicalMalpracticeCoverageType> coverages = changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            Iterator it = coverages.iterator();
            while (it.hasNext()) {
                MedicalMalpracticeCoverageType coverage = (MedicalMalpracticeCoverageType)it.next();
                String parentCoverageId = coverage.getParentCoverageNumberId();
                if (!StringUtils.isBlank(parentCoverageId))
                    continue;

                if (coverage.getReferredInsured()==null || coverage.getReferredInsured().getInsuredReference()==null) {
                    String errorMsg = "ReferredInsured or InsuredReference ";
                    AppException ae = new AppException("ws.policy.change.new.coverage.version", "", new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getNewOrChangedCoverages", ae);
                    throw ae;
                }

                if (coverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(riskId)) {
                    RecordSet rs = getCoverageChanges(policyHeader, coverage, originalPolicy, changedPolicy, dbCoverages);
                    if (rs.getSize() > 0) {
                        recordSet.addRecords(rs);
                    }
                }
            }
        }

        //Find the records that need to be deleted.
        if (originalPolicy != null && originalPolicy.getMedicalMalpracticeLineOfBusiness() != null) {
            List<MedicalMalpracticeCoverageType> origCoverages =
                originalPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            for (MedicalMalpracticeCoverageType origCoverage : origCoverages) {
                if (!StringUtils.isBlank(origCoverage.getParentCoverageNumberId()))
                    continue;
                if (origCoverage.getReferredInsured() == null || origCoverage.getReferredInsured().getInsuredReference() == null) {
                    String errorMsg = "ReferredInsured or InsuredReference ";
                    AppException ae = new AppException("ws.policy.change.new.coverage.version", "", new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getNewOrChangedCoverages", ae);
                    throw ae;
                }
                RecordSet deletedRs = null;
                if (riskId.equalsIgnoreCase(origCoverage.getReferredInsured().getInsuredReference())) {
                    deletedRs = getDeletedCoverages(policyHeader, origCoverage, changedPolicy,
                        originalPolicy, dbCoverages, false);
                }
                if (deletedRs != null && deletedRs.getSize() > 0) {
                    recordSet.addRecords(deletedRs);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNewOrChangedCoverages", recordSet);
        }

        return recordSet;
    }

    /**
     * Get the records that need to be deleted.
     * @param policyHeader
     * @param origCoverage
     * @param changedPolicy
     * @param originalPolicy
     * @param dbCoverages
     * @param processCoverageClassB
     * @return
     */
    RecordSet getDeletedCoverages(PolicyHeader policyHeader,
                                  MedicalMalpracticeCoverageType origCoverage,
                                  MedicalMalpracticePolicyType changedPolicy,
                                  MedicalMalpracticePolicyType originalPolicy,
                                  RecordSet dbCoverages,
                                  Boolean processCoverageClassB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDeletedCoverages",
                new Object[]{policyHeader, origCoverage, originalPolicy, changedPolicy, dbCoverages, processCoverageClassB});
        }

        RecordSet rs = new RecordSet();
        if (changedPolicy != null && changedPolicy.getMedicalMalpracticeLineOfBusiness() != null) {
            List<MedicalMalpracticeCoverageType> changedCoverages =
                changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            MedicalMalpracticeCoverageType changedCoverage = new MedicalMalpracticeCoverageType();
            //Find the same coverage in After image.
            for (MedicalMalpracticeCoverageType coverage : changedCoverages) {
                if (StringUtils.isSame(origCoverage.getCoverageNumberId(),
                    coverage.getCoverageNumberId())) {
                    changedCoverage = coverage;
                }
            }

            for (MedicalMalpracticeCoverageVersionType origCoverageVersion :
                origCoverage.getMedicalMalpracticeCoverageVersion()) {
                boolean existedB = false;
                //Find which coverage version that need to be deleted.
                for (MedicalMalpracticeCoverageVersionType changedCovgVersion :
                    changedCoverage.getMedicalMalpracticeCoverageVersion()) {
                    if (StringUtils.isSame(origCoverageVersion.getMedicalMalpracticeCoverageVersionId(),
                        changedCovgVersion.getMedicalMalpracticeCoverageVersionId())) {
                        existedB = true;
                        break;
                    }
                }
                if (!existedB) {
                    //1. If the coverage that need to be deleted does not exist in DB, then break process.
                    String filterField = CoverageInquiryFields.COVERAGE_ID;
                    if (processCoverageClassB) {
                        filterField = CoverageInquiryFields.COVERAGE_CLASS_ID;
                    }
                    RecordSet subSet = dbCoverages.getSubSet(new RecordFilter(filterField,
                        origCoverageVersion.getMedicalMalpracticeCoverageVersionId()));
                    if (subSet == null || subSet.getSize() == 0) {
                        String errorMsg = "Cannot delete coverage/sub coverage with MedicalMalpracticeCoverageVersionId: " +
                            origCoverageVersion.getMedicalMalpracticeCoverageVersionId() + ", because it doesn't exist in the DB.";
                        AppException ae = new AppException("ws.policy.change.invalid.update", "",
                            new String[]{errorMsg});
                        l.throwing(getClass().getName(), "getDeletedCoverages", ae);
                        throw ae;
                    }

                    //2. Only can delete TEMP record.
                    Record covgDBRec = subSet.getRecord(0);
                    if (!"TEMP".equalsIgnoreCase(covgDBRec.getStringValue(CoverageInquiryFields.RECORD_MODE_CODE))) {
                        String errorMsg = "Cannot delete a coverage/sub coverage (MedicalMalpracticeCoverageVersionId: " +
                            origCoverageVersion.getMedicalMalpracticeCoverageVersionId() + ") " +
                            "which is not in Temp status.";
                        AppException ae = new AppException("ws.policy.change.invalid.update", "",
                            new String[]{errorMsg});
                        l.throwing(getClass().getName(), "getDeletedCoverages", ae);
                        throw ae;
                    }
                    Record coverageRecord =
                        setInputCoverageToRecord(policyHeader, originalPolicy, origCoverage, origCoverageVersion, covgDBRec);
                    coverageRecord.setUpdateIndicator(UpdateIndicator.DELETED);
                    coverageRecord.setFieldValue(CoverageInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
                    rs.addRecord(coverageRecord);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDeletedCoverages", rs);
        }
        return rs;
    }
    
    private RecordSet getCoverageChanges(PolicyHeader policyHeader, MedicalMalpracticeCoverageType coverage, MedicalMalpracticePolicyType originalPolicy,
                                         MedicalMalpracticePolicyType changedPolicy, RecordSet dbCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCoverageChanges", new Object[]{policyHeader, coverage, originalPolicy, changedPolicy, dbCoverages});
        }

        boolean isCoverageFound = false;

        RecordSet changes = new RecordSet();

        if (originalPolicy != null && originalPolicy.getMedicalMalpracticeLineOfBusiness() != null) {
            List <MedicalMalpracticeCoverageType> origCoverages = originalPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            Iterator it = origCoverages.iterator();
            while (it.hasNext()) {
                MedicalMalpracticeCoverageType origCoverage = (MedicalMalpracticeCoverageType)it.next();
                if (!StringUtils.isBlank(coverage.getKey()) && coverage.getKey().equalsIgnoreCase(origCoverage.getKey())) {
                    isCoverageFound = true;
                    validateForChangeCoverage(origCoverage, coverage);

                    List<MedicalMalpracticeCoverageVersionType> coverageVersion = coverage.getMedicalMalpracticeCoverageVersion();
                    List<MedicalMalpracticeCoverageVersionType> origCoverageVersion = origCoverage.getMedicalMalpracticeCoverageVersion();
                    Iterator covIt = coverageVersion.iterator();
                    Iterator origCovIt = origCoverageVersion.iterator();

                    while (covIt.hasNext()) {
                        boolean versionFound = false;
                        MedicalMalpracticeCoverageVersionType covVersion = (MedicalMalpracticeCoverageVersionType)covIt.next();
                        while (origCovIt.hasNext()) {
                            MedicalMalpracticeCoverageVersionType origCovVersion = (MedicalMalpracticeCoverageVersionType)origCovIt.next();
                            if (!StringUtils.isBlank(covVersion.getMedicalMalpracticeCoverageVersionId()) &&
                                covVersion.getMedicalMalpracticeCoverageVersionId().equalsIgnoreCase(origCovVersion.getMedicalMalpracticeCoverageVersionId())) {
                                if (isCoverageVersionChanged(covVersion, origCovVersion)) {
                                    RecordSet subRecordSet = dbCoverages.getSubSet(new RecordFilter(CoverageFields.COVERAGE_ID, covVersion.getMedicalMalpracticeCoverageVersionId()));
                                    Record dbCoverage = null;
                                    if (subRecordSet != null && subRecordSet.getSize() > 0) {
                                        dbCoverage = subRecordSet.getRecord(0);
                                    }
                                    validateForChangeCoverageVersion(policyHeader, covVersion, origCovVersion, dbCoverage);
                                    resetCoveragePrimaryIndicator(dbCoverage, origCovVersion, covVersion);
                                    addChangedCoverageVersionToRecordSet(policyHeader, originalPolicy, coverage, covVersion, changes, dbCoverage);
                                }
                                versionFound = true;
                                break;
                            }
                        }
                        if (!versionFound) {
                            validateForNewCoverageVersion(coverage, covVersion);
                            addNewCoverageVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, coverage, covVersion, changes, dbCoverages);
                        }
                    }
                }
                if (isCoverageFound) {
                    break;
                }
            }
        }

        if (!isCoverageFound) {
            List<MedicalMalpracticeCoverageVersionType> coverageVersion = coverage.getMedicalMalpracticeCoverageVersion();
            Iterator covIt = coverageVersion.iterator();
            while (covIt.hasNext()) {
                MedicalMalpracticeCoverageVersionType covVersion = (MedicalMalpracticeCoverageVersionType)covIt.next();
                validateForNewCoverageVersion(coverage, covVersion);
                addNewCoverageVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, coverage, covVersion, changes, dbCoverages);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCoverageChanges");
        }

        return changes;
    }

    private void resetCoveragePrimaryIndicator(Record dbCoverage, MedicalMalpracticeCoverageVersionType originalCoverageVersion,
                                               MedicalMalpracticeCoverageVersionType coverageVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetCoveragePrimaryIndicator", new Object[]{dbCoverage,
                originalCoverageVersion.getPrimaryIndicator(),
                coverageVersion.getPrimaryIndicator()});
        }

        String primaryIndicator = "N";
        if (dbCoverage != null && dbCoverage.hasField("primaryCoverageB")) {
            primaryIndicator = YesNoEmptyFlag.getInstance(dbCoverage.getStringValue("primaryCoverageB")).getName();
            if (!YesNoEmptyFlag.getInstance(dbCoverage.getStringValue("primaryCoverageB")).equals(originalCoverageVersion.getPrimaryIndicator())) {
                originalCoverageVersion.setPrimaryIndicator(primaryIndicator);
                coverageVersion.setPrimaryIndicator(primaryIndicator);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resetCoveragePrimaryIndicator");
        }
    }
    
    private boolean isCoverageVersionChanged(MedicalMalpracticeCoverageVersionType covVersion, MedicalMalpracticeCoverageVersionType origCovVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCoverageVersionChanged", new Object[]{covVersion, origCovVersion});
        }

        boolean isChanged = false;
        if (covVersion==null || origCovVersion==null) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (!isEqual(covVersion.getPrimaryIndicator(), origCovVersion.getPrimaryIndicator()) ||
            !isEqual(covVersion.getEffectivePeriod().getStartDate(), origCovVersion.getEffectivePeriod().getStartDate()) ||
            !isEqual(covVersion.getEffectivePeriod().getEndDate(), origCovVersion.getEffectivePeriod().getEndDate())) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (!isEqual(covVersion.getClaimProcessCode(), origCovVersion.getClaimProcessCode())) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (covVersion.getLimit() != null && origCovVersion.getLimit() != null) {
            if (covVersion.getLimit().getLimitTypeCode() != null && origCovVersion.getLimit().getLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getLimitTypeCode().getValue(), origCovVersion.getLimit().getLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getLimitTypeCode() == null && origCovVersion.getLimit().getLimitTypeCode() != null) ||
                (covVersion.getLimit().getLimitTypeCode() != null && origCovVersion.getLimit().getLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getIncidentLimitTypeCode() != null && origCovVersion.getLimit().getIncidentLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getIncidentLimitTypeCode().getValue(), origCovVersion.getLimit().getIncidentLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getIncidentLimitTypeCode() == null && origCovVersion.getLimit().getIncidentLimitTypeCode() != null) ||
                (covVersion.getLimit().getIncidentLimitTypeCode() != null && origCovVersion.getLimit().getIncidentLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getAgregateLimitTypeCode() != null && origCovVersion.getLimit().getAgregateLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getAgregateLimitTypeCode().getValue(), origCovVersion.getLimit().getAgregateLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getAgregateLimitTypeCode() == null && origCovVersion.getLimit().getAgregateLimitTypeCode() != null) ||
                (covVersion.getLimit().getAgregateLimitTypeCode() != null && origCovVersion.getLimit().getAgregateLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getIncidentLimitTypeCode() != null && origCovVersion.getLimit().getIncidentLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getIncidentLimitTypeCode().getValue(), origCovVersion.getLimit().getIncidentLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getIncidentLimitTypeCode() == null && origCovVersion.getLimit().getIncidentLimitTypeCode() != null) ||
                (covVersion.getLimit().getIncidentLimitTypeCode() != null && origCovVersion.getLimit().getIncidentLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getManualIncidentLimitTypeCode() != null && origCovVersion.getLimit().getManualIncidentLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getManualIncidentLimitTypeCode().getValue(), origCovVersion.getLimit().getManualIncidentLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getManualIncidentLimitTypeCode() == null && origCovVersion.getLimit().getManualIncidentLimitTypeCode() != null) ||
                (covVersion.getLimit().getManualIncidentLimitTypeCode() != null && origCovVersion.getLimit().getManualIncidentLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getManualAggregateLimitTypeCode() != null && origCovVersion.getLimit().getManualAggregateLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getManualAggregateLimitTypeCode().getValue(), origCovVersion.getLimit().getManualAggregateLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getManualAggregateLimitTypeCode() == null && origCovVersion.getLimit().getManualAggregateLimitTypeCode() != null) ||
                (covVersion.getLimit().getManualAggregateLimitTypeCode() != null && origCovVersion.getLimit().getManualAggregateLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (!isEqual(covVersion.getLimit().getSharedLimitIndicator(), origCovVersion.getLimit().getSharedLimitIndicator()) ||
                !isEqual(covVersion.getLimit().getSubLimitIndicator(), origCovVersion.getLimit().getSubLimitIndicator()) ||
                !isEqual(covVersion.getLimit().getErosionTypeCode(), origCovVersion.getLimit().getErosionTypeCode()) ||
                !isEqual(covVersion.getLimit().getDailyLimitAmount(), origCovVersion.getLimit().getDailyLimitAmount())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }

        }
        else if ((covVersion.getLimit() == null && origCovVersion.getLimit() != null) ||
            (covVersion.getLimit() != null && origCovVersion.getLimit() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (covVersion.getClaimMadeLiabilityPolicyInformation() != null && origCovVersion.getClaimMadeLiabilityPolicyInformation() != null) {
            if (!isEqual(covVersion.getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate(), origCovVersion.getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate()) ||
                !isEqual(covVersion.getClaimMadeLiabilityPolicyInformation().getSplitRetroactiveDate(), origCovVersion.getClaimMadeLiabilityPolicyInformation().getSplitRetroactiveDate()) ||
                !isEqual(covVersion.getClaimMadeLiabilityPolicyInformation().getClaimsMadeDate(), origCovVersion.getClaimMadeLiabilityPolicyInformation().getClaimsMadeDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getClaimMadeLiabilityPolicyInformation() == null && origCovVersion.getClaimMadeLiabilityPolicyInformation() != null) ||
            (covVersion.getClaimMadeLiabilityPolicyInformation() != null && origCovVersion.getClaimMadeLiabilityPolicyInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (!isEqual(covVersion.getPayorCode(), origCovVersion.getPayorCode()) ||
            !isEqual(covVersion.getCancellationMethodCode(), origCovVersion.getCancellationMethodCode()) ||
            !isEqual(covVersion.getAnnualBaseRate(), origCovVersion.getAnnualBaseRate()) ||
            !isEqual(covVersion.getDefaultAmountOfInsurance(), origCovVersion.getDefaultAmountOfInsurance()) ||
            !isEqual(covVersion.getAdditionalAmountOfInsurance(), origCovVersion.getAdditionalAmountOfInsurance()) ||
            !isEqual(covVersion.getLossOfIncomeDays(), origCovVersion.getLossOfIncomeDays()) ||
            !isEqual(covVersion.getExposureUnit(), origCovVersion.getExposureUnit()) ||
            !isEqual(covVersion.getBuildingRate(), origCovVersion.getBuildingRate()) ||
            !isEqual(covVersion.getForecastIndicator(), origCovVersion.getForecastIndicator()) ||
            !isEqual(covVersion.getDirectPrimaryIndicator(), origCovVersion.getDirectPrimaryIndicator()) ||
            !isEqual(covVersion.getAdditionalSymbolCode(), origCovVersion.getAdditionalSymbolCode())) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (covVersion.getCoverageConversionInformation() != null && origCovVersion.getCoverageConversionInformation() != null) {
            if (!isEqual(covVersion.getCoverageConversionInformation().getClaimsMadeDate(), origCovVersion.getCoverageConversionInformation().getClaimsMadeDate()) ||
                !isEqual(covVersion.getCoverageConversionInformation().getClaimsMadeOverrideDate(), origCovVersion.getCoverageConversionInformation().getClaimsMadeOverrideDate()) ||
                !isEqual(covVersion.getCoverageConversionInformation().getOccurenceDate(), origCovVersion.getCoverageConversionInformation().getOccurenceDate()) ||
                !isEqual(covVersion.getCoverageConversionInformation().getOccurenceOverrideDate(), origCovVersion.getCoverageConversionInformation().getOccurenceOverrideDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getCoverageConversionInformation() == null && origCovVersion.getCoverageConversionInformation() != null) ||
            (covVersion.getCoverageConversionInformation() != null && origCovVersion.getCoverageConversionInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (covVersion.getPcf() != null && origCovVersion.getPcf() != null) {
            if (!isEqual(covVersion.getPcf().getPracticeCountyCode(), origCovVersion.getPcf().getPracticeCountyCode()) ||
                !isEqual(covVersion.getPcf().getStartDate(), origCovVersion.getPcf().getStartDate()) ||
                !isEqual(covVersion.getDeductible(), origCovVersion.getDeductible()) ||
                !isEqual(covVersion.getManualDeductibleSIRCode(), origCovVersion.getManualDeductibleSIRCode()) ||
                !isEqual(covVersion.getManualDeductibleSIRIncidentAmount(), origCovVersion.getManualDeductibleSIRIncidentAmount()) ||
                !isEqual(covVersion.getManualDeductibleSIRAggregateAmount(), origCovVersion.getManualDeductibleSIRAggregateAmount()) ||
                !isEqual(covVersion.getDeductibleSIRIndemnityTypeCode(), origCovVersion.getDeductibleSIRIndemnityTypeCode())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getPcf() == null && origCovVersion.getPcf() != null) ||
            (covVersion.getPcf() != null && origCovVersion.getPcf() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (covVersion.getAdditionalInformation() != null && origCovVersion.getAdditionalInformation() != null) {
            if (covVersion.getAdditionalInformation().getAdditionalDateTime() != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime() != null) {
                if (covVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                    for (int i = covVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                        covVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (origCovVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                    for (int i = origCovVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                        origCovVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (!isEqual(covVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue(), origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()) ||
                    !isEqual(covVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue(), origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()) ||
                    !isEqual(covVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue(), origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getAdditionalInformation().getAdditionalDateTime() == null && origCovVersion.getAdditionalInformation().getAdditionalDateTime() != null) ||
                (covVersion.getAdditionalInformation().getAdditionalDateTime() != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getAdditionalInformation().getAdditionalNumber() != null && origCovVersion.getAdditionalInformation().getAdditionalNumber() != null) {
                if (covVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                    for (int i = covVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                        covVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (origCovVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                    for (int i = origCovVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                        origCovVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (!isEqual(covVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue(), origCovVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue()) ||
                    !isEqual(covVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue(), origCovVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue()) ||
                    !isEqual(covVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue(), origCovVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getAdditionalInformation().getAdditionalNumber() == null && origCovVersion.getAdditionalInformation().getAdditionalNumber() != null) ||
                (covVersion.getAdditionalInformation().getAdditionalNumber() != null && origCovVersion.getAdditionalInformation().getAdditionalNumber() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getAdditionalInformation().getAdditionalData() != null && origCovVersion.getAdditionalInformation().getAdditionalData() != null) {
                if (covVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                    for (int i = covVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                        covVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (origCovVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                    for (int i = origCovVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                        origCovVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (!isEqual(covVersion.getAdditionalInformation().getAdditionalData().get(0).getValue(), origCovVersion.getAdditionalInformation().getAdditionalData().get(0).getValue()) ||
                    !isEqual(covVersion.getAdditionalInformation().getAdditionalData().get(1).getValue(), origCovVersion.getAdditionalInformation().getAdditionalData().get(1).getValue()) ||
                    !isEqual(covVersion.getAdditionalInformation().getAdditionalData().get(2).getValue(), origCovVersion.getAdditionalInformation().getAdditionalData().get(2).getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getAdditionalInformation().getAdditionalData() == null && origCovVersion.getAdditionalInformation().getAdditionalData() != null) ||
                (covVersion.getAdditionalInformation().getAdditionalData() != null && origCovVersion.getAdditionalInformation().getAdditionalData() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getAdditionalInformation() == null && origCovVersion.getAdditionalInformation() != null) ||
            (covVersion.getAdditionalInformation() != null && origCovVersion.getAdditionalInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCoverageVersionChanged", isChanged);
        }

        return isChanged;
    }

    private boolean isSubCoverageVersionChanged(MedicalMalpracticeCoverageVersionType covVersion, MedicalMalpracticeCoverageVersionType origCovVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSubCoverageVersionChanged", new Object[]{covVersion, origCovVersion});
        }

        boolean isChanged = false;
        if (covVersion==null || origCovVersion==null) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (!isEqual(covVersion.getPrimaryIndicator(), origCovVersion.getPrimaryIndicator()) ||
            !isEqual(covVersion.getPayorCode(), origCovVersion.getPayorCode()) ||
            !isEqual(covVersion.getCancellationMethodCode(), origCovVersion.getCancellationMethodCode()) ||
            !isEqual(covVersion.getAnnualBaseRate(), origCovVersion.getAnnualBaseRate()) ||
            !isEqual(covVersion.getDefaultAmountOfInsurance(), origCovVersion.getDefaultAmountOfInsurance()) ||
            !isEqual(covVersion.getAdditionalAmountOfInsurance(), origCovVersion.getAdditionalAmountOfInsurance()) ||
            !isEqual(covVersion.getLossOfIncomeDays(), origCovVersion.getLossOfIncomeDays()) ||
            !isEqual(covVersion.getExposureUnit(), origCovVersion.getExposureUnit()) ||
            !isEqual(covVersion.getBuildingRate(), origCovVersion.getBuildingRate()) ||
            !isEqual(covVersion.getForecastIndicator(), origCovVersion.getForecastIndicator()) ||
            !isEqual(covVersion.getDirectPrimaryIndicator(), origCovVersion.getDirectPrimaryIndicator()) ||
            !isEqual(covVersion.getAdditionalSymbolCode(), origCovVersion.getAdditionalSymbolCode()) ||
            !isEqual(covVersion.getLimit().getSharedLimitIndicator(), origCovVersion.getLimit().getSharedLimitIndicator()) ||
            !isEqual(covVersion.getLimit().getSubLimitIndicator(), origCovVersion.getLimit().getSubLimitIndicator()) ||
            !isEqual(covVersion.getLimit().getErosionTypeCode(), origCovVersion.getLimit().getErosionTypeCode()) ||
            !isEqual(covVersion.getLimit().getDailyLimitAmount(), origCovVersion.getLimit().getDailyLimitAmount()) ||
            !isEqual(covVersion.getDeductible(), origCovVersion.getDeductible())) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (covVersion.getEffectivePeriod() != null && origCovVersion.getEffectivePeriod() != null) {
            if (!isEqual(covVersion.getEffectivePeriod().getStartDate(), origCovVersion.getEffectivePeriod().getStartDate()) ||
                !isEqual(covVersion.getEffectivePeriod().getEndDate(), origCovVersion.getEffectivePeriod().getEndDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ( (covVersion.getEffectivePeriod() == null && origCovVersion.getEffectivePeriod() != null) ||
            (covVersion.getEffectivePeriod() != null && origCovVersion.getEffectivePeriod() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (covVersion.getLimit() != null && origCovVersion.getLimit() != null) {
            if (covVersion.getLimit().getLimitTypeCode() != null && origCovVersion.getLimit().getLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getLimitTypeCode().getValue(), origCovVersion.getLimit().getLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getLimitTypeCode() == null && origCovVersion.getLimit().getLimitTypeCode() != null) ||
                (covVersion.getLimit().getLimitTypeCode() != null && origCovVersion.getLimit().getLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getIncidentLimitTypeCode() != null && origCovVersion.getLimit().getIncidentLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getIncidentLimitTypeCode().getValue(), origCovVersion.getLimit().getIncidentLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getIncidentLimitTypeCode() == null && origCovVersion.getLimit().getIncidentLimitTypeCode() != null) ||
                (covVersion.getLimit().getIncidentLimitTypeCode() != null && origCovVersion.getLimit().getIncidentLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getAgregateLimitTypeCode() != null && origCovVersion.getLimit().getAgregateLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getAgregateLimitTypeCode().getValue(), origCovVersion.getLimit().getAgregateLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getAgregateLimitTypeCode() == null && origCovVersion.getLimit().getAgregateLimitTypeCode() != null) ||
                (covVersion.getLimit().getAgregateLimitTypeCode() != null && origCovVersion.getLimit().getAgregateLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getManualIncidentLimitTypeCode() != null && origCovVersion.getLimit().getManualIncidentLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getManualIncidentLimitTypeCode().getValue(), origCovVersion.getLimit().getManualIncidentLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getManualIncidentLimitTypeCode() == null && origCovVersion.getLimit().getManualIncidentLimitTypeCode() != null) ||
                (covVersion.getLimit().getManualIncidentLimitTypeCode() != null && origCovVersion.getLimit().getManualIncidentLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (covVersion.getLimit().getManualAggregateLimitTypeCode() != null && origCovVersion.getLimit().getManualAggregateLimitTypeCode() != null) {
                if (!isEqual(covVersion.getLimit().getManualAggregateLimitTypeCode().getValue(), origCovVersion.getLimit().getManualAggregateLimitTypeCode().getValue())) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getLimit().getManualAggregateLimitTypeCode() == null && origCovVersion.getLimit().getManualAggregateLimitTypeCode() != null) ||
                (covVersion.getLimit().getManualAggregateLimitTypeCode() != null && origCovVersion.getLimit().getManualAggregateLimitTypeCode() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getLimit() == null && origCovVersion.getLimit() != null) ||
            (covVersion.getLimit() != null && origCovVersion.getLimit() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (covVersion.getClaimMadeLiabilityPolicyInformation() != null && origCovVersion.getClaimMadeLiabilityPolicyInformation() != null) {
            if (!isEqual(covVersion.getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate(), origCovVersion.getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate()) ||
                !isEqual(covVersion.getClaimMadeLiabilityPolicyInformation().getSplitRetroactiveDate(), origCovVersion.getClaimMadeLiabilityPolicyInformation().getSplitRetroactiveDate()) ||
                !isEqual(covVersion.getClaimMadeLiabilityPolicyInformation().getClaimsMadeDate(), origCovVersion.getClaimMadeLiabilityPolicyInformation().getClaimsMadeDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getClaimMadeLiabilityPolicyInformation() == null && origCovVersion.getClaimMadeLiabilityPolicyInformation() != null) ||
            (covVersion.getClaimMadeLiabilityPolicyInformation() != null && origCovVersion.getClaimMadeLiabilityPolicyInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (covVersion.getCoverageConversionInformation() != null && origCovVersion.getCoverageConversionInformation() != null) {
            if (!isEqual(covVersion.getCoverageConversionInformation().getClaimsMadeDate(), origCovVersion.getCoverageConversionInformation().getClaimsMadeDate()) ||
                !isEqual(covVersion.getCoverageConversionInformation().getClaimsMadeOverrideDate(), origCovVersion.getCoverageConversionInformation().getClaimsMadeOverrideDate()) ||
                !isEqual(covVersion.getCoverageConversionInformation().getOccurenceDate(), origCovVersion.getCoverageConversionInformation().getOccurenceDate()) ||
                !isEqual(covVersion.getCoverageConversionInformation().getOccurenceOverrideDate(), origCovVersion.getCoverageConversionInformation().getOccurenceOverrideDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getCoverageConversionInformation() == null && origCovVersion.getCoverageConversionInformation() != null) ||
            (covVersion.getCoverageConversionInformation() != null && origCovVersion.getCoverageConversionInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (covVersion.getPcf() != null && origCovVersion.getPcf() != null) {
            if (!isEqual(covVersion.getPcf().getPracticeCountyCode(), origCovVersion.getPcf().getPracticeCountyCode()) ||
                !isEqual(covVersion.getPcf().getStartDate(), origCovVersion.getPcf().getStartDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getPcf() == null && origCovVersion.getPcf() != null) ||
            (covVersion.getPcf() != null && origCovVersion.getPcf() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (covVersion.getAdditionalInformation() != null && origCovVersion.getAdditionalInformation() != null) {
            if (covVersion.getAdditionalInformation().getAdditionalDateTime() != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime() != null) {
                if (covVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                    for (int i = covVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                        covVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (origCovVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                    for (int i = origCovVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                        origCovVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (covVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue(), origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalDateTime().get(0) == null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(0) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (covVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue(), origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (covVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue(), origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalDateTime().get(2) == null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime().get(2) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getAdditionalInformation().getAdditionalDateTime() == null && origCovVersion.getAdditionalInformation().getAdditionalDateTime() != null) ||
                (covVersion.getAdditionalInformation().getAdditionalDateTime() != null && origCovVersion.getAdditionalInformation().getAdditionalDateTime() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }

            if (covVersion.getAdditionalInformation().getAdditionalNumber() != null && origCovVersion.getAdditionalInformation().getAdditionalNumber() != null) {
                if (covVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                    for (int i = covVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                        covVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (origCovVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                    for (int i = origCovVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                        origCovVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (covVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue(), origCovVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalNumber().get(0) == null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(0) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (covVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue(), origCovVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalNumber().get(1) == null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(1) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (covVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue(), origCovVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalNumber().get(2) == null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null && origCovVersion.getAdditionalInformation().getAdditionalNumber().get(2) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getAdditionalInformation().getAdditionalNumber() == null && origCovVersion.getAdditionalInformation().getAdditionalNumber() != null) ||
                (covVersion.getAdditionalInformation().getAdditionalNumber() != null && origCovVersion.getAdditionalInformation().getAdditionalNumber() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }

            if (covVersion.getAdditionalInformation().getAdditionalData() != null && origCovVersion.getAdditionalInformation().getAdditionalData() != null) {
                if (covVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                    for (int i = covVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                        covVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (origCovVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                    for (int i = origCovVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                        origCovVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (covVersion.getAdditionalInformation().getAdditionalData().get(0) != null && origCovVersion.getAdditionalInformation().getAdditionalData().get(0) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalData().get(0).getValue(), origCovVersion.getAdditionalInformation().getAdditionalData().get(0).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalData().get(0) == null && origCovVersion.getAdditionalInformation().getAdditionalData().get(0) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalData().get(0) != null && origCovVersion.getAdditionalInformation().getAdditionalData().get(0) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (covVersion.getAdditionalInformation().getAdditionalData().get(1) != null && origCovVersion.getAdditionalInformation().getAdditionalData().get(1) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalData().get(1).getValue(), origCovVersion.getAdditionalInformation().getAdditionalData().get(1).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalData().get(1) == null && origCovVersion.getAdditionalInformation().getAdditionalData().get(1) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalData().get(1) != null && origCovVersion.getAdditionalInformation().getAdditionalData().get(1) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (covVersion.getAdditionalInformation().getAdditionalData().get(2) != null && origCovVersion.getAdditionalInformation().getAdditionalData().get(2) != null) {
                    if (!isEqual(covVersion.getAdditionalInformation().getAdditionalData().get(2).getValue(), origCovVersion.getAdditionalInformation().getAdditionalData().get(2).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((covVersion.getAdditionalInformation().getAdditionalData().get(2) == null && origCovVersion.getAdditionalInformation().getAdditionalData().get(2) != null) ||
                    (covVersion.getAdditionalInformation().getAdditionalData().get(2) != null && origCovVersion.getAdditionalInformation().getAdditionalData().get(2) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((covVersion.getAdditionalInformation().getAdditionalData() == null && origCovVersion.getAdditionalInformation().getAdditionalData() != null) ||
                (covVersion.getAdditionalInformation().getAdditionalData() != null && origCovVersion.getAdditionalInformation().getAdditionalData() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((covVersion.getAdditionalInformation() == null && origCovVersion.getAdditionalInformation() != null) ||
            (covVersion.getAdditionalInformation() != null && origCovVersion.getAdditionalInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSubCoverageVersionChanged", isChanged);
        }

        return isChanged;
    }

    private void addChangedCoverageVersionToRecordSet(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                                      MedicalMalpracticeCoverageType coverage,
                                                      MedicalMalpracticeCoverageVersionType covVersion, RecordSet changes,
                                                      Record dbCoverage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addChangedCoverageVersionToRecordSet", new Object[]{policyHeader, originalPolicy, coverage, covVersion, changes});
        }

        Record record = setInputCoverageToRecord(policyHeader, originalPolicy, coverage, covVersion, dbCoverage);

        if (isNewTransactionB()) {
            if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
                record.setFieldValue(CoverageInquiryFields.RECORD_MODE_CODE, RecordMode.REQUEST);
            }
            else {
                record.setFieldValue(CoverageInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
            }
        }
        else {
            if (covVersion.getMedicalMalpracticeCoverageVersionDetail() != null &&
                RecordMode.OFFICIAL.equals(covVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode())) {
                record.setFieldValue(CoverageInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
            }
            else {
                record.setFieldValue(CoverageInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
            }
        }
        record.setUpdateIndicator(UpdateIndicator.UPDATED);

        changes.addRecord(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addChangedCoverageVersionToRecordSet");
        }

    }

    private void addNewCoverageVersionToRecordSet(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                                  MedicalMalpracticePolicyType changedPolicy, MedicalMalpracticeCoverageType coverage,
                                                  MedicalMalpracticeCoverageVersionType covVersion, RecordSet changes, RecordSet dbCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addNewCoverageVersionToRecordSet", new Object[]{originalPolicy, coverage, covVersion, changes});
        }

        if (!StringUtils.isBlank(coverage.getCoverageNumberId())) {
            String errorMsg = "Request for new Coverage should not have CoverageNumberId provided.";
            AppException ae = new AppException("ws.policy.change.invalid.update", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "addNewCoverageVersionToRecordSet", ae);
            throw ae;
        }

        Record inputRecord = setInputCoverageToRecord(policyHeader, originalPolicy, coverage, covVersion, null);

        List<InsuredType> insureds = changedPolicy.getInsured();
        String insuredId = null;
        Iterator insIt = insureds.iterator();
        while (insIt.hasNext()) {
            InsuredType insured = (InsuredType)insIt.next();
            insuredId = insured.getInsuredNumberId();
            if (insured.getKey().equalsIgnoreCase(coverage.getReferredInsured().getInsuredReference())) {
                inputRecord.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, insuredId);
                break;
            }
        }

        Record defaultDbCoverage = null;
        if (dbCoverages != null) {
            defaultDbCoverage = handleDefaultDbCoverage(policyHeader, inputRecord, dbCoverages);
        }

        Record saveValuesRecord = null;
        if (defaultDbCoverage != null) {
            // Reuse the already created default coverage record.
            saveValuesRecord = defaultDbCoverage;
        }
        else {
            // Get the initial value for a coverage when is to be added.
            saveValuesRecord = getCoverageManager().getInitialValuesForCoverage(policyHeader, inputRecord);
        }

        String coverageNumberId = saveValuesRecord.getStringValue(CoverageInquiryFields.COVERAGE_NUMBER_ID);
        String coverageId = saveValuesRecord.getStringValue(CoverageInquiryFields.COVERAGE_ID);

        coverage.setCoverageNumberId(coverageNumberId);
        covVersion.setMedicalMalpracticeCoverageVersionId(coverageId);

        if (isNewPolicyB()) {
            inputRecord.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getContractPeriod().getStartDate()));
        }
        else if (isNewTransactionB()) {
            inputRecord.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()));
        }

        mergeCoverageRecords(inputRecord, saveValuesRecord);

        saveValuesRecord.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
        if (defaultDbCoverage != null) {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
        }
        else {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.INSERTED);
        }

        changes.addRecord(saveValuesRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addNewCoverageVersionToRecordSet");
        }

    }

    private Record handleDefaultDbRisk(PolicyHeader policyHeader, Record inputRecord, RecordSet dbRisks) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleDefaultDbRisk", new Object[]{policyHeader, inputRecord, dbRisks});
        }

        Record returnRecord = null;
        Iterator it = dbRisks.getRecords();
        while (it.hasNext()) {
            Record currRecord = (Record)it.next();
            if (currRecord.getStringValue(RiskInquiryFields.ENTITY_ID).equals(
                inputRecord.getStringValue(RiskInquiryFields.ENTITY_ID)) &&
                currRecord.getStringValue(RiskInquiryFields.RISK_TYPE_CODE).equals(
                inputRecord.getStringValue(RiskInquiryFields.RISK_TYPE_CODE)) &&
                "TEMP".equalsIgnoreCase(currRecord.getStringValue(CoverageInquiryFields.RECORD_MODE_CODE))) {
                // The desired risk is already in the DB
                if (isNewPolicyB() || isNewTransactionB()) {
                    // It was added via default risk mechanism. We save this information for later processing
                    returnRecord = currRecord;
                    break;
                }
                else {
                    // It is an existing transaction, the risk was added either through a previous call to
                    // the WS or from the UI. We raise an error as the same coverage cannot be added twice.
                    AppException ae = new AppException("ws.policy.change.duplicate.risk.input", "",
                        new String[]{inputRecord.getStringValue(currRecord.getStringValue(RiskInquiryFields.ENTITY_ID))});
                    l.throwing(getClass().getName(), "handleDefaultDbRisk", ae);
                    throw ae;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleDefaultDbRisk", returnRecord);
        }

        return returnRecord;
    }

    private Record handleDefaultDbCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet dbCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleDefaultDbCoverage", new Object[]{policyHeader, inputRecord, dbCoverages});
        }

        Record returnRecord = null;
        Iterator it = dbCoverages.getRecords();
        while (it.hasNext()) {
            Record currRecord = (Record)it.next();
            if (currRecord.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CODE).equals(
                inputRecord.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CODE)) &&
                "TEMP".equalsIgnoreCase(currRecord.getStringValue(CoverageInquiryFields.RECORD_MODE_CODE))) {
                // The desired coverage is already in the DB
                if (isNewPolicyB() || isNewTransactionB()) {
                    // It was added via default coverage mechanism. We save this information for later processing.
                    returnRecord = currRecord;
                    break;
                }
                else {
                    // It is an existing transaction, the coverage was added either through a previous call to
                    // the WS or from the UI. We raise an error as the same coverage cannot be added twice.
                    AppException ae = new AppException("ws.policy.change.duplicate.coverage.input", "",
                        new String[]{inputRecord.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CODE),
                            inputRecord.getStringValue(CoverageInquiryFields.RISK_BASE_RECORD_ID)});
                    l.throwing(getClass().getName(), "handleDefaultDbCoverage", ae);
                    throw ae;
                }
            }
        }

        if (returnRecord != null) {
            // Some mandatory fields that need to exist for passing validations.
            if (!returnRecord.hasField(CoverageInquiryFields.IS_MANUALLY_RATED))
                returnRecord.setFieldValue(CoverageInquiryFields.IS_MANUALLY_RATED, "N");
            if (!returnRecord.hasField(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE))
                returnRecord.setFieldValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE, "");
            if (!returnRecord.hasField(CoverageInquiryFields.ORIG_RATE_PAYOR_DEPEND_CODE))
                returnRecord.setFieldValue(CoverageInquiryFields.ORIG_RATE_PAYOR_DEPEND_CODE, "");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleDefaultDbCoverage", returnRecord);
        }

        return returnRecord;
    }

    private Record handleDefaultDbSubCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet dbSubCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleDefaultDbSubCoverage", new Object[]{policyHeader, inputRecord, dbSubCoverages});
        }

        Record returnRecord = null;
        Iterator it = dbSubCoverages.getRecords();
        while (it.hasNext()) {
            Record currRecord = (Record)it.next();

            if (currRecord.getStringValue(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID).equals(
                inputRecord.getStringValue(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID)) &&
                currRecord.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CLASS_CODE).equals(
                    inputRecord.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CLASS_CODE)) &&
                "TEMP".equalsIgnoreCase(currRecord.getStringValue(CoverageInquiryFields.RECORD_MODE_CODE))) {
                // The desired SUB coverage is already in the DB
                if (isNewPolicyB() || isNewTransactionB()) {
                    // It was added via default sub coverage mechanism. We save this information for later processing.
                    returnRecord = currRecord;
                    break;
                }
                else {
                    // It is an existing transaction, the sub coverage was added either through a previous call to
                    // the WS or from the UI. We raise an error as the same coverage cannot be added twice.
                    AppException ae = new AppException("ws.policy.change.duplicate.coverage.class.input", "",
                        new String[]{inputRecord.getStringValue(CoverageInquiryFields.PRODUCT_COVERAGE_CLASS_CODE),
                            inputRecord.getStringValue(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID)});
                    l.throwing(getClass().getName(), "handleDefaultDbSubCoverage", ae);
                    throw ae;
                }
            }
        }

        if (returnRecord != null) {
            // Some mandatory fields that need to exist for passing validations.
            if (!returnRecord.hasField(CoverageInquiryFields.IS_MANUALLY_RATED))
                returnRecord.setFieldValue(CoverageInquiryFields.IS_MANUALLY_RATED, "N");
            if (!returnRecord.hasField(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE))
                returnRecord.setFieldValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE, "");
            if (!returnRecord.hasField(CoverageInquiryFields.ORIG_RATE_PAYOR_DEPEND_CODE))
                returnRecord.setFieldValue(CoverageInquiryFields.ORIG_RATE_PAYOR_DEPEND_CODE, "");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleDefaultDbSubCoverage", returnRecord);
        }

        return returnRecord;
    }

    private Record handleDefaultDbComponents(PolicyHeader policyHeader, Record inputRecord, RecordSet dbComp) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleDefaultDbComponents", new Object[]{policyHeader, inputRecord, dbComp});
        }

        Record returnRecord = null;
        Iterator it = dbComp.getRecords();
        while (it.hasNext()) {
            Record currRecord = (Record)it.next();

            if (currRecord.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID).equals(
                inputRecord.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID)) &&
                currRecord.getStringValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID).equals(
                    inputRecord.getStringValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID)) &&
                "TEMP".equalsIgnoreCase(currRecord.getStringValue(ComponentInquiryFields.RECORD_MODE_CODE))) {
                // The desired coverage is already in the DB
                if (isNewPolicyB() || isNewTransactionB()) {
                    // It was added via default component mechanism. We save this information for later processing.
                    returnRecord = currRecord;
                    break;
                }
                else {
                    // It is an existing transaction, the component was added either through a previous call to
                    // the WS or from the UI. We raise an error as the same component cannot be added twice.
                    AppException ae = new AppException("ws.policy.change.duplicate.component.input", "",
                        new String[]{inputRecord.getStringValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID),
                            inputRecord.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID)});
                    l.throwing(getClass().getName(), "handleDefaultDbComponents", ae);
                    throw ae;
                }
            }
        }

        if (returnRecord != null) {
            // Some mandatory fields that need to exist for passing validations.
            if (!returnRecord.hasField(ComponentInquiryFields.ORIG_COMPONENT_VALUE)) {
                returnRecord.setFieldValue(ComponentInquiryFields.ORIG_COMPONENT_VALUE,
                    returnRecord.getFieldValue(ComponentInquiryFields.COMPONENT_VALUE));
            }
            if (!returnRecord.hasField(ComponentInquiryFields.ORIG_COMPONENT_EFF_TO_DATE)) {
                returnRecord.setFieldValue(ComponentInquiryFields.ORIG_COMPONENT_EFF_TO_DATE,
                    returnRecord.getFieldValue(ComponentInquiryFields.COMPONENT_EFF_TO_DATE));
            }
            if (!returnRecord.hasField(ComponentInquiryFields.ORIG_RENEWAL_B)) {
                returnRecord.setFieldValue(ComponentInquiryFields.ORIG_RENEWAL_B,
                    returnRecord.getStringValue(ComponentInquiryFields.RENEWAL_B));
            }
            if (!returnRecord.hasField(ComponentInquiryFields.ORIG_COMPONENT_CYCLE_DATE)) {
                returnRecord.setFieldValue(ComponentInquiryFields.ORIG_COMPONENT_CYCLE_DATE,
                    returnRecord.getStringValue(ComponentInquiryFields.COMPONENT_CYCLE_DATE));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleDefaultDbComponents", returnRecord);
        }

        return returnRecord;
    }

    private Record setInputCoverageToRecord(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                            MedicalMalpracticeCoverageType coverage, MedicalMalpracticeCoverageVersionType covVersion,
                                            Record dbCoverage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputCoverageToRecord", new Object[]{policyHeader, originalPolicy, coverage, covVersion});
        }
        boolean isCoverageClass = false;
        Record record = new Record();

        record.setFieldValue(PolicyInquiryFields.POLICY_ID, policyHeader.getPolicyNo());
        record.setFieldValue(PolicyInquiryFields.POLICY_NO, policyHeader.getPolicyId());
        record.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, coverage.getReferredInsured().getInsuredReference());


        if (coverage.getMedicalMalpracticeCoverageCode()!=null)
            record.setFieldValue(CoverageInquiryFields.PRODUCT_COVERAGE_CODE, coverage.getMedicalMalpracticeCoverageCode().getValue());

        if (!StringUtils.isBlank(coverage.getParentCoverageNumberId())) {
            isCoverageClass = true;
            record.setFieldValue(CoverageInquiryFields.COVERAGE_CLASS_ID, covVersion.getMedicalMalpracticeCoverageVersionId());
            record.setFieldValue(CoverageInquiryFields.COVERAGE_CLASS_BASE_ID, coverage.getCoverageNumberId());
            record.setFieldValue(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID, coverage.getParentCoverageNumberId());
            if (covVersion.getEffectivePeriod() != null) {
                record.setFieldValue(CoverageInquiryFields.CLASS_EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getStartDate()));
                record.setFieldValue(CoverageInquiryFields.CLASS_EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
            }
        }
        else {
            isCoverageClass = false;
            record.setFieldValue(CoverageInquiryFields.COVERAGE_ID, covVersion.getMedicalMalpracticeCoverageVersionId());
            record.setFieldValue(CoverageInquiryFields.COVERAGE_NUMBER_ID, coverage.getCoverageNumberId());
            if (covVersion.getEffectivePeriod() != null) {
                record.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getStartDate()));
                record.setFieldValue(CoverageInquiryFields.EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
            }
        }

        if (covVersion.getPrimaryIndicator()!=null)
            record.setFieldValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR, YesNoEmptyFlag.getInstance(covVersion.getPrimaryIndicator()).getName());
        if (covVersion.getEffectivePeriod()!=null) {
            record.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getStartDate()));
            record.setFieldValue(CoverageInquiryFields.EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
            record.setFieldValue(CoverageInquiryFields.ORIG_EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
        }
        if (covVersion.getLimit()!=null) {
            if (covVersion.getLimit().getLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.COVERAGE_LIMIT_CODE, covVersion.getLimit().getLimitTypeCode().getValue());
            if (covVersion.getLimit().getSharedLimitIndicator()!=null)
                record.setFieldValue(CoverageInquiryFields.SHARED_LIMIT_B, YesNoEmptyFlag.getInstance(covVersion.getLimit().getSharedLimitIndicator()).getName());
            if (covVersion.getLimit().getIncidentLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.INCIDENT_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getIncidentLimitTypeCode().getValue()));
            if (covVersion.getLimit().getAgregateLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.AGGREGATE_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getAgregateLimitTypeCode().getValue()));
            if (covVersion.getLimit().getSubLimitIndicator()!=null)
                record.setFieldValue(CoverageInquiryFields.PRODUCT_SUBLIMIT_B, YesNoEmptyFlag.getInstance(covVersion.getLimit().getSubLimitIndicator()).getName());
            record.setFieldValue(CoverageInquiryFields.LIMIT_EROSION_CODE, covVersion.getLimit().getErosionTypeCode());
            if (covVersion.getLimit().getManualIncidentLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.MANUAL_INCIDENT_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getManualIncidentLimitTypeCode().getValue()));
            if (covVersion.getLimit().getManualAggregateLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.MANUAL_AGGREGATE_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getManualAggregateLimitTypeCode().getValue()));
            record.setFieldValue(CoverageInquiryFields.PER_DAY_LIMIT,StringUtils.validateNumeric(covVersion.getLimit().getDailyLimitAmount()));
        }
        if (covVersion.getClaimMadeLiabilityPolicyInformation()!=null) {
            record.setFieldValue(CoverageInquiryFields.RETROACTIVE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate()));
            record.setFieldValue(CoverageInquiryFields.SPLIT_RETROACTIVE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getClaimMadeLiabilityPolicyInformation().getSplitRetroactiveDate()));
            record.setFieldValue(CoverageInquiryFields.CLAIMS_MADE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getClaimMadeLiabilityPolicyInformation().getClaimsMadeDate()));
        }

        record.setFieldValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE, covVersion.getPayorCode());
        record.setFieldValue(CoverageInquiryFields.CANCELLATION_METHOD_CODE, covVersion.getCancellationMethodCode());
        record.setFieldValue(CoverageInquiryFields.ANNUAL_BASE_RATE, covVersion.getAnnualBaseRate());
        record.setFieldValue(CoverageInquiryFields.DEFAULT_AMOUNT_OF_INSURANCE, StringUtils.validateNumeric(covVersion.getDefaultAmountOfInsurance()));
        record.setFieldValue(CoverageInquiryFields.ADDL_AMOUNT_OF_INSURANCE, StringUtils.validateNumeric(covVersion.getAdditionalAmountOfInsurance()));
        record.setFieldValue(CoverageInquiryFields.LOSS_OF_INCOME_DAYS, StringUtils.validateNumeric(covVersion.getLossOfIncomeDays()));
        record.setFieldValue(CoverageInquiryFields.EXPOSURE_UNIT, StringUtils.validateNumeric(covVersion.getExposureUnit()));
        record.setFieldValue(CoverageInquiryFields.BUILDING_RATE, covVersion.getBuildingRate());
        if (covVersion.getForecastIndicator() != null)
            record.setFieldValue(CoverageInquiryFields.USED_FOR_FORECAST_B, YesNoEmptyFlag.getInstance(covVersion.getForecastIndicator()).getName());
        if (covVersion.getDirectPrimaryIndicator() != null)
            record.setFieldValue(CoverageInquiryFields.DIRECT_PRIMARY_B, YesNoEmptyFlag.getInstance(covVersion.getDirectPrimaryIndicator()).getName());
        record.setFieldValue(CoverageInquiryFields.SYMBOL, covVersion.getAdditionalSymbolCode());

        if (covVersion.getCoverageConversionInformation()!=null) {
            record.setFieldValue(CoverageInquiryFields.CM_CONV_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getClaimsMadeDate()));
            record.setFieldValue(CoverageInquiryFields.CM_CONV_OVERRIDE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getClaimsMadeOverrideDate()));
            record.setFieldValue(CoverageInquiryFields.OC_CONV_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getOccurenceDate()));
            record.setFieldValue(CoverageInquiryFields.OC_CONV_OVERRIDE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getOccurenceOverrideDate()));
        }

        if (covVersion.getPcf() != null) {
            record.setFieldValue(CoverageInquiryFields.PCF_COUNTY_CODE, covVersion.getPcf().getPracticeCountyCode());
            record.setFieldValue(CoverageInquiryFields.PCF_PARTICIPATION_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getPcf().getStartDate()));
        }

        record.setFieldValue(CoverageInquiryFields.DEDUCTIBLE_COMPONENT_ID, covVersion.getDeductible());
        record.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_CODE, covVersion.getManualDeductibleSIRCode());
        record.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_INC_VALUE, StringUtils.validateNumeric(covVersion.getManualDeductibleSIRIncidentAmount()));
        record.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_AGG_VALUE, StringUtils.validateNumeric(covVersion.getManualDeductibleSIRAggregateAmount()));
        record.setFieldValue(CoverageInquiryFields.INDEMNITY_TYPE, covVersion.getDeductibleSIRIndemnityTypeCode());
        record.setFieldValue(CoverageInquiryFields.CLAIM_PROCESS_CODE, covVersion.getClaimProcessCode());

        if (covVersion.getAdditionalInformation()!=null) {
            if (covVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                for (int i = covVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                    covVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                }
            }
            record.setFieldValue(CoverageInquiryFields.DATE1, DateUtils.parseXMLDateToOasisDate(covVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()));
            record.setFieldValue(CoverageInquiryFields.DATE2, DateUtils.parseXMLDateToOasisDate(covVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()));
            record.setFieldValue(CoverageInquiryFields.DATE3, DateUtils.parseXMLDateToOasisDate(covVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue()));

            if (covVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                for (int i = covVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                    covVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                }
            }
            record.setFieldValue(CoverageInquiryFields.NUM1, StringUtils.validateNumeric(covVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue()));
            record.setFieldValue(CoverageInquiryFields.NUM2, StringUtils.validateNumeric(covVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue()));
            record.setFieldValue(CoverageInquiryFields.NUM3, StringUtils.validateNumeric(covVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue()));

            if (covVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                for (int i = covVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                    covVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                }
            }
            record.setFieldValue(CoverageInquiryFields.CHAR1, covVersion.getAdditionalInformation().getAdditionalData().get(0).getValue());
            record.setFieldValue(CoverageInquiryFields.CHAR2, covVersion.getAdditionalInformation().getAdditionalData().get(1).getValue());
            record.setFieldValue(CoverageInquiryFields.CHAR3, covVersion.getAdditionalInformation().getAdditionalData().get(2).getValue());
        }

        record.setFieldValue(PolicyInquiryFields.SHORT_TERM_B, "");
        if (dbCoverage != null) {
            record.setFields(dbCoverage, false);
            record.setFieldValue("isManuallyRated", getCoverageManager().isManuallyRated(CoverageFields.getRatingModuleCode(record)));
        }

        if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode()) &&
            RecordMode.OFFICIAL.equals(covVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode())) {
            String oosCoverageId = getCoverageManager().getCoverageSequenceId();
            if (!isCoverageClass) {
                record.setFieldValue(CoverageInquiryFields.COVERAGE_ID, oosCoverageId);
            }
            else {
                record.setFieldValue(CoverageInquiryFields.COVERAGE_CLASS_ID, oosCoverageId);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputCoverageToRecord", record);
        }

        return record;
    }

    private void validateForChangeCoverage(MedicalMalpracticeCoverageType originalCoverage, MedicalMalpracticeCoverageType coverage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeCoverage", new Object[]{originalCoverage, coverage});
        }

        String errorMsg = "";

        if (!originalCoverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(coverage.getReferredInsured().getInsuredReference()))
            errorMsg = errorMsg + "ReferredInsured";
        if (!isEqual(originalCoverage.getParentCoverageNumberId(), coverage.getParentCoverageNumberId()))
            errorMsg = errorMsg + "ParentCoverageNumberId";
        if (!isEqual(originalCoverage.getMedicalMalpracticeCoverageCode().getValue(), coverage.getMedicalMalpracticeCoverageCode().getValue()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageCode";
        if (!isEqual(originalCoverage.getMedicalMalpracticeCoverageCode().getValue(), coverage.getMedicalMalpracticeCoverageCode().getValue()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageCode";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.existing.coverage", "", new String[]{errorMsg, coverage.getCoverageNumberId()});
            l.throwing(getClass().getName(), "validateForChangeCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeCoverage");
        }
    }

    private void validateForChangeCoverageVersion(PolicyHeader policyHeader,
                                                  MedicalMalpracticeCoverageVersionType covVersion,
                                                  MedicalMalpracticeCoverageVersionType origCovVersion,
                                                  Record dbCoverageRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeCoverageVersion",
                new Object[]{policyHeader, covVersion, origCovVersion, dbCoverageRecord});
        }

        if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
            if (RecordMode.TEMP.equals(covVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode()) &&
                !StringUtils.isBlank(covVersion.getMedicalMalpracticeCoverageVersionDetail().getParentVersionNumberId())) {
                AppException ae = new AppException("pm.oose.modified.record.exist.error2", "",
                    new String[]{"coverage"});
                l.throwing(getClass().getName(), "validateForChangeCoverageVersion", ae);
                throw ae;
            }
        }

        if (dbCoverageRecord == null) {
            AppException ae = new AppException("ws.policy.change.version.not.exist", "",
                new String[]{"Coverage Version " + covVersion.getMedicalMalpracticeCoverageVersionId()});
            l.throwing(getClass().getName(), "validateForChangeCoverageVersion", ae);
            throw ae;
        }

        String errorMsg = "";
        if (!origCovVersion.getPrimaryIndicator().equalsIgnoreCase(covVersion.getPrimaryIndicator()))
            errorMsg = errorMsg + "PrimaryIndicator";
        if (!origCovVersion.getEffectivePeriod().getStartDate().equalsIgnoreCase(covVersion.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";
        if (!origCovVersion.getEffectivePeriod().getEndDate().equalsIgnoreCase(covVersion.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";
        if (!origCovVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode().equalsIgnoreCase(covVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageVersionDetail/VersionModeCode, ";
        if (!origCovVersion.getMedicalMalpracticeCoverageVersionDetail().getParentVersionNumberId().equalsIgnoreCase(covVersion.getMedicalMalpracticeCoverageVersionDetail().getParentVersionNumberId()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageVersionDetail/ParentVersionNumberId, ";
        if (!origCovVersion.getMedicalMalpracticeCoverageVersionDetail().getAfterImageIndicator().equalsIgnoreCase(covVersion.getMedicalMalpracticeCoverageVersionDetail().getAfterImageIndicator()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageVersionDetail/AfterImageIndicator, ";
        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length() - 2);
            AppException ae = new AppException("ws.policy.change.existing.coverage", "", new String[]{errorMsg, covVersion.getMedicalMalpracticeCoverageVersionId()});
            l.throwing(getClass().getName(), "validateForChangeCoverageVersion", ae);
            throw ae;
        }

        Date versionEff = DateUtils.parseXMLDate(covVersion.getEffectivePeriod().getStartDate());
        Date versionExp = DateUtils.parseXMLDate(covVersion.getEffectivePeriod().getEndDate());
        Date transactionDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        if (transactionDate.before(versionEff) ||
            transactionDate.after(versionExp) ||
            transactionDate.equals(versionExp)) {
            AppException ae = new AppException("ws.policy.change.version.invalid", "",
                new String[]{"Coverage Version: " + covVersion.getMedicalMalpracticeCoverageVersionId()});
            l.throwing(getClass().getName(), "validateForChangeCoverageVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeCoverageVersion");
        }
    }

    private void validateForChangeSubCoverage(MedicalMalpracticeCoverageType originalCoverage, MedicalMalpracticeCoverageType coverage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeSubCoverage", new Object[]{originalCoverage, coverage});
        }

        String errorMsg = "";

        if (!originalCoverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(coverage.getReferredInsured().getInsuredReference()))
            errorMsg = errorMsg + "ReferredInsured";
        if (!isEqual(originalCoverage.getParentCoverageNumberId(), coverage.getParentCoverageNumberId()))
            errorMsg = errorMsg + "ParentCoverageNumberId";
        if (!isEqual(originalCoverage.getMedicalMalpracticeCoverageCode().getValue(), coverage.getMedicalMalpracticeCoverageCode().getValue()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageCode";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.existing.subcoverage", "", new String[]{errorMsg, coverage.getCoverageNumberId()});
            l.throwing(getClass().getName(), "validateForChangeSubCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeSubCoverage");
        }
    }

    private void validateForChangeSubCoverageVersion(PolicyHeader policyHeader,
                                                     MedicalMalpracticeCoverageVersionType origSubCovgVersion,
                                                     MedicalMalpracticeCoverageVersionType subCovgVersion,
                                                     Record dbSubCoverageRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeSubCoverageVersion", new Object[]{policyHeader, origSubCovgVersion, subCovgVersion});
        }

        if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
            if (RecordMode.TEMP.equals(subCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode()) &&
                !StringUtils.isBlank(subCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getParentVersionNumberId())) {
                AppException ae = new AppException("pm.oose.modified.record.exist.error2", "",
                    new String[]{"coverage class"});
                l.throwing(getClass().getName(), "validateForChangeSubCoverageVersion", ae);
                throw ae;
            }
        }

        if (dbSubCoverageRecord == null) {
            AppException ae = new AppException("ws.policy.change.version.not.exist", "",
                new String[]{"Coverage Class Version " + subCovgVersion.getMedicalMalpracticeCoverageVersionId()});
            l.throwing(getClass().getName(), "validateForChangeSubCoverageVersion", ae);
            throw ae;
        }

        String errorMsg = "";
        if (!origSubCovgVersion.getPrimaryIndicator().equalsIgnoreCase(subCovgVersion.getPrimaryIndicator()))
            errorMsg = errorMsg + "PrimaryIndicator, ";
        if (!origSubCovgVersion.getEffectivePeriod().getStartDate().equalsIgnoreCase(subCovgVersion.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";
        if (!origSubCovgVersion.getEffectivePeriod().getEndDate().equalsIgnoreCase(subCovgVersion.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";
        if (!origSubCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode().equalsIgnoreCase(subCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getVersionModeCode()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageVersionDetail/VersionModeCode, ";
        if (!origSubCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getParentVersionNumberId().equalsIgnoreCase(subCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getParentVersionNumberId()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageVersionDetail/ParentVersionNumberId, ";
        if (!origSubCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getAfterImageIndicator().equalsIgnoreCase(subCovgVersion.getMedicalMalpracticeCoverageVersionDetail().getAfterImageIndicator()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageVersionDetail/AfterImageIndicator, ";
        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length() - 2);
            AppException ae = new AppException("ws.policy.change.existing.subcoverage", "", new String[]{errorMsg, subCovgVersion.getMedicalMalpracticeCoverageVersionId()});
            l.throwing(getClass().getName(), "validateForChangeSubCoverageVersion", ae);
            throw ae;
        }

        Date versionEff = DateUtils.parseXMLDate(subCovgVersion.getEffectivePeriod().getStartDate());
        Date versionExp = DateUtils.parseXMLDate(subCovgVersion.getEffectivePeriod().getEndDate());
        Date transactionDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        if (transactionDate.before(versionEff) ||
            transactionDate.after(versionExp) ||
            transactionDate.equals(versionExp)) {
            AppException ae = new AppException("ws.policy.change.version.invalid", "",
                new String[]{"Coverage Class Version: " + subCovgVersion.getMedicalMalpracticeCoverageVersionId()});
            l.throwing(getClass().getName(), "validateForChangeSubCoverageVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeSubCoverageVersion");
        }
    }

    private RecordSet getNewOrChangedComponents(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                                MedicalMalpracticePolicyType changedPolicy, String riskKey, String riskNumberId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNewOrChangedComponents", new Object[]{policyHeader, originalPolicy,
                changedPolicy, riskKey, riskNumberId});
        }

        RecordSet dbComp = null;
        RecordSet recordSet = new RecordSet();
        if (changedPolicy.getMedicalMalpracticeLineOfBusiness() != null && changedPolicy.getCreditSurchargeDeductible() != null) {
            List <MedicalMalpracticeCoverageType> coverages = changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            List <CreditSurchargeDeductibleType> components = changedPolicy.getCreditSurchargeDeductible();
            
            if (components.size()>0) {
                dbComp = getComponentManager().loadAllComponentForWs(policyHeader, riskNumberId, ComponentOwner.COVERAGE);
            }

            Iterator compIt = components.iterator();
            while (compIt.hasNext()) {
                CreditSurchargeDeductibleType component = (CreditSurchargeDeductibleType)compIt.next();
                String coverageId = component.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference();
                Iterator it = coverages.iterator();
                boolean processComponent = false;
                while (it.hasNext()) {
                    MedicalMalpracticeCoverageType coverage = (MedicalMalpracticeCoverageType)it.next();
                    if (coverage.getReferredInsured()==null || coverage.getReferredInsured().getInsuredReference()==null) {
                        String errorMsg = "ReferredInsured or InsuredReference ";
                        AppException ae = new AppException("ws.policy.change.new.coverage.version", "", new String[]{errorMsg});
                        l.throwing(getClass().getName(), "getNewOrChangedComponents", ae);
                        throw ae;
                    }

                    if (coverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(riskKey) && coverage.getKey().equalsIgnoreCase(coverageId)) {
                        processComponent = true;
                    }
                }

                if (processComponent) {
                    RecordSet rs = getComponentChanges(policyHeader, component, originalPolicy, changedPolicy, dbComp);
                    if (rs.getSize()>0) {
                        recordSet.addRecords(rs);

                        // Handle Dependent Components.
                        Record record = getInputRecordForDependentComponents(changedPolicy, component, riskKey);

                        if (record.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID) != null &&
                            rs.getFirstRecord().hasField(ComponentInquiryFields.NEW_COVERAGE_BASE_RECORD_ID) &&
                            rs.getFirstRecord().getStringValue(ComponentInquiryFields.NEW_COVERAGE_BASE_RECORD_ID) != null &&
                            !record.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID).equalsIgnoreCase(rs.getFirstRecord().getStringValue(ComponentInquiryFields.NEW_COVERAGE_BASE_RECORD_ID))) {
                            record.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID, rs.getFirstRecord().getStringValue(ComponentInquiryFields.NEW_COVERAGE_BASE_RECORD_ID));
                        }

                        rs = getComponentManager().loadDependentComponent(policyHeader, record, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
                        if (rs.getSize()>0) {
                            Iterator itDepComp = rs.getRecords();
                            while (itDepComp.hasNext()) {
                                Record depRec = (Record)itDepComp.next();
                                depRec.setFields(record, false);
                                Record initDependRec = getComponentManager().getInitialValuesForAddComponent(policyHeader, depRec);
                                initDependRec.setFields(depRec, false);
                                initDependRec.setUpdateIndicator(UpdateIndicator.INSERTED);
                                recordSet.addRecord(initDependRec);
                            }
                        }
                    }
                }
            }
        }

        //Handle deleting component.
        if (originalPolicy != null && originalPolicy.getMedicalMalpracticeLineOfBusiness() != null) {
            if (dbComp == null) {
                dbComp = getComponentManager().loadAllComponentForWs(policyHeader, riskNumberId, ComponentOwner.COVERAGE);
            }
            
            List<MedicalMalpracticeCoverageType> origCoverageList =
                originalPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            List<CreditSurchargeDeductibleType> origComponentList = originalPolicy.getCreditSurchargeDeductible();
            for (CreditSurchargeDeductibleType deductible : origComponentList) {
                String coverageId = deductible.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference();
                boolean processDeleteComponentB = false;
                for (MedicalMalpracticeCoverageType origCoverage : origCoverageList) {
                    if (origCoverage.getReferredInsured() == null || origCoverage.getReferredInsured().getInsuredReference() == null) {
                        String errorMsg = "ReferredInsured or InsuredReference ";
                        AppException ae = new AppException("ws.policy.change.new.coverage.version", "", new String[]{errorMsg});
                        l.throwing(getClass().getName(), "getNewOrChangedComponents", ae);
                        throw ae;
                    }
                    if (coverageId.equalsIgnoreCase(origCoverage.getCoverageNumberId()) &&
                        riskKey.equalsIgnoreCase(origCoverage.getReferredInsured().getInsuredReference())) {
                        processDeleteComponentB = true;
                    }
                }
                if (processDeleteComponentB) {
                    RecordSet deletedCompRS = getDeletedComponents(policyHeader, deductible, changedPolicy, dbComp);
                    if (deletedCompRS != null && deletedCompRS.getSize() > 0) {
                        recordSet.addRecords(deletedCompRS);
                    }
                }
            }
        }
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNewOrChangedComponents", recordSet);
        }

        return recordSet;
    }

    /**
     * Get the records that need to be deleted.
     * @param policyHeader
     * @param origComponent
     * @param changedPolicy
     * @param dbComponents
     * @return
     */
    private RecordSet getDeletedComponents(PolicyHeader policyHeader,
                                           CreditSurchargeDeductibleType origComponent,
                                           MedicalMalpracticePolicyType changedPolicy,
                                           RecordSet dbComponents) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDeletedComponents",
                new Object[]{policyHeader, origComponent, changedPolicy, dbComponents});
        }

        RecordSet rs = new RecordSet();

        //Find the component in After image.
        CreditSurchargeDeductibleType changedComponent = new CreditSurchargeDeductibleType();
        List<CreditSurchargeDeductibleType> changedComponentList = changedPolicy.getCreditSurchargeDeductible();
        for (CreditSurchargeDeductibleType deductible : changedComponentList) {
            if (StringUtils.isSame(origComponent.getCreditSurchargeDeductibleNumberId(),
                deductible.getCreditSurchargeDeductibleNumberId())) {
                changedComponent = deductible;
            }
        }

        for (CreditSurchargeDeductibleVersionType deductibleVersion :
            origComponent.getCreditSurchargeDeductibleVersion()) {
            boolean existedB = false;
            //Find the component version that need to be deleted.
            //The component version that does not exist in After image need to be deleted.
            for (CreditSurchargeDeductibleVersionType changedDeductibleVersion :
                changedComponent.getCreditSurchargeDeductibleVersion()) {
                if (StringUtils.isSame(deductibleVersion.getCreditSurchargeDeductibleVersionId(),
                    changedDeductibleVersion.getCreditSurchargeDeductibleVersionId())) {
                    existedB = true;
                }
            }
            if (!existedB) {
                //1. Cannot delete a component that does not exist in DB.
                RecordSet subSet = dbComponents.getSubSet(new RecordFilter(ComponentInquiryFields.POLICY_COV_COMPONENT_ID,
                    deductibleVersion.getCreditSurchargeDeductibleVersionId()));
                if (subSet == null || subSet.getSize() == 0) {
                    String errorMsg = "Cannot delete the component with CreditSurchargeDeductibleVersionId: " +
                        deductibleVersion.getCreditSurchargeDeductibleVersionId() +
                        ", because it does not exist in the DB.";
                    AppException ae = new AppException("ws.policy.change.invalid.update", "",
                        new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getDeletedComponents", ae);
                    throw ae;
                }

                //2. Cannot delete a component which is in OFFICIAL status.
                Record dbComponentRec = subSet.getRecord(0);
                if (!"TEMP".equalsIgnoreCase(dbComponentRec.getStringValue(ComponentInquiryFields.RECORD_MODE_CODE))) {
                    String errorMsg = "Cannot delete a component (CreditSurchargeDeductibleVersionId: " +
                        deductibleVersion.getCreditSurchargeDeductibleVersionId() + ") " +
                        "which is not in Temp status.";
                    AppException ae = new AppException("ws.policy.change.invalid.update", "",
                        new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getDeletedComponents", ae);
                    throw ae;
                }

                Record componentRecord =
                    setInputComponentToRecord(policyHeader, null, changedPolicy, origComponent, deductibleVersion);
                componentRecord.setFields(dbComponentRec, false);
                componentRecord.setUpdateIndicator(UpdateIndicator.DELETED);
                componentRecord.setFieldValue(CoverageInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
                rs.addRecord(componentRecord);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDeletedComponents", rs);
        }
        return rs;
    }

    private RecordSet getComponentChanges(PolicyHeader policyHeader, CreditSurchargeDeductibleType component, MedicalMalpracticePolicyType originalPolicy,
                                          MedicalMalpracticePolicyType changedPolicy, RecordSet dbComp) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getComponentChanges", new Object[]{policyHeader, component, originalPolicy, changedPolicy, dbComp});
        }

        boolean isComponentFound = false;

        RecordSet changes = new RecordSet();
        if (originalPolicy != null) {
            List <CreditSurchargeDeductibleType> origComponents = originalPolicy.getCreditSurchargeDeductible();
            Iterator it = origComponents.iterator();
            while (it.hasNext()) {
                CreditSurchargeDeductibleType origComponent = (CreditSurchargeDeductibleType)it.next();
                if (!StringUtils.isBlank(component.getCreditSurchargeDeductibleNumberId()) && component.getCreditSurchargeDeductibleNumberId().equalsIgnoreCase(origComponent.getCreditSurchargeDeductibleNumberId())) {
                    isComponentFound = true;
                    validateForChangeComponent(origComponent, component);

                    List<CreditSurchargeDeductibleVersionType> componentVersion = component.getCreditSurchargeDeductibleVersion();
                    List<CreditSurchargeDeductibleVersionType> origComponentVersion = origComponent.getCreditSurchargeDeductibleVersion();
                    Iterator compIt = componentVersion.iterator();
                    Iterator origCompIt = origComponentVersion.iterator();

                    while (compIt.hasNext()) {
                        boolean versionFound = false;
                        CreditSurchargeDeductibleVersionType compVersion = (CreditSurchargeDeductibleVersionType)compIt.next();
                        while (origCompIt.hasNext()) {
                            CreditSurchargeDeductibleVersionType origCompVersion = (CreditSurchargeDeductibleVersionType)origCompIt.next();
                            if (!StringUtils.isBlank(compVersion.getCreditSurchargeDeductibleVersionId()) &&
                                compVersion.getCreditSurchargeDeductibleVersionId().equalsIgnoreCase(origCompVersion.getCreditSurchargeDeductibleVersionId())) {
                                if (isComponentVersionChanged(compVersion, origCompVersion)) {
                                    RecordSet subRecordSet = dbComp.getSubSet(new RecordFilter(ComponentInquiryFields.POLICY_COV_COMPONENT_ID, compVersion.getCreditSurchargeDeductibleVersionId()));
                                    Record dbComponent = null;
                                    if (subRecordSet != null && subRecordSet.getSize() > 0) {
                                        dbComponent = subRecordSet.getRecord(0);
                                    }
                                    validateForChangeComponentVersion(policyHeader, origCompVersion, compVersion, dbComponent);
                                    addChangedComponentVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, component, compVersion, changes, dbComponent);
                                }
                                versionFound = true;
                                break;
                            }
                        }
                        if (!versionFound) {
                            validateForNewComponentVersion(component, compVersion);
                            addNewComponentVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, component, compVersion, changes, dbComp);
                        }
                    }
                }
                if (isComponentFound) {
                    break;
                }
            }
        }

        if (!isComponentFound) {
            List<CreditSurchargeDeductibleVersionType> componentVersion = component.getCreditSurchargeDeductibleVersion();
            Iterator compIt = componentVersion.iterator();
            while (compIt.hasNext()) {
                CreditSurchargeDeductibleVersionType compVersion = (CreditSurchargeDeductibleVersionType)compIt.next();
                validateForNewComponentVersion(component, compVersion);
                addNewComponentVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, component, compVersion, changes, dbComp);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getComponentChanges");
        }

        return changes;
    }

    private boolean isComponentVersionChanged(CreditSurchargeDeductibleVersionType compVersion, CreditSurchargeDeductibleVersionType origCompVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isComponentVersionChanged", new Object[]{compVersion, origCompVersion});
        }

        boolean isChanged = false;
        if (compVersion==null || origCompVersion==null) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (compVersion.getEffectivePeriod() != null && origCompVersion.getEffectivePeriod() != null) {
            if (!isEqual(compVersion.getEffectivePeriod().getStartDate(), origCompVersion.getEffectivePeriod().getStartDate()) ||
                !isEqual(compVersion.getEffectivePeriod().getEndDate(), origCompVersion.getEffectivePeriod().getEndDate())) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((compVersion.getEffectivePeriod() == null && origCompVersion.getEffectivePeriod() != null) ||
            (compVersion.getEffectivePeriod() != null && origCompVersion.getEffectivePeriod() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (!isEqual(compVersion.getNumericValue(), origCompVersion.getNumericValue()) ||
            !isEqual(compVersion.getIncidentDeductibleNumericValue(), origCompVersion.getIncidentDeductibleNumericValue()) ||
            !isEqual(compVersion.getAggregateDeductibleNumericValue(), origCompVersion.getAggregateDeductibleNumericValue()) ||
            !isEqual(compVersion.getCycleDate(), origCompVersion.getCycleDate()) ||
            !isEqual(compVersion.getProrateIndicator(), origCompVersion.getProrateIndicator()) ||
            !isEqual(compVersion.getClassificationCode(), origCompVersion.getClassificationCode()) ||
            !isEqual(compVersion.getAdditionalNotes(), origCompVersion.getAdditionalNotes())) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
            }
            return isChanged;
        }
        if (compVersion.getAdditionalInformation() != null && origCompVersion.getAdditionalInformation() != null) {
            if (compVersion.getAdditionalInformation().getAdditionalDateTime() != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime() != null) {
                if (compVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                    for (int i = compVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                        compVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (origCompVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                    for (int i = origCompVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                        origCompVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                    }
                }
                if (compVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue(), origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalDateTime().get(0) == null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalDateTime().get(0) != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(0) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (compVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue(), origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalDateTime().get(1) == null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalDateTime().get(1) != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(1) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (compVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue(), origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalDateTime().get(2) == null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalDateTime().get(2) != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime().get(2) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((compVersion.getAdditionalInformation().getAdditionalDateTime() == null && origCompVersion.getAdditionalInformation().getAdditionalDateTime() != null) ||
                (compVersion.getAdditionalInformation().getAdditionalDateTime() != null && origCompVersion.getAdditionalInformation().getAdditionalDateTime() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (compVersion.getAdditionalInformation().getAdditionalNumber() != null && origCompVersion.getAdditionalInformation().getAdditionalNumber() != null) {
                if (compVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                    for (int i = compVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                        compVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (origCompVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                    for (int i = origCompVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                        origCompVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                    }
                }
                if (compVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue(), origCompVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalNumber().get(0) == null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalNumber().get(0) != null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(0) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (compVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue(), origCompVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalNumber().get(1) == null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalNumber().get(1) != null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(1) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (compVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue(), origCompVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalNumber().get(2) == null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalNumber().get(2) != null && origCompVersion.getAdditionalInformation().getAdditionalNumber().get(2) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((compVersion.getAdditionalInformation().getAdditionalNumber() == null && origCompVersion.getAdditionalInformation().getAdditionalNumber() != null) ||
                (compVersion.getAdditionalInformation().getAdditionalNumber() != null && origCompVersion.getAdditionalInformation().getAdditionalNumber() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                }
                return isChanged;
            }
            if (compVersion.getAdditionalInformation().getAdditionalData() != null && origCompVersion.getAdditionalInformation().getAdditionalData() != null) {
                if (compVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                    for (int i = compVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                        compVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (origCompVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                    for (int i = origCompVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                        origCompVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                    }
                }
                if (compVersion.getAdditionalInformation().getAdditionalData().get(0) != null && origCompVersion.getAdditionalInformation().getAdditionalData().get(0) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalData().get(0).getValue(), origCompVersion.getAdditionalInformation().getAdditionalData().get(0).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalData().get(0) == null && origCompVersion.getAdditionalInformation().getAdditionalData().get(0) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalData().get(0) != null && origCompVersion.getAdditionalInformation().getAdditionalData().get(0) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (compVersion.getAdditionalInformation().getAdditionalData().get(1) != null && origCompVersion.getAdditionalInformation().getAdditionalData().get(1) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalData().get(1).getValue(), origCompVersion.getAdditionalInformation().getAdditionalData().get(1).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalData().get(1) == null && origCompVersion.getAdditionalInformation().getAdditionalData().get(1) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalData().get(1) != null && origCompVersion.getAdditionalInformation().getAdditionalData().get(1) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
                if (compVersion.getAdditionalInformation().getAdditionalData().get(2) != null && origCompVersion.getAdditionalInformation().getAdditionalData().get(2) != null) {
                    if (!isEqual(compVersion.getAdditionalInformation().getAdditionalData().get(2).getValue(), origCompVersion.getAdditionalInformation().getAdditionalData().get(2).getValue())) {
                        isChanged = true;
                        if (l.isLoggable(Level.FINER)) {
                            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                        }
                        return isChanged;
                    }
                }
                else if ((compVersion.getAdditionalInformation().getAdditionalData().get(2) == null && origCompVersion.getAdditionalInformation().getAdditionalData().get(2) != null) ||
                    (compVersion.getAdditionalInformation().getAdditionalData().get(2) != null && origCompVersion.getAdditionalInformation().getAdditionalData().get(2) == null)) {
                    isChanged = true;
                    if (l.isLoggable(Level.FINER)) {
                        l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                    }
                    return isChanged;
                }
            }
            else if ((compVersion.getAdditionalInformation().getAdditionalData() == null && origCompVersion.getAdditionalInformation().getAdditionalData() != null) ||
                (compVersion.getAdditionalInformation().getAdditionalData() != null && origCompVersion.getAdditionalInformation().getAdditionalData() == null)) {
                isChanged = true;
                if (l.isLoggable(Level.FINER)) {
                    l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
                }
                return isChanged;
            }
        }
        else if ((compVersion.getAdditionalInformation() == null && origCompVersion.getAdditionalInformation() != null) ||
            (compVersion.getAdditionalInformation() != null && origCompVersion.getAdditionalInformation() == null)) {
            isChanged = true;
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
            }
            return isChanged;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isComponentVersionChanged", isChanged);
        }

        return isChanged;
    }

    private Record getInputRecordForDependentComponents(MedicalMalpracticePolicyType changedPolicy, CreditSurchargeDeductibleType component, String riskId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInputRecordForDependentComponents", new Object[]{changedPolicy, component, riskId});
        }

        Record record = new Record();
        record.setFieldValue(PolicyInquiryFields.POLICY_NO, changedPolicy.getPolicyId());
        record.setFieldValue(PolicyInquiryFields.POLICY_TERM_HISTORY_ID, changedPolicy.getPolicyTermNumberId());
        record.setFieldValue(PolicyInquiryFields.POLICY_VIEW_MODE, "WIP");
        record.setFieldValue(PolicyInquiryFields.PROCESS, "loadDependentComponent");
        record.setFieldValue(ComponentInquiryFields.COMPONENT_OWNER, "COVERAGE");
        record.setFieldValue(RiskInquiryFields.RISK_ID, riskId);
        record.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID, component.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference());
        record.setFieldValue(ComponentInquiryFields.COMPONENT_PARENT, component.getCreditSurchargeDeductibleCode().getValue());

        List <MedicalMalpracticeCoverageType> coverages = changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
        Iterator it = coverages.iterator();
        while (it.hasNext()) {
            MedicalMalpracticeCoverageType coverage = (MedicalMalpracticeCoverageType)it.next();
            if (coverage.getKey().equalsIgnoreCase(component.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference())) {
                record.setFieldValue(CoverageInquiryFields.PRODUCT_COVERAGE_CODE, coverage.getMedicalMalpracticeCoverageCode().getValue());
                List<MedicalMalpracticeCoverageVersionType> covVersions= coverage.getMedicalMalpracticeCoverageVersion();
                Iterator itCovVer = covVersions.iterator();
                while (itCovVer.hasNext()) {
                    MedicalMalpracticeCoverageVersionType covVersion = (MedicalMalpracticeCoverageVersionType) itCovVer.next();
                    record.setFieldValue(CoverageInquiryFields.COVERAGE_BASE_EFFECTIVE_FROM_DATE,
                        DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getStartDate()));
                    record.setFieldValue(CoverageInquiryFields.COVERAGE_BASE_EFFECTIVE_TO_DATE,
                        DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
                    record.setFieldValue(CoverageInquiryFields.COVERAGE_ID, covVersion.getMedicalMalpracticeCoverageVersionId());
                    record.setFieldValue(CoverageInquiryFields.COVERAGE_STATUS, covVersion.getMedicalMalpracticeCoverageStatusCode());
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInputRecordForDependentComponents", record);
        }

        return record;
    }

    private void addChangedComponentVersionToRecordSet(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                                       MedicalMalpracticePolicyType changedPolicy,
                                                       CreditSurchargeDeductibleType component, CreditSurchargeDeductibleVersionType compVersion,
                                                       RecordSet changes, Record dbComponent) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addChangedComponentVersionToRecordSet", new Object[]{policyHeader, originalPolicy, changedPolicy, component, compVersion, changes});
        }

        Record record = setInputComponentToRecord(policyHeader, originalPolicy, changedPolicy, component, compVersion);
        if (isNewTransactionB()) {
            if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
                record.setFieldValue(ComponentInquiryFields.RECORD_MODE_CODE, RecordMode.REQUEST);
            }
            else {
                record.setFieldValue(ComponentInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
            }
        }
        else {
            if (compVersion.getCreditSurchargeDeductibleVersionDetail() != null &&
                RecordMode.OFFICIAL.equals(compVersion.getCreditSurchargeDeductibleVersionDetail().getVersionModeCode())) {
                record.setFieldValue(ComponentInquiryFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
            }
            else {
                record.setFieldValue(ComponentInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
            }
        }
        record.setUpdateIndicator(UpdateIndicator.UPDATED);

        record.setFields(dbComponent, false);
        changes.addRecord(record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addChangedComponentVersionToRecordSet");
        }

    }

    private void addNewComponentVersionToRecordSet(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                                   MedicalMalpracticePolicyType changedPolicy, CreditSurchargeDeductibleType component,
                                                   CreditSurchargeDeductibleVersionType compVersion, RecordSet changes, RecordSet dbComp) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addNewComponentVersionToRecordSet", new Object[]{originalPolicy, component, compVersion, changes, dbComp});
        }

        if (!StringUtils.isBlank(component.getCreditSurchargeDeductibleNumberId())) {
            String errorMsg = "Request for new Component should not have CreditSurchargeDeductibleNumberId provided.";
            AppException ae = new AppException("ws.policy.change.invalid.update", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "addNewComponentVersionToRecordSet", ae);
            throw ae;
        }

        Record inputRecord = setInputComponentToRecord(policyHeader, originalPolicy, changedPolicy, component, compVersion);

        Record defaultDbComponents = null;
        if (dbComp != null) {
            defaultDbComponents = handleDefaultDbComponents(policyHeader, inputRecord, dbComp);
        }

        Record saveValuesRecord = null;
        if (defaultDbComponents != null) {
            // Reuse the already created default component record.
            saveValuesRecord = defaultDbComponents;
        }
        else {
            // Get the initial value for a component when is to be added.
            saveValuesRecord = getComponentManager().getInitialValuesForAddComponent(policyHeader, inputRecord);
        }

        String componentNumberId = saveValuesRecord.getStringValue(ComponentInquiryFields.POLICY_COV_COMPONENT_ID); // pk
        String componentId = saveValuesRecord.getStringValue(ComponentInquiryFields.COMPONENT_NUMBER_ID); //Base record id

        component.setCreditSurchargeDeductibleNumberId(componentId);
        compVersion.setCreditSurchargeDeductibleVersionId(componentNumberId);

        if (isNewPolicyB()) {
            inputRecord.setFieldValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getContractPeriod().getStartDate()));
        }
        else if (isNewTransactionB()) {
            inputRecord.setFieldValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()));
        }

        mergeComponentRecords(inputRecord, saveValuesRecord);

        saveValuesRecord.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
        if (defaultDbComponents != null) {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
        }
        else {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.INSERTED);
        }

        if (component.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference() != null &&
            saveValuesRecord.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID) != null &&
            !component.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference().equalsIgnoreCase(saveValuesRecord.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID))) {
            saveValuesRecord.setFieldValue(ComponentInquiryFields.NEW_COVERAGE_BASE_RECORD_ID, saveValuesRecord.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID));
        }

        saveValuesRecord.setFieldValue(ComponentInquiryFields.OFFICIAL_RECORD_ID, null);

        changes.addRecord(saveValuesRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addNewComponentVersionToRecordSet", changes);
        }
    }

    private Record setInputComponentToRecord(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                             MedicalMalpracticePolicyType changedPolicy, CreditSurchargeDeductibleType component,
                                             CreditSurchargeDeductibleVersionType compVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputComponentToRecord", new Object[]{policyHeader, originalPolicy, changedPolicy, component, compVersion});
        }

        Record record = new Record();
        record.setFieldValue(PolicyInquiryFields.POLICY_ID, policyHeader.getPolicyNo());
        record.setFieldValue(PolicyInquiryFields.POLICY_NO, policyHeader.getPolicyId());
        record.setFieldValue(PolicyInquiryFields.POLICY_VIEW_MODE, PolicyInquiryFields.WIP);
        record.setFieldValue(PolicyInquiryFields.PROCESS, "getInitialValuesForAddComponent");

        String coverageId = component.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference();
        List <MedicalMalpracticeCoverageType> coverages = null;

        if (changedPolicy.getMedicalMalpracticeLineOfBusiness()!=null) {
            coverages = changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
        }

        if (coverages!=null) {
            Iterator covIt = coverages.iterator();
            while (covIt.hasNext()) {
                MedicalMalpracticeCoverageType coverage = (MedicalMalpracticeCoverageType)covIt.next();
                if (coverage.getKey().equalsIgnoreCase(coverageId)) {
                    record.setFieldValue(RiskInquiryFields.RISK_ID, coverage.getReferredInsured().getInsuredReference());
                    record.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID, coverage.getCoverageNumberId());
                    record.setFieldValue(CoverageInquiryFields.COVERAGE_ID, coverageId);
                    if (coverage.getMedicalMalpracticeCoverageCode()!=null)
                        record.setFieldValue(CoverageInquiryFields.PRODUCT_COVERAGE_CODE, coverage.getMedicalMalpracticeCoverageCode().getValue());
                    List<MedicalMalpracticeCoverageVersionType> coverageVersion = coverage.getMedicalMalpracticeCoverageVersion();
                    Iterator covVerIt = coverageVersion.iterator();
                    while (covVerIt.hasNext()) {
                        MedicalMalpracticeCoverageVersionType covVersion = (MedicalMalpracticeCoverageVersionType)covVerIt.next();
                        record.setFieldValue(CoverageInquiryFields.COVERAGE_STATUS, covVersion.getMedicalMalpracticeCoverageStatusCode());
                        if (covVersion.getEffectivePeriod()!=null) {
                            record.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getStartDate()));
                            record.setFieldValue(CoverageInquiryFields.EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
                            record.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
                            record.setFieldValue(ComponentInquiryFields.LATEST_COVERAGE_EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
                        }
                        record.setFieldValue(ComponentInquiryFields.COMPONENT_OWNER, "COVERAGE");
                        record.setFieldValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID, component.getCreditSurchargeDeductibleCodeNumberId());
                        break;
                    }
                    if (compVersion.getEffectivePeriod()!=null) {
                        record.setFieldValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE, DateUtils.parseXMLDateToOasisDate(compVersion.getEffectivePeriod().getStartDate()));
                        record.setFieldValue(ComponentInquiryFields.COMPONENT_EFF_TO_DATE, DateUtils.parseXMLDateToOasisDate(compVersion.getEffectivePeriod().getEndDate()));
                    }
                    record.setFieldValue(ComponentInquiryFields.COMPONENT_VALUE, StringUtils.validateNumeric(compVersion.getNumericValue()));
                    record.setFieldValue(ComponentInquiryFields.INCIDENT_VALUE, compVersion.getIncidentDeductibleNumericValue());
                    record.setFieldValue(ComponentInquiryFields.AGGREGATE_VALUE, compVersion.getAggregateDeductibleNumericValue());
                    record.setFieldValue(ComponentInquiryFields.COMPONENT_CYCLE_DATE, DateUtils.parseXMLDateToOasisDate(compVersion.getCycleDate()));
                    record.setFieldValue(ComponentInquiryFields.TO_PRORATE_B, YesNoEmptyFlag.getInstance(compVersion.getProrateIndicator()).getName());
                    record.setFieldValue(ComponentInquiryFields.CLASSIFICATION_CODE, compVersion.getClassificationCode());
                    record.setFieldValue(ComponentInquiryFields.NOTE, compVersion.getAdditionalNotes());

                    if (compVersion.getAdditionalInformation()!=null) {
                        if (compVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                            for (int i = compVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                                compVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                            }
                        }
                        record.setFieldValue(ComponentInquiryFields.CHAR1, compVersion.getAdditionalInformation().getAdditionalData().get(0).getValue());
                        record.setFieldValue(ComponentInquiryFields.CHAR2, compVersion.getAdditionalInformation().getAdditionalData().get(1).getValue());
                        record.setFieldValue(ComponentInquiryFields.CHAR3, compVersion.getAdditionalInformation().getAdditionalData().get(2).getValue());

                        if (compVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                            for (int i = compVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                                compVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                            }
                        }
                        record.setFieldValue(ComponentInquiryFields.NUM1, StringUtils.validateNumeric(compVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue()));
                        record.setFieldValue(ComponentInquiryFields.NUM2, StringUtils.validateNumeric(compVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue()));
                        record.setFieldValue(ComponentInquiryFields.NUM3, StringUtils.validateNumeric(compVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue()));

                        if (compVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                            for (int i = compVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                                compVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                            }
                        }
                        record.setFieldValue(ComponentInquiryFields.DATE1, DateUtils.parseXMLDateToOasisDate(compVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()));
                        record.setFieldValue(ComponentInquiryFields.DATE2, DateUtils.parseXMLDateToOasisDate(compVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()));
                        record.setFieldValue(ComponentInquiryFields.DATE3, DateUtils.parseXMLDateToOasisDate(compVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue()));
                    }
                    break;
                }
            }
        }

        if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode()) &&
            RecordMode.OFFICIAL.equals(compVersion.getCreditSurchargeDeductibleVersionDetail().getVersionModeCode())) {
            String oosComponentId = getComponentManager().getComponentSequenceId();
            record.setFieldValue(ComponentInquiryFields.POLICY_COV_COMPONENT_ID, oosComponentId);
            record.setFieldValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputComponentToRecord", record);
        }

        return record;
    }

    private void validateForChangeComponent(CreditSurchargeDeductibleType originalComponent, CreditSurchargeDeductibleType component) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeComponent", new Object[]{originalComponent, component});
        }

        String errorMsg = "";
        List<CreditSurchargeDeductibleVersionType> originalVersions = originalComponent.getCreditSurchargeDeductibleVersion();
        List<CreditSurchargeDeductibleVersionType> currentVersions = component.getCreditSurchargeDeductibleVersion();

        for (CreditSurchargeDeductibleVersionType originalVersion : originalVersions) {
            for (CreditSurchargeDeductibleVersionType currentVersion : currentVersions) {
                if (originalVersion.getCreditSurchargeDeductibleVersionId().equalsIgnoreCase(currentVersion.getCreditSurchargeDeductibleVersionId())) {
                    if (currentVersion.getEffectivePeriod() != null && !StringUtils.isBlank(currentVersion.getEffectivePeriod().getStartDate()) &&
                        originalVersion.getEffectivePeriod() != null && !StringUtils.isBlank(originalVersion.getEffectivePeriod().getStartDate()) &&
                        !currentVersion.getEffectivePeriod().getStartDate().equalsIgnoreCase(originalVersion.getEffectivePeriod().getStartDate())) {
                        errorMsg = errorMsg + "EffectivePeriod/StartDate, ";
                    }

                    if (errorMsg != "") {
                        errorMsg = errorMsg.substring(0, errorMsg.length()-2);
                        AppException ae = new AppException("ws.policy.change.existing.component.version", "", new String[]{errorMsg, originalComponent.getCreditSurchargeDeductibleCodeNumberId()});
                        l.throwing(getClass().getName(), "validateForChangeRiskVersion", ae);
                        throw ae;
                    }
                }
            }
        }

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policy.change.existing.component", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForChangeComponent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeComponent");
        }
    }

    private void validateForChangeComponentVersion(PolicyHeader policyHeader,
                                                   CreditSurchargeDeductibleVersionType origComponentVersion,
                                                   CreditSurchargeDeductibleVersionType componentVersion,
                                                   Record dbComponentRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeComponentVersion",
                new Object[]{policyHeader, origComponentVersion, componentVersion, dbComponentRecord});
        }

        if (TransactionCode.OOSENDORSE.equals(policyHeader.getLastTransactionInfo().getTransactionCode())) {
            if (RecordMode.TEMP.equals(componentVersion.getCreditSurchargeDeductibleVersionDetail().getVersionModeCode()) &&
                !StringUtils.isBlank(componentVersion.getCreditSurchargeDeductibleVersionDetail().getParentVersionNumberId())) {
                AppException ae = new AppException("pm.oose.modified.record.exist.error2", "",
                    new String[]{"component"});
                l.throwing(getClass().getName(), "validateForChangeComponentVersion", ae);
                throw ae;
            }
        }

        if(dbComponentRecord == null) {
            AppException ae = new AppException("ws.policy.change.version.not.exist", "",
                new String[]{"Component Version " + componentVersion.getCreditSurchargeDeductibleVersionId()});
            l.throwing(getClass().getName(), "validateForChangeComponentVersion", ae);
            throw ae;
        }

        String errorMsg = "";
        if (!origComponentVersion.getEffectivePeriod().getStartDate().equalsIgnoreCase(componentVersion.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";
        if (!origComponentVersion.getEffectivePeriod().getEndDate().equalsIgnoreCase(componentVersion.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";
        if (!origComponentVersion.getCreditSurchargeDeductibleVersionDetail().getVersionModeCode().equalsIgnoreCase(componentVersion.getCreditSurchargeDeductibleVersionDetail().getVersionModeCode()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleVersionDetail/VersionModeCode, ";
        if (!origComponentVersion.getCreditSurchargeDeductibleVersionDetail().getAfterImageIndicator().equalsIgnoreCase(componentVersion.getCreditSurchargeDeductibleVersionDetail().getAfterImageIndicator()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleVersionDetail/AfterImageIndicator, ";
        if (!origComponentVersion.getCreditSurchargeDeductibleVersionDetail().getParentVersionNumberId().equalsIgnoreCase(componentVersion.getCreditSurchargeDeductibleVersionDetail().getParentVersionNumberId()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleVersionDetail/ParentVersionNumberId, ";
        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length() - 2);
            AppException ae = new AppException("ws.policy.change.existing.component.version", "",
                new String[]{errorMsg, componentVersion.getCreditSurchargeDeductibleVersionId()});
            l.throwing(getClass().getName(), "validateForChangeComponentVersion", ae);
            throw ae;
        }

        Date versionEff = DateUtils.parseXMLDate(componentVersion.getEffectivePeriod().getStartDate());
        Date versionExp = DateUtils.parseXMLDate(componentVersion.getEffectivePeriod().getEndDate());
        Date transactionDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        if (transactionDate.before(versionEff) ||
            transactionDate.after(versionExp) ||
            transactionDate.equals(versionExp)) {
            AppException ae = new AppException("ws.policy.change.version.invalid", "",
                new String[]{"Component Version: " + componentVersion.getCreditSurchargeDeductibleVersionId()});
            l.throwing(getClass().getName(), "validateForChangeComponentVersion", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeComponentVersion");
        }
    }

    private void mergeComponentRecords(Record source, Record target) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mergeComponentRecords", new Object[]{source, target});
        }

        if (source.hasStringValue(ComponentInquiryFields.POLICY_COV_COMPONENT_ID) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.POLICY_COV_COMPONENT_ID)))
            target.setFieldValue(ComponentInquiryFields.POLICY_COV_COMPONENT_ID, source.getStringValue(ComponentInquiryFields.POLICY_COV_COMPONENT_ID));
        if (source.hasStringValue(ComponentInquiryFields.COMPONENT_NUMBER_ID) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COMPONENT_NUMBER_ID)))
            target.setFieldValue(ComponentInquiryFields.COMPONENT_NUMBER_ID, source.getStringValue(ComponentInquiryFields.COMPONENT_NUMBER_ID));
        if (source.hasStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID)))
            target.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID, source.getStringValue(ComponentInquiryFields.COVERAGE_BASE_RECORD_ID));
        if (source.hasStringValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID)))
            target.setFieldValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID, source.getStringValue(ComponentInquiryFields.PRODUCT_COV_COMPONENT_ID));
        if (source.hasStringValue(ComponentInquiryFields.COMPONENT_TYPE_CODE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COMPONENT_TYPE_CODE)))
            target.setFieldValue(ComponentInquiryFields.COMPONENT_TYPE_CODE, source.getStringValue(ComponentInquiryFields.COMPONENT_TYPE_CODE));
        if (source.hasStringValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE)))
            target.setFieldValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE, source.getStringValue(ComponentInquiryFields.COMPONENT_EFF_FROM_DATE));
        if (source.hasStringValue(ComponentInquiryFields.COMPONENT_EFF_TO_DATE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COMPONENT_EFF_TO_DATE)))
            target.setFieldValue(ComponentInquiryFields.COMPONENT_EFF_TO_DATE, source.getStringValue(ComponentInquiryFields.COMPONENT_EFF_TO_DATE));
        if (source.hasStringValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_FROM_DATE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_FROM_DATE)))
            target.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_FROM_DATE, source.getStringValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_FROM_DATE));
        if (source.hasStringValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_TO_DATE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_TO_DATE)))
            target.setFieldValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_TO_DATE, source.getStringValue(ComponentInquiryFields.COVERAGE_BASE_EFFECTIVE_TO_DATE));
        if (source.hasStringValue(ComponentInquiryFields.LATEST_COVERAGE_EFFECTIVE_TO_DATE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.LATEST_COVERAGE_EFFECTIVE_TO_DATE)))
            target.setFieldValue(ComponentInquiryFields.LATEST_COVERAGE_EFFECTIVE_TO_DATE, source.getStringValue(ComponentInquiryFields.LATEST_COVERAGE_EFFECTIVE_TO_DATE));
        if (source.hasStringValue(ComponentInquiryFields.COMPONENT_VALUE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COMPONENT_VALUE)))
            target.setFieldValue(ComponentInquiryFields.COMPONENT_VALUE, StringUtils.validateNumeric(source.getStringValue(ComponentInquiryFields.COMPONENT_VALUE)));
        if (source.hasStringValue(ComponentInquiryFields.INCIDENT_VALUE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.INCIDENT_VALUE)))
            target.setFieldValue(ComponentInquiryFields.INCIDENT_VALUE, source.getStringValue(ComponentInquiryFields.INCIDENT_VALUE));
        if (source.hasStringValue(ComponentInquiryFields.AGGREGATE_VALUE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.AGGREGATE_VALUE)))
            target.setFieldValue(ComponentInquiryFields.AGGREGATE_VALUE, source.getStringValue(ComponentInquiryFields.AGGREGATE_VALUE));
        if (source.hasStringValue(ComponentInquiryFields.COMPONENT_CYCLE_DATE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COMPONENT_CYCLE_DATE)))
            target.setFieldValue(ComponentInquiryFields.COMPONENT_CYCLE_DATE, source.getStringValue(ComponentInquiryFields.COMPONENT_CYCLE_DATE));
        if (source.hasStringValue(ComponentInquiryFields.TO_PRORATE_B) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.TO_PRORATE_B)))
            target.setFieldValue(ComponentInquiryFields.TO_PRORATE_B, source.getStringValue(ComponentInquiryFields.TO_PRORATE_B));
        if (source.hasStringValue(ComponentInquiryFields.CLASSIFICATION_CODE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.CLASSIFICATION_CODE)))
            target.setFieldValue(ComponentInquiryFields.CLASSIFICATION_CODE, source.getStringValue(ComponentInquiryFields.CLASSIFICATION_CODE));
        if (source.hasStringValue(ComponentInquiryFields.NOTE) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.NOTE)))
            target.setFieldValue(ComponentInquiryFields.NOTE, source.getStringValue(ComponentInquiryFields.NOTE));
        if (source.hasStringValue(ComponentInquiryFields.COMPONENT_OWNER) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.COMPONENT_OWNER)))
            target.setFieldValue(ComponentInquiryFields.COMPONENT_OWNER, source.getStringValue(ComponentInquiryFields.COMPONENT_OWNER));
        if (source.hasStringValue(ComponentInquiryFields.CHAR1) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.CHAR1)))
            target.setFieldValue(ComponentInquiryFields.CHAR1, source.getStringValue(ComponentInquiryFields.CHAR1));
        if (source.hasStringValue(ComponentInquiryFields.CHAR2) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.CHAR2)))
            target.setFieldValue(ComponentInquiryFields.CHAR2, source.getStringValue(ComponentInquiryFields.CHAR2));
        if (source.hasStringValue(ComponentInquiryFields.CHAR3) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.CHAR3)))
            target.setFieldValue(ComponentInquiryFields.CHAR3, source.getStringValue(ComponentInquiryFields.CHAR3));
        if (source.hasStringValue(ComponentInquiryFields.NUM1) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.NUM1)))
            target.setFieldValue(ComponentInquiryFields.NUM1, StringUtils.validateNumeric(source.getStringValue(ComponentInquiryFields.NUM1)));
        if (source.hasStringValue(ComponentInquiryFields.NUM2) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.NUM2)))
            target.setFieldValue(ComponentInquiryFields.NUM2, StringUtils.validateNumeric(source.getStringValue(ComponentInquiryFields.NUM2)));
        if (source.hasStringValue(ComponentInquiryFields.NUM3) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.NUM3)))
            target.setFieldValue(ComponentInquiryFields.NUM3, StringUtils.validateNumeric(source.getStringValue(ComponentInquiryFields.NUM3)));
        if (source.hasStringValue(ComponentInquiryFields.DATE1) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.DATE1)))
            target.setFieldValue(ComponentInquiryFields.DATE1, source.getStringValue(ComponentInquiryFields.DATE1));
        if (source.hasStringValue(ComponentInquiryFields.DATE2) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.DATE2)))
            target.setFieldValue(ComponentInquiryFields.DATE2, source.getStringValue(ComponentInquiryFields.DATE2));
        if (source.hasStringValue(ComponentInquiryFields.DATE3) && !StringUtils.isBlank(source.getStringValue(ComponentInquiryFields.DATE3)))
            target.setFieldValue(ComponentInquiryFields.DATE3, source.getStringValue(ComponentInquiryFields.DATE3));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mergeComponentRecords");
        }
    }

    private RecordSet getNewOrChangedSubCoverages(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                                  MedicalMalpracticePolicyType changedPolicy, String riskId, RecordSet dbSubCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNewOrChangedSubCoverages", new Object[]{policyHeader, originalPolicy, changedPolicy, riskId, dbSubCoverages});
        }

        RecordSet recordSet = new RecordSet();
        if (changedPolicy != null && changedPolicy.getMedicalMalpracticeLineOfBusiness() !=null) {
            List <MedicalMalpracticeCoverageType> coverages = changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            Iterator it = coverages.iterator();
            while (it.hasNext()) {
                MedicalMalpracticeCoverageType coverage = (MedicalMalpracticeCoverageType)it.next();
                String parentCoverageId = coverage.getParentCoverageNumberId();
                if (StringUtils.isBlank(parentCoverageId))
                    continue;

                if (coverage.getReferredInsured()==null || coverage.getReferredInsured().getInsuredReference()==null) {
                    String errorMsg = "ReferredInsured or InsuredReference ";
                    AppException ae = new AppException("ws.policy.change.new.coverage.version", "", new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getNewOrChangedSubCoverages", ae);
                    throw ae;
                }

                if (coverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(riskId)) {
                    RecordSet rs = getSubCoverageChanges(policyHeader, coverage, originalPolicy, changedPolicy, dbSubCoverages);
                    if (rs.getSize()>0) {
                        recordSet.addRecords(rs);
                    }
                }
            }
        }

        //Find the records that need to be deleted.
        if (originalPolicy != null && originalPolicy.getMedicalMalpracticeLineOfBusiness() != null) {
            List<MedicalMalpracticeCoverageType> origCoverages =
                originalPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            for (MedicalMalpracticeCoverageType origCoverage : origCoverages) {
                if (StringUtils.isBlank(origCoverage.getParentCoverageNumberId())) {
                    continue;
                }
                if (origCoverage.getReferredInsured() == null ||
                    origCoverage.getReferredInsured().getInsuredReference() == null) {
                    String errorMsg = "ReferredInsured or InsuredReference ";
                    AppException ae = new AppException("ws.policy.change.new.coverage.version",
                        "", new String[]{errorMsg});
                    l.throwing(getClass().getName(), "getNewOrChangedSubCoverages", ae);
                    throw ae;
                }
                RecordSet deletedRs = null;
                if (origCoverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(riskId)) {
                    deletedRs =
                        getDeletedCoverages(policyHeader, origCoverage, changedPolicy, originalPolicy, dbSubCoverages, true);
                }
                if (deletedRs != null && deletedRs.getSize() > 0) {
                    recordSet.addRecords(deletedRs);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNewOrChangedSubCoverages", recordSet);
        }
        return recordSet;
    }

    private String getParentCoveragePk(List <MedicalMalpracticeCoverageType> coverages, String parentCoverageId) {
        String parentPk = null;
        for (MedicalMalpracticeCoverageType coverage : coverages) {
            if (coverage.getKey().equals(parentCoverageId)) {
                parentPk = coverage.getCoverageNumberId();
                break;
            }
        }
        return parentPk;
    }

    private RecordSet getSubCoverageChanges(PolicyHeader policyHeader, MedicalMalpracticeCoverageType subCoverage, MedicalMalpracticePolicyType originalPolicy,
                                            MedicalMalpracticePolicyType changedPolicy, RecordSet dbSubCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSubCoverageChanges", new Object[]{policyHeader, subCoverage, originalPolicy, changedPolicy, dbSubCoverages});
        }

        boolean isCoverageFound = false;

        RecordSet changes = new RecordSet();
        if (originalPolicy != null) {
            List <MedicalMalpracticeCoverageType> origCoverages = originalPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            Iterator it = origCoverages.iterator();
            while (it.hasNext()) {
                MedicalMalpracticeCoverageType origCoverage = (MedicalMalpracticeCoverageType)it.next();
                String parentOrigCoverageId = origCoverage.getParentCoverageNumberId();
                // We only process here sub-coverages, using parent id to skip coverage.
                if (StringUtils.isBlank(parentOrigCoverageId))
                    continue;

                if (!StringUtils.isBlank(subCoverage.getKey()) && subCoverage.getKey().equalsIgnoreCase(origCoverage.getKey())) {
                    isCoverageFound = true;
                    validateForChangeSubCoverage(origCoverage, subCoverage);

                    policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, parentOrigCoverageId);

                    List<MedicalMalpracticeCoverageVersionType> subCoverageVersion = subCoverage.getMedicalMalpracticeCoverageVersion();
                    List<MedicalMalpracticeCoverageVersionType> origSubCoverageVersion = origCoverage.getMedicalMalpracticeCoverageVersion();
                    Iterator subCovIt = subCoverageVersion.iterator();
                    Iterator origSubCovIt = origSubCoverageVersion.iterator();

                    while (subCovIt.hasNext()) {
                        boolean versionFound = false;
                        MedicalMalpracticeCoverageVersionType subCovVersion = (MedicalMalpracticeCoverageVersionType)subCovIt.next();
                        while (origSubCovIt.hasNext()) {
                            MedicalMalpracticeCoverageVersionType origCovVersion = (MedicalMalpracticeCoverageVersionType)origSubCovIt.next();
                            if (!StringUtils.isBlank(subCovVersion.getMedicalMalpracticeCoverageVersionId()) &&
                                subCovVersion.getMedicalMalpracticeCoverageVersionId().equalsIgnoreCase(origCovVersion.getMedicalMalpracticeCoverageVersionId())) {
                                if (isSubCoverageVersionChanged(subCovVersion, origCovVersion)) {
                                    RecordSet subRecordSet = dbSubCoverages.getSubSet(new RecordFilter(CoverageInquiryFields.COVERAGE_CLASS_ID, subCovVersion.getMedicalMalpracticeCoverageVersionId()));
                                    Record dbSubCoverage = null;
                                    if (subRecordSet != null && subRecordSet.getSize() > 0) {
                                        dbSubCoverage = subRecordSet.getRecord(0);
                                    }
                                    validateForChangeSubCoverageVersion(policyHeader, origCovVersion, subCovVersion, dbSubCoverage);
                                    addChangedCoverageVersionToRecordSet(policyHeader, originalPolicy, subCoverage, subCovVersion, changes, dbSubCoverage);
                                }
                                versionFound = true;
                                break;
                            }
                        }
                        if (!versionFound) {
                            validateForNewCoverageVersion(subCoverage, subCovVersion);
                            addNewSubCoverageVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, subCoverage, subCovVersion, changes, dbSubCoverages);
                        }
                    }
                }
                if (isCoverageFound) {
                    break;
                }
            }
        }

        if (!isCoverageFound) {
            // New Sub Coverage
            List<MedicalMalpracticeCoverageVersionType> coverageVersion = subCoverage.getMedicalMalpracticeCoverageVersion();
            List <MedicalMalpracticeCoverageType> coverages = changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage();
            String parentCoverageId = getParentCoveragePk(coverages, subCoverage.getParentCoverageNumberId());
            policyHeader = getCoverageManager().loadCoverageHeader(policyHeader, parentCoverageId);

            Iterator covIt = coverageVersion.iterator();
            while (covIt.hasNext()) {
                MedicalMalpracticeCoverageVersionType subCovVersion = (MedicalMalpracticeCoverageVersionType)covIt.next();
                validateForNewCoverageVersion(subCoverage, subCovVersion);
                addNewSubCoverageVersionToRecordSet(policyHeader, originalPolicy, changedPolicy, subCoverage, subCovVersion, changes, dbSubCoverages);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSubCoverageChanges");
        }

        return changes;
    }

    private void addNewSubCoverageVersionToRecordSet(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                                     MedicalMalpracticePolicyType changedPolicy, MedicalMalpracticeCoverageType coverage,
                                                     MedicalMalpracticeCoverageVersionType covVersion, RecordSet changes, RecordSet dbSubCoverages) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addNewSubCoverageVersionToRecordSet", new Object[]{originalPolicy, coverage, covVersion, changes, dbSubCoverages});
        }

        if (!StringUtils.isBlank(coverage.getCoverageNumberId())) {
            String errorMsg = "Request for new Coverage Class should not have CoverageNumberId provided.";
            AppException ae = new AppException("ws.policy.change.invalid.update", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "addNewSubCoverageVersionToRecordSet", ae);
            throw ae;
        }

        Record inputRecord = setInputSubCoverageToRecord(policyHeader, originalPolicy, coverage, covVersion);

        inputRecord.setFieldValue(PolicyInquiryFields.PROCESS, "getInitialValuesForCoverageClass");

        Record defaultDbSubCoverage = null;
        if (dbSubCoverages != null) {
            defaultDbSubCoverage = handleDefaultDbSubCoverage(policyHeader, inputRecord, dbSubCoverages);
        }

        Record saveValuesRecord = null;
        if (defaultDbSubCoverage != null) {
            // Reuse the already created default coverage record.
            saveValuesRecord = defaultDbSubCoverage;
        }
        else {
            // Get the initial value for a coverage when is to be added.
            saveValuesRecord = getCoverageClassManager().getInitialValuesForCoverageClass(policyHeader, inputRecord);
        }

        String coverageClassNumberId = saveValuesRecord.getStringValue(CoverageInquiryFields.COVERAGE_CLASS_BASE_ID);
        String coverageClassId = saveValuesRecord.getStringValue(CoverageInquiryFields.COVERAGE_CLASS_ID);

        coverage.setCoverageNumberId(coverageClassNumberId);
        covVersion.setMedicalMalpracticeCoverageVersionId(coverageClassId);

        if (isNewPolicyB()) {
            inputRecord.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getContractPeriod().getStartDate()));
        }
        else if (isNewTransactionB()) {
            inputRecord.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(changedPolicy.getTransactionDetail().getTransactionEffectiveDate()));
        }

        mergeCoverageRecords(inputRecord, saveValuesRecord);

        saveValuesRecord.setFieldValue(RiskInquiryFields.RECORD_MODE_CODE, RecordMode.TEMP);
        if (defaultDbSubCoverage != null) {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.UPDATED);
        }
        else {
            saveValuesRecord.setUpdateIndicator(UpdateIndicator.INSERTED);
        }

        changes.addRecord(saveValuesRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addNewSubCoverageVersionToRecordSet");
        }

    }

    private Record setInputSubCoverageToRecord(PolicyHeader policyHeader, MedicalMalpracticePolicyType originalPolicy,
                                               MedicalMalpracticeCoverageType coverage, MedicalMalpracticeCoverageVersionType covVersion) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputSubCoverageToRecord", new Object[]{policyHeader, originalPolicy, coverage, covVersion});
        }

        Record record = new Record();

        record.setFieldValue(PolicyInquiryFields.POLICY_ID, policyHeader.getPolicyNo());
        record.setFieldValue(PolicyInquiryFields.POLICY_NO, policyHeader.getPolicyId());
        if (coverage.getReferredInsured()!=null)
            record.setFieldValue(RiskInquiryFields.RISK_NUMBER_ID, coverage.getReferredInsured().getInsuredReference());
        record.setFieldValue(CoverageInquiryFields.COVERAGE_NUMBER_ID, coverage.getCoverageNumberId());
        record.setFieldValue(CoverageInquiryFields.PARENT_COVERAGE_NUMBER_ID, coverage.getParentCoverageNumberId());
        if (coverage.getMedicalMalpracticeCoverageCode()!=null)
            record.setFieldValue(CoverageInquiryFields.PRODUCT_COVERAGE_CLASS_CODE, coverage.getMedicalMalpracticeCoverageCode().getValue());
        record.setFieldValue(CoverageInquiryFields.COVERAGE_ID, covVersion.getMedicalMalpracticeCoverageVersionId());
        if (covVersion.getPrimaryIndicator()!=null)
            record.setFieldValue(CoverageInquiryFields.PRIMARY_COVERAGE_INDICATOR, YesNoEmptyFlag.getInstance(covVersion.getPrimaryIndicator()).getName());
        if (covVersion.getEffectivePeriod()!=null) {
            record.setFieldValue(CoverageInquiryFields.EFFECTIVE_FROM_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getStartDate()));
            record.setFieldValue(CoverageInquiryFields.EFFECTIVE_TO_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getEffectivePeriod().getEndDate()));
        }
        if (covVersion.getLimit()!=null) {
            if (covVersion.getLimit().getLimitTypeCode() != null)
                record.setFieldValue(CoverageInquiryFields.COVERAGE_LIMIT_CODE, covVersion.getLimit().getLimitTypeCode().getValue());
            if (covVersion.getLimit().getSharedLimitIndicator()!=null)
                record.setFieldValue(CoverageInquiryFields.SHARED_LIMIT_B, YesNoEmptyFlag.getInstance(covVersion.getLimit().getSharedLimitIndicator()).getName());
            if (covVersion.getLimit().getIncidentLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.INCIDENT_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getIncidentLimitTypeCode().getValue()));
            if (covVersion.getLimit().getAgregateLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.AGGREGATE_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getAgregateLimitTypeCode().getValue()));
            if (covVersion.getLimit().getSubLimitIndicator()!=null)
                record.setFieldValue(CoverageInquiryFields.PRODUCT_SUBLIMIT_B, YesNoEmptyFlag.getInstance(covVersion.getLimit().getSubLimitIndicator()).getName());
            record.setFieldValue(CoverageInquiryFields.LIMIT_EROSION_CODE, covVersion.getLimit().getErosionTypeCode());
            if (covVersion.getLimit().getManualIncidentLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.MANUAL_INCIDENT_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getManualIncidentLimitTypeCode().getValue()));
            if (covVersion.getLimit().getManualAggregateLimitTypeCode()!=null)
                record.setFieldValue(CoverageInquiryFields.MANUAL_AGGREGATE_LIMIT, StringUtils.validateNumeric(covVersion.getLimit().getManualAggregateLimitTypeCode().getValue()));
            record.setFieldValue(CoverageInquiryFields.PER_DAY_LIMIT,StringUtils.validateNumeric(covVersion.getLimit().getDailyLimitAmount()));
        }
        if (covVersion.getClaimMadeLiabilityPolicyInformation()!=null) {
            record.setFieldValue(CoverageInquiryFields.RETROACTIVE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getClaimMadeLiabilityPolicyInformation().getCurrentRetroactiveDate()));
            record.setFieldValue(CoverageInquiryFields.SPLIT_RETROACTIVE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getClaimMadeLiabilityPolicyInformation().getSplitRetroactiveDate()));
            record.setFieldValue(CoverageInquiryFields.CLAIMS_MADE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getClaimMadeLiabilityPolicyInformation().getClaimsMadeDate()));
        }
        record.setFieldValue(CoverageInquiryFields.RATE_PAYOR_DEPEND_CODE, covVersion.getPayorCode());
        record.setFieldValue(CoverageInquiryFields.CANCELLATION_METHOD_CODE, covVersion.getCancellationMethodCode());
        record.setFieldValue(CoverageInquiryFields.ANNUAL_BASE_RATE, StringUtils.validateNumeric(covVersion.getAnnualBaseRate()));
        record.setFieldValue(CoverageInquiryFields.DEFAULT_AMOUNT_OF_INSURANCE, covVersion.getDefaultAmountOfInsurance());
        record.setFieldValue(CoverageInquiryFields.ADDL_AMOUNT_OF_INSURANCE, covVersion.getAdditionalAmountOfInsurance());
        record.setFieldValue(CoverageInquiryFields.LOSS_OF_INCOME_DAYS, covVersion.getLossOfIncomeDays());
        record.setFieldValue(CoverageInquiryFields.EXPOSURE_UNIT, StringUtils.validateNumeric(covVersion.getExposureUnit()));
        record.setFieldValue(CoverageInquiryFields.BUILDING_RATE, covVersion.getBuildingRate());
        if (covVersion.getForecastIndicator() != null)
            record.setFieldValue(CoverageInquiryFields.USED_FOR_FORECAST_B, YesNoEmptyFlag.getInstance(covVersion.getForecastIndicator()).getName());
        if (covVersion.getDirectPrimaryIndicator() != null)
            record.setFieldValue(CoverageInquiryFields.DIRECT_PRIMARY_B, YesNoEmptyFlag.getInstance(covVersion.getDirectPrimaryIndicator()).getName());
        record.setFieldValue(CoverageInquiryFields.SYMBOL, covVersion.getAdditionalSymbolCode());
        if (covVersion.getCoverageConversionInformation()!=null) {
            record.setFieldValue(CoverageInquiryFields.CM_CONV_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getClaimsMadeDate()));
            record.setFieldValue(CoverageInquiryFields.CM_CONV_OVERRIDE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getClaimsMadeOverrideDate()));
            record.setFieldValue(CoverageInquiryFields.OC_CONV_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getOccurenceDate()));
            record.setFieldValue(CoverageInquiryFields.OC_CONV_OVERRIDE_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getCoverageConversionInformation().getOccurenceOverrideDate()));
        }
        if (covVersion.getPcf()!=null) {
            record.setFieldValue(CoverageInquiryFields.PCF_COUNTY_CODE, covVersion.getPcf().getPracticeCountyCode());
            record.setFieldValue(CoverageInquiryFields.PCF_PARTICIPATION_DATE, DateUtils.parseXMLDateToOasisDate(covVersion.getPcf().getStartDate()));
        }
        record.setFieldValue(CoverageInquiryFields.DEDUCTIBLE_COMPONENT_ID, covVersion.getDeductible());
        record.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_CODE, covVersion.getManualDeductibleSIRCode());
        record.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_INC_VALUE, StringUtils.validateNumeric(covVersion.getManualDeductibleSIRIncidentAmount()));
        record.setFieldValue(CoverageInquiryFields.MANUAL_DED_SIR_AGG_VALUE, StringUtils.validateNumeric(covVersion.getManualDeductibleSIRAggregateAmount()));
        record.setFieldValue(CoverageInquiryFields.INDEMNITY_TYPE, covVersion.getDeductibleSIRIndemnityTypeCode());

        if (covVersion.getAdditionalInformation()!=null) {
            if (covVersion.getAdditionalInformation().getAdditionalDateTime().size() < 3) {
                for (int i = covVersion.getAdditionalInformation().getAdditionalDateTime().size(); i < 3; i++) {
                    covVersion.getAdditionalInformation().getAdditionalDateTime().add(new AdditionalDateTimeType());
                }
            }
            record.setFieldValue(CoverageInquiryFields.DATE1, DateUtils.parseXMLDateToOasisDate(covVersion.getAdditionalInformation().getAdditionalDateTime().get(0).getValue()));
            record.setFieldValue(CoverageInquiryFields.DATE2, DateUtils.parseXMLDateToOasisDate(covVersion.getAdditionalInformation().getAdditionalDateTime().get(1).getValue()));
            record.setFieldValue(CoverageInquiryFields.DATE3, DateUtils.parseXMLDateToOasisDate(covVersion.getAdditionalInformation().getAdditionalDateTime().get(2).getValue()));

            if (covVersion.getAdditionalInformation().getAdditionalNumber().size() < 3) {
                for (int i = covVersion.getAdditionalInformation().getAdditionalNumber().size(); i < 3; i++) {
                    covVersion.getAdditionalInformation().getAdditionalNumber().add(new AdditionalNumberType());
                }
            }
            record.setFieldValue(CoverageInquiryFields.NUM1, StringUtils.validateNumeric(covVersion.getAdditionalInformation().getAdditionalNumber().get(0).getValue()));
            record.setFieldValue(CoverageInquiryFields.NUM2, StringUtils.validateNumeric(covVersion.getAdditionalInformation().getAdditionalNumber().get(1).getValue()));
            record.setFieldValue(CoverageInquiryFields.NUM3, StringUtils.validateNumeric(covVersion.getAdditionalInformation().getAdditionalNumber().get(2).getValue()));

            if (covVersion.getAdditionalInformation().getAdditionalData().size() < 3) {
                for (int i = covVersion.getAdditionalInformation().getAdditionalData().size(); i < 3; i++) {
                    covVersion.getAdditionalInformation().getAdditionalData().add(new AdditionalDataType());
                }
            }
            record.setFieldValue(CoverageInquiryFields.CHAR1, covVersion.getAdditionalInformation().getAdditionalData().get(0).getValue());
            record.setFieldValue(CoverageInquiryFields.CHAR2, covVersion.getAdditionalInformation().getAdditionalData().get(1).getValue());
            record.setFieldValue(CoverageInquiryFields.CHAR3, covVersion.getAdditionalInformation().getAdditionalData().get(2).getValue());
        }
        record.setFieldValue(PolicyInquiryFields.SHORT_TERM_B, "");
        record.setFieldValue(CoverageInquiryFields.COVERAGE_ID, covVersion.getMedicalMalpracticeCoverageVersionId());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputSubCoverageToRecord", record);
        }

        return record;
    }

    private void copyRequestAndUpdateNumberIds(MedicalMalpracticePolicyChangeRequestType policyChangeRequest, MedicalMalpracticePolicyChangeResultType policyChangeResult) {
        policyChangeResult.setCorrelationId(policyChangeRequest.getCorrelationId());
        policyChangeResult.setMessageId(policyChangeRequest.getMessageId());
        policyChangeResult.getMedicalMalpracticePolicy().addAll(policyChangeRequest.getMedicalMalpracticePolicy());
    }

    private void invokePartyChangeService(MedicalMalpracticePolicyChangeRequestType policyChangeRequest, MedicalMalpracticePolicyChangeResultType policyChangeResult) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "invokePartyChangeService", new Object[]{policyChangeRequest, policyChangeResult});
        }

        PartyChangeRequestType partyChangeRequest = new PartyChangeRequestType();
        partyChangeRequest.setCorrelationId(policyChangeRequest.getCorrelationId());
        partyChangeRequest.setMessageId(policyChangeRequest.getMessageId());
        partyChangeRequest.setUserId(policyChangeRequest.getUserId());

        partyChangeRequest.getAddress().addAll(policyChangeRequest.getAddress());
        partyChangeRequest.getPerson().addAll(policyChangeRequest.getPerson());
        partyChangeRequest.getOrganization().addAll(policyChangeRequest.getOrganization());
        partyChangeRequest.getProperty().addAll(policyChangeRequest.getProperty());

        if (policyChangeRequest.getDataModificationInformation()!=null &&
            policyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription()!=null) {
            com.delphi_tech.ows.policychangeservice.PreviousDataValueDescriptionType previousPolicyData = policyChangeRequest.getDataModificationInformation().getPreviousDataValueDescription();

            com.delphi_tech.ows.partychangeservice.DataModificationInformationType partyDataMod = new com.delphi_tech.ows.partychangeservice.DataModificationInformationType();
            com.delphi_tech.ows.partychangeservice.PreviousDataValueDescriptionType previousPartyData = new com.delphi_tech.ows.partychangeservice.PreviousDataValueDescriptionType();
            previousPartyData.getAddress().addAll(previousPolicyData.getAddress());
            previousPartyData.getPerson().addAll(previousPolicyData.getPerson());
            previousPartyData.getOrganization().addAll(previousPolicyData.getOrganization());
            previousPartyData.getProperty().addAll(previousPolicyData.getProperty());
            partyDataMod.setPreviousDataValueDescription(previousPartyData);
            partyChangeRequest.setDataModificationInformation(partyDataMod);
        }

        PartyChangeResultType partyChangeResult = getPartyChangeServiceManager().saveParty(partyChangeRequest);

        policyChangeResult.getAddress().addAll(partyChangeResult.getAddress());
        policyChangeResult.getPerson().addAll(partyChangeResult.getPerson());
        policyChangeResult.getOrganization().addAll(partyChangeResult.getOrganization());
        policyChangeResult.getProperty().addAll(partyChangeResult.getProperty());

        setPartyReferences(policyChangeRequest, partyChangeResult);

        l.exiting(getClass().getName(), "invokePartyChangeService");
    }

    private void setPartyReferences (MedicalMalpracticePolicyChangeRequestType policyChangeRequest, PartyChangeResultType partyChangeResult) {
        List<MedicalMalpracticePolicyType> medicalMalpracticePolicy = policyChangeRequest.getMedicalMalpracticePolicy();
        for (MedicalMalpracticePolicyType policy : medicalMalpracticePolicy) {
            com.delphi_tech.ows.policy.PolicyHolderType policyHolder = policy.getPolicyHolder();
            ProducerType producer = policy.getProducer();
            List <InsurerType> insurers = policy.getInsurer();
            List<InsuredType> insureds = policy.getInsured();

            List<PersonType> persons = partyChangeResult.getPerson();
            for (PersonType person : persons) {
                if (policyHolder != null && policyHolder.getPersonReference() != null) {
                    if (person.getKey().equalsIgnoreCase(policyHolder.getPersonReference())) {
                        policyHolder.getReferredParty().setPartyNumberId(person.getPersonNumberId());
                    }
                }

                if (producer != null && producer.getPersonReference() != null) {
                    if (person.getKey().equalsIgnoreCase(producer.getPersonReference())) {
                        producer.getReferredParty().setPartyNumberId(person.getPersonNumberId());
                    }
                }

                if (insurers != null) {
                    for (InsurerType insurer : insurers) {
                        if (insurer.getPersonReference() != null &&
                            person.getKey().equalsIgnoreCase(insurer.getPersonReference())) {
                            insurer.getReferredParty().setPartyNumberId(person.getPersonNumberId());
                        }
                    }
                }

                for (InsuredType insured : insureds) {
                    if (person.getKey().equalsIgnoreCase(insured.getPersonReference())) {
                        insured.getReferredParty().setPartyNumberId(person.getPersonNumberId());
                        break;
                    }
                }
            }

            List<OrganizationType> organizations = partyChangeResult.getOrganization();
            for (OrganizationType organization : organizations) {
                if (policyHolder != null && policyHolder.getOrganizationReference() != null) {
                    if (organization.getKey().equalsIgnoreCase(policyHolder.getOrganizationReference())) {
                        policyHolder.getReferredParty().setPartyNumberId(organization.getOrganizationNumberId());
                    }
                }

                if (producer != null && producer.getOrganizationReference() != null) {
                    if (organization.getKey().equalsIgnoreCase(producer.getOrganizationReference())) {
                        producer.getReferredParty().setPartyNumberId(organization.getOrganizationNumberId());
                    }
                }

                if (insurers != null) {
                    for (InsurerType insurer : insurers) {
                        if (insurer.getOrganizationReference() != null &&
                            organization.getKey().equalsIgnoreCase(insurer.getOrganizationReference())) {
                            insurer.getReferredParty().setPartyNumberId(organization.getOrganizationNumberId());
                        }
                    }
                }

                for (InsuredType insured : insureds) {
                    if (organization.getKey().equalsIgnoreCase(insured.getOrganizationReference())) {
                        insured.getReferredParty().setPartyNumberId(organization.getOrganizationNumberId());
                        break;
                    }
                }
            }

            List<PropertyType> properties = partyChangeResult.getProperty();
            for (PropertyType property : properties) {
                for (InsuredType insured : insureds) {
                    if (property.getKey().equalsIgnoreCase(insured.getPropertyReference())) {
                        insured.getReferredParty().setPartyNumberId(property.getPropertyNumberId());
                        break;
                    }
                }
            }
        }
    }

    private void cleanPartyResult(MedicalMalpracticePolicyChangeResultType policyChangeResult) {
        policyChangeResult.getPerson().clear();
        policyChangeResult.getAddress().clear();
        policyChangeResult.getOrganization().clear();
        policyChangeResult.getProperty().clear();
    }

    private boolean isEqual(String s1, String s2) {
        if (s1 == null)
            s1 = "";
        if (s2 == null)
            s2 = "";
        if ("true".equalsIgnoreCase(s1)) {
            s1 = "Y";
        }
        else if ("false".equalsIgnoreCase(s1)) {
            s1 = "N";
        }

        if ("true".equalsIgnoreCase(s2)) {
            s2 = "Y";
        }
        else if ("false".equalsIgnoreCase(s2)) {
            s2 = "N";
        }

        return s1.equalsIgnoreCase(s2);
    }

    public void verifyConfig() {
        if (getPartyChangeServiceManager() == null)
            throw new ConfigurationException("The required property 'partyChangeServiceManager' is missing.");
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
        if (getCoverageClassManager() == null)
            throw new ConfigurationException("The required property 'coverageClassManager' is missing.");
        if (getComponentManager() == null)
            throw new ConfigurationException("The required property 'componentManager' is missing.");
        if (getLockManager() == null)
            throw new ConfigurationException("The required property 'lockManager' is missing.");
        if (getTransactionManager() == null)
            throw new ConfigurationException("The required property 'transactionManager' is missing.");
        if (getBillingManager() == null)
            throw new ConfigurationException("The required property 'billingManager' is missing.");
        if (getAgentManager() == null)
            throw new ConfigurationException("The required property 'agentManager' is missing.");
        if(getUnderwriterManager() == null)
            throw new ConfigurationException("The required property 'underwriterManager' is missing.");
        if(getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        if (getRiskAddtlExposureManager() == null)
            throw new ConfigurationException("The required property 'riskAddtlExposureManager' is missing.");
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

    public PartyChangeServiceManager getPartyChangeServiceManager() {
        return m_partyChangeServiceManager;
    }

    public void setPartyChangeServiceManager(PartyChangeServiceManager partyChangeServiceManager) {
        m_partyChangeServiceManager = partyChangeServiceManager;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public LockManager getLockManager() {
        return m_lockManager;
    }

    public void setLockManager(LockManager lockManager) {
        m_lockManager = lockManager;
    }

    public TransactionManager getTransactionManager() {
        return m_transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        m_transactionManager = transactionManager;
    }

    public void setBillingManager(BillingManager billingManager) {
        m_billingManager = billingManager;
    }

    public BillingManager getBillingManager() {
        return m_billingManager;
    }

    public AgentManager getAgentManager() {
        return m_agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        m_agentManager = agentManager;
    }

    public UnderwriterManager getUnderwriterManager() {
        return m_underwriterManager;
    }

    public void setUnderwriterManager(UnderwriterManager underwriterManager) {
        m_underwriterManager = underwriterManager;
    }

    public RiskAddtlExposureManager getRiskAddtlExposureManager() {
        return m_RiskAddtlExposureManager;
    }

    public void setRiskAddtlExposureManager(RiskAddtlExposureManager riskAddtlExposureManager) {
        m_RiskAddtlExposureManager = riskAddtlExposureManager;
    }

    public boolean isNewTransactionB() {
        return newTransactionB;
    }

    public void setNewTransactionB(boolean newTransactionB) {
        this.newTransactionB = newTransactionB;
    }

    public boolean isNewPolicyB() {
        return newPolicyB;
    }

    public void setNewPolicyB(boolean newPolicyB) {
        this.newPolicyB = newPolicyB;
    }

    public boolean isRateActionB() {
        return rateActionB;
    }

    public void setRateActionB(boolean rateActionB) {
        this.rateActionB = rateActionB;
    }

    public boolean isIssueActionB() {
        return issueActionB;
    }

    public void setIssueActionB(boolean issueActionB) {
        this.issueActionB = issueActionB;
    }

    public boolean isDeleteWipOnFailureB() {
        return deleteWipOnFailureB;
    }

    public void setDeleteWipOnFailureB(boolean deleteWipOnFailureB) {
        this.deleteWipOnFailureB = deleteWipOnFailureB;
    }

    public boolean isIgnoreSoftValidationActionB() { return ignoreSoftValidationActionB; }

    public void setIgnoreSoftValidationActionB(Boolean ignoreSoftValidationActionB) {
        this.ignoreSoftValidationActionB = ignoreSoftValidationActionB;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public BillingAccountChangeWSClientManager getBillingAccountChangeWSClientManager() {
        return m_billingAccountChangeWSClientManager;
    }

    public void setBillingAccountChangeWSClientManager(BillingAccountChangeWSClientManager billingAccountChangeWSClientManager) {
        m_billingAccountChangeWSClientManager = billingAccountChangeWSClientManager;
    }

    public PolicyChangeServiceHelper getPolicyChangeServiceHelper() {
        return m_policyChangeServiceHelper;
    }

    public void setPolicyChangeServiceHelper(PolicyChangeServiceHelper policyChangeServiceHelper) {
        m_policyChangeServiceHelper = policyChangeServiceHelper;
    }

    public PolicyInquiryServiceHelper getPolicyInquiryServiceHelper() {
        return m_policyInquiryServiceHelper;
    }

    public void setPolicyInquiryServiceHelper(PolicyInquiryServiceHelper policyInquiryServiceHelper) {
        m_policyInquiryServiceHelper = policyInquiryServiceHelper;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    private PolicyInquiryServiceHelper m_policyInquiryServiceHelper;
    private PartyChangeServiceManager m_partyChangeServiceManager;
    private PolicyManager m_policyManager;
    private RiskManager m_riskManager;
    private CoverageManager m_coverageManager;
    private CoverageClassManager m_coverageClassManager;
    private ComponentManager m_componentManager;
    private LockManager m_lockManager;
    private TransactionManager m_transactionManager;
    private BillingManager m_billingManager;
    private AgentManager m_agentManager;
    private UnderwriterManager m_underwriterManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private BillingAccountChangeWSClientManager m_billingAccountChangeWSClientManager;
    private PolicyChangeServiceHelper m_policyChangeServiceHelper;
    private RiskAddtlExposureManager m_RiskAddtlExposureManager;
    private DBUtilityManager m_dbUtilityManager;

    private boolean newTransactionB;
    private boolean newPolicyB;
    private boolean rateActionB;
    private boolean issueActionB;
    private boolean deleteWipOnFailureB;
    private boolean ignoreSoftValidationActionB;

    private final String policyRequestId = "dti.pm.policymgr.struts.MaintainPolicyAction&process=loadPolicyDetail";
    protected static final String POLICY_SAVE_PROCESSOR = "PolicyManager";
    protected static final String RISK_SAVE_PROCESSOR = "RiskManager";
    protected static final String COVERAGE_SAVE_PROCESSOR = "CoverageManager";
    protected static final String COVERAGE_CLASS_SAVE_PROCESSOR = "CoverageClassManager";
    protected static final String MAINTAIN_UNDERWRITER_ACTION_CLASS_NAME = "dti.pm.policymgr.underwritermgr.struts.MaintainUnderwriterAction";
    private static final String RELATED_POLICY_ERRORS_RECORDSET = "relatedPolicyErrorsRecordSet";

    protected static final String FAILED = "FAILED";

}
