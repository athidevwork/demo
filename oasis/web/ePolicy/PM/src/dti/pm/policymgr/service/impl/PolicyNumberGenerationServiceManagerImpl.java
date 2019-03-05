package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationRequestType;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationResultType;
import com.delphi_tech.ows.policynumbergenerationservice.PolicyNumberGenerationRequestParametersType;
import com.delphi_tech.ows.policynumbergenerationservice.IssueCompanyType;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.ows.common.MessageStatusHelper;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.service.PolicyNumberGenerationFields;
import dti.pm.policymgr.service.PolicyNumberGenerationServiceManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.util.XMLUtils;

import javax.xml.namespace.QName;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   06/15/16
 *
 * @author eyin
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/22/2016       eyin        177211 - Created for policy number generation service.
 * 10/07/2016       fcb         179813 - New fields added.
 * 11/02/2018      wrong        196790 - 1) Added validation for clinet Id in issue company.
 *                                       2) Modified setInputPolicyToRecord to get/set entity id by client id.
 * ---------------------------------------------------
 */
public class PolicyNumberGenerationServiceManagerImpl implements PolicyNumberGenerationServiceManager {

    public final static QName _PolicyNumberGenerationRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyNumberGenerationService", "PolicyNumberGenerationRequest");
    public final static QName _PolicyNumberGenerationResult_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyNumberGenerationService", "PolicyNumberGenerationResult");

    /**
     * Constructor.
     */
    public PolicyNumberGenerationServiceManagerImpl() {
    }

    public PolicyNumberGenerationResultType generatePolicyNumber (PolicyNumberGenerationRequestType policyNumberGenerationRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePolicyNumber", new Object[]{policyNumberGenerationRequest});
        }
        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(policyNumberGenerationRequest, _PolicyNumberGenerationRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                policyNumberGenerationRequest.getMessageId(), policyNumberGenerationRequest.getCorrelationId(), policyNumberGenerationRequest.getUserId(), _PolicyNumberGenerationRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "generatePolicyNumber", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(policyNumberGenerationRequest, _PolicyNumberGenerationRequest_QNAME,
                policyNumberGenerationRequest.getMessageId(), policyNumberGenerationRequest.getCorrelationId(), policyNumberGenerationRequest.getUserId());
        }

        PolicyNumberGenerationResultType policyNumberGenerationResult = new PolicyNumberGenerationResultType();

        try{
            validateForGenPolNo(policyNumberGenerationRequest);

            Record inputRecord = setInputPolicyToRecord(policyNumberGenerationRequest);
            String policyNo = getPolicyManager().generatePolicyNumberForWS(inputRecord);

            policyNumberGenerationResult.setPolicyNumber(policyNo);

            policyNumberGenerationResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR,
                "Failure invoking the PolicyNumberGenerationServiceManagerImpl", e , false);
            l.logp(Level.SEVERE, getClass().getName(), "generatePolicyNumber", ae.getMessage(), ae);
            policyNumberGenerationResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
        }

        owsLogRequest.setMessageStatusCode(policyNumberGenerationResult.getMessageStatus().getMessageStatusCode());

        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(policyNumberGenerationResult, _PolicyNumberGenerationResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "generatePolicyNumber", xmlResult);
        } else {
            owsLogRequest.setServiceResult(policyNumberGenerationResult);
            owsLogRequest.setServiceResultQName(_PolicyNumberGenerationResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generatePolicyNumber", policyNumberGenerationResult);
        }
        return policyNumberGenerationResult;
    }

    private void validateForGenPolNo(PolicyNumberGenerationRequestType policyNumberGenerationRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForGenPolNo", new Object[]{policyNumberGenerationRequest});
        }

        String errorMsg = "";

        if (policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters() == null)
            errorMsg = errorMsg + "PolicyNumberGeneration Request Parameters, ";
        else {
            PolicyNumberGenerationRequestParametersType policyNumberGenerationRequestParameters = policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters();
            if (policyNumberGenerationRequestParameters.getPolicyTypeCode() == null ||
                StringUtils.isBlank(policyNumberGenerationRequestParameters.getPolicyTypeCode()))
                errorMsg = errorMsg + "Policy Type Code, ";
            if (policyNumberGenerationRequestParameters.getPolicyCycleCode() == null ||
                StringUtils.isBlank(policyNumberGenerationRequestParameters.getPolicyCycleCode()))
                errorMsg = errorMsg + "Policy Cycle Code, ";

            if(policyNumberGenerationRequestParameters.getIssueCompany() == null){
                errorMsg = errorMsg + "IssueCompany, ";
            }
            else{
                IssueCompanyType issueCompany = policyNumberGenerationRequestParameters.getIssueCompany();
                if (issueCompany.getReferredParty() == null || issueCompany.getReferredParty().getPartyNumberId() == null ||
                    (StringUtils.isBlank(issueCompany.getReferredParty().getPartyNumberId())
                     && StringUtils.isBlank(issueCompany.getReferredParty().getClientId())))
                    errorMsg = errorMsg + "Issue Company id or client id, ";
                if (issueCompany.getControllingStateOrProvinceCode() == null ||
                    StringUtils.isBlank(issueCompany.getControllingStateOrProvinceCode()))
                    errorMsg = errorMsg + "Issue State, ";
            }
        }

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-2);
            AppException ae = new AppException("ws.policyNumberGeneration.required", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForPolicyNumberGeneration", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForGenPolNo");
        }
    }

    private Record setInputPolicyToRecord(PolicyNumberGenerationRequestType policyNumberGenerationRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputPolicyToRecord", new Object[]{policyNumberGenerationRequest});
        }
        Record inputRecord = new Record();
        String issueCompanyEntityId  =
            getPolicyManager().getEntityIdByClientId(policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getIssueCompany().getReferredParty().getPartyNumberId(),
            policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getIssueCompany().getReferredParty().getClientId());

        inputRecord.setFieldValue(PolicyNumberGenerationFields.POLICY_TYPE_CODE,
            policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getPolicyTypeCode());

        inputRecord.setFieldValue(PolicyNumberGenerationFields.POLICY_CYCLE_CODE,
            policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getPolicyCycleCode());

        inputRecord.setFieldValue(PolicyNumberGenerationFields.ISSUE_COMPANY_IDENTIFIER, issueCompanyEntityId);

        inputRecord.setFieldValue(PolicyNumberGenerationFields.SOURCE_POLICY_CYCLE,
            policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getSourcePolicyCycle());

        inputRecord.setFieldValue(PolicyNumberGenerationFields.SOURCE_POLICY_NO,
            policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getSourcePolicyNo());

        inputRecord.setFieldValue(PolicyNumberGenerationFields.QUOTE_CYCLE_CODE,
            policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getQuoteCycleCode());

        inputRecord.setFieldValue(PolicyNumberGenerationFields.START_DATE,
            DateUtils.parseXMLDateToOasisDate(policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getStartDate()));

        inputRecord.setFieldValue(PolicyNumberGenerationFields.ISSUE_STATE,
            policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getIssueCompany().getControllingStateOrProvinceCode());

        if(policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getIssueCompany().getProcessLocationCode() == null ||
            StringUtils.isBlank(policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getIssueCompany().getProcessLocationCode())){
            inputRecord.setFieldValue(PolicyNumberGenerationFields.PROCESS_LOCATION_CODE, "NONE");
        }
        else{
            inputRecord.setFieldValue(PolicyNumberGenerationFields.PROCESS_LOCATION_CODE,
                policyNumberGenerationRequest.getPolicyNumberGenerationRequestParameters().getIssueCompany().getProcessLocationCode());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputPolicyToRecord", inputRecord);
        }
        return inputRecord;
    }

    public void verifyConfig() {
        if (getPolicyManager() == null)
            throw new ConfigurationException("The required property 'policyManager' is missing.");
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    private PolicyManager m_policyManager;
}
