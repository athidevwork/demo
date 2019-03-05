package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.initiateelectronicapplicationservice.InitiateElectronicApplicationRequestType;
import com.delphi_tech.ows.initiateelectronicapplicationservice.InitiateElectronicApplicationResultType;
import com.delphi_tech.ows.initiateelectronicapplicationservice.RequestDetails;
import com.delphi_tech.ows.initiateelectronicapplicationservice.ResultDetails;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusHelper;
import dti.pm.policymgr.applicationmgr.ApplicationManager;
import dti.pm.policymgr.service.EApplicationInquiryFields;
import dti.pm.policymgr.service.InitiateElectronicApplicationServiceManager;
import dti.pm.policymgr.service.PolicyInquiryFields;
import dti.pm.policymgr.service.PolicyInquiryServiceHelper;
import org.apache.commons.collections.ListUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   1/4/2017
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */
public class InitiateElectronicApplicationServiceManagerImpl implements InitiateElectronicApplicationServiceManager {
    private final Logger l = LogUtils.getLogger(getClass());

    private final static QName _InitiateEAppRequest_QNAME = new QName("http://www.delphi-tech.com/ows/InitiateElectronicApplicationService", "InitiateElectronicApplicationRequest");

    private final static QName _InitiateEAppResult_QNAME = new QName("http://www.delphi-tech.com/ows/InitiateElectronicApplicationService", "InitiateElectronicApplicationResult");


    @Override
    public void initiateEApp(InitiateElectronicApplicationRequestType initiateEAppRequest, InitiateElectronicApplicationResultType initiateEAppResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initiateEApp", new Object[]{initiateEAppRequest, initiateEAppResult});
        }

        OwsLogRequest owsLogRequest = addOwsAccessTrailLog(initiateEAppRequest);

        try {
            preValidateRequestParameters(initiateEAppRequest);

            doInitiateEApp(initiateEAppRequest, initiateEAppResult);

            initiateEAppResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure invoking the initiateEApp", e, false);
            initiateEAppResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
            l.logp(Level.SEVERE, getClass().getName(), "initiateEApp", ae.getMessage(), ae);
        }

        updateOwsAccessTrailLog(owsLogRequest, initiateEAppResult);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initiateEApp", initiateEAppResult);
        }
    }

    /**
     * Initiate eApplication
     * @param initiateEAppRequest
     * @param initiateEAppResult
     */
    private void doInitiateEApp(InitiateElectronicApplicationRequestType initiateEAppRequest, InitiateElectronicApplicationResultType initiateEAppResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doInitiateEApp", new Object[]{initiateEAppRequest, initiateEAppResult});
        }

        List<RequestDetails> requestDetailsList = initiateEAppRequest.getRequestDetails();
        List<ResultDetails> resultDetailsList = new ArrayList<>();
        List<String[]> termPolicyList = new ArrayList<>();

        Iterator it = requestDetailsList.iterator();
        while (it.hasNext()) {
            RequestDetails requestDetails = (RequestDetails)it.next();
            Record inputRecord = setInputRecord(requestDetails);
            if (!EApplicationInquiryFields.hasPolicyTermNumberId(inputRecord)) {
                String[] termInfo = getPolicyInquiryServiceHelper().getLatestTerm(inputRecord);
                if (null != termInfo) {
                    termPolicyList.add(termInfo);
                }
            }
            else {
                List<String[]> tempTermPolicyList = getPolicyInquiryServiceHelper().getTermPolicyList(inputRecord);
                termPolicyList.addAll(tempTermPolicyList);
            }

            if (termPolicyList.isEmpty() || termPolicyList.size() > 1) {
                String invalidParameters = "Policy Id " + EApplicationInquiryFields.getPolicyId(inputRecord);
                if (EApplicationInquiryFields.hasPolicyNumberId(inputRecord)) {
                    invalidParameters = invalidParameters + ", Policy Number Id " + EApplicationInquiryFields.getPolicyNumberId(inputRecord);
                }
                if (EApplicationInquiryFields.hasPolicyTermNumberId(inputRecord)) {
                    invalidParameters = invalidParameters + ", Policy Term Number Id " + EApplicationInquiryFields.getPolicyTermNumberId(inputRecord);
                }
                AppException ae = new AppException("ws.eApp.initiate.request.noValidPolicyTerm.invalid", "", new String[]{invalidParameters});
                l.throwing(getClass().getName(), "doInitiateEApp", ae);
                throw ae;
            }

            String[] termInfo = termPolicyList.get(0);
            EApplicationInquiryFields.setPolicyTermNumberId(inputRecord, termInfo[0]);
            EApplicationInquiryFields.setPolicyId(inputRecord, termInfo[1]);
            EApplicationInquiryFields.setPolicyNumberId(inputRecord, termInfo[2]);

            setEApplicationId(inputRecord);

            addRecordToResultDetailsList(inputRecord, resultDetailsList);
        }
        addToResult(resultDetailsList, initiateEAppRequest, initiateEAppResult);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "doInitiateEApp");
        }
    }

    /**
     * Add record to ResultDetails
     * @param inputRecord
     * @param resultDetailsList
     */
    private void addRecordToResultDetailsList(Record inputRecord, List<ResultDetails> resultDetailsList) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addRecordToResultDetailsList", new Object[]{inputRecord, resultDetailsList});
        }

        ResultDetails resultDetail = new ResultDetails();
        resultDetail.setPolicyId(EApplicationInquiryFields.getPolicyId(inputRecord));
        resultDetail.setPolicyTermNumberId(EApplicationInquiryFields.getPolicyTermNumberId(inputRecord));
        resultDetail.setPolicyNumberId(EApplicationInquiryFields.getPolicyNumberId(inputRecord));
        resultDetail.setEApplicationId(EApplicationInquiryFields.getEApplicationId(inputRecord));
        resultDetailsList.add(resultDetail);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addRecordToResultDetailsList");
        }
    }

    /**
     * Add to result
     * @param resultDetailsList
     * @param initiateEAppRequest
     * @param initiateEAppResult
     */
    private void addToResult(List<ResultDetails> resultDetailsList, InitiateElectronicApplicationRequestType initiateEAppRequest, InitiateElectronicApplicationResultType initiateEAppResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addToResult", new Object[]{resultDetailsList, initiateEAppRequest, initiateEAppResult});
        }

        initiateEAppResult.setMessageId(initiateEAppRequest.getMessageId());
        initiateEAppResult.setCorrelationId(initiateEAppRequest.getCorrelationId());
        initiateEAppResult.setUserId(initiateEAppRequest.getUserId());
        initiateEAppResult.getResultDetails().addAll(resultDetailsList);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addToResult");
        }
    }

    /**
     * Call eApp to create eApplication ID.
     * @param inputRecord
     * @return
     */
    private void setEApplicationId(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setEApplicationId", inputRecord);
        }

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
        String userName = ActionHelper.getCurrentUserId(request);

        Record outputRecord = getApplicationManager().processInitiateApp(inputRecord, userName);
        EApplicationInquiryFields.setEApplicationId(inputRecord, EApplicationInquiryFields.getEApplicationId(outputRecord));

        String initResult = EApplicationInquiryFields.getInitResult(outputRecord);
        if (!initResult.equals(EApplicationInquiryFields.APP_INIT_SUCCESS)) {
            AppException appException = EApplicationInquiryFields.getInitException(outputRecord);
            String initMessage = null;
            if (initResult.equals(EApplicationInquiryFields.APP_INIT_EXISTED)) {
                initMessage = MessageManager.getInstance().formatMessage("pm.eApp.initiate.applicationExists.info");
            }
            else {
                initMessage = MessageManager.getInstance().formatMessage(AppException.UNEXPECTED_ERROR);
            }

            if (null == appException) {
                appException = new AppException(initMessage);
            }
            else {
                appException.setMessageKey("pm.eApp.initiate.process.error");
            }
            throw appException;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setEApplicationId");
        }
    }

    /**
     * Set input record.
     * @param requestDetails
     * @return
     */
    private Record setInputRecord(RequestDetails requestDetails) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputRecord", requestDetails);
        }

        Record inputRecord = new Record();
        EApplicationInquiryFields.setPolicyId(inputRecord, requestDetails.getPolicyId());
        inputRecord.setFieldValue(PolicyInquiryFields.POLICY_NO, requestDetails.getPolicyId());
        if (!StringUtils.isBlank(requestDetails.getPolicyNumberId())) {
            EApplicationInquiryFields.setPolicyNumberId(inputRecord, requestDetails.getPolicyNumberId());
        }

        if (!StringUtils.isBlank(requestDetails.getPolicyTermNumberId())) {
            EApplicationInquiryFields.setPolicyTermNumberId(inputRecord, requestDetails.getPolicyTermNumberId());
        }

        if (!StringUtils.isBlank(requestDetails.getTypeCode())) {
            EApplicationInquiryFields.setTypeCode(inputRecord, requestDetails.getTypeCode());
        }

        if (StringUtils.isBlank(requestDetails.getStatusCode())) {
            EApplicationInquiryFields.setStatusCode(inputRecord, EApplicationInquiryFields.STATUS_CODE_REQUESTED);
        }
        else {
            EApplicationInquiryFields.setStatusCode(inputRecord, requestDetails.getStatusCode());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputRecord", inputRecord);
        }

        return inputRecord;
    }

    /**
     * Validate request parameters
     * @param initiateEAppRequest
     */
    private void preValidateRequestParameters(InitiateElectronicApplicationRequestType initiateEAppRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "preValidateRequestParameters", initiateEAppRequest);
        }

        List<RequestDetails> requestDetailsList = initiateEAppRequest.getRequestDetails() == null ?
                                                  ListUtils.EMPTY_LIST : initiateEAppRequest.getRequestDetails();
        String errorMsgKey = null;
        if (requestDetailsList.isEmpty()) {
            errorMsgKey = "ws.eApp.initiate.requestDetails.required";
        }
        else if (requestDetailsList.size() > 1) {
            errorMsgKey = "ws.eApp.initiate.multiple.requestDetails.input";
        }
        else if (StringUtils.isBlank(requestDetailsList.get(0).getPolicyId())) {
            errorMsgKey = "ws.eApp.initiate.policyId.required";
        }

        if (!StringUtils.isBlank(errorMsgKey)) {
            AppException ae = new AppException(errorMsgKey, "Fail in validating the request parameters.");
            l.throwing(getClass().getName(), "preValidateRequestParameters", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "preValidateRequestParameters", initiateEAppRequest);
        }
    }

    /**
     * Add OWS access trail log.
     * @param initiateEAppRequest
     */
    private OwsLogRequest addOwsAccessTrailLog(InitiateElectronicApplicationRequestType initiateEAppRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addOwsAccessTrailLog", initiateEAppRequest);
        }

        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(initiateEAppRequest, _InitiateEAppRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                initiateEAppRequest.getMessageId(), initiateEAppRequest.getCorrelationId(), initiateEAppRequest.getUserId(),_InitiateEAppRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "initiateEApp", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(initiateEAppRequest, _InitiateEAppRequest_QNAME,
                initiateEAppRequest.getMessageId(), initiateEAppRequest.getCorrelationId(), initiateEAppRequest.getUserId());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addOwsAccessTrailLog", initiateEAppRequest);
        }
        return owsLogRequest;
    }

    /**
     * Update OWS access trail log.
     * @param owsLogRequest
     * @param initiateEAppResult
     */
    private void updateOwsAccessTrailLog(OwsLogRequest owsLogRequest, InitiateElectronicApplicationResultType initiateEAppResult) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateOwsAccessTrailLog", new Object[]{owsLogRequest, initiateEAppResult});
        }

        owsLogRequest.setMessageStatusCode(initiateEAppResult.getMessageStatus().getMessageStatusCode());
        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(initiateEAppResult, _InitiateEAppResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "initiateEApp", xmlResult);
        } else {
            owsLogRequest.setServiceResult(initiateEAppResult);
            owsLogRequest.setServiceResultQName(_InitiateEAppResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateOwsAccessTrailLog", owsLogRequest);
        }
    }

    public PolicyInquiryServiceHelper getPolicyInquiryServiceHelper() {
        return m_policyInquiryServiceHelper;
    }

    public void setPolicyInquiryServiceHelper(PolicyInquiryServiceHelper policyInquiryServiceHelper) {
        m_policyInquiryServiceHelper = policyInquiryServiceHelper;
    }

    public ApplicationManager getApplicationManager() {
        return m_applicationManager;
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        m_applicationManager = applicationManager;
    }

    private PolicyInquiryServiceHelper m_policyInquiryServiceHelper;
    private ApplicationManager m_applicationManager;
}
