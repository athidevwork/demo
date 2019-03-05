package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policy.CreditSurchargeDeductibleType;
import com.delphi_tech.ows.policy.CreditSurchargeDeductibleVersionType;
import com.delphi_tech.ows.policy.InsuredType;
import com.delphi_tech.ows.policy.InsuredVersionType;
import com.delphi_tech.ows.policy.InsurerType;
import com.delphi_tech.ows.policy.MedicalMalpracticeCoverageType;
import com.delphi_tech.ows.policy.MedicalMalpracticeCoverageVersionType;
import com.delphi_tech.ows.policy.MedicalMalpracticePolicyType;
import com.delphi_tech.ows.policychangeservice.DataModificationInformationType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeRequestType;
import com.delphi_tech.ows.policychangeservice.MedicalMalpracticePolicyChangeResultType;
import com.delphi_tech.ows.policychangeservice.PreviousDataValueDescriptionType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryRequestType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultParametersType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryResultType;
import com.delphi_tech.ows.policyinquiryservice.PolicyInquiryType;
import com.delphi_tech.ows.quotecopyservice.MedicalMalpracticeQuoteCopyRequestType;
import com.delphi_tech.ows.quotecopyservice.MedicalMalpracticeQuoteCopyResultType;
import dti.cs.policynotificationmgr.NotificationTransactionTimeEnum;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.request.service.WebServiceHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.lockmgr.LockManager;
import dti.pm.policymgr.service.PolicyChangeServiceHelper;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryServiceManager;
import dti.pm.policymgr.service.PolicyWebServiceHelper;
import dti.pm.policymgr.service.QuoteCopyServiceManager;
import dti.pm.transactionmgr.TransactionManager;
import org.apache.commons.collections.ListUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/23/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2016       tzeng       166929 - Initial version.
 * 12/04/2018       eyin        197179: Replaced function getPolicyDetail with getFirstPolicyDetail.
 * ---------------------------------------------------
 */
public class QuoteCopyServiceManagerImpl implements QuoteCopyServiceManager {
    private final Logger l = LogUtils.getLogger(getClass());

    private final static QName _QuoteCopyRequest_QNAME = new QName("http://www.delphi-tech.com/ows/QuoteCopyService", "MedicalMalpracticeQuoteCopyRequest");

    private final static QName _QuoteCopyResult_QNAME = new QName("http://www.delphi-tech.com/ows/QuoteCopyService", "MedicalMalpracticeQuoteCopyResult");

    @Override
    public MedicalMalpracticeQuoteCopyResultType doCopyToNewQuote(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doCopyToNewQuote", new Object[]{quoteCopyRequest, quoteCopyResult});
        }

        OwsLogRequest owsLogRequest = addOwsAccessTrailLog(quoteCopyRequest);

        boolean preValidateSuccessfully = false;
        String newQuoteNo = null;

        try {
            preValidateSuccessfully = preValidateRequestParameters(quoteCopyRequest);

            newQuoteNo = performCopy(quoteCopyRequest, quoteCopyResult);

            performPolicyChanges(quoteCopyRequest, quoteCopyResult);

            doAction(newQuoteNo, quoteCopyRequest, quoteCopyResult);

            loadPolicyInfoByRequest(newQuoteNo, quoteCopyRequest, quoteCopyResult);

            if(quoteCopyResult.getMedicalMalpracticePolicy().size()>0){
                owsLogRequest.setSourceTableName("POLICY_TERM_HISTORY");
                owsLogRequest.setSourceRecordFk(quoteCopyResult.getMedicalMalpracticePolicy().get(0).getPolicyTermNumberId());
                owsLogRequest.setSourceRecordNo(quoteCopyResult.getMedicalMalpracticePolicy().get(0).getPolicyId());
            }

            if (quoteCopyResult.getMessageStatus() == null) {
                quoteCopyResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
            }
        }
        catch (MessageStatusAppException wsae) {
            quoteCopyResult.setMessageStatus(wsae.getMessageStatus());
        }
        catch (Exception e) {
            if (!StringUtils.isBlank(newQuoteNo)) {
                loadPolicyInfoByRequest(newQuoteNo, quoteCopyRequest, quoteCopyResult);
            }
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR,
                "Failure invoking the copyToNewQuote", e , false);
            Boolean validationFailureB = PolicyWebServiceHelper.getInstance().getValidationFailureB(e);
            MessageStatusType newMessageStatusType;
            if (!preValidateSuccessfully) {
                ae = ExceptionHelper.getInstance().handleException("Validate input parameters failed", e);
                newMessageStatusType = MessageStatusHelper.getInstance().getRejectedMessageStatus(ae);
            }
            else if (validationFailureB) {
                newMessageStatusType = quoteCopyResult.getMessageStatus();
            }
            else {
                newMessageStatusType = MessageStatusHelper.getInstance().getRejectedMessageStatus(ae);
            }
            quoteCopyResult.setMessageStatus(newMessageStatusType);
            l.logp(Level.SEVERE, getClass().getName(), "QuoteCopyServiceManagerImpl", ae.getMessage(), ae);
        }

        updateOwsAccessTrailLog(owsLogRequest, quoteCopyResult);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "doCopyToNewQuote", quoteCopyResult);
        }

        return quoteCopyResult;
    }

    /**
     * Do action upon the value of ActionCode node provided.
     * @param newQuoteNo
     * @param quoteCopyRequest
     * @param quoteCopyResult
     * @return
     */
    private PolicyServiceCallStatus performActionCode(String newQuoteNo, MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performActionCode", new Object[]{newQuoteNo, quoteCopyRequest, quoteCopyResult});
        }
        PolicyServiceCallStatus callStatus = new PolicyServiceCallStatus();

        MessageStatusType mergeMessageStatus = quoteCopyResult.getMessageStatus() == null ? new MessageStatusType() : quoteCopyResult.getMessageStatus();
        if ((MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(mergeMessageStatus.getMessageStatusCode()) ||
             MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(mergeMessageStatus.getMessageStatusCode()))) {
            return callStatus;
        }

        setActionCodeFlags(quoteCopyRequest);
        MessageStatusType messageStatusType = new MessageStatusType();
        PolicyChangeServiceSaveProcessor policyChangeServiceSaveProcessor = (PolicyChangeServiceSaveProcessor) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
        PolicyHeader policyHeader = null;
        try {
            policyHeader = getPolicyManager().loadPolicyHeader(newQuoteNo, requestId, "QuoteCopyService: performActionCode");
            callStatus.setPolicyHeader(policyHeader);

            if (!policyHeader.getPolicyIdentifier().ownLock()) {
                try {
                    policyChangeServiceSaveProcessor.owsLockPolicy(policyHeader);
                }
                catch (Exception e) {
                    MessageManager mm = MessageManager.getInstance();
                    mm.addErrorMessage("ws.quote.copy.lock.policy", new String[]{newQuoteNo});
                    throw new AppException("Error: cannot lock policy " + newQuoteNo);
                }
            }

            if (isRateActionB()) {
                boolean ignoreSoftValidationToRateB = false;
                if (isIssueActionB() && isIgnoreSoftValidationActionB()) {
                    ignoreSoftValidationToRateB = true;
                }
                messageStatusType = getPolicyChangeServiceHelper().performRateAction(policyHeader, getTransactionManager(), ignoreSoftValidationToRateB);
                if (messageStatusType.getMessageStatusCode() != null) {
                    if (MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                        || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())) {
                        throw new AppException("Error: transaction validation failed for rate policy " + newQuoteNo);
                    }
                    else {
                        quoteCopyResult.setMessageStatus(messageStatusType);
                    }
                }
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
                    if (MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode())
                        || MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())) {
                        throw new AppException("Error: transaction validation failed for issue policy " + newQuoteNo);
                    }
                    else {
                        quoteCopyResult.setMessageStatus(messageStatusType);
                    }
                }
            }
            else {
                callStatus.setPolicyLockedB(true);
            }
            callStatus.setSuccessStatusB(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            callStatus.setSuccessStatusB(true);
            callStatus.setAppException(new AppException(e.getMessage(), e));
            if (policyHeader != null && policyHeader.getPolicyIdentifier().ownLock()) {
                callStatus.setPolicyLockedB(true);
            }

            if (MessageStatusHelper.STATUS_SAVED_WITH_INFO.equals(messageStatusType.getMessageStatusCode()) ||
                MessageStatusHelper.STATUS_SAVED_WITH_ERRORS.equals(messageStatusType.getMessageStatusCode())) {
                PolicyWebServiceHelper.getInstance().setValidationErrorToOutput(messageStatusType, newQuoteNo, mergeMessageStatus);
                callStatus.setValidationFailureB(true);
                quoteCopyResult.setMessageStatus(mergeMessageStatus);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performActionCode", callStatus);
        }

        return callStatus;
    }

    @Override
    public void performPolicyChanges(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performPolicyChanges", new Object[]{quoteCopyRequest, quoteCopyResult});
        }

        if (quoteCopyRequest.getDataModificationInformation() == null ||
            quoteCopyRequest.getDataModificationInformation().getPreviousDataValueDescription() == null ||
            quoteCopyRequest.getDataModificationInformation().getPreviousDataValueDescription().getMedicalMalpracticePolicy().size() == 0) {
            l.info("There is no original data inputted so that just copy a new quote without changes.");
            return;
        }

        MedicalMalpracticePolicyType changedPolicy = quoteCopyRequest.getMedicalMalpracticePolicy().get(0);
        MedicalMalpracticePolicyType dbPolicy = quoteCopyResult.getMedicalMalpracticePolicy().get(0);

        prepareValueForChange(dbPolicy, changedPolicy);

        MedicalMalpracticePolicyChangeRequestType policyChangeRequest = new MedicalMalpracticePolicyChangeRequestType();
        policyChangeRequest.getAddress().addAll(quoteCopyRequest.getAddress());
        policyChangeRequest.getOrganization().addAll(quoteCopyRequest.getOrganization());
        policyChangeRequest.getPerson().addAll(quoteCopyRequest.getPerson());
        policyChangeRequest.getProperty().addAll(quoteCopyRequest.getProperty());
        policyChangeRequest.getMedicalMalpracticePolicy().add(changedPolicy);

        PreviousDataValueDescriptionType previousPolicyData = new PreviousDataValueDescriptionType();
        com.delphi_tech.ows.quotecopyservice.PreviousDataValueDescriptionType quoteCopyPreviousPolicyData = quoteCopyRequest.getDataModificationInformation().getPreviousDataValueDescription();
        previousPolicyData.getAddress().addAll(quoteCopyPreviousPolicyData.getAddress());
        previousPolicyData.getOrganization().addAll(quoteCopyPreviousPolicyData.getOrganization());
        previousPolicyData.getPerson().addAll(quoteCopyPreviousPolicyData.getPerson());
        previousPolicyData.getProperty().addAll(quoteCopyPreviousPolicyData.getProperty());
        previousPolicyData.getMedicalMalpracticePolicy().add(dbPolicy);

        DataModificationInformationType policyDataMod = new DataModificationInformationType();
        policyDataMod.setPreviousDataValueDescription(previousPolicyData);
        policyChangeRequest.setDataModificationInformation(policyDataMod);
        MedicalMalpracticePolicyChangeResultType policyChangeResult = new MedicalMalpracticePolicyChangeResultType();
        WebServiceHelper.getInstance().setWebServiceUser(quoteCopyRequest.getUserId());
        getPolicyChangeServiceSaveProcessor().changePolicy(policyChangeRequest, policyChangeResult);

        quoteCopyResult.setMessageStatus(policyChangeResult.getMessageStatus());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performPolicyChanges", quoteCopyResult);
        }
    }

    /**
     * Call performActionCode and deal with the callback.
     * @param newQuoteNo
     * @param quoteCopyRequest
     * @param quoteCopyResult
     */
    private void doAction(String newQuoteNo, MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doAction", new Object[]{newQuoteNo, quoteCopyRequest, quoteCopyResult});
        }

        PolicyServiceCallStatus callStatus = performActionCode(newQuoteNo, quoteCopyRequest, quoteCopyResult);
        if (callStatus.isPolicyLocked()) {
            PolicyChangeServiceSaveProcessor policyChangeServiceSaveProcessor =
                (PolicyChangeServiceSaveProcessor) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
            policyChangeServiceSaveProcessor.owsUnlockPolicy(callStatus.getPolicyHeader());
        }

        if (callStatus.getAppException() != null) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing performActionCode", callStatus.getAppException());
            if (callStatus.getValidationFailureB()) {
                ae.setMessageParameters(new Object[]{callStatus.getValidationFailureB()});
            }

            l.throwing(getClass().getName(), "doAction", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "doAction");
        }
    }

    /**
     * Prepare the value for calling PolicyChangeWebService.
     * @param dbPolicy
     * @param changedPolicy
     */
    private void prepareValueForChange(MedicalMalpracticePolicyType dbPolicy,
                                          MedicalMalpracticePolicyType changedPolicy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "prepareValueForChange", new Object[]{dbPolicy, changedPolicy});
        }

        preparePolicyChange(dbPolicy, changedPolicy);

        prepareRiskChange(dbPolicy, changedPolicy);

        prepareCoverageChange(dbPolicy, changedPolicy);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "prepareValueForChange");
        }
    }

    /**
     * Prepare the value for policy change.
     * @param dbPolicy
     * @param changedPolicy
     */
    private void preparePolicyChange(MedicalMalpracticePolicyType dbPolicy, MedicalMalpracticePolicyType changedPolicy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "preparePolicyChange", new Object[]{dbPolicy, changedPolicy});
        }

        dbPolicy.getTransactionDetail().setTransactionEffectiveDate(dbPolicy.getContractPeriod().getStartDate());
        if (null != dbPolicy.getPrintName() && null != dbPolicy.getPrintName().getFullName()) {
            dbPolicy.getPrintName().setFullName(dbPolicy.getPrintName().getFullName().trim());
        }

        if (null != changedPolicy.getPrintName() && null != changedPolicy.getPrintName().getFullName()) {
            changedPolicy.getPrintName().setFullName(changedPolicy.getPrintName().getFullName().trim());
        }

        //Reset changed value
        changedPolicy.setPolicyId(dbPolicy.getPolicyId());
        changedPolicy.setPolicyNumberId(dbPolicy.getPolicyNumberId());
        changedPolicy.setPolicyTermNumberId(dbPolicy.getPolicyTermNumberId());
        changedPolicy.setTransactionDetail(dbPolicy.getTransactionDetail());
        changedPolicy.getTransactionDetail().setTransactionEffectiveDate(dbPolicy.getContractPeriod().getStartDate());

        if (dbPolicy.getInsurer()!=null && changedPolicy.getInsurer()!=null) {
            for (InsurerType changedInsurer : changedPolicy.getInsurer()) {
                for (InsurerType dbInsurer : dbPolicy.getInsurer()) {
                    if (dbInsurer.getReferredParty() != null && changedInsurer.getReferredParty() != null &&
                        dbInsurer.getReferredParty().getPartyNumberId().equalsIgnoreCase(changedInsurer.getReferredParty().getPartyNumberId()) &&
                        dbInsurer.getTypeCode() != null && dbInsurer.getTypeCode().equalsIgnoreCase(changedInsurer.getTypeCode())) {
                        changedInsurer.setInsurerNumberId(dbInsurer.getInsurerNumberId());
                    }
                }
            }
        }

        if (null != changedPolicy.getPolicyDetail()) {
            changedPolicy.getFirstPolicyDetail().setPolicyStatusCode(dbPolicy.getFirstPolicyDetail().getPolicyStatusCode());
            changedPolicy.getFirstPolicyDetail().setPolicyVersionDetail(dbPolicy.getFirstPolicyDetail().getPolicyVersionDetail());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "preparePolicyChange");
        }
    }

    /**
     * Prepare the value for risk change.
     * @param dbPolicy
     * @param changedPolicy
     */
    private void prepareRiskChange(MedicalMalpracticePolicyType dbPolicy, MedicalMalpracticePolicyType changedPolicy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "prepareRiskChange", new Object[]{dbPolicy, changedPolicy});
        }

        for (InsuredType changedInsured : changedPolicy.getInsured()) {
            validateForChangeRisk(changedInsured);
            for (InsuredType dbInsured : dbPolicy.getInsured()) {
                if (!isSameRiskEntity(dbInsured, changedInsured)) {
                    continue;
                }
                for (InsuredVersionType changedInsuredVersion : changedInsured.getInsuredVersion()) {
                    validateForChangeRiskVersion(changedInsuredVersion, changedInsured.getKey());
                    for (InsuredVersionType dbInsuredVersion : dbInsured.getInsuredVersion()) {
                        if (!isSameRiskVersion(dbInsuredVersion, changedInsuredVersion)) {
                            continue;
                        }
                        resetChangeInsuredInfo(dbInsuredVersion, changedInsuredVersion, dbInsured, changedInsured, changedPolicy);
                        break;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "prepareRiskChange");
        }
    }

    /**
     * Prepare the value for coverage change.
     * @param dbPolicy
     * @param changedPolicy
     */
    private void prepareCoverageChange(MedicalMalpracticePolicyType dbPolicy, MedicalMalpracticePolicyType changedPolicy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "prepareCoverageChange", new Object[]{dbPolicy, changedPolicy});
        }

        for (InsuredType changedInsured : changedPolicy.getInsured()) {
            String changedInsuredKey = changedInsured.getKey();
            for (MedicalMalpracticeCoverageType changedCoverage : changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage()) {
                if (StringUtils.isBlank(changedCoverage.getParentCoverageNumberId())) {
                    validateForChangeCoverage(changedCoverage);
                }
                else {
                    validateForChangeSubCoverage(changedCoverage);
                }
                boolean isCoverageFound = false;
                if (changedCoverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(changedInsuredKey)) {
                    for (MedicalMalpracticeCoverageType dbCoverage : dbPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage()) {
                        if (!isSameCoverage(dbCoverage, changedCoverage)) {
                            continue;
                        }
                        isCoverageFound = true;
                        for (MedicalMalpracticeCoverageVersionType changedCoverageVersion : changedCoverage.getMedicalMalpracticeCoverageVersion()) {
                            if (StringUtils.isBlank(changedCoverage.getParentCoverageNumberId())) {
                                validateForChangeCoverageVersion(changedCoverageVersion, changedCoverage.getKey());
                            }
                            else {
                                validateForChangeSubCoverageVersion(changedCoverageVersion, changedCoverage.getKey());
                            }
                            for (MedicalMalpracticeCoverageVersionType dbCoverageVersion : dbCoverage.getMedicalMalpracticeCoverageVersion()) {
                                if (!isSameCoverageVersion(changedCoverageVersion, dbCoverageVersion)) {
                                    continue;
                                }
                                resetChangeCoverageInfo(dbCoverageVersion, changedCoverageVersion, dbCoverage, changedCoverage, changedPolicy);
                                break;
                            }
                        }
                    }
                    if (isCoverageFound) {
                        prepareComponentChange(dbPolicy, changedPolicy, changedCoverage.getKey());
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "prepareCoverageChange");
        }
    }

    /**
     * Validate the input parameters for change subCoverage version.
     * @param changedCoverageVersion
     * @param subCoverageKey
     */
    private void validateForChangeSubCoverageVersion(MedicalMalpracticeCoverageVersionType changedCoverageVersion, String subCoverageKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeSubCoverageVersion", new Object[]{changedCoverageVersion, subCoverageKey});
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedCoverageVersion.getPrimaryIndicator()))
            errorMsg = errorMsg + "PrimaryIndicator, ";

        if (StringUtils.isBlank(changedCoverageVersion.getEffectivePeriod() == null ? null : changedCoverageVersion.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";

        if (StringUtils.isBlank(changedCoverageVersion.getEffectivePeriod() == null ? null : changedCoverageVersion.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.subcoverage.version.required", "Fail to do the changes", new String[]{errorMsg, subCoverageKey});
            l.throwing(getClass().getName(), "validateForChangeSubCoverageVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeSubCoverageVersion");
        }
    }

    /**
     * Validate the input parameters for change subCoverage.
     * @param changedSubCoverage
     */
    private void validateForChangeSubCoverage(MedicalMalpracticeCoverageType changedSubCoverage) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeSubCoverage", changedSubCoverage);
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedSubCoverage.getKey()))
            errorMsg = errorMsg + "Key, ";

        if (StringUtils.isBlank(changedSubCoverage.getMedicalMalpracticeCoverageCode() == null ? null : changedSubCoverage.getMedicalMalpracticeCoverageCode().getValue()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageCode, ";

        if (StringUtils.isBlank(changedSubCoverage.getReferredInsured() == null ? null : changedSubCoverage.getReferredInsured().getInsuredReference()))
            errorMsg = errorMsg + "ReferredInsured/InsuredReference, ";

        if (StringUtils.isBlank(changedSubCoverage.getParentCoverageNumberId()))
            errorMsg = errorMsg + "ParentCoverageNumberId, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.subcoverage.required", "Fail to do the changes", new String[]{errorMsg, changedSubCoverage.getCoverageNumberId()});
            l.throwing(getClass().getName(), "validateForChangeSubCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeSubCoverage");
        }
    }

    /**
     * Validate the input parameters for change component.
     * @param changedComponent
     */
    private void validateForChangeComponent(CreditSurchargeDeductibleType changedComponent) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeComponent", changedComponent);
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedComponent.getKey()))
            errorMsg = errorMsg + "Key, ";

        if (StringUtils.isBlank(changedComponent.getReferredMedicalMalpracticeCoverage() == null ? null : changedComponent.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference()))
            errorMsg = errorMsg + "ReferredMedicalMalpracticeCoverage/MedicalMalpracticeCoverageReference";

        if (StringUtils.isBlank(changedComponent.getCreditSurchargeDeductibleCode().getValue()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleCode, ";

        if (StringUtils.isBlank(changedComponent.getCreditSurchargeDeductibleTypeCode().getValue()))
            errorMsg = errorMsg + "CreditSurchargeDeductibleTypeCode, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.component.required", "Fail to do the changes", new String[]{errorMsg, changedComponent.getCreditSurchargeDeductibleNumberId()});
            l.throwing(getClass().getName(), "validateForChangeComponent", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeComponent");
        }
    }

    /**
     * Prepare the value for component change.
     * @param dbPolicy
     * @param changedPolicy
     * @param changedCoverageKey
     */
    private void prepareComponentChange(MedicalMalpracticePolicyType dbPolicy, MedicalMalpracticePolicyType changedPolicy, String changedCoverageKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "prepareComponentChange", new Object[]{dbPolicy, changedPolicy, changedCoverageKey});
        }

        for (CreditSurchargeDeductibleType changedComponent : changedPolicy.getCreditSurchargeDeductible()) {
            validateForChangeComponent(changedComponent);
            if (changedCoverageKey.equalsIgnoreCase(changedComponent.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference())) {
                for (CreditSurchargeDeductibleType dbComponent : dbPolicy.getCreditSurchargeDeductible()) {
                    if (!isSameComponent(dbComponent, changedComponent)){
                        continue;
                    }
                    for (CreditSurchargeDeductibleVersionType changedComponentVersion : changedComponent.getCreditSurchargeDeductibleVersion()) {
                        validateForChangeComponentVersion(changedComponentVersion, changedComponent.getKey());
                        for (CreditSurchargeDeductibleVersionType dbComponentVersion : dbComponent.getCreditSurchargeDeductibleVersion()) {
                            if (!isSameComponentVersion(dbComponentVersion, changedComponentVersion)) {
                                continue;
                            }
                            resetChangeComponentInfo(dbComponentVersion, changedComponentVersion, dbComponent, changedComponent);
                        }
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "prepareComponentChange");
        }
    }

    /**
     * Use db value to reset the value for change component.
     * @param dbComponentVersion
     * @param changedComponentVersion
     * @param dbComponent
     * @param changedComponent
     */
    private void resetChangeComponentInfo(CreditSurchargeDeductibleVersionType dbComponentVersion, CreditSurchargeDeductibleVersionType changedComponentVersion, CreditSurchargeDeductibleType dbComponent, CreditSurchargeDeductibleType changedComponent) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetChangeComponentInfo", new Object[]{dbComponentVersion, changedComponentVersion});
        }

        changedComponent.setKey(dbComponent.getKey());
        changedComponent.setCreditSurchargeDeductibleNumberId(dbComponent.getCreditSurchargeDeductibleNumberId());
        changedComponentVersion.setCreditSurchargeDeductibleVersionId(dbComponentVersion.getCreditSurchargeDeductibleVersionId());
        changedComponentVersion.setCreditSurchargeDeductibleVersionDetail(dbComponentVersion.getCreditSurchargeDeductibleVersionDetail());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resetChangeComponentInfo");
        }
    }

    /**
     * Check if the inputted component is the same as db component version.
     * @param dbComponentVersion
     * @param changedComponentVersion
     * @return
     */
    private boolean isSameComponentVersion(CreditSurchargeDeductibleVersionType dbComponentVersion, CreditSurchargeDeductibleVersionType changedComponentVersion) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSameComponentVersion", new Object[]{dbComponentVersion, changedComponentVersion});
        }

        boolean isSame = true;

        if (!dbComponentVersion.getEffectivePeriod().getStartDate().equalsIgnoreCase(changedComponentVersion.getEffectivePeriod() == null ? null :changedComponentVersion.getEffectivePeriod().getStartDate()))
            isSame = false;

        else if (!dbComponentVersion.getEffectivePeriod().getEndDate().equalsIgnoreCase(changedComponentVersion.getEffectivePeriod() == null ? null : changedComponentVersion.getEffectivePeriod().getEndDate()))
            isSame = false;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSameComponentVersion");
        }
        return isSame;
    }

    /**
     * Validate the input parameters for change component version.
     * @param changedComponentVersion
     * @param componentKey
     */
    private void validateForChangeComponentVersion(CreditSurchargeDeductibleVersionType changedComponentVersion, String componentKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeComponentVersion", changedComponentVersion);
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedComponentVersion.getEffectivePeriod() == null ? null : changedComponentVersion.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";

        if (StringUtils.isBlank(changedComponentVersion.getEffectivePeriod() == null ? null : changedComponentVersion.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.component.version.required", "Fail to do the changes", new String[]{errorMsg, componentKey});
            l.throwing(getClass().getName(), "validateForChangeComponentVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeComponentVersion");
        }
    }

    /**
     * Check if the inputted component is the same as db component.
     * @param dbComponent
     * @param changedComponent
     * @return
     */
    private boolean isSameComponent(CreditSurchargeDeductibleType dbComponent, CreditSurchargeDeductibleType changedComponent) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSameComponent", new Object[]{dbComponent, changedComponent});
        }

        boolean isSame = true;

        if (!dbComponent.getCreditSurchargeDeductibleCode().getValue().equalsIgnoreCase(changedComponent.getCreditSurchargeDeductibleCode().getValue()))
            isSame = false;

        else if (!dbComponent.getCreditSurchargeDeductibleTypeCode().getValue().equalsIgnoreCase(changedComponent.getCreditSurchargeDeductibleTypeCode().getValue()))
            isSame = false;

        else if (!dbComponent.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference().equalsIgnoreCase(changedComponent.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference()))
            isSame = false;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSameComponent");
        }

        return isSame;
    }

    /**
     * Use db value to reset the value for change coverage.
     * @param dbCoverageVersion
     * @param changedCoverageVersion
     * @param dbCoverage
     * @param changedCoverage
     * @param changedPolicy
     */
    private void resetChangeCoverageInfo(MedicalMalpracticeCoverageVersionType dbCoverageVersion,
                                         MedicalMalpracticeCoverageVersionType changedCoverageVersion,
                                         MedicalMalpracticeCoverageType dbCoverage,
                                         MedicalMalpracticeCoverageType changedCoverage,
                                         MedicalMalpracticePolicyType changedPolicy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetChangeCoverageInfo", new Object[]{dbCoverageVersion, changedCoverageVersion, dbCoverage, changedCoverage, changedPolicy});
        }

        changedCoverage.setCoverageNumberId(dbCoverage.getCoverageNumberId());
        changedCoverage.setParentCoverageNumberId(dbCoverage.getParentCoverageNumberId());
        changedCoverageVersion.setMedicalMalpracticeCoverageVersionId(dbCoverageVersion.getMedicalMalpracticeCoverageVersionId());
        changedCoverageVersion.setMedicalMalpracticeCoverageVersionDetail(dbCoverageVersion.getMedicalMalpracticeCoverageVersionDetail());
        changedCoverageVersion.setMedicalMalpracticeCoverageStatusCode(dbCoverageVersion.getMedicalMalpracticeCoverageStatusCode());
        if (null != changedPolicy.getCreditSurchargeDeductible()) {
            for (CreditSurchargeDeductibleType changedComponent : changedPolicy.getCreditSurchargeDeductible()) {
                if (null != changedComponent.getReferredMedicalMalpracticeCoverage() &&
                    changedComponent.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference().equalsIgnoreCase(changedCoverage.getKey())) {
                    changedComponent.getReferredMedicalMalpracticeCoverage().setMedicalMalpracticeCoverageReference(dbCoverage.getKey());
                }
            }
        }
        changedCoverage.setKey(dbCoverage.getKey());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resetChangeCoverageInfo");
        }
    }

    /**
     * Check if the inputted component version is the same as db component version.
     * @param changedCoverageVersion
     * @param dbCoverageVersion
     * @return
     */
    private boolean isSameCoverageVersion(MedicalMalpracticeCoverageVersionType changedCoverageVersion, MedicalMalpracticeCoverageVersionType dbCoverageVersion) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSameCoverageVersion", new Object[]{changedCoverageVersion, dbCoverageVersion});
        }

        boolean isSame = true;

        if (!dbCoverageVersion.getPrimaryIndicator().equalsIgnoreCase(changedCoverageVersion.getPrimaryIndicator()))
            isSame = false;

        else if (!dbCoverageVersion.getEffectivePeriod().getStartDate().equalsIgnoreCase(changedCoverageVersion.getEffectivePeriod() == null ? null :changedCoverageVersion.getEffectivePeriod().getStartDate()))
            isSame = false;

        else if (!dbCoverageVersion.getEffectivePeriod().getEndDate().equalsIgnoreCase(changedCoverageVersion.getEffectivePeriod() == null ? null : changedCoverageVersion.getEffectivePeriod().getEndDate()))
            isSame = false;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSameCoverageVersion");
        }

        return isSame;
    }

    /**
     * Validate the input parameters for change coverage version.
     * @param changedCoverageVersion
     * @param coverageKey
     */
    private void validateForChangeCoverageVersion(MedicalMalpracticeCoverageVersionType changedCoverageVersion, String coverageKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeCoverageVersion", new Object[]{changedCoverageVersion, coverageKey});
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedCoverageVersion.getPrimaryIndicator()))
            errorMsg = errorMsg + "PrimaryIndicator, ";

        if (StringUtils.isBlank(changedCoverageVersion.getEffectivePeriod() == null ? null : changedCoverageVersion.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";

        if (StringUtils.isBlank(changedCoverageVersion.getEffectivePeriod() == null ? null : changedCoverageVersion.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.coverage.version.required", "Fail to do the changes", new String[]{errorMsg, coverageKey});
            l.throwing(getClass().getName(), "validateForChangeCoverageVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeCoverageVersion");
        }
    }

    /**
     * Check if the inputted coverage is the same as db coverage.
     * @param dbCoverage
     * @param changedCoverage
     * @return
     */
    private boolean isSameCoverage(MedicalMalpracticeCoverageType dbCoverage, MedicalMalpracticeCoverageType changedCoverage) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSameCoverage", new Object[]{dbCoverage, changedCoverage});
        }

        boolean isSame = true;

        if (!dbCoverage.getParentCoverageNumberId().equalsIgnoreCase(changedCoverage.getParentCoverageNumberId())) {
            isSame = false;
        }
        else if (!dbCoverage.getMedicalMalpracticeCoverageCode().getValue().equalsIgnoreCase(changedCoverage.getMedicalMalpracticeCoverageCode().getValue())) {
            isSame = false;
        }
        else if (!dbCoverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(changedCoverage.getReferredInsured().getInsuredReference())) {
            isSame = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSameCoverage");
        }
        return isSame;
    }

    /**
     * Validate the input parameters for change coverage.
     * @param changedCoverage
     */
    private void validateForChangeCoverage(MedicalMalpracticeCoverageType changedCoverage) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeCoverage");
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedCoverage.getKey()))
            errorMsg = errorMsg + "Key, ";

        if (StringUtils.isBlank(changedCoverage.getMedicalMalpracticeCoverageCode() == null ? null : changedCoverage.getMedicalMalpracticeCoverageCode().getValue()))
            errorMsg = errorMsg + "MedicalMalpracticeCoverageCode, ";

        if (StringUtils.isBlank(changedCoverage.getReferredInsured() == null ? null : changedCoverage.getReferredInsured().getInsuredReference()))
            errorMsg = errorMsg + "ReferredInsured/InsuredReference, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.coverage.required", "Fail to do the changes", new String[]{errorMsg, changedCoverage.getCoverageNumberId()});
            l.throwing(getClass().getName(), "validateForChangeCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeCoverage");
        }
    }

    /**
     * Use db value to reset the value for change risk.
     * @param dbInsuredVersion
     * @param changedInsuredVersion
     * @param dbInsured
     * @param changedInsured
     * @param changedPolicy
     */
    private void resetChangeInsuredInfo(InsuredVersionType dbInsuredVersion,
                                        InsuredVersionType changedInsuredVersion,
                                        InsuredType dbInsured,
                                        InsuredType changedInsured,
                                        MedicalMalpracticePolicyType changedPolicy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetChangeInsuredInfo", new Object[]{dbInsuredVersion, changedInsuredVersion, dbInsured, changedInsured, changedPolicy});
        }

        changedInsured.setInsuredNumberId(dbInsured.getInsuredNumberId());
        changedInsuredVersion.setInsuredVersionNumberId(dbInsuredVersion.getInsuredVersionNumberId());
        changedInsuredVersion.setInsuredVersionDetail(dbInsuredVersion.getInsuredVersionDetail());
        changedInsuredVersion.setInsuredStatusCode(dbInsuredVersion.getInsuredStatusCode());
        if (null != changedPolicy.getMedicalMalpracticeLineOfBusiness()) {
            for (MedicalMalpracticeCoverageType changedCoverage : changedPolicy.getMedicalMalpracticeLineOfBusiness().getMedicalMalpracticeCoverage()) {
                if (null != changedCoverage.getReferredInsured() &&
                    changedCoverage.getReferredInsured().getInsuredReference().equalsIgnoreCase(changedInsured.getKey())) {
                    changedCoverage.getReferredInsured().setInsuredReference(dbInsured.getKey());
                }
            }
        }
        changedInsured.setKey(dbInsured.getKey());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resetChangeInsuredInfo");
        }
    }

    /**
     * Validate the input parameters for change risk.
     * @param changedInsured
     */
    private void validateForChangeRisk(InsuredType changedInsured) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeRisk", changedInsured);
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedInsured.getKey()))
            errorMsg = errorMsg + "Key, ";

        if (StringUtils.isBlank(changedInsured.getReferredParty().getPartyNumberId()))
            errorMsg = errorMsg + "ReferredParty/PartyNumberId, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.risk.required", "Fail to do the changes", new String[]{errorMsg, changedInsured.getInsuredNumberId()});
            l.throwing(getClass().getName(), "validateForChangeRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeRisk");
        }
    }

    /**
     * Validate the input parameters for change risk version.
     * @param changedInsuredversion
     * @param insuredKey
     */
    private void validateForChangeRiskVersion(InsuredVersionType changedInsuredversion, String insuredKey) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForChangeRiskVersion", new Object[]{changedInsuredversion, insuredKey});
        }

        String errorMsg = "";

        if (StringUtils.isBlank(changedInsuredversion.getPrimaryIndicator()))
            errorMsg = errorMsg + "PrimaryIndicator, ";

        if (StringUtils.isBlank(changedInsuredversion.getInsuredTypeCode() == null ? null : changedInsuredversion.getInsuredTypeCode().getValue()))
            errorMsg = errorMsg + "InsuredTypeCode, ";

        if (StringUtils.isBlank(changedInsuredversion.getEffectivePeriod() == null ? null : changedInsuredversion.getEffectivePeriod().getStartDate()))
            errorMsg = errorMsg + "EffectivePeriod/StartDate, ";

        if (StringUtils.isBlank(changedInsuredversion.getEffectivePeriod() == null ? null : changedInsuredversion.getEffectivePeriod().getEndDate()))
            errorMsg = errorMsg + "EffectivePeriod/EndDate, ";

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.quote.copy.change.risk.version.required", "Fail to do the changes", new String[]{errorMsg, insuredKey});
            l.throwing(getClass().getName(), "validateForChangeRiskVersion", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForChangeRiskVersion");
        }
    }

    /**
     * Check if the inputted risk version is the same as db risk version.
     * @param dbInsuredVersion
     * @param changedInsuredversion
     * @return
     */
    private boolean isSameRiskVersion(InsuredVersionType dbInsuredVersion, InsuredVersionType changedInsuredversion) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSameRiskVersion", new Object[]{dbInsuredVersion, changedInsuredversion});
        }

        boolean isSame = true;

        if (!dbInsuredVersion.getInsuredTypeCode().getValue().equalsIgnoreCase(changedInsuredversion.getInsuredTypeCode().getValue()))
            isSame = false;

        else if (!dbInsuredVersion.getEffectivePeriod().getStartDate().equalsIgnoreCase(changedInsuredversion.getEffectivePeriod().getStartDate()))
            isSame = false;

        else if (!dbInsuredVersion.getEffectivePeriod().getEndDate().equalsIgnoreCase(changedInsuredversion.getEffectivePeriod().getEndDate()))
            isSame = false;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSameRiskVersion");
        }
        return isSame;
    }

    /**
     * Check if the inputted risk entity is the same as db risk entity.
     * @param dbInsured
     * @param changedInsured
     * @return
     */
    private boolean isSameRiskEntity(InsuredType dbInsured, InsuredType changedInsured) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSameRiskEntity", new Object[]{dbInsured, changedInsured});
        }

        boolean isSame = dbInsured.getReferredParty().getPartyNumberId().equalsIgnoreCase(changedInsured.getReferredParty().getPartyNumberId());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSameRiskEntity");
        }
        return isSame;
    }

    @Override
    public String performCopy(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performCopy", quoteCopyRequest);
        }

        String newQuoteNo = null;
        boolean validateSuccessfullyBeforeProcessB = false;
        try {
            MedicalMalpracticePolicyType changedQuote = quoteCopyRequest.getMedicalMalpracticePolicy().get(0);

            // Load policy header for original quote.
            if (RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)) {
                RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
            }
            PolicyHeader quotePolicyHeader = getPolicyManager().loadPolicyHeader(changedQuote.getPolicyId(), requestId, "QuoteCopyService: performCopy");

            validateSuccessfullyBeforeProcessB = PolicyWebServiceHelper.getInstance().validateQuoteBeforeProcess(quotePolicyHeader);

            // Copy Quote.
            Record inputRecord = new Record();
            PolicyHeaderFields.setPolicyNo(inputRecord, changedQuote.getPolicyId());
            Record outputRecord = getPolicyManager().copyQuote(quotePolicyHeader, inputRecord);
            newQuoteNo = outputRecord.getStringValue("copiedQuoteNo");

            // Get the policy header of the newly created quote number.
            if(RequestStorageManager.getInstance().has(RequestStorageIds.POLICY_HEADER)){
                RequestStorageManager.getInstance().remove(RequestStorageIds.POLICY_HEADER);
            }
            quotePolicyHeader = getPolicyManager().loadPolicyHeader(newQuoteNo, requestId, "QuoteCopyService: performCopy");

            // Load all data of new quote in quoteCopyResult for prepare to do policy change.
            loadPolicyInfo(newQuoteNo, ListUtils.EMPTY_LIST, quoteCopyResult);
        }
        catch (Exception e) {
            if (!validateSuccessfullyBeforeProcessB) {
                AppException ae = ExceptionHelper.getInstance().handleException("Fail in validateQuoteBeforeProcess", e);
                MessageStatusType messageStatusType = MessageStatusHelper.getInstance().getRejectedMessageStatus(ae);
                MessageStatusAppException wsae = MessageStatusHelper.getInstance().handleRejectedServiceCall("Fail in validateQuoteBeforeProcess", messageStatusType);
                throw wsae;
            }
            else {
                AppException ae = ExceptionHelper.getInstance().handleException("Error when executing performCopy", e);
                l.throwing(getClass().getName(), "performCopy", ae);
                throw ae;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performCopy");
        }
        return newQuoteNo;
    }

    /**
     * Load policy info by view name.
     * @param quoteNo
     * @param quoteCopyRequest
     * @param quoteCopyResult
     */
    public void loadPolicyInfoByRequest(String quoteNo, MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyInfoByRequest", new Object[]{quoteNo, quoteCopyRequest, quoteCopyResult});
        }
        List<String> viewList = quoteCopyRequest.getMedicalMalpracticeQuoteCopyResultParameters() == null ?
                                ListUtils.EMPTY_LIST : quoteCopyRequest.getMedicalMalpracticeQuoteCopyResultParameters().getViewName();

        quoteCopyResult.getMedicalMalpracticePolicy().clear();
        quoteCopyResult.getPerson().clear();
        quoteCopyResult.getAddress().clear();
        quoteCopyResult.getOrganization().clear();
        quoteCopyResult.getProperty().clear();

        loadPolicyInfo(quoteNo, viewList, quoteCopyResult);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyInfoByRequest");
        }
    }

    /**
     * Load policy info.
     * @param quoteNo
     * @param quoteCopyResult
     */
    private void loadPolicyInfo(String quoteNo, List<String> viewNameList, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPolicyInfo", new Object[]{quoteNo, viewNameList, quoteCopyResult});
        }

        PolicyInquiryRequestType policyInquiryRequest = new PolicyInquiryRequestType();
        PolicyInquiryResultParametersType policyInquiryResultParameters = new PolicyInquiryResultParametersType();
        policyInquiryResultParameters.getViewName().addAll(viewNameList);
        policyInquiryRequest.setPolicyInquiryResultParameters(policyInquiryResultParameters);

        PolicyInquiryType inquiry = new PolicyInquiryType();
        inquiry.setPolicyId(quoteNo);
        PolicyInquiryRequestParametersType inquiryRequestParameters = new PolicyInquiryRequestParametersType();
        inquiryRequestParameters.setPolicyInquiry(inquiry);
        policyInquiryRequest.getPolicyInquiryRequestParameters().add(inquiryRequestParameters);
        PolicyInquiryResultType resultType = getPolicyInquiryServiceManager().loadPolicy(policyInquiryRequest);

        if (MessageStatusHelper.getInstance().isRejected(resultType.getMessageStatus())) {
            MessageStatusAppException wsae = MessageStatusHelper.getInstance().handleRejectedServiceCall("Failure invoking the PolicyInquiryServiceManagerImpl",
                resultType.getMessageStatus());
            l.logp(Level.SEVERE, getClass().getName(), "loadPolicyInfo", wsae.getMessage(), wsae);
            throw wsae;
        }

        quoteCopyResult.setCorrelationId(resultType.getCorrelationId());
        quoteCopyResult.setMessageId(resultType.getMessageId());
        if (quoteCopyResult.getMessageStatus() == null) {
            quoteCopyResult.setMessageStatus(resultType.getMessageStatus());
        }
        quoteCopyResult.getMedicalMalpracticePolicy().addAll(resultType.getMedicalMalpracticePolicy());
        quoteCopyResult.getAddress().addAll(resultType.getAddress());
        quoteCopyResult.getOrganization().addAll(resultType.getOrganization());
        quoteCopyResult.getPerson().addAll(resultType.getPerson());
        quoteCopyResult.getProperty().addAll(resultType.getProperty());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPolicyInfo", new Object[]{quoteNo, quoteCopyResult});
        }
    }

    /**
     * Pre-validate request parameters, if validate successfully, then return true.
     * @param quoteCopyRequest
     * @return
     */
    private boolean preValidateRequestParameters(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "preValidateRequestParameters", quoteCopyRequest);
        }

        List<MedicalMalpracticePolicyType> changedPolicies = quoteCopyRequest.getMedicalMalpracticePolicy();
        List<MedicalMalpracticePolicyType> originalPolicies = quoteCopyRequest.getDataModificationInformation() == null ?
                                                              ListUtils.EMPTY_LIST : quoteCopyRequest.getDataModificationInformation().getPreviousDataValueDescription() == null ?
                                                              ListUtils.EMPTY_LIST : quoteCopyRequest.getDataModificationInformation().getPreviousDataValueDescription().getMedicalMalpracticePolicy();
        String errorMsgKey = null;
        String errorMsg = "";
        if (changedPolicies.isEmpty()) {
            errorMsgKey = "ws.quote.copy.changedQuote.policy.required";
        }
        else if (changedPolicies.size() > 1) {
            errorMsgKey = "ws.quote.copy.one.quote.input";
            errorMsg = "changed data";
        }
        else if (originalPolicies.size() > 1) {
            errorMsgKey = "ws.quote.copy.one.quote.input";
            errorMsg = "original data";
        }
        else if (StringUtils.isBlank(changedPolicies.get(0).getPolicyId())) {
            errorMsgKey = "ws.quote.copy.changedQuote.policyId.required";
        }
        else if (originalPolicies.size() == 1 &&
                 !StringUtils.isSame(changedPolicies.get(0).getPolicyId(), originalPolicies.get(0).getPolicyId(), true)) {
            errorMsgKey = "ws.quote.copy.originalQuote.policyId.noMatch.input";
        }

        if (!StringUtils.isBlank(errorMsgKey)) {
            AppException ae = new AppException(errorMsgKey, "Validate the request parameters for quote copy in failure", new String[]{errorMsg});
            l.throwing(getClass().getName(), "preValidateRequestParameters", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "preValidateRequestParameters", quoteCopyRequest);
        }
        return true;
    }

    /**
     * Add OWS access trail log.
     * @param quoteCopyRequest
     */
    private OwsLogRequest addOwsAccessTrailLog(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addOwsAccessTrailLog", quoteCopyRequest);
        }

        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(quoteCopyRequest, _QuoteCopyRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                quoteCopyRequest.getMessageId(), quoteCopyRequest.getCorrelationId(), quoteCopyRequest.getUserId(),_QuoteCopyRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "addOwsAccessTrailLog", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(quoteCopyRequest, _QuoteCopyRequest_QNAME,
                quoteCopyRequest.getMessageId(), quoteCopyRequest.getCorrelationId(), quoteCopyRequest.getUserId());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addOwsAccessTrailLog", quoteCopyRequest);
        }
        return owsLogRequest;
    }

    /**
     * Update OWS access trail log.
     * @param owsLogRequest
     * @param quoteCopyResult
     */
    private void updateOwsAccessTrailLog(OwsLogRequest owsLogRequest, MedicalMalpracticeQuoteCopyResultType quoteCopyResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateOwsAccessTrailLog", new Object[]{owsLogRequest, quoteCopyResult});
        }

        owsLogRequest.setMessageStatusCode(quoteCopyResult.getMessageStatus().getMessageStatusCode());

        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(quoteCopyResult, _QuoteCopyResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "updateOwsAccessTrailLog", xmlResult);
        } else {
            owsLogRequest.setServiceResult(quoteCopyResult);
            owsLogRequest.setServiceResultQName(_QuoteCopyResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateOwsAccessTrailLog", owsLogRequest);
        }
    }

    /**
     * This method unlocks the policy using an autonomous transaction. This is necessary in case of failure in order
     * to not commit partial changes, and to be able to rollback all the policy changes done by the Web Service call.
     * To accomplish this, the method is configured in applicationConfig.xml for bean Pm.TxAttributes with
     * PROPAGATION_REQUIRES_NEW.
     * @param newQuotePolicyHeader
     */
    public void owsUnlockPolicy(PolicyHeader newQuotePolicyHeader) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "owsUnlockPolicy", newQuotePolicyHeader);
        }

        getLockManager().unLockPolicy(newQuotePolicyHeader, YesNoFlag.Y, "Unlock from Quote Copy Web Service");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "owsUnlockPolicy");
        }
    }

    /**
     * This method sets the ActionCode flags.
     * @param quoteCopyRequest
     */
    public void setActionCodeFlags(MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setActionCodeFlags", new Object[]{quoteCopyRequest});
        }

        setRateActionB(false);
        setIssueActionB(false);
        setIgnoreSoftValidationActionB(false);

        if (quoteCopyRequest.getDataModificationInformation()!=null) {
            for (String actionCode : quoteCopyRequest.getDataModificationInformation().getActionCode()) {
                if (PolicyInquiryFields.RATE_ACTION_CODE.equalsIgnoreCase(actionCode)) {
                    setRateActionB(true);
                }
                if (PolicyInquiryFields.ISSUE_ACTION_CODE.equalsIgnoreCase(actionCode)) {
                    setRateActionB(true);
                    setIssueActionB(true);
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

    public PolicyChangeServiceSaveProcessor getPolicyChangeServiceSaveProcessor() {
        return m_policyChangeServiceSaveProcessor;
    }

    public void setPolicyChangeServiceSaveProcessor(PolicyChangeServiceSaveProcessor policyChangeServiceSaveProcessor) {
        m_policyChangeServiceSaveProcessor = policyChangeServiceSaveProcessor;
    }

    public PolicyInquiryServiceManager getPolicyInquiryServiceManager() {
        return m_policyInquiryServiceManager;
    }

    public void setPolicyInquiryServiceManager(PolicyInquiryServiceManager policyInquiryServiceManager) {
        m_policyInquiryServiceManager = policyInquiryServiceManager;
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

    private boolean isRateActionB() {
        return rateActionB;
    }

    private void setRateActionB(boolean rateActionB) {
        this.rateActionB = rateActionB;
    }

    private boolean isIssueActionB() {
        return issueActionB;
    }

    private void setIssueActionB(boolean issueActionB) {
        this.issueActionB = issueActionB;
    }

    private boolean isIgnoreSoftValidationActionB() { return ignoreSoftValidationActionB; }

    private void setIgnoreSoftValidationActionB(Boolean ignoreSoftValidationActionB) {
        this.ignoreSoftValidationActionB = ignoreSoftValidationActionB;
    }

    private PolicyChangeServiceSaveProcessor m_policyChangeServiceSaveProcessor;
    private PolicyManager m_policyManager;
    private TransactionManager m_transactionManager;
    private PolicyInquiryServiceManager m_policyInquiryServiceManager;
    private LockManager m_lockManager;
    private PolicyChangeServiceHelper m_policyChangeServiceHelper;

    private boolean rateActionB;
    private boolean issueActionB;
    private boolean ignoreSoftValidationActionB;

    public static final String SERVICE_MANAGER_BEAN_NAME = "PolicyChangeServiceManager";
    private final String requestId = "dti.pm.policymgr.struts.MaintainPolicyAction&process=loadPolicyDetail";
}